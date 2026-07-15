package com.zhifou.fortune

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationAttributes
import android.os.Vibrator
import android.os.VibratorManager
import android.hardware.SensorEventListener
import android.hardware.SensorEvent
import android.hardware.Sensor
import android.os.Bundle
import android.view.ViewConfiguration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.withContext
import com.nlf.calendar.Solar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.SecureRandom
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private val LinearDecelEasing = Easing { p -> 2f * p - p * p }

// 主题调色板：深/浅各一套，通过 LocalFortunePalette 注入。
@Immutable
data class FortunePalette(
    val ink: Color,
    val panel: Color,
    val panelAlt: Color,
    val line: Color,
    val gold: Color,
    val mint: Color,
    val rose: Color,
    val danger: Color,
    val textMain: Color,
    val textSub: Color,
    val chatBubbleAssistant: Color,
    val chatBubbleUser: Color,
    val chatText: Color,
    val isLight: Boolean,
)

val DarkPalette = FortunePalette(
    ink = Color(0xFF111318),
    panel = Color(0xFF1B1F27),
    panelAlt = Color(0xFF242832),
    line = Color(0xFF343A46),
    gold = Color(0xFFE2C16B),
    mint = Color(0xFF7ED7C1),
    rose = Color(0xFFE48A9A),
    danger = Color(0xFFE5484D),
    textMain = Color(0xFFF3F0E8),
    textSub = Color(0xFFB7B2A6),
    chatBubbleAssistant = Color(0xFF2A3340),
    chatBubbleUser = Color(0xFF28342C),
    chatText = Color(0xFFECE8DF),
    isLight = false,
)

val LightPalette = FortunePalette(
    ink = Color(0xFFF2EEE5),
    panel = Color(0xFFFAF7F0),
    panelAlt = Color(0xFFEFE9DC),
    line = Color(0xFFE0DBD0),
    gold = Color(0xFF8A6A1E),
    mint = Color(0xFF2E8F7A),
    rose = Color(0xFFB5546A),
    danger = Color(0xFFC7323A),
    textMain = Color(0xFF2A2620),
    textSub = Color(0xFF6B655B),
    chatBubbleAssistant = Color(0xFFDDEEFF),
    chatBubbleUser = Color(0xFFDDF4DF),
    chatText = Color(0xFF172128),
    isLight = true,
)

val LocalFortunePalette = staticCompositionLocalOf { DarkPalette }

enum class ThemeMode { Dark, Light, System }

@Suppress("DEPRECATION")
private fun applySystemBars(window: android.view.Window, dark: Boolean) {
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.isAppearanceLightStatusBars = !dark
    controller.isAppearanceLightNavigationBars = !dark
    window.statusBarColor = android.graphics.Color.TRANSPARENT
    window.navigationBarColor = android.graphics.Color.TRANSPARENT
}

/** Small, event-based haptic vocabulary shared by the three physical tools. */
private class ToolHaptics(context: Context) {
    private val appContext = context.applicationContext
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        appContext.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    private val gameAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    private var lastDiceImpactAt = 0L
    private var lastWheelTickAt = 0L

    private fun effectiveAmplitude(motor: Vibrator, amplitude: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && motor.hasAmplitudeControl()) {
            amplitude.coerceIn(1, 255)
        } else {
            VibrationEffect.DEFAULT_AMPLITUDE
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrate(motor: Vibrator, effect: VibrationEffect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            motor.vibrate(
                effect,
                VibrationAttributes.createForUsage(VibrationAttributes.USAGE_MEDIA),
            )
        } else {
            motor.vibrate(effect, gameAttributes)
        }
    }

    private fun oneShot(durationMs: Long, amplitude: Int) {
        val motor = vibrator ?: return
        if (!motor.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrate(
                    motor,
                    VibrationEffect.createOneShot(
                        durationMs,
                        effectiveAmplitude(motor, amplitude),
                    ),
                )
            } else {
                @Suppress("DEPRECATION")
                motor.vibrate(durationMs)
            }
        } catch (_: Throwable) {
            // Haptics are optional; a missing or restricted motor must not affect gameplay.
        }
    }

    private fun waveform(timings: LongArray, amplitudes: IntArray) {
        val motor = vibrator ?: return
        if (!motor.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effectiveAmplitudes = if (motor.hasAmplitudeControl()) {
                    amplitudes.map { amplitude -> amplitude.coerceIn(0, 255) }.toIntArray()
                } else {
                    amplitudes.map { amplitude ->
                        if (amplitude == 0) 0 else VibrationEffect.DEFAULT_AMPLITUDE
                    }.toIntArray()
                }
                vibrate(
                    motor,
                    VibrationEffect.createWaveform(timings, effectiveAmplitudes, -1),
                )
            } else {
                @Suppress("DEPRECATION")
                motor.vibrate(timings, -1)
            }
        } catch (_: Throwable) {
            // Haptics are optional; a missing or restricted motor must not affect gameplay.
        }
    }

    fun diceStart() = oneShot(42L, 220)

    fun diceImpact(strength: Float) {
        val now = System.currentTimeMillis()
        if (now - lastDiceImpactAt < 48L) return
        lastDiceImpactAt = now
        val intensity = (strength / 5f).coerceIn(0.15f, 1f)
        oneShot(
            durationMs = (13f + intensity * 17f).toLong(),
            amplitude = (155f + intensity * 100f).toInt(),
        )
    }

    fun diceSettle() = waveform(
        timings = longArrayOf(0L, 30L, 46L, 22L),
        amplitudes = intArrayOf(0, 235, 0, 175),
    )

    fun wheelStart() = waveform(
        timings = longArrayOf(0L, 20L, 30L, 14L),
        amplitudes = intArrayOf(0, 185, 0, 120),
    )

    fun wheelTick() {
        val now = System.currentTimeMillis()
        if (now - lastWheelTickAt < 30L) return
        lastWheelTickAt = now
        oneShot(13L, 135)
    }

    fun wheelSettle() = waveform(
        timings = longArrayOf(0L, 28L, 48L, 34L),
        amplitudes = intArrayOf(0, 210, 0, 255),
    )

    fun wheelGlideStop() = oneShot(22L, 150)

    fun coinStart() = oneShot(28L, 165)

    fun coinFlip(index: Int) = oneShot(13L + index % 3, 125 + (index % 3) * 20)

    fun coinSettle() = waveform(
        timings = longArrayOf(0L, 26L, 42L, 30L),
        amplitudes = intArrayOf(0, 195, 0, 245),
    )
}

// 按压反馈：按下时缩放 0.97，松开回弹（redesign: physical press feedback）。
// 用法：Button(interactionSource = src, modifier = Modifier.pressScale(src))
@Composable
private fun pressScaleModifier(interaction: MutableInteractionSource): Modifier {
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, animationSpec = tween(120), label = "pressScale")
    return Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

class MainActivity : ComponentActivity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        val repo = FortuneRepository(applicationContext)
        val initialDark = when (repo.themeMode) {
            ThemeMode.Dark -> true
            ThemeMode.Light -> false
            ThemeMode.System -> (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        }
        applySystemBars(window, initialDark)
        val window = this.window
        setContent {
            val vm: FortuneViewModel = viewModel()
            val resolvedDark = when (vm.themeMode) {
                ThemeMode.Dark -> true
                ThemeMode.Light -> false
                ThemeMode.System -> isSystemInDarkTheme()
            }
            val C = if (resolvedDark) DarkPalette else LightPalette
            SideEffect { applySystemBars(window, resolvedDark) }
            CompositionLocalProvider(LocalFortunePalette provides C) {
                MaterialTheme(
                    colorScheme = if (resolvedDark) darkColorScheme(
                        background = C.ink, surface = C.panel, primary = C.gold,
                        secondary = C.mint, tertiary = C.rose,
                        onBackground = C.textMain, onSurface = C.textMain, onPrimary = C.ink,
                    ) else lightColorScheme(
                        background = C.ink, surface = C.panel, primary = C.gold,
                        secondary = C.mint, tertiary = C.rose,
                        onBackground = C.textMain, onSurface = C.textMain, onPrimary = C.textMain,
                    ),
                ) {
                    Surface(Modifier.fillMaxSize(), color = C.ink) {
                        FortuneApp(vm)
                    }
                }
            }
        }
    }
}

private enum class Tab(val label: String, val icon: ImageVector) {
    Home("首页", Icons.Default.Home),
    Schedule("日历", Icons.AutoMirrored.Filled.EventNote),
    Oracle("占卜", Icons.Default.AutoAwesome),
    Tools("小工具", Icons.Default.Casino),
    Mine("我的", Icons.Default.AccountCircle),
}

private const val VOICE_CANCEL_DISTANCE_DP = 140

@Composable
private fun FortuneApp(vm: FortuneViewModel = viewModel()) {
    val C = LocalFortunePalette.current
    val context = LocalContext.current
    val offlineRecognizer = remember(context) { OfflineSpeechRecognizer(context.applicationContext) }
    var tab by rememberTabState()
    var showDiceTool by remember { mutableStateOf(false) }
    var showWheelTool by remember { mutableStateOf(false) }
    var showCoinTool by remember { mutableStateOf(false) }
    val oracleTimelineState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (vm.oracleTimeline.isEmpty()) 0 else vm.oracleTimeline.size,
    )

    BackHandler(enabled = showDiceTool) { showDiceTool = false }
    BackHandler(enabled = showWheelTool) { showWheelTool = false }
    BackHandler(enabled = showCoinTool) { showCoinTool = false }

    DisposableEffect(offlineRecognizer) {
        onDispose {
            Thread({ offlineRecognizer.close() }, "zhifou-asr-cleanup").start()
        }
    }

    Scaffold(
        containerColor = C.ink,
        topBar = { if (tab == Tab.Home) AppTopBar() },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().height(144.dp)) {
                NavigationBar(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                    containerColor = C.panel,
                    tonalElevation = 0.dp,
                ) {
                    Tab.entries.forEach { item ->
                        NavigationBarItem(
                            selected = tab == item,
                            onClick = {
                                tab = item
                                if (item != Tab.Tools) {
                                    showDiceTool = false
                                    showWheelTool = false
                                    showCoinTool = false
                                }
                            },
                            icon = {
                                if (item == Tab.Oracle) {
                                    Spacer(Modifier.size(32.dp))
                                } else {
                                    Icon(item.icon, contentDescription = item.label)
                                }
                            },
                            label = if (item == Tab.Oracle) null else ({ Text(item.label) }),
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = C.gold,
                                selectedTextColor = C.gold,
                                indicatorColor = if (item == Tab.Oracle) Color.Transparent else C.gold.copy(alpha = 0.22f),
                                unselectedIconColor = C.textSub,
                                unselectedTextColor = C.textSub,
                            ),
                        )
                    }
                }
                Column(
                    modifier = Modifier.align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Surface(
                        onClick = {
                            tab = Tab.Oracle
                            showDiceTool = false
                        },
                        color = C.gold,
                        shape = CircleShape,
                        border = if (tab == Tab.Oracle) BorderStroke(2.dp, C.mint) else null,
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .size(64.dp)
                            .graphicsLayer { translationY = -2.dp.toPx() },
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Tab.Oracle.icon,
                                contentDescription = Tab.Oracle.label,
                                tint = C.ink,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                    Text(
                        Tab.Oracle.label,
                        color = if (tab == Tab.Oracle) C.gold else C.textSub,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.graphicsLayer { translationY = -1.dp.toPx() },
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(C.ink)
                .padding(padding),
        ) {
            when (tab) {
                Tab.Home -> HomeScreen(vm, onOpenOracle = { tab = Tab.Oracle })
                Tab.Oracle -> OracleScreen(vm, offlineRecognizer, oracleTimelineState)
                Tab.Tools -> when {
                    showWheelTool -> WheelScreen(vm, onBack = { showWheelTool = false })
                    showDiceTool -> DiceScreen(onBack = { showDiceTool = false })
                    showCoinTool -> CoinScreen(onBack = { showCoinTool = false })
                    else -> ToolsScreen(
                        onOpenDice = { showDiceTool = true },
                        onOpenWheel = { showWheelTool = true },
                        onOpenCoin = { showCoinTool = true },
                    )
                }
                Tab.Schedule -> ScheduleScreen(vm)
                Tab.Mine -> MineScreen(vm)
            }
        }
    }
}

@Composable
private fun rememberTabState() = androidx.compose.runtime.remember {
    mutableStateOf(Tab.Home)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar() {
    val C = LocalFortunePalette.current
    CenterAlignedTopAppBar(
        title = {
            Text(
                "知否运势",
                color = C.textMain,
                style = MaterialTheme.typography.titleLarge.copy(letterSpacing = (-0.5).sp, fontWeight = FontWeight.SemiBold),
            )
        },
        colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = C.ink,
            titleContentColor = C.textMain,
        ),
    )
}

