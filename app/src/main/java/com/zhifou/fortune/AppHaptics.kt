package com.zhifou.fortune

import android.content.Context
import android.database.ContentObserver
import android.media.AudioAttributes
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

internal val LocalAppHaptics = staticCompositionLocalOf<AppHaptics> {
    error("AppHaptics has not been provided")
}

@Suppress("DEPRECATION")
@Composable
internal fun rememberAppHaptics(enabled: Boolean): AppHaptics {
    val view = LocalView.current
    val owner = LocalLifecycleOwner.current
    val haptics = remember(view) { AppHaptics(view) }
    SideEffect { haptics.setEnabled(enabled) }
    LaunchedEffect(haptics) {
        withContext(Dispatchers.Default) { haptics.prepareHardware() }
    }
    DisposableEffect(haptics, owner) {
        val resolver = view.context.contentResolver
        val preferenceObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) = haptics.refreshSystemPreference()
        }
        val registered = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && runCatching {
            resolver.registerContentObserver(
                Settings.System.getUriFor(Settings.System.HAPTIC_FEEDBACK_ENABLED),
                false,
                preferenceObserver,
            )
        }.isSuccess
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    haptics.refreshSystemPreference()
                    haptics.setResumed(true)
                }
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY ->
                    haptics.setResumed(false)
                else -> Unit
            }
        }
        haptics.refreshSystemPreference()
        haptics.setResumed(owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
        owner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            owner.lifecycle.removeObserver(lifecycleObserver)
            if (registered) runCatching { resolver.unregisterContentObserver(preferenceObserver) }
            haptics.setResumed(false)
        }
    }
    return haptics
}

@Composable
internal fun rememberToolHaptics(): AppHaptics {
    val haptics = LocalAppHaptics.current
    DisposableEffect(haptics) { onDispose { haptics.cancel() } }
    return haptics
}

@Composable
internal fun HapticPageFeedback(route: Any, depth: Int, active: Boolean = true) {
    val haptics = LocalAppHaptics.current
    val state = remember { arrayOf(Triple(route, depth, active)) }
    LaunchedEffect(route, depth, active) {
        val previous = state[0]
        if (active && previous.third && route != previous.first) {
            haptics.scene(if (depth < previous.second) HapticCue.NavigateOut else HapticCue.NavigateIn)
        }
        state[0] = Triple(route, depth, active)
    }
}

/** UI uses system semantics; animated tools use supported, short motor effects. */
internal class AppHaptics(private val view: View) {
    private val context = view.context.applicationContext
    private data class Hardware(
        val motor: Vibrator?,
        val available: Boolean,
        val amplitudeControl: Boolean,
        val primitives: Set<HapticPrimitive>,
        val durations: Map<HapticPrimitive, Int>,
        val envelopes: Boolean,
        val candidates: Int = 1,
    ) {
        val quality get() = if (!available) -1 else
            (if (envelopes) 1_000 else 0) + primitives.size * 20 + (if (amplitudeControl) 10 else 0)
    }

    private data class PreparedEffect(val effect: VibrationEffect, val durationMs: Long, val backend: String)

    @Volatile
    private var hardware: Hardware? = null
    private val pulseGate = HapticPulseGate()
    private val arbiter = HapticArbiter()
    private val effects = ConcurrentHashMap<Int, PreparedEffect>()
    private val touchAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    private var enabled = true
    private var systemEnabled = true
    private var resumed = false
    private var submitted = 0
    private var suppressed = 0
    private var lastBackend = "none"

