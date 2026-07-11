package com.zhifou.fortune

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
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
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.VerticalPager
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import java.security.SecureRandom
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
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
    Schedule("日程", Icons.AutoMirrored.Filled.EventNote),
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
                Tab.Oracle -> OracleScreen(vm, offlineRecognizer)
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
    val today = vm.todayReading
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FortuneDial(score = today.score)
        ReadingCard(reading = today)
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

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun OracleScreen(vm: FortuneViewModel, offlineRecognizer: OfflineSpeechRecognizer) {
    val C = LocalFortunePalette.current
    val context = LocalContext.current
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
    val suggestedQuestions = remember {
        listOf(
            "我今天适合做重要决定吗？",
            "这段关系下一步应该怎么走？",
            "近期的事业机会在哪里？",
            "现在应该坚持还是改变？",
        )
    }
    val oracleTimeline = vm.oracleTimeline
    val timelineState = rememberLazyListState()
    val selectedKeys = remember { androidx.compose.runtime.mutableStateMapOf<String, Boolean>() }
    val selectionMode = selectedKeys.isNotEmpty()
    var confirmDeleteSelection by remember { mutableStateOf(false) }

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
                vm.sendChatMessage()
            }
        }
    }

    LaunchedEffect(oracleTimeline.size, vm.chatSending) {
        if (oracleTimeline.isNotEmpty()) {
            timelineState.animateScrollToItem(timelineState.layoutInfo.totalItemsCount.coerceAtLeast(1) - 1)
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            LazyColumn(
                state = timelineState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (oracleTimeline.isEmpty()) {
                    item(key = "suggestions") {
                        Column(
                            modifier = Modifier.padding(top = 48.dp, bottom = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                "今天想问些什么？",
                                color = C.textMain,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            suggestedQuestions.forEach { question ->
                                Surface(
                                    onClick = { vm.question = question },
                                    color = C.panelAlt,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, C.line),
                                ) {
                                    Text(
                                        question,
                                        color = C.textMain,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    )
                                }
                            }
                        }
                    }
                }
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
                    onClick = { vm.castCoins() },
                    interactionSource = coinsSrc,
                    modifier = Modifier.weight(1f).then(pressScaleModifier(coinsSrc)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = C.textMain),
                    border = BorderStroke(1.dp, C.line),
                ) {
                    Icon(Icons.Default.Casino, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("三币起卦")
                }
                val bookSrc = remember { MutableInteractionSource() }
                OutlinedButton(
                    onClick = { vm.drawAnswerBook() },
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
            Box(modifier = Modifier.fillMaxWidth().height(104.dp)) {
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
                        placeholder = { Text("输入问题，或按住说话") },
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
                CoinToolPreview()
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
private fun CoinToolPreview() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoinVisual(side = CoinSide.Flower, rotation = 0f, rolling = false, modifier = Modifier.size(86.dp))
        CoinVisual(side = CoinSide.Character, rotation = 0f, rolling = false, modifier = Modifier.size(86.dp))
    }
}

@Composable
private fun CoinScreen(onBack: () -> Unit) {
    val C = LocalFortunePalette.current
    val scope = rememberCoroutineScope()
    val rotations = remember { List(10) { Animatable(0f) } }
    val secureRandom = remember { SecureRandom() }
    var coinCount by remember { mutableStateOf(1) }
    var results by remember { mutableStateOf(listOf(CoinSide.Flower)) }
    var flipping by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("等待抛掷") }
    var history by remember { mutableStateOf<List<String>>(emptyList()) }

    fun tossCoins() {
        if (flipping) return
        val tosses = List(coinCount) {
            if (secureRandom.nextBoolean()) CoinSide.Flower else CoinSide.Character
        }
        results = tosses
        scope.launch {
            flipping = true
            val jobs = tosses.indices.map { index ->
                launch {
                    val normalized = rotations[index].value % 360f
                    rotations[index].snapTo(normalized)
                    val fullTurns = 5 + secureRandom.nextInt(3)
                    val finalHalfTurn = if (tosses[index] == CoinSide.Character) 180f else 0f
                    rotations[index].animateTo(
                        targetValue = normalized + fullTurns * 360f + finalHalfTurn,
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
            flipping = false
        }
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
            "每枚硬币独立随机，花面与字面各有 50% 概率。",
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
    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                rotationX = if (rolling) kotlin.math.sin(Math.toRadians(rotation.toDouble())).toFloat() * 7f else 0f
                cameraDistance = 12f * density.density
            },
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
            side.label.removeSuffix("面"),
            color = C.ink,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
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
        spinJob = scope.launch {
            // 两段线性减速，速度连续：前 3s v0->v1，后 1s v1->0。方向由 dir 决定。
            val v0 = dir * 10f * 360f      // 初始角速度（度/秒，带方向）
            val v1 = dir * 1.5f * 360f     // t=3s 时的角速度（度/秒，带方向）
            val t1 = 3f
            val t2 = 4f
            var pos = angle.value
            var v = v0
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
            }
            spinning = false
            winnerIndex = winnerFromAngle(angle.value, n)
            vm.wheelSegments.getOrNull(winnerIndex)?.let { vm.addWheelHistory(it) }
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
        spinJob = scope.launch {
            val decel = 1800f            // 减速度（度/秒²）
            val duration = speed.absoluteValue / decel
            val dir = if (speed >= 0f) 1f else -1f
            var pos = angle.value
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
            }
            spinning = false
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
    val physicsWorld = remember { DicePhysicsWorld() }
    var diceCount by remember { mutableStateOf(1) }
    var diceSides by remember { mutableStateOf(6) }
    var faces by remember { mutableStateOf(listOf(1)) }
    var resultText by remember { mutableStateOf("1枚6面骰：[1] = 1") }
    var rolling by remember { mutableStateOf(false) }
    var cupClosed by remember { mutableStateOf(false) }
    var canReveal by remember { mutableStateOf(true) }
    var dragTotal by remember { mutableStateOf(0f) }
    var lastShakeAt by remember { mutableStateOf(0L) }
    var history by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(physicsWorld) {
        while (true) {
            physicsWorld.step(1f / 60f)
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

    fun startCupRoll() {
        if (rolling) return
        val roll = rollDice(diceCount, diceSides)
        scope.launch {
            rolling = true
            cupClosed = true
            canReveal = false
            physicsWorld.startRoll(roll)
            playShakeSound()
            delay(1_650)
            faces = roll.rolls
            resultText = roll.displayText
            history = (listOf(roll.displayText) + history).take(6)
            canReveal = true
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
                    scope.launch { startCupRoll() }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sensorManager?.unregisterListener(listener) }
    }

    val displayedFaces = if (cupClosed) listOf(faces.firstOrNull() ?: 1) else faces

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
                        .pointerInput(rolling, canReveal) {
                            detectVerticalDragGestures(
                                onDragStart = { dragTotal = 0f },
                                onVerticalDrag = { _, dragAmount -> dragTotal += dragAmount },
                                onDragEnd = {
                                    if (dragTotal < -52f && !rolling && canReveal) {
                                        cupClosed = false
                                    } else if (dragTotal > 52f && !rolling) {
                                        cupClosed = true
                                    }
                                },
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    DiceStage(
                        faces = displayedFaces,
                        sides = diceSides,
                        physicsWorld = physicsWorld,
                        rolling = rolling,
                    )
                    if (cupClosed) {
                        DiceCupOverlay(rolling = rolling, canReveal = canReveal)
                    }
                }
                Text(
                    when {
                        rolling -> "骰盅摇动中"
                        cupClosed && canReveal -> "向上滑开骰盅查看结果"
                        else -> resultText
                    },
                    color = if (rolling || cupClosed) C.textSub else C.gold,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Button(
                    onClick = { startCupRoll() },
                    enabled = !rolling,
                    colors = ButtonDefaults.buttonColors(containerColor = C.gold, contentColor = if (C.isLight) Color(0xFFFAF7F0) else C.ink),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Casino, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (rolling) "摇动中" else "合盅摇骰")
                }
                Text(
                    "可下滑合上骰盅，点击按钮或摇动手机开始；声音结束后上滑打开。",
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
private fun DiceCupOverlay(
    rolling: Boolean,
    canReveal: Boolean,
) {
    val C = LocalFortunePalette.current
    val shakeAnimation = rememberInfiniteTransition(label = "diceCupShake")
    val animatedShake by shakeAnimation.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 70, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "diceCupShakeOffset",
    )
    val shakeOffset = if (rolling) animatedShake else 0f
    // 拟物骰盅配色：深色模式用深木纹，浅色模式用浅木/暖灰，与各自背景协调。
    val trayColor = if (C.isLight) Color(0xFFC8B79A) else Color(0xFF2A2420)
    val cupTop = if (C.isLight) Color(0xFFD8C7A8) else Color(0xFF776041)
    val cupBottom = if (C.isLight) Color(0xFFB59E78) else Color(0xFF362A20)
    val cupShadow = if (C.isLight) Color(0xFF9C8A66).copy(alpha = 0.66f) else Color(0xAA111318)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .graphicsLayer(translationX = shakeOffset),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val trayHeight = size.height * 0.18f
            drawRoundRect(
                color = trayColor,
                topLeft = Offset(size.width * 0.08f, size.height * 0.74f),
                size = Size(size.width * 0.84f, trayHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx(), 24.dp.toPx()),
            )
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(cupTop, cupBottom),
                    startY = size.height * 0.08f,
                    endY = size.height * 0.86f,
                ),
                topLeft = Offset(size.width * 0.16f, size.height * 0.08f),
                size = Size(size.width * 0.68f, size.height * 0.72f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(34.dp.toPx(), 34.dp.toPx()),
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x66FFFFFF), Color.Transparent),
                    start = Offset(size.width * 0.22f, size.height * 0.12f),
                    end = Offset(size.width * 0.58f, size.height * 0.72f),
                ),
                topLeft = Offset(size.width * 0.2f, size.height * 0.13f),
                size = Size(size.width * 0.14f, size.height * 0.58f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx(), 22.dp.toPx()),
            )
            drawOval(
                color = C.gold.copy(alpha = 0.86f),
                topLeft = Offset(size.width * 0.38f, size.height * 0.02f),
                size = Size(size.width * 0.24f, size.height * 0.12f),
            )
            drawRoundRect(
                color = cupShadow,
                topLeft = Offset(size.width * 0.22f, size.height * 0.82f),
                size = Size(size.width * 0.56f, size.height * 0.12f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx(), 18.dp.toPx()),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                when {
                    rolling -> "摇动中"
                    canReveal -> "上滑查看"
                    else -> "骰盅已合上"
                },
                color = C.textMain,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                if (rolling) "沙沙声结束后打开" else "下滑可再次合上",
                color = C.textSub,
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
    val C = LocalFortunePalette.current
    val mesh = remember(sides) { buildPhysicsMesh(sides) }
    val dice = world.renderState

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
    ) {
        drawRoundRect(
            color = C.panelAlt.copy(alpha = 0.76f),
            topLeft = Offset(size.width * 0.05f, size.height * 0.08f),
            size = Size(size.width * 0.9f, size.height * 0.82f),
            cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
        )
        drawRoundRect(
            color = C.line.copy(alpha = 0.42f),
            topLeft = Offset(size.width * 0.08f, size.height * 0.12f),
            size = Size(size.width * 0.84f, size.height * 0.74f),
            cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
            style = Stroke(width = 1.dp.toPx()),
        )

        val bodies = if (dice.isEmpty()) {
            fallbackFaces.mapIndexed { index, face ->
                PhysicsDieRender(
                    position = Vec3((index - (fallbackFaces.size - 1) / 2f) * 0.45f, 0.34f, 0f),
                    rotation = Quat.identity,
                    result = face,
                )
            }
        } else {
            dice
        }
        val triangleBatch = ArrayList<PhysicsRenderTriangle>(bodies.size * mesh.size)
        bodies.forEach { die ->
            mesh.forEach { triangle ->
                val a = die.position + die.rotation.rotate(triangle.a)
                val b = die.position + die.rotation.rotate(triangle.b)
                val c = die.position + die.rotation.rotate(triangle.c)
                val pa = projectDicePoint(a, size)
                val pb = projectDicePoint(b, size)
                val pc = projectDicePoint(c, size)
                val area = (pb.x - pa.x) * (pc.y - pa.y) - (pb.y - pa.y) * (pc.x - pa.x)
                val normal = (b - a).cross(c - a).normalized()
                val shade = (0.66f + normal.dot(Vec3(-0.32f, 0.78f, 0.55f)) * 0.34f).coerceIn(0.34f, 1f)
                triangleBatch += PhysicsRenderTriangle(
                    a = pa,
                    b = pb,
                    c = pc,
                    depth = (pa.depth + pb.depth + pc.depth) / 3f,
                    area = area,
                    color = shadeColor(diceColorForSides(sides, C.gold), shade),
                )
            }
            val shadow = projectDicePoint(Vec3(die.position.x, 0.01f, die.position.z), size)
            drawOval(
                color = Color.Black.copy(alpha = 0.28f),
                topLeft = Offset(shadow.x - 34.dp.toPx(), shadow.y - 7.dp.toPx()),
                size = Size(68.dp.toPx(), 14.dp.toPx()),
            )
        }

        triangleBatch
            .asSequence()
            .filter { kotlin.math.abs(it.area) > 0.4f }
            .sortedByDescending { it.depth }
            .forEach { triangle ->
                val path = Path().apply {
                    moveTo(triangle.a.x, triangle.a.y)
                    lineTo(triangle.b.x, triangle.b.y)
                    lineTo(triangle.c.x, triangle.c.y)
                    close()
                }
                drawPath(path, triangle.color)
                drawPath(path, Color.White.copy(alpha = 0.12f), style = Stroke(width = 0.8.dp.toPx()))
            }

        if (!rolling) {
            bodies.forEach { die ->
                val labelPoint = projectDicePoint(
                    die.position + die.rotation.rotate(Vec3(0f, 0.38f, 0f)),
                    size,
                )
                drawDiceLabel(die.result.toString(), labelPoint, C)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDiceLabel(
    value: String,
    point: DiceScreenPoint,
    palette: FortunePalette,
) {
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = if (palette.isLight) android.graphics.Color.WHITE else android.graphics.Color.BLACK
            textSize = 24.dp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        canvas.nativeCanvas.drawText(value, point.x, point.y + paint.textSize * 0.35f, paint)
    }
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
    }
}

private data class PhysicsDieRender(
    val position: Vec3,
    val rotation: Quat,
    val result: Int,
)

private data class PhysicsTriangle3d(val a: Vec3, val b: Vec3, val c: Vec3)

private data class DiceScreenPoint(val x: Float, val y: Float, val depth: Float)

private data class PhysicsRenderTriangle(
    val a: DiceScreenPoint,
    val b: DiceScreenPoint,
    val c: DiceScreenPoint,
    val depth: Float,
    val area: Float,
    val color: Color,
)

private class DicePhysicsWorld {
    private data class Body(
        val position: Vec3,
        var velocity: Vec3,
        var rotation: Quat,
        var angularVelocity: Vec3,
        val result: Int,
        var radius: Float,
    )

    private val bodies = ArrayList<Body>(6)
    private var elapsed = 0f
    private var active = false

    var renderState by mutableStateOf<List<PhysicsDieRender>>(emptyList())
        private set

    fun startRoll(roll: DiceRollResult) {
        bodies.clear()
        elapsed = 0f
        active = true
        roll.rolls.forEachIndexed { index, result ->
            val row = index / 3
            val column = index % 3
            bodies += Body(
                position = Vec3((column - 1) * 0.38f, 0.56f + row * 0.03f, (row - 0.5f) * 0.28f),
                velocity = Vec3(
                    (Random.nextFloat() - 0.5f) * 4.8f,
                    3.6f + Random.nextFloat() * 2.2f,
                    (Random.nextFloat() - 0.5f) * 3.8f,
                ),
                rotation = Quat(
                    Random.nextFloat(),
                    Random.nextFloat(),
                    Random.nextFloat(),
                    Random.nextFloat(),
                ).normalized(),
                angularVelocity = Vec3(
                    (Random.nextFloat() - 0.5f) * 22f,
                    (Random.nextFloat() - 0.5f) * 22f,
                    (Random.nextFloat() - 0.5f) * 22f,
                ),
                result = result,
                radius = if (roll.sides == 4) 0.31f else 0.34f,
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
        resolveBodyCollisions()
        if (elapsed > 1.45f) {
            bodies.forEach { body ->
                body.velocity = body.velocity * 0.82f
                body.angularVelocity = body.angularVelocity * 0.78f
            }
        }
        if (elapsed > 1.62f) {
            bodies.forEach { body ->
                body.velocity = Vec3(0f, 0f, 0f)
                body.angularVelocity = Vec3(0f, 0f, 0f)
            }
            active = false
        }
        publish()
    }

    fun stop() {
        active = false
        bodies.clear()
        renderState = emptyList()
    }

    private fun resolveBounds(body: Body) {
        val floor = body.radius * 0.86f
        if (body.position.y < floor) {
            body.position.y = floor
            if (body.velocity.y < 0f) body.velocity.y = -body.velocity.y * 0.52f
            body.velocity.x *= 0.91f
            body.velocity.z *= 0.91f
        }
        val limits = listOf(
            body.position.x to 1.04f,
            body.position.z to 0.63f,
        )
        if (body.position.x < -limits[0].second) {
            body.position.x = -limits[0].second
            body.velocity.x = kotlin.math.abs(body.velocity.x) * 0.55f
        } else if (body.position.x > limits[0].second) {
            body.position.x = limits[0].second
            body.velocity.x = -kotlin.math.abs(body.velocity.x) * 0.55f
        }
        if (body.position.z < -limits[1].second) {
            body.position.z = -limits[1].second
            body.velocity.z = kotlin.math.abs(body.velocity.z) * 0.55f
        } else if (body.position.z > limits[1].second) {
            body.position.z = limits[1].second
            body.velocity.z = -kotlin.math.abs(body.velocity.z) * 0.55f
        }
    }

    private fun resolveBodyCollisions() {
        for (firstIndex in 0 until bodies.size) {
            for (secondIndex in firstIndex + 1 until bodies.size) {
                val first = bodies[firstIndex]
                val second = bodies[secondIndex]
                val delta = second.position - first.position
                val distance = delta.length().coerceAtLeast(0.0001f)
                val minimumDistance = first.radius + second.radius
                if (distance >= minimumDistance) continue
                val normal = delta * (1f / distance)
                val correction = (minimumDistance - distance) * 0.51f
                first.position.x -= normal.x * correction
                first.position.y -= normal.y * correction
                first.position.z -= normal.z * correction
                second.position.x += normal.x * correction
                second.position.y += normal.y * correction
                second.position.z += normal.z * correction
                val relativeVelocity = (second.velocity - first.velocity).dot(normal)
                if (relativeVelocity < 0f) {
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

private fun projectDicePoint(point: Vec3, canvasSize: Size): DiceScreenPoint {
    val cameraDistance = 4.1f
    val depth = (cameraDistance - point.z).coerceAtLeast(1.2f)
    val scale = canvasSize.minDimension * 0.86f
    return DiceScreenPoint(
        x = canvasSize.width * 0.5f + point.x / depth * scale,
        y = canvasSize.height * 0.61f - (point.y + point.z * 0.16f) / depth * scale,
        depth = depth,
    )
}

private fun buildPhysicsMesh(sides: Int): List<PhysicsTriangle3d> {
    val radius = 0.31f
    return when (sides) {
        4 -> {
            val top = Vec3(0f, radius, 0f)
            val bottom = Vec3(0f, -radius, 0f)
            val base = listOf(
                Vec3(-radius, -radius * 0.34f, -radius * 0.58f),
                Vec3(radius, -radius * 0.34f, -radius * 0.58f),
                Vec3(0f, -radius * 0.34f, radius * 0.68f),
            )
            listOf(
                PhysicsTriangle3d(top, base[0], base[1]),
                PhysicsTriangle3d(top, base[1], base[2]),
                PhysicsTriangle3d(top, base[2], base[0]),
                PhysicsTriangle3d(bottom, base[1], base[0]),
            )
        }
        6 -> cubePhysicsMesh(radius)
        8 -> octahedronPhysicsMesh(radius)
        10 -> bipyramidPhysicsMesh(radius, 5)
        20 -> icosahedronPhysicsMesh(radius)
        else -> lathePhysicsMesh(radius, 6)
    }
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

private fun bipyramidPhysicsMesh(radius: Float, segments: Int): List<PhysicsTriangle3d> {
    val top = Vec3(0f, radius, 0f)
    val bottom = Vec3(0f, -radius, 0f)
    val ring = List(segments) { index ->
        val angle = 2f * Math.PI.toFloat() * index / segments
        Vec3(kotlin.math.cos(angle) * radius * 0.88f, 0f, kotlin.math.sin(angle) * radius * 0.88f)
    }
    return ring.indices.flatMap { index ->
        val next = ring[(index + 1) % ring.size]
        listOf(PhysicsTriangle3d(top, ring[index], next), PhysicsTriangle3d(bottom, next, ring[index]))
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

@Composable
private fun ScheduleScreen(vm: FortuneViewModel) {
    val C = LocalFortunePalette.current
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    val selectedItems = remember(vm.scheduleItems, selectedDate) {
        vm.scheduleItems.filter { it.date == selectedDate.toString() }
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
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = C.panel),
            border = BorderStroke(1.dp, C.line),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "记录 ${selectedDate.monthValue}月${selectedDate.dayOfMonth}日",
                    color = C.textMain,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                OutlinedTextField(
                    value = vm.scheduleTitle,
                    onValueChange = { vm.scheduleTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("事项") },
                    placeholder = { Text("例如：周五前确认方案") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = vm.scheduleNote,
                    onValueChange = { vm.scheduleNote = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("备注") },
                    placeholder = { Text("补充地点、时间或准备事项") },
                    minLines = 2,
                )
                Button(
                    onClick = { vm.addScheduleItem(selectedDate) },
                    enabled = vm.scheduleTitle.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = C.gold, contentColor = if (C.isLight) Color(0xFFFAF7F0) else Color(0xFF111318)),
                ) {
                    Icon(Icons.AutoMirrored.Filled.EventNote, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("保存到日程")
                }
            }
        }

        Text(
            "${selectedDate.monthValue}月${selectedDate.dayOfMonth}日日程",
            color = C.textMain,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (selectedItems.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = C.panelAlt),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("当日还没有日程。", color = C.textSub, modifier = Modifier.padding(16.dp))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                selectedItems.forEach { item ->
                    ScheduleItemCard(
                        item = item,
                        onToggle = { vm.toggleScheduleItem(item.id) },
                        onDelete = { vm.deleteScheduleItem(item.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarPanel(
    scheduleItems: List<ScheduleItem>,
    selectedDate: LocalDate,
    onSelectedDateChange: (LocalDate) -> Unit,
) {
    val C = LocalFortunePalette.current
    val today = remember { LocalDate.now() }
    val animationScope = rememberCoroutineScope()
    var visibleMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var picker by remember { mutableStateOf<CalendarPicker?>(null) }
    val centerPage = 6000
    val pagerState = rememberPagerState(initialPage = centerPage) { 12001 }
    val scheduledDates = remember(scheduleItems) { scheduleItems.mapTo(hashSetOf()) { it.date } }
    // 进页时预计算当月及前后各两月，翻页大概率命中缓存。
    LaunchedEffect(Unit) {
        listOf(-2, -1, 1, 2, 0).forEach { delta ->
            prefetchMonthInfo(YearMonth.from(today).plusMonths(delta.toLong()))
        }
    }
    // 翻页后，预计算新当月前后各两月，保持缓存领先于滑动。
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val base = YearMonth.from(today).plusMonths((page - centerPage).toLong())
            listOf(-2, -1, 1, 2).forEach { delta ->
                prefetchMonthInfo(base.plusMonths(delta.toLong()))
            }
        }
    }
    val selectedInfo = remember(selectedDate) { calendarDateInfo(selectedDate) }
    val showToday = visibleMonth != YearMonth.from(today) || selectedDate != today

    fun showMonth(month: YearMonth) {
        val safeYear = month.year.coerceIn(CALENDAR_MIN_YEAR, CALENDAR_MAX_YEAR)
        val safeMonth = YearMonth.of(safeYear, month.monthValue)
        visibleMonth = safeMonth
        onSelectedDateChange(safeMonth.atDay(selectedDate.dayOfMonth.coerceAtMost(safeMonth.lengthOfMonth())))
        val monthsBetween = ChronoUnit.MONTHS.between(YearMonth.from(today), safeMonth)
        val targetPage = (centerPage + monthsBetween).toInt().coerceIn(0, 12000)
        animationScope.launch { pagerState.animateScrollToPage(targetPage) }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = C.panel),
        border = BorderStroke(1.dp, C.line),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("今天", color = C.textSub, style = MaterialTheme.typography.labelLarge)
                Text(
                    formatFullCalendarDate(today),
                    color = C.textMain,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
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
                            pagerState.animateScrollToPage(centerPage)
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
            val yearDragScope = rememberCoroutineScope()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(392.dp)
                    .clipToBounds()
                    .pointerInput(visibleMonth) {
                        var dragX = 0f
                        detectDragGestures(
                            onDragStart = { dragX = 0f },
                            onDrag = { change, amount ->
                                if (kotlin.math.abs(amount.x) > kotlin.math.abs(amount.y)) {
                                    change.consume()
                                    dragX += amount.x
                                }
                            },
                            onDragEnd = {
                                if (kotlin.math.abs(dragX) > 72.dp.toPx()) {
                                    val delta = if (dragX < 0f) 12 else -12
                                    yearDragScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + delta)
                                    }
                                }
                            },
                            onDragCancel = { dragX = 0f },
                        )
                    },
            ) {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 0,
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
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            DateDetail(info = selectedInfo, scheduleItems = scheduleItems.filter { it.date == selectedDate.toString() })
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

private val weekLabels = listOf("一", "二", "三", "四", "五", "六", "日")

@Composable
private fun CalendarMonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    scheduledDates: Set<String>,
    onSelectedDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val C = LocalFortunePalette.current
    // 命中缓存则同步返回（0ms），未命中则后台算（~250ms）。
    val days by produceState<List<CalendarDateInfo>>(initialValue = monthInfoCache.get(month) ?: emptyList(), month) {
        value = withContext(Dispatchers.Default) { computeMonthInfo(month) }
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
        if (days.isEmpty()) {
            calendarCells(month).chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    week.forEach { date ->
                        CalendarDayCell(
                            info = CalendarDateInfo(date, "", "", "", "", "", "", emptyList()),
                            visibleMonth = month,
                            selectedDate = selectedDate,
                            today = today,
                            hasSchedule = date.toString() in scheduledDates,
                            onClick = { onSelectedDateChange(date) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        } else {
            days.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    week.forEach { info ->
                        CalendarDayCell(
                            info = info,
                            visibleMonth = month,
                            selectedDate = selectedDate,
                            today = today,
                            hasSchedule = info.date.toString() in scheduledDates,
                            onClick = { onSelectedDateChange(info.date) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    info: CalendarDateInfo,
    visibleMonth: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    hasSchedule: Boolean,
    onClick: () -> Unit,
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
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

private fun calendarCells(month: YearMonth): List<LocalDate> {
    val first = month.atDay(1)
    val start = first.minusDays((first.dayOfWeek.value - 1).toLong())
    return List(42) { start.plusDays(it.toLong()) }
}

// 按月缓存农历/节气计算结果，避免翻页时反复计算（lunar 库单月 ~250ms）。
private val monthInfoCache = android.util.LruCache<YearMonth, List<CalendarDateInfo>>(64)

private fun computeMonthInfo(month: YearMonth): List<CalendarDateInfo> {
    monthInfoCache.get(month)?.let { return it }
    val result = calendarCells(month).map(::calendarDateInfo)
    monthInfoCache.put(month, result)
    return result
}

private fun prefetchMonthInfo(month: YearMonth) {
    if (monthInfoCache.get(month) != null) return
    CoroutineScope(Dispatchers.Default).launch { computeMonthInfo(month) }
}

// 菱形缓存范围：以 center(年,月) 为中心，包含本年全年、前/后年 center月±3、前前/后后年 center月±1。
private fun diamondMonths(center: YearMonth): List<YearMonth> {
    val result = LinkedHashSet<YearMonth>()
    // 本年全年
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
        SettingsEntryRow(icon = Icons.Default.Settings, title = "设置", subtitle = "昵称、AI 解读与对话、主题", onClick = onOpenSettings)
        Card(
            colors = CardDefaults.cardColors(containerColor = C.panel),
            border = BorderStroke(1.dp, C.line),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("个人资料", color = C.textMain, fontWeight = FontWeight.SemiBold)
                Text("昵称", color = C.textSub, style = MaterialTheme.typography.labelMedium)
                Text(vm.nickname.ifBlank { "未设置" }, color = C.textMain)
                Text("生日或长期关键词", color = C.textSub, style = MaterialTheme.typography.labelMedium)
                Text(vm.birthHint.ifBlank { "未设置" }, color = C.textMain)
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
private fun SubScreenHeader(title: String, onBack: () -> Unit, actions: @Composable () -> Unit) {
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
        OutlinedTextField(
            value = vm.nickname,
            onValueChange = { vm.updateNickname(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("昵称") },
        )
        OutlinedTextField(
            value = vm.birthHint,
            onValueChange = { vm.updateBirthHint(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("生日或长期关键词") },
            placeholder = { Text("用于稳定生成每日运势，可留空") },
        )
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
            Text(reading.body, color = C.textMain, style = MaterialTheme.typography.bodyLarge)
            Text(reading.advice, color = C.gold, style = MaterialTheme.typography.bodyMedium)
            if (reading.aiStatus.isNotBlank()) {
                Text(reading.aiStatus, color = C.textSub, style = MaterialTheme.typography.bodyMedium)
            }
            if (reading.aiInterpretation.isNotBlank()) {
                Surface(color = C.panelAlt, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, C.line)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("AI 解读", color = C.mint, fontWeight = FontWeight.SemiBold)
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
                Text("今日能量", color = C.textMain, style = MaterialTheme.typography.titleMedium)
                Text("结合日期与个人关键词生成，适合每天打开一次。", color = C.textSub)
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

    init { ensureCalendarCache() }

    private var calendarCacheWarmed = false

    // 把持久化的菱形缓存载入内存；若缓存年份与当前年不符（每月1号/跨年），后台重建。
    private fun ensureCalendarCache() {
        val now = LocalDate.now()
        val center = YearMonth.of(now.year, now.monthValue)
        // 先把已有持久化缓存载入内存，命中即免计算。
        val cachedYear = repo.loadCalendarCacheYear()
        val cached = repo.loadCalendarCache()
        cached.forEach { (key, cells) ->
            runCatching { YearMonth.parse(key) }.getOrNull()?.let { monthInfoCache.put(it, cells) }
        }
        calendarCacheWarmed = true
        if (cachedYear != now.year || cached.isEmpty()) {
            rebuildDiamondCache(center)
        }
    }

    private fun rebuildDiamondCache(center: YearMonth) {
        viewModelScope.launch(Dispatchers.Default) {
            val months = diamondMonths(center)
            val map = HashMap<String, List<CalendarDateInfo>>()
            months.forEach { month ->
                // 每月串行计算，避免抢占后台线程影响体验；计算结果即时入内存缓存。
                val cells = computeMonthInfo(month)
                monthInfoCache.put(month, cells)
                map[month.toString()] = cells
                // 主动让出，平衡占用
                kotlinx.coroutines.yield()
            }
            repo.saveCalendarCache(center.year, map)
        }
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
    var scheduleItems by mutableStateOf(repo.loadScheduleItems())
        private set
    var wheelSegments by mutableStateOf(repo.loadWheelSegments())
        private set
    var wheelHistory by mutableStateOf(repo.loadWheelHistory())
        private set
    var nickname by mutableStateOf(repo.nickname)
        private set
    var birthHint by mutableStateOf(repo.birthHint)
        private set
    var themeMode by mutableStateOf(repo.themeMode)
        private set
    var todayReading by mutableStateOf(oracle.today(nickname, birthHint))
        private set
    var scheduleTitle by mutableStateOf("")
    var scheduleNote by mutableStateOf("")
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
        save(oracle.coin(question))
        question = ""
    }

    fun drawAnswerBook() {
        save(oracle.answerBook(question))
        question = ""
    }

    fun refreshToday() {
        todayReading = oracle.today(nickname, birthHint)
    }

    fun updateNickname(value: String) {
        nickname = value
        repo.nickname = value
        refreshToday()
    }

    fun updateThemeMode(value: ThemeMode) {
        themeMode = value
        repo.themeMode = value
    }

    fun updateBirthHint(value: String) {
        birthHint = value
        repo.birthHint = value
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

    fun addScheduleItem(date: LocalDate) {
        val title = scheduleTitle.trim()
        if (title.isBlank()) return
        val item = ScheduleItem(
            id = System.currentTimeMillis(),
            title = title,
            note = scheduleNote.trim(),
            date = date.toString(),
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
        )
        scheduleItems = repo.saveScheduleItems(listOf(item) + scheduleItems)
        scheduleTitle = ""
        scheduleNote = ""
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
)

data class ScheduleItem(
    val id: Long,
    val title: String,
    val note: String,
    val date: String,
    val createdAt: String,
    val done: Boolean = false,
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

    var nickname: String
        get() = prefs.getString("nickname", "") ?: ""
        set(value) = prefs.edit().putString("nickname", value).apply()

    var themeMode: ThemeMode
        get() = runCatching { ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.System.name) ?: ThemeMode.System.name) }.getOrDefault(ThemeMode.System)
        set(value) = prefs.edit().putString("theme_mode", value.name).apply()

    var birthHint: String
        get() = prefs.getString("birth_hint", "") ?: ""
        set(value) = prefs.edit().putString("birth_hint", value).apply()

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

    // 菱形日历缓存的持久化：{"year":2026,"months":{"2026-06":[{...cell},...]}}
    fun loadCalendarCacheYear(): Int {
        return prefs.getInt("calendar_cache_year", 0)
    }

    internal fun loadCalendarCache(): Map<String, List<CalendarDateInfo>> {
        val raw = prefs.getString("calendar_cache", "{}") ?: "{}"
        return runCatching {
            val root = JSONObject(raw)
            val months = root.optJSONObject("months") ?: return@runCatching emptyMap()
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
            result
        }.getOrDefault(emptyMap())
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
        prefs.edit().putInt("calendar_cache_year", year).putString("calendar_cache", root.toString()).apply()
    }
}

private fun FortuneReading.toJson(): JSONObject = JSONObject()
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
)

private fun ScheduleItem.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("title", title)
    .put("note", note)
    .put("date", date)
    .put("createdAt", createdAt)
    .put("done", done)

private fun JSONObject.toScheduleItem(): ScheduleItem = ScheduleItem(
    id = optLong("id"),
    title = optString("title"),
    note = optString("note"),
    date = optString("date").ifBlank {
        optString("createdAt").take(10).takeIf { value -> runCatching { LocalDate.parse(value) }.isSuccess }
            ?: LocalDate.now().toString()
    },
    createdAt = optString("createdAt"),
    done = optBoolean("done", false),
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

private object AiInterpreter {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    suspend fun interpret(
        endpoint: String,
        apiKey: String,
        model: String,
        reading: FortuneReading,
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject()
                .put("model", model.ifBlank { "gpt-4o-mini" })
                .put("temperature", 0.45)
                .put("messages", JSONArray()
                    .put(JSONObject()
                        .put("role", "system")
                        .put(
                            "content",
                            "你是知否运势的占卜解读助手。用中文回答，保持克制、具体、可执行。不要宣称确定未来，不做医疗、法律、金融结论。",
                        )
                    )
                    .put(JSONObject()
                        .put("role", "user")
                        .put("content", buildAiPrompt(reading))
                    )
                )
                .put("max_tokens", 900)

            val request = Request.Builder()
                .url(endpoint.ifBlank { "https://api.openai.com/v1/chat/completions" })
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body.toString().toRequestBody(jsonType))
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IllegalStateException("HTTP ${response.code}"))
                }
                val content = JSONObject(responseBody)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()
                Result.success(content)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private fun buildAiPrompt(reading: FortuneReading): String {
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
        """.trimIndent()
    }
}

class FortuneOracle {
    private val random = SecureRandom()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun today(nickname: String, birthHint: String): FortuneReading {
        val date = LocalDate.now()
        val seed = stableIndex("${date}|$nickname|$birthHint", dailyBodies.size)
        val score = 58 + stableIndex("${date}|score|$nickname|$birthHint", 39)
        return FortuneReading(
            id = date.toEpochDay(),
            kind = "运势",
            title = if (nickname.isBlank()) "今日运势" else "$nickname 的今日运势",
            question = "",
            body = dailyBodies[seed],
            advice = dailyAdvice[stableIndex("$seed|$score", dailyAdvice.size)],
            score = score,
            timeLabel = date.toString(),
        )
    }

    fun coin(question: String): FortuneReading {
        val lines = List(6) { tossLine() }
        val main = lines.map { it == 7 || it == 9 }
        val changed = lines.map { if (it == 6) true else if (it == 9) false else it == 7 }
        val moving = lines.mapIndexedNotNull { index, value -> if (value == 6 || value == 9) index + 1 else null }
        val mainHex = hexagram(main)
        val changedHex = hexagram(changed)
        val movingText = if (moving.isEmpty()) "六爻皆静" else moving.joinToString("、") { "第${it}爻动" }
        return FortuneReading(
            id = System.currentTimeMillis(),
            kind = "铜钱卦",
            title = "${mainHex.name} ${mainHex.symbol}",
            question = question.trim(),
            body = "本卦为${mainHex.name}，$movingText。变卦为${changedHex.name}。当前局面重在看清主次：先稳住最关键的一件事，再处理旁支变化。",
            advice = "建议：今日先做低风险验证，避免一次性承诺全部资源。",
            score = 72 + random.nextInt(18),
            timeLabel = LocalDateTime.now().format(formatter),
        )
    }

    fun answerBook(question: String): FortuneReading {
        val item = answers[random.nextInt(answers.size)]
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

    private fun tossLine(): Int {
        var sum = 0
        repeat(3) { sum += if (random.nextBoolean()) 3 else 2 }
        return sum
    }

    private fun hexagram(lines: List<Boolean>): Hexagram {
        val lower = trigram(lines[0], lines[1], lines[2])
        val upper = trigram(lines[3], lines[4], lines[5])
        val name = hexNames[upper.name to lower.name] ?: "${upper.name}${lower.name}"
        return Hexagram(name, upper.symbol + lower.symbol)
    }

    private fun trigram(bottom: Boolean, middle: Boolean, top: Boolean): Trigram =
        trigrams.getValue(listOf(bottom, middle, top))

    private fun stableIndex(value: String, size: Int): Int = value.hashCode().absoluteValue % size
}

private data class Hexagram(val name: String, val symbol: String)
private data class Trigram(val name: String, val symbol: String)
private data class Answer(val page: Int, val answer: String, val advice: String)

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

private val dailyBodies = listOf(
    "今天适合把混乱的事情重新排队。先处理最能改变局面的那一步，其余事项保持观察。",
    "外部变化会比较多，但真正重要的是你的节奏。不要被临时消息牵着走。",
    "今天的机会来自一个不起眼的细节。多检查一次记录、数据或对话，会减少后续阻力。",
    "适合修复关系和补齐承诺。表达要短，行动要实，效果会比解释更好。",
    "能量偏向收拢。减少新承诺，整理已有资源，会比强行扩张更有价值。",
    "今天宜主动，但不宜冒进。先发出明确试探，再根据反馈调整。",
)

private val dailyAdvice = listOf(
    "把今天的目标压缩成一件可以完成的事。",
    "重要回复延迟十分钟再发。",
    "先确认事实，再判断态度。",
    "保留一个退出选项。",
    "把模糊担心写成三个具体问题。",
    "不要让焦虑替你安排优先级。",
)

private val answers = listOf(
    Answer(1, "先停一下，答案会在安静后出现。", "不要立刻行动，给自己十分钟。"),
    Answer(2, "这件事值得再确认一次。", "问清关键条件，再做决定。"),
    Answer(3, "可以开始，但不要一次押上全部。", "先做一个小规模尝试。"),
    Answer(4, "你已经知道答案，只是在等勇气。", "把最真实的选择写下来。"),
    Answer(5, "此刻不适合硬碰硬。", "换一种更柔和的表达方式。"),
    Answer(6, "机会是真的，风险也是真的。", "列出最坏情况和应对方案。"),
    Answer(7, "别让焦虑替你做决定。", "等情绪下降后再回复。"),
    Answer(8, "答案藏在你不愿面对的那一点里。", "先处理最回避的问题。"),
    Answer(9, "继续推进，但放慢速度。", "今天只完成最关键的一步。"),
    Answer(10, "现在需要的是边界，不是解释。", "明确说出你的底线。"),
    Answer(11, "这不是结束，而是调整方向。", "保留资源，重新规划路线。"),
    Answer(12, "有人能帮你，但你要先开口。", "向可信的人请求具体帮助。"),
    Answer(13, "不要为了合群牺牲判断。", "按事实而不是气氛决定。"),
    Answer(14, "最简单的办法可能就是对的。", "删掉多余步骤，直接处理核心。"),
    Answer(15, "暂时不要公开。", "先内部准备，等稳定再说。"),
    Answer(16, "你需要更多信息。", "再问三个具体问题。"),
    Answer(17, "答案偏向肯定，但要留后手。", "推进时保留退出选项。"),
    Answer(18, "不要把沉默误认为拒绝。", "给对方一点反应时间。"),
    Answer(19, "这件事会比想象中慢。", "把时间预期拉长一倍。"),
    Answer(20, "换个入口，阻力会小很多。", "从最容易被接受的部分开始。"),
)
