package com.zhifou.fortune

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

internal val LocalReducedFortuneMotion = staticCompositionLocalOf { false }
internal val LocalFortuneMotionVisible = staticCompositionLocalOf { true }

/** One observer per theme; custom frame-driven artwork and Material components share this preference. */
@Composable
internal fun rememberReducedFortuneMotion(): Boolean {
    val resolver = LocalContext.current.applicationContext.contentResolver
    val owner = LocalLifecycleOwner.current
    fun read() = runCatching {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) <= 0f
    }.getOrDefault(false)
    var reduced by remember(resolver) { mutableStateOf(read()) }
    DisposableEffect(resolver, owner) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) { reduced = read() }
        }
        val lifecycle = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) reduced = read()
        }
        resolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer)
        owner.lifecycle.addObserver(lifecycle)
        onDispose {
            resolver.unregisterContentObserver(observer)
            owner.lifecycle.removeObserver(lifecycle)
        }
    }
    return reduced
}

// M3 Expressive spatial/effect tokens, implemented with stable Compose APIs.
// MotionScheme is internal in the project's Material 1.4; do not call hidden APIs or upgrade the stack for motion.
internal fun <T> fortuneSpatialMotionSpec(reduced: Boolean, fast: Boolean): FiniteAnimationSpec<T> =
    if (reduced) snap() else spring(dampingRatio = if (fast) 0.6f else 0.8f, stiffness = if (fast) 800f else 380f)

internal fun <T> fortuneEffectsMotionSpec(reduced: Boolean): FiniteAnimationSpec<T> =
    if (reduced) snap() else spring(dampingRatio = 1f, stiffness = 3800f)

@Composable
internal fun <T> fortuneSpatialSpec(fast: Boolean = false): FiniteAnimationSpec<T> =
    fortuneSpatialMotionSpec(LocalReducedFortuneMotion.current, fast)

@Composable
internal fun <T> fortuneEffectsSpec(): FiniteAnimationSpec<T> =
    fortuneEffectsMotionSpec(LocalReducedFortuneMotion.current)

internal fun fortunePressTarget(pressed: Boolean, enabled: Boolean, reduced: Boolean, depth: Float): Float =
    if (pressed && enabled && !reduced) depth.coerceIn(0.9f, 1f) else 1f

@Composable
internal fun Modifier.fortunePress(
    source: MutableInteractionSource,
    enabled: Boolean = true,
    depth: Float = 0.965f,
): Modifier {
    val pressed by source.collectIsPressedAsState()
    val scale = animateFloatAsState(
        fortunePressTarget(pressed, enabled, LocalReducedFortuneMotion.current, depth),
        fortuneSpatialSpec(fast = true), label = "touch-spring",
    )
    return graphicsLayer { scaleX = scale.value; scaleY = scale.value }
}

@Composable
internal fun FortuneAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val effects = fortuneEffectsSpec<Float>()
    AnimatedVisibility(
        visible = visible, modifier = modifier,
        enter = fadeIn(effects) + expandVertically(fortuneSpatialSpec(), expandFrom = Alignment.Top),
        exit = fadeOut(effects) + shrinkVertically(fortuneSpatialSpec(fast = true), shrinkTowards = Alignment.Top),
        content = content,
    )
}