@Composable
private fun HomeScreen(vm: FortuneViewModel, onOpenOracle: () -> Unit) {
    val C = LocalFortunePalette.current
    val snapshot = vm.todayFortune
    val today = snapshot.reading
    LaunchedEffect(Unit) {
        var activeDate = snapshot.almanac.date
        while (true) {
            delay(60_000L)
            val currentDate = LocalDate.now()
            if (currentDate != activeDate) {
                vm.refreshToday()
                activeDate = currentDate
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FortuneDial(score = today.score)
        ReadingCard(reading = today)
        DailyAlmanacPanel(
            info = snapshot.almanac,
            personalizationBasis = snapshot.personalizationBasis,
            environmentSummary = snapshot.environmentSummary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            val pressSrc = remember { MutableInteractionSource() }
            Button(
                onClick = { vm.refreshToday() },
                interactionSource = pressSrc,
                modifier = Modifier.weight(1f).then(pressScaleModifier(pressSrc)),
                colors = ButtonDefaults.buttonColors(containerColor = C.gold, contentColor = if (C.isLight) Color(0xFFFAF7F0) else Color(0xFF111318)),
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("今日运势")
            }
            val oracleSrc = remember { MutableInteractionSource() }
            FilledTonalButton(
                onClick = onOpenOracle,
                interactionSource = oracleSrc,
                modifier = Modifier.weight(1f).then(pressScaleModifier(oracleSrc)),
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("去占卜")
            }
        }
        InsightStrip()
    }
}

@Composable
private fun DailyAlmanacPanel(
    info: DailyAlmanacInfo,
    personalizationBasis: List<String>,
    environmentSummary: String?,
) {
    val C = LocalFortunePalette.current
    Card(
        colors = CardDefaults.cardColors(containerColor = C.panel),
        border = BorderStroke(1.dp, C.line),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("今日黄历", color = C.textMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(info.dateLabel, color = C.textMain, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${info.lunarLabel} · ${info.dayGanZhi}日${info.solarTerm.takeIf(String::isNotBlank)?.let { " · $it" }.orEmpty()}",
                    color = C.textSub,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Text("今日方位", color = C.textSub, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            val directions = listOf(
                "喜神" to info.joyDirection,
                "福神" to info.fortuneDirection,
                "财神" to info.wealthDirection,
                "阳贵神" to info.yangNobleDirection,
                "阴贵神" to info.yinNobleDirection,
            )
            directions.chunked(3).forEach { rowItems ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowItems.forEach { (label, direction) ->
                        AlmanacDirection(label, direction, Modifier.weight(1f))
                    }
                    repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(C.line))
            AlmanacActivityRow("宜", info.suitable, C.mint)
            AlmanacActivityRow("忌", info.avoid, C.rose)
            if (info.clash.isNotBlank() || info.shaDirection.isNotBlank()) {
                Text(
                    "冲煞：${info.clash}${info.shaDirection.takeIf(String::isNotBlank)?.let { " · 煞$it" }.orEmpty()}",
                    color = C.textSub,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Text(
                "个性化依据：${personalizationBasis.joinToString(" · ")}",
                color = C.textSub,
                style = MaterialTheme.typography.bodySmall,
            )
            if (environmentSummary != null) {
                Text("环境参考：$environmentSummary", color = C.textSub, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "方位与宜忌由本机离线历法计算，仅作传统民俗文化参考。",
                color = C.textSub,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun AlmanacDirection(label: String, direction: String, modifier: Modifier = Modifier) {
    val C = LocalFortunePalette.current
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, color = C.textSub, style = MaterialTheme.typography.labelMedium)
        Text(direction, color = C.gold, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AlmanacActivityRow(label: String, activities: List<String>, accent: Color) {
    val C = LocalFortunePalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(label, color = accent, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
        Text(
            activities.ifEmpty { listOf("无") }.joinToString("、"),
            color = C.textMain,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun OracleScreen(
    vm: FortuneViewModel,
    offlineRecognizer: OfflineSpeechRecognizer,
    timelineState: LazyListState,
) {
    val C = LocalFortunePalette.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val voiceUiScope = rememberCoroutineScope()
    val voiceHoldThresholdMs = remember {
        (ViewConfiguration.getTapTimeout() + 100)
            .coerceAtMost(ViewConfiguration.getLongPressTimeout())
            .toLong()
    }
    val questionFocusRequester = remember { FocusRequester() }
    var modelReady by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var isCancelling by remember { mutableStateOf(false) }
    var textEditing by remember { mutableStateOf(false) }
    var keyboardEdited by remember { mutableStateOf(false) }
    var voiceLevel by remember { mutableStateOf(0f) }
    var questionBeforeRecording by remember { mutableStateOf("") }
    val oracleDate = remember { LocalDate.now() }
    val oracleDateKey = oracleDate.toString()
    var showDailyPrompt by rememberSaveable(oracleDateKey) {
        mutableStateOf(vm.consumeDailyOraclePrompt(oracleDate))
    }
    val suggestedQuestions = remember(vm.todayFortune) { dailyOracleSuggestions(vm.todayFortune) }
    val oraclePageSwipeThreshold = with(LocalDensity.current) { 72.dp.toPx() }
    val oracleTimeline = vm.oracleTimeline
    var observedFollowSnapshot by remember {
        mutableStateOf(
            OracleFollowSnapshot(
                timelineSize = oracleTimeline.size,
                chatSending = vm.chatSending,
                coinCasting = vm.coinCasting,
                coinLineCount = vm.coinCastingLines.size,
            )
        )
    }
    val selectedKeys = remember { androidx.compose.runtime.mutableStateMapOf<String, Boolean>() }
    val selectionMode = selectedKeys.isNotEmpty()
    var confirmDeleteSelection by remember { mutableStateOf(false) }

    LaunchedEffect(oracleDate) {
        if (vm.todayFortune.almanac.date != oracleDate) vm.refreshToday(oracleDate)
    }
    BackHandler(enabled = showDailyPrompt) { showDailyPrompt = false }

    fun entryKey(entry: OracleTimelineEntry) = when (entry) {
        is OracleTimelineEntry.Reading -> "reading-${entry.reading.id}"
        is OracleTimelineEntry.Chat -> "chat-${entry.message.id}"
    }
    fun toggleSelection(entry: OracleTimelineEntry) {
        val key = entryKey(entry)
        if (selectedKeys.containsKey(key)) selectedKeys.remove(key) else selectedKeys[key] = true
    }
    fun exitSelection() {
        selectedKeys.clear()
    }
    fun deleteSelection() {
        val readingIds = oracleTimeline
            .filterIsInstance<OracleTimelineEntry.Reading>()
            .filter { selectedKeys.containsKey("reading-${it.reading.id}") }
            .map { it.reading.id }
            .toSet()
        val chatIds = oracleTimeline
            .filterIsInstance<OracleTimelineEntry.Chat>()
            .filter { selectedKeys.containsKey("chat-${it.message.id}") }
            .map { it.message.id }
            .toSet()
        if (readingIds.isNotEmpty()) vm.deleteReadingsByIds(readingIds)
        if (chatIds.isNotEmpty()) vm.deleteChatMessagesByIds(chatIds)
        exitSelection()
    }

    fun showVoiceMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun commitRecognizedText(text: String) {
        val recognized = text.trim()
        if (recognized.isBlank()) return
        vm.question = when {
            questionBeforeRecording.isBlank() -> recognized
            questionBeforeRecording.last().isWhitespace() -> questionBeforeRecording + recognized
            else -> "${questionBeforeRecording.trimEnd()} $recognized"
        }
    }

    LaunchedEffect(offlineRecognizer, vm.cloudSpeechEnabled) {
        if (vm.cloudSpeechEnabled) {
            modelReady = true
        } else {
            val result = offlineRecognizer.prepare()
            modelReady = result.isSuccess
            if (result.isFailure) showVoiceMessage("离线语音模型加载失败，请重新安装应用")
        }
    }

    fun startRecording() {
        if (isRecording) return
        val cloudConfig = if (vm.cloudSpeechEnabled) {
            when {
                vm.cloudSpeechApiKey.isBlank() -> {
                    showVoiceMessage("请先在“我的 > 设置”中填写语音接口 API Key")
                    return
                }
                vm.cloudSpeechEndpoint.isBlank() || vm.cloudSpeechModel.isBlank() -> {
                    showVoiceMessage("请先完整配置 AI 语音接口")
                    return
                }
                else -> CloudSpeechConfig(
                    endpoint = vm.cloudSpeechEndpoint,
                    apiKey = vm.cloudSpeechApiKey,
                    model = vm.cloudSpeechModel,
                )
            }
        } else {
            null
        }
        if (cloudConfig == null && !modelReady) {
            showVoiceMessage("离线语音模型正在加载，请稍后再试")
            return
        }
        questionBeforeRecording = vm.question
        keyboardController?.hide()
        isCancelling = false
        voiceLevel = 0.06f
        isRecording = offlineRecognizer.start(
            cloudConfig = cloudConfig,
            onFinal = { text ->
                voiceLevel = 0f
                if (text.isBlank()) {
                    showVoiceMessage("没有听清，请重试")
                } else {
                    commitRecognizedText(text)
                }
            },
            onLevel = { voiceLevel = it },
            onError = {
                isRecording = false
                isCancelling = false
                voiceLevel = 0f
                showVoiceMessage(it)
            },
        )
        if (!isRecording) {
            showVoiceMessage("无法启动麦克风，请重试")
        }
    }

    fun finishRecording(cancelled: Boolean) {
        if (!isRecording) return
        isRecording = false
        isCancelling = false
        textEditing = false
        voiceLevel = 0f
        if (cancelled) {
            offlineRecognizer.stop(cancelled = true)
        } else {
            offlineRecognizer.stop(cancelled = false)
        }
    }

    fun sendChatMessage() {
        when {
            selectionMode -> showVoiceMessage("请先退出选择模式")
            vm.question.isBlank() -> showVoiceMessage("请先输入问题")
            vm.aiApiKey.isBlank() -> showVoiceMessage("请先在“我的 > 设置”中配置 AI 解读")
            else -> {
                keyboardController?.hide()
                textEditing = false
                keyboardEdited = false
                showDailyPrompt = false
                vm.sendChatMessage()
            }
        }
    }

    fun dismissTextInput() {
        keyboardController?.hide()
        focusManager.clearFocus(force = true)
        textEditing = false
        if (vm.question.isBlank()) keyboardEdited = false
    }

    LaunchedEffect(oracleTimeline.size, vm.chatSending, vm.coinCasting, vm.coinCastingLines.size) {
        val currentSnapshot = OracleFollowSnapshot(
            timelineSize = oracleTimeline.size,
            chatSending = vm.chatSending,
            coinCasting = vm.coinCasting,
            coinLineCount = vm.coinCastingLines.size,
        )
        val shouldFollowNewContent = shouldFollowOracleContent(observedFollowSnapshot, currentSnapshot)
        observedFollowSnapshot = currentSnapshot

        if (shouldFollowNewContent) {
            showDailyPrompt = false
            withFrameNanos { }
            val targetIndex = (timelineState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
            if (targetIndex - timelineState.firstVisibleItemIndex > 3) {
                timelineState.scrollToItem(targetIndex)
            } else {
                timelineState.animateScrollToItem(targetIndex)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        showVoiceMessage(if (granted) "请再次长按输入框说话" else "需要麦克风权限才能使用语音输入")
    }

    fun handlePressStart() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startRecording()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { dismissTextInput() })
            }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (showDailyPrompt) {
                DailyOraclePrompt(
                    questions = suggestedQuestions,
                    onQuestionClick = { question ->
                        vm.question = question
                        keyboardEdited = true
                    },
                    onReturnToTimeline = { showDailyPrompt = false },
                    swipeThresholdPx = oraclePageSwipeThreshold,
                )
            } else {
            LazyColumn(
                state = timelineState,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(timelineState, oraclePageSwipeThreshold, selectionMode) {
                        if (selectionMode) return@pointerInput
                        awaitEachGesture {
                            val down = awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial,
                            )
                            val startedAtTimelineEnd = !timelineState.canScrollForward
                            var totalDragX = 0f
                            var totalDragY = 0f
                            var revealPrompt = false
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                totalDragX = change.position.x - down.position.x
                                totalDragY = change.position.y - down.position.y
                                revealPrompt = startedAtTimelineEnd &&
                                    isUpwardOraclePageSwipe(totalDragY, oraclePageSwipeThreshold)
                                if (revealPrompt) change.consume()
                                if (!change.pressed) {
                                    if (revealPrompt) {
                                        showDailyPrompt = true
                                    } else if (
                                        sqrt(totalDragX * totalDragX + totalDragY * totalDragY) <=
                                        viewConfiguration.touchSlop
                                    ) {
                                        dismissTextInput()
                                    }
                                    break
                                }
                            }
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(oracleTimeline, key = { entry ->
                    when (entry) {
                        is OracleTimelineEntry.Reading -> "reading-${entry.reading.id}"
                        is OracleTimelineEntry.Chat -> "chat-${entry.message.id}"
                    }
                }) { entry ->
                    val key = entryKey(entry)
                    val selected = selectedKeys.containsKey(key)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                enabled = true,
                                onLongClick = {
                                    if (!selectedKeys.containsKey(key)) selectedKeys[key] = true
                                },
                                onClick = {
                                    if (selectionMode) toggleSelection(entry)
                                },
                            ),
                    ) {
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(C.gold.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                    .border(1.dp, C.gold, RoundedCornerShape(8.dp)),
                            )
                        }
                        when (entry) {
                            is OracleTimelineEntry.Reading -> ReadingCard(entry.reading, compact = true)
                            is OracleTimelineEntry.Chat -> ChatBubble(entry.message)
                        }
                        if (selectionMode) {
                            Icon(
                                if (selected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (selected) C.gold else C.textSub,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.TopEnd),
                            )
                        }
                    }
                }
                if (vm.coinCasting) {
                    item(key = "coin-casting") {
                        CoinCastingProgressCard(
                            lines = vm.coinCastingLines,
                            question = vm.coinCastingQuestion,
                        )
                    }
                }
                if (vm.chatSending) {
                    item(key = "chat-loading") {
                        ThinkingBubble()
                    }
                }
                if (vm.chatStatus.isNotBlank()) {
                    item(key = "chat-status") {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = C.rose, modifier = Modifier.size(14.dp))
                            Text(vm.chatStatus, color = C.rose, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                item(key = "timeline-bottom") { Spacer(Modifier.height(4.dp)) }
            }
            }
        }
        if (selectionMode) {
            Surface(
                color = C.panelAlt,
                border = BorderStroke(1.dp, C.line),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(onClick = { exitSelection() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("取消")
                    }
                    Text(
                        "已选 ${selectedKeys.size} 项",
                        color = C.textMain,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TextButton(onClick = { confirmDeleteSelection = true }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("删除", color = C.danger)
                    }
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                val coinsSrc = remember { MutableInteractionSource() }
                OutlinedButton(
                    onClick = {
                        showDailyPrompt = false
                        vm.castCoins()
                    },
                    enabled = !vm.coinCasting,
                    interactionSource = coinsSrc,
                    modifier = Modifier.weight(1f).then(pressScaleModifier(coinsSrc)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = C.textMain),
                    border = BorderStroke(1.dp, C.line),
                ) {
                    Icon(Icons.Default.Toll, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (vm.coinCasting) "起卦 ${vm.coinCastingLines.size}/6" else "铜钱卦")
                }
                val bookSrc = remember { MutableInteractionSource() }
                OutlinedButton(
                    onClick = {
                        showDailyPrompt = false
                        vm.drawAnswerBook()
                    },
                    interactionSource = bookSrc,
                    modifier = Modifier.weight(1f).then(pressScaleModifier(bookSrc)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = C.textMain),
                    border = BorderStroke(1.dp, C.line),
                ) {
                    Icon(Icons.Default.Book, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("答案之书")
                }
            }
            Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.graphicsLayer { alpha = if (isRecording) 0f else 1f },
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedTextField(
                        value = vm.question,
                        onValueChange = {
                            vm.question = it
                            keyboardEdited = it.isNotBlank()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(questionFocusRequester)
                            .onFocusChanged { state ->
                                if (!state.isFocused) textEditing = false
                            }
                            .then(
                                if (textEditing || keyboardEdited) {
                                    Modifier
                                } else {
                                    Modifier.pointerInput(modelReady, vm.cloudSpeechEnabled, voiceHoldThresholdMs) {
                                        awaitEachGesture {
                                            val down = awaitFirstDown(
                                                requireUnconsumed = false,
                                                pass = PointerEventPass.Initial,
                                            )
                                            down.consume()
                                            val releasedBeforeVoice = withTimeoutOrNull(voiceHoldThresholdMs) {
                                                while (true) {
                                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                                    val change = event.changes.firstOrNull { it.id == down.id }
                                                        ?: return@withTimeoutOrNull true
                                                    change.consume()
                                                    if (!change.pressed) return@withTimeoutOrNull true
                                                }
                                            } == true

                                            if (releasedBeforeVoice) {
                                                textEditing = true
                                                voiceUiScope.launch {
                                                    delay(20)
                                                    questionFocusRequester.requestFocus()
                                                    keyboardController?.show()
                                                }
                                                return@awaitEachGesture
                                            }

                                            handlePressStart()
                                            var cancelled = false
                                            while (true) {
                                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                                val change = event.changes.firstOrNull { it.id == down.id }
                                                if (change == null) {
                                                    finishRecording(cancelled = true)
                                                    break
                                                }
                                                change.consume()
                                                cancelled = change.position.y < down.position.y - VOICE_CANCEL_DISTANCE_DP.dp.toPx()
                                                isCancelling = cancelled
                                                if (!change.pressed) {
                                                    finishRecording(cancelled)
                                                    break
                                                }
                                            }
                                        }
                                    }
                                }
                            ),
                        placeholder = { Text("输入问题，或在心中默念") },
                        maxLines = 3,
                        readOnly = !textEditing && !keyboardEdited,
                    )
                    val sendSrc = remember { MutableInteractionSource() }
                    IconButton(
                        onClick = ::sendChatMessage,
                        enabled = vm.question.isNotBlank() && !vm.chatSending,
                        interactionSource = sendSrc,
                        modifier = Modifier
                            .size(52.dp)
                            .background(C.mint, RoundedCornerShape(8.dp))
                            .then(pressScaleModifier(sendSrc)),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "发送给 AI",
                            tint = C.chatText,
                        )
                    }
                }
                Text(
                    "轻触输入文字，按住输入语音",
                    color = C.textSub,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            if (isRecording) {
                VoiceCapturePanel(
                    level = voiceLevel,
                    cancelling = isCancelling,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        }
    }
    if (confirmDeleteSelection) {
        ConfirmDeleteDialog(
            title = "删除选中项",
            message = "确定删除选中的 ${selectedKeys.size} 项记录吗？",
            onConfirm = { deleteSelection() },
            onDismiss = { confirmDeleteSelection = false },
        )
    }
}

@Composable
private fun DailyOraclePrompt(
    questions: List<String>,
    onQuestionClick: (String) -> Unit,
    onReturnToTimeline: () -> Unit,
    swipeThresholdPx: Float,
) {
    val C = LocalFortunePalette.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(swipeThresholdPx) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var totalDragY = 0f
                    var returnToTimeline = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        totalDragY = change.position.y - down.position.y
                        returnToTimeline = isDownwardOraclePageSwipe(totalDragY, swipeThresholdPx)
                        if (returnToTimeline) change.consume()
                        if (!change.pressed) {
                            if (returnToTimeline) onReturnToTimeline()
                            break
                        }
                    }
                }
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 52.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                "今天想问些什么？",
                color = C.textMain,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            questions.take(4).forEach { question ->
                val interactionSource = remember(question) { MutableInteractionSource() }
                Surface(
                    onClick = { onQuestionClick(question) },
                    interactionSource = interactionSource,
                    color = C.panelAlt,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, C.line),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(pressScaleModifier(interactionSource)),
                ) {
                    Text(
                        question,
                        color = C.textMain,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                    )
                }
            }
        }
    }
}

internal data class OracleFollowSnapshot(
    val timelineSize: Int,
    val chatSending: Boolean,
    val coinCasting: Boolean,
    val coinLineCount: Int,
)

internal fun shouldFollowOracleContent(
    previous: OracleFollowSnapshot,
    current: OracleFollowSnapshot,
): Boolean =
    current.timelineSize > previous.timelineSize ||
        (current.chatSending && !previous.chatSending) ||
        (current.coinCasting && !previous.coinCasting) ||
        current.coinLineCount > previous.coinLineCount

// 零依赖 Markdown 渲染：行内 **加粗** *斜体* `代码`；块级 #/##/### 标题、- 列表、> 引用、``` 代码块、--- 分隔线。
// onLight=true 用于浅色气泡（聊天，深色文字+深色 accent）；false 用于深色背景（占卜卡，浅色文字+金色 accent）。
@Composable
private fun MarkdownText(
    text: String,
    color: Color,
    style: TextStyle,
    modifier: Modifier = Modifier,
    onLight: Boolean = false,
) {
    val C = LocalFortunePalette.current
    val accent = if (onLight) color.copy(alpha = 0.85f) else C.gold
    val quoteColor = if (onLight) color.copy(alpha = 0.7f) else C.textSub
    val codeBg = if (onLight) color.copy(alpha = 0.08f) else C.textMain.copy(alpha = 0.06f)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val lines = text.split("\n")
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            when {
                line.startsWith("```") -> {
                    val lang = line.removePrefix("```").trim()
                    val code = StringBuilder()
                    i++
                    while (i < lines.size && !lines[i].startsWith("```")) {
                        code.append(lines[i]).append("\n")
                        i++
                    }
                    i++ // skip closing ```
                    Surface(color = codeBg, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            code.toString().trimEnd('\n'),
                            color = color,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp),
                        )
                    }
                }
                line.startsWith("### ") -> {
                    Text(parseInline(line.removePrefix("### "), color, style, accent), color = color, style = style.merge(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = style.fontSize * 0.95f)))
                }
                line.startsWith("## ") -> {
                    Text(parseInline(line.removePrefix("## "), color, style, accent), color = color, style = style.merge(SpanStyle(fontWeight = FontWeight.Bold, fontSize = style.fontSize * 1.1f)))
                }
                line.startsWith("# ") -> {
                    Text(parseInline(line.removePrefix("# "), color, style, accent), color = color, style = style.merge(SpanStyle(fontWeight = FontWeight.Bold, fontSize = style.fontSize * 1.25f)))
                }
                line.startsWith("> ") -> {
                    Surface(color = color.copy(alpha = 0.05f), modifier = Modifier.fillMaxWidth().padding(start = 0.dp)) {
                        Row {
                            Box(modifier = Modifier.width(3.dp).height(IntrinsicSize.Min).background(accent.copy(alpha = 0.6f)))
                            Text(
                                parseInline(line.removePrefix("> "), quoteColor, style, accent),
                                style = style.copy(color = quoteColor),
                                modifier = Modifier.padding(start = 10.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
                            )
                        }
                    }
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    Row(verticalAlignment = Alignment.Top) {
                        Text("•", color = accent, style = style, modifier = Modifier.padding(end = 6.dp))
                        Text(parseInline(line.drop(2), color, style, accent), color = color, style = style, modifier = Modifier.weight(1f))
                    }
                }
                Regex("^\\d+\\.\\s").containsMatchIn(line) -> {
                    val m = Regex("^(\\d+)\\.\\s").find(line)!!
                    val num = m.groupValues[1]
                    val rest = line.substring(m.range.last + 1)
                    Row(verticalAlignment = Alignment.Top) {
                        Text("$num.", color = accent, style = style, modifier = Modifier.padding(end = 6.dp))
                        Text(parseInline(rest, color, style, accent), color = color, style = style, modifier = Modifier.weight(1f))
                    }
                }
                line.trim() == "---" || line.trim() == "***" -> {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color.copy(alpha = 0.18f)))
                }
                line.isBlank() -> Unit
                else -> {
                    Text(parseInline(line, color, style, accent), color = color, style = style)
                }
            }
            i++
        }
    }
}

// 行内：**加粗** *斜体* `代码`（代码优先，避免 ** 被误吞）。顺序处理。
private fun parseInline(text: String, baseColor: Color, baseStyle: TextStyle, accent: Color): AnnotatedString {
    val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
    val italicStyle = SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
    val codeStyle = SpanStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, background = baseColor.copy(alpha = 0.10f))
    return buildAnnotatedString {
        var i = 0
        val s = text
        while (i < s.length) {
            when {
                s.startsWith("**", i) -> {
                    val end = s.indexOf("**", i + 2)
                    if (end >= 0) {
                        withStyle(boldStyle) { append(s.substring(i + 2, end)) }
                        i = end + 2
                    } else { append(s[i]); i++ }
                }
                s.startsWith("`", i) -> {
                    val end = s.indexOf('`', i + 1)
                    if (end >= 0) {
                        withStyle(codeStyle) { append(s.substring(i + 1, end)) }
                        i = end + 1
                    } else { append(s[i]); i++ }
                }
                s.startsWith("*", i) -> {
                    val end = s.indexOf('*', i + 1)
                    if (end >= 0 && end > i + 1) {
                        withStyle(italicStyle) { append(s.substring(i + 1, end)) }
                        i = end + 1
                    } else { append(s[i]); i++ }
                }
                else -> { append(s[i]); i++ }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val C = LocalFortunePalette.current
    val isUser = message.role == "user"
    val bubbleColor = if (isUser) C.chatBubbleUser else C.chatBubbleAssistant
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.84f),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    if (isUser) "你" else "知否研习",
                    color = C.chatText.copy(alpha = 0.68f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                MarkdownText(message.content, color = C.chatText, style = MaterialTheme.typography.bodyMedium, onLight = C.isLight)
                if (message.createdAt.isNotBlank()) {
                    Text(
                        message.createdAt,
                        color = C.chatText.copy(alpha = 0.54f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
        }
    }
}

// 加载态：三个呼吸跳动的圆点，比静态"正在思考…"更有活力（redesign: animated loading）。
@Composable
private fun ThinkingBubble() {
    val C = LocalFortunePalette.current
    val transition = rememberInfiniteTransition(label = "thinking")
    val dots = listOf(0, 1, 2).map { i ->
        transition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = androidx.compose.animation.core.StartOffset(i * 180),
            ),
            label = "dot$i",
        )
    }
    Surface(
        color = C.chatBubbleAssistant,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(0.84f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dots.forEach { d ->
                Box(
                    Modifier
                        .size(8.dp)
                        .background(C.chatText.copy(alpha = d.value), CircleShape),
                )
            }
        }
    }
}

@Composable
private fun VoiceCapturePanel(
    level: Float,
    cancelling: Boolean,
    modifier: Modifier = Modifier,
) {
    val C = LocalFortunePalette.current
    val color = if (cancelling) C.rose else C.gold
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            if (cancelling) "松手取消发送" else "松手完成，上滑取消",
            color = color,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(10.dp))
        Canvas(modifier = Modifier.fillMaxWidth(0.68f).height(30.dp)) {
            val barCount = 31
            val step = size.width / barCount
            val centerY = size.height / 2f
            repeat(barCount) { index ->
                val distance = kotlin.math.abs(index - barCount / 2f) / (barCount / 2f)
                val envelope = 1f - distance * 0.55f
                val variation = 0.55f + 0.45f * kotlin.math.abs(kotlin.math.sin(index * 1.67f))
                val halfHeight = (2.dp.toPx() + level * centerY * envelope * variation).coerceAtMost(centerY)
                val x = step * (index + 0.5f)
                drawLine(
                    color = color,
                    start = Offset(x, centerY - halfHeight),
                    end = Offset(x, centerY + halfHeight),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun ToolsScreen(onOpenDice: () -> Unit, onOpenWheel: () -> Unit, onOpenCoin: () -> Unit) {
    val C = LocalFortunePalette.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("小工具", color = C.textMain, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Surface(
            onClick = onOpenDice,
            color = C.panel,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, C.line),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.tool_dice_card),
                    contentDescription = "摇骰子",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                )
                Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("摇骰子", color = C.textMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("选择骰子个数和面数，合上骰盅后摇动手机或点击按钮。", color = C.textSub, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Surface(
            onClick = onOpenWheel,
            color = C.panel,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, C.line),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.tool_wheel_card),
                    contentDescription = "转盘",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                )
                Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("转盘", color = C.textMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("自定义选项，摇动手机或拨动转盘随机抽取。", color = C.textSub, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Surface(
            onClick = onOpenCoin,
            color = C.panel,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, C.line),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.tool_coin_card),
                    contentDescription = "抛硬币",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                )
                Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("抛硬币", color = C.textMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("最多同时抛出 10 枚硬币，花面与字面各有 50% 概率。", color = C.textSub, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

private enum class CoinSide(val label: String) {
    Flower("花面"),
    Character("字面"),
}

@Composable
private fun CoinScreen(onBack: () -> Unit) {
    val C = LocalFortunePalette.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptics = remember(context) { ToolHaptics(context.applicationContext) }
    val rotations = remember { List(10) { Animatable(0f) } }
    val secureRandom = remember { SecureRandom() }
    var coinCount by remember { mutableStateOf(1) }
    var results by remember { mutableStateOf(listOf(CoinSide.Flower)) }
    var flipping by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("等待抛掷") }
    var history by remember { mutableStateOf<List<String>>(emptyList()) }
    var lastShakeAt by remember { mutableStateOf(0L) }

    fun tossCoins() {
        if (flipping) return
        flipping = true
        val tosses = List(coinCount) {
            if (secureRandom.nextBoolean()) CoinSide.Flower else CoinSide.Character
        }
        results = tosses
        haptics.coinStart()
        scope.launch {
            tosses.forEachIndexed { index, _ ->
                launch {
                    delay(110L + index * 24L)
                    haptics.coinFlip(index)
                }
            }
            val jobs = tosses.indices.map { index ->
                launch {
                    val normalized = rotations[index].value % 360f
                    rotations[index].snapTo(normalized)
                    val fullTurns = 5 + secureRandom.nextInt(3)
                    rotations[index].animateTo(
                        targetValue = coinTargetRotation(
                            currentRotation = normalized,
                            characterSide = tosses[index] == CoinSide.Character,
                            fullTurns = fullTurns,
                        ),
                        animationSpec = tween(
                            durationMillis = 980 + index * 35,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                }
            }
            jobs.forEach { it.join() }
            val flowerCount = tosses.count { it == CoinSide.Flower }
            val characterCount = tosses.size - flowerCount
            resultText = "花面 ${flowerCount} · 字面 ${characterCount}"
            history = (listOf(resultText) + history).take(10)
            haptics.coinSettle()
            flipping = false
        }
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values.getOrNull(0) ?: return
                val y = event.values.getOrNull(1) ?: return
                val z = event.values.getOrNull(2) ?: return
                val force = sqrt(x * x + y * y + z * z)
                val now = System.currentTimeMillis()
                if (force > 20f && now - lastShakeAt > 1_200L && !flipping) {
                    lastShakeAt = now
                    tossCoins()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sensorManager?.unregisterListener(listener) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("小工具")
            }
            Text("抛硬币", color = C.textMain, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(72.dp))
        }
        DiceDropdown(
            label = "硬币个数",
            value = "${coinCount}枚",
            options = (1..10).map { it to "${it}枚" },
            enabled = !flipping,
            onSelect = { count ->
                coinCount = count
                results = List(count) { CoinSide.Flower }
                resultText = "等待抛掷"
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = C.panel),
            border = BorderStroke(1.dp, C.line),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val rows = results.chunked(5)
                rows.forEachIndexed { rowIndex, row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    ) {
                        row.forEachIndexed { columnIndex, side ->
                            val index = rowIndex * 5 + columnIndex
                            CoinVisual(
                                side = side,
                                rotation = rotations[index].value,
                                rolling = flipping,
                                modifier = Modifier.size(if (coinCount > 5) 58.dp else 74.dp),
                            )
                        }
                    }
                }
                Text(
                    if (flipping) "翻转中" else resultText,
                    color = if (flipping) C.textSub else C.gold,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Button(
                    onClick = ::tossCoins,
                    enabled = !flipping,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = C.gold,
                        contentColor = if (C.isLight) Color(0xFFFAF7F0) else C.ink,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (flipping) "翻转中" else "抛硬币")
                }
            }
        }
        Text(
            "点击按钮或摇晃手机开始；每枚硬币独立随机，花面与字面各有 50% 概率。",
            color = C.textSub,
            style = MaterialTheme.typography.bodySmall,
        )
        if (history.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = C.panelAlt),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("最近结果", color = C.textMain, fontWeight = FontWeight.SemiBold)
                    history.forEach { value -> Badge(value) }
                }
            }
        }
    }
}

@Composable
private fun CoinVisual(
    side: CoinSide,
    rotation: Float,
    rolling: Boolean,
    modifier: Modifier = Modifier.size(82.dp),
) {
    val C = LocalFortunePalette.current
    val density = LocalDensity.current
    val visibleSide = if (rolling) {
        if (isCharacterCoinSideVisible(rotation)) CoinSide.Character else CoinSide.Flower
    } else {
        side
    }
    val displayRotation = if (rolling) rotation else if (side == CoinSide.Character) 180f else 0f
    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = displayRotation
                rotationX = if (rolling) kotlin.math.sin(Math.toRadians(displayRotation.toDouble())).toFloat() * 7f else 0f
                cameraDistance = 12f * density.density
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationY = if (visibleSide == CoinSide.Character) 180f else 0f },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension * 0.42f
                drawCircle(Color.Black.copy(alpha = 0.28f), radius = radius * 1.04f, center = center.copy(y = center.y + radius * 0.12f))
                drawCircle(C.gold.copy(alpha = 0.95f), radius = radius, center = center)
                drawCircle(C.gold.copy(alpha = 0.34f), radius = radius * 0.82f, center = center, style = Stroke(width = 2.dp.toPx()))
                drawCircle(Color.White.copy(alpha = 0.18f), radius = radius * 0.68f, center = center, style = Stroke(width = 1.dp.toPx()))
            }
            Text(
                visibleSide.label.removeSuffix("面"),
                color = C.ink,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

internal fun isCharacterCoinSideVisible(rotation: Float): Boolean {
    val normalized = ((rotation % 360f) + 360f) % 360f
    return normalized >= 90f && normalized < 270f
}

internal fun coinTargetRotation(
    currentRotation: Float,
    characterSide: Boolean,
    fullTurns: Int,
): Float {
    val normalized = ((currentRotation % 360f) + 360f) % 360f
    val target = if (characterSide) 180f else 0f
    val remainder = ((target - normalized) + 360f) % 360f
    return currentRotation + fullTurns.coerceAtLeast(0) * 360f + remainder
}

// 与 Canvas 绘制规则严格同源：扇区 i 中心绘制角度 = 270 + i*segDeg，指针在顶部=270°。
// rotationZ 顺时针旋转 angle 度后，扇区 i 中心出现在 270 + i*segDeg + angle。
// 指向指针（270）的板块：i*segDeg ≡ -angle (mod 360)。
private fun winnerFromAngle(angle: Float, n: Int): Int {
    if (n < 1) return 0
    val segDeg = 360f / n
    val normalized = ((-angle) % 360f + 360f) % 360f   // 0..360
    val idx = ((normalized + segDeg / 2f) / segDeg).toInt() % n
    return ((idx % n) + n) % n
}

private enum class WheelSubPage { Main, Settings, History }

@Composable
private fun WheelScreen(vm: FortuneViewModel, onBack: () -> Unit) {
    val C = LocalFortunePalette.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptics = remember(context) { ToolHaptics(context.applicationContext) }
    val segments = vm.wheelSegments
    val measurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = C.textMain)
    val palette = listOf(C.gold, C.rose, C.mint, Color(0xFF6C8EAD), Color(0xFFB07AA1), Color(0xFFD9A566))

    val angle = remember { Animatable(0f) }
    val glow = remember { Animatable(0f) }
    var spinning by remember { mutableStateOf(false) }
    var spinStartMs by remember { mutableStateOf(0L) }
    var spinJob by remember { mutableStateOf<Job?>(null) }
    var winnerIndex by remember { mutableStateOf(-1) }
    var wheelSubPage by remember { mutableStateOf(WheelSubPage.Main) }
    var lastShakeAt by remember { mutableStateOf(0L) }

    fun kick(direction: Float = 1f) {
        val n = vm.wheelSegments.size
        if (n < 2) return
        spinJob?.cancel()
        spinning = true
        spinStartMs = System.currentTimeMillis()
        winnerIndex = -1
        val dir = if (direction >= 0f) 1f else -1f
        haptics.wheelStart()
        spinJob = scope.launch {
            // 两段线性减速，速度连续：前 3s v0->v1，后 1s v1->0。方向由 dir 决定。
            val v0 = dir * 10f * 360f      // 初始角速度（度/秒，带方向）
            val v1 = dir * 1.5f * 360f     // t=3s 时的角速度（度/秒，带方向）
            val t1 = 3f
            val t2 = 4f
            var pos = angle.value
            var v = v0
            val segmentAngle = 360f / n
            var lastTickIndex = kotlin.math.floor(angle.value / segmentAngle).toInt()
            var lastSec = withFrameNanos { it / 1_000_000_000.0 }
            while (true) {
                val nowSec = withFrameNanos { it / 1_000_000_000.0 }
                val dt = (nowSec - lastSec).toFloat().coerceIn(0f, 0.05f)
                lastSec = nowSec
                val t = (System.currentTimeMillis() - spinStartMs) / 1000f
                if (t >= t2) break
                v = if (t < t1) {
                    v0 + (v1 - v0) * (t / t1)
                } else {
                    v1 * (1f - (t - t1) / (t2 - t1))
                }
                pos += v * dt
                angle.snapTo(pos)
                val tickIndex = kotlin.math.floor(angle.value / segmentAngle).toInt()
                if (tickIndex != lastTickIndex) {
                    haptics.wheelTick()
                    lastTickIndex = tickIndex
                }
            }
            spinning = false
            winnerIndex = winnerFromAngle(angle.value, n)
            vm.wheelSegments.getOrNull(winnerIndex)?.let { vm.addWheelHistory(it) }
            haptics.wheelSettle()
        }
        scope.launch {
            glow.snapTo(0f)
            glow.animateTo(1f, tween(durationMillis = 250))
            glow.animateTo(0f, tween(durationMillis = 250))
        }
    }

    // 慢速拖动松手后的惯性滑行：从松手速度线性衰减到 0，不产生判断结果。
    fun coast(speedDegPerSec: Float) {
        spinJob?.cancel()
        val speed = speedDegPerSec.coerceIn(-3600f * 2f, 3600f * 2f)
        if (speed.absoluteValue < 30f) return   // 太慢不滑
        spinning = true
        spinStartMs = System.currentTimeMillis()
        haptics.wheelStart()
        spinJob = scope.launch {
            val decel = 1800f            // 减速度（度/秒²）
            val duration = speed.absoluteValue / decel
            val dir = if (speed >= 0f) 1f else -1f
            var pos = angle.value
            val segmentAngle = 360f / vm.wheelSegments.size.coerceAtLeast(2)
            var lastTickIndex = kotlin.math.floor(angle.value / segmentAngle).toInt()
            var lastSec = withFrameNanos { it / 1_000_000_000.0 }
            while (true) {
                val nowSec = withFrameNanos { it / 1_000_000_000.0 }
                val dt = (nowSec - lastSec).toFloat().coerceIn(0f, 0.05f)
                lastSec = nowSec
                val t = (System.currentTimeMillis() - spinStartMs) / 1000f
                if (t >= duration) break
                val v = dir * (speed.absoluteValue - decel * t).coerceAtLeast(0f)
                pos += v * dt
                angle.snapTo(pos)
                val tickIndex = kotlin.math.floor(angle.value / segmentAngle).toInt()
                if (tickIndex != lastTickIndex) {
                    haptics.wheelTick()
                    lastTickIndex = tickIndex
                }
            }
            spinning = false
            haptics.wheelGlideStop()
        }
    }

    fun tryAccelerate() {
        if (!spinning) {
            kick(1f)
        } else if (System.currentTimeMillis() - spinStartMs < 3000L) {
            kick(1f)
        }
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values.getOrNull(0) ?: return
                val y = event.values.getOrNull(1) ?: return
                val z = event.values.getOrNull(2) ?: return
                val force = sqrt(x * x + y * y + z * z)
                val now = System.currentTimeMillis()
                val threshold = if (spinning) 45f else 25f
                if (force > threshold && now - lastShakeAt > 350L && wheelSubPage == WheelSubPage.Main) {
                    lastShakeAt = now
                    tryAccelerate()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sensorManager?.unregisterListener(listener) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("小工具")
            }
            Text("转盘", color = C.textMain, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(72.dp))
        }

        when (wheelSubPage) {
            WheelSubPage.Settings -> WheelSettingsView(vm)
            WheelSubPage.History -> WheelHistoryView(vm)
            WheelSubPage.Main -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .pointerInput(Unit) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        var prevAngle = 0.0
                        var lastDragMs = 0L
                        var lastDragDeg = 0.0
                        var releaseSpeed = 0f   // 度/秒，带方向
                        detectDragGestures(
                            onDragStart = { offset ->
                                prevAngle = Math.atan2((offset.y - cy).toDouble(), (offset.x - cx).toDouble())
                                lastDragMs = System.currentTimeMillis()
                                lastDragDeg = 0.0
                                releaseSpeed = 0f
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                if (!spinning) {
                                    val a = Math.atan2((change.position.y - cy).toDouble(), (change.position.x - cx).toDouble())
                                    var d = Math.toDegrees(a - prevAngle)
                                    while (d > 180.0) d -= 360.0
                                    while (d < -180.0) d += 360.0
                                    val df = d.toFloat()
                                    scope.launch { angle.snapTo(angle.value + df) }
                                    val nowMs = System.currentTimeMillis()
                                    val dtMs = (nowMs - lastDragMs).coerceAtLeast(1L)
                                    releaseSpeed = (df / dtMs * 1000f) * 0.4f + releaseSpeed * 0.6f
                                    lastDragMs = nowMs
                                    lastDragDeg = d
                                    prevAngle = a
                                }
                            },
                            onDragEnd = {
                                val sp = releaseSpeed
                                if (sp.absoluteValue >= 360f) {
                                    // 快速滑动：正式旋转，产生结果，方向跟随手指。
                                    kick(if (sp >= 0f) 1f else -1f)
                                } else if (sp.absoluteValue >= 30f) {
                                    // 慢速拖动：惯性滑行，不产生结果。
                                    coast(sp)
                                }
                            },
                            onDragCancel = { },
                        )
                    },
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationZ = angle.value },
                ) {
                    val n = segments.size.coerceAtLeast(1)
                    val segDeg = 360f / n
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val r = (if (size.width < size.height) size.width else size.height) / 2f
                    val topLeft = Offset(cx - r, cy - r)
                    val arcSize = Size(r * 2f, r * 2f)
                    for (i in 0 until n) {
                        drawArc(
                            color = palette[i % palette.size],
                            startAngle = 270f - segDeg / 2f + i * segDeg,
                            sweepAngle = segDeg,
                            useCenter = true,
                            topLeft = topLeft,
                            size = arcSize,
                        )
                    }
                    for (i in 0 until n) {
                        val a = Math.toRadians((270f - segDeg / 2f + i * segDeg).toDouble())
                        drawLine(
                            color = C.line,
                            start = Offset(cx, cy),
                            end = Offset(cx + (r * cos(a)).toFloat(), cy + (r * sin(a)).toFloat()),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
                    drawArc(
                        color = C.line,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                    val labelR = r * 0.62f
                    for (i in 0 until n) {
                        val label = segments[i].take(6)
                        if (label.isBlank()) continue
                        val midRad = Math.toRadians((270f + i * segDeg).toDouble())
                        val px = cx + (labelR * cos(midRad)).toFloat()
                        val py = cy + (labelR * sin(midRad)).toFloat()
                        val res = measurer.measure(label, labelStyle)
                        drawText(res, topLeft = Offset(px - res.size.width / 2f, py - res.size.height / 2f))
                    }
                    if (winnerIndex in 0 until n && glow.value > 0f) {
                        drawArc(
                            color = C.gold.copy(alpha = glow.value),
                            startAngle = 270f - segDeg / 2f + winnerIndex * segDeg,
                            sweepAngle = segDeg,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = 6.dp.toPx()),
                        )
                    }
                }
                Canvas(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(width = 36.dp, height = 22.dp),
                ) {
                    val w = size.width
                    val h = size.height
                    val shadow = Path().apply {
                        moveTo(w * 0.5f + 2f, h + 2f)
                        lineTo(w * 0.12f + 2f, 2f + 2f)
                        lineTo(w * 0.88f + 2f, 2f + 2f)
                        close()
                    }
                    drawPath(shadow, Color(0x66000000))
                    val tri = Path().apply {
                        moveTo(w * 0.5f, h)
                        lineTo(w * 0.12f, 2f)
                        lineTo(w * 0.88f, 2f)
                        close()
                    }
                    drawPath(tri, C.danger)
                }
            }

            val winnerLabel = segments.getOrNull(winnerIndex)
            if (winnerLabel != null) {
                Text("本次：$winnerLabel", color = C.gold, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            } else {
                Text("摇晃手机或拨动转盘开始", color = C.textSub, style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    onClick = { wheelSubPage = WheelSubPage.Settings },
                    color = C.panel,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, C.line),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = C.textSub, modifier = Modifier.size(18.dp))
                        Text("设置", color = C.textMain, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Surface(
                    onClick = { wheelSubPage = WheelSubPage.History },
                    color = C.panel,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, C.line),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = C.textSub, modifier = Modifier.size(18.dp))
                        Text("历史", color = C.textMain, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun WheelSettingsView(vm: FortuneViewModel) {
    val C = LocalFortunePalette.current
    val segments = vm.wheelSegments
    var pendingDelete by remember { mutableStateOf(-1) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("板块设置", color = C.textMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Text("最少 2 个，最多 25 个，修改自动保存", color = C.textSub, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth())
        Text("快速选择板块数量", color = C.textSub, style = MaterialTheme.typography.bodySmall)
        val quickCounts = listOf(3, 4, 6, 8, 12, 16, 24)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            quickCounts.forEach { count ->
                Surface(
                    onClick = { vm.setWheelSegmentCount(count) },
                    color = if (segments.size == count) C.gold else C.panel,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, C.line),
                ) {
                    Text(
                        count.toString(),
                        color = if (segments.size == count) C.ink else C.textMain,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }
        }
        TextButton(onClick = { vm.resetWheelSegments() }) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = C.textSub)
            Spacer(Modifier.width(4.dp))
            Text("重置为「是/否」", color = C.textSub)
        }
        segments.forEachIndexed { index, seg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = C.panel),
                border = BorderStroke(1.dp, C.line),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = seg,
                        onValueChange = { vm.updateWheelSegment(index, it) },
                        label = { Text("第 ${index + 1} 块") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        onClick = { pendingDelete = index },
                        enabled = segments.size > 2,
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "删除", tint = C.danger)
                    }
                }
            }
        }
        OutlinedButton(
            onClick = { vm.addWheelSegment() },
            enabled = segments.size < 25,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = C.textMain),
            border = BorderStroke(1.dp, C.line),
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("添加板块")
        }
    }
    if (pendingDelete >= 0) {
        ConfirmDeleteDialog(
            title = "删除板块",
            message = "确定删除这块吗？",
            onConfirm = { vm.deleteWheelSegment(pendingDelete) },
            onDismiss = { pendingDelete = -1 },
        )
    }
}

@Composable
private fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val C = LocalFortunePalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) { Text("删除", color = C.danger) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    )
}

@Composable
private fun WheelHistoryView(vm: FortuneViewModel) {
    val C = LocalFortunePalette.current
    val history = vm.wheelHistory
    var pendingDelete by remember { mutableStateOf<Long?>(null) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("历史记录", color = C.textMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        if (history.isEmpty()) {
            Text("还没有转盘记录", color = C.textSub, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 12.dp))
        } else {
            history.forEach { entry ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = C.panel),
                    border = BorderStroke(1.dp, C.line),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(entry.result, color = C.textMain, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text(entry.createdAt, color = C.textSub, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { pendingDelete = entry.id }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除", tint = C.danger)
                        }
                    }
                }
            }
        }
    }
    pendingDelete?.let { id ->
        ConfirmDeleteDialog(
            title = "删除记录",
            message = "确定删除这条转盘记录吗？",
            onConfirm = { vm.deleteWheelHistory(id) },
            onDismiss = { pendingDelete = null },
        )
    }
}