    fun prepareHardware() {
        val motors = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(VibratorManager::class.java)
                listOfNotNull(manager?.defaultVibrator) +
                    (manager?.vibratorIds?.map { manager.getVibrator(it) } ?: emptyList())
            } else {
                @Suppress("DEPRECATION")
                listOfNotNull(context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            }
        }.getOrDefault(emptyList())
        // The manager's aggregate vibrator may advertise fewer capabilities than an individual actuator.
        hardware = motors.map { motor ->
            val supported = HapticPrimitive.entries.filter { supportsPrimitive(motor, primitiveId(it)) }.toSet()
            val durations = if (Build.VERSION.SDK_INT >= 31) supported.associateWith {
                runCatching { motor.getPrimitiveDurations(primitiveId(it)).first().coerceAtLeast(1) }.getOrDefault(20)
            } else emptyMap()
            Hardware(motor, runCatching { motor.hasVibrator() }.getOrDefault(false),
                runCatching { motor.hasAmplitudeControl() }.getOrDefault(false), supported, durations,
                Build.VERSION.SDK_INT >= 36 && runCatching { motor.areEnvelopeEffectsSupported() }.getOrDefault(false), motors.size)
        }.maxByOrNull { it.quality } ?: Hardware(null, false, false, emptySet(), emptyMap(), false, 0)
        effects.clear()
    }

    fun setEnabled(value: Boolean) {
        enabled = value
        if (!value) cancel()
    }

    internal fun capabilityReport(): String {
        val device = hardware ?: return "ready=false"
        return "ready=true available=${device.available} envelope=${device.envelopes} amplitude=${device.amplitudeControl} primitives=${device.primitives.size} candidates=${device.candidates}"
    }

    internal fun playbackReport() = "submitted=$submitted suppressed=$suppressed backend=$lastBackend"

    fun setResumed(value: Boolean) {
        resumed = value
        if (!value) { cancel(); arbiter.reset() }
    }

    @Suppress("DEPRECATION")
    fun refreshSystemPreference() {
        // Android 13+ applies its current per-usage intensity to USAGE_TOUCH.
        systemEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU || runCatching {
            Settings.System.getInt(context.contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 1) != 0
        }.getOrDefault(true)
        if (!systemEnabled) cancel()
    }

    fun cancel() {
        pulseGate.reset()
        runCatching { hardware?.motor?.cancel() }
    }

    fun selection() = scene(HapticCue.Selection)
    fun toggle(on: Boolean) = scene(if (on) HapticCue.ToggleOn else HapticCue.ToggleOff)
    fun menu(open: Boolean) = scene(if (open) HapticCue.MenuOpen else HapticCue.MenuClose)

    fun sendAccepted() = scene(HapticCue.Send)

    fun scene(cue: HapticCue, amount: Float = 1f): Boolean {
        if (!canPlay() || !amount.isFinite() || amount <= 0f) return false
        val device = hardware
        val level = (amount.coerceIn(0f, 1f) * 8).roundToInt().coerceIn(1, 8)
        val score = device?.let { HapticComposition.score(cue, it.primitives, it.durations) }
        val scoreDuration = score?.sumOf { (device.durations[it.primitive] ?: 20).toLong() + it.delayMs } ?: 0L
        val advanced = device != null && (device.envelopes || device.amplitudeControl || (score != null && scoreDuration <= 220))
        val prepared = if (device != null && advanced) runCatching {
            effects.getOrPut(cue.ordinal * 16 + level) {
                val points = HapticDesign.points(cue, level / 8f)
                when {
                    Build.VERSION.SDK_INT >= 36 && device.envelopes -> {
                        val builder = VibrationEffect.BasicEnvelopeBuilder().setInitialSharpness(points.first().sharpness)
                        points.forEach { builder.addControlPoint(it.intensity, it.sharpness, it.durationMs) }
                        PreparedEffect(builder.build(), points.sumOf { it.durationMs }, "envelope")
                    }
                    Build.VERSION.SDK_INT >= 31 && score != null && scoreDuration <= 220 -> {
                        val builder = VibrationEffect.startComposition()
                        score.forEach { builder.addPrimitive(primitiveId(it.primitive), it.scale * level / 8f, it.delayMs) }
                        PreparedEffect(builder.compose(), scoreDuration, "composition")
                    }
                    device.amplitudeControl -> {
                        val samples = HapticDesign.sample(points)
                        PreparedEffect(VibrationEffect.createWaveform(samples.timings, samples.amplitudes, -1),
                            samples.timings.sum(), "amplitude")
                    }
                    else -> error("No supported tactile score")
                }
            }
        }.getOrNull() else fallbackEffect(cue)
        val duration = prepared?.durationMs ?: 45L
        if (!arbiter.acquire(cue, SystemClock.uptimeMillis(), duration)) { suppressed++; return false }
        if (prepared != null && play(prepared.effect)) {
            submitted++
            lastBackend = prepared.backend
            return true
        }
        val accepted = systemFeedback(systemConstant(cue))
        if (accepted) { submitted++; lastBackend = "system:${systemConstant(cue)}" }
        return accepted
    }

    fun gestureProgress(track: HapticGestureTrack, progress: Float): Boolean {
        val sample = track.advance(progress, SystemClock.uptimeMillis()) ?: return false
        return scene(sample.cue, sample.energy)
    }

    fun gestureFinished(track: HapticGestureTrack, committed: Boolean) {
        track.reset()
        scene(if (committed) HapticCue.GestureRelease else HapticCue.GestureCancel)
    }

    fun diceStart() = scene(HapticCue.CoinLift, .9f)

    fun diceImpact(strength: Float) {
        if (!canPlay() || !pulseGate.tryAcquire(SystemClock.uptimeMillis(), 65L)) return
        scene(HapticCue.DiceImpact, .35f + (strength / 5f).coerceIn(0f, 1f) * .65f)
    }

    fun diceSettle() = scene(HapticCue.DiceFinish)
    fun wheelStart() = scene(HapticCue.ToggleOn)

    fun wheelTick() {
        if (!canPlay() || !pulseGate.tryAcquire(SystemClock.uptimeMillis(), 50L)) return
        scene(HapticCue.WheelTick)
    }

    fun wheelSettle() = scene(HapticCue.WheelFinish)
    fun wheelGlideStop() = scene(HapticCue.CardLand)
    fun coinStart() = scene(HapticCue.CoinLift)

    fun coinSpinPulse(index: Int) {
        if (!canPlay() || !pulseGate.tryAcquire(SystemClock.uptimeMillis(), 65L)) return
        scene(HapticCue.CoinSpin, if (index % 2 == 0) 1f else .8f)
    }

    fun coinSettle() = scene(HapticCue.CoinFinish)

    fun oracleTouch(cue: OracleTouchCue) {
        if (!canPlay()) return
        if (cue.touch == OracleTouch.Complete) {
            scene(HapticCue.Confirm)
            return
        }
        if (!pulseGate.tryAcquire(SystemClock.uptimeMillis(), 64L)) return
        when (cue.touch) {
            OracleTouch.CoinLift -> scene(HapticCue.CoinLift, .65f)
            OracleTouch.CoinRoll -> scene(HapticCue.CoinSpin, .55f)
            OracleTouch.CoinLand -> scene(HapticCue.CoinImpact, .85f - cue.index * .15f)
            OracleTouch.Shuffle -> scene(HapticCue.Shuffle)
            OracleTouch.CardDraw -> scene(HapticCue.CardDraw)
            OracleTouch.CardTurn -> scene(HapticCue.CardTurn)
            OracleTouch.CardSettle -> scene(HapticCue.CardLand)
            OracleTouch.Complete -> Unit
        }
    }

    private fun canPlay() = enabled && systemEnabled && resumed && hardware?.available != false &&
        view.isAttachedToWindow && view.isShown && view.isHapticFeedbackEnabled

    private fun systemFeedback(constant: Int): Boolean =
        canPlay() && runCatching { view.performHapticFeedback(constant) }.getOrDefault(false)

    private fun supportsPrimitive(motor: Vibrator?, primitive: Int): Boolean =
        Build.VERSION.SDK_INT >= 31 &&
            runCatching { motor?.areAllPrimitivesSupported(primitive) == true }.getOrDefault(false)

    private fun primitiveId(primitive: HapticPrimitive): Int = when (primitive) {
        HapticPrimitive.Click -> VibrationEffect.Composition.PRIMITIVE_CLICK
        HapticPrimitive.Tick -> VibrationEffect.Composition.PRIMITIVE_TICK
        HapticPrimitive.LowTick -> VibrationEffect.Composition.PRIMITIVE_LOW_TICK
        HapticPrimitive.QuickRise -> VibrationEffect.Composition.PRIMITIVE_QUICK_RISE
        HapticPrimitive.SlowRise -> VibrationEffect.Composition.PRIMITIVE_SLOW_RISE
        HapticPrimitive.QuickFall -> VibrationEffect.Composition.PRIMITIVE_QUICK_FALL
        HapticPrimitive.Spin -> VibrationEffect.Composition.PRIMITIVE_SPIN
        HapticPrimitive.Thud -> VibrationEffect.Composition.PRIMITIVE_THUD
    }

    private fun systemConstant(cue: HapticCue): Int {
        if (Build.VERSION.SDK_INT >= 34) return when (cue) {
            HapticCue.Selection, HapticCue.PageSnap -> HapticFeedbackConstants.SEGMENT_TICK
            HapticCue.FineSelection, HapticCue.GesturePull, HapticCue.Shuffle, HapticCue.CoinSpin,
            HapticCue.CardLand, HapticCue.WheelTick -> HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
            HapticCue.GestureArm -> HapticFeedbackConstants.GESTURE_THRESHOLD_ACTIVATE
            HapticCue.GestureDisarm -> HapticFeedbackConstants.GESTURE_THRESHOLD_DEACTIVATE
            HapticCue.ToggleOn -> HapticFeedbackConstants.TOGGLE_ON
            HapticCue.ToggleOff -> HapticFeedbackConstants.TOGGLE_OFF
            else -> legacyConstant(cue)
        }
        return legacyConstant(cue)
    }

    private fun legacyConstant(cue: HapticCue): Int = when (cue) {
        HapticCue.Confirm, HapticCue.Send, HapticCue.VoiceStop, HapticCue.GestureRelease,
        HapticCue.CoinFinish, HapticCue.DiceFinish, HapticCue.WheelFinish ->
            if (Build.VERSION.SDK_INT >= 30) HapticFeedbackConstants.CONFIRM else HapticFeedbackConstants.VIRTUAL_KEY
        HapticCue.Reject, HapticCue.VoiceCancel ->
            if (Build.VERSION.SDK_INT >= 30) HapticFeedbackConstants.REJECT else HapticFeedbackConstants.LONG_PRESS
        HapticCue.NavigateOut, HapticCue.MenuClose, HapticCue.GestureCancel, HapticCue.ToggleOff ->
            if (Build.VERSION.SDK_INT >= 30) HapticFeedbackConstants.GESTURE_END else HapticFeedbackConstants.CLOCK_TICK
        HapticCue.CardDraw, HapticCue.CoinLift, HapticCue.VoiceStart ->
            if (Build.VERSION.SDK_INT >= 30) HapticFeedbackConstants.GESTURE_START else HapticFeedbackConstants.LONG_PRESS
        HapticCue.CardTurn, HapticCue.GestureArm, HapticCue.ToggleOn -> HapticFeedbackConstants.CONTEXT_CLICK
        HapticCue.NavigateIn, HapticCue.MenuOpen -> HapticFeedbackConstants.VIRTUAL_KEY
        else -> HapticFeedbackConstants.CLOCK_TICK
    }

    private fun fallbackEffect(cue: HapticCue): PreparedEffect? {
        // UI semantics use OEM-tuned View effects, not maximum-amplitude on/off approximations.
        val preset = when (cue) {
            HapticCue.DiceImpact -> if (Build.VERSION.SDK_INT >= 29) VibrationEffect.EFFECT_HEAVY_CLICK else null
            HapticCue.CoinImpact -> if (Build.VERSION.SDK_INT >= 29) VibrationEffect.EFFECT_CLICK else null
            else -> return null
        }
        return if (Build.VERSION.SDK_INT >= 29 && preset != null)
            PreparedEffect(VibrationEffect.createPredefined(preset), 35, "preset:$preset")
        else PreparedEffect(VibrationEffect.createWaveform(HapticDesign.basicTimings(cue), -1), 35, "timing")
    }

    @Suppress("DEPRECATION")
    private fun play(effect: VibrationEffect): Boolean =
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                hardware?.motor?.vibrate(effect, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_TOUCH))
            } else {
                hardware?.motor?.vibrate(effect, touchAttributes)
            }
            hardware?.motor != null
        }.getOrDefault(false)
}