@Composable
private fun DiceScreen(onBack: () -> Unit) {
    val C = LocalFortunePalette.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptics = remember(context) { ToolHaptics(context.applicationContext) }
    val physicsWorld = remember { DicePhysicsWorld() }
    var diceCount by remember { mutableStateOf(1) }
    var diceSides by remember { mutableStateOf(6) }
    var faces by remember { mutableStateOf(listOf(1)) }
    var resultText by remember { mutableStateOf("1枚6面骰：等待摇骰") }
    var rolling by remember { mutableStateOf(false) }
    var curtainClosed by remember { mutableStateOf(false) }
    var curtainDragProgress by remember { mutableStateOf<Float?>(null) }
    var lastShakeAt by remember { mutableStateOf(0L) }
    var history by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(physicsWorld) {
        while (true) {
            physicsWorld.step(1f / 60f)
            val impactStrength = physicsWorld.consumeImpactStrength()
            if (impactStrength > 0f) {
                haptics.diceImpact(impactStrength)
            }
            delay(16)
        }
    }

    DisposableEffect(physicsWorld) {
        onDispose { physicsWorld.stop() }
    }

    fun playShakeSound() {
        try {
            MediaPlayer.create(context, R.raw.dice_shake)?.apply {
                setOnCompletionListener { player -> player.release() }
                start()
            }
        } catch (_: Throwable) {
            // Sound is decorative; rolling should continue if audio is unavailable.
        }
    }

    fun startRoll() {
        if (rolling) return
        val roll = rollDice(diceCount, diceSides)
        scope.launch {
            rolling = true
            haptics.diceStart()
            physicsWorld.startRoll(roll)
            playShakeSound()
            delay(1_650)
            faces = roll.rolls
            resultText = roll.displayText
            history = (listOf(roll.displayText) + history).take(6)
            haptics.diceSettle()
            rolling = false
        }
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values.getOrNull(0) ?: return
                val y = event.values.getOrNull(1) ?: return
                val z = event.values.getOrNull(2) ?: return
                val force = sqrt(x * x + y * y + z * z)
                val now = System.currentTimeMillis()
                if (force > 20f && now - lastShakeAt > 1200L && !rolling) {
                    lastShakeAt = now
                    scope.launch { startRoll() }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sensorManager?.unregisterListener(listener) }
    }

    val curtainProgress by animateFloatAsState(
        targetValue = curtainDragProgress ?: if (curtainClosed) 1f else 0f,
        animationSpec = if (curtainDragProgress != null) {
            snap()
        } else {
            tween(durationMillis = 240, easing = FastOutSlowInEasing)
        },
        label = "diceCurtainProgress",
    )
    val curtainVisible = curtainDragProgress != null || curtainProgress > 0.001f
    val curtainSwipeThreshold = with(LocalDensity.current) { 84.dp.toPx() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = C.panel),
            border = BorderStroke(1.dp, C.line),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("小工具")
                    }
                    Text("摇骰子", color = C.textMain, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(72.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DiceDropdown(
                        label = "骰子个数",
                        value = "${diceCount}枚",
                        options = (1..6).map { it to "${it}枚" },
                        enabled = !rolling,
                        onSelect = { count ->
                            diceCount = count
                            faces = List(count) { 1.coerceAtMost(diceSides) }
                            resultText = "${count}枚${diceSides}面骰：等待摇骰"
                        },
                        modifier = Modifier.weight(1f),
                    )
                    DiceDropdown(
                        label = "骰子面数",
                        value = "${diceSides}面",
                        options = listOf(4, 6, 8, 10, 12, 20).map { it to "${it}面" },
                        enabled = !rolling,
                        onSelect = { sides ->
                            diceSides = sides
                            faces = List(diceCount) { 1 }
                            resultText = "${diceCount}枚${sides}面骰：等待摇骰"
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(curtainClosed, curtainSwipeThreshold) {
                            var gestureDistance = 0f
                            detectVerticalDragGestures(
                                onDragStart = {
                                    gestureDistance = 0f
                                    curtainDragProgress = if (curtainClosed) 1f else 0f
                                },
                                onVerticalDrag = { _, dragAmount ->
                                    gestureDistance += dragAmount
                                    val currentProgress = curtainDragProgress ?: if (curtainClosed) 1f else 0f
                                    curtainDragProgress = (currentProgress + dragAmount / size.height.coerceAtLeast(1))
                                        .coerceIn(0f, 1f)
                                },
                                onDragEnd = {
                                    curtainClosed = when {
                                        gestureDistance > curtainSwipeThreshold -> true
                                        gestureDistance < -curtainSwipeThreshold -> false
                                        else -> (curtainDragProgress ?: 0f) >= 0.5f
                                    }
                                    curtainDragProgress = null
                                },
                                onDragCancel = { curtainDragProgress = null },
                            )
                        },
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        DiceStage(
                            faces = faces,
                            sides = diceSides,
                            physicsWorld = physicsWorld,
                            rolling = rolling,
                        )
                        DiceResultCounter(
                            faces = faces,
                            sides = diceSides,
                            resultText = resultText,
                            rolling = rolling,
                        )
                    }
                    if (curtainVisible) {
                        DiceCurtainOverlay(
                            progress = curtainProgress,
                            modifier = Modifier.matchParentSize(),
                        )
                    }
                }
                Button(
                    onClick = { startRoll() },
                    enabled = !rolling,
                    colors = ButtonDefaults.buttonColors(containerColor = C.gold, contentColor = if (C.isLight) Color(0xFFFAF7F0) else C.ink),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Casino, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (rolling) "摇动中" else "摇骰子")
                }
                Text(
                    "点击按钮或摇动手机开始；向下滑动可遮住赌盘和结果。",
                    color = C.textSub,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        if (history.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = C.panelAlt),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("最近结果", color = C.textMain, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        history.forEach { value -> Badge(value) }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiceDropdown(
    label: String,
    value: String,
    options: List<Pair<Int, String>>,
    enabled: Boolean,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val C = LocalFortunePalette.current
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = C.textSub, style = MaterialTheme.typography.labelMedium)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = C.textMain),
                border = BorderStroke(1.dp, C.line),
            ) {
                Text(value, color = C.textMain, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (rawValue, text) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            expanded = false
                            onSelect(rawValue)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DiceResultCounter(
    faces: List<Int>,
    sides: Int,
    resultText: String,
    rolling: Boolean,
) {
    val C = LocalFortunePalette.current
    val hasResult = !rolling && !resultText.contains("等待")
    Surface(
        color = if (C.isLight) Color(0xFF10211B) else Color(0xFF0B1713),
        contentColor = Color(0xFFE8F7D4),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color(0xFF3C7D5B)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "TOTAL",
                    color = Color(0xFF8DC6A2),
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    if (hasResult) faces.sum().toString() else "----",
                    color = if (rolling) Color(0xFFE1B85A) else Color(0xFFD9F6B7),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "${faces.size}D$sides",
                    color = Color(0xFF8DC6A2),
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    if (hasResult) faces.joinToString(" · ") else "等待结果",
                    color = Color(0xFFD9F6B7),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun DiceCurtainOverlay(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val C = LocalFortunePalette.current
    Box(
        modifier = modifier
            .clipToBounds()
            .graphicsLayer { translationY = -size.height * (1f - progress) },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val curtainBase = if (C.isLight) Color(0xFF6A2630) else Color(0xFF3A151D)
            val curtainLight = if (C.isLight) Color(0xFF9A3F4D) else Color(0xFF6E2735)
            drawRect(
                brush = Brush.verticalGradient(listOf(curtainLight, curtainBase)),
                size = size,
            )
            val stripeGap = 22.dp.toPx()
            var y = stripeGap
            while (y < size.height) {
                drawLine(
                    color = Color.Black.copy(alpha = 0.16f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2.dp.toPx(),
                )
                y += stripeGap
            }
            val railHeight = 18.dp.toPx()
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF2D2020), Color(0xFF110D0D), Color(0xFF332424)),
                ),
                topLeft = Offset(0f, size.height - railHeight),
                size = Size(size.width, railHeight),
            )
            drawRoundRect(
                color = Color(0xFFB98A45),
                topLeft = Offset(size.width * 0.34f, size.height - 13.dp.toPx()),
                size = Size(size.width * 0.32f, 4.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "结果已遮住",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "向上滑动打开卷帘",
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

private data class DiceRollResult(
    val quantity: Int,
    val sides: Int,
    val rolls: List<Int>,
    val sum: Int,
) {
    val displayText: String
        get() = "${quantity}枚${sides}面骰：[${rolls.joinToString(", ")}] = $sum"
}

private fun rollDice(quantity: Int, sides: Int): DiceRollResult {
    val safeQuantity = quantity.coerceIn(1, 6)
    val safeSides = if (sides in setOf(4, 6, 8, 10, 12, 20)) sides else 6
    val rolls = List(safeQuantity) { Random.nextInt(1, safeSides + 1) }
    return DiceRollResult(
        quantity = safeQuantity,
        sides = safeSides,
        rolls = rolls,
        sum = rolls.sum(),
    )
}

@Composable
private fun DiceStage(
    faces: List<Int>,
    sides: Int,
    physicsWorld: DicePhysicsWorld,
    rolling: Boolean,
) {
    PhysicsDiceStage(
        fallbackFaces = faces,
        sides = sides,
        world = physicsWorld,
        rolling = rolling,
    )
}

@Composable
private fun PhysicsDiceStage(
    fallbackFaces: List<Int>,
    sides: Int,
    world: DicePhysicsWorld,
    rolling: Boolean,
) {
    val mesh = remember(sides) { buildPhysicsMesh(sides) }
    val meshFaces = remember(sides) { buildPhysicsMeshFaces(mesh) }
    val meshEdges = remember(sides) { buildPhysicsMeshEdges(mesh) }
    val camera = remember(sides) { if (sides == 4) DICE_D4_CAMERA else DICE_TOP_CAMERA }
    val dice = world.renderState
    val fallbackBodies = remember(fallbackFaces, sides, world) {
        world.restingFallback(fallbackFaces, sides)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(20.dp)),
    ) {
        Image(
            painter = painterResource(R.drawable.dice_table_felt),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.Black.copy(alpha = 0.055f))
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF0D0D0E), Color(0xFF343536), Color(0xFF0A0A0B)),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                ),
                topLeft = Offset(5.dp.toPx(), 5.dp.toPx()),
                size = Size(size.width - 10.dp.toPx(), size.height - 10.dp.toPx()),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                style = Stroke(width = 10.dp.toPx()),
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF3B1D0E), Color(0xFF9B5729), Color(0xFF41200F)),
                    start = Offset(0f, size.height * 0.2f),
                    end = Offset(size.width, size.height * 0.8f),
                ),
                topLeft = Offset(11.dp.toPx(), 11.dp.toPx()),
                size = Size(size.width - 22.dp.toPx(), size.height - 22.dp.toPx()),
                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                style = Stroke(width = 3.dp.toPx()),
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.16f),
                topLeft = Offset(13.dp.toPx(), 13.dp.toPx()),
                size = Size(size.width - 26.dp.toPx(), size.height - 26.dp.toPx()),
                cornerRadius = CornerRadius(13.dp.toPx(), 13.dp.toPx()),
                style = Stroke(width = 0.8.dp.toPx()),
            )

        val bodies = if (dice.isEmpty()) fallbackBodies else dice
        val silverBase = Color(0xFFD5D9DC)
        val sortedBodies = bodies.sortedByDescending { die ->
            (die.position - camera.position).dot(camera.forward)
        }

        sortedBodies.forEach { die ->
            val shadow = projectDicePoint(Vec3(die.position.x, 0.01f, die.position.z), size, camera)
            drawOval(
                color = Color.Black.copy(alpha = 0.28f),
                topLeft = Offset(shadow.x - 34.dp.toPx(), shadow.y - 7.dp.toPx()),
                size = Size(68.dp.toPx(), 14.dp.toPx()),
            )
        }

        sortedBodies.forEach { die ->
            val visibleFaces = ArrayList<PhysicsRenderFace>(meshFaces.size)
            meshFaces.forEach faceLoop@ { face ->
                val worldVertices = face.vertices.map { vertex ->
                    die.position + die.rotation.rotate(vertex)
                }
                val outwardNormal = die.rotation.rotate(face.normal)
                if (outwardNormal.dot(camera.viewDirection) <= 0.015f) return@faceLoop
                val projected = worldVertices.map { vertex -> projectDicePoint(vertex, size, camera) }
                var twiceArea = 0f
                projected.indices.forEach { index ->
                    val current = projected[index]
                    val next = projected[(index + 1) % projected.size]
                    twiceArea += current.x * next.y - current.y * next.x
                }
                val area = kotlin.math.abs(twiceArea) * 0.5f
                if (area <= 0.4f) return@faceLoop
                val light = kotlin.math.abs(outwardNormal.dot(Vec3(-0.32f, 0.78f, 0.55f)))
                val shade = (0.58f + light * 0.48f).coerceIn(0.48f, 1.06f)
                visibleFaces += PhysicsRenderFace(
                    points = projected,
                    depth = projected.sumOf { point -> point.depth.toDouble() }.toFloat() / projected.size,
                    color = shadeColor(silverBase, shade),
                )
            }
            visibleFaces
                .sortedByDescending { it.depth }
                .forEach { face ->
                    val path = Path().apply {
                        moveTo(face.points.first().x, face.points.first().y)
                        face.points.drop(1).forEach { point -> lineTo(point.x, point.y) }
                        close()
                    }
                    drawPath(path, face.color)
                }

            meshEdges.forEach edgeLoop@ { edge ->
                val a = die.position + die.rotation.rotate(edge.a)
                val b = die.position + die.rotation.rotate(edge.b)
                val visible = edge.normals.any { normal ->
                    die.rotation.rotate(normal).dot(camera.viewDirection) > 0.015f
                }
                if (!visible) return@edgeLoop
                val pa = projectDicePoint(a, size, camera)
                val pb = projectDicePoint(b, size, camera)
                drawLine(
                    color = Color(0xFF765019),
                    start = Offset(pa.x, pa.y),
                    end = Offset(pb.x, pb.y),
                    strokeWidth = 2.4.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = Color(0xFFFFD978),
                    start = Offset(pa.x, pa.y),
                    end = Offset(pb.x, pb.y),
                    strokeWidth = 0.95.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            if (!rolling) {
                findDiceLabelProjection(die, mesh, size, camera)?.let { label ->
                    drawDiceLabel(die.result.toString(), label, sides)
                }
            }
        }
    }
}
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDiceLabel(
    value: String,
    label: DiceLabelProjection,
    sides: Int,
) {
    drawIntoCanvas { canvas ->
        val number = value.toIntOrNull() ?: 1
        val hue = ((number - 1) * 137.508f) % 360f
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.HSVToColor(floatArrayOf(hue, 0.82f, 0.58f))
            textSize = when {
                sides >= 20 -> 15.dp.toPx()
                sides >= 10 -> 17.dp.toPx()
                else -> 21.dp.toPx()
            }
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        while (paint.measureText(value) > label.maxTextWidth && paint.textSize > 4.dp.toPx()) {
            paint.textSize *= 0.86f
        }
        while (paint.descent() - paint.ascent() > label.maxTextHeight && paint.textSize > 4.dp.toPx()) {
            paint.textSize *= 0.86f
        }
        if (label.maxTextWidth < 5.dp.toPx() || label.maxTextHeight < 5.dp.toPx()) return@drawIntoCanvas
        val outlinePaint = android.graphics.Paint(paint).apply {
            color = android.graphics.Color.rgb(31, 34, 36)
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 1.15.dp.toPx()
            strokeJoin = android.graphics.Paint.Join.ROUND
        }
        val baseline = label.point.y - (paint.ascent() + paint.descent()) * 0.5f
        canvas.nativeCanvas.save()
        canvas.nativeCanvas.rotate(label.rotationDegrees, label.point.x, label.point.y)
        canvas.nativeCanvas.drawText(value, label.point.x, baseline, outlinePaint)
        canvas.nativeCanvas.drawText(value, label.point.x, baseline, paint)
        canvas.nativeCanvas.restore()
    }
}

private data class DiceLabelProjection(
    val point: DiceScreenPoint,
    val rotationDegrees: Float,
    val maxTextWidth: Float,
    val maxTextHeight: Float,
)

private data class DiceFaceTriangleProjection(
    val center: Vec3,
    val normal: Vec3,
    val projected: List<DiceScreenPoint>,
    val area: Float,
)

private data class DiceFaceProjectionGroup(
    val normal: Vec3,
    val triangles: MutableList<DiceFaceTriangleProjection>,
)

/** Places the result on the nearest real mesh face instead of above the die. */
private fun findDiceLabelProjection(
    die: PhysicsDieRender,
    mesh: List<PhysicsTriangle3d>,
    canvasSize: Size,
    camera: DiceCameraProjection,
): DiceLabelProjection? {
    val groups = mutableListOf<DiceFaceProjectionGroup>()
    mesh.forEach triangleLoop@ { triangle ->
        val a = die.position + die.rotation.rotate(triangle.a)
        val b = die.position + die.rotation.rotate(triangle.b)
        val c = die.position + die.rotation.rotate(triangle.c)
        val pa = projectDicePoint(a, canvasSize, camera)
        val pb = projectDicePoint(b, canvasSize, camera)
        val pc = projectDicePoint(c, canvasSize, camera)
        val area = kotlin.math.abs((pb.x - pa.x) * (pc.y - pa.y) - (pb.y - pa.y) * (pc.x - pa.x))
        if (area < 1.5f) return@triangleLoop
        val rawNormal = (b - a).cross(c - a).normalized()
        val center = (a + b + c) * (1f / 3f)
        val normal = if (rawNormal.dot(center - die.position) < 0f) rawNormal * -1f else rawNormal
        val sample = DiceFaceTriangleProjection(center, normal, listOf(pa, pb, pc), area)
        val group = groups.firstOrNull { it.normal.dot(normal) > 0.985f }
        if (group == null) {
            groups += DiceFaceProjectionGroup(normal, mutableListOf(sample))
        } else {
            group.triangles += sample
        }
    }
    if (groups.isEmpty()) return null

    val visibleGroups = groups.filter { group ->
        val center = group.triangles.map { it.center }.reduce { total, point -> total + point } *
            (1f / group.triangles.size)
        group.normal.dot(camera.viewDirection) > 0.015f
    }
    val best = visibleGroups.maxByOrNull { group ->
        val center = group.triangles.map { it.center }.reduce { total, point -> total + point } * (1f / group.triangles.size)
        val area = group.triangles.sumOf { it.area.toDouble() }.toFloat()
        val facing = group.normal.dot(camera.viewDirection).coerceAtLeast(0f)
        val upward = group.normal.y.coerceIn(-1f, 1f)
        upward * 100_000f + area * (0.72f + facing) + center.y * 240f
    } ?: return null
    val center = best.triangles.map { it.center }.reduce { total, point -> total + point } * (1f / best.triangles.size)
    val edges = best.triangles.flatMap { triangle ->
        listOf(
            triangle.projected[0] to triangle.projected[1],
            triangle.projected[1] to triangle.projected[2],
            triangle.projected[2] to triangle.projected[0],
        )
    }
    val longestEdge = edges.maxByOrNull { (a, b) ->
        val dx = b.x - a.x
        val dy = b.y - a.y
        dx * dx + dy * dy
    } ?: return null
    val minEdge = edges.minOf { (a, b) ->
        val dx = b.x - a.x
        val dy = b.y - a.y
        sqrt(dx * dx + dy * dy)
    }
    val labelPoint = projectDicePoint(center + best.normal * 0.014f, canvasSize, camera)
    val minClearance = edges.minOf { (a, b) ->
        val dx = b.x - a.x
        val dy = b.y - a.y
        val denominator = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        kotlin.math.abs(dx * (labelPoint.y - a.y) - dy * (labelPoint.x - a.x)) / denominator
    }
    val dx = longestEdge.second.x - longestEdge.first.x
    val dy = longestEdge.second.y - longestEdge.first.y
    var angle = kotlin.math.atan2(dy, dx) * 180f / Math.PI.toFloat()
    if (angle > 90f) angle -= 180f
    if (angle < -90f) angle += 180f
    return DiceLabelProjection(
        point = labelPoint,
        rotationDegrees = angle,
        maxTextWidth = if (best.triangles.size == 1) {
            (minClearance * 1.42f).coerceAtLeast(4f)
        } else {
            (minEdge * 0.52f).coerceAtLeast(4f)
        },
        maxTextHeight = if (best.triangles.size == 1) {
            (minClearance * 1.28f).coerceAtLeast(4f)
        } else {
            (minEdge * 0.48f).coerceAtLeast(4f)
        },
    )
}

private data class Vec3(var x: Float, var y: Float, var z: Float) {
    operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
    operator fun times(value: Float) = Vec3(x * value, y * value, z * value)
    fun dot(other: Vec3) = x * other.x + y * other.y + z * other.z
    fun cross(other: Vec3) = Vec3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x,
    )
    fun length() = sqrt(x * x + y * y + z * z)
    fun normalized(): Vec3 {
        val length = length()
        return if (length < 0.0001f) Vec3(0f, 1f, 0f) else this * (1f / length)
    }
}

private data class Quat(var w: Float, var x: Float, var y: Float, var z: Float) {
    operator fun times(other: Quat) = Quat(
        w * other.w - x * other.x - y * other.y - z * other.z,
        w * other.x + x * other.w + y * other.z - z * other.y,
        w * other.y - x * other.z + y * other.w + z * other.x,
        w * other.z + x * other.y - y * other.x + z * other.w,
    )

    fun normalized(): Quat {
        val length = sqrt(w * w + x * x + y * y + z * z).coerceAtLeast(0.0001f)
        return Quat(w / length, x / length, y / length, z / length)
    }

    fun rotate(vector: Vec3): Vec3 {
        val rotated = this * Quat(0f, vector.x, vector.y, vector.z) * conjugate()
        return Vec3(rotated.x, rotated.y, rotated.z)
    }

    fun integrate(angularVelocity: Vec3, dt: Float): Quat {
        val derivative = Quat(0f, angularVelocity.x, angularVelocity.y, angularVelocity.z) * this
        return Quat(
            w + derivative.w * 0.5f * dt,
            x + derivative.x * 0.5f * dt,
            y + derivative.y * 0.5f * dt,
            z + derivative.z * 0.5f * dt,
        ).normalized()
    }

    private fun conjugate() = Quat(w, -x, -y, -z)

    companion object {
        val identity = Quat(1f, 0f, 0f, 0f)

        fun fromTo(from: Vec3, to: Vec3): Quat {
            val source = from.normalized()
            val target = to.normalized()
            val dot = source.dot(target).coerceIn(-1f, 1f)
            if (dot > 0.9995f) return identity
            if (dot < -0.9995f) {
                val reference = if (kotlin.math.abs(source.x) < 0.9f) Vec3(1f, 0f, 0f) else Vec3(0f, 1f, 0f)
                val axis = source.cross(reference).normalized()
                return Quat(0f, axis.x, axis.y, axis.z)
            }
            val cross = source.cross(target)
            return Quat(
                w = 1f + dot,
                x = cross.x,
                y = cross.y,
                z = cross.z,
            ).normalized()
        }

        fun slerp(from: Quat, to: Quat, progress: Float): Quat {
            val t = progress.coerceIn(0f, 1f)
            var target = to
            var dot = from.w * target.w + from.x * target.x + from.y * target.y + from.z * target.z
            if (dot < 0f) {
                target = Quat(-target.w, -target.x, -target.y, -target.z)
                dot = -dot
            }
            if (dot > 0.9995f) {
                return Quat(
                    from.w + (target.w - from.w) * t,
                    from.x + (target.x - from.x) * t,
                    from.y + (target.y - from.y) * t,
                    from.z + (target.z - from.z) * t,
                ).normalized()
            }
            val theta = kotlin.math.acos(dot.coerceIn(-1f, 1f))
            val sinTheta = sin(theta).coerceAtLeast(0.0001f)
            val fromWeight = sin((1f - t) * theta) / sinTheta
            val targetWeight = sin(t * theta) / sinTheta
            return Quat(
                from.w * fromWeight + target.w * targetWeight,
                from.x * fromWeight + target.x * targetWeight,
                from.y * fromWeight + target.y * targetWeight,
                from.z * fromWeight + target.z * targetWeight,
            ).normalized()
        }
    }
}

private data class PhysicsDieRender(
    val position: Vec3,
    val rotation: Quat,
    val result: Int,
)

private data class PhysicsTriangle3d(val a: Vec3, val b: Vec3, val c: Vec3)

private data class PhysicsMeshFace(
    val vertices: List<Vec3>,
    val normal: Vec3,
)

private data class PhysicsMeshEdge(
    val a: Vec3,
    val b: Vec3,
    val normals: List<Vec3>,
)

private data class DiceScreenPoint(val x: Float, val y: Float, val depth: Float)

private data class PhysicsRenderEdge(
    val a: DiceScreenPoint,
    val b: DiceScreenPoint,
    val depth: Float,
)

private data class PhysicsRenderFace(
    val points: List<DiceScreenPoint>,
    val depth: Float,
    val color: Color,
)

private class DicePhysicsWorld {
    private data class Body(
        val position: Vec3,
        var velocity: Vec3,
        var rotation: Quat,
        var angularVelocity: Vec3,
        val result: Int,
        val sides: Int,
        val settleRotation: Quat,
        val restHeight: Float,
        val footprintRadius: Float,
        var settleX: Float,
        var settleZ: Float,
        val radius: Float,
    )

    private val bodies = ArrayList<Body>(6)
    private var elapsed = 0f
    private var active = false
    private var settleTargetsReady = false
    private var pendingImpactStrength = 0f

    var renderState by mutableStateOf<List<PhysicsDieRender>>(emptyList())
        private set

    fun restingFallback(faces: List<Int>, sides: Int): List<PhysicsDieRender> {
        if (faces.isEmpty()) return emptyList()
        val mesh = buildPhysicsMesh(sides)
        val restingDice = faces.mapIndexed { index, result ->
            val rotation = stableRestingRotation(mesh, index)
            Triple(result, rotation, restingFootprintRadius(mesh, rotation))
        }
        val columns = minOf(3, faces.size)
        val rows = (faces.size + columns - 1) / columns
        val spacing = restingDice.maxOf { (_, _, footprint) -> footprint } * 2f + 0.12f
        return restingDice.mapIndexed { index, (result, rotation, _) ->
            val row = index / columns
            val column = index % columns
            val itemsInRow = minOf(columns, faces.size - row * columns)
            PhysicsDieRender(
                position = Vec3(
                    (column - (itemsInRow - 1) / 2f) * spacing,
                    restingHeight(mesh, rotation),
                    (row - (rows - 1) / 2f) * spacing,
                ),
                rotation = rotation,
                result = result,
            )
        }
    }

    fun startRoll(roll: DiceRollResult) {
        bodies.clear()
        elapsed = 0f
        active = true
        settleTargetsReady = false
        pendingImpactStrength = 0f
        val mesh = buildPhysicsMesh(roll.sides)
        val meshVertices = mesh.flatMap { triangle -> listOf(triangle.a, triangle.b, triangle.c) }
        val collisionRadius = meshVertices.maxOf { vertex -> vertex.length() }
        val columns = minOf(3, roll.rolls.size)
        val rows = (roll.rolls.size + columns - 1) / columns
        val initialSpacing = collisionRadius * 2f + 0.12f
        roll.rolls.forEachIndexed { index, result ->
            val row = index / columns
            val column = index % columns
            val itemsInRow = minOf(columns, roll.rolls.size - row * columns)
            val xOffset = (column - (itemsInRow - 1) / 2f) * initialSpacing
            val zOffset = (row - (rows - 1) / 2f) * initialSpacing
            val initialRotation = Quat(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat(),
            ).normalized()
            val settleRotation = stableRestingRotation(mesh, index)
            bodies += Body(
                position = Vec3(xOffset, collisionRadius + 0.16f, zOffset),
                velocity = Vec3(
                    (Random.nextFloat() - 0.5f) * 4.8f,
                    3.6f + Random.nextFloat() * 2.2f,
                    (Random.nextFloat() - 0.5f) * 3.8f,
                ),
                rotation = initialRotation,
                angularVelocity = Vec3(
                    (Random.nextFloat() - 0.5f) * 22f,
                    (Random.nextFloat() - 0.5f) * 22f,
                    (Random.nextFloat() - 0.5f) * 22f,
                ),
                result = result,
                sides = roll.sides,
                settleRotation = settleRotation,
                restHeight = restingHeight(mesh, settleRotation),
                footprintRadius = restingFootprintRadius(mesh, settleRotation),
                settleX = xOffset,
                settleZ = zOffset,
                radius = collisionRadius,
            )
        }
        publish()
    }

    fun step(deltaSeconds: Float) {
        if (!active || bodies.isEmpty()) return
        val dt = deltaSeconds.coerceIn(0.008f, 0.025f)
        elapsed += dt
        bodies.forEach { body ->
            body.velocity.y -= 9.6f * dt
            body.position.x += body.velocity.x * dt
            body.position.y += body.velocity.y * dt
            body.position.z += body.velocity.z * dt
            body.rotation = body.rotation.integrate(body.angularVelocity, dt)
            body.angularVelocity = body.angularVelocity * 0.989f
            resolveBounds(body)
        }
        repeat(3) {
            resolveBodyCollisions()
        }
        if (elapsed > 1.45f) {
            bodies.forEach { body ->
                body.velocity = body.velocity * 0.82f
                body.angularVelocity = body.angularVelocity * 0.78f
            }
        }
        if (elapsed > 1.30f && !settleTargetsReady) {
            prepareSettleTargets()
            settleTargetsReady = true
        }
        if (elapsed > 1.34f) {
            val settleProgress = ((elapsed - 1.34f) / 0.28f).coerceIn(0f, 1f)
            bodies.forEach { body ->
                body.rotation = Quat.slerp(body.rotation, body.settleRotation, settleProgress)
                body.position.x += (body.settleX - body.position.x) * settleProgress
                body.position.y += (body.restHeight - body.position.y) * settleProgress
                body.position.z += (body.settleZ - body.position.z) * settleProgress
            }
        }
        if (elapsed > 1.62f) {
            if (!settleTargetsAreValid()) {
                prepareSettleTargets(randomize = false)
            }
            bodies.forEach { body ->
                body.position.x = body.settleX
                body.position.y = body.restHeight
                body.position.z = body.settleZ
                body.rotation = body.settleRotation
                body.velocity = Vec3(0f, 0f, 0f)
                body.angularVelocity = Vec3(0f, 0f, 0f)
            }
            active = false
        }
        publish()
    }

    fun stop() {
        active = false
        settleTargetsReady = false
        bodies.clear()
        pendingImpactStrength = 0f
        renderState = emptyList()
    }

    fun consumeImpactStrength(): Float {
        val strength = pendingImpactStrength
        pendingImpactStrength = 0f
        return strength
    }

    private fun registerImpact(strength: Float) {
        if (strength > 0.65f) {
            pendingImpactStrength = max(pendingImpactStrength, strength)
        }
    }

    private fun stableRestingRotation(mesh: List<PhysicsTriangle3d>, index: Int): Quat {
        val yaw = (index * 1.17f + Random.nextFloat() * 0.8f) * Math.PI.toFloat()
        val yawRotation = Quat(cos(yaw * 0.5f), 0f, sin(yaw * 0.5f), 0f)
        mesh.sortedByDescending { triangle ->
            val normal = (triangle.b - triangle.a).cross(triangle.c - triangle.a).normalized()
            kotlin.math.abs(normal.y)
        }.forEach { face ->
            val faceCenter = (face.a + face.b + face.c) * (1f / 3f)
            val rawNormal = (face.b - face.a).cross(face.c - face.a).normalized()
            val outwardNormal = if (rawNormal.dot(faceCenter) < 0f) rawNormal * -1f else rawNormal
            val align = Quat.fromTo(outwardNormal, Vec3(0f, -1f, 0f))
            val candidate = (yawRotation * align).normalized()
            if (hasFlatSupport(mesh, candidate)) return candidate
        }
        return Quat.identity
    }

    private fun restingHeight(mesh: List<PhysicsTriangle3d>, rotation: Quat): Float {
        val minimumY = mesh
            .flatMap { triangle -> listOf(triangle.a, triangle.b, triangle.c) }
            .minOf { vertex -> rotation.rotate(vertex).y }
        return (-minimumY).coerceAtLeast(0.02f)
    }

    private fun restingFootprintRadius(mesh: List<PhysicsTriangle3d>, rotation: Quat): Float {
        return mesh
            .flatMap { triangle -> listOf(triangle.a, triangle.b, triangle.c) }
            .maxOf { vertex ->
                val rotated = rotation.rotate(vertex)
                sqrt(rotated.x * rotated.x + rotated.z * rotated.z)
            }
    }

    private fun hasFlatSupport(mesh: List<PhysicsTriangle3d>, rotation: Quat): Boolean {
        val rotatedTriangles = mesh.map { triangle ->
            Triple(
                rotation.rotate(triangle.a),
                rotation.rotate(triangle.b),
                rotation.rotate(triangle.c),
            )
        }
        val minimumY = rotatedTriangles
            .flatMap { listOf(it.first.y, it.second.y, it.third.y) }
            .minOrNull() ?: return false
        return rotatedTriangles.any { triangle ->
            val a = triangle.first
            val b = triangle.second
            val c = triangle.third
            val normal = (b - a).cross(c - a).normalized()
            kotlin.math.abs(a.y - minimumY) < 0.0015f &&
                kotlin.math.abs(b.y - minimumY) < 0.0015f &&
                kotlin.math.abs(c.y - minimumY) < 0.0015f &&
                kotlin.math.abs(normal.y) > 0.999f
        }
    }

    /** Builds randomized table positions with a hard non-overlap guarantee. */
    private fun prepareSettleTargets(randomize: Boolean = true) {
        if (bodies.isEmpty()) return
        val xLimit = 1.47f
        val zLimit = 1.02f
        val columns = minOf(3, bodies.size)
        val rows = (bodies.size + columns - 1) / columns
        val maximumFootprint = bodies.maxOf { body -> body.footprintRadius }
        val spacing = maximumFootprint * 2f + 0.18f
        val rawTargets = bodies.indices.map { index ->
            val row = index / columns
            val column = index % columns
            val itemsInRow = minOf(columns, bodies.size - row * columns)
            Vec3(
                (column - (itemsInRow - 1) / 2f) * spacing,
                0f,
                (row - (rows - 1) / 2f) * spacing,
            )
        }
        val layoutAngle = if (randomize) (Random.nextFloat() * 2f - 1f) * 0.08f else 0f
        val angleCos = cos(layoutAngle)
        val angleSin = sin(layoutAngle)
        val rotatedTargets = rawTargets.map { target ->
            Vec3(
                target.x * angleCos - target.z * angleSin,
                0f,
                target.x * angleSin + target.z * angleCos,
            )
        }
        val maxTargetX = rotatedTargets.maxOf { target -> kotlin.math.abs(target.x) }
        val maxTargetZ = rotatedTargets.maxOf { target -> kotlin.math.abs(target.z) }
        val offsetRangeX = (xLimit - maximumFootprint - maxTargetX).coerceAtLeast(0f).coerceAtMost(0.10f)
        val offsetRangeZ = (zLimit - maximumFootprint - maxTargetZ).coerceAtLeast(0f).coerceAtMost(0.10f)
        val offsetX = if (randomize) (Random.nextFloat() * 2f - 1f) * offsetRangeX else 0f
        val offsetZ = if (randomize) (Random.nextFloat() * 2f - 1f) * offsetRangeZ else 0f
        val targets = rotatedTargets.map { target ->
            Vec3(
                target.x + offsetX,
                0f,
                target.z + offsetZ,
            )
        }.shuffled().toMutableList()

        if (randomize && bodies.size == 1) {
            val radius = bodies.first().footprintRadius
            targets[0] = Vec3(
                -xLimit + radius + Random.nextFloat() * (2f * (xLimit - radius)),
                0f,
                -zLimit + radius + Random.nextFloat() * (2f * (zLimit - radius)),
            )
        } else if (randomize) {
            repeat(320) { attempt ->
                val index = Random.nextInt(targets.size)
                val body = bodies[index]
                val current = targets[index]
                val step = if (attempt < 160) 0.22f else 0.11f
                val candidateX = current.x + (Random.nextFloat() * 2f - 1f) * step
                val candidateZ = current.z + (Random.nextFloat() * 2f - 1f) * step
                if (candidateX !in (-xLimit + body.footprintRadius)..(xLimit - body.footprintRadius) ||
                    candidateZ !in (-zLimit + body.footprintRadius)..(zLimit - body.footprintRadius)
                ) {
                    return@repeat
                }
                val canMove = targets.indices.all { otherIndex ->
                    if (otherIndex == index) return@all true
                    val other = targets[otherIndex]
                    val dx = other.x - candidateX
                    val dz = other.z - candidateZ
                    val minimumDistance = body.footprintRadius + bodies[otherIndex].footprintRadius + 0.08f
                    dx * dx + dz * dz >= minimumDistance * minimumDistance
                }
                if (canMove) {
                    targets[index] = Vec3(candidateX, 0f, candidateZ)
                }
            }
        }
        bodies.forEachIndexed { index, body ->
            val target = targets[index]
            body.settleX = target.x.coerceIn(-xLimit + body.footprintRadius, xLimit - body.footprintRadius)
            body.settleZ = target.z.coerceIn(-zLimit + body.footprintRadius, zLimit - body.footprintRadius)
        }
    }

    private fun settleTargetsAreValid(): Boolean {
        for (firstIndex in 0 until bodies.size) {
            for (secondIndex in firstIndex + 1 until bodies.size) {
                val first = bodies[firstIndex]
                val second = bodies[secondIndex]
                val dx = second.settleX - first.settleX
                val dz = second.settleZ - first.settleZ
                val minimumDistance = first.footprintRadius + second.footprintRadius + 0.06f
                if (dx * dx + dz * dz < minimumDistance * minimumDistance) return false
            }
        }
        return bodies.all { body -> hasFlatSupport(buildPhysicsMesh(body.sides), body.settleRotation) }
    }

    private fun resolveBounds(body: Body) {
        val floor = body.radius * 0.86f
        if (body.position.y < floor) {
            body.position.y = floor
            if (body.velocity.y < 0f) {
                registerImpact(-body.velocity.y)
                body.velocity.y = -body.velocity.y * 0.52f
            }
            body.velocity.x *= 0.91f
            body.velocity.z *= 0.91f
        }
        val limits = listOf(
            body.position.x to 1.47f,
            body.position.z to 1.02f,
        )
        if (body.position.x < -limits[0].second) {
            body.position.x = -limits[0].second
            registerImpact(-body.velocity.x)
            body.velocity.x = kotlin.math.abs(body.velocity.x) * 0.55f
        } else if (body.position.x > limits[0].second) {
            body.position.x = limits[0].second
            registerImpact(body.velocity.x)
            body.velocity.x = -kotlin.math.abs(body.velocity.x) * 0.55f
        }
        if (body.position.z < -limits[1].second) {
            body.position.z = -limits[1].second
            registerImpact(-body.velocity.z)
            body.velocity.z = kotlin.math.abs(body.velocity.z) * 0.55f
        } else if (body.position.z > limits[1].second) {
            body.position.z = limits[1].second
            registerImpact(body.velocity.z)
            body.velocity.z = -kotlin.math.abs(body.velocity.z) * 0.55f
        }
    }

    private fun resolveBodyCollisions() {
        for (firstIndex in 0 until bodies.size) {
            for (secondIndex in firstIndex + 1 until bodies.size) {
                val first = bodies[firstIndex]
                val second = bodies[secondIndex]
                val planarCollision = elapsed > 0.72f
                val spatialDelta = second.position - first.position
                val delta = if (planarCollision) Vec3(spatialDelta.x, 0f, spatialDelta.z) else spatialDelta
                val rawDistance = delta.length()
                val distance = rawDistance.coerceAtLeast(0.0001f)
                val minimumDistance = if (planarCollision) {
                    first.footprintRadius + second.footprintRadius + 0.04f
                } else {
                    first.radius + second.radius
                }
                if (distance >= minimumDistance) continue
                val rawNormal = if (rawDistance < 0.0001f) {
                    Vec3(if ((firstIndex + secondIndex) % 2 == 0) 1f else -1f, 0f, 0f)
                } else {
                    delta * (1f / distance)
                }
                val normal = if (planarCollision) {
                    Vec3(rawNormal.x, 0f, rawNormal.z).normalized()
                } else {
                    Vec3(rawNormal.x, rawNormal.y.coerceIn(-0.24f, 0.24f), rawNormal.z).normalized()
                }
                if (normal.length() < 0.0001f) {
                    continue
                }
                val correction = (minimumDistance - distance) * 0.51f
                first.position.x -= normal.x * correction
                first.position.y -= normal.y * correction
                first.position.z -= normal.z * correction
                second.position.x += normal.x * correction
                second.position.y += normal.y * correction
                second.position.z += normal.z * correction
                val relativeVelocity = (second.velocity - first.velocity).dot(normal)
                if (relativeVelocity < 0f) {
                    registerImpact(-relativeVelocity)
                    val impulse = -relativeVelocity * 0.62f
                    first.velocity = first.velocity - normal * impulse
                    second.velocity = second.velocity + normal * impulse
                }
            }
        }
    }

    private fun publish() {
        renderState = bodies.map { body ->
            PhysicsDieRender(
                position = body.position.copy(),
                rotation = body.rotation.copy(),
                result = body.result,
            )
        }
    }
}

private data class DiceCameraProjection(
    val position: Vec3,
    val forward: Vec3,
    val right: Vec3,
    val up: Vec3,
    val viewDirection: Vec3,
)

private val DICE_TOP_CAMERA = DiceCameraProjection(
    position = Vec3(0f, 5f, 0f),
    forward = Vec3(0f, -1f, 0f),
    right = Vec3(1f, 0f, 0f),
    up = Vec3(0f, 0f, -1f),
    viewDirection = Vec3(0f, 1f, 0f),
)

private val DICE_D4_CAMERA = DiceCameraProjection(
    position = Vec3(0f, 3.5355f, 3.5355f),
    forward = Vec3(0f, -0.7071f, -0.7071f),
    right = Vec3(1f, 0f, 0f),
    up = Vec3(0f, 0.7071f, -0.7071f),
    viewDirection = Vec3(0f, 0.7071f, 0.7071f),
)

private fun projectDicePoint(
    point: Vec3,
    canvasSize: Size,
    camera: DiceCameraProjection,
): DiceScreenPoint {
    val depth = (point - camera.position).dot(camera.forward).coerceAtLeast(1.2f)
    val scale = canvasSize.minDimension * 0.33f
    return DiceScreenPoint(
        x = canvasSize.width * 0.5f + point.dot(camera.right) * scale,
        y = canvasSize.height * 0.58f - point.dot(camera.up) * scale,
        depth = depth,
    )
}

private fun buildPhysicsMesh(sides: Int): List<PhysicsTriangle3d> {
    val radius = if (sides == 6) 0.31f * 0.90f else 0.31f * 1.10f
    return when (sides) {
        4 -> {
            val vertices = listOf(
                Vec3(1f, 1f, 1f),
                Vec3(-1f, -1f, 1f),
                Vec3(-1f, 1f, -1f),
                Vec3(1f, -1f, -1f),
            ).map { vertex -> vertex.normalized() * radius }
            listOf(
                PhysicsTriangle3d(vertices[0], vertices[1], vertices[2]),
                PhysicsTriangle3d(vertices[0], vertices[3], vertices[1]),
                PhysicsTriangle3d(vertices[0], vertices[2], vertices[3]),
                PhysicsTriangle3d(vertices[1], vertices[3], vertices[2]),
            )
        }
        6 -> cubePhysicsMesh(radius)
        8 -> octahedronPhysicsMesh(radius)
        10 -> pentagonalTrapezohedronPhysicsMesh(radius)
        12 -> dodecahedronPhysicsMesh(radius)
        20 -> icosahedronPhysicsMesh(radius)
        else -> lathePhysicsMesh(radius, 6)
    }
}

private fun buildPhysicsMeshFaces(mesh: List<PhysicsTriangle3d>): List<PhysicsMeshFace> {
    data class FaceAccumulator(
        val normal: Vec3,
        val planeDistance: Float,
        val vertices: MutableList<Vec3>,
    )

    fun samePoint(first: Vec3, second: Vec3): Boolean {
        return kotlin.math.abs(first.x - second.x) < 0.0001f &&
            kotlin.math.abs(first.y - second.y) < 0.0001f &&
            kotlin.math.abs(first.z - second.z) < 0.0001f
    }

    val faces = mutableListOf<FaceAccumulator>()
    mesh.forEach { triangle ->
        val center = (triangle.a + triangle.b + triangle.c) * (1f / 3f)
        val rawNormal = (triangle.b - triangle.a).cross(triangle.c - triangle.a).normalized()
        val normal = if (rawNormal.dot(center) < 0f) rawNormal * -1f else rawNormal
        val planeDistance = normal.dot(center)
        val face = faces.firstOrNull { candidate ->
            candidate.normal.dot(normal) > 0.9995f &&
                kotlin.math.abs(candidate.planeDistance - planeDistance) < 0.001f
        } ?: FaceAccumulator(normal, planeDistance, mutableListOf()).also { faces += it }
        listOf(triangle.a, triangle.b, triangle.c).forEach { vertex ->
            if (face.vertices.none { existing -> samePoint(existing, vertex) }) {
                face.vertices += vertex
            }
        }
    }
    return faces.map { face ->
        val center = face.vertices.reduce { total, vertex -> total + vertex } * (1f / face.vertices.size)
        val reference = (face.vertices.first() - center).normalized()
        val tangent = face.normal.cross(reference).normalized()
        PhysicsMeshFace(
            vertices = face.vertices.sortedBy { vertex ->
                val delta = vertex - center
                atan2(delta.dot(tangent), delta.dot(reference))
            },
            normal = face.normal,
        )
    }
}

private fun buildPhysicsMeshEdges(mesh: List<PhysicsTriangle3d>): List<PhysicsMeshEdge> {
    data class EdgeAccumulator(
        val a: Vec3,
        val b: Vec3,
        val normals: MutableList<Vec3>,
    )

    fun samePoint(first: Vec3, second: Vec3): Boolean {
        return kotlin.math.abs(first.x - second.x) < 0.0001f &&
            kotlin.math.abs(first.y - second.y) < 0.0001f &&
            kotlin.math.abs(first.z - second.z) < 0.0001f
    }

    val edges = mutableListOf<EdgeAccumulator>()
    mesh.forEach { triangle ->
        val center = (triangle.a + triangle.b + triangle.c) * (1f / 3f)
        val rawNormal = (triangle.b - triangle.a).cross(triangle.c - triangle.a).normalized()
        val normal = if (rawNormal.dot(center) < 0f) rawNormal * -1f else rawNormal
        listOf(
            triangle.a to triangle.b,
            triangle.b to triangle.c,
            triangle.c to triangle.a,
        ).forEach { (a, b) ->
            val existing = edges.firstOrNull { edge ->
                (samePoint(edge.a, a) && samePoint(edge.b, b)) ||
                    (samePoint(edge.a, b) && samePoint(edge.b, a))
            }
            if (existing == null) {
                edges += EdgeAccumulator(a, b, mutableListOf(normal))
            } else {
                existing.normals += normal
            }
        }
    }
    return edges
        .filter { edge ->
            edge.normals.size == 1 || edge.normals.any { first ->
                edge.normals.any { second -> first.dot(second) < 0.985f }
            }
        }
        .map { edge -> PhysicsMeshEdge(edge.a, edge.b, edge.normals.toList()) }
}

private fun cubePhysicsMesh(radius: Float): List<PhysicsTriangle3d> {
    val vertices = listOf(
        Vec3(-radius, -radius, -radius), Vec3(radius, -radius, -radius),
        Vec3(radius, radius, -radius), Vec3(-radius, radius, -radius),
        Vec3(-radius, -radius, radius), Vec3(radius, -radius, radius),
        Vec3(radius, radius, radius), Vec3(-radius, radius, radius),
    )
    val quads = listOf(
        intArrayOf(0, 1, 2, 3), intArrayOf(4, 7, 6, 5),
        intArrayOf(0, 4, 5, 1), intArrayOf(1, 5, 6, 2),
        intArrayOf(2, 6, 7, 3), intArrayOf(4, 0, 3, 7),
    )
    return quads.flatMap { quad ->
        listOf(
            PhysicsTriangle3d(vertices[quad[0]], vertices[quad[1]], vertices[quad[2]]),
            PhysicsTriangle3d(vertices[quad[0]], vertices[quad[2]], vertices[quad[3]]),
        )
    }
}

private fun octahedronPhysicsMesh(radius: Float): List<PhysicsTriangle3d> {
    val top = Vec3(0f, radius, 0f)
    val bottom = Vec3(0f, -radius, 0f)
    val ring = listOf(
        Vec3(radius, 0f, 0f), Vec3(0f, 0f, radius),
        Vec3(-radius, 0f, 0f), Vec3(0f, 0f, -radius),
    )
    return ring.indices.flatMap { index ->
        val next = ring[(index + 1) % ring.size]
        listOf(
            PhysicsTriangle3d(top, ring[index], next),
            PhysicsTriangle3d(bottom, next, ring[index]),
        )
    }
}

private fun pentagonalTrapezohedronPhysicsMesh(radius: Float): List<PhysicsTriangle3d> {
    val segments = 5
    val top = Vec3(0f, radius, 0f)
    val bottom = Vec3(0f, -radius, 0f)
    val halfStep = Math.PI.toFloat() / segments
    val halfStepCosine = cos(halfStep)
    val ringHeight = radius * (1f - halfStepCosine) / (1f + halfStepCosine)
    val ringRadius = radius * 0.92f
    val ring = List(segments * 2) { index ->
        val angle = Math.PI.toFloat() * index / segments
        Vec3(
            cos(angle) * ringRadius,
            if (index % 2 == 0) -ringHeight else ringHeight,
            sin(angle) * ringRadius,
        )
    }
    val faces = mutableListOf<PhysicsTriangle3d>()
    repeat(segments) { index ->
        val lowerRingIndex = index * 2
        val upperLeft = ring[(lowerRingIndex - 1 + ring.size) % ring.size]
        val upperCenter = ring[lowerRingIndex]
        val upperRight = ring[(lowerRingIndex + 1) % ring.size]
        faces += PhysicsTriangle3d(top, upperLeft, upperCenter)
        faces += PhysicsTriangle3d(top, upperCenter, upperRight)

        val upperRingIndex = lowerRingIndex + 1
        val lowerLeft = ring[(upperRingIndex - 1 + ring.size) % ring.size]
        val lowerCenter = ring[upperRingIndex]
        val lowerRight = ring[(upperRingIndex + 1) % ring.size]
        faces += PhysicsTriangle3d(bottom, lowerLeft, lowerCenter)
        faces += PhysicsTriangle3d(bottom, lowerCenter, lowerRight)
    }
    return faces
}

private fun dodecahedronPhysicsMesh(radius: Float): List<PhysicsTriangle3d> {
    val phi = (1f + sqrt(5f)) / 2f
    val inversePhi = 1f / phi
    val vertices = listOf(
        Vec3(-1f, -1f, -1f), Vec3(-1f, -1f, 1f),
        Vec3(-1f, 1f, -1f), Vec3(-1f, 1f, 1f),
        Vec3(1f, -1f, -1f), Vec3(1f, -1f, 1f),
        Vec3(1f, 1f, -1f), Vec3(1f, 1f, 1f),
        Vec3(0f, -inversePhi, -phi), Vec3(0f, -inversePhi, phi),
        Vec3(0f, inversePhi, -phi), Vec3(0f, inversePhi, phi),
        Vec3(-inversePhi, -phi, 0f), Vec3(-inversePhi, phi, 0f),
        Vec3(inversePhi, -phi, 0f), Vec3(inversePhi, phi, 0f),
        Vec3(-phi, 0f, -inversePhi), Vec3(-phi, 0f, inversePhi),
        Vec3(phi, 0f, -inversePhi), Vec3(phi, 0f, inversePhi),
    ).map { vertex -> vertex.normalized() * radius }
    val pentagons = listOf(
        intArrayOf(0, 1, 12, 16, 17),
        intArrayOf(0, 2, 8, 10, 16),
        intArrayOf(0, 4, 8, 12, 14),
        intArrayOf(1, 3, 9, 11, 17),
        intArrayOf(1, 5, 9, 12, 14),
        intArrayOf(2, 3, 13, 16, 17),
        intArrayOf(2, 6, 10, 13, 15),
        intArrayOf(3, 7, 11, 13, 15),
        intArrayOf(4, 5, 14, 18, 19),
        intArrayOf(4, 6, 8, 10, 18),
        intArrayOf(5, 7, 9, 11, 19),
        intArrayOf(6, 7, 15, 18, 19),
    )
    return pentagons.flatMap { face ->
        val center = face
            .map { index -> vertices[index] }
            .reduce { total, vertex -> total + vertex } * (1f / face.size)
        val outward = center.normalized()
        val reference = (vertices[face.first()] - center).normalized()
        val tangent = outward.cross(reference).normalized()
        val ordered = face.sortedBy { index ->
            val delta = vertices[index] - center
            atan2(delta.dot(tangent), delta.dot(reference))
        }
        (1 until ordered.lastIndex).map { index ->
            PhysicsTriangle3d(
                vertices[ordered[0]],
                vertices[ordered[index]],
                vertices[ordered[index + 1]],
            )
        }
    }
}

private fun lathePhysicsMesh(radius: Float, segments: Int): List<PhysicsTriangle3d> {
    val top = Vec3(0f, radius, 0f)
    val bottom = Vec3(0f, -radius, 0f)
    val upper = List(segments) { index ->
        val angle = 2f * Math.PI.toFloat() * index / segments
        Vec3(kotlin.math.cos(angle) * radius * 0.9f, radius * 0.35f, kotlin.math.sin(angle) * radius * 0.9f)
    }
    val lower = upper.map { Vec3(it.x, -it.y, it.z) }
    return upper.indices.flatMap { index ->
        val next = (index + 1) % segments
        listOf(
            PhysicsTriangle3d(top, upper[index], upper[next]),
            PhysicsTriangle3d(upper[index], lower[index], upper[next]),
            PhysicsTriangle3d(upper[next], lower[index], lower[next]),
            PhysicsTriangle3d(bottom, lower[next], lower[index]),
        )
    }
}

private fun icosahedronPhysicsMesh(radius: Float): List<PhysicsTriangle3d> {
    val phi = (1f + sqrt(5f)) / 2f
    val raw = listOf(
        Vec3(-1f, phi, 0f), Vec3(1f, phi, 0f), Vec3(-1f, -phi, 0f), Vec3(1f, -phi, 0f),
        Vec3(0f, -1f, phi), Vec3(0f, 1f, phi), Vec3(0f, -1f, -phi), Vec3(0f, 1f, -phi),
        Vec3(phi, 0f, -1f), Vec3(phi, 0f, 1f), Vec3(-phi, 0f, -1f), Vec3(-phi, 0f, 1f),
    )
    val vertices = raw.map { it.normalized() * radius }
    val faces = listOf(
        intArrayOf(0, 11, 5), intArrayOf(0, 5, 1), intArrayOf(0, 1, 7), intArrayOf(0, 7, 10), intArrayOf(0, 10, 11),
        intArrayOf(1, 5, 9), intArrayOf(5, 11, 4), intArrayOf(11, 10, 2), intArrayOf(10, 7, 6), intArrayOf(7, 1, 8),
        intArrayOf(3, 9, 4), intArrayOf(3, 4, 2), intArrayOf(3, 2, 6), intArrayOf(3, 6, 8), intArrayOf(3, 8, 9),
        intArrayOf(4, 9, 5), intArrayOf(2, 4, 11), intArrayOf(6, 2, 10), intArrayOf(8, 6, 7), intArrayOf(9, 8, 1),
    )
    return faces.map { face -> PhysicsTriangle3d(vertices[face[0]], vertices[face[1]], vertices[face[2]]) }
}

private fun diceColorForSides(sides: Int, accent: Color): Color = when (sides) {
    4 -> Color(0xFF23A84A)
    6 -> Color(0xFF2AB7C9)
    8 -> Color(0xFF8738D9)
    10 -> Color(0xFFE23A8C)
    12 -> Color(0xFFE33327)
    20 -> Color(0xFFFF7400)
    else -> accent
}

@Composable
private fun DiceVisual(
    face: Int,
    sides: Int,
    rotation: Float,
    bounce: Float,
    rolling: Boolean,
    modifier: Modifier = Modifier.size(160.dp),
) {
    val C = LocalFortunePalette.current
    val tilt = if (rolling) sin(rotation / 18f) * 7f else sin(rotation / 42f) * 2.5f
    val shapeColor = diceShapeColor(sides, accent = C.gold)
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    translationY = -18f * bounce,
                    rotationZ = tilt,
                    scaleX = if (rolling) 0.98f + bounce * 0.02f else 1f,
                    scaleY = if (rolling) 0.98f + bounce * 0.02f else 1f,
                ),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val minSide = size.minDimension
                val dieSide = minSide * 0.72f
                val centerPoint = Offset(center.x, center.y - minSide * 0.02f)
                val shadowWidth = dieSide * 0.76f
                drawOval(
                    color = Color(0x55000000),
                    topLeft = Offset(center.x - shadowWidth / 2f, center.y + dieSide * 0.36f),
                    size = Size(shadowWidth, dieSide * 0.14f),
                )
                drawColoredDieShape(sides = sides, side = dieSide, centerPoint = centerPoint, color = shapeColor)
            }
            Text(
                face.toString(),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun diceShapeColor(sides: Int, accent: Color): Color {
    return when (sides) {
        4 -> Color(0xFF23A84A)
        6 -> Color(0xFF2AB7C9)
        8 -> Color(0xFF8738D9)
        10 -> Color(0xFFE23A8C)
        12 -> Color(0xFFE33327)
        20 -> Color(0xFFFF7400)
        else -> accent
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawColoredDieShape(
    sides: Int,
    side: Float,
    centerPoint: Offset,
    color: Color,
) {
    when (sides) {
        4 -> {
            val points = listOf(
                Offset(centerPoint.x, centerPoint.y - side * 0.52f),
                Offset(centerPoint.x - side * 0.5f, centerPoint.y + side * 0.42f),
                Offset(centerPoint.x + side * 0.5f, centerPoint.y + side * 0.42f),
            )
            drawPolygon(points, color)
        }
        6 -> {
            val dieSide = side * 0.86f
            drawRoundRect(
                color = color,
                topLeft = Offset(centerPoint.x - dieSide / 2f, centerPoint.y - dieSide / 2f),
                size = Size(dieSide, dieSide),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(side * 0.02f, side * 0.02f),
            )
        }
        8 -> {
            drawPolygon(
                listOf(
                    Offset(centerPoint.x, centerPoint.y - side * 0.55f),
                    Offset(centerPoint.x + side * 0.43f, centerPoint.y - side * 0.25f),
                    Offset(centerPoint.x + side * 0.47f, centerPoint.y + side * 0.34f),
                    Offset(centerPoint.x, centerPoint.y + side * 0.58f),
                    Offset(centerPoint.x - side * 0.47f, centerPoint.y + side * 0.34f),
                    Offset(centerPoint.x - side * 0.43f, centerPoint.y - side * 0.25f),
                ),
                color,
            )
        }
        10 -> {
            drawPolygon(
                listOf(
                    Offset(centerPoint.x, centerPoint.y - side * 0.55f),
                    Offset(centerPoint.x + side * 0.42f, centerPoint.y - side * 0.25f),
                    Offset(centerPoint.x + side * 0.42f, centerPoint.y + side * 0.28f),
                    Offset(centerPoint.x, centerPoint.y + side * 0.56f),
                    Offset(centerPoint.x - side * 0.42f, centerPoint.y + side * 0.28f),
                    Offset(centerPoint.x - side * 0.42f, centerPoint.y - side * 0.25f),
                ),
                color,
            )
        }
        12 -> drawPolygon(polygonPoints(centerPoint, side * 0.5f, 10, -82f), color)
        else -> drawPolygon(polygonPoints(centerPoint, side * 0.52f, 6, -90f), color)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPolygon(points: List<Offset>, color: Color) {
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    drawPath(path, color)
    drawPath(path, Color(0x22FFFFFF), style = Stroke(width = 1.2.dp.toPx()))
}

private fun polygonPoints(center: Offset, radius: Float, count: Int, startDegrees: Float): List<Offset> {
    return List(count) { index ->
        val angle = Math.toRadians((startDegrees + 360f * index / count).toDouble())
        Offset(
            x = center.x + cos(angle).toFloat() * radius,
            y = center.y + sin(angle).toFloat() * radius,
        )
    }
}

private data class DiceMesh(
    val vertices: List<MeshVertex>,
    val triangles: List<MeshTriangle>,
    val materials: List<Color>,
)

private data class MeshVertex(val x: Float, val y: Float, val z: Float)
private data class MeshPoint(val x: Float, val y: Float, val z: Float)
private data class MeshTriangle(val a: Int, val b: Int, val c: Int, val material: Int)
private data class RenderTriangle(
    val a: MeshPoint,
    val b: MeshPoint,
    val c: MeshPoint,
    val z: Float,
    val color: Color,
)

private fun loadDiceMesh(context: Context): DiceMesh? {
    return try {
        val json = context.assets.open("dice_mesh.json").bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        val verticesJson = root.getJSONArray("vertices")
        val trianglesJson = root.getJSONArray("triangles")
        val materialsJson = root.getJSONArray("materials")
        val vertices = List(verticesJson.length()) { index ->
            val item = verticesJson.getJSONArray(index)
            MeshVertex(
                x = item.getDouble(0).toFloat(),
                y = item.getDouble(1).toFloat(),
                z = item.getDouble(2).toFloat(),
            )
        }
        val triangles = List(trianglesJson.length()) { index ->
            val item = trianglesJson.getJSONArray(index)
            MeshTriangle(
                a = item.getInt(0),
                b = item.getInt(1),
                c = item.getInt(2),
                material = item.getInt(3),
            )
        }
        val materials = List(materialsJson.length()) { index ->
            Color(android.graphics.Color.parseColor(materialsJson.getString(index)))
        }
        DiceMesh(vertices = vertices, triangles = triangles, materials = materials)
    } catch (_: Throwable) {
        null
    }
}

private fun shadeColor(color: Color, shade: Float): Color {
    return Color(
        red = (color.red * shade).coerceIn(0f, 1f),
        green = (color.green * shade).coerceIn(0f, 1f),
        blue = (color.blue * shade).coerceIn(0f, 1f),
        alpha = color.alpha,
    )
}

private enum class SchedulePage {
    Calendar,
    Editor,
    Memorial,
    Detail,
}

@Composable
private fun ScheduleScreen(vm: FortuneViewModel) {
    val C = LocalFortunePalette.current
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    var detailDateText by rememberSaveable { mutableStateOf<String?>(null) }
    var pageName by rememberSaveable { mutableStateOf(SchedulePage.Calendar.name) }
    var editorReturnPageName by rememberSaveable { mutableStateOf(SchedulePage.Calendar.name) }
    var activeItemId by rememberSaveable { mutableStateOf<Long?>(null) }
    val page = runCatching { SchedulePage.valueOf(pageName) }.getOrDefault(SchedulePage.Calendar)
    val editorReturnPage = runCatching { SchedulePage.valueOf(editorReturnPageName) }
        .getOrDefault(SchedulePage.Calendar)
    val detailDate = remember(detailDateText) {
        detailDateText?.let { value -> runCatching { LocalDate.parse(value) }.getOrNull() }
    }
    val activeItem = remember(vm.scheduleItems, activeItemId) {
        activeItemId?.let { id -> vm.scheduleItems.firstOrNull { it.id == id } }
    }

    BackHandler(enabled = detailDate != null || page != SchedulePage.Calendar) {
        when {
            detailDate != null -> detailDateText = null
            page == SchedulePage.Editor -> pageName = editorReturnPage.name
            page == SchedulePage.Detail -> pageName = SchedulePage.Memorial.name
            page == SchedulePage.Memorial -> pageName = SchedulePage.Calendar.name
        }
    }
    if (detailDate != null) {
        AlmanacDateDetailScreen(
            date = detailDate,
            scheduleItems = vm.scheduleItems.filter { it.date == detailDate.toString() },
            onBack = { detailDateText = null },
            onToggleSchedule = vm::toggleScheduleItem,
            onDeleteSchedule = vm::deleteScheduleItem,
        )
        return
    }

    when (page) {
        SchedulePage.Editor -> {
            ScheduleEditorScreen(
                initialDate = activeItem?.let(::scheduleStartDate) ?: selectedDate,
                existingItem = activeItem,
                allItems = vm.scheduleItems,
                onCancel = { pageName = editorReturnPage.name },
                onSave = { draft ->
                    val saved = vm.saveScheduleDraft(draft, activeItem?.id)
                    selectedDate = runCatching { LocalDate.parse(saved.date) }.getOrDefault(selectedDate)
                    activeItemId = saved.id
                    pageName = editorReturnPage.name
                },
            )
            return
        }
        SchedulePage.Memorial -> {
            ScheduleMemorialScreen(
                items = vm.scheduleItems,
                onBack = { pageName = SchedulePage.Calendar.name },
                onItemClick = { item ->
                    activeItemId = item.id
                    pageName = SchedulePage.Detail.name
                },
            )
            return
        }
        SchedulePage.Detail -> {
            if (activeItem == null) {
                LaunchedEffect(activeItemId) { pageName = SchedulePage.Memorial.name }
            } else {
                ScheduleDetailScreen(
                    item = activeItem,
                    onBack = { pageName = SchedulePage.Memorial.name },
                    onEdit = {
                        editorReturnPageName = SchedulePage.Detail.name
                        pageName = SchedulePage.Editor.name
                    },
                    onToggleDone = { vm.toggleScheduleItem(activeItem.id) },
                    onDelete = {
                        vm.deleteScheduleItem(activeItem.id)
                        activeItemId = null
                        pageName = SchedulePage.Memorial.name
                    },
                )
            }
            return
        }
        SchedulePage.Calendar -> Unit
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CalendarPanel(
            scheduleItems = vm.scheduleItems,
            selectedDate = selectedDate,
            onSelectedDateChange = { selectedDate = it },
            onOpenDateDetail = {
                selectedDate = it
                detailDateText = it.toString()
            },
            onAddSchedule = {
                activeItemId = null
                editorReturnPageName = SchedulePage.Calendar.name
                pageName = SchedulePage.Editor.name
            },
        )

        ScheduleMemorialEntryCard(itemCount = vm.scheduleItems.size) {
            pageName = SchedulePage.Memorial.name
        }
    }
}

@Composable
private fun CalendarPanel(
    scheduleItems: List<ScheduleItem>,
    selectedDate: LocalDate,
    onSelectedDateChange: (LocalDate) -> Unit,
    onOpenDateDetail: (LocalDate) -> Unit,
    onAddSchedule: () -> Unit,
) {
    val C = LocalFortunePalette.current
    val today = remember { LocalDate.now() }
    val animationScope = rememberCoroutineScope()
    var visibleMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var picker by remember { mutableStateOf<CalendarPicker?>(null) }
    val centerPage = 6000
    val pagerState = rememberPagerState(initialPage = centerPage) { 12001 }
    val scheduledDates = remember(scheduleItems) { scheduleItems.mapTo(hashSetOf()) { it.date } }
    val todayHeader = remember(today) { formatFullCalendarDate(today) }
    val selectedScheduleItems = remember(scheduleItems, selectedDate) {
        scheduleItems.filter { it.date == selectedDate.toString() }
    }
    val yearTransition = remember { Animatable(1f) }
    var yearTransitionDirection by remember { mutableStateOf(1f) }
    var calendarTransitionAxis by remember { mutableStateOf(CalendarDragAxis.Vertical) }
    val yearTransitionDistance = with(LocalDensity.current) { 12.dp.toPx() }
    val calendarSwipeThreshold = with(LocalDensity.current) { 88.dp.toPx() }
    // 进页时预计算当月及前后各两月，翻页大概率命中缓存。
    LaunchedEffect(Unit) {
        listOf(0, -1, 1, -2, 2).forEach { delta ->
            prefetchMonthInfo(YearMonth.from(today).plusMonths(delta.toLong()))
        }
    }
    // 翻页后，预计算新当月前后各两月，保持缓存领先于滑动。
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val base = YearMonth.from(today).plusMonths((page - centerPage).toLong())
            listOf(-1, 1, -2, 2).forEach { delta ->
                prefetchMonthInfo(base.plusMonths(delta.toLong()))
            }
        }
    }
    val selectedInfo = remember(selectedDate) { calendarDateInfo(selectedDate) }
    val showToday = visibleMonth != YearMonth.from(today) || selectedDate != today
    val calendarGridHeight = remember(visibleMonth) {
        (20 + calendarRowCount(visibleMonth) * 62).dp
    }

    suspend fun moveToPage(
        targetPage: Int,
        animateAdjacent: Boolean,
        transitionAxis: CalendarDragAxis = CalendarDragAxis.Vertical,
    ) {
        val safeTarget = targetPage.coerceIn(0, 12000)
        val pageDistance = kotlin.math.abs(safeTarget - pagerState.currentPage)
        if (pageDistance == 0) return
        if (animateAdjacent && pageDistance == 1) {
            pagerState.animateScrollToPage(safeTarget)
            return
        }
        calendarTransitionAxis = transitionAxis
        yearTransitionDirection = if (safeTarget > pagerState.currentPage) -1f else 1f
        yearTransition.snapTo(1f)
        yearTransition.animateTo(0f, tween(durationMillis = 75, easing = FastOutSlowInEasing))
        pagerState.scrollToPage(safeTarget)
        yearTransition.animateTo(1f, tween(durationMillis = 145, easing = FastOutSlowInEasing))
    }

    fun showMonth(month: YearMonth) {
        val safeYear = month.year.coerceIn(CALENDAR_MIN_YEAR, CALENDAR_MAX_YEAR)
        val safeMonth = YearMonth.of(safeYear, month.monthValue)
        visibleMonth = safeMonth
        onSelectedDateChange(safeMonth.atDay(selectedDate.dayOfMonth.coerceAtMost(safeMonth.lengthOfMonth())))
        val monthsBetween = ChronoUnit.MONTHS.between(YearMonth.from(today), safeMonth)
        val targetPage = (centerPage + monthsBetween).toInt().coerceIn(0, 12000)
        prefetchMonthInfo(safeMonth)
        animationScope.launch { moveToPage(targetPage, animateAdjacent = true) }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = C.panel),
        border = BorderStroke(1.dp, C.line),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "今天",
                        color = C.textSub,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.align(Alignment.CenterStart),
                    )
                    TextButton(
                        onClick = onAddSchedule,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 8.dp),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = C.gold)
                        Spacer(Modifier.width(4.dp))
                        Text("新增日程", color = C.gold, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(
                    todayHeader,
                    color = C.textMain,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { picker = CalendarPicker.Year }) {
                        Text(
                            "${visibleMonth.year}年",
                            color = C.gold,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(onClick = { picker = CalendarPicker.Month }) {
                        Text(
                            "${visibleMonth.monthValue}月",
                            color = C.gold,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                if (showToday) {
                    TextButton(onClick = {
                        onSelectedDateChange(today)
                        animationScope.launch {
                            moveToPage(centerPage, animateAdjacent = true)
                        }
                    }, modifier = Modifier.align(Alignment.CenterEnd)) { Text("今日") }
                }
            }
            LaunchedEffect(pagerState.currentPage) {
                val month = YearMonth.from(today).plusMonths((pagerState.currentPage - centerPage).toLong())
                if (month != visibleMonth) {
                    visibleMonth = month
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(calendarGridHeight)
                    .clipToBounds()
                    .graphicsLayer {
                        alpha = 0.68f + yearTransition.value * 0.32f
                        val transitionOffset =
                            (1f - yearTransition.value) * yearTransitionDistance * yearTransitionDirection
                        translationX = if (calendarTransitionAxis == CalendarDragAxis.Horizontal) {
                            transitionOffset
                        } else {
                            0f
                        }
                        translationY = if (calendarTransitionAxis == CalendarDragAxis.Vertical) {
                            transitionOffset
                        } else {
                            0f
                        }
                    }
                    .pointerInput(pagerState, calendarSwipeThreshold) {
                        awaitEachGesture {
                            val down = awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial,
                            )
                            var dragAxis: CalendarDragAxis? = null
                            var dragX = 0f
                            var dragY = 0f
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                dragX = change.position.x - down.position.x
                                dragY = change.position.y - down.position.y
                                if (dragAxis == null) {
                                    val distance = sqrt(dragX * dragX + dragY * dragY)
                                    if (distance > viewConfiguration.touchSlop) {
                                        dragAxis = if (kotlin.math.abs(dragY) > kotlin.math.abs(dragX) * 1.15f) {
                                            CalendarDragAxis.Vertical
                                        } else {
                                            CalendarDragAxis.Horizontal
                                        }
                                    }
                                }
                                if (dragAxis != null) {
                                    change.consume()
                                }
                                if (!change.pressed) break
                            }
                            when {
                                dragAxis == CalendarDragAxis.Vertical &&
                                    kotlin.math.abs(dragY) > calendarSwipeThreshold -> {
                                    val delta = if (dragY < 0f) 12 else -12
                                    val targetPage = (pagerState.currentPage + delta).coerceIn(0, 12000)
                                    val targetMonth = YearMonth.from(today)
                                        .plusMonths((targetPage - centerPage).toLong())
                                    prefetchMonthInfo(targetMonth)
                                    animationScope.launch {
                                        moveToPage(
                                            targetPage = targetPage,
                                            animateAdjacent = false,
                                            transitionAxis = CalendarDragAxis.Vertical,
                                        )
                                    }
                                }
                                dragAxis == CalendarDragAxis.Horizontal &&
                                    kotlin.math.abs(dragX) > calendarSwipeThreshold -> {
                                    val delta = if (dragX < 0f) 1 else -1
                                    val targetPage = (pagerState.currentPage + delta).coerceIn(0, 12000)
                                    val targetMonth = YearMonth.from(today)
                                        .plusMonths((targetPage - centerPage).toLong())
                                    prefetchMonthInfo(targetMonth)
                                    animationScope.launch {
                                        moveToPage(
                                            targetPage = targetPage,
                                            animateAdjacent = false,
                                            transitionAxis = CalendarDragAxis.Horizontal,
                                        )
                                    }
                                }
                            }
                        }
                    },
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 0,
                    userScrollEnabled = false,
                ) { page ->
                    val month = YearMonth.from(today).plusMonths((page - centerPage).toLong())
                    CalendarMonthGrid(
                        month = month,
                        selectedDate = selectedDate,
                        today = today,
                        scheduledDates = scheduledDates,
                        onSelectedDateChange = {
                            visibleMonth = YearMonth.from(it)
                            onSelectedDateChange(it)
                        },
                        onOpenDateDetail = onOpenDateDetail,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            DateDetail(info = selectedInfo, scheduleItems = selectedScheduleItems)
        }
    }

    when (picker) {
        CalendarPicker.Year -> CalendarValuePicker(
            title = "选择年份",
            values = (CALENDAR_MIN_YEAR..CALENDAR_MAX_YEAR).toList(),
            selected = visibleMonth.year,
            label = { "${it}年" },
            onDismiss = { picker = null },
            onSelect = {
                showMonth(YearMonth.of(it, visibleMonth.monthValue))
                picker = null
            },
        )
        CalendarPicker.Month -> CalendarValuePicker(
            title = "选择月份",
            values = (1..12).toList(),
            selected = visibleMonth.monthValue,
            label = { "${it}月" },
            onDismiss = { picker = null },
            onSelect = {
                showMonth(YearMonth.of(visibleMonth.year, it))
                picker = null
            },
        )
        null -> Unit
    }
}

private val weekLabels = listOf("日", "一", "二", "三", "四", "五", "六")

@Composable
private fun CalendarMonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    scheduledDates: Set<String>,
    onSelectedDateChange: (LocalDate) -> Unit,
    onOpenDateDetail: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val C = LocalFortunePalette.current
    val initialDays = remember(month) { monthInfoCache.get(month) }
    val days by produceState<List<CalendarDateInfo>?>(initialValue = initialDays, month) {
        if (value == null) {
            value = withContext(calendarComputationDispatcher) { computeMonthInfo(month) }
        }
    }
    val placeholders = remember(month) {
        List(month.lengthOfMonth()) { index ->
            CalendarDateInfo(month.atDay(index + 1), "", "", "", "", "", "", emptyList())
        }
    }
    val displayDays = (days ?: placeholders).filter { info -> YearMonth.from(info.date) == month }
    val leadingEmptyCells = month.atDay(1).dayOfWeek.value % 7
    val totalCells = leadingEmptyCells + displayDays.size
    val trailingEmptyCells = (7 - totalCells % 7) % 7
    val gridCells = buildList<CalendarDateInfo?> {
        repeat(leadingEmptyCells) { add(null) }
        addAll(displayDays)
        repeat(trailingEmptyCells) { add(null) }
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            weekLabels.forEach { label ->
                Text(
                    label,
                    color = C.textSub,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        gridCells.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { info ->
                    if (info == null) {
                        Spacer(Modifier.weight(1f).height(58.dp))
                    } else {
                        CalendarDayCell(
                            info = info,
                            visibleMonth = month,
                            selectedDate = selectedDate,
                            today = today,
                            hasSchedule = info.date.toString() in scheduledDates,
                            onClick = { onSelectedDateChange(info.date) },
                            onDoubleClick = {
                                onSelectedDateChange(info.date)
                                onOpenDateDetail(info.date)
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CalendarDayCell(
    info: CalendarDateInfo,
    visibleMonth: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    hasSchedule: Boolean,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val C = LocalFortunePalette.current
    val date = info.date
    val inMonth = date.monthValue == visibleMonth.monthValue
    val isToday = date == today
    val isSelected = date == selectedDate
    val bgColor = when {
        isSelected -> C.gold.copy(alpha = 0.18f)
        isToday -> C.mint.copy(alpha = 0.14f)
        else -> Color.Transparent
    }
    val borderColor = when {
        isSelected -> C.gold
        isToday -> C.mint
        else -> Color.Transparent
    }
    Box(
        modifier = modifier
            .height(58.dp)
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onDoubleClick = onDoubleClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 6.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                date.dayOfMonth.toString(),
                color = when {
                    !inMonth -> C.textSub.copy(alpha = 0.42f)
                    isSelected -> C.gold
                    else -> C.textMain
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                info.cellLabel,
                color = when {
                    !inMonth -> C.textSub.copy(alpha = 0.36f)
                    info.solarTerm.isNotBlank() || info.traditionalFestivals.isNotEmpty() -> C.gold
                    else -> C.textSub
                },
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (hasSchedule) {
                Box(Modifier.size(3.dp).background(C.mint, CircleShape))
            }
        }
    }
}

@Composable
private fun DateDetail(info: CalendarDateInfo, scheduleItems: List<ScheduleItem>) {
    val C = LocalFortunePalette.current
    Surface(color = C.panelAlt, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "${info.date.monthValue}月${info.date.dayOfMonth}日 周${info.weekday}",
                color = C.textMain,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text("公历 ${info.date.year}年 · 当年第${info.date.dayOfYear}天", color = C.textSub)
            Text(
                "农历 ${info.ganZhiYear}年 · 生肖${info.zodiac} · ${info.lunarMonth}${info.lunarDay}",
                color = C.textSub,
            )
            if (info.solarTerm.isNotBlank()) {
                CalendarDetailRow(label = "节气", value = info.solarTerm, accent = C.gold)
            }
            if (info.traditionalFestivals.isNotEmpty()) {
                CalendarDetailRow(label = "传统节日", value = info.traditionalFestivals.joinToString("、"), accent = C.rose)
            }
            CalendarDetailRow(
                label = "日程",
                value = if (scheduleItems.isEmpty()) "暂无事项" else scheduleItems.joinToString(" · ") { it.title },
                accent = C.mint,
            )
        }
    }
}

@Composable
private fun AlmanacDateDetailScreen(
    date: LocalDate,
    scheduleItems: List<ScheduleItem>,
    onBack: () -> Unit,
    onToggleSchedule: (Long) -> Unit,
    onDeleteSchedule: (Long) -> Unit,
) {
    val C = LocalFortunePalette.current
    val cachedDetail = remember(date) { almanacDayDetailCache.get(date) }
    val detail by produceState<AlmanacDayDetail?>(initialValue = cachedDetail, date) {
        if (value == null) {
            value = withContext(calendarComputationDispatcher) {
                almanacDayDetailCache.get(date) ?: AlmanacDayDetailEngine.create(date).also {
                    almanacDayDetailCache.put(date, it)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SubScreenHeader(title = "日期详情", onBack = onBack) {}
        val info = detail
        if (info == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("正在计算当日历法信息…", color = C.textSub)
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "date-header") {
                Column(
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        "${info.date.year}年${info.date.monthValue}月${info.date.dayOfMonth}日",
                        color = C.textMain,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "周${info.weekday} · 农历${info.lunarMonth}${info.lunarDay} · ${info.constellation}座",
                        color = C.textSub,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            item(key = "calendar-basics") {
                AlmanacSectionCard(title = "公历与农历") {
                    AlmanacDetailRow("公历", "${info.date.year}年${info.date.monthValue}月${info.date.dayOfMonth}日 · 当年第${info.date.dayOfYear}天")
                    AlmanacDetailRow("农历", "${info.lunarYear}${info.lunarMonth}${info.lunarDay}")
                    AlmanacDetailRow("星座", "${info.constellation}座")
                    if (info.traditionalFestivals.isNotEmpty()) {
                        AlmanacDetailRow("传统节日", info.traditionalFestivals.joinToString("、"), C.rose)
                    }
                }
            }

            item(key = "pillars") {
                AlmanacSectionCard(title = "干支与纳音") {
                    AlmanacPillarOverview(info.pillars)
                    AlmanacDetailRow("日干五行", info.dayStemElement)
                    AlmanacDetailRow("日五行", info.pillars.last().naYin)
                }
            }

            item(key = "seasons") {
                AlmanacSectionCard(title = "节气与时令") {
                    AlmanacDetailRow("当日节气", info.solarTerm.ifBlank { "当日无交节" }, C.gold)
                    AlmanacDetailRow("节候", info.seasonalPhase)
                    AlmanacDetailRow("物候", info.phenology)
                    AlmanacDetailRow("前一节气", info.previousSolarTerm)
                    AlmanacDetailRow("后一节气", info.nextSolarTerm)
                }
            }

            item(key = "yi-ji") {
                AlmanacSectionCard(title = "今日宜忌") {
                    AlmanacListBlock("宜", info.suitable, C.mint)
                    AlmanacListBlock("忌", info.avoid, C.rose)
                }
            }

            item(key = "gods") {
                AlmanacSectionCard(title = "值日与神煞") {
                    AlmanacDetailRow("建除十二值", info.dayOfficer, C.gold)
                    AlmanacDetailRow(
                        "值日天神",
                        "${info.dayGod}（${info.dayGodType}日）· ${info.dayGodLuck}",
                    )
                    AlmanacDetailRow("吉神宜趋", info.auspiciousGods.joinToString("、").ifBlank { "无" }, C.mint)
                    AlmanacDetailRow("凶煞宜忌", info.inauspiciousGods.joinToString("、").ifBlank { "无" }, C.rose)
                }
            }

            item(key = "taboos") {
                AlmanacSectionCard(title = "百忌、冲煞与胎神") {
                    AlmanacDetailRow("彭祖百忌", "${info.pengZuGan}；${info.pengZuZhi}")
                    AlmanacDetailRow("相冲", info.clash)
                    AlmanacDetailRow("岁煞", "岁煞${info.shaDirection}")
                    AlmanacDetailRow("本月胎神", info.monthFetalGod)
                    AlmanacDetailRow("今日胎神", info.dayFetalGod)
                }
            }

            item(key = "moon-customs") {
                AlmanacSectionCard(title = "月相与民俗") {
                    AlmanacDetailRow("月名", "${info.lunarMonth} · ${info.pillars[1].ganZhi}月")
                    AlmanacDetailRow("月相", info.moonPhase)
                    AlmanacDetailRow("六耀", info.liuYao)
                    AlmanacDetailRow("日禄", info.dayLu)
                    AlmanacDetailRow("岁时民俗", info.annualFolkCustoms.joinToString("·"))
                }
            }

            item(key = "directions") {
                AlmanacSectionCard(title = "吉神方位") {
                    AlmanacDetailRow("喜神", info.joyDirection, C.gold)
                    AlmanacDetailRow("福神", info.fortuneDirection)
                    AlmanacDetailRow("财神", info.wealthDirection)
                    AlmanacDetailRow("阳贵神", info.yangNobleDirection)
                    AlmanacDetailRow("阴贵神", info.yinNobleDirection)
                }
            }

            item(key = "void-nine-star") {
                AlmanacSectionCard(title = "空亡与九宫飞星") {
                    AlmanacDetailRow("年空亡", info.yearVoid)
                    AlmanacDetailRow("月空亡", info.monthVoid)
                    AlmanacDetailRow("日空亡", info.dayVoid)
                    AlmanacDetailRow("九星", info.nineStar, C.gold)
                    AlmanacDetailRow("九星所值", info.nineStarDetail)
                }
            }

            item(key = "schedule-title") {
                Text(
                    "当日日程",
                    color = C.textMain,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            if (scheduleItems.isEmpty()) {
                item(key = "schedule-empty") {
                    Surface(
                        color = C.panelAlt,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("当日还没有日程。", color = C.textSub, modifier = Modifier.padding(16.dp))
                    }
                }
            } else {
                items(scheduleItems, key = { "schedule-${it.id}" }) { item ->
                    ScheduleItemCard(
                        item = item,
                        onToggle = { onToggleSchedule(item.id) },
                        onDelete = { onDeleteSchedule(item.id) },
                    )
                }
            }

            item(key = "calendar-note") {
                Text(
                    "历法内容依据传统干支、节气与择日规则离线推算，不含法定节假日及调休安排。不同流派在月柱交接和宜忌取法上可能略有差异。",
                    color = C.textSub,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            item(key = "bottom-space") { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun AlmanacSectionCard(title: String, content: @Composable () -> Unit) {
    val C = LocalFortunePalette.current
    Card(
        colors = CardDefaults.cardColors(containerColor = C.panel),
        border = BorderStroke(1.dp, C.line),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                title,
                color = C.textMain,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            content()
        }
    }
}

@Composable
private fun AlmanacPillarOverview(pillars: List<AlmanacPillar>) {
    val C = LocalFortunePalette.current
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        pillars.forEachIndexed { index, pillar ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(pillar.label, color = C.textSub, style = MaterialTheme.typography.labelMedium)
                Text(
                    pillar.ganZhi,
                    color = C.gold,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text("属${pillar.zodiac}", color = C.textMain, style = MaterialTheme.typography.bodyMedium)
                Text(pillar.naYin, color = C.textSub, style = MaterialTheme.typography.labelMedium)
            }
            if (index < pillars.lastIndex) {
                Box(Modifier.width(1.dp).height(68.dp).background(C.line))
            }
        }
    }
}

@Composable
private fun AlmanacDetailRow(label: String, value: String, accent: Color? = null) {
    val C = LocalFortunePalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            label,
            color = accent ?: C.textSub,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.width(84.dp),
        )
        Text(
            value.ifBlank { "无" },
            color = C.textMain,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AlmanacListBlock(label: String, values: List<String>, accent: Color) {
    val C = LocalFortunePalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(7.dp)
                .background(accent, CircleShape),
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = accent, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(
                values.joinToString("·").ifBlank { "无" },
                color = C.textMain,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun CalendarDetailRow(label: String, value: String, accent: Color) {
    val C = LocalFortunePalette.current
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Text(label, color = accent, style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(68.dp))
        Text(value, color = C.textMain, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun CalendarValuePicker(
    title: String,
    values: List<Int>,
    selected: Int,
    label: (Int) -> String,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    val C = LocalFortunePalette.current
    val selectedIndex = values.indexOf(selected).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (selectedIndex - 3).coerceAtLeast(0))
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().height(360.dp)) {
                items(values, key = { it }) { value ->
                    Surface(
                        onClick = { onSelect(value) },
                        color = if (value == selected) C.gold.copy(alpha = 0.18f) else Color.Transparent,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                label(value),
                                color = if (value == selected) C.gold else C.textMain,
                                fontWeight = if (value == selected) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("关闭") } },
        properties = DialogProperties(usePlatformDefaultWidth = true),
    )
}

private fun calendarRowCount(month: YearMonth): Int {
    val leadingEmptyCells = month.atDay(1).dayOfWeek.value % 7
    return (leadingEmptyCells + month.lengthOfMonth() + 6) / 7
}

// 农历计算串行执行，避免启动或快速翻页时多个 CPU 密集任务争抢渲染线程。
private val monthInfoCache = android.util.LruCache<YearMonth, List<CalendarDateInfo>>(64)
private val almanacDayDetailCache = android.util.LruCache<LocalDate, AlmanacDayDetail>(32)
private val monthInfoComputeLock = Any()
private val calendarComputationDispatcher = Dispatchers.Default.limitedParallelism(1)
private val calendarComputationScope = CoroutineScope(SupervisorJob() + calendarComputationDispatcher)
private val prefetchedMonths = ConcurrentHashMap.newKeySet<YearMonth>()

private fun computeMonthInfo(month: YearMonth): List<CalendarDateInfo> {
    monthInfoCache.get(month)?.let { return it }
    return synchronized(monthInfoComputeLock) {
        monthInfoCache.get(month)?.let { return@synchronized it }
        List(month.lengthOfMonth()) { index ->
            calendarDateInfo(month.atDay(index + 1))
        }.also { result ->
            monthInfoCache.put(month, result)
        }
    }
}

private fun prefetchMonthInfo(month: YearMonth) {
    if (monthInfoCache.get(month) != null || !prefetchedMonths.add(month)) return
    calendarComputationScope.launch {
        try {
            computeMonthInfo(month)
        } finally {
            prefetchedMonths.remove(month)
        }
    }
}

// 菱形缓存范围：以 center(年,月) 为中心，包含本年全年、前/后年 center月±3、前前/后后年 center月±1。
private fun diamondMonths(center: YearMonth): List<YearMonth> {
    val result = LinkedHashSet<YearMonth>()
    result.add(center)
    for (distance in 1..2) {
        result.add(center.minusMonths(distance.toLong()))
        result.add(center.plusMonths(distance.toLong()))
    }
    // 年份手势最常访问前后年同月，优先于本年剩余月份。
    result.add(center.minusYears(1))
    result.add(center.plusYears(1))
    for (m in 1..12) result.add(YearMonth.of(center.year, m))
    // 前/后年：center.month ±3（跨年自然处理）
    for (d in -3..3) {
        result.add(center.minusYears(1).plusMonths(d.toLong()))
        result.add(center.plusYears(1).plusMonths(d.toLong()))
    }
    // 前前/后后年：center.month ±1
    for (d in -1..1) {
        result.add(center.minusYears(2).plusMonths(d.toLong()))
        result.add(center.plusYears(2).plusMonths(d.toLong()))
    }
    return result.toList()
}

private enum class CalendarPicker { Year, Month }

private enum class CalendarDragAxis { Horizontal, Vertical }

private const val CALENDAR_MIN_YEAR = 1900
private const val CALENDAR_MAX_YEAR = 2100

private val TRADITIONAL_FESTIVAL_NAMES = setOf(
    "春节",
    "元宵节",
    "龙抬头",
    "龙头节",
    "上巳节",
    "寒食节",
    "清明节",
    "端午节",
    "七夕节",
    "中元节",
    "中秋节",
    "重阳节",
    "寒衣节",
    "下元节",
    "腊八节",
    "小年",
    "除夕",
)

internal data class CalendarDateInfo(
    val date: LocalDate,
    val weekday: String,
    val ganZhiYear: String,
    val zodiac: String,
    val lunarMonth: String,
    val lunarDay: String,
    val solarTerm: String,
    val traditionalFestivals: List<String>,
) {
    val cellLabel: String
        get() = solarTerm.ifBlank {
            traditionalFestivals.firstOrNull() ?: lunarDay
        }
}

private fun calendarDateInfo(date: LocalDate): CalendarDateInfo {
    val solar = Solar.fromYmd(date.year, date.monthValue, date.dayOfMonth)
    val lunar = solar.lunar
    val solarTerm = lunar.jieQi.orEmpty()
    val traditional = (
        lunar.festivals +
            lunar.otherFestivals +
            if (solarTerm == "清明") listOf("清明节") else emptyList()
        ).filter { it in TRADITIONAL_FESTIVAL_NAMES }.distinct()
    return CalendarDateInfo(
        date = date,
        weekday = listOf("一", "二", "三", "四", "五", "六", "日")[date.dayOfWeek.value - 1],
        ganZhiYear = lunar.yearInGanZhi,
        zodiac = lunar.yearShengXiao,
        lunarMonth = (if (lunar.month < 0) "闰" else "") + lunar.monthInChinese + "月",
        lunarDay = lunar.dayInChinese,
        solarTerm = solarTerm,
        traditionalFestivals = traditional,
    )
}

private fun formatFullCalendarDate(date: LocalDate): String {
    val info = calendarDateInfo(date)
    return "${date.year}年${date.monthValue}月${date.dayOfMonth}日周${info.weekday}·${info.ganZhiYear}年${info.lunarMonth}${info.lunarDay}"
}

@Composable
private fun ScheduleItemCard(
    item: ScheduleItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    val C = LocalFortunePalette.current
    var confirm by remember { mutableStateOf(false) }
    Card(
        colors = CardDefaults.cardColors(containerColor = if (item.done) C.panelAlt else C.panel),
        border = BorderStroke(1.dp, C.line),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    if (item.done) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (item.done) "标记未完成" else "标记完成",
                    tint = if (item.done) C.mint else C.textSub,
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.title, color = C.textMain, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                if (item.note.isNotBlank()) {
                    Text(item.note, color = C.textSub, style = MaterialTheme.typography.bodyMedium)
                }
                Text(item.createdAt, color = C.textSub, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { confirm = true }) {
                Icon(Icons.Default.Delete, contentDescription = "删除日程", tint = C.textSub)
            }
        }
    }
    if (confirm) {
        ConfirmDeleteDialog(
            title = "删除日程",
            message = "确定删除日程「${item.title}」吗？",
            onConfirm = onDelete,
            onDismiss = { confirm = false },
        )
    }
}

@Composable
private fun HistoryScreen(vm: FortuneViewModel, onBack: () -> Unit) {
    val C = LocalFortunePalette.current
    var confirmClear by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        SubScreenHeader(title = "历史记录", onBack = onBack) {
            TextButton(onClick = { confirmClear = true }, enabled = vm.history.isNotEmpty()) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("清空")
            }
        }
        if (vm.history.isEmpty()) {
            EmptyState("还没有占卜记录", "起一卦或抽一次答案之书后，结果会出现在这里")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(vm.history, key = { it.id }) { ReadingCard(it, compact = true) }
            }
        }
    }
    if (confirmClear) {
        ConfirmDeleteDialog(
            title = "清空记录",
            message = "确定清空全部占卜记录吗？此操作不可恢复。",
            onConfirm = { vm.clearHistory() },
            onDismiss = { confirmClear = false },
        )
    }
}

@Composable
private fun MineScreen(vm: FortuneViewModel) {
    var section by remember { mutableStateOf(MineSection.Profile) }
    BackHandler(enabled = section != MineSection.Profile) { section = MineSection.Profile }
    when (section) {
        MineSection.Profile -> MineProfile(
            vm,
            onOpenSettings = { section = MineSection.Settings },
            onOpenHistory = { section = MineSection.History },
        )
        MineSection.History -> HistoryScreen(vm, onBack = { section = MineSection.Profile })
        MineSection.Settings -> SettingsScreen(vm, onBack = { section = MineSection.Profile })
    }
}

private enum class MineSection { Profile, History, Settings }

@Composable
private fun MineProfile(vm: FortuneViewModel, onOpenSettings: () -> Unit, onOpenHistory: () -> Unit) {
    val C = LocalFortunePalette.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var editingProfile by rememberSaveable { mutableStateOf(false) }
    var draftNickname by rememberSaveable { mutableStateOf("") }
    var draftBirthDate by rememberSaveable { mutableStateOf("") }
    var draftFortuneKeywords by rememberSaveable { mutableStateOf("") }
    val draftBirthDateValid = draftBirthDate.isBlank() ||
        DailyFortuneEngine.parseBirthDate(draftBirthDate, LocalDate.now()) != null

    fun startEditingProfile() {
        draftNickname = vm.nickname
        draftBirthDate = vm.birthDate
        draftFortuneKeywords = vm.fortuneKeywords
        editingProfile = true
    }

    fun cancelEditingProfile() {
        keyboardController?.hide()
        editingProfile = false
    }

    fun finishEditingProfile() {
        if (!draftBirthDateValid) return
        keyboardController?.hide()
        vm.updateProfile(
            nicknameValue = draftNickname,
            birthDateValue = draftBirthDate,
            fortuneKeywordsValue = draftFortuneKeywords,
        )
        editingProfile = false
    }

    BackHandler(enabled = editingProfile) { cancelEditingProfile() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(color = C.gold.copy(alpha = 0.16f), shape = CircleShape, modifier = Modifier.size(64.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = C.gold, modifier = Modifier.size(42.dp))
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    vm.nickname.ifBlank { "知否用户" },
                    color = C.textMain,
                    style = MaterialTheme.typography.titleLarge.copy(letterSpacing = (-0.5).sp, fontWeight = FontWeight.SemiBold),
                )
                Text("本地账户", color = C.textSub, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileMetric("占卜记录", vm.history.size.toString(), Modifier.weight(1f), onClick = onOpenHistory)
            ProfileMetric("日程事项", vm.scheduleItems.size.toString(), Modifier.weight(1f))
        }
        SettingsEntryRow(icon = Icons.AutoMirrored.Filled.List, title = "占卜记录", subtitle = "查看历史占卜与解读", onClick = onOpenHistory)
        SettingsEntryRow(icon = Icons.Default.Settings, title = "设置", subtitle = "主题、AI 解读、语音与隐私", onClick = onOpenSettings)
        Card(
            colors = CardDefaults.cardColors(containerColor = C.panel),
            border = BorderStroke(1.dp, C.line),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("个人资料", color = C.textMain, fontWeight = FontWeight.SemiBold)
                    if (editingProfile) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = ::cancelEditingProfile) {
                                Text("取消", color = C.textSub, style = MaterialTheme.typography.labelMedium)
                            }
                            TextButton(
                                onClick = ::finishEditingProfile,
                                enabled = draftBirthDateValid,
                            ) {
                                Text("完成", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    } else {
                        TextButton(onClick = ::startEditingProfile) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("修改")
                        }
                    }
                }
                if (editingProfile) {
                    OutlinedTextField(
                        value = draftNickname,
                        onValueChange = { draftNickname = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("昵称") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = draftBirthDate,
                        onValueChange = { draftBirthDate = it.take(10) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("生日（公历）") },
                        placeholder = { Text("YYYY-MM-DD，可留空") },
                        singleLine = true,
                        isError = !draftBirthDateValid,
                        supportingText = if (!draftBirthDateValid) {
                            { Text("请输入有效且不晚于今天的日期；留空不影响使用") }
                        } else {
                            null
                        },
                    )
                    OutlinedTextField(
                        value = draftFortuneKeywords,
                        onValueChange = { draftFortuneKeywords = it.take(120) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("运势关注词") },
                        placeholder = { Text("例如：工作、健康、关系，可留空") },
                    )
                } else {
                    Text("昵称", color = C.textSub, style = MaterialTheme.typography.labelMedium)
                    Text(vm.nickname.ifBlank { "未设置" }, color = C.textMain)
                    Text("生日（公历）", color = C.textSub, style = MaterialTheme.typography.labelMedium)
                    Text(vm.birthDate.ifBlank { "未设置" }, color = C.textMain)
                    Text("运势关注词", color = C.textSub, style = MaterialTheme.typography.labelMedium)
                    Text(vm.fortuneKeywords.ifBlank { "未设置" }, color = C.textMain)
                }
            }
        }
    }
}

@Composable
private fun ProfileMetric(label: String, value: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val C = LocalFortunePalette.current
    Surface(
        onClick = onClick ?: {},
        color = C.panelAlt,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        enabled = onClick != null,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(value, color = C.gold, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(label, color = C.textSub, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// 二级页面顶部：返回按钮 + 标题 + 可选右侧操作。
@Composable
internal fun SubScreenHeader(title: String, onBack: () -> Unit, actions: @Composable () -> Unit) {
    val C = LocalFortunePalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = C.textMain)
        }
        Text(title, color = C.textMain, style = MaterialTheme.typography.titleLarge.copy(letterSpacing = (-0.5).sp, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.weight(1f))
        actions()
    }
}

// 一级页里的入口行：左图标 + 标题/副标题 + 右箭头（替代顶部 tab，更清晰的导航入口）。
@Composable
private fun SettingsEntryRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    val C = LocalFortunePalette.current
    Surface(
        onClick = onClick,
        color = C.panel,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, C.line),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Icon(icon, contentDescription = null, tint = C.gold, modifier = Modifier.size(24.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, color = C.textMain, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = C.textSub, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = C.textSub)
        }
    }
}

@Composable
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
private fun SettingsScreen(vm: FortuneViewModel, onBack: () -> Unit) {
    val C = LocalFortunePalette.current
    Column(Modifier.fillMaxSize()) {
        SubScreenHeader(title = "设置", onBack = onBack) {}
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = C.panel),
            border = BorderStroke(1.dp, C.line),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("主题", color = C.textMain, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    val labels = listOf("深色" to ThemeMode.Dark, "浅色" to ThemeMode.Light, "跟随系统" to ThemeMode.System)
                    labels.forEachIndexed { i, (label, mode) ->
                        SegmentedButton(
                            selected = vm.themeMode == mode,
                            onClick = { vm.updateThemeMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(i, labels.size),
                        ) { Text(label) }
                    }
                }
            }
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = C.panel),
            border = BorderStroke(1.dp, C.line),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("AI 解读与对话", color = C.textMain, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = vm.aiApiKey,
                    onValueChange = { vm.updateAiApiKey(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("API Key") },
                    placeholder = { Text("仅保存在本机") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = vm.aiModel,
                    onValueChange = { vm.updateAiModel(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("模型") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = vm.aiEndpoint,
                    onValueChange = { vm.updateAiEndpoint(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("OpenAI-compatible endpoint") },
                    singleLine = true,
                )
                Text(
                    "AI Key、模型和接口地址只写入本机设置，不会进入 Git 仓库。未配置 Key 时仍可使用本地占卜，但不能生成 AI 解读或对话回复。",
                    color = C.textSub,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = C.panel),
            border = BorderStroke(1.dp, C.line),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI 语音转写", color = C.textMain, fontWeight = FontWeight.SemiBold)
                        Text("试验性 OpenAI-compatible 接口", color = C.textSub, style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = vm.cloudSpeechEnabled,
                        onCheckedChange = { vm.updateCloudSpeechEnabled(it) },
                    )
                }
                AnimatedVisibility(vm.cloudSpeechEnabled) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = vm.cloudSpeechApiKey,
                            onValueChange = { vm.updateCloudSpeechApiKey(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("语音接口 API Key") },
                            placeholder = { Text("仅保存在本机") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = vm.cloudSpeechModel,
                            onValueChange = { vm.updateCloudSpeechModel(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("语音模型") },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = vm.cloudSpeechEndpoint,
                            onValueChange = { vm.updateCloudSpeechEndpoint(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("音频转写 endpoint") },
                            singleLine = true,
                        )
                        Text(
                            "开启后，录音会发送到所配置的第三方服务。关闭时始终使用应用内离线模型。配置只保存在本机。",
                            color = C.textSub,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = C.panel),
            border = BorderStroke(1.dp, C.line),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("应用定位", color = C.textMain, fontWeight = FontWeight.SemiBold)
                Text("知否运势是安装即用的手机端应用，所有基础记录都保存在本机。后续可以继续扩展账号同步、AI 解读、提醒和会员能力。", color = C.textSub)
            }
        }
    }
    }
}

@Composable
private fun CoinCastingProgressCard(lines: List<CoinLineResult>, question: String) {
    val C = LocalFortunePalette.current
    Card(
        colors = CardDefaults.cardColors(containerColor = C.panel),
        border = BorderStroke(1.dp, C.gold.copy(alpha = 0.55f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("铜钱起卦", color = C.textMain, fontWeight = FontWeight.SemiBold)
                    Text("初爻起，由下向上投掷", color = C.textSub, style = MaterialTheme.typography.bodySmall)
                }
                Text("${lines.size}/6", color = C.gold, style = MaterialTheme.typography.titleMedium)
            }
            Text(
                "所问：$question",
                color = C.textMain,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            for (position in 6 downTo 1) {
                CoinLineRow(
                    position = position,
                    line = lines.firstOrNull { it.position == position },
                )
            }
            Text(
                "每枚铜钱独立投掷：阳面记 3，阴面记 2。",
                color = C.textSub,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun CoinHexagramDetail(reading: FortuneReading) {
    val C = LocalFortunePalette.current
    val movingCount = reading.coinLines.count { it.isMoving }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(C.panelAlt, RoundedCornerShape(6.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("六次投掷记录", color = C.textMain, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text("本卦", color = C.textSub, style = MaterialTheme.typography.labelMedium)
                Text(
                    reading.primaryHexagram.ifBlank { reading.title },
                    color = C.gold,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Column(Modifier.weight(1f)) {
                Text(if (movingCount == 0) "动爻" else "变卦", color = C.textSub, style = MaterialTheme.typography.labelMedium)
                Text(
                    if (movingCount == 0) "无 · 六爻皆静" else reading.transformedHexagram,
                    color = if (movingCount == 0) C.textMain else C.mint,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        for (position in 6 downTo 1) {
            CoinLineRow(
                position = position,
                line = reading.coinLines.firstOrNull { it.position == position },
            )
        }
        Text(
            "阳面=3，阴面=2；第一次投掷为最下方初爻。6、9 为动爻。",
            color = C.textSub,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun ClassicSelectionDetail(reading: FortuneReading) {
    if (reading.classicReferences.isEmpty()) return
    val C = LocalFortunePalette.current
    var commentaryExpanded by remember(reading.id) { mutableStateOf(false) }
    val contexts = listOfNotNull(reading.primaryClassic, reading.transformedClassic)
        .distinctBy { it.number }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(C.panelAlt, RoundedCornerShape(6.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Text("经典取用", color = C.textMain, fontWeight = FontWeight.SemiBold)
        Text(reading.classicRule, color = C.textMain, style = MaterialTheme.typography.bodyMedium)
        reading.classicReferences.forEachIndexed { index, reference ->
            if (index > 0) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(C.line))
            }
            val lineLabel = if (reference.linePosition in 1..6) {
                " · ${coinLinePositionLabel(reference.linePosition)}"
            } else {
                ""
            }
            Text(
                "${if (reference.isPrimary) "主要依据" else "参看"} · 第${reference.hexagramNumber}卦 ${reference.hexagramName}${reference.hexagramGlyph} · ${reference.textType}$lineLabel",
                color = if (reference.isPrimary) C.gold else C.textSub,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (reference.isPrimary) FontWeight.SemiBold else FontWeight.Normal,
            )
            Text(reference.text, color = C.textMain, style = MaterialTheme.typography.bodyLarge)
            if (reference.commentary.isNotBlank()) {
                Text(reference.commentary, color = C.textSub, style = MaterialTheme.typography.bodySmall)
            }
        }
        Text(
            reading.classicMethod,
            color = C.textSub,
            style = MaterialTheme.typography.bodySmall,
        )
        if (contexts.isNotEmpty()) {
            TextButton(onClick = { commentaryExpanded = !commentaryExpanded }) {
                Icon(
                    if (commentaryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(if (commentaryExpanded) "收起《彖》《象》" else "查看《彖》《象》原文")
            }
            AnimatedVisibility(commentaryExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    contexts.forEach { context ->
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(
                                "第${context.number}卦 ${context.name}${context.glyph}",
                                color = C.mint,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text("卦辞：${context.judgment}", color = C.textMain, style = MaterialTheme.typography.bodyMedium)
                            Text(context.tuan, color = C.textSub, style = MaterialTheme.typography.bodySmall)
                            Text(context.image, color = C.textSub, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        Text(reading.classicSource, color = C.textSub, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun CoinLineRow(position: Int, line: CoinLineResult?) {
    val C = LocalFortunePalette.current
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            coinLinePositionLabel(position),
            color = if (line == null) C.textSub else C.textMain,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(36.dp),
        )
        CoinLineStroke(line = line, modifier = Modifier.width(68.dp).height(24.dp))
        Spacer(Modifier.width(8.dp))
        if (line == null) {
            Text(
                "等待第${position}次投掷",
                color = C.textSub,
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    line.coinValues.forEach { value -> CoinFaceChip(isYang = value == 3) }
                    Spacer(Modifier.width(3.dp))
                    Text(line.combinationLabel, color = C.textMain, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    "${line.value} · ${line.typeLabel} · ${if (line.isMoving) "动爻" else "静爻"}",
                    color = if (line.isMoving) C.rose else C.textSub,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun CoinFaceChip(isYang: Boolean) {
    val C = LocalFortunePalette.current
    Box(
        modifier = Modifier
            .size(19.dp)
            .background(if (isYang) C.gold.copy(alpha = 0.2f) else C.panel, CircleShape)
            .border(1.dp, if (isYang) C.gold else C.line, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            if (isYang) "阳" else "阴",
            color = if (isYang) C.gold else C.textSub,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CoinLineStroke(line: CoinLineResult?, modifier: Modifier = Modifier) {
    val C = LocalFortunePalette.current
    Canvas(modifier) {
        val y = size.height / 2f
        val strokeWidth = 4.dp.toPx()
        val color = if (line == null) C.line else C.gold
        when {
            line == null -> drawLine(color, Offset(4.dp.toPx(), y), Offset(size.width - 4.dp.toPx(), y), 2.dp.toPx(), StrokeCap.Round)
            line.isYang -> drawLine(color, Offset(3.dp.toPx(), y), Offset(size.width - 3.dp.toPx(), y), strokeWidth, StrokeCap.Round)
            else -> {
                val gap = 10.dp.toPx()
                drawLine(color, Offset(3.dp.toPx(), y), Offset(size.width / 2f - gap / 2f, y), strokeWidth, StrokeCap.Round)
                drawLine(color, Offset(size.width / 2f + gap / 2f, y), Offset(size.width - 3.dp.toPx(), y), strokeWidth, StrokeCap.Round)
            }
        }
        if (line?.value == 9) {
            drawCircle(C.rose, radius = 4.dp.toPx(), center = Offset(size.width / 2f, y), style = Stroke(1.5.dp.toPx()))
        } else if (line?.value == 6) {
            val radius = 4.dp.toPx()
            drawLine(C.rose, Offset(size.width / 2f - radius, y - radius), Offset(size.width / 2f + radius, y + radius), 1.5.dp.toPx(), StrokeCap.Round)
            drawLine(C.rose, Offset(size.width / 2f + radius, y - radius), Offset(size.width / 2f - radius, y + radius), 1.5.dp.toPx(), StrokeCap.Round)
        }
    }
}

@Composable
private fun ReadingCard(reading: FortuneReading, compact: Boolean = false) {
    val C = LocalFortunePalette.current
    Card(
        colors = CardDefaults.cardColors(containerColor = C.panel),
        border = BorderStroke(1.dp, C.line),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(if (compact) 14.dp else 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(reading.title, color = C.textMain, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(reading.timeLabel, color = C.textSub, style = MaterialTheme.typography.bodySmall)
                }
                Badge(reading.kind)
            }
            if (reading.question.isNotBlank()) {
                Text("问：${reading.question}", color = C.textSub, style = MaterialTheme.typography.bodyMedium)
            }
            if (reading.coinLines.isNotEmpty()) {
                CoinHexagramDetail(reading)
            }
            if (reading.classicReferences.isNotEmpty()) {
                ClassicSelectionDetail(reading)
            }
            if (reading.kind == "铜钱卦") {
                Text("本地基础解读", color = C.textSub, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }
            Text(reading.body, color = C.textMain, style = MaterialTheme.typography.bodyLarge)
            Text(reading.advice, color = C.gold, style = MaterialTheme.typography.bodyMedium)
            if (reading.aiStatus.isNotBlank()) {
                Text(reading.aiStatus, color = C.textSub, style = MaterialTheme.typography.bodyMedium)
            }
            if (reading.aiInterpretation.isNotBlank()) {
                Surface(color = C.panelAlt, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, C.line)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(if (reading.kind == "铜钱卦") "AI 详细解读" else "AI 解读", color = C.mint, fontWeight = FontWeight.SemiBold)
                        MarkdownText(reading.aiInterpretation, color = C.textMain, style = MaterialTheme.typography.bodyMedium, onLight = C.isLight)
                    }
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String) {
    val C = LocalFortunePalette.current
    Surface(color = C.panelAlt, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, C.line)) {
        Text(text, color = C.gold, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

@Composable
private fun FortuneDial(score: Int) {
    val C = LocalFortunePalette.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = C.panel),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, C.line),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(112.dp)) {
                Canvas(Modifier.fillMaxSize()) {
                    val stroke = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    drawArc(C.panelAlt, -220f, 260f, false, style = stroke, size = Size(size.width, size.height))
                    drawArc(
                        brush = Brush.sweepGradient(listOf(C.mint, C.gold, C.rose, C.mint)),
                        startAngle = -220f,
                        sweepAngle = 260f * (score / 100f),
                        useCenter = false,
                        style = stroke,
                        size = Size(size.width, size.height),
                    )
                }
                Text("$score", color = C.textMain, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("今日节奏", color = C.textMain, style = MaterialTheme.typography.titleMedium)
                Text("用于排列今日提示的相对刻度，不代表概率或确定结论。", color = C.textSub)
            }
        }
    }
}

@Composable
private fun InsightStrip() {
    val C = LocalFortunePalette.current
    val labels = listOf("事业", "关系", "财务")
    val subs = listOf("稳中推进", "先听后说", "控制变量")
    val dots = listOf(C.mint, C.rose, C.gold)
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        labels.forEachIndexed { index, label ->
            Card(
                colors = CardDefaults.cardColors(containerColor = C.panelAlt),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp)
                        .padding(horizontal = 14.dp),
                ) {
                    // 标题组（圆点 + 标题）水平居中，垂直位于卡片约 20% 处。
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val dotColor = dots[index]
                        Box(
                            Modifier.size(8.dp).background(
                                brush = Brush.radialGradient(
                                    colors = listOf(dotColor, dotColor.copy(alpha = 0.55f)),
                                    center = androidx.compose.ui.geometry.Offset(8f, 8f),
                                    radius = 12f,
                                ),
                                shape = CircleShape,
                            ),
                        )
                        Text(
                            label,
                            color = C.textMain,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp,
                        )
                    }
                    // 副标题居中，位于标题组下方，留出呼吸空间；极轻微主题色倾向。
                    val subColor = androidx.compose.ui.graphics.lerp(
                        C.textSub, dots[index], if (C.isLight) 0.18f else 0.22f,
                    )
                    Text(
                        subs[index],
                        color = subColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 64.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(text: String, hint: String = "") {
    val C = LocalFortunePalette.current
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = C.line, modifier = Modifier.size(34.dp))
            Text(text, color = C.textSub, style = MaterialTheme.typography.bodyMedium)
            if (hint.isNotBlank()) {
                Text(hint, color = C.textSub.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

class FortuneViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = FortuneRepository(application)
    private val oracle = FortuneOracle()

    init {
        viewModelScope.launch(calendarComputationDispatcher) {
            delay(400)
            ensureCalendarCache()
        }
    }

    // 把持久化的菱形缓存载入内存；若缓存年份与当前年不符（每月1号/跨年），后台重建。
    private suspend fun ensureCalendarCache() {
        val now = LocalDate.now()
        val center = YearMonth.of(now.year, now.monthValue)
        // 先把已有持久化缓存载入内存，命中即免计算。
        val cached = repo.loadCalendarCache()
        cached.months.forEach { (key, cells) ->
            runCatching { YearMonth.parse(key) }.getOrNull()?.let { monthInfoCache.put(it, cells) }
        }
        if (cached.year != now.year || cached.months.isEmpty()) {
            rebuildDiamondCache(center)
        }
    }

    private suspend fun rebuildDiamondCache(center: YearMonth) {
        val months = diamondMonths(center)
        val map = HashMap<String, List<CalendarDateInfo>>()
        months.forEach { month ->
            val cells = computeMonthInfo(month)
            monthInfoCache.put(month, cells)
            map[month.toString()] = cells
            kotlinx.coroutines.yield()
        }
        repo.saveCalendarCache(center.year, map)
    }

    var question by mutableStateOf("")
    var latestReading by mutableStateOf<FortuneReading?>(null)
        private set
    var history by mutableStateOf(repo.loadHistory())
        private set
    var chatMessages by mutableStateOf(repo.loadChatMessages())
        private set
    var chatSending by mutableStateOf(false)
        private set
    var chatStatus by mutableStateOf("")
        private set
    var coinCasting by mutableStateOf(false)
        private set
    var coinCastingLines by mutableStateOf<List<CoinLineResult>>(emptyList())
        private set
    var coinCastingQuestion by mutableStateOf("")
        private set
    var scheduleItems by mutableStateOf(repo.loadScheduleItems())
        private set
    var wheelSegments by mutableStateOf(repo.loadWheelSegments())
        private set
    var wheelHistory by mutableStateOf(repo.loadWheelHistory())
        private set
    var nickname by mutableStateOf(repo.nickname)
        private set
    var birthDate by mutableStateOf(repo.birthDate)
        private set
    var fortuneKeywords by mutableStateOf(repo.fortuneKeywords)
        private set
    var themeMode by mutableStateOf(repo.themeMode)
        private set
    var todayFortune by mutableStateOf(
        DailyFortuneEngine.create(LocalDate.now(), nickname, birthDate, fortuneKeywords)
    )
        private set
    var aiApiKey by mutableStateOf(repo.aiApiKey)
        private set
    var aiModel by mutableStateOf(repo.aiModel)
        private set
    var aiEndpoint by mutableStateOf(repo.aiEndpoint)
        private set
    var cloudSpeechEnabled by mutableStateOf(repo.cloudSpeechEnabled)
        private set
    var cloudSpeechApiKey by mutableStateOf(repo.cloudSpeechApiKey)
        private set
    var cloudSpeechModel by mutableStateOf(repo.cloudSpeechModel)
        private set
    var cloudSpeechEndpoint by mutableStateOf(repo.cloudSpeechEndpoint)
        private set

    val recentOracleReadings: List<FortuneReading>
        get() {
            val cutoff = System.currentTimeMillis() - 3L * 24 * 60 * 60 * 1_000
            return history
                .asSequence()
                .filter { it.kind == "铜钱卦" || it.kind == "答案之书" }
                .filter { it.id >= cutoff }
                .sortedByDescending { it.id }
                .take(5)
                .toList()
        }

    val oracleTimeline: List<OracleTimelineEntry>
        get() {
            val readings = recentOracleReadings.asReversed()
            return (readings.map { OracleTimelineEntry.Reading(it) } +
                chatMessages.map { OracleTimelineEntry.Chat(it) })
                .sortedBy { it.timestamp }
        }

    fun castCoins() {
        if (coinCasting) return
        val castingQuestion = question.trim()
        question = ""
        coinCasting = true
        coinCastingQuestion = castingQuestion
        coinCastingLines = emptyList()
        viewModelScope.launch {
            val lines = mutableListOf<CoinLineResult>()
            repeat(6) { index ->
                delay(if (index == 0) 140L else 320L)
                lines += oracle.tossCoinLine(position = index + 1)
                coinCastingLines = lines.toList()
            }
            delay(280L)
            save(oracle.coin(castingQuestion, lines))
            coinCasting = false
            coinCastingLines = emptyList()
            coinCastingQuestion = ""
        }
    }

    fun drawAnswerBook() {
        save(oracle.answerBook(question))
        question = ""
    }

    fun refreshToday(date: LocalDate = LocalDate.now()) {
        todayFortune = DailyFortuneEngine.create(
            date = date,
            nickname = nickname,
            birthDateText = birthDate,
            keywordsText = fortuneKeywords,
        )
    }

    fun consumeDailyOraclePrompt(date: LocalDate): Boolean = repo.consumeDailyOraclePrompt(date)

    fun updateNickname(value: String) {
        nickname = value
        repo.nickname = value
        refreshToday()
    }

    fun updateProfile(
        nicknameValue: String,
        birthDateValue: String,
        fortuneKeywordsValue: String,
    ) {
        nickname = nicknameValue.trim()
        birthDate = birthDateValue.trim().take(10)
        fortuneKeywords = fortuneKeywordsValue.trim().take(120)
        repo.nickname = nickname
        repo.birthDate = birthDate
        repo.fortuneKeywords = fortuneKeywords
        refreshToday()
    }

    fun updateThemeMode(value: ThemeMode) {
        themeMode = value
        repo.themeMode = value
    }

    fun updateBirthDate(value: String) {
        birthDate = value.take(10)
        repo.birthDate = birthDate
        refreshToday()
    }

    fun updateFortuneKeywords(value: String) {
        fortuneKeywords = value.take(120)
        repo.fortuneKeywords = fortuneKeywords
        refreshToday()
    }

    fun updateAiApiKey(value: String) {
        aiApiKey = value.trim()
        repo.aiApiKey = aiApiKey
    }

    fun updateAiModel(value: String) {
        aiModel = value.trim()
        repo.aiModel = aiModel
    }

    fun updateAiEndpoint(value: String) {
        aiEndpoint = value.trim()
        repo.aiEndpoint = aiEndpoint
    }

    fun updateCloudSpeechEnabled(value: Boolean) {
        cloudSpeechEnabled = value
        repo.cloudSpeechEnabled = value
    }

    fun updateCloudSpeechApiKey(value: String) {
        cloudSpeechApiKey = value.trim()
        repo.cloudSpeechApiKey = cloudSpeechApiKey
    }

    fun updateCloudSpeechModel(value: String) {
        cloudSpeechModel = value.trim()
        repo.cloudSpeechModel = cloudSpeechModel
    }

    fun updateCloudSpeechEndpoint(value: String) {
        cloudSpeechEndpoint = value.trim()
        repo.cloudSpeechEndpoint = cloudSpeechEndpoint
    }

    fun sendChatMessage() {
        val content = question.trim()
        if (content.isBlank() || aiApiKey.isBlank() || chatSending) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis(),
            role = "user",
            content = content,
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
        )
        chatMessages = repo.saveChatMessages(chatMessages + userMessage)
        question = ""
        chatStatus = ""
        chatSending = true

        viewModelScope.launch {
            val result = AiChatClient.reply(
                endpoint = aiEndpoint,
                apiKey = aiApiKey,
                model = aiModel,
                messages = chatMessages,
            )
            result.fold(
                onSuccess = { content ->
                    val assistantMessage = ChatMessage(
                        id = maxOf(System.currentTimeMillis(), userMessage.id + 1),
                        role = "assistant",
                        content = content,
                        createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                    )
                    chatMessages = repo.saveChatMessages(chatMessages + assistantMessage)
                    chatStatus = ""
                },
                onFailure = { error ->
                    chatStatus = "AI 回复失败：${error.message ?: "请检查网络或接口配置"}"
                },
            )
            chatSending = false
        }
    }

    fun clearHistory() {
        repo.clearHistory()
        history = emptyList()
    }

    fun deleteReadingsByIds(ids: Set<Long>) {
        if (ids.isEmpty()) return
        history = repo.saveHistory(history.filterNot { it.id in ids })
    }

    fun deleteChatMessagesByIds(ids: Set<Long>) {
        if (ids.isEmpty()) return
        chatMessages = repo.saveChatMessages(chatMessages.filterNot { it.id in ids })
    }

    internal fun saveScheduleDraft(draft: ScheduleDraft, existingId: Long? = null): ScheduleItem {
        val existing = existingId?.let { id -> scheduleItems.firstOrNull { it.id == id } }
        val normalizedEndDate = draft.endDate.ifBlank {
            if (draft.endTime.isNotBlank()) draft.date.toString() else ""
        }
        val item = ScheduleItem(
            id = existing?.id ?: System.currentTimeMillis(),
            title = draft.title.trim().take(120),
            note = draft.note.trim().take(2_000),
            date = draft.date.toString(),
            createdAt = existing?.createdAt
                ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            done = draft.done,
            startTime = draft.startTime,
            endDate = normalizedEndDate,
            endTime = draft.endTime,
            location = draft.location.trim().take(240),
            participants = draft.participants.trim().take(500),
            highlightColor = draft.highlightColor,
            backgroundImageUri = draft.backgroundImageUri,
            pinned = draft.pinned,
        )
        scheduleItems = if (existing == null) {
            repo.saveScheduleItems(listOf(item) + scheduleItems)
        } else {
            repo.saveScheduleItems(scheduleItems.map { current -> if (current.id == item.id) item else current })
        }
        return item
    }

    fun toggleScheduleItem(id: Long) {
        scheduleItems = repo.saveScheduleItems(
            scheduleItems.map { item ->
                if (item.id == id) item.copy(done = !item.done) else item
            }
        )
    }

    fun deleteScheduleItem(id: Long) {
        scheduleItems = repo.saveScheduleItems(scheduleItems.filterNot { it.id == id })
    }

    fun addWheelSegment() {
        if (wheelSegments.size < 25) {
            wheelSegments = repo.saveWheelSegments(wheelSegments + "选项 ${wheelSegments.size + 1}")
        }
    }

    fun updateWheelSegment(index: Int, value: String) {
        if (index !in wheelSegments.indices) return
        wheelSegments = repo.saveWheelSegments(
            wheelSegments.toMutableList().also { it[index] = value }
        )
    }

    fun deleteWheelSegment(index: Int) {
        if (wheelSegments.size > 2 && index in wheelSegments.indices) {
            wheelSegments = repo.saveWheelSegments(
                wheelSegments.toMutableList().also { it.removeAt(index) }
            )
        }
    }

    fun setWheelSegmentCount(target: Int) {
        val n = target.coerceIn(2, 25)
        val current = wheelSegments.toMutableList()
        while (current.size < n) {
            current.add("选项 ${current.size + 1}")
        }
        if (current.size > n) {
            current.subList(n, current.size).clear()
        }
        wheelSegments = repo.saveWheelSegments(current)
    }

    fun resetWheelSegments() {
        wheelSegments = repo.saveWheelSegments(listOf("是", "否"))
    }

    fun addWheelHistory(result: String) {
        if (result.isBlank()) return
        val entry = WheelHistoryEntry(
            id = System.currentTimeMillis(),
            result = result,
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
        )
        wheelHistory = repo.saveWheelHistory(listOf(entry) + wheelHistory)
    }

    fun deleteWheelHistory(id: Long) {
        wheelHistory = repo.saveWheelHistory(wheelHistory.filterNot { it.id == id })
    }

    fun clearWheelHistory() {
        wheelHistory = repo.saveWheelHistory(emptyList())
    }

    private fun save(reading: FortuneReading) {
        val saved = if (aiApiKey.isBlank()) {
            reading.copy(aiStatus = "未配置 AI Key，已生成本地占卜结果")
        } else {
            reading.copy(aiStatus = "AI 解读生成中")
        }
        latestReading = saved
        history = repo.save(saved)
        if (aiApiKey.isNotBlank()) generateAiInterpretation(saved)
    }

    private fun generateAiInterpretation(reading: FortuneReading) {
        viewModelScope.launch {
            val result = AiInterpreter.interpret(
                endpoint = aiEndpoint,
                apiKey = aiApiKey,
                model = aiModel,
                reading = reading,
            )
            val updated = result.fold(
                onSuccess = { text -> reading.copy(aiInterpretation = text, aiStatus = "") },
                onFailure = { error -> reading.copy(aiStatus = "AI 解读失败：${error.message ?: "请检查网络、Key 或模型配置"}") },
            )
            latestReading = updated
            history = repo.replaceReading(updated)
        }
    }
}

data class FortuneReading(
    val id: Long,
    val kind: String,
    val title: String,
    val question: String,
    val body: String,
    val advice: String,
    val score: Int,
    val timeLabel: String,
    val aiInterpretation: String = "",
    val aiStatus: String = "",
    val coinLines: List<CoinLineResult> = emptyList(),
    val primaryHexagram: String = "",
    val transformedHexagram: String = "",
    val classicMethod: String = "",
    val classicRule: String = "",
    val classicSource: String = "",
    val classicReferences: List<ClassicReference> = emptyList(),
    val primaryClassic: ClassicHexagramContext? = null,
    val transformedClassic: ClassicHexagramContext? = null,
)

data class CoinLineResult(
    val position: Int,
    val coinValues: List<Int>,
) {
    val value: Int get() = coinValues.sum()
    val isYang: Boolean get() = value == 7 || value == 9
    val isMoving: Boolean get() = value == 6 || value == 9
    val transformedIsYang: Boolean
        get() = when (value) {
            6 -> true
            9 -> false
            else -> isYang
        }
    val typeLabel: String
        get() = when (value) {
            6 -> "老阴"
            7 -> "少阳"
            8 -> "少阴"
            9 -> "老阳"
            else -> "无效"
        }
    val combinationLabel: String
        get() = when (coinValues.count { it == 3 }) {
            0 -> "三阴"
            1 -> "一阳两阴"
            2 -> "两阳一阴"
            else -> "三阳"
        }
}

private fun coinLinePositionLabel(position: Int): String = when (position) {
    1 -> "初爻"
    2 -> "二爻"
    3 -> "三爻"
    4 -> "四爻"
    5 -> "五爻"
    6 -> "上爻"
    else -> "第${position}爻"
}

private fun coinLinePositionFocus(position: Int): String = when (position) {
    1 -> "事情的起点与动机"
    2 -> "内部条件与执行基础"
    3 -> "由内向外的转折"
    4 -> "外部环境的介入"
    5 -> "核心决策与主导因素"
    6 -> "阶段末端与结果边界"
    else -> "当前层级"
}

data class ScheduleItem(
    val id: Long,
    val title: String,
    val note: String,
    val date: String,
    val createdAt: String,
    val done: Boolean = false,
    val startTime: String = "",
    val endDate: String = "",
    val endTime: String = "",
    val location: String = "",
    val participants: String = "",
    val highlightColor: String = "",
    val backgroundImageUri: String = "",
    val pinned: Boolean = false,
)

data class WheelHistoryEntry(
    val id: Long,
    val result: String,
    val createdAt: String,
)

data class ChatMessage(
    val id: Long,
    val role: String,
    val content: String,
    val createdAt: String,
)

sealed interface OracleTimelineEntry {
    val timestamp: Long

    data class Reading(val reading: FortuneReading) : OracleTimelineEntry {
        override val timestamp: Long get() = reading.id
    }

    data class Chat(val message: ChatMessage) : OracleTimelineEntry {
        override val timestamp: Long get() = message.id
    }
}

class FortuneRepository(context: Context) {
    private val prefs = context.getSharedPreferences("zhifou_fortune", Context.MODE_PRIVATE)
    private val calendarCacheFile = File(context.cacheDir, "calendar_info_v2.json")

    var nickname: String
        get() = prefs.getString("nickname", "") ?: ""
        set(value) = prefs.edit().putString("nickname", value).apply()

    var themeMode: ThemeMode
        get() = runCatching { ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.System.name) ?: ThemeMode.System.name) }.getOrDefault(ThemeMode.System)
        set(value) = prefs.edit().putString("theme_mode", value.name).apply()

    var birthDate: String
        get() {
            if (prefs.contains("birth_date")) return prefs.getString("birth_date", "") ?: ""
            val legacy = prefs.getString("birth_hint", "").orEmpty().trim()
            return DailyFortuneEngine.parseBirthDate(legacy, LocalDate.now())?.toString().orEmpty()
        }
        set(value) = prefs.edit().putString("birth_date", value).apply()

    var fortuneKeywords: String
        get() {
            if (prefs.contains("fortune_keywords")) return prefs.getString("fortune_keywords", "") ?: ""
            val legacy = prefs.getString("birth_hint", "").orEmpty().trim()
            return legacy.takeIf { DailyFortuneEngine.parseBirthDate(it, LocalDate.now()) == null }.orEmpty()
        }
        set(value) = prefs.edit().putString("fortune_keywords", value).apply()

    fun consumeDailyOraclePrompt(date: LocalDate): Boolean {
        val lastShownDate = prefs.getString("daily_oracle_prompt_date", "").orEmpty()
        if (!shouldShowDailyOraclePrompt(lastShownDate, date)) return false
        prefs.edit().putString("daily_oracle_prompt_date", date.toString()).apply()
        return true
    }

    var aiApiKey: String
        get() = prefs.getString("ai_api_key", "") ?: ""
        set(value) = prefs.edit().putString("ai_api_key", value).apply()

    var aiModel: String
        get() = prefs.getString("ai_model", "gpt-4o-mini") ?: "gpt-4o-mini"
        set(value) = prefs.edit().putString("ai_model", value.ifBlank { "gpt-4o-mini" }).apply()

    var aiEndpoint: String
        get() = prefs.getString("ai_endpoint", "https://api.openai.com/v1/chat/completions")
            ?: "https://api.openai.com/v1/chat/completions"
        set(value) = prefs.edit()
            .putString("ai_endpoint", value.ifBlank { "https://api.openai.com/v1/chat/completions" })
            .apply()

    var cloudSpeechEnabled: Boolean
        get() = prefs.getBoolean("cloud_speech_enabled", false)
        set(value) = prefs.edit().putBoolean("cloud_speech_enabled", value).apply()

    var cloudSpeechApiKey: String
        get() = prefs.getString("cloud_speech_api_key", "") ?: ""
        set(value) = prefs.edit().putString("cloud_speech_api_key", value).apply()

    var cloudSpeechModel: String
        get() = prefs.getString("cloud_speech_model", "whisper-1") ?: "whisper-1"
        set(value) = prefs.edit().putString("cloud_speech_model", value.ifBlank { "whisper-1" }).apply()

    var cloudSpeechEndpoint: String
        get() = prefs.getString(
            "cloud_speech_endpoint",
            "https://api.openai.com/v1/audio/transcriptions",
        ) ?: "https://api.openai.com/v1/audio/transcriptions"
        set(value) = prefs.edit()
            .putString(
                "cloud_speech_endpoint",
                value.ifBlank { "https://api.openai.com/v1/audio/transcriptions" },
            )
            .apply()

    fun loadHistory(): List<FortuneReading> {
        val raw = prefs.getString("history", "[]") ?: "[]"
        val array = JSONArray(raw)
        return (0 until array.length()).map { index ->
            array.getJSONObject(index).toReading()
        }
    }

    fun save(reading: FortuneReading): List<FortuneReading> {
        val next = (listOf(reading) + loadHistory()).take(80)
        val array = JSONArray()
        next.forEach { array.put(it.toJson()) }
        prefs.edit().putString("history", array.toString()).apply()
        return next
    }

    fun replaceReading(reading: FortuneReading): List<FortuneReading> {
        val next = loadHistory().map { item ->
            if (item.id == reading.id) reading else item
        }
        val array = JSONArray()
        next.forEach { array.put(it.toJson()) }
        prefs.edit().putString("history", array.toString()).apply()
        return next
    }

    fun saveHistory(items: List<FortuneReading>): List<FortuneReading> {
        val next = items.take(80)
        val array = JSONArray()
        next.forEach { array.put(it.toJson()) }
        prefs.edit().putString("history", array.toString()).apply()
        return next
    }

    fun clearHistory() {
        prefs.edit().remove("history").apply()
    }

    fun loadChatMessages(): List<ChatMessage> {
        val raw = prefs.getString("ai_chat_messages", "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length())
                .map { index -> array.getJSONObject(index).toChatMessage() }
                .filter { it.role == "user" || it.role == "assistant" }
                .filter { it.content.isNotBlank() }
                .takeLast(30)
        }.getOrDefault(emptyList())
    }

    fun saveChatMessages(messages: List<ChatMessage>): List<ChatMessage> {
        val next = messages.takeLast(30)
        val array = JSONArray()
        next.forEach { array.put(it.toJson()) }
        prefs.edit().putString("ai_chat_messages", array.toString()).apply()
        return next
    }

    fun loadScheduleItems(): List<ScheduleItem> {
        val raw = prefs.getString("schedule_items", "[]") ?: "[]"
        val array = JSONArray(raw)
        return (0 until array.length()).map { index ->
            array.getJSONObject(index).toScheduleItem()
        }
    }

    fun saveScheduleItems(items: List<ScheduleItem>): List<ScheduleItem> {
        val next = items.take(120)
        val array = JSONArray()
        next.forEach { array.put(it.toJson()) }
        prefs.edit().putString("schedule_items", array.toString()).apply()
        return next
    }

    fun loadWheelSegments(): List<String> {
        val raw = prefs.getString("wheel_segments", "[]") ?: "[]"
        val list = runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).map { array.getString(it) }
        }.getOrDefault(emptyList())
        return if (list.size < 2) listOf("是", "否") else list
    }

    fun saveWheelSegments(items: List<String>): List<String> {
        val next = items.take(25)
        val array = JSONArray()
        next.forEach { array.put(it) }
        prefs.edit().putString("wheel_segments", array.toString()).apply()
        return next
    }

    fun loadWheelHistory(): List<WheelHistoryEntry> {
        val raw = prefs.getString("wheel_history", "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).map { index ->
                val obj = array.getJSONObject(index)
                WheelHistoryEntry(
                    id = obj.optLong("id"),
                    result = obj.optString("result"),
                    createdAt = obj.optString("createdAt"),
                )
            }
        }.getOrDefault(emptyList())
    }

    fun saveWheelHistory(items: List<WheelHistoryEntry>): List<WheelHistoryEntry> {
        val next = items.take(200)
        val array = JSONArray()
        next.forEach { entry ->
            array.put(JSONObject().put("id", entry.id).put("result", entry.result).put("createdAt", entry.createdAt))
        }
        prefs.edit().putString("wheel_history", array.toString()).apply()
        return next
    }

    internal data class PersistedCalendarCache(
        val year: Int,
        val months: Map<String, List<CalendarDateInfo>>,
    )

    // 日历缓存独立存放，避免普通设置首次读取时解析数百 KB 的月份数据。
    internal fun loadCalendarCache(): PersistedCalendarCache {
        val legacyRaw = prefs.getString("calendar_cache", null)
        if (!calendarCacheFile.exists() && !legacyRaw.isNullOrBlank()) {
            runCatching { calendarCacheFile.writeText(legacyRaw) }
        }
        if (prefs.contains("calendar_cache") || prefs.contains("calendar_cache_year")) {
            prefs.edit()
                .remove("calendar_cache")
                .remove("calendar_cache_year")
                .commit()
        }
        val raw = runCatching { calendarCacheFile.readText() }.getOrDefault("{}")
        return runCatching {
            val root = JSONObject(raw)
            val months = root.optJSONObject("months")
                ?: return@runCatching PersistedCalendarCache(0, emptyMap())
            val result = HashMap<String, List<CalendarDateInfo>>()
            val keys = months.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val arr = months.getJSONArray(key)
                val list = (0 until arr.length()).map { i ->
                    val o = arr.getJSONObject(i)
                    val festivals = o.optJSONArray("festivals")
                    CalendarDateInfo(
                        date = LocalDate.parse(o.getString("date")),
                        weekday = o.optString("weekday"),
                        ganZhiYear = o.optString("ganZhiYear"),
                        zodiac = o.optString("zodiac"),
                        lunarMonth = o.optString("lunarMonth"),
                        lunarDay = o.optString("lunarDay"),
                        solarTerm = o.optString("solarTerm"),
                        traditionalFestivals = if (festivals == null) emptyList() else (0 until festivals.length()).map { festivals.getString(it) },
                    )
                }
                result[key] = list
            }
            PersistedCalendarCache(root.optInt("year", 0), result)
        }.getOrDefault(PersistedCalendarCache(0, emptyMap()))
    }

    internal fun saveCalendarCache(year: Int, months: Map<String, List<CalendarDateInfo>>) {
        val root = JSONObject().put("year", year)
        val monthsObj = JSONObject()
        months.forEach { (key, cells) ->
            val arr = JSONArray()
            cells.forEach { info ->
                val fest = JSONArray()
                info.traditionalFestivals.forEach { fest.put(it) }
                arr.put(JSONObject()
                    .put("date", info.date.toString())
                    .put("weekday", info.weekday)
                    .put("ganZhiYear", info.ganZhiYear)
                    .put("zodiac", info.zodiac)
                    .put("lunarMonth", info.lunarMonth)
                    .put("lunarDay", info.lunarDay)
                    .put("solarTerm", info.solarTerm)
                    .put("festivals", fest))
            }
            monthsObj.put(key, arr)
        }
        root.put("months", monthsObj)
        val temporary = File(calendarCacheFile.parentFile, "${calendarCacheFile.name}.tmp")
        runCatching {
            temporary.writeText(root.toString())
            if (!temporary.renameTo(calendarCacheFile)) {
                calendarCacheFile.writeText(temporary.readText())
                temporary.delete()
            }
        }.onFailure { temporary.delete() }
    }
}

private fun FortuneReading.toJson(): JSONObject {
    val coinLinesJson = JSONArray()
    coinLines.forEach { line -> coinLinesJson.put(line.toJson()) }
    val classicReferencesJson = JSONArray()
    classicReferences.forEach { reference -> classicReferencesJson.put(reference.toJson()) }
    return JSONObject()
        .put("id", id)
        .put("kind", kind)
        .put("title", title)
        .put("question", question)
        .put("body", body)
        .put("advice", advice)
        .put("score", score)
        .put("timeLabel", timeLabel)
        .put("aiInterpretation", aiInterpretation)
        .put("aiStatus", aiStatus)
        .put("coinLines", coinLinesJson)
        .put("primaryHexagram", primaryHexagram)
        .put("transformedHexagram", transformedHexagram)
        .put("classicMethod", classicMethod)
        .put("classicRule", classicRule)
        .put("classicSource", classicSource)
        .put("classicReferences", classicReferencesJson)
        .put("primaryClassic", primaryClassic?.toJson() ?: JSONObject.NULL)
        .put("transformedClassic", transformedClassic?.toJson() ?: JSONObject.NULL)
}

private fun JSONObject.toReading(): FortuneReading = FortuneReading(
    id = optLong("id"),
    kind = optString("kind"),
    title = optString("title"),
    question = optString("question"),
    body = optString("body"),
    advice = optString("advice"),
    score = optInt("score", 70),
    timeLabel = optString("timeLabel"),
    aiInterpretation = optString("aiInterpretation"),
    aiStatus = optString("aiStatus"),
    coinLines = readCoinLines(),
    primaryHexagram = optString("primaryHexagram"),
    transformedHexagram = optString("transformedHexagram"),
    classicMethod = optString("classicMethod"),
    classicRule = optString("classicRule"),
    classicSource = optString("classicSource"),
    classicReferences = readClassicReferences(),
    primaryClassic = optJSONObject("primaryClassic")?.toClassicHexagramContext(),
    transformedClassic = optJSONObject("transformedClassic")?.toClassicHexagramContext(),
)

private fun CoinLineResult.toJson(): JSONObject {
    val values = JSONArray()
    coinValues.forEach(values::put)
    return JSONObject()
        .put("position", position)
        .put("coinValues", values)
}

private fun JSONObject.readCoinLines(): List<CoinLineResult> {
    val lines = optJSONArray("coinLines") ?: return emptyList()
    return (0 until lines.length()).mapNotNull { index ->
        val item = lines.optJSONObject(index) ?: return@mapNotNull null
        val valuesJson = item.optJSONArray("coinValues") ?: return@mapNotNull null
        val values = (0 until valuesJson.length()).map(valuesJson::optInt)
        val position = item.optInt("position")
        if (position !in 1..6 || values.size != 3 || values.any { it != 2 && it != 3 }) {
            null
        } else {
            CoinLineResult(position = position, coinValues = values)
        }
    }.distinctBy { it.position }.sortedBy { it.position }
}

private fun ClassicReference.toJson(): JSONObject = JSONObject()
    .put("hexagramNumber", hexagramNumber)
    .put("hexagramName", hexagramName)
    .put("hexagramGlyph", hexagramGlyph)
    .put("textType", textType)
    .put("linePosition", linePosition)
    .put("text", text)
    .put("commentary", commentary)
    .put("isPrimary", isPrimary)

private fun JSONObject.readClassicReferences(): List<ClassicReference> {
    val references = optJSONArray("classicReferences") ?: return emptyList()
    return (0 until references.length()).mapNotNull { index ->
        val item = references.optJSONObject(index) ?: return@mapNotNull null
        val number = item.optInt("hexagramNumber")
        val position = item.optInt("linePosition")
        val text = item.optString("text")
        if (number !in 1..64 || position !in 0..6 || text.isBlank()) {
            null
        } else {
            ClassicReference(
                hexagramNumber = number,
                hexagramName = item.optString("hexagramName"),
                hexagramGlyph = item.optString("hexagramGlyph"),
                textType = item.optString("textType"),
                linePosition = position,
                text = text,
                commentary = item.optString("commentary"),
                isPrimary = item.optBoolean("isPrimary"),
            )
        }
    }
}

private fun ClassicHexagramContext.toJson(): JSONObject = JSONObject()
    .put("number", number)
    .put("name", name)
    .put("glyph", glyph)
    .put("judgment", judgment)
    .put("tuan", tuan)
    .put("image", image)

private fun JSONObject.toClassicHexagramContext(): ClassicHexagramContext? {
    val number = optInt("number")
    val judgment = optString("judgment")
    if (number !in 1..64 || judgment.isBlank()) return null
    return ClassicHexagramContext(
        number = number,
        name = optString("name"),
        glyph = optString("glyph"),
        judgment = judgment,
        tuan = optString("tuan"),
        image = optString("image"),
    )
}

internal fun ScheduleItem.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("title", title)
    .put("note", note)
    .put("date", date)
    .put("createdAt", createdAt)
    .put("done", done)
    .put("startTime", startTime)
    .put("endDate", endDate)
    .put("endTime", endTime)
    .put("location", location)
    .put("participants", participants)
    .put("highlightColor", highlightColor)
    .put("backgroundImageUri", backgroundImageUri)
    .put("pinned", pinned)

internal fun JSONObject.toScheduleItem(): ScheduleItem = ScheduleItem(
    id = optLong("id"),
    title = optString("title"),
    note = optString("note"),
    date = optString("date").ifBlank {
        optString("createdAt").take(10).takeIf { value -> runCatching { LocalDate.parse(value) }.isSuccess }
            ?: LocalDate.now().toString()
    },
    createdAt = optString("createdAt"),
    done = optBoolean("done", false),
    startTime = optString("startTime"),
    endDate = optString("endDate"),
    endTime = optString("endTime"),
    location = optString("location"),
    participants = optString("participants"),
    highlightColor = optString("highlightColor"),
    backgroundImageUri = optString("backgroundImageUri"),
    pinned = optBoolean("pinned", false),
)

private fun ChatMessage.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("role", role)
    .put("content", content)
    .put("createdAt", createdAt)

private fun JSONObject.toChatMessage(): ChatMessage = ChatMessage(
    id = optLong("id"),
    role = optString("role"),
    content = optString("content"),
    createdAt = optString("createdAt"),
)

private object AiChatClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    suspend fun reply(
        endpoint: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val requestMessages = JSONArray().put(
                JSONObject()
                    .put("role", "system")
                    .put("content", CHAT_SYSTEM_PROMPT)
            )
            messages.takeLast(20).forEach { message ->
                requestMessages.put(
                    JSONObject()
                        .put("role", message.role)
                        .put("content", message.content.take(2_000))
                )
            }
            val body = JSONObject()
                .put("model", model.ifBlank { "gpt-4o-mini" })
                .put("temperature", 0.55)
                .put("messages", requestMessages)
                .put("max_tokens", 1_000)
            val request = Request.Builder()
                .url(endpoint.ifBlank { "https://api.openai.com/v1/chat/completions" })
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body.toString().toRequestBody(jsonType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("HTTP ${response.code}")
                }
                val responseBody = response.body?.string().orEmpty()
                JSONObject(responseBody)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()
                    .ifBlank { error("接口没有返回有效内容") }
            }
        }
    }

    private const val CHAT_SYSTEM_PROMPT = """
        你是“知否研习”，一位从事中国哲学、宗教学与传统文化研究的研究生导师，熟悉《周易》《道德经》《心经》、道教史、佛教思想史与相关学术研究，也理解传统道士的文化实践视角。
        与用户自然对话，优先澄清问题，再给出有依据、可理解的分析。严格区分经典原文、学术解释、宗教信仰与民俗经验；不伪造经文、出处、历史人物观点或学术共识。引用经典时尽量注明篇章，无法确认原文时明确说明是意译。
        尊重不同宗教与无宗教立场，不制造恐惧，不宣称能确定预测未来，不诱导迷信消费。涉及医疗、法律、金融或心理危机时，只提供一般文化讨论并建议寻求合格专业人士。
        默认使用简体中文，用户使用英文时可以用英文或中英双语回答。回答应专业、克制、清楚，不堆砌玄虚术语。
    """
}

internal object AiInterpreter {
    private const val INITIAL_OUTPUT_TOKENS = 2_400
    private const val CONTINUATION_OUTPUT_TOKENS = 1_200
    private const val MAX_CONTINUATIONS = 2
    private const val COMPLETION_MARKER = "【解读完成】"

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    private data class CompletionResponse(
        val content: String,
        val finishReason: String,
    )

    suspend fun interpret(
        endpoint: String,
        apiKey: String,
        model: String,
        reading: FortuneReading,
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val resolvedModel = model.ifBlank { "gpt-4o-mini" }
            val messages = JSONArray()
                .put(JSONObject()
                        .put("role", "system")
                        .put(
                            "content",
                            if (reading.kind == "铜钱卦") {
                                "你是知否运势的《周易》铜钱卦解读助手。先在内部静默校验投掷值、爻位、本卦、动爻、变卦和经典取用结果，不得自行改卦或更换取用规则，也不要向用户展示校验步骤或重复六爻数据。用户提供了明确问题时，问题本身是回答主轴，经典文本是分析依据；回答的大部分篇幅必须用于解释该问题的现实条件、矛盾、风险、选择和行动，不能停留在泛泛讲解经典。用户未提供文字问题时，不得猜测其心中默念的内容，只做一般卦义、自省方向和行动提示。只有输入中“经典原文”与“《彖》《象》上下文”里的文字可以加引号作为逐字引文；其他解释必须明确写成现代释义，不得伪造经文、出处、王弼、孔颖达、朱熹或现代译者观点。用中文回答，保持克制、具体、可执行，不宣称确定未来，不做医疗、法律、金融结论。"
                            } else {
                                "你是知否运势的占卜解读助手。用中文回答，保持克制、具体、可执行。不要宣称确定未来，不做医疗、法律、金融结论。"
                            },
                        )
                    )
                .put(JSONObject()
                    .put("role", "user")
                    .put("content", buildAiPrompt(reading))
                )

            val combined = StringBuilder()
            var completion = requestCompletion(
                endpoint = endpoint,
                apiKey = apiKey,
                model = resolvedModel,
                messages = messages,
                maxTokens = INITIAL_OUTPUT_TOKENS,
            )
            combined.append(completion.content)

            var continuationCount = 0
            while (needsContinuation(completion.finishReason, combined.toString()) && continuationCount < MAX_CONTINUATIONS) {
                messages
                    .put(JSONObject().put("role", "assistant").put("content", completion.content))
                    .put(
                        JSONObject()
                            .put("role", "user")
                            .put(
                                "content",
                                "上一段输出因接口长度限制中断。请严格从最后一个字符之后继续，只补全尚未完成的部分，不要重写或概括已经输出的内容；完成后在最后一行输出$COMPLETION_MARKER。",
                            )
                    )
                completion = requestCompletion(
                    endpoint = endpoint,
                    apiKey = apiKey,
                    model = resolvedModel,
                    messages = messages,
                    maxTokens = CONTINUATION_OUTPUT_TOKENS,
                )
                combined.append(completion.content)
                continuationCount++
            }

            if (needsContinuation(completion.finishReason, combined.toString())) {
                error("AI 返回内容多次达到长度上限，请重试或选择支持更长输出的模型")
            }
            Result.success(stripCompletionMarker(combined.toString()))
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private fun requestCompletion(
        endpoint: String,
        apiKey: String,
        model: String,
        messages: JSONArray,
        maxTokens: Int,
    ): CompletionResponse {
        val body = JSONObject()
            .put("model", model)
            .put("temperature", 0.45)
            .put("messages", messages)
            .put("max_tokens", maxTokens)
        val request = Request.Builder()
            .url(endpoint.ifBlank { "https://api.openai.com/v1/chat/completions" })
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body.toString().toRequestBody(jsonType))
            .build()

        return client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) error("HTTP ${response.code}")
            val choice = JSONObject(responseBody)
                .getJSONArray("choices")
                .getJSONObject(0)
            val content = choice
                .getJSONObject("message")
                .getString("content")
                .trim()
                .ifBlank { error("接口没有返回有效内容") }
            CompletionResponse(
                content = content,
                finishReason = choice.optString("finish_reason", ""),
            )
        }
    }

    internal fun needsContinuation(finishReason: String, content: String): Boolean {
        val normalizedReason = finishReason.trim().lowercase().takeUnless { it == "null" }.orEmpty()
        val reachedTokenLimit = normalizedReason in setOf("length", "max_tokens", "max_output_tokens", "token_limit")
        val providerDidNotReportReason = normalizedReason.isBlank()
        return reachedTokenLimit || (providerDidNotReportReason && COMPLETION_MARKER !in content)
    }

    internal fun stripCompletionMarker(content: String): String = content
        .replace(COMPLETION_MARKER, "")
        .trim()
        .ifBlank { error("接口没有返回有效内容") }

    internal fun buildAiPrompt(reading: FortuneReading): String {
        if (reading.kind == "铜钱卦" && reading.coinLines.size == 6) {
            val lineDetails = reading.coinLines
                .sortedBy { it.position }
                .joinToString("\n") { line ->
                    val coins = line.coinValues.joinToString("、") { value ->
                        if (value == 3) "阳面(3)" else "阴面(2)"
                    }
                    val transformed = if (line.isMoving) {
                        if (line.transformedIsYang) "变为阳爻" else "变为阴爻"
                    } else {
                        "不变"
                    }
                    "${coinLinePositionLabel(line.position)}：$coins；${line.combinationLabel}；合计${line.value}，${line.typeLabel}${if (line.isMoving) "（动）" else "（静）"}，$transformed"
                }
            val movingLines = reading.coinLines
                .filter { it.isMoving }
                .joinToString("、") { coinLinePositionLabel(it.position) }
                .ifBlank { "无（六爻皆静）" }
            val classicReferences = reading.classicReferences.joinToString("\n") { reference ->
                val lineLabel = if (reference.linePosition in 1..6) {
                    "，${coinLinePositionLabel(reference.linePosition)}"
                } else {
                    ""
                }
                buildString {
                    append("- ${if (reference.isPrimary) "主要依据" else "参看"}：第${reference.hexagramNumber}卦${reference.hexagramName}${reference.hexagramGlyph}，${reference.textType}$lineLabel：${reference.text}")
                    if (reference.commentary.isNotBlank()) {
                        append("\n  对应《象》注：${reference.commentary}")
                    }
                }
            }.ifBlank { "- 旧记录未保存经典取用文本；不得自行补写原文" }
            val classicContexts = listOfNotNull(reading.primaryClassic, reading.transformedClassic)
                .distinctBy { it.number }
                .joinToString("\n\n") { context ->
                    """第${context.number}卦 ${context.name}${context.glyph}
卦辞：${context.judgment}
${context.tuan}
${context.image}"""
                }
                .ifBlank { "旧记录未保存《彖》《象》上下文" }
            val questionGuidance = if (reading.question.isBlank()) {
                """用户没有提供文字问题，可能选择在心中默念。
                不得猜测、复述或虚构用户心中的问题。请给出可适用于当前阶段的一般卦义、自省方向和低风险行动建议。""".trimIndent()
            } else {
                """用户明确提出的问题是本次解读的核心：${reading.question}
                开头直接回应这个问题。把卦辞、爻辞和变化结构逐项映射到问题中的现实对象、条件、阻力、时机与可选行动；避免只讲抽象卦义后附一句通用建议。""".trimIndent()
            }
            val outputStructure = if (reading.question.isBlank()) {
                """1. 整体提示：用2至4句说明当前结构与可能的变化方向
                2. 经典依据：只列本次真正取用的核心经文并作简短现代释义
                3. 自省方向：给出3个用户可自行对应内心所问的问题
                4. 行动建议：给出3条低风险、可验证、可撤回的做法
                5. 解读边界：用1句话说明这是传统文化解释而非确定预测""".trimIndent()
            } else {
                """1. 针对所问：开门见山回答用户的问题，用条件式语言说明倾向、关键条件和主要风险
                2. 经典依据：精简引用本次主要依据与必要的参看依据，并说明它们如何对应用户的问题
                3. 现实分析：具体分析问题中的对象、约束、时机、取舍和可能变化，不泛泛堆砌吉凶术语
                4. 行动建议：给出3条紧扣该问题、现实中可验证且可撤回的做法
                5. 解读边界：用1句话说明这是传统文化解释而非确定预测""".trimIndent()
            }
            return """
                请依据下面完整、已核验的三枚铜钱起卦记录与应用指定的经典取用结果做详细解读。

                解读任务：
                $questionGuidance

                固定规则：阳面记3，阴面记2；第一次投掷为最下方初爻，依次向上；6老阴、7少阳、8少阴、9老阳；6和9翻转。

                六次投掷（实际顺序：初爻至上爻）：
                $lineDetails

                本卦：${reading.primaryHexagram}
                动爻：$movingLines
                变卦：${reading.transformedHexagram.ifBlank { "无；本卦不变" }}

                采用的变占框架：${reading.classicMethod.ifBlank { "旧记录未保存" }}
                本次取用规则：${reading.classicRule.ifBlank { "旧记录未保存" }}

                经典原文（这是本次唯一允许逐字引用的取用文本）：
                $classicReferences

                《彖》《象》上下文（用于义理校核，不得张冠李戴）：
                $classicContexts

                后台校验要求：
                - 写作前在内部核对六爻、本卦、动爻、变卦与经典取用是否一致。
                - 校验过程不属于用户需要阅读的内容，禁止输出“起卦复核”“卦象复核”“数据复核”等段落，也不要逐条重复六次投掷。
                - 若确实发现矛盾，只输出“本次卦象数据无法通过校验，请重新起卦”，不要继续解读。

                输出结构：
                $outputStructure

                篇幅与完整性要求：全文控制在700至1100个汉字；各部分都要完整写完，若篇幅紧张应删减重复释义，不得省略现实分析、行动建议或解读边界。最后另起一行输出$COMPLETION_MARKER，该标记只用于应用确认内容完整。

                若所给经典上下文不足以支持某个判断，请直接说明证据不足，不得用记忆补造引文。不要声称采用了未提供的某一现代译本。
            """.trimIndent()
        }
        return """
            请对下面的占卜结果做解释：
            类型：${reading.kind}
            问题：${reading.question.ifBlank { "未填写具体问题" }}
            标题：${reading.title}
            本地解释：${reading.body}
            本地建议：${reading.advice}

            输出结构：
            1. 局面判断：2-3 句
            2. 关键提醒：3 条短句
            3. 行动建议：3 条具体做法
            4. 今日宜忌：宜/忌各 2 条

            篇幅与完整性要求：全文控制在400至700个汉字，各部分必须完整写完，避免重复解释。最后另起一行输出$COMPLETION_MARKER，该标记只用于应用确认内容完整。
        """.trimIndent()
    }
}

class FortuneOracle {
    private val random = SecureRandom()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun tossCoinLine(position: Int): CoinLineResult {
        require(position in 1..6)
        return CoinLineResult(
            position = position,
            coinValues = List(3) { if (random.nextBoolean()) 3 else 2 },
        )
    }

    fun coin(question: String, lines: List<CoinLineResult>): FortuneReading {
        val orderedLines = lines.sortedBy { it.position }
        require(orderedLines.map { it.position } == (1..6).toList())
        val main = orderedLines.map { it.isYang }
        val changed = orderedLines.map { it.transformedIsYang }
        val moving = orderedLines.filter { it.isMoving }.map { it.position }
        val mainHex = hexagram(main)
        val changedHex = hexagram(changed)
        val mainClassic = ZhouyiClassics.fromLines(main)
        val changedClassic = ZhouyiClassics.fromLines(changed)
        val classicSelection = ZhouyiSelectionRules.select(
            primary = mainClassic,
            transformed = changedClassic,
            movingPositions = moving,
        )
        val mainLabel = "${mainHex.name} ${mainHex.symbol}"
        val changedLabel = "${changedHex.name} ${changedHex.symbol}"
        val movingText = if (moving.isEmpty()) {
            "六爻皆静，本次没有动爻"
        } else {
            moving.joinToString("、") { "${coinLinePositionLabel(it)}动" }
        }
        val localStructure = when (moving.size) {
            0 -> "本地基础解读以本卦所呈现的当前结构为主，不额外推断变化方向。"
            1 -> "变化集中在一个层级，优先观察该动爻对应的问题环节。"
            2 -> "两个层级同时变化，需要辨别二者的先后关系与相互影响。"
            3 -> "动静各半，本卦的当前条件与变卦的变化方向应结合观察。"
            else -> "动爻较多，局面处于高变化状态；与其追求单一结论，更适合先确认边界和可逆步骤。"
        }
        val movingFocus = orderedLines
            .filter { it.isMoving }
            .joinToString("；") { line ->
                "${coinLinePositionLabel(line.position)}对应${coinLinePositionFocus(line.position)}"
            }
        return FortuneReading(
            id = System.currentTimeMillis(),
            kind = "铜钱卦",
            title = if (moving.isEmpty()) mainLabel else "${mainHex.name} → ${changedHex.name}",
            question = question.trim(),
            body = buildString {
                append("本卦为$mainLabel，$movingText。")
                if (moving.isNotEmpty()) append("动爻翻转后得到变卦$changedLabel。")
                append("${classicSelection.ruleSummary}")
                append(localStructure)
                if (movingFocus.isNotBlank()) append("结构位置：$movingFocus。")
            },
            advice = "本地结构提示不替代完整义理解读。请把经文放回所问事项与现实条件中核验，不把传统占筮表述为确定预言。",
            score = 0,
            timeLabel = LocalDateTime.now().format(formatter),
            coinLines = orderedLines,
            primaryHexagram = mainLabel,
            transformedHexagram = if (moving.isEmpty()) "" else changedLabel,
            classicMethod = "取用规则：${ZhouyiClassics.SELECTION_METHOD}。这是传统变占框架之一，不代表唯一断法。",
            classicRule = classicSelection.ruleSummary,
            classicSource = "经文来源：${ZhouyiClassics.SOURCE_LABEL}；原文保留繁体字形。",
            classicReferences = classicSelection.references,
            primaryClassic = ZhouyiSelectionRules.context(mainClassic),
            transformedClassic = if (moving.isEmpty()) null else ZhouyiSelectionRules.context(changedClassic),
        )
    }

    fun answerBook(question: String): FortuneReading {
        val entries = AnswerBook.entries
        val item = entries[random.nextInt(entries.size)]
        return FortuneReading(
            id = System.currentTimeMillis(),
            kind = "答案之书",
            title = "第%03d页".format(item.page),
            question = question.trim(),
            body = item.answer,
            advice = item.advice,
            score = 60 + random.nextInt(36),
            timeLabel = LocalDateTime.now().format(formatter),
        )
    }

    private fun hexagram(lines: List<Boolean>): Hexagram {
        val lower = trigram(lines[0], lines[1], lines[2])
        val upper = trigram(lines[3], lines[4], lines[5])
        val name = hexNames[upper.name to lower.name] ?: "${upper.name}${lower.name}"
        return Hexagram(name, upper.symbol + lower.symbol)
    }

    private fun trigram(bottom: Boolean, middle: Boolean, top: Boolean): Trigram =
        trigrams.getValue(listOf(bottom, middle, top))

}

private data class Hexagram(val name: String, val symbol: String)
private data class Trigram(val name: String, val symbol: String)
private val trigrams = mapOf(
    listOf(true, true, true) to Trigram("乾", "☰"),
    listOf(true, true, false) to Trigram("兑", "☱"),
    listOf(true, false, true) to Trigram("离", "☲"),
    listOf(true, false, false) to Trigram("震", "☳"),
    listOf(false, true, true) to Trigram("巽", "☴"),
    listOf(false, true, false) to Trigram("坎", "☵"),
    listOf(false, false, true) to Trigram("艮", "☶"),
    listOf(false, false, false) to Trigram("坤", "☷"),
)

private val hexNames = mapOf(
    ("乾" to "乾") to "乾为天", ("乾" to "兑") to "天泽履", ("乾" to "离") to "天火同人", ("乾" to "震") to "天雷无妄",
    ("乾" to "巽") to "天风姤", ("乾" to "坎") to "天水讼", ("乾" to "艮") to "天山遁", ("乾" to "坤") to "天地否",
    ("兑" to "乾") to "泽天夬", ("兑" to "兑") to "兑为泽", ("兑" to "离") to "泽火革", ("兑" to "震") to "泽雷随",
    ("兑" to "巽") to "泽风大过", ("兑" to "坎") to "泽水困", ("兑" to "艮") to "泽山咸", ("兑" to "坤") to "泽地萃",
    ("离" to "乾") to "火天大有", ("离" to "兑") to "火泽睽", ("离" to "离") to "离为火", ("离" to "震") to "火雷噬嗑",
    ("离" to "巽") to "火风鼎", ("离" to "坎") to "火水未济", ("离" to "艮") to "火山旅", ("离" to "坤") to "火地晋",
    ("震" to "乾") to "雷天大壮", ("震" to "兑") to "雷泽归妹", ("震" to "离") to "雷火丰", ("震" to "震") to "震为雷",
    ("震" to "巽") to "雷风恒", ("震" to "坎") to "雷水解", ("震" to "艮") to "雷山小过", ("震" to "坤") to "雷地豫",
    ("巽" to "乾") to "风天小畜", ("巽" to "兑") to "风泽中孚", ("巽" to "离") to "风火家人", ("巽" to "震") to "风雷益",
    ("巽" to "巽") to "巽为风", ("巽" to "坎") to "风水涣", ("巽" to "艮") to "风山渐", ("巽" to "坤") to "风地观",
    ("坎" to "乾") to "水天需", ("坎" to "兑") to "水泽节", ("坎" to "离") to "水火既济", ("坎" to "震") to "水雷屯",
    ("坎" to "巽") to "水风井", ("坎" to "坎") to "坎为水", ("坎" to "艮") to "水山蹇", ("坎" to "坤") to "水地比",
    ("艮" to "乾") to "山天大畜", ("艮" to "兑") to "山泽损", ("艮" to "离") to "山火贲", ("艮" to "震") to "山雷颐",
    ("艮" to "巽") to "山风蛊", ("艮" to "坎") to "山水蒙", ("艮" to "艮") to "艮为山", ("艮" to "坤") to "山地剥",
    ("坤" to "乾") to "地天泰", ("坤" to "兑") to "地泽临", ("坤" to "离") to "地火明夷", ("坤" to "震") to "地雷复",
    ("坤" to "巽") to "地风升", ("坤" to "坎") to "地水师", ("坤" to "艮") to "地山谦", ("坤" to "坤") to "坤为地",
)
