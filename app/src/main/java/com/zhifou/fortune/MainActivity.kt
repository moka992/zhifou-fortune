package com.zhifou.fortune

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.SystemClock
import android.hardware.SensorEventListener
import android.hardware.SensorEvent
import android.hardware.Sensor
import android.os.Bundle
import android.view.ViewConfiguration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.drawscope.clipPath
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.currentStateAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.withContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.CompletableDeferred
import com.nlf.calendar.Solar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.SecureRandom
import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
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

private const val COIN_TOSS_REVEAL_DURATION_MS = 520

enum class ThemeMode { Dark, Light, System }

private fun ComponentActivity.configureEdgeToEdge(dark: Boolean) {
    val transparent = android.graphics.Color.TRANSPARENT
    val style = if (dark) {
        SystemBarStyle.dark(transparent)
    } else {
        SystemBarStyle.light(transparent, transparent)
    }
    enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.isNavigationBarContrastEnforced = false
    }
}

@Composable
private fun pressScaleModifier(interaction: MutableInteractionSource): Modifier = Modifier.fortunePress(interaction)

@Composable
private fun sendButtonFeedbackModifier(
    interaction: MutableInteractionSource,
    enabled: Boolean,
): Modifier {
    return Modifier.fortunePress(interaction, enabled = enabled, depth = 0.9f)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = FortuneRepository(applicationContext)
        val initialDark = when (repo.themeMode) {
            ThemeMode.Dark -> true
            ThemeMode.Light -> false
            ThemeMode.System -> (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        }
        configureEdgeToEdge(initialDark)
        setContent {
            val vm: FortuneViewModel = viewModel()
            val resolvedDark = when (vm.themeMode) {
                ThemeMode.Dark -> true
                ThemeMode.Light -> false
                ThemeMode.System -> isSystemInDarkTheme()
            }
            SideEffect { configureEdgeToEdge(resolvedDark) }
            ZhiFouTheme(darkTheme = resolvedDark) {
                val glassLightController = rememberGlassLightController(vm.glassMotionEnabled)
                val appHaptics = rememberAppHaptics(vm.hapticsEnabled)
                CompositionLocalProvider(
                    LocalGlassLightController provides glassLightController,
                    LocalAppHaptics provides appHaptics,
                ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    FortuneAppMd3(vm)
                }
                }
            }
        }
    }
}

internal enum class Tab(val label: String, val icon: ImageVector) {
    Home("首页", Icons.Outlined.Home),
    Schedule("日历", Icons.Outlined.CalendarMonth),
    Oracle("占卜", Icons.Default.AutoAwesome),
    Tools("小工具", Icons.Outlined.GridView),
    Mine("我的", Icons.Outlined.PersonOutline),
}

@Stable
private class ScheduleNavigationState(
    pageName: String = SchedulePage.Calendar.name,
    editorReturnPageName: String = SchedulePage.Calendar.name,
    activeItemId: Long? = null,
    detailDateText: String? = null,
) {
    var pageName by mutableStateOf(pageName)
    var editorReturnPageName by mutableStateOf(editorReturnPageName)
    var activeItemId by mutableStateOf(activeItemId)
    var detailDateText by mutableStateOf(detailDateText)
}

private val ScheduleNavigationStateSaver = Saver<ScheduleNavigationState, Bundle>(
    save = { state ->
        Bundle().apply {
            putString("page", state.pageName)
            putString("editorReturn", state.editorReturnPageName)
            state.activeItemId?.let {
                putBoolean("hasActiveItem", true)
                putLong("activeItem", it)
            }
            putString("detailDate", state.detailDateText)
        }
    },
    restore = { bundle ->
        ScheduleNavigationState(
            pageName = bundle.getString("page") ?: SchedulePage.Calendar.name,
            editorReturnPageName = bundle.getString("editorReturn") ?: SchedulePage.Calendar.name,
            activeItemId = if (bundle.getBoolean("hasActiveItem")) bundle.getLong("activeItem") else null,
            detailDateText = bundle.getString("detailDate"),
        )
    },
)

@Composable
private fun rememberScheduleNavigationState(): ScheduleNavigationState = rememberSaveable(
    saver = ScheduleNavigationStateSaver,
) { ScheduleNavigationState() }

internal enum class FortuneWindowWidth { Compact, Medium, Expanded }

@Immutable
internal data class FortuneLayoutMetrics(
    val windowWidth: FortuneWindowWidth,
    val compactWidth: Boolean,
    val compactHeight: Boolean,
    val shortWide: Boolean = false,
    val contentMaxWidth: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val sectionSpacing: Dp,
    val cardPadding: Dp,
    val bottomBarHeight: Dp,
    val navigationBarHeight: Dp,
    val oracleButtonSize: Dp,
    val calendarCellHeight: Dp,
)

internal val LocalFortuneLayout = staticCompositionLocalOf {
    FortuneLayoutMetrics(
        windowWidth = FortuneWindowWidth.Compact,
        compactWidth = false,
        compactHeight = false,
        contentMaxWidth = 720.dp,
        horizontalPadding = 20.dp,
        verticalPadding = 20.dp,
        sectionSpacing = 16.dp,
        cardPadding = 16.dp,
        bottomBarHeight = 112.dp,
        navigationBarHeight = 76.dp,
        oracleButtonSize = 58.dp,
        calendarCellHeight = 58.dp,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberFortuneLayoutMetrics(): FortuneLayoutMetrics {
    val windowSize = currentWindowDpSize()
    val fontScale = LocalDensity.current.fontScale
    val windowWidth = when {
        windowSize.width < 600.dp -> FortuneWindowWidth.Compact
        windowSize.width < 840.dp -> FortuneWindowWidth.Medium
        else -> FortuneWindowWidth.Expanded
    }
    val compactWidth = windowSize.width < 380.dp
    val compactHeight = windowSize.height < 700.dp
    val shortWide = isShortWideWindow(windowSize.width.value, windowSize.height.value)
    return remember(windowSize, windowWidth, compactWidth, compactHeight, fontScale) {
        FortuneLayoutMetrics(
            windowWidth = windowWidth,
            compactWidth = compactWidth,
            compactHeight = compactHeight,
            shortWide = shortWide,
            contentMaxWidth = if (shortWide) 1120.dp else if (compactHeight) 720.dp else when (windowWidth) {
                FortuneWindowWidth.Compact -> 720.dp
                FortuneWindowWidth.Medium -> 760.dp
                FortuneWindowWidth.Expanded -> 960.dp
            },
            horizontalPadding = when {
                shortWide -> 16.dp
                compactWidth -> 12.dp
                windowWidth == FortuneWindowWidth.Compact -> 16.dp
                windowWidth == FortuneWindowWidth.Medium -> 24.dp
                else -> 32.dp
            },
            verticalPadding = if (shortWide) 8.dp else if (compactHeight) 12.dp else 18.dp,
            sectionSpacing = if (shortWide) 10.dp else if (compactHeight) 14.dp else 20.dp,
            cardPadding = if (compactWidth) 14.dp else 18.dp,
            bottomBarHeight = if (compactHeight) 96.dp else 112.dp,
            navigationBarHeight = if (compactHeight) 68.dp else 76.dp,
            oracleButtonSize = if (compactWidth || compactHeight) 50.dp else 58.dp,
            calendarCellHeight = maxOf(when {
                shortWide -> 44.dp
                compactWidth || compactHeight -> 50.dp
                windowWidth == FortuneWindowWidth.Compact -> 58.dp
                else -> 64.dp
            }, (39f * fontScale + 12f).dp),
        )
    }
}

private const val VOICE_CANCEL_DISTANCE_DP = 140

@Composable
private fun FortuneApp(vm: FortuneViewModel = viewModel()) {
    val C = LocalFortunePalette.current
    val layout = rememberFortuneLayoutMetrics()
    val navigationBarInset = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
    val context = LocalContext.current
    val offlineRecognizer = remember(context) { OfflineSpeechRecognizer(context.applicationContext) }
    var tab by rememberTabState()
    val tabStateHolder = rememberSaveableStateHolder()
    var showDiceTool by rememberSaveable { mutableStateOf(false) }
    var showWheelTool by rememberSaveable { mutableStateOf(false) }
    var showCoinTool by rememberSaveable { mutableStateOf(false) }
    var showTarotWiki by rememberSaveable { mutableStateOf(false) }
    var showFourCardTarot by rememberSaveable { mutableStateOf(false) }
    var wheelSubPageName by rememberSaveable { mutableStateOf(WheelSubPage.Main.name) }
    var requestedScheduleDetailDate by rememberSaveable { mutableStateOf<String?>(null) }
    val scheduleNavigation = rememberScheduleNavigationState()
    val oracleTimelineState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (vm.oracleTimeline.isEmpty()) 0 else vm.oracleTimeline.size,
    )

    BackHandler(enabled = showDiceTool) { showDiceTool = false }
    BackHandler(enabled = showCoinTool) { showCoinTool = false }

    DisposableEffect(offlineRecognizer) {
        onDispose {
            Thread({ offlineRecognizer.close() }, "zhifou-asr-cleanup").start()
        }
    }

    CompositionLocalProvider(LocalFortuneLayout provides layout) {
    Scaffold(
        containerColor = C.ink,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(layout.bottomBarHeight + navigationBarInset),
            ) {
                NavigationBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .height(layout.navigationBarHeight + navigationBarInset),
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
                                    showTarotWiki = false
                                    showFourCardTarot = false
                                }
                            },
                            icon = {
                                if (item == Tab.Oracle) {
                                    Spacer(Modifier.size(28.dp))
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (tab == item) {
                                                    C.selectionFill
                                                } else {
                                                    Color.Transparent
                                                },
                                                shape = RoundedCornerShape(16.dp),
                                            )
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        FortuneIcon(item.icon, contentDescription = item.label, modifier = Modifier.size(22.dp))
                                    }
                                }
                            },
                            label = if (item == Tab.Oracle) null else ({
                                Text(item.label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                            }),
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = C.gold,
                                selectedTextColor = C.gold,
                                indicatorColor = Color.Transparent,
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
                            showWheelTool = false
                            showCoinTool = false
                            showTarotWiki = false
                            showFourCardTarot = false
                        },
                        color = C.accentFill,
                        shape = CircleShape,
                        border = if (tab == Tab.Oracle) BorderStroke(2.dp, C.mint) else null,
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .size(layout.oracleButtonSize)
                            .graphicsLayer { translationY = -2.dp.toPx() },
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            FortuneIcon(
                                Tab.Oracle.icon,
                                contentDescription = Tab.Oracle.label,
                                tint = C.onAccentFill,
                                modifier = Modifier.size(if (layout.compactWidth) 25.dp else 29.dp),
                            )
                        }
                    }
                    Text(
                        Tab.Oracle.label,
                        color = if (tab == Tab.Oracle) C.gold else C.textSub,
                        style = if (layout.compactWidth) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
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
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
            tabStateHolder.SaveableStateProvider(tab.name) {
                when (tab) {
                    Tab.Home -> HomeScreen(
                        vm = vm,
                        onOpenOracle = { tab = Tab.Oracle },
                        onOpenTodayAlmanac = { date ->
                            vm.selectScheduleDate(date)
                            requestedScheduleDetailDate = date.toString()
                            tab = Tab.Schedule
                        },
                    )
                    Tab.Oracle -> OracleScreen(vm, offlineRecognizer, oracleTimelineState)
                    Tab.Tools -> when {
                        showWheelTool -> WheelScreen(
                            vm = vm,
                            subPage = WheelSubPage.valueOf(wheelSubPageName),
                            onSubPageChange = { wheelSubPageName = it.name },
                            onBack = { showWheelTool = false },
                        )
                        showDiceTool -> DiceScreen(onBack = { showDiceTool = false })
                        showCoinTool -> CoinScreen(onBack = { showCoinTool = false })
                    showTarotWiki -> TarotWikiScreen(onBack = { showTarotWiki = false })
                    showFourCardTarot -> FourCardTarotScreen(vm, onBack = { showFourCardTarot = false })
                        else -> ToolsScreen(
                            onOpenDice = { showDiceTool = true },
                            onOpenWheel = { showWheelTool = true },
                            onOpenCoin = { showCoinTool = true },
                            onOpenTarotWiki = { showTarotWiki = true },
                        onOpenFourCardTarot = { showFourCardTarot = true },
                        )
                    }
                    Tab.Schedule -> ScheduleScreen(
                        vm = vm,
                        navigation = scheduleNavigation,
                        requestedDetailDate = requestedScheduleDetailDate,
                        onDetailRequestHandled = { requestedScheduleDetailDate = null },
                    )
                    Tab.Mine -> MineScreen(vm)
                }
            }
            }
        }
    }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun FortuneAppMd3(vm: FortuneViewModel = viewModel()) {
    val layout = rememberFortuneLayoutMetrics()
    val context = LocalContext.current
    val haptics = LocalAppHaptics.current
    val offlineRecognizer = remember(context) { OfflineSpeechRecognizer(context.applicationContext) }
    var tab by rememberTabState()
    var previousFeedbackTab by remember { mutableStateOf(tab) }
    val tabStateHolder = rememberSaveableStateHolder()
    var showDiceTool by rememberSaveable { mutableStateOf(false) }
    var showWheelTool by rememberSaveable { mutableStateOf(false) }
    var showCoinTool by rememberSaveable { mutableStateOf(false) }
    var showTarotWiki by rememberSaveable { mutableStateOf(false) }
    var showFourCardTarot by rememberSaveable { mutableStateOf(false) }
    var wheelSubPageName by rememberSaveable { mutableStateOf(WheelSubPage.Main.name) }
    var requestedScheduleDetailDate by rememberSaveable { mutableStateOf<String?>(null) }
    val scheduleNavigation = rememberScheduleNavigationState()
    val oracleTimelineState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (vm.oracleTimeline.isEmpty()) 0 else vm.oracleTimeline.size,
    )

    LaunchedEffect(tab) {
        if (previousFeedbackTab != tab) {
            previousFeedbackTab = tab
            haptics.selection()
        }
    }

    fun openDestination(destination: Tab) {
        if (tab != destination) {
            haptics.cancel()
            tab = destination
        }
        if (destination != Tab.Tools) {
            showDiceTool = false
            showWheelTool = false
            showCoinTool = false
            showTarotWiki = false
            showFourCardTarot = false
        }
    }

    val toolsScrollState = rememberScrollState()
    val toolRoute = when {
        showDiceTool -> "dice"
        showWheelTool -> "wheel/$wheelSubPageName"
        showCoinTool -> "coin"
        showTarotWiki -> "wiki"
        showFourCardTarot -> "four-card"
        else -> "catalog"
    }
    HapticPageFeedback(toolRoute, when {
        toolRoute == "catalog" -> 0
        showWheelTool && wheelSubPageName != WheelSubPage.Main.name -> 2
        else -> 1
    }, active = tab == Tab.Tools)
    BackHandler(enabled = mainBackDestination(tab) != null) {
        mainBackDestination(tab)?.let(::openDestination)
    }
    BackHandler(enabled = tab == Tab.Tools && showDiceTool) { showDiceTool = false }
    BackHandler(enabled = tab == Tab.Tools && showCoinTool) { showCoinTool = false }

    DisposableEffect(offlineRecognizer) {
        onDispose {
            Thread({ offlineRecognizer.close() }, "zhifou-asr-cleanup").start()
        }
    }

    val pageScaffold: @Composable (Boolean) -> Unit = { usePhoneBottomBar ->
        val editingOracle = tab == Tab.Oracle && WindowInsets.ime.getBottom(LocalDensity.current) > 0
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (usePhoneBottomBar && !editingOracle && !(tab == Tab.Tools && showFourCardTarot)) {
                    FortuneLiquidBottomBar(
                        selectedTab = tab,
                        onTabSelected = ::openDestination,
                    )
                }
            },
            contentWindowInsets = if (usePhoneBottomBar) WindowInsets.safeDrawing.only(
                WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
            ) else WindowInsets.safeDrawing.union(WindowInsets.mandatorySystemGestures),
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = layout.contentMaxWidth)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                ) {
                    tabStateHolder.SaveableStateProvider(tab.name) {
                        when (tab) {
                            Tab.Home -> HomeScreen(
                                vm = vm,
                                onOpenOracle = { openDestination(Tab.Oracle) },
                                onOpenTodayAlmanac = { date ->
                                    vm.selectScheduleDate(date)
                                    requestedScheduleDetailDate = date.toString()
                                    openDestination(Tab.Schedule)
                                },
                            )
                            Tab.Oracle -> OracleScreen(vm, offlineRecognizer, oracleTimelineState)
                            Tab.Tools -> when {
                                showWheelTool -> WheelScreen(
                                    vm = vm,
                                    subPage = WheelSubPage.valueOf(wheelSubPageName),
                                    onSubPageChange = { wheelSubPageName = it.name },
                                    onBack = { showWheelTool = false },
                                )
                                showDiceTool -> DiceScreen(onBack = { showDiceTool = false })
                                showCoinTool -> CoinScreen(onBack = { showCoinTool = false })
                                showTarotWiki -> TarotWikiScreen(onBack = { showTarotWiki = false })
                                showFourCardTarot -> FourCardTarotScreen(vm, onBack = { showFourCardTarot = false })
                                else -> ToolsScreenMd3(
                                    scrollState = toolsScrollState,
                                    onOpenDice = { showDiceTool = true },
                                    onOpenWheel = {
                                        wheelSubPageName = WheelSubPage.Main.name
                                        showWheelTool = true
                                    },
                                    onOpenCoin = { showCoinTool = true },
                                    onOpenTarotWiki = { showTarotWiki = true },
                                    onOpenFourCardTarot = { showFourCardTarot = true },
                                )
                            }
                            Tab.Schedule -> ScheduleScreen(
                                vm = vm,
                                navigation = scheduleNavigation,
                                requestedDetailDate = requestedScheduleDetailDate,
                                onDetailRequestHandled = { requestedScheduleDetailDate = null },
                            )
                            Tab.Mine -> MineScreen(vm)
                        }
                    }
                }
            }
        }
    }

    CompositionLocalProvider(LocalFortuneLayout provides layout) {
        GeminiAmbientBackdrop(
            modifier = Modifier.fillMaxSize(),
            backgroundStyle = vm.backgroundStyle,
            customImageUri = vm.customBackgroundUri,
        ) {
        val showSideRail = layout.windowWidth != FortuneWindowWidth.Compact
        // Keep the page at one composition location when navigation changes shape.
        Row(Modifier.fillMaxSize().then(
            if (showSideRail) Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)) else Modifier,
        )) {
            if (showSideRail) FortuneSideRail(selectedTab = tab, onTabSelected = ::openDestination)
            Box(Modifier.weight(1f)) { pageScaffold(!showSideRail) }
        }
        }
    }
}

@Composable
private fun rememberTabState() = rememberSaveable {
    mutableStateOf(Tab.Home)
}

@Composable
private fun FortuneLiquidBottomBar(selectedTab: Tab, onTabSelected: (Tab) -> Unit) {
    val layout = LocalFortuneLayout.current
    val colors = MaterialTheme.colorScheme
    val palette = LocalFortunePalette.current
    val safe = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal).asPaddingValues()
    val bottom = maxOf(
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
        WindowInsets.mandatorySystemGestures.asPaddingValues().calculateBottomPadding(),
    )
    val short = layout.compactHeight
    val barHeight = maxOf(if (short) 68.dp else 76.dp,
        ((if (short) 43f else 49f) + 18f * LocalDensity.current.fontScale + 6f).dp)
    val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    Box(Modifier.fillMaxWidth().height(barHeight + bottom + 10.dp)) {
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(barHeight + bottom).clip(shape)) {
            LiquidGlassOptics(shape = shape, tint = colors.surface, isLight = palette.isLight, modifier = Modifier.matchParentSize())
            Box(Modifier.matchParentSize().background(palette.panel.copy(alpha = 0.94f)))
            Box(Modifier.fillMaxWidth().height(0.5.dp).background(colors.outlineVariant.copy(alpha = 0.55f)))
        }
        Row(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(safe)
                .padding(bottom = bottom, start = 4.dp, end = 4.dp).height(barHeight),
            verticalAlignment = Alignment.Top,
        ) {
            Tab.entries.forEach { destination ->
                val selected = destination == selectedTab
                val oracle = destination == Tab.Oracle
                val source = remember(destination) { MutableInteractionSource() }
                val selectedScale = animateFloatAsState(if (selected) 1f else 0.96f,
                    fortuneSpatialSpec(fast = true), label = "navigation")
                val indicator by animateColorAsState(
                    when { oracle -> colors.primary; selected -> colors.primaryContainer; else -> colors.primaryContainer.copy(alpha = 0f) },
                    fortuneEffectsSpec(), label = "navigation-indicator")
                val iconColor by animateColorAsState(
                    when { oracle -> colors.onPrimary; selected -> colors.primary; else -> colors.onSurfaceVariant },
                    fortuneEffectsSpec(), label = "navigation-icon")
                Column(
                    Modifier.weight(1f).fillMaxHeight()
                        .selectable(selected = selected, interactionSource = source, indication = null,
                            role = androidx.compose.ui.semantics.Role.Tab, onClick = { onTabSelected(destination) })
                        .then(pressScaleModifier(source)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(Modifier.height(if (short) 43.dp else 49.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Surface(
                            color = indicator,
                            shape = if (oracle) CircleShape else RoundedCornerShape(16.dp),
                            modifier = (if (oracle) Modifier.offset(y = (-7).dp).requiredSize(if (short) 48.dp else 54.dp)
                                else Modifier.size(width = 48.dp, height = 32.dp))
                                .graphicsLayer { scaleX = selectedScale.value; scaleY = selectedScale.value },
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                FortuneIcon(destination.icon, null, active = selected, interactionSource = source,
                                    tint = iconColor,
                                    modifier = Modifier.size(if (oracle) 26.dp else 23.dp))
                            }
                        }
                    }
                    Text(destination.label, style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected || oracle) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) colors.primary else colors.onSurfaceVariant, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    vm: FortuneViewModel,
    onOpenOracle: () -> Unit,
    onOpenTodayAlmanac: (LocalDate) -> Unit,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val context = LocalContext.current
    val snapshot = vm.todayFortune
    val homeHaptics = LocalAppHaptics.current
    val today = snapshot.reading
    var selectedInsight by remember { mutableStateOf<DailyInsightCategory?>(null) }
    val homeListState = rememberLazyListState()
    val overviewScroll = rememberLazyListState()
    val insightsScroll = rememberLazyListState()
    fun openInsight(category: DailyInsightCategory) {
        if (vm.ensureAiReady()) {
            homeHaptics.menu(true)
            selectedInsight = category
            vm.requestDailyAiInsight(category)
        } else {
            val message = when (vm.aiConnectionStatus) {
                AiConnectionStatus.NoNetwork -> "当前没有可用网络"
                AiConnectionStatus.Checking -> "AI模型正在连接测试"
                else -> "请配置AI模型获得完整功能"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
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
    if (layout.shortWide) {
        Row(Modifier.fillMaxSize().padding(horizontal = layout.horizontalPadding, vertical = layout.verticalPadding),
            horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyColumn(Modifier.weight(1f).fillMaxWidth(), state = overviewScroll,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item(key = "home-title") { PageHeading("知否运势", snapshot.almanac.dateLabel) }
                    item(key = "today-reading") { DailyOverview(snapshot) }
                }
                HomeQuickActions(onOpenOracle = onOpenOracle,
                    onOpenTodayAlmanac = { onOpenTodayAlmanac(snapshot.almanac.date) })
            }
            LazyColumn(Modifier.weight(1f).fillMaxHeight(), state = insightsScroll,
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item(key = "insights") { SectionHeading("今日关照", "事业 · 关系 · 财务") }
                item(key = "insight-items") {
                    InsightStrip(snapshot.insights, vm.aiConnectionStatus == AiConnectionStatus.Connected, ::openInsight)
                }
                item(key = "today-almanac") {
                    DailyAlmanacPanel(snapshot.almanac, snapshot.personalizationBasis, snapshot.environmentSummary)
                }
            }
        }
    } else LazyColumn(
        state = homeListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = layout.horizontalPadding,
            vertical = layout.verticalPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(layout.sectionSpacing),
    ) {
        item(key = "home-title") {
            PageHeading("知否运势", snapshot.almanac.dateLabel) {
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
                    FortuneIcon(Icons.Default.AutoAwesome, null, tint = C.gold, modifier = Modifier.padding(12.dp).size(22.dp))
                }
            }
        }
        item(key = "today-reading") {
            DailyOverview(snapshot)
        }
        item(key = "home-actions") {
            HomeQuickActions(onOpenOracle = onOpenOracle, onOpenTodayAlmanac = { onOpenTodayAlmanac(snapshot.almanac.date) })
        }
        item(key = "insights") {
            SectionHeading("今日关照", "事业 · 关系 · 财务")
        }
        item(key = "insight-items") {
            InsightStrip(
                insights = snapshot.insights,
                aiConnected = vm.aiConnectionStatus == AiConnectionStatus.Connected,
                onTap = ::openInsight,
            )
        }
        item(key = "today-almanac") {
            DailyAlmanacPanel(
                info = snapshot.almanac,
                personalizationBasis = snapshot.personalizationBasis,
                environmentSummary = snapshot.environmentSummary,
            )
        }
    }
    selectedInsight?.let { category ->
        DailyAiInsightDialog(
            insight = snapshot.insights.first { it.category == category },
            aiContent = vm.dailyAiInsights[category],
            loading = vm.dailyAiInsightLoading == category,
            error = vm.dailyAiInsightError.takeIf { vm.dailyAiInsightErrorCategory == category }.orEmpty(),
            onRetry = { vm.requestDailyAiInsight(category, forceRetry = true) },
            onDismiss = { homeHaptics.menu(false); selectedInsight = null },
        )
    }
}

@Composable
private fun HomeQuickActions(onOpenOracle: () -> Unit, onOpenTodayAlmanac: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        val calendarSource = remember { MutableInteractionSource() }
        HomeActionButton("今日黄历", Icons.Default.CalendarMonth, false, calendarSource, onOpenTodayAlmanac,
            Modifier.weight(1f).then(pressScaleModifier(calendarSource)))
        val oracleSource = remember { MutableInteractionSource() }
        HomeActionButton("去占卜", Icons.Default.AutoAwesome, true, oracleSource, onOpenOracle,
            Modifier.weight(1f).then(pressScaleModifier(oracleSource)))
    }
}

@Composable
private fun HomeActionButton(
    label: String,
    icon: ImageVector,
    primary: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val shortWide = LocalFortuneLayout.current.shortWide
    Surface(
        onClick = onClick, interactionSource = interactionSource,
        color = if (primary) colors.primary else colors.surfaceContainerHigh,
        contentColor = if (primary) colors.onPrimary else colors.onSurface,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.heightIn(min = if (shortWide) 48.dp else 52.dp),
    ) {
        Row(
            Modifier.padding(horizontal = if (shortWide) 6.dp else 10.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
        ) {
            FortuneIcon(icon, null, modifier = Modifier.size(if (shortWide) 18.dp else 20.dp))
            Spacer(Modifier.width(if (shortWide) 4.dp else 8.dp))
            Text(label, style = if (shortWide) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge, maxLines = 1)
        }
    }
}

@Composable
private fun DailyAlmanacPanel(
    info: DailyAlmanacInfo,
    personalizationBasis: List<String>,
    environmentSummary: String?,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    GeminiGlassPanel(
        tone = GeminiGlassTone.Neutral,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(layout.cardPadding),
            verticalArrangement = Arrangement.spacedBy(if (layout.compactHeight) 10.dp else 12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    "今日黄历",
                    color = C.textMain,
                    style = if (layout.compactWidth) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    info.dateLabel,
                    color = C.textMain,
                    style = if (layout.compactWidth) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                )
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
        Text(direction, color = C.textMain, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
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

private enum class OracleTool(val label: String) {
    COIN("铜钱卦"),
    ANSWER_BOOK("答案之书"),
    TAROT("韦特塔罗"),
}

private fun OracleTool.icon(): ImageVector = when (this) {
    OracleTool.COIN -> Icons.Default.Toll
    OracleTool.ANSWER_BOOK -> Icons.Default.Book
    OracleTool.TAROT -> Icons.Default.Style
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun OracleScreen(
    vm: FortuneViewModel,
    offlineRecognizer: OfflineSpeechRecognizer,
    timelineState: LazyListState,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val context = LocalContext.current
    val sendHaptics = LocalAppHaptics.current
    if (vm.coinCasting) OracleMotionFeedback(vm.coinCastingStartedAt, OracleMotionKind.Coins)
    if (vm.tarotDrawing) OracleMotionFeedback(vm.tarotDrawingStartedAt, OracleMotionKind.Tarot)
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val voiceUiScope = rememberCoroutineScope()
    val voiceHoldThresholdMs = remember {
        (ViewConfiguration.getTapTimeout() + 100)
            .coerceAtMost(ViewConfiguration.getLongPressTimeout())
            .toLong()
    }
    val questionFocusRequester = remember { FocusRequester() }
    var modelReady by remember(offlineRecognizer, vm.cloudSpeechEnabled) {
        mutableStateOf(vm.cloudSpeechEnabled || offlineRecognizer.isPrepared)
    }
    var isRecording by remember { mutableStateOf(false) }
    var isCancelling by remember { mutableStateOf(false) }
    var textEditing by remember { mutableStateOf(false) }
    var keyboardEdited by remember { mutableStateOf(false) }
    var questionEditor by remember { mutableStateOf(TextFieldValue(vm.question, TextRange(vm.question.length))) }
    val questionValue = syncOracleEditorValue(questionEditor, vm.question)
    val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    SideEffect { if (questionEditor != questionValue) questionEditor = questionValue }
    var voiceLevel by remember { mutableStateOf(0f) }
    var questionBeforeRecording by remember { mutableStateOf("") }
    var toolMenuExpanded by remember { mutableStateOf(false) }
    var selectedToolName by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedTool = selectedToolName?.let { name ->
        OracleTool.entries.firstOrNull { it.name == name }
    }
    val oracleDate = remember { LocalDate.now() }
    val oracleDateKey = oracleDate.toString()
    val initialOraclePage = rememberSaveable(oracleDateKey) {
        if (initiallyShowOraclePrompt(
            firstVisitToday = vm.consumeDailyOraclePrompt(oracleDate),
            hasTimeline = vm.oracleTimeline.isNotEmpty(),
        )) ORACLE_INSPIRATION_PAGE else ORACLE_MESSAGES_PAGE
    }
    val oraclePagerState = rememberPagerState(initialPage = initialOraclePage) { ORACLE_PAGE_COUNT }
    HapticPagerFeedback(oraclePagerState)
    fun showOracleMessages(animated: Boolean = false) {
        voiceUiScope.launch {
            if (oraclePagerState.currentPage == ORACLE_MESSAGES_PAGE && !oraclePagerState.isScrollInProgress) return@launch
            if (animated) oraclePagerState.animateScrollToPage(ORACLE_MESSAGES_PAGE, animationSpec = tween(240, easing = FastOutSlowInEasing))
            else oraclePagerState.scrollToPage(ORACLE_MESSAGES_PAGE)
        }
    }
    val suggestedQuestions = remember(vm.todayFortune) { dailyOracleSuggestions(vm.todayFortune) }
    val oracleTimeline = vm.oracleTimeline
    val timelineFade = fortuneEffectsSpec<Float>()
    var observedFollowSnapshot by remember {
        mutableStateOf(
            OracleFollowSnapshot(
                timelineSize = oracleTimeline.size,
                chatSending = vm.chatSending,
                coinCasting = vm.coinCasting,
                coinLineCount = vm.coinCastingLines.size,
                tarotDrawing = vm.tarotDrawing,
                tarotCardCount = vm.tarotDrawingCards.size,
                tarotRevealCount = vm.tarotRevealedCount,
            )
        )
    }
    val selectedKeys = remember { androidx.compose.runtime.mutableStateMapOf<String, Boolean>() }
    val selectionMode = selectedKeys.isNotEmpty()
    var confirmDeleteSelection by remember { mutableStateOf(false) }

    LaunchedEffect(oracleDate) {
        if (vm.todayFortune.almanac.date != oracleDate) vm.refreshToday(oracleDate)
    }
    fun entryKey(entry: OracleTimelineEntry) = when (entry) {
        is OracleTimelineEntry.Reading -> "reading-${entry.reading.id}"
        is OracleTimelineEntry.Chat -> "chat-${entry.message.id}"
    }
    fun toggleSelection(entry: OracleTimelineEntry) {
        val key = entryKey(entry)
        sendHaptics.selection()
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
            if (!offlineRecognizer.isPrepared) delay(3_000L)
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
                    providerId = vm.cloudSpeechProviderId,
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
        val startResult = offlineRecognizer.start(
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
        isRecording = startResult == SpeechStartResult.Started
        if (isRecording) {
            sendHaptics.scene(HapticCue.VoiceStart)
        } else {
            startResult.message?.let(::showVoiceMessage)
        }
    }

    fun finishRecording(cancelled: Boolean) {
        if (!isRecording) return
        sendHaptics.scene(if (cancelled) HapticCue.VoiceCancel else HapticCue.VoiceStop)
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
                showOracleMessages()
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

    fun runPrimaryAction() {
        when (selectedTool) {
            OracleTool.COIN -> {
                dismissTextInput()
                showOracleMessages()
                vm.castCoins()
            }
            OracleTool.ANSWER_BOOK -> {
                dismissTextInput()
                showOracleMessages()
                vm.drawAnswerBook()
            }
            OracleTool.TAROT -> {
                dismissTextInput()
                showOracleMessages()
                vm.drawTarot()
            }
            null -> sendChatMessage()
        }
    }

    LaunchedEffect(isCancelling) {
        if (isRecording) sendHaptics.selection()
    }

    LaunchedEffect(
        oracleTimeline.size,
        vm.chatSending,
        vm.coinCasting,
        vm.coinCastingLines.size,
        vm.tarotDrawing,
        vm.tarotDrawingCards.size,
        vm.tarotRevealedCount,
    ) {
        val currentSnapshot = OracleFollowSnapshot(
            timelineSize = oracleTimeline.size,
            chatSending = vm.chatSending,
            coinCasting = vm.coinCasting,
            coinLineCount = vm.coinCastingLines.size,
            tarotDrawing = vm.tarotDrawing,
            tarotCardCount = vm.tarotDrawingCards.size,
            tarotRevealCount = vm.tarotRevealedCount,
        )
        val shouldFollowNewContent = shouldFollowOracleContent(observedFollowSnapshot, currentSnapshot)
        observedFollowSnapshot = currentSnapshot

        if (shouldFollowNewContent) {
            if (oraclePagerState.currentPage != ORACLE_MESSAGES_PAGE || oraclePagerState.isScrollInProgress) {
                oraclePagerState.scrollToPage(ORACLE_MESSAGES_PAGE)
            }
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
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { dismissTextInput() })
            }
            .padding(
                start = layout.horizontalPadding,
                end = layout.horizontalPadding,
                top = layout.verticalPadding,
                bottom = 4.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(if (layout.shortWide) 4.dp else if (layout.compactHeight) 8.dp else 12.dp),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(oraclePagerGestureRegion()),
        ) {
            val oracleBodyHeight = maxHeight
            HorizontalPager(
                state = oraclePagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = !selectionMode && !isRecording,
                key = { if (it == ORACLE_INSPIRATION_PAGE) "inspiration" else "messages" },
            ) { page ->
            if (page == ORACLE_INSPIRATION_PAGE) {
                DailyOraclePrompt(
                    questions = suggestedQuestions,
                    onQuestionClick = { question ->
                        sendHaptics.selection()
                        vm.question = question
                        keyboardEdited = true
                        dismissTextInput()
                        showOracleMessages(animated = true)
                    },
                )
            } else {
            LazyColumn(
                state = timelineState,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(selectionMode) {
                        if (selectionMode) return@pointerInput
                        awaitEachGesture {
                            val down = awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial,
                            )
                            var dragged = false
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                val delta = change.position - down.position
                                dragged = dragged || delta.getDistance() > viewConfiguration.touchSlop
                                if (!change.pressed) {
                                    if (!dragged) dismissTextInput()
                                    break
                                }
                            }
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (oracleTimeline.isEmpty() && !vm.coinCasting && !vm.tarotDrawing &&
                    !vm.chatSending && vm.chatStatus.isBlank()) {
                    item(key = "timeline-empty") {
                        Text(
                            "还没有消息",
                            color = C.textSub,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            textAlign = TextAlign.Center,
                        )
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
                            .animateItem(fadeInSpec = timelineFade, placementSpec = null, fadeOutSpec = timelineFade)
                            .combinedClickable(
                                enabled = true,
                                onLongClick = {
                                    if (!selectedKeys.containsKey(key)) {
                                        sendHaptics.selection()
                                        selectedKeys[key] = true
                                    }
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
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.34f),
                                        MaterialTheme.shapes.large,
                                    )
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.72f),
                                        MaterialTheme.shapes.large,
                                    ),
                            )
                        }
                        when (entry) {
                            is OracleTimelineEntry.Reading -> ReadingCard(entry.reading, compact = true)
                            is OracleTimelineEntry.Chat -> ChatBubble(entry.message)
                        }
                        if (selectionMode) {
                            FortuneIcon(
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
                            currentThrow = vm.coinCastingThrow,
                            startedAt = vm.coinCastingStartedAt,
                            availableHeight = oracleBodyHeight,
                        )
                    }
                }
                if (vm.tarotDrawing) {
                    item(key = "tarot-drawing") {
                        TarotDrawingProgressCard(
                            draws = vm.tarotDrawingCards,
                            revealedCount = vm.tarotRevealedCount,
                            question = vm.tarotDrawingQuestion,
                            startedAt = vm.tarotDrawingStartedAt,
                            availableHeight = oracleBodyHeight,
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
                            FortuneIcon(Icons.Default.Info, contentDescription = null, tint = C.rose, modifier = Modifier.size(14.dp))
                            Text(vm.chatStatus, color = C.rose, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                item(key = "timeline-bottom") { Spacer(Modifier.height(4.dp)) }
            }
            }
            }
            OraclePagerIndicator(
                state = oraclePagerState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp),
                enabled = !selectionMode && !isRecording,
            )
        }
        if (selectionMode) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(onClick = { exitSelection() }) {
                        FortuneIcon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("取消")
                    }
                    Text(
                        "已选 ${selectedKeys.size} 项",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    TextButton(onClick = { confirmDeleteSelection = true }) {
                        FortuneIcon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("删除", color = C.danger)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth()) {
            GeminiGlassPanel(
                tone = GeminiGlassTone.Neutral,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = if (isRecording) 0f else 1f },
            ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                FortuneAnimatedVisibility(visible = selectedTool != null && !layout.shortWide) {
                    selectedTool?.let { tool ->
                        GeminiGlassPanel(
                            tone = when (tool) {
                                OracleTool.COIN -> GeminiGlassTone.Gold
                                OracleTool.ANSWER_BOOK -> GeminiGlassTone.Teal
                                OracleTool.TAROT -> GeminiGlassTone.Rose
                            },
                            shape = CircleShape,
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 11.dp, end = 2.dp, top = 2.dp, bottom = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                FortuneIcon(
                                    imageVector = tool.icon(),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    tool.label,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                                IconButton(
                                    onClick = {
                                        sendHaptics.selection()
                                        selectedToolName = null
                                    },
                                    modifier = Modifier.size(28.dp),
                                ) {
                                    FortuneIcon(
                                        Icons.Default.Close,
                                        contentDescription = "取消${tool.label}",
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (layout.compactWidth) 8.dp else 10.dp),
                ) {
                    Box {
                        val addInteractionSource = remember { MutableInteractionSource() }
                        Surface(
                            onClick = {
                                sendHaptics.menu(!toolMenuExpanded)
                                toolMenuExpanded = !toolMenuExpanded
                            },
                            interactionSource = addInteractionSource,
                            modifier = Modifier
                                .size(if (layout.compactWidth) 44.dp else 48.dp)
                                .then(pressScaleModifier(addInteractionSource)),
                            color = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            shape = CircleShape,
                        ) {
                            GeminiGlassPanel(
                                tone = if (selectedTool == null) GeminiGlassTone.Neutral else GeminiGlassTone.Gold,
                                shape = CircleShape,
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    FortuneIcon(
                                        Icons.Default.Add,
                                        contentDescription = "选择占卜功能",
                                        replayKey = toolMenuExpanded,
                                        interactionSource = addInteractionSource,
                                        tint = if (selectedTool == null) {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        },
                                    )
                                }
                            }
                        }
                        DropdownMenu(
                            expanded = toolMenuExpanded,
                            onDismissRequest = { sendHaptics.menu(false); toolMenuExpanded = false },
                        ) {
                            OracleTool.entries.forEach { tool ->
                                val isSelected = selectedTool == tool
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            tool.label,
                                            color = if (isSelected) C.gold else C.textMain,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        )
                                    },
                                    onClick = {
                                        selectedToolName = if (isSelected) null else tool.name
                                        sendHaptics.selection()
                                        toolMenuExpanded = false
                                    },
                                    leadingIcon = {
                                        FortuneIcon(
                                            imageVector = tool.icon(),
                                            contentDescription = null,
                                            tint = if (isSelected) C.gold else C.textSub,
                                        )
                                    },
                                    trailingIcon = {
                                        if (isSelected) {
                                            FortuneIcon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "已选择",
                                                tint = C.gold,
                                                modifier = Modifier.size(18.dp),
                                            )
                                        }
                                    },
                                    modifier = Modifier.background(
                                        if (isSelected) C.gold.copy(alpha = 0.08f) else Color.Transparent,
                                    ),
                                )
                            }
                        }
                    }
                    if (layout.shortWide) selectedTool?.let { tool ->
                        OracleSelectedToolChip(tool, onClear = {
                            sendHaptics.selection()
                            selectedToolName = null
                        })
                    }
                    TextField(
                        value = questionValue,
                        onValueChange = {
                            questionEditor = it
                            vm.question = it.text
                            keyboardEdited = it.text.isNotBlank()
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
                                                if (isRecording && cancelled != isCancelling) {
                                                    sendHaptics.scene(if (cancelled) HapticCue.GestureArm else HapticCue.GestureDisarm)
                                                }
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
                        placeholder = {
                            Text(
                                if (layout.compactWidth) "输入问题，或默念" else "输入问题，或在心中默念",
                                style = if (layout.compactWidth) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        maxLines = if (layout.shortWide) 2 else if (layout.compactHeight) 3 else 5,
                        readOnly = !textEditing && !keyboardEdited,
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                    )
                    val sendSrc = remember { MutableInteractionSource() }
                    val sendEnabled = !vm.coinCasting && !vm.tarotDrawing && when (selectedTool) {
                        OracleTool.COIN -> !vm.coinCasting
                        OracleTool.ANSWER_BOOK -> true
                        OracleTool.TAROT -> !vm.tarotDrawing
                        null -> vm.question.isNotBlank() && !vm.chatSending
                    }
                    Surface(
                        onClick = {
                            sendHaptics.sendAccepted()
                            runPrimaryAction()
                        },
                        enabled = sendEnabled,
                        interactionSource = sendSrc,
                        color = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(if (layout.compactWidth) 48.dp else 52.dp)
                            .then(sendButtonFeedbackModifier(sendSrc, sendEnabled)),
                    ) {
                        GeminiGlassPanel(
                            tone = if (sendEnabled) GeminiGlassTone.Teal else GeminiGlassTone.Neutral,
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { alpha = if (sendEnabled) 1f else 0.65f },
                        ) {
                            Box(Modifier.fillMaxSize().background(if (sendEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh), contentAlignment = Alignment.Center) {
                                FortuneIcon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = selectedTool?.let { "使用${it.label}" } ?: "发送给 AI",
                                    active = sendEnabled,
                                    interactionSource = sendSrc,
                                    tint = if (sendEnabled) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                )
                            }
                        }
                    }
                }
                if (!keyboardVisible && !layout.shortWide) Text(
                    "轻触输入文字，按住输入语音",
                    color = C.textSub,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 1.dp),
                )
            }
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
private fun OracleSelectedToolChip(tool: OracleTool, onClear: () -> Unit) {
    GeminiGlassPanel(shape = CircleShape, tone = GeminiGlassTone.Neutral) {
        Row(Modifier.padding(start = 8.dp, end = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(tool.label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            IconButton(onClick = onClear, modifier = Modifier.size(28.dp)) {
                FortuneIcon(Icons.Default.Close, contentDescription = "取消${tool.label}", modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun DailyOraclePrompt(
    questions: List<String>,
    onQuestionClick: (String) -> Unit,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val today = remember { LocalDate.now() }
    val suggestionScroll = rememberScrollState()
    val suggestionTones = listOf(
        GeminiGlassTone.Teal,
        GeminiGlassTone.Gold,
        GeminiGlassTone.Rose,
        GeminiGlassTone.Neutral,
    )
    if (layout.shortWide) {
        Column(Modifier.fillMaxSize().verticalScroll(suggestionScroll), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("今天，想问些什么？", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            questions.take(4).chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { question ->
                        val source = remember(question) { MutableInteractionSource() }
                        Surface(onClick = { onQuestionClick(question) }, interactionSource = source,
                            color = Color.Transparent, shape = MaterialTheme.shapes.large,
                            modifier = Modifier.weight(1f).then(pressScaleModifier(source))) {
                            GeminiGlassPanel(tone = GeminiGlassTone.Neutral, modifier = Modifier.fillMaxWidth()) {
                            Text(question, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp))
                            }
                        }
                    }
                    repeat(2 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        return
    }
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FortuneIcon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Column(Modifier.weight(1f)) {
                Text("今日灵感", style = MaterialTheme.typography.titleSmall, maxLines = 1)
                Text(
                    "${today.monthValue}月${today.dayOfMonth}日",
                    color = C.textSub,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(suggestionScroll)
                .padding(top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(if (layout.compactHeight) 10.dp else 14.dp),
        ) {
            Text(
                "今天，\n想问些什么？",
                color = MaterialTheme.colorScheme.onBackground,
                style = if (layout.compactWidth) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            )
            questions.take(4).forEachIndexed { index, question ->
                val interactionSource = remember(question) { MutableInteractionSource() }
                Surface(
                    onClick = { onQuestionClick(question) },
                    interactionSource = interactionSource,
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(pressScaleModifier(interactionSource)),
                ) {
                    GeminiGlassPanel(
                        tone = GeminiGlassTone.Neutral,
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = if (layout.compactWidth) 12.dp else 16.dp,
                            vertical = if (layout.compactHeight) 11.dp else 14.dp,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            question,
                            style = if (layout.compactWidth) {
                                MaterialTheme.typography.bodyMedium
                            } else {
                                MaterialTheme.typography.bodyLarge
                            },
                            modifier = Modifier.weight(1f),
                        )
                        FortuneIcon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    }
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
    val tarotDrawing: Boolean = false,
    val tarotCardCount: Int = 0,
    val tarotRevealCount: Int = 0,
)

internal fun shouldFollowOracleContent(
    previous: OracleFollowSnapshot,
    current: OracleFollowSnapshot,
): Boolean =
    current.timelineSize > previous.timelineSize ||
        (current.chatSending && !previous.chatSending) ||
        (current.coinCasting && !previous.coinCasting) ||
        current.coinLineCount > previous.coinLineCount ||
        (current.tarotDrawing && !previous.tarotDrawing) ||
        current.tarotCardCount > previous.tarotCardCount ||
        current.tarotRevealCount > previous.tarotRevealCount

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
        val bubbleShape = if (isUser) {
                RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 22.dp, bottomEnd = 6.dp)
            } else {
                RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 6.dp, bottomEnd = 22.dp)
            }
        GeminiGlassPanel(
            tone = if (isUser) GeminiGlassTone.Teal else GeminiGlassTone.Neutral,
            shape = bubbleShape,
            modifier = Modifier.fillMaxWidth(if (isUser) 0.82f else 0.88f),
        ) {
            Column(
                modifier = Modifier
                    .background(bubbleColor)
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
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
    val lifecycle by androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val dots = if (!LocalReducedFortuneMotion.current && lifecycle.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
        val transition = rememberInfiniteTransition(label = "thinking")
        List(3) { i ->
            transition.animateFloat(
                initialValue = 0.3f, targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = androidx.compose.animation.core.StartOffset(i * 180),
                ), label = "dot$i",
            )
        }
    } else {
        remember { List(3) { mutableStateOf(0.65f) } }
    }
    GeminiGlassPanel(
        tone = GeminiGlassTone.Neutral,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 6.dp, bottomEnd = 22.dp),
        modifier = Modifier.fillMaxWidth(0.88f),
    ) {
        Row(
            modifier = Modifier
                .background(C.chatBubbleAssistant.copy(alpha = if (C.isLight) 0.44f else 0.24f))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dots.forEach { d ->
                Box(
                    Modifier
                        .size(8.dp)
                        .graphicsLayer { alpha = d.value }
                        .background(C.chatText, CircleShape),
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
    GeminiGlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .height(104.dp),
        tone = if (cancelling) GeminiGlassTone.Rose else GeminiGlassTone.Gold,
    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color.Transparent,
                        color.copy(alpha = if (C.isLight) 0.16f else 0.09f),
                        Color.Transparent,
                    ),
                ),
            )
            ,
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
}

@Composable
private fun ToolsScreen(
    onOpenDice: () -> Unit,
    onOpenWheel: () -> Unit,
    onOpenCoin: () -> Unit,
    onOpenTarotWiki: () -> Unit,
    onOpenFourCardTarot: () -> Unit,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = layout.horizontalPadding,
                vertical = layout.verticalPadding,
            ),
        verticalArrangement = Arrangement.spacedBy(layout.sectionSpacing),
    ) {
        Text(
            "小工具",
            color = C.textMain,
            style = if (layout.compactWidth) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Surface(
            onClick = onOpenDice,
            color = C.panel,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, C.line),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.tool_dice_refined),
                    contentDescription = "摇骰子",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (layout.compactWidth) 2.05f else 1.85f),
                )
                Column(
                    Modifier.padding(
                        start = layout.cardPadding,
                        end = layout.cardPadding,
                        bottom = layout.cardPadding,
                    ),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text("摇骰子", color = C.textMain, style = if (layout.compactWidth) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("选择骰子个数和面数，合上骰盅后摇动手机或点击按钮。", color = C.textSub, style = if (layout.compactWidth) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium)
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
                        .aspectRatio(if (layout.compactWidth) 2.05f else 1.85f),
                )
                Column(
                    Modifier.padding(
                        start = layout.cardPadding,
                        end = layout.cardPadding,
                        bottom = layout.cardPadding,
                    ),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text("转盘", color = C.textMain, style = if (layout.compactWidth) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("自定义选项，摇动手机或拨动转盘随机抽取。", color = C.textSub, style = if (layout.compactWidth) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium)
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
                    painter = painterResource(id = R.drawable.tool_coin_refined),
                    contentDescription = "抛硬币",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (layout.compactWidth) 2.05f else 1.85f),
                )
                Column(
                    Modifier.padding(
                        start = layout.cardPadding,
                        end = layout.cardPadding,
                        bottom = layout.cardPadding,
                    ),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text("抛硬币", color = C.textMain, style = if (layout.compactWidth) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("最多同时抛出 10 枚硬币，花面与字面各有 50% 概率。", color = C.textSub, style = if (layout.compactWidth) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium)
                }
            }
        }
        ToolFeatureCard(ToolFeature("四牌塔罗", "亲手抽牌 · 四牌十字阵", ToolVisual.FourTarot, onOpenFourCardTarot),
            compactCard = false)
        TarotWikiToolCard(onClick = onOpenTarotWiki)
    }
}

private data class ToolFeature(
    val title: String,
    val description: String,
    val visual: ToolVisual,
    val onClick: () -> Unit,
)

private enum class ToolVisual { Dice, Wheel, Coin, Tarot, FourTarot }

@Composable
private fun ToolsScreenMd3(
    scrollState: ScrollState,
    onOpenDice: () -> Unit,
    onOpenWheel: () -> Unit,
    onOpenCoin: () -> Unit,
    onOpenTarotWiki: () -> Unit,
    onOpenFourCardTarot: () -> Unit,
) {
    val layout = LocalFortuneLayout.current
    val fontScale = LocalDensity.current.fontScale
    BoxWithConstraints(Modifier.fillMaxSize()) {
    val columns = toolCatalogColumns((maxWidth - layout.horizontalPadding * 2).value, layout.shortWide, fontScale)
    val tools = listOf(
        ToolFeature(
            title = "摇骰子",
            description = "D4–D20 · 1–6 枚",
            visual = ToolVisual.Dice,
            onClick = onOpenDice,
        ),
        ToolFeature(
            title = "转盘",
            description = "自定义选项 · 随机选择",
            visual = ToolVisual.Wheel,
            onClick = onOpenWheel,
        ),
        ToolFeature(
            title = "抛硬币",
            description = "花面与字面 · 1–10 枚",
            visual = ToolVisual.Coin,
            onClick = onOpenCoin,
        ),
        ToolFeature(
            title = "四牌塔罗",
            description = "亲手抽牌 · 四牌十字阵",
            visual = ToolVisual.FourTarot,
            onClick = onOpenFourCardTarot,
        ),
        ToolFeature(
            title = "韦特塔罗 Wiki",
            description = "塔罗牌 · 牌义与历史",
            visual = ToolVisual.Tarot,
            onClick = onOpenTarotWiki,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = layout.horizontalPadding, vertical = layout.verticalPadding),
        verticalArrangement = Arrangement.spacedBy(layout.sectionSpacing),
    ) {
        PageHeading("小工具", "给生活一点偶然")
        if (!layout.shortWide) SectionHeading("探索与发现", "${tools.size} 个小工具")
        tools.chunked(columns).forEach { rowTools ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(if (layout.shortWide) 10.dp else 14.dp),
            ) {
                rowTools.forEach { tool ->
                    ToolFeatureCard(
                        feature = tool,
                        compactCard = columns > 1,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowTools.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
    }
}

@Composable
private fun ToolFeatureCard(feature: ToolFeature, compactCard: Boolean, modifier: Modifier = Modifier) {
    val source = remember(feature.title) { MutableInteractionSource() }
    val colors = MaterialTheme.colorScheme
    val shortWide = LocalFortuneLayout.current.shortWide
    val fill = when (feature.visual) {
        ToolVisual.Dice -> colors.primaryContainer
        ToolVisual.Wheel -> colors.secondaryContainer
        ToolVisual.Coin -> colors.tertiaryContainer
        ToolVisual.Tarot, ToolVisual.FourTarot -> colors.surfaceContainerHigh
    }
    Surface(
        onClick = feature.onClick, interactionSource = source,
        modifier = modifier.then(pressScaleModifier(source)),
        color = LocalFortunePalette.current.panel, shape = MaterialTheme.shapes.large,
    ) {
        Column {
            Box(
                Modifier.fillMaxWidth().aspectRatio(if (shortWide) 1.20f else 1f).background(fill),
                contentAlignment = Alignment.Center,
            ) {
                ToolFeatureVisual(feature.visual, compactCard, feature.title)
            }
            Column(Modifier.padding(if (shortWide) 10.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(feature.title, style = if (shortWide) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                        maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    if (!shortWide) FortuneIcon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
                Text(feature.description, color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodySmall,
                    minLines = if (compactCard && !shortWide) 2 else 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun ToolFeatureVisual(
    visual: ToolVisual,
    compact: Boolean,
    contentDescription: String,
) {
    if (visual == ToolVisual.FourTarot) {
        FourCardTarotToolArtwork(Modifier.fillMaxSize())
        return
    }
    if (visual != ToolVisual.Tarot) {
        val image = when (visual) {
            ToolVisual.Dice -> R.drawable.tool_dice_refined
            ToolVisual.Wheel -> R.drawable.tool_wheel_refined
            ToolVisual.Coin -> R.drawable.tool_coin_refined
            ToolVisual.Tarot -> error("Tarot uses the original deck artwork")
            ToolVisual.FourTarot -> error("Four-card tarot uses the original deck artwork")
        }
        Image(
            painter = painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    } else {
        Box(
            modifier = Modifier.size(width = 144.dp, height = if (compact) 112.dp else 128.dp),
            contentAlignment = Alignment.Center,
        ) {
            listOf(
                Triple(R.drawable.tarot_major_00, -27f, -12f),
                Triple(R.drawable.tarot_major_02, 0f, 0f),
                Triple(R.drawable.tarot_major_21, 27f, 12f),
            ).forEach { (imageRes, offsetX, rotation) ->
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = offsetX.dp)
                        .fillMaxHeight(0.90f)
                        .aspectRatio(0.58f)
                        .graphicsLayer { rotationZ = rotation }
                        .clip(RoundedCornerShape(4.dp)),
                )
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
    val layout = LocalFortuneLayout.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptics = rememberToolHaptics()
    val rotations = remember { List(10) { Animatable(0f) } }
    val secureRandom = remember { SecureRandom() }
    var coinCount by rememberSaveable { mutableStateOf(1) }
    var results by rememberSaveable { mutableStateOf(listOf(CoinSide.Flower)) }
    var flipping by remember { mutableStateOf(false) }
    var resultText by rememberSaveable { mutableStateOf("等待抛掷") }
    var history by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
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
            val jobs = tosses.indices.map { index ->
                launch {
                    val normalized = rotations[index].value % 360f
                    rotations[index].snapTo(normalized)
                    val fullTurns = 5 + secureRandom.nextInt(3)
                    var previousHalfTurn = kotlin.math.floor(normalized / 180f).toInt()
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
                    ) {
                        if (index == tosses.lastIndex) {
                            val currentHalfTurn = kotlin.math.floor(value / 180f).toInt()
                            if (currentHalfTurn > previousHalfTurn) {
                                previousHalfTurn = currentHalfTurn
                                haptics.coinSpinPulse(currentHalfTurn)
                            }
                        }
                    }
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

    if (layout.shortWide) {
        ToolScreenLayout(title = "抛硬币", onBack = onBack, sideContent = {
            DiceDropdown("硬币个数", "${coinCount}枚", (1..10).map { it to "${it}枚" }, !flipping,
                onSelect = { count ->
                    coinCount = count
                    results = List(count) { CoinSide.Flower }
                    resultText = "等待抛掷"
                }, modifier = Modifier.fillMaxWidth())
            Text(if (flipping) "翻转中" else resultText, color = C.gold,
                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            LandscapeToolAction(if (flipping) "翻转中" else "抛硬币", Icons.Default.Refresh, ::tossCoins, !flipping)
            if (history.isNotEmpty()) {
                Text("最近结果", style = MaterialTheme.typography.labelMedium, color = C.textSub)
                history.forEach { Badge(it) }
            }
        }) {
            GeminiGlassPanel(modifier = Modifier.fillMaxWidth().height(LocalToolSceneHeight.current), tone = GeminiGlassTone.Neutral) {
                CoinModelScene(
                    angles = results.mapIndexed { index, side ->
                        if (flipping) rotations[index].value else if (side == CoinSide.Character) 180f else 0f
                    },
                    rolling = flipping,
                    description = if (flipping) "硬币翻转中" else results.joinToString("、") { it.label },
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                )
            }
        }
        return
    }
    ToolScreenLayout(title = "抛硬币", onBack = onBack) {
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
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxWidth(),
        )
        GeminiGlassPanel(
            tone = GeminiGlassTone.Neutral,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val columns = minOf(coinCount, 5)
                    val preferredSize = when {
                        coinCount == 1 -> 160.dp
                        coinCount <= 3 -> 96.dp
                        coinCount <= 5 -> 76.dp
                        else -> 60.dp
                    }
                    val coinSize = minOf(preferredSize, (maxWidth - 8.dp * (columns - 1)) / columns)
                    val rows = (coinCount + columns - 1) / columns
                    CoinModelScene(
                        angles = results.mapIndexed { index, side ->
                            if (flipping) rotations[index].value else if (side == CoinSide.Character) 180f else 0f
                        },
                        rolling = flipping,
                        description = if (flipping) "硬币翻转中" else results.joinToString("、") { it.label },
                        modifier = Modifier.fillMaxWidth().height(coinSize * rows + 28.dp + 12.dp * (rows - 1)),
                    )
                }
                Text(
                    if (flipping) "翻转中" else resultText,
                    color = if (flipping) C.textSub else C.gold,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Surface(
                    onClick = ::tossCoins,
                    enabled = !flipping,
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    GeminiGlassPanel(
                        tone = GeminiGlassTone.Rose,
                        shape = CircleShape,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.tertiary.copy(
                                        alpha = if (flipping) 0.035f else 0.10f,
                                    ),
                                )
                                .padding(horizontal = 18.dp, vertical = 13.dp)
                                .graphicsLayer { alpha = if (flipping) 0.42f else 1f },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            FortuneIcon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(Modifier.width(8.dp))
                            Text(if (flipping) "翻转中" else "抛硬币", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
        if (history.isNotEmpty()) {
            GeminiGlassPanel(
                tone = GeminiGlassTone.Neutral,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("最近结果", color = C.textMain, fontWeight = FontWeight.SemiBold)
                    history.forEach { value -> Badge(value) }
                }
            }
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
private fun WheelScreen(
    vm: FortuneViewModel,
    subPage: WheelSubPage,
    onSubPageChange: (WheelSubPage) -> Unit,
    onBack: () -> Unit,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptics = rememberToolHaptics()
    val segments = vm.wheelSegments
    val mainScroll = rememberScrollState()
    val settingsScroll = rememberScrollState()
    val historyScroll = rememberScrollState()
    val hapticVisible by rememberUpdatedState(subPage == WheelSubPage.Main)
    val measurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelMedium.copy(
        color = C.textMain,
        fontWeight = FontWeight.SemiBold,
    )
    val palette = if (C.isLight) {
        listOf(
            Color(0xFFD7E7F0),
            Color(0xFFF1DCD6),
            Color(0xFFDDECE1),
            Color(0xFFECE8D4),
            Color(0xFFE4E0ED),
            Color(0xFFF2E2D6),
        )
    } else {
        listOf(
            Color(0xFF344C5B),
            Color(0xFF5B403B),
            Color(0xFF354F40),
            Color(0xFF504D37),
            Color(0xFF464255),
            Color(0xFF584638),
        )
    }
    val wheelDivider = Color.White.copy(alpha = if (C.isLight) 0.76f else 0.38f)
    val wheelPointer = MaterialTheme.colorScheme.tertiary

    val angle = remember { Animatable(0f) }
    val glow = remember { Animatable(0f) }
    var spinning by remember { mutableStateOf(false) }
    var spinStartMs by remember { mutableStateOf(0L) }
    var spinJob by remember { mutableStateOf<Job?>(null) }
    var winnerIndex by remember { mutableStateOf(-1) }
    var lastShakeAt by remember { mutableStateOf(0L) }

    LaunchedEffect(subPage) {
        if (subPage != WheelSubPage.Main) haptics.cancel()
    }

    val handleBack: () -> Unit = {
        if (subPage == WheelSubPage.Main) {
            onBack()
        } else {
            onSubPageChange(WheelSubPage.Main)
        }
    }

    BackHandler(onBack = handleBack)

    fun kick(direction: Float = 1f) {
        val n = vm.wheelSegments.size
        if (n < 2) return
        spinJob?.cancel()
        spinning = true
        spinStartMs = SystemClock.uptimeMillis()
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
                val t = (SystemClock.uptimeMillis() - spinStartMs) / 1000f
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
                    if (hapticVisible) haptics.wheelTick()
                    lastTickIndex = tickIndex
                }
            }
            spinning = false
            winnerIndex = winnerFromAngle(angle.value, n)
            vm.wheelSegments.getOrNull(winnerIndex)?.let { vm.addWheelHistory(it) }
            if (hapticVisible) haptics.wheelSettle()
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
        spinStartMs = SystemClock.uptimeMillis()
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
                val t = (SystemClock.uptimeMillis() - spinStartMs) / 1000f
                if (t >= duration) break
                val v = dir * (speed.absoluteValue - decel * t).coerceAtLeast(0f)
                pos += v * dt
                angle.snapTo(pos)
                val tickIndex = kotlin.math.floor(angle.value / segmentAngle).toInt()
                if (tickIndex != lastTickIndex) {
                    if (hapticVisible) haptics.wheelTick()
                    lastTickIndex = tickIndex
                }
            }
            spinning = false
            if (hapticVisible) haptics.wheelGlideStop()
        }
    }

    fun tryAccelerate() {
        if (!spinning) {
            kick(1f)
        } else if (SystemClock.uptimeMillis() - spinStartMs < 3000L) {
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
                if (force > threshold && now - lastShakeAt > 350L && subPage == WheelSubPage.Main) {
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

    ToolScreenLayout(
        title = when (subPage) {
            WheelSubPage.Main -> "转盘"
            WheelSubPage.Settings -> "转盘设置"
            WheelSubPage.History -> "转盘记录"
        },
        onBack = handleBack,
        scrollState = when (subPage) {
            WheelSubPage.Main -> mainScroll
            WheelSubPage.Settings -> settingsScroll
            WheelSubPage.History -> historyScroll
        },
        sideContent = if (layout.shortWide && subPage == WheelSubPage.Main) ({
            Text(segments.getOrNull(winnerIndex)?.let { "本次：$it" } ?: "摇晃手机或拨动转盘开始",
                color = C.gold, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            LandscapeToolAction("设置", Icons.Default.Settings, { onSubPageChange(WheelSubPage.Settings) })
            LandscapeToolAction("历史", Icons.AutoMirrored.Filled.List, { onSubPageChange(WheelSubPage.History) })
        }) else null,
    ) {
        when (subPage) {
            WheelSubPage.Settings -> WheelSettingsView(vm)
            WheelSubPage.History -> WheelHistoryView(vm)
            WheelSubPage.Main -> {
                GeminiGlassPanel(
                    tone = GeminiGlassTone.Neutral,
                    shape = CircleShape,
                    modifier = Modifier
                        .widthIn(max = if (layout.shortWide) LocalToolSceneHeight.current else 520.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f),
                ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
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
                            color = wheelDivider,
                            start = Offset(cx, cy),
                            end = Offset(cx + (r * cos(a)).toFloat(), cy + (r * sin(a)).toFloat()),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
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
                GeminiGlassPanel(
                    tone = GeminiGlassTone.Neutral,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.Center)
                                .size(if (layout.shortWide) 44.dp else 72.dp),
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.72f), CircleShape),
                        )
                    }
                }
                Canvas(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(width = if (layout.shortWide) 28.dp else 36.dp, height = if (layout.shortWide) 18.dp else 22.dp),
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
                    drawPath(tri, wheelPointer)
                }
            }
            }

            if (!layout.shortWide) {
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
                    onClick = { onSubPageChange(WheelSubPage.Settings) },
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape,
                ) {
                    GeminiGlassPanel(
                        tone = GeminiGlassTone.Teal,
                        shape = CircleShape,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            FortuneIcon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("设置", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Surface(
                    onClick = { onSubPageChange(WheelSubPage.History) },
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape,
                ) {
                    GeminiGlassPanel(
                        tone = GeminiGlassTone.Neutral,
                        shape = CircleShape,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            FortuneIcon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("历史", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
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
                    onClick = hapticAction { vm.setWheelSegmentCount(count) },
                    color = if (segments.size == count) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                    contentColor = if (segments.size == count) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    shape = CircleShape,
                ) {
                    Text(
                        count.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }
        }
        TextButton(onClick = { vm.resetWheelSegments() }) {
            FortuneIcon(Icons.Default.Refresh, contentDescription = null, tint = C.textSub)
            Spacer(Modifier.width(4.dp))
            Text("重置为「是/否」", color = C.textSub)
        }
        segments.forEachIndexed { index, seg ->
            GeminiGlassPanel(
                tone = GeminiGlassTone.Neutral,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    GeminiGlassTextField(
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
                        FortuneIcon(Icons.Default.Delete, contentDescription = "删除", tint = C.danger)
                    }
                }
            }
        }
        OutlinedButton(
            onClick = { vm.addWheelSegment() },
            enabled = segments.size < 25,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        ) {
            FortuneIcon(Icons.Default.Add, contentDescription = null)
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
                GeminiGlassPanel(
                    tone = GeminiGlassTone.Neutral,
                    shape = MaterialTheme.shapes.large,
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
                            FortuneIcon(Icons.Default.Delete, contentDescription = "删除", tint = C.danger)
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
@OptIn(ExperimentalMaterial3Api::class)
private fun DiceScreen(onBack: () -> Unit) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val rollInteractionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()
    val haptics = rememberToolHaptics()
    val physicsWorld = remember { DicePhysicsWorld() }
    var diceCount by rememberSaveable { mutableStateOf(1) }
    var diceSides by rememberSaveable { mutableStateOf(6) }
    var faces by rememberSaveable { mutableStateOf(listOf(1)) }
    var resultText by rememberSaveable { mutableStateOf("1枚6面骰：等待摇骰") }
    var rolling by remember { mutableStateOf(false) }
    var curtainClosed by rememberSaveable { mutableStateOf(false) }
    var curtainDragProgress by remember { mutableStateOf<Float?>(null) }
    var history by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var preparationVersion by remember { mutableStateOf(0) }
    val preparedRolls = remember(diceCount, diceSides, preparationVersion) {
        DiceThrowStrength.entries.associateWith { CompletableDeferred<Result<Pair<DiceRollResult, DiceTrajectory>>>() }
    }

    LaunchedEffect(preparedRolls) {
        val count = diceCount
        val sides = diceSides
        withContext(Dispatchers.Default) {
            val job = currentCoroutineContext()
            val roll = rollDice(count, sides)
            val seed = kotlin.random.Random.nextLong()
            for (strength in listOf(DiceThrowStrength.Medium, DiceThrowStrength.Strong, DiceThrowStrength.Light)) {
                job.ensureActive()
                val started = SystemClock.elapsedRealtimeNanos()
                val outcome = runCatching {
                    roll to DiceRigidBodySimulation.generate(sides, roll.rolls, seed, strength) { job.ensureActive() }
                }
                job.ensureActive()
                preparedRolls.getValue(strength).complete(outcome)
                if (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0) {
                    outcome.onSuccess { (_, path) ->
                        android.util.Log.d("DicePhysics", "prepare_ms=${(SystemClock.elapsedRealtimeNanos() - started) / 1_000_000} strength=$strength sides=$sides count=$count attempts=${path.attempts} frames=${path.frames.size}")
                    }
                }
            }
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

    fun startRoll(strength: DiceThrowStrength = DiceThrowStrength.Medium) {
        if (rolling) return
        val request = preparedRolls.getValue(strength)
        rolling = true
        haptics.diceStart()
        scope.launch {
            try {
                scrollState.scrollTo(0)
                val simulationStarted = SystemClock.elapsedRealtimeNanos()
                val (roll, trajectory) = request.await().getOrThrow()
                preparationVersion++
                if (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0) {
                    android.util.Log.d("DicePhysics", "start_wait_ms=${(SystemClock.elapsedRealtimeNanos() - simulationStarted) / 1_000_000} strength=$strength")
                }
                playShakeSound()
                physicsWorld.play(trajectory) { haptics.diceImpact(it) }
                faces = roll.rolls
                resultText = roll.displayText
                history = (listOf(roll.displayText) + history).take(6)
                haptics.diceSettle()
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (_: LinkageError) {
                preparationVersion++
                resultText = "等待重新摇骰"
                Toast.makeText(context, "骰子引擎暂不可用，请重新打开工具", Toast.LENGTH_SHORT).show()
            } catch (_: Exception) {
                preparationVersion++
                resultText = "等待重新摇骰"
                Toast.makeText(context, "本次未能落稳，请重新摇骰", Toast.LENGTH_SHORT).show()
            } finally {
                rolling = false
            }
        }
    }

    ObserveDiceShake(enabled = !rolling, onShake = { startRoll(it) })

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
    val handleHeightPx = with(LocalDensity.current) { 48.dp.toPx() }
    val fontScale = LocalDensity.current.fontScale
    val configuration = LocalConfiguration.current
    val shortWide = configuration.screenWidthDp >= 500 && configuration.screenWidthDp > configuration.screenHeightDp

    @Composable
    fun CountControl(modifier: Modifier = Modifier, compact: Boolean = false) {
        DiceDropdown(
            label = "骰子个数",
            value = "${diceCount}枚",
            options = (1..6).map { it to "${it}枚" },
            enabled = !rolling,
            onSelect = { count ->
                physicsWorld.stop()
                diceCount = count
                faces = List(count) { 1.coerceAtMost(diceSides) }
                resultText = "${count}枚${diceSides}面骰：等待摇骰"
            },
            modifier = modifier,
            showLabel = !compact,
        )
    }

    @Composable
    fun SidesControl(modifier: Modifier = Modifier, compact: Boolean = false) {
        DiceDropdown(
            label = "骰子面数",
            value = "${diceSides}面",
            options = listOf(4, 6, 8, 10, 12, 20).map { it to "${it}面" },
            enabled = !rolling,
            onSelect = { sides ->
                physicsWorld.stop()
                diceSides = sides
                faces = List(diceCount) { 1 }
                resultText = "${diceCount}枚${sides}面骰：等待摇骰"
            },
            modifier = modifier,
            showLabel = !compact,
        )
    }

    @Composable
    fun PlayArea(modifier: Modifier, wide: Boolean = false) {
        var playHeight by remember { mutableStateOf(1f) }
        val density = LocalDensity.current
        BoxWithConstraints(modifier.onSizeChanged { playHeight = it.height.toFloat().coerceAtLeast(1f) }) {
            val counterWidth = diceLandscapeCounterWidth(maxWidth.value, fontScale).dp
            val paneSpacing = 12.dp
            val handleOffset = if (wide) with(density) { -((counterWidth + paneSpacing) / 2).roundToPx() } else 0
            if (wide) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(paneSpacing)) {
                    DiceStage(faces, diceSides, physicsWorld, rolling, Modifier.weight(1f).fillMaxHeight())
                    Box(Modifier.width(counterWidth).fillMaxHeight()) {
                        DiceResultCounter(faces, diceSides, resultText, rolling, vertical = true)
                    }
                }
            } else Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DiceStage(
                    faces = faces,
                    sides = diceSides,
                    physicsWorld = physicsWorld,
                    rolling = rolling,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                )
                DiceResultCounter(faces, diceSides, resultText, rolling)
            }
            if (curtainVisible) {
                DiceCurtainOverlay(curtainProgress, Modifier.matchParentSize())
            }
            // Only this small moving handle accepts curtain gestures; the table and counter do not.
            Box(
                Modifier.align(Alignment.TopCenter)
                    .offset {
                        androidx.compose.ui.unit.IntOffset(handleOffset, ((playHeight - handleHeightPx).coerceAtLeast(0f) * curtainProgress).toInt())
                    }
                    .size(width = 96.dp, height = 48.dp)
                    .pointerInput(curtainClosed, curtainSwipeThreshold, playHeight) {
                            var gestureDistance = 0f
                            val tactile = HapticGestureTrack()
                            detectVerticalDragGestures(
                                onDragStart = {
                                    tactile.reset()
                                    gestureDistance = 0f
                                    curtainDragProgress = if (curtainClosed) 1f else 0f
                                },
                                onVerticalDrag = { change, dragAmount ->
                                    change.consume()
                                    gestureDistance += dragAmount
                                    val currentProgress = curtainDragProgress ?: if (curtainClosed) 1f else 0f
                                    curtainDragProgress = (currentProgress + dragAmount / playHeight)
                                        .coerceIn(0f, 1f)
                                    haptics.gestureProgress(tactile, kotlin.math.abs(gestureDistance) / curtainSwipeThreshold)
                                },
                                onDragEnd = {
                                    val wasClosed = curtainClosed
                                    curtainClosed = when {
                                        gestureDistance > curtainSwipeThreshold -> true
                                        gestureDistance < -curtainSwipeThreshold -> false
                                        else -> (curtainDragProgress ?: 0f) >= 0.5f
                                    }
                                    haptics.gestureFinished(tactile, curtainClosed != wasClosed)
                                    curtainDragProgress = null
                                },
                                onDragCancel = { tactile.reset(); curtainDragProgress = null },
                            )
                    }
                    .semantics { contentDescription = "骰子卷帘把手" }
                    .clickable(role = androidx.compose.ui.semantics.Role.Button) {
                        curtainDragProgress = null
                        curtainClosed = !curtainClosed
                        haptics.scene(HapticCue.GestureRelease)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Canvas(Modifier.size(width = 40.dp, height = 4.dp)) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0xFF756F60), Color(0xFFBAB09A), Color(0xFF8B806A))),
                        cornerRadius = CornerRadius(2.dp.toPx()),
                        alpha = .65f,
                    )
                }
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        SubScreenHeader(title = "摇骰子", onBack = onBack) {
            if (shortWide) {
                CountControl(Modifier.padding(end = 8.dp).width(88.dp), compact = true)
                SidesControl(Modifier.padding(end = 8.dp).width(88.dp), compact = true)
            }
            androidx.compose.material3.TooltipBox(
                positionProvider = androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider(androidx.compose.material3.TooltipAnchorPosition.Above),
                tooltip = { PlainTooltip { Text("摇骰子") } },
                state = androidx.compose.material3.rememberTooltipState(),
            ) {
                androidx.compose.material3.FilledTonalIconButton(
                    onClick = { startRoll() },
                    enabled = !rolling,
                    interactionSource = rollInteractionSource,
                    modifier = Modifier.padding(end = 8.dp).size(48.dp).then(pressScaleModifier(rollInteractionSource)),
                ) {
                    if (rolling) CircularProgressIndicator(Modifier.size(20.dp).semantics { contentDescription = "摇动中" }, strokeWidth = 2.dp)
                    else FortuneIcon(Icons.Default.Casino, contentDescription = "摇骰子")
                }
            }
        }
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            val minimumHeight = if (shortWide) (110f + 30f * fontScale).dp else (200f + 100f * fontScale).dp
            val panelHeight = maxOf(maxHeight - layout.verticalPadding * 2, minimumHeight)
            Column(
                Modifier.fillMaxSize().verticalScroll(scrollState)
                    .padding(horizontal = layout.horizontalPadding, vertical = layout.verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(layout.sectionSpacing),
            ) {
                if (shortWide) {
                    PlayArea(Modifier.fillMaxWidth().height(panelHeight), wide = true)
                } else {
                    GeminiGlassPanel(
                        modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth().height(panelHeight),
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                CountControl(Modifier.weight(1f))
                                SidesControl(Modifier.weight(1f))
                            }
                            PlayArea(Modifier.weight(1f).fillMaxWidth())
                        }
                    }
                }
                if (history.isNotEmpty()) {
                    GeminiGlassPanel(
                        tone = GeminiGlassTone.Neutral,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
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
    showLabel: Boolean = true,
) {
    val C = LocalFortunePalette.current
    var expanded by remember { mutableStateOf(false) }
    val haptics = LocalAppHaptics.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (showLabel) Text(label, color = C.textSub, style = MaterialTheme.typography.labelMedium)
        Box {
            OutlinedButton(
                onClick = { haptics.menu(true); expanded = true },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().semantics { if (!showLabel) contentDescription = label },
                shape = MaterialTheme.shapes.large,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = if (showLabel) 24.dp else 10.dp, vertical = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
            ) {
                Text(value, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { haptics.menu(false); expanded = false }) {
                options.forEach { (rawValue, text) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            if (text != value) haptics.selection()
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
    compact: Boolean = false,
    vertical: Boolean = false,
) {
    val C = LocalFortunePalette.current
    val hasResult = !rolling && !resultText.contains("等待")
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val totalStyle = (if (vertical) MaterialTheme.typography.headlineLarge else if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium)
        .copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    val valuesStyle = (if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodyMedium)
        .copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
    val separator = if (compact) "·" else " · "
    val totalWidth = with(density) { measurer.measure(AnnotatedString("----"), style = totalStyle).size.width.toDp() }
    Surface(
        color = if (C.isLight) Color(0xFF10211B) else Color(0xFF0B1713),
        contentColor = Color(0xFFE8F7D4),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color(0xFF3C7D5B)),
        modifier = if (vertical) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
    ) {
        if (vertical) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val headerStyle = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                val faceStyle = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                val headerWidth = with(density) {
                    (measurer.measure(AnnotatedString("TOTAL"), headerStyle).size.width +
                        measurer.measure(AnnotatedString("${faces.size}D$sides"), headerStyle).size.width).toDp()
                } + 32.dp
                val headerFits = headerWidth <= maxWidth
                val cellWidth = with(density) { measurer.measure(AnnotatedString("--"), faceStyle).size.width.toDp() }
                val columns = ((maxWidth - 20.dp) / (cellWidth + 4.dp)).toInt().coerceIn(1, 3)
                val valueHeight = with(density) { measurer.measure(AnnotatedString("--"), faceStyle).size.height.toDp() } + 2.dp
                val headerHeight = with(density) { measurer.measure(AnnotatedString("TOTAL"), headerStyle).size.height.toDp() }
                val totalHeight = with(density) { measurer.measure(AnnotatedString("120"), totalStyle).size.height.toDp() }
                val rows = (faces.size + columns - 1) / columns
                val contentHeight = (if (headerFits) headerHeight else headerHeight * 2 + 2.dp) +
                    totalHeight + valueHeight * rows + 4.dp * (rows - 1) + 40.dp
                val tight = contentHeight > maxHeight
                Column(
                    Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight).padding(horizontal = 12.dp, vertical = if (tight) 8.dp else 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    if (headerFits) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TOTAL", color = Color(0xFF8DC6A2), style = headerStyle, maxLines = 1, softWrap = false)
                            Text("${faces.size}D$sides", color = Color(0xFF8DC6A2), style = headerStyle, maxLines = 1, softWrap = false)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("TOTAL", color = Color(0xFF8DC6A2), style = headerStyle, maxLines = 1, softWrap = false)
                            Text("${faces.size}D$sides", color = Color(0xFF8DC6A2), style = headerStyle, maxLines = 1, softWrap = false)
                        }
                    }
                    Spacer(Modifier.height(if (tight) 4.dp else 8.dp))
                    Text(
                        if (hasResult) faces.sum().toString() else "--",
                        color = if (rolling) Color(0xFFE1B85A) else Color(0xFFD9F6B7),
                        style = totalStyle, maxLines = 1, softWrap = false,
                    )
                    Spacer(Modifier.height(if (tight) 4.dp else 8.dp))
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        faces.chunked(columns).forEach { row ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                row.forEach { face ->
                                    Box(
                                        Modifier.weight(1f).heightIn(min = valueHeight),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(if (hasResult) face.toString() else "--",
                                            color = Color(0xFFD9F6B7), style = faceStyle,
                                            maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else Row(
            modifier = Modifier.padding(horizontal = if (compact) 12.dp else 14.dp, vertical = if (compact) 8.dp else 10.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 14.dp),
        ) {
            Column(modifier = Modifier.width(totalWidth), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "TOTAL",
                    color = Color(0xFF8DC6A2),
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                )
                Text(
                    if (hasResult) faces.sum().toString() else "----",
                    color = if (rolling) Color(0xFFE1B85A) else Color(0xFFD9F6B7),
                    style = totalStyle,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                )
            }
            BoxWithConstraints(Modifier.weight(1f)) {
                val reservedLines = measurer.measure(
                    AnnotatedString(List(faces.size) { sides.toString() }.joinToString(separator)),
                    style = valuesStyle,
                    constraints = androidx.compose.ui.unit.Constraints(maxWidth = with(density) { maxWidth.roundToPx().coerceAtLeast(1) }),
                ).lineCount
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "${faces.size}D$sides",
                    color = Color(0xFF8DC6A2),
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    if (hasResult) faces.joinToString(separator) else "等待结果",
                    color = Color(0xFFD9F6B7),
                    style = valuesStyle,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    minLines = reservedLines,
                )
            }
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
    modifier: Modifier = Modifier,
) {
    PhysicsDiceStage(
        fallbackFaces = faces,
        sides = sides,
        world = physicsWorld,
        rolling = rolling,
        modifier = modifier,
    )
}

@Composable
private fun PhysicsDiceStage(
    fallbackFaces: List<Int>,
    sides: Int,
    world: DicePhysicsWorld,
    rolling: Boolean,
    modifier: Modifier = Modifier,
) {
    val mesh = remember(sides) { buildPhysicsMesh(sides) }
    val meshFaces = remember(sides) { buildPhysicsMeshFaces(mesh) }
    val meshEdges = remember(sides) { buildPhysicsMeshEdges(mesh) }
    val camera = remember(sides) { if (sides == 4) DICE_D4_CAMERA else DICE_TOP_CAMERA }
    val glassLight = LocalGlassLightController.current
    val rimTint = MaterialTheme.colorScheme.secondary
    val rimAccent = MaterialTheme.colorScheme.primary
    val labelPainter = remember { DiceNumberPainter() }
    val vertices = remember(sides) { mesh.flatMap { listOf(it.a, it.b, it.c) }.distinct() }
    val faceIndices = remember(sides) { meshFaces.map { face -> face.vertices.map { vertices.indexOf(it) } } }
    val edgeIndices = remember(sides) { meshEdges.map { vertices.indexOf(it.a) to vertices.indexOf(it.b) } }
    val edgeNormals = remember(sides) {
        meshEdges.map { edge -> edge.normals.map { normal -> meshFaces.indexOfFirst { it.normal.dot(normal) > 0.985f } } }
    }
    val labelBases = remember(sides) { meshFaces.map(::buildDiceFaceBasis) }
    val fallbackBodies = remember(fallbackFaces, sides, world) {
        world.restingFallback(fallbackFaces, sides)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
    ) {
        Image(
            painter = painterResource(R.drawable.dice_table_felt),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.Black.copy(alpha = 0.10f))
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF071013), Color(0xFF2B383E), Color(0xFF080D10)),
                    start = Offset(
                        size.width * (0.12f + glassLight.x * 0.10f),
                        size.height * (0.10f + glassLight.y * 0.08f),
                    ),
                    end = Offset(size.width, size.height),
                ),
                topLeft = Offset(5.dp.toPx(), 5.dp.toPx()),
                size = Size(size.width - 10.dp.toPx(), size.height - 10.dp.toPx()),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                style = Stroke(width = 10.dp.toPx()),
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.64f),
                        rimTint.copy(alpha = 0.72f),
                        rimAccent.copy(alpha = 0.54f),
                        Color.White.copy(alpha = 0.38f),
                    ),
                    start = Offset(
                        size.width * (0.08f + glassLight.x * 0.16f),
                        size.height * (0.16f + glassLight.y * 0.12f),
                    ),
                    end = Offset(size.width * 0.92f, size.height * 0.84f),
                ),
                topLeft = Offset(11.dp.toPx(), 11.dp.toPx()),
                size = Size(size.width - 22.dp.toPx(), size.height - 22.dp.toPx()),
                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                style = Stroke(width = 3.dp.toPx()),
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.42f),
                topLeft = Offset(13.dp.toPx(), 13.dp.toPx()),
                size = Size(size.width - 26.dp.toPx(), size.height - 26.dp.toPx()),
                cornerRadius = CornerRadius(13.dp.toPx(), 13.dp.toPx()),
                style = Stroke(width = 0.8.dp.toPx()),
            )

        val dice = world.renderState
        val bodies = if (dice.isEmpty()) fallbackBodies else dice
        val silverBase = Color(0xFFD5D9DC)
        val sortedBodies = bodies.sortedByDescending { die ->
            (die.position - camera.position).dot(camera.forward)
        }.map { die ->
            val points = vertices.map { die.position + die.rotation.rotate(it) }
            PhysicsProjectedDie(die, points, points.map { projectDicePoint(it, size, camera) },
                meshFaces.map { die.rotation.rotate(it.normal) })
        }

        sortedBodies.forEach { projectedDie ->
            val die = projectedDie.die
            val clearance = projectedDie.worldPoints.minOf { it.y }.coerceAtLeast(0f)
            val shadow = projectDicePoint(Vec3(die.position.x + 0.07f + clearance * 0.14f, 0.003f, die.position.z + 0.05f), size, camera)
            val spread = (1f + clearance * 0.65f)
            val radius = size.minDimension * 0.33f * (if (sides == 6) 0.37f else 0.32f) * spread
            val shadowHeight = radius * if (sides == 4) 0.68f else 0.92f
            drawOval(
                brush = Brush.radialGradient(
                    listOf(Color.Black.copy(alpha = 0.38f / spread), Color.Transparent),
                    center = Offset(shadow.x, shadow.y),
                    radius = radius,
                ),
                topLeft = Offset(shadow.x - radius, shadow.y - shadowHeight),
                size = Size(radius * 2f, shadowHeight * 2f),
            )
        }

        sortedBodies.forEach { projectedDie ->
            val die = projectedDie.die
            val visibleFaces = ArrayList<PhysicsRenderFace>(meshFaces.size)
            meshFaces.forEachIndexed faceLoop@ { faceIndex, face ->
                val outwardNormal = projectedDie.normals[faceIndex]
                if (outwardNormal.dot(camera.viewDirection) <= 0.015f) return@faceLoop
                val projected = faceIndices[faceIndex].map { projectedDie.points[it] }
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
                    value = die.faceValues[faceIndex],
                    label = projectDiceFaceLabel(labelBases[faceIndex], die, size, camera),
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
                    face.label?.let { label ->
                        clipPath(path) {
                            drawDiceLabel(face.value.toString(), label, sides, labelPainter)
                        }
                    }
                }

            meshEdges.forEachIndexed edgeLoop@ { edgeIndex, _ ->
                val visible = edgeNormals[edgeIndex].any { normalIndex ->
                    normalIndex >= 0 && projectedDie.normals[normalIndex].dot(camera.viewDirection) > 0.015f
                }
                if (!visible) return@edgeLoop
                val pa = projectedDie.points[edgeIndices[edgeIndex].first]
                val pb = projectedDie.points[edgeIndices[edgeIndex].second]
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

        }
    }
}
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDiceLabel(
    value: String,
    label: DiceLabelProjection,
    sides: Int,
    painter: DiceNumberPainter,
) {
    drawIntoCanvas { canvas ->
        val paint = painter.fill.apply {
            color = painter.colors[(value.toIntOrNull() ?: 1) - 1]
            textSize = when {
                sides >= 20 -> 15.dp.toPx()
                sides >= 10 -> 17.dp.toPx()
                else -> 21.dp.toPx()
            }
        }
        while (paint.measureText(value) > label.maxTextWidth && paint.textSize > 4.dp.toPx()) {
            paint.textSize *= 0.86f
        }
        while (paint.descent() - paint.ascent() > label.maxTextHeight && paint.textSize > 4.dp.toPx()) {
            paint.textSize *= 0.86f
        }
        if (label.maxTextWidth < 5.dp.toPx() || label.maxTextHeight < 5.dp.toPx()) return@drawIntoCanvas
        val outlinePaint = painter.outline.apply {
            textSize = paint.textSize
            strokeWidth = 1.15.dp.toPx()
            strokeJoin = android.graphics.Paint.Join.ROUND
        }
        val baseline = -(paint.ascent() + paint.descent()) * 0.5f
        painter.transformValues[0] = label.right.x
        painter.transformValues[1] = label.down.x
        painter.transformValues[2] = label.point.x
        painter.transformValues[3] = label.right.y
        painter.transformValues[4] = label.down.y
        painter.transformValues[5] = label.point.y
        painter.transform.setValues(painter.transformValues)
        canvas.nativeCanvas.save()
        canvas.nativeCanvas.concat(painter.transform)
        canvas.nativeCanvas.drawText(value, 0f, baseline, outlinePaint)
        canvas.nativeCanvas.drawText(value, 0f, baseline, paint)
        canvas.nativeCanvas.restore()
    }
}

private class DiceNumberPainter {
    val transform = android.graphics.Matrix()
    val transformValues = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
    val colors = IntArray(20) { number ->
        android.graphics.Color.HSVToColor(floatArrayOf((number * 137.508f) % 360f, 0.82f, 0.58f))
    }
    val fill = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    val outline = android.graphics.Paint(fill).apply {
        color = android.graphics.Color.rgb(31, 34, 36)
        style = android.graphics.Paint.Style.STROKE
    }
}

private fun projectDiceFaceLabel(
    basis: DiceFaceBasis,
    die: PhysicsDieRender,
    size: Size,
    camera: DiceCameraProjection,
): DiceLabelProjection {
    val center = die.position + die.rotation.rotate(basis.center)
    val right = die.rotation.rotate(basis.right)
    val down = die.rotation.rotate(basis.up) * -1f
    val scale = size.minDimension * 0.33f
    return DiceLabelProjection(
        projectDicePoint(center, size, camera),
        Offset(right.dot(camera.right), -right.dot(camera.up)),
        Offset(down.dot(camera.right), -down.dot(camera.up)),
        basis.clearance * scale * 1.42f,
        basis.clearance * scale * 1.28f,
    )
}

private data class DiceLabelProjection(
    val point: DiceScreenPoint,
    val right: Offset,
    val down: Offset,
    val maxTextWidth: Float,
    val maxTextHeight: Float,
)

private data class PhysicsDieRender(
    val position: Vec3,
    val rotation: Quat,
    val result: Int,
    val faceValues: List<Int>,
)

private data class PhysicsProjectedDie(
    val die: PhysicsDieRender,
    val worldPoints: List<Vec3>,
    val points: List<DiceScreenPoint>,
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
    val value: Int,
    val label: DiceLabelProjection?,
)

private class DicePhysicsWorld {
    var renderState by mutableStateOf<List<PhysicsDieRender>>(emptyList())
        private set

    suspend fun play(trajectory: DiceTrajectory, onImpact: (Float) -> Unit) {
        val start = withFrameNanos { it }
        var lastEvent = -1
        do {
            val now = withFrameNanos { it }
            val framePosition = ((now - start) / 1_000_000_000f / DiceRigidBodySimulation.STEP_SECONDS)
                .coerceAtMost(trajectory.frames.lastIndex.toFloat())
            val index = framePosition.toInt()
            val next = (index + 1).coerceAtMost(trajectory.frames.lastIndex)
            val blend = framePosition - index
            val first = trajectory.frames[index]
            val second = trajectory.frames[next]
            renderState = first.poses.mapIndexed { die, pose ->
                val following = second.poses[die]
                PhysicsDieRender(
                    pose.position + (following.position - pose.position) * blend,
                    Quat.slerp(pose.rotation, following.rotation, blend),
                    trajectory.results[die],
                    trajectory.faceValues[die],
                )
            }
            var impact = 0f
            for (event in lastEvent + 1..index) impact = maxOf(impact, trajectory.frames[event].impact)
            lastEvent = index
            if (impact > 0f) onImpact(impact)
        } while (index < trajectory.frames.lastIndex)
    }

    fun stop() { renderState = emptyList() }

    fun restingFallback(results: List<Int>, sides: Int): List<PhysicsDieRender> {
        val mesh = buildPhysicsMesh(sides)
        val faces = buildPhysicsMeshFaces(mesh)
        val align = Quat.fromTo(faces.first().normal, Vec3(0f, -1f, 0f))
        val columns = minOf(3, results.size)
        val rows = (results.size + columns - 1) / columns
        return results.mapIndexed { index, result ->
            val yaw = index * 1.17f
            val rotation = Quat(cos(yaw * 0.5f), 0f, sin(yaw * 0.5f), 0f) * align
            val height = -mesh.flatMap { listOf(it.a, it.b, it.c) }.minOf { rotation.rotate(it).y }
            val items = minOf(columns, results.size - index / columns * columns)
            PhysicsDieRender(
                Vec3((index % columns - (items - 1) * 0.5f) * 0.86f, height, (index / columns - (rows - 1) * 0.5f) * 0.82f),
                rotation,
                result,
                DiceRigidBodySimulation.faceValues(faces, DiceRigidBodySimulation.resultFace(faces, rotation, sides), result, sides),
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
private fun ScheduleScreen(
    vm: FortuneViewModel,
    navigation: ScheduleNavigationState,
    requestedDetailDate: String?,
    onDetailRequestHandled: () -> Unit,
) {
    val selectedDate = vm.selectedScheduleDate
    val calendarScrollState = rememberScrollState()
    val subPageStates = rememberSaveableStateHolder()
    val page = runCatching { SchedulePage.valueOf(navigation.pageName) }.getOrDefault(SchedulePage.Calendar)
    val editorReturnPage = runCatching { SchedulePage.valueOf(navigation.editorReturnPageName) }
        .getOrDefault(SchedulePage.Calendar)
    val detailDate = remember(navigation.detailDateText) {
        navigation.detailDateText?.let { value -> runCatching { LocalDate.parse(value) }.getOrNull() }
    }
    val activeItem = remember(vm.scheduleItems, navigation.activeItemId) {
        navigation.activeItemId?.let { id -> vm.scheduleItems.firstOrNull { it.id == id } }
    }
    HapticPageFeedback(if (detailDate != null) "almanac" else page.name, when {
        detailDate != null -> 1
        page == SchedulePage.Calendar -> 0
        page == SchedulePage.Memorial -> 1
        else -> 2
    })

    LaunchedEffect(requestedDetailDate) {
        val requestedDate = requestedDetailDate
            ?.let { value -> runCatching { LocalDate.parse(value) }.getOrNull() }
        if (requestedDate != null) {
            vm.selectScheduleDate(requestedDate)
            navigation.pageName = SchedulePage.Calendar.name
            navigation.detailDateText = requestedDate.toString()
            onDetailRequestHandled()
        }
    }

    LaunchedEffect(Unit) {
        delay(180L)
        vm.prepareCalendarCache()
    }

    BackHandler(enabled = detailDate != null || page != SchedulePage.Calendar) {
        when {
            detailDate != null -> navigation.detailDateText = null
            page == SchedulePage.Editor -> navigation.pageName = editorReturnPage.name
            page == SchedulePage.Detail -> navigation.pageName = SchedulePage.Memorial.name
            page == SchedulePage.Memorial -> navigation.pageName = SchedulePage.Calendar.name
        }
    }
    if (detailDate != null) {
        AlmanacDateDetailScreen(
            date = detailDate,
            scheduleItems = vm.scheduleItems.filter { it.date == detailDate.toString() },
            onBack = { navigation.detailDateText = null },
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
                onCancel = { navigation.pageName = editorReturnPage.name },
                onSave = { draft ->
                    val saved = vm.saveScheduleDraft(draft, activeItem?.id)
                    vm.selectScheduleDate(
                        runCatching { LocalDate.parse(saved.date) }.getOrDefault(selectedDate)
                    )
                    navigation.activeItemId = saved.id
                    navigation.pageName = editorReturnPage.name
                },
            )
            return
        }
        SchedulePage.Memorial -> {
            subPageStates.SaveableStateProvider("memorial") {
                ScheduleMemorialScreen(
                    items = vm.scheduleItems,
                    onBack = { navigation.pageName = SchedulePage.Calendar.name },
                    onItemClick = { item ->
                        navigation.activeItemId = item.id
                        navigation.pageName = SchedulePage.Detail.name
                    },
                )
            }
            return
        }
        SchedulePage.Detail -> {
            if (activeItem == null) {
                LaunchedEffect(navigation.activeItemId) { navigation.pageName = SchedulePage.Memorial.name }
            } else {
                subPageStates.SaveableStateProvider("detail-${activeItem.id}") {
                    ScheduleDetailScreen(
                        item = activeItem,
                        onBack = { navigation.pageName = SchedulePage.Memorial.name },
                        onEdit = {
                            navigation.editorReturnPageName = SchedulePage.Detail.name
                            navigation.pageName = SchedulePage.Editor.name
                        },
                        onToggleDone = { vm.toggleScheduleItem(activeItem.id) },
                        onDelete = {
                            vm.deleteScheduleItem(activeItem.id)
                            navigation.activeItemId = null
                            navigation.pageName = SchedulePage.Memorial.name
                        },
                    )
                }
            }
            return
        }
        SchedulePage.Calendar -> Unit
    }

    val calendar: @Composable (Dp) -> Unit = { availableHeight ->
        CalendarPanel(
            scheduleItems = vm.scheduleItems,
            selectedDate = selectedDate,
            onSelectedDateChange = vm::selectScheduleDate,
            onOpenDateDetail = {
                vm.selectScheduleDate(it)
                navigation.detailDateText = it.toString()
            },
            onAddSchedule = {
                navigation.activeItemId = null
                navigation.editorReturnPageName = SchedulePage.Calendar.name
                navigation.pageName = SchedulePage.Editor.name
            },
            availableHeight = availableHeight,
            onOpenMemorial = { navigation.pageName = SchedulePage.Memorial.name },
        )
    }
    if (LocalFortuneLayout.current.shortWide) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(
            horizontal = LocalFortuneLayout.current.horizontalPadding,
            vertical = LocalFortuneLayout.current.verticalPadding)) {
            calendar(maxHeight)
        }
    } else Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(calendarScrollState)
            .padding(
                horizontal = LocalFortuneLayout.current.horizontalPadding,
                vertical = LocalFortuneLayout.current.verticalPadding,
            ),
        verticalArrangement = Arrangement.spacedBy(LocalFortuneLayout.current.sectionSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        calendar(Dp.Unspecified)

        ScheduleMemorialEntryCard(itemCount = vm.scheduleItems.size) {
            navigation.pageName = SchedulePage.Memorial.name
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
    availableHeight: Dp,
    onOpenMemorial: () -> Unit,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val haptics = LocalAppHaptics.current
    val today = remember { LocalDate.now() }
    val animationScope = rememberCoroutineScope()
    val initialVisibleMonth = remember { YearMonth.from(selectedDate) }
    var visibleMonth by remember { mutableStateOf(initialVisibleMonth) }
    var picker by remember { mutableStateOf<CalendarPicker?>(null) }
    val centerPage = 6000
    val initialPage = remember {
        val offset = ChronoUnit.MONTHS.between(YearMonth.from(today), initialVisibleMonth)
        (centerPage + offset).toInt().coerceIn(0, 12000)
    }
    val pagerState = rememberPagerState(initialPage = initialPage) { 12001 }
    val scheduledDates = remember(scheduleItems) { scheduleItems.mapTo(hashSetOf()) { it.date } }
    var todayInfo by remember(today) { mutableStateOf(cachedCalendarDateInfo(today)) }
    LaunchedEffect(today) {
        if (todayInfo == null) {
            delay(220L)
            todayInfo = withContext(calendarComputationDispatcher) {
                cachedCalendarDateInfo(today) ?: calendarDateInfo(today)
            }
        }
    }
    val todayHeader = todayInfo?.let { formatFullCalendarDate(today, it) }
        ?: "${today.year}年${today.monthValue}月${today.dayOfMonth}日"
    val selectedScheduleItems = remember(scheduleItems, selectedDate) {
        scheduleItems.filter { it.date == selectedDate.toString() }
    }
    val yearTransition = remember { Animatable(1f) }
    var yearTransitionDirection by remember { mutableStateOf(1f) }
    var calendarTransitionAxis by remember { mutableStateOf(CalendarDragAxis.Vertical) }
    val yearTransitionDistance = with(LocalDensity.current) { 12.dp.toPx() }
    val calendarSwipeThreshold = with(LocalDensity.current) { 88.dp.toPx() }
    // 进页时预计算当月及前后各两月，翻页大概率命中缓存。
    LaunchedEffect(initialVisibleMonth) {
        delay(900L)
        listOf(-1, 1).forEach { delta ->
            prefetchMonthInfo(initialVisibleMonth.plusMonths(delta.toLong()))
            delay(180L)
        }
    }
    // 翻页后，预计算新当月前后各两月，保持缓存领先于滑动。
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val base = YearMonth.from(today).plusMonths((page - centerPage).toLong())
            listOf(-1, 1).forEach { delta ->
                prefetchMonthInfo(base.plusMonths(delta.toLong()))
                delay(180L)
            }
        }
    }
    var selectedInfo by remember(selectedDate) { mutableStateOf(cachedCalendarDateInfo(selectedDate)) }
    LaunchedEffect(selectedDate) {
        if (selectedInfo == null) {
            delay(220L)
            selectedInfo = withContext(calendarComputationDispatcher) {
                cachedCalendarDateInfo(selectedDate) ?: calendarDateInfo(selectedDate)
            }
        }
    }
    val showToday = visibleMonth != YearMonth.from(today) || selectedDate != today
    val weekdayHeight = with(LocalDensity.current) { MaterialTheme.typography.labelMedium.lineHeight.toDp() }
    val gridGap = if (layout.shortWide) 2.dp else 4.dp
    val rows = calendarRowCount(visibleMonth)
    val cellHeight = if (layout.shortWide) {
        landscapeCalendarCellHeight(availableHeight.value, weekdayHeight.value, rows, LocalDensity.current.fontScale).dp
    } else layout.calendarCellHeight
    val calendarGridHeight = weekdayHeight + (cellHeight + gridGap) * rows
    val gridNeedsScroll = layout.shortWide && calendarGridHeight > availableHeight + 1.dp

    suspend fun moveToPage(
        targetPage: Int,
        animateAdjacent: Boolean,
        transitionAxis: CalendarDragAxis = CalendarDragAxis.Vertical,
    ) {
        val safeTarget = targetPage.coerceIn(0, 12000)
        val pageDistance = kotlin.math.abs(safeTarget - pagerState.currentPage)
        if (pageDistance == 0) return
        haptics.scene(HapticCue.PageSnap)
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

    val monthControls: @Composable () -> Unit = {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { haptics.menu(true); picker = CalendarPicker.Year }, contentPadding = PaddingValues(horizontal = 6.dp)) {
                Text("${visibleMonth.year}年", color = C.textMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            TextButton(onClick = { haptics.menu(true); picker = CalendarPicker.Month }, contentPadding = PaddingValues(horizontal = 6.dp)) {
                Text("${visibleMonth.monthValue}月", color = C.textMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            if (showToday) TextButton(onClick = {
                onSelectedDateChange(today)
                animationScope.launch { moveToPage(centerPage, animateAdjacent = true) }
            }) { Text("今日") }
            TextButton(onClick = onAddSchedule) {
                FortuneIcon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("新增日程", maxLines = 1)
            }
        }
    }
    val monthContent: @Composable () -> Unit = {
        Column(
            Modifier.fillMaxWidth().padding(vertical = if (layout.shortWide) 0.dp else 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!layout.shortWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "日历",
                    color = C.textMain,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    onClick = onAddSchedule,
                    modifier = Modifier.offset(x = 8.dp),
                    contentPadding = if (layout.compactWidth) {
                        androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    } else {
                        ButtonDefaults.TextButtonContentPadding
                    },
                ) {
                    FortuneIcon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = C.gold,
                        modifier = Modifier.size(if (layout.compactWidth) 18.dp else 24.dp),
                    )
                    Spacer(Modifier.width(if (layout.compactWidth) 2.dp else 4.dp))
                    Text(
                        "新增日程",
                        color = C.gold,
                        style = if (layout.compactWidth) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                }
            }
            Text(
                todayHeader,
                color = C.textSub,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { haptics.menu(true); picker = CalendarPicker.Year }) {
                        Text(
                            "${visibleMonth.year}年",
                            color = C.textMain,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(onClick = { haptics.menu(true); picker = CalendarPicker.Month }) {
                        Text(
                            "${visibleMonth.monthValue}月",
                            color = C.textMain,
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
                    .pointerInput(pagerState, calendarSwipeThreshold, gridNeedsScroll) {
                        awaitEachGesture {
                            val down = awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial,
                            )
                            var dragAxis: CalendarDragAxis? = null
                            val tactile = HapticGestureTrack()
                            val tactileAllowed = down.position.x > 32.dp.toPx() && down.position.x < size.width - 32.dp.toPx()
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
                                if (dragAxis != null && !(gridNeedsScroll && dragAxis == CalendarDragAxis.Vertical)) {
                                    change.consume()
                                    if (change.pressed && tactileAllowed) haptics.gestureProgress(tactile,
                                        (if (dragAxis == CalendarDragAxis.Vertical) kotlin.math.abs(dragY) else kotlin.math.abs(dragX)) /
                                            calendarSwipeThreshold)
                                }
                                if (!change.pressed) break
                            }
                            when {
                                dragAxis == CalendarDragAxis.Vertical &&
                                    !gridNeedsScroll &&
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
                            if (it != selectedDate) haptics.selection()
                            visibleMonth = YearMonth.from(it)
                            onSelectedDateChange(it)
                        },
                        onOpenDateDetail = onOpenDateDetail,
                        modifier = Modifier.fillMaxWidth(),
                        cellHeight = cellHeight,
                        gridGap = gridGap,
                    )
                }
            }
        }
    }
    if (layout.shortWide) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(.64f).fillMaxHeight().verticalScroll(rememberScrollState())) { monthContent() }
            Column(Modifier.weight(.36f).fillMaxHeight().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                monthControls()
                selectedInfo?.let { DateDetail(it, selectedScheduleItems) }
                ScheduleMemorialEntryCard(scheduleItems.size, onOpenMemorial)
            }
        }
    } else Box(Modifier.widthIn(max = 920.dp).fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            monthContent()
            selectedInfo?.let { DateDetail(it, selectedScheduleItems) }
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
    cellHeight: Dp,
    gridGap: Dp,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    var days by remember(month) { mutableStateOf(monthInfoCache.get(month)) }
    LaunchedEffect(month) {
        if (days == null) {
            delay(260L)
            days = withContext(calendarComputationDispatcher) { computeMonthInfo(month) }
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
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(gridGap)) {
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
                        Spacer(Modifier.weight(1f).height(cellHeight))
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
                            cellHeight = cellHeight,
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
    cellHeight: Dp,
) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val date = info.date
    val inMonth = date.monthValue == visibleMonth.monthValue
    val isToday = date == today
    val isSelected = date == selectedDate
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnDoubleClick by rememberUpdatedState(onDoubleClick)
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
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
            .height(cellHeight)
            .background(bgColor, MaterialTheme.shapes.medium)
            .border(1.dp, borderColor, MaterialTheme.shapes.medium)
            .semantics(mergeDescendants = true) {
                contentDescription = "${date.year}年${date.monthValue}月${date.dayOfMonth}日，${info.cellLabel}"
                selected = isSelected
                role = androidx.compose.ui.semantics.Role.Button
                this.onClick(label = "选择日期") {
                    currentOnClick()
                    true
                }
                customActions = listOf(
                    CustomAccessibilityAction("查看当日详情") {
                        currentOnDoubleClick()
                        true
                    },
                )
            }
            .pointerInput(date) {
                var previousTapUptime = Long.MIN_VALUE
                val doubleTapTimeout = ViewConfiguration.getDoubleTapTimeout().toLong()
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    val up = waitForUpOrCancellation()
                    if (up != null) {
                        val elapsed = up.uptimeMillis - previousTapUptime
                        if (elapsed in 40L..doubleTapTimeout) {
                            previousTapUptime = Long.MIN_VALUE
                            currentOnDoubleClick()
                        } else {
                            previousTapUptime = up.uptimeMillis
                            currentOnClick()
                        }
                    }
                }
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = if (layout.shortWide) 0.dp else if (layout.compactHeight) 4.dp else 6.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                date.dayOfMonth.toString(),
                color = when {
                    !inMonth -> C.textSub.copy(alpha = 0.42f)
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    else -> C.textMain
                },
                style = if (layout.shortWide) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                info.cellLabel,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.90f)
                    !inMonth -> C.textSub.copy(alpha = 0.36f)
                    info.solarTerm.isNotBlank() || info.traditionalFestivals.isNotEmpty() -> C.gold
                    else -> C.textSub
                },
                style = if (layout.compactWidth) {
                    MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, lineHeight = 11.sp)
                } else {
                    MaterialTheme.typography.labelSmall
                },
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (hasSchedule) {
                Box(Modifier.size(3.dp).background(if (isSelected) MaterialTheme.colorScheme.onPrimary else C.mint, CircleShape))
            }
        }
    }
}

@Composable
private fun DateDetail(info: CalendarDateInfo, scheduleItems: List<ScheduleItem>) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    GeminiGlassPanel(
        tone = GeminiGlassTone.Neutral,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(layout.cardPadding),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text(
                "${info.date.monthValue}月${info.date.dayOfMonth}日 周${info.weekday}",
                color = C.textMain,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "公历 ${info.date.year}年 · 当年第${info.date.dayOfYear}天",
                color = C.textSub,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "农历 ${info.ganZhiYear}年 · 生肖${info.zodiac} · ${info.lunarMonth}${info.lunarDay}",
                color = C.textSub,
                style = MaterialTheme.typography.bodyMedium,
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
    val layout = LocalFortuneLayout.current
    var detail by remember(date) { mutableStateOf(almanacDayDetailCache.get(date)) }
    LaunchedEffect(date) {
        if (detail == null) {
            detail = withContext(calendarComputationDispatcher) {
                almanacDayDetailCache.get(date) ?: AlmanacDayDetailEngine.create(date).also {
                    almanacDayDetailCache.put(date, it)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                .widthIn(max = 920.dp)
                .fillMaxSize()
                .padding(horizontal = layout.horizontalPadding),
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
                        style = if (layout.compactWidth) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
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
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = MaterialTheme.shapes.large,
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
    GeminiGlassPanel(
        tone = GeminiGlassTone.Neutral,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                title,
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
    val pickerHaptics = LocalAppHaptics.current
    val selectedIndex = values.indexOf(selected).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (selectedIndex - 3).coerceAtLeast(0))
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().height(360.dp)) {
                items(values, key = { it }) { value ->
                    Surface(
                        onClick = { if (value != selected) pickerHaptics.selection(); onSelect(value) },
                        color = if (value == selected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Transparent
                        },
                        contentColor = if (value == selected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                label(value),
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
private val calendarComputationDispatcher = Executors.newSingleThreadExecutor { task ->
    Thread(
        {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
            task.run()
        },
        "zhifou-calendar",
    ).apply { isDaemon = true }
}.asCoroutineDispatcher()
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

private fun cachedCalendarDateInfo(date: LocalDate): CalendarDateInfo? {
    return monthInfoCache.get(YearMonth.from(date))?.firstOrNull { it.date == date }
}

private fun formatFullCalendarDate(date: LocalDate, info: CalendarDateInfo): String {
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
        colors = CardDefaults.cardColors(
            containerColor = if (item.done) {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        ),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            IconButton(onClick = onToggle) {
                FortuneIcon(
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
                FortuneIcon(Icons.Default.Delete, contentDescription = "删除日程", tint = C.textSub)
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
internal fun HistoryScreen(vm: FortuneViewModel, onBack: () -> Unit) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    var confirmClear by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        SubScreenHeader(title = "占卜记录", onBack = onBack) {
            TextButton(onClick = { confirmClear = true }, enabled = vm.history.isNotEmpty()) {
                FortuneIcon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("清空")
            }
        }
        if (vm.history.isEmpty()) {
            EmptyState("还没有占卜记录", "完成一次占卜后，结果会出现在这里")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = layout.horizontalPadding,
                    vertical = layout.verticalPadding,
                ),
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


private val LocalToolSceneHeight = staticCompositionLocalOf { 180.dp }

@Composable
private fun LandscapeToolAction(label: String, icon: ImageVector, onClick: () -> Unit, enabled: Boolean = true) {
    val source = remember { MutableInteractionSource() }
    Surface(onClick = onClick, enabled = enabled, interactionSource = source, color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().then(pressScaleModifier(source)), shape = MaterialTheme.shapes.large) {
        GeminiGlassPanel(tone = GeminiGlassTone.Neutral, modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically) {
                FortuneIcon(icon, null, modifier = Modifier.size(20.dp))
                Text(label, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun ToolScreenLayout(
    title: String,
    onBack: () -> Unit,
    scrollState: ScrollState = rememberScrollState(),
    sideContent: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val layout = LocalFortuneLayout.current
    Column(Modifier.fillMaxSize().imePadding()) {
        SubScreenHeader(title = title, onBack = onBack) {}
        if (layout.shortWide && sideContent != null) {
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().padding(
                horizontal = layout.horizontalPadding, vertical = layout.verticalPadding)) {
                CompositionLocalProvider(LocalToolSceneHeight provides maxHeight.coerceAtLeast(96.dp)) {
                    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        Column(Modifier.weight(.64f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center, content = content)
                        Column(Modifier.weight(.36f).fillMaxHeight().verticalScroll(scrollState),
                            verticalArrangement = Arrangement.spacedBy(8.dp), content = sideContent)
                    }
                }
            }
        } else {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = layout.horizontalPadding, vertical = layout.verticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(layout.sectionSpacing),
            content = content,
        )
        }
    }
}

// Insets are applied by the app scaffold, not again by each page's toolbar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SubScreenHeader(title: String, onBack: () -> Unit, actions: @Composable () -> Unit) {
    val actionLineHeight = with(LocalDensity.current) { MaterialTheme.typography.labelLarge.lineHeight.toDp() }
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        expandedHeight = if (LocalFortuneLayout.current.shortWide) landscapeToolbarHeight(actionLineHeight.value).dp else TopAppBarDefaults.TopAppBarExpandedHeight,
        windowInsets = WindowInsets(0, 0, 0, 0),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        navigationIcon = {
            IconButton(onClick = onBack) {
                FortuneIcon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
            }
        },
        title = {
            Text(
                title,
                style = if (LocalFortuneLayout.current.shortWide) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = { actions() },
    )
}


@Composable
internal fun BackgroundChoice(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    imageRes: Int? = null,
    custom: Boolean = false,
    customImageUri: String = "",
) {
    val palette = LocalFortunePalette.current
    Column(
        modifier = Modifier
            .width(104.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(MaterialTheme.shapes.large)
                .background(
                    if (imageRes == null && !custom) {
                        Brush.linearGradient(
                            0.0f to Color(0xFF0F1116),
                            0.48f to Color(0xFF20242C),
                            0.52f to Color(0xFFF1F3F7),
                            1.0f to Color.White,
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                                MaterialTheme.colorScheme.surfaceContainerLow,
                            ),
                        )
                    },
                )
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.62f)
                    },
                    shape = MaterialTheme.shapes.large,
                ),
            contentAlignment = Alignment.Center,
        ) {
            when {
                imageRes != null -> Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                custom && customImageUri.isNotBlank() -> AppBackgroundImage(
                    style = AppBackgroundStyle.Custom,
                    customImageUri = customImageUri,
                    modifier = Modifier.fillMaxSize(),
                )
                custom -> FortuneIcon(
                    Icons.Default.Wallpaper,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(25.dp),
                )
            }
            if (selected) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(20.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        FortuneIcon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                }
            }
        }
        Text(
            text = label,
            color = if (selected) MaterialTheme.colorScheme.onSurface else palette.textSub,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.heightIn(min = 32.dp),
        )
    }
}

@Composable
internal fun CoinCastingProgressCard(
    lines: List<CoinLineResult>, question: String, currentThrow: CoinLineResult?, startedAt: Long,
    availableHeight: Dp = Dp.Unspecified,
) {
    val C = LocalFortunePalette.current
    val elapsed = rememberOracleElapsed(startedAt, OracleMotionKind.Coins)
    if (LocalFortuneLayout.current.shortWide) {
        GeminiGlassPanel(tone = GeminiGlassTone.Gold, modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(.45f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("铜钱起卦", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("${lines.size}/6", color = C.gold, style = MaterialTheme.typography.labelLarge)
                    }
                    if (question.isNotBlank()) Text("问：$question", style = MaterialTheme.typography.labelSmall,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    CopperCoinTossStage(currentThrow, lines.firstOrNull { it.position == (currentThrow?.position ?: 1) - 1 }, elapsed)
                    Text("日纹阳面 3 · 月纹阴面 2", color = C.textSub, style = MaterialTheme.typography.labelSmall)
                }
                Column(Modifier.weight(.55f)) {
                    for (position in 6 downTo 1) CoinLineRow(position, lines.firstOrNull { it.position == position }, compact = true, dense = true)
                }
            }
        }
        return
    }
    GeminiGlassPanel(
        tone = GeminiGlassTone.Gold,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("铜钱起卦", color = C.textMain, fontWeight = FontWeight.SemiBold)
                    Text(if (question.isBlank()) "初爻起，由下向上投掷" else "问：$question",
                        color = C.textSub, style = MaterialTheme.typography.bodySmall,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text("${lines.size}/6", color = C.gold, style = MaterialTheme.typography.titleMedium)
            }
            CopperCoinTossStage(currentThrow, lines.firstOrNull { it.position == (currentThrow?.position ?: 1) - 1 }, elapsed)
            for (position in 6 downTo 1) {
                CoinLineRow(
                    position = position,
                    line = lines.firstOrNull { it.position == position },
                    compact = true,
                )
            }
            Text(
                "日纹阳面记 3，月纹阴面记 2；每枚独立投掷。",
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
    GeminiGlassPanel(
        tone = GeminiGlassTone.Gold,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
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
                FortuneIcon(
                    if (commentaryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(if (commentaryExpanded) "收起《彖》《象》" else "查看《彖》《象》原文")
            }
            FortuneAnimatedVisibility(commentaryExpanded) {
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
private fun CoinLineRow(position: Int, line: CoinLineResult?, compact: Boolean = false, dense: Boolean = false) {
    val C = LocalFortunePalette.current
    val density = LocalDensity.current.density
    val revealProgress by animateFloatAsState(
        targetValue = if (line == null) 0f else 1f,
        animationSpec = tween(
            durationMillis = COIN_TOSS_REVEAL_DURATION_MS,
            easing = FastOutSlowInEasing,
        ),
        label = "coin-line-reveal-$position",
    )
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = if (dense) 24.dp else if (compact) 36.dp else 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            coinLinePositionLabel(position),
            color = if (line == null) C.textSub else C.textMain,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(if (dense) 28.dp else 36.dp),
        )
        CoinLineStroke(
            line = line,
            modifier = Modifier
                .width(if (dense) 44.dp else 68.dp)
                .height(24.dp)
                .graphicsLayer {
                    if (line != null) {
                        alpha = 0.45f + revealProgress * 0.55f
                        scaleX = 0.72f + revealProgress * 0.28f
                    }
                },
        )
        Spacer(Modifier.width(8.dp))
        if (line == null) {
            Text(
                if (dense) "等待投掷" else "等待第${position}次投掷",
                color = C.textSub,
                style = MaterialTheme.typography.bodySmall,
            )
        } else if (dense) {
            Text("${line.combinationLabel} · ${line.value} ${line.typeLabel}${if (line.isMoving) " · 动" else ""}",
                color = if (line.isMoving) C.rose else C.textMain, style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f))
        } else {
            Column(
                modifier = Modifier.graphicsLayer {
                    alpha = revealProgress
                    translationY = (1f - revealProgress) * 5f * density
                },
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    if (!compact) {
                        line.coinValues.forEach { value ->
                            CoinFaceChip(isYang = value == 3, revealProgress = revealProgress)
                        }
                        Spacer(Modifier.width(3.dp))
                    }
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
private fun CoinFaceChip(isYang: Boolean, revealProgress: Float) {
    val C = LocalFortunePalette.current
    val localDensity = LocalDensity.current
    val density = localDensity.density
    val labelSize = with(localDensity) { 8.dp.toSp() }
    Box(
        modifier = Modifier
            .size(23.dp)
            .graphicsLayer {
                rotationY = (1f - revealProgress) * 90f
                cameraDistance = 8f * density
            }
            .background(
                Brush.radialGradient(
                    colors = if (isYang) {
                        listOf(Color.White.copy(alpha = 0.22f), C.gold.copy(alpha = 0.16f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.12f), MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.26f))
                    },
                    center = Offset(0f, 0f),
                ),
                CircleShape,
            )
            .border(1.dp, if (isYang) C.gold.copy(alpha = 0.88f) else C.line, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            if (isYang) "阳" else "阴",
            color = if (isYang) C.gold else C.textSub,
            fontSize = labelSize,
            lineHeight = labelSize,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
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
internal fun TarotDrawingProgressCard(
    draws: List<TarotDraw>,
    revealedCount: Int,
    question: String,
    startedAt: Long,
    availableHeight: Dp = Dp.Unspecified,
) {
    val C = LocalFortunePalette.current
    val elapsed = rememberOracleElapsed(startedAt, OracleMotionKind.Tarot)
    if (LocalFortuneLayout.current.shortWide) {
        val height = if (availableHeight.value.isFinite()) availableHeight else (LocalConfiguration.current.screenHeightDp - 108).dp
        val cardHeight = ((height - 24.dp) / 2).coerceIn(54.dp, 130.dp)
        GeminiGlassPanel(tone = GeminiGlassTone.Rose, modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(.30f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TarotShuffleStack(elapsed, Modifier)
                    Text("韦特十字牌阵", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("$revealedCount / 10", style = MaterialTheme.typography.labelLarge, color = C.gold)
                    if (question.isNotBlank()) Text("问：$question", style = MaterialTheme.typography.bodySmall,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Column(Modifier.weight(.70f), verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    draws.chunked(5).forEach { row ->
                        Row(Modifier.widthIn(max = cardHeight * .575f * 5 + 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { draw -> TarotAnimatedCard(draw, elapsed, Modifier.weight(1f).aspectRatio(.575f)) }
                            repeat(5 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
        return
    }
    GeminiGlassPanel(
        tone = GeminiGlassTone.Rose,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TarotShuffleStack(elapsed, Modifier.padding(end = 12.dp))
                Column(Modifier.weight(1f)) {
                    Text("正在抽取十字牌阵", color = C.textMain, fontWeight = FontWeight.SemiBold)
                    if (question.isNotBlank()) {
                        Text(
                            "问：$question",
                            color = C.textSub,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Text(
                    "$revealedCount / 10",
                    color = C.gold,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            draws.chunked(5).forEach { rowDraws ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    rowDraws.forEach { draw ->
                        TarotAnimatedCard(
                            draw = draw,
                            elapsed = elapsed,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.575f),
                        )
                    }
                    repeat(5 - rowDraws.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            Text(
                "采用十张牌的韦特十字牌阵；正逆位在洗牌时独立确定。",
                color = C.textSub,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}


@Composable
private fun TarotSpreadDetail(reading: FortuneReading) {
    val C = LocalFortunePalette.current
    var expanded by rememberSaveable(reading.id) { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "韦特十字牌阵 · 10 张",
            color = C.textSub,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        reading.tarotDraws
            .sortedBy(TarotDraw::positionIndex)
            .chunked(5)
            .forEach { rowDraws ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    rowDraws.forEach { draw ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            TarotResultArtwork(
                                draw = draw,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.575f)
                                    .clip(RoundedCornerShape(5.dp))
                                    .border(1.dp, C.gold.copy(alpha = 0.45f), RoundedCornerShape(5.dp)),
                            )
                            Text(
                                "${draw.positionIndex}. ${draw.position.name}",
                                color = C.textSub,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                draw.card.nameZh,
                                color = C.textMain,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                minLines = 2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(draw.orientationLabel, color = C.textSub,
                                style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                        }
                    }
                    repeat(5 - rowDraws.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        TextButton(onClick = { expanded = !expanded }) {
            FortuneIcon(
                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
            )
            Spacer(Modifier.width(4.dp))
            Text(if (expanded) "收起逐牌解释" else "查看十张牌位与逐牌解释")
        }
        FortuneAnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                reading.tarotDraws.sortedBy(TarotDraw::positionIndex).forEach { draw ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                "${draw.positionIndex}. ${draw.position.name} · ${draw.card.nameZh}${draw.orientationLabel}",
                                color = C.textMain,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(draw.position.prompt, color = C.textSub, style = MaterialTheme.typography.bodySmall)
                            Text(
                                draw.card.classificationLabel,
                                color = C.textSub,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                draw.keywords.joinToString(" · "),
                                color = C.gold,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(draw.meaning, color = C.textMain, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "牌面：${draw.card.imageDescription}",
                                color = C.textSub,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
                Text(
                    "牌位采用 A. E. Waite《塔罗图解钥匙》第三部分的十字展开结构；本应用不按性别、年龄或外貌猜测预设指示牌。",
                    color = C.textSub,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun ReadingCard(reading: FortuneReading, compact: Boolean = false) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val dense = compact || layout.compactWidth
    GeminiGlassPanel(
        tone = GeminiGlassTone.Neutral,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(if (dense) 12.dp else 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        reading.title,
                        color = C.textMain,
                        style = if (dense) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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
            if (reading.kind == FourCardTarot.KIND) {
                FourCardReadingSummary(reading)
            } else if (reading.tarotDraws.isNotEmpty()) {
                TarotSpreadDetail(reading)
            }
            if (reading.kind == "铜钱卦") {
                Text("本地基础解读", color = C.textSub, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            } else if (reading.kind == "韦特塔罗" || reading.kind == FourCardTarot.KIND) {
                Text("本地牌阵解读", color = C.textSub, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }
            Text(
                reading.body,
                color = C.textMain,
                style = if (dense) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyLarge,
            )
            Text(
                reading.advice,
                color = C.gold,
                style = if (dense) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
            )
            if (reading.aiStatus.isNotBlank()) {
                Text(reading.aiStatus, color = C.textSub, style = MaterialTheme.typography.bodyMedium)
            }
            if (reading.aiInterpretation.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.secondary.copy(alpha = if (C.isLight) 0.12f else 0.09f),
                                    Color.Transparent,
                                ),
                            ),
                            MaterialTheme.shapes.medium,
                        )
                        .padding(vertical = 12.dp),
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(3.dp)
                            .background(C.mint, CircleShape),
                    )
                    Column(
                        Modifier.padding(start = 12.dp, end = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            when (reading.kind) {
                                "铜钱卦" -> "AI 详细解读"
                                "韦特塔罗" -> "AI 塔罗解读"
                                else -> "AI 解读"
                            },
                            color = C.mint,
                            fontWeight = FontWeight.SemiBold,
                        )
                        MarkdownText(reading.aiInterpretation, color = C.textMain, style = MaterialTheme.typography.bodyMedium, onLight = C.isLight)
                    }
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f),
        contentColor = MaterialTheme.colorScheme.tertiary,
        shape = CircleShape,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        )
    }
}

@Composable
private fun FortuneDial(score: Int) {
    val C = LocalFortunePalette.current
    val layout = LocalFortuneLayout.current
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val dialColors = if (C.isLight) {
        listOf(
            Color(0xFF6F95F8),
            Color(0xFF45C7B5),
            Color(0xFFF0C859),
            Color(0xFF6F95F8),
        )
    } else {
        listOf(
            Color(0xFF91AFFF),
            C.mint,
            C.gold,
            Color(0xFF91AFFF),
        )
    }
    GeminiGlassPanel(
        modifier = Modifier.fillMaxWidth(),
        tone = GeminiGlassTone.Neutral,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .padding(if (layout.compactWidth) 16.dp else 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (layout.compactWidth) 12.dp else 18.dp),
        ) {
            val dialSize = if (layout.compactWidth) 84.dp else 112.dp
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(dialSize)) {
                Canvas(Modifier.fillMaxSize()) {
                    val stroke = Stroke(
                        width = (if (layout.compactWidth) 9.dp else 12.dp).toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawArc(trackColor, -220f, 260f, false, style = stroke, size = Size(size.width, size.height))
                    drawArc(
                        brush = Brush.sweepGradient(dialColors),
                        startAngle = -220f,
                        sweepAngle = 260f * (score / 100f),
                        useCenter = false,
                        style = stroke,
                        size = Size(size.width, size.height),
                    )
                }
                Text(
                    "$score",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(if (layout.compactWidth) 5.dp else 8.dp),
            ) {
                Text(
                    "今日节奏",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = if (layout.compactWidth) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                )
                Text(
                    "用于排列今日提示的相对刻度，不代表概率或确定结论。",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = if (layout.compactWidth) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun InsightStrip(
    insights: List<DailyInsight>,
    aiConnected: Boolean,
    onTap: (DailyInsightCategory) -> Unit,
) {
    val layout = LocalFortuneLayout.current
    if (layout.shortWide || layout.compactWidth || LocalDensity.current.fontScale > 1.2f) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            insights.forEach { insight ->
                InsightTile(
                    insight = insight,
                    aiConnected = aiConnected,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onTap(insight.category) },
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            insights.forEach { insight ->
                InsightTile(
                    insight = insight,
                    aiConnected = aiConnected,
                    modifier = Modifier.weight(1f),
                    onClick = { onTap(insight.category) },
                )
            }
        }
    }
}

@Composable
internal fun InsightTile(
    insight: DailyInsight,
    aiConnected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember(insight.category) { MutableInteractionSource() }
    val colors = MaterialTheme.colorScheme
    val palette = colors.dailyInsightColors(insight.category)
    val accent = palette.accent
    val compact = LocalFortuneLayout.current.shortWide || LocalFortuneLayout.current.compactWidth || LocalDensity.current.fontScale > 1.2f
    val icon = when (insight.category) {
        DailyInsightCategory.Career -> Icons.Outlined.WorkOutline
        DailyInsightCategory.Relationship -> Icons.Outlined.FavoriteBorder
        DailyInsightCategory.Finance -> Icons.Outlined.AccountBalanceWallet
    }
    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .heightIn(min = if (LocalFortuneLayout.current.shortWide) 60.dp else if (compact) 76.dp else 120.dp)
            .then(pressScaleModifier(interactionSource)),
        color = palette.container,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.large,
    ) {
        if (compact) {
            Row(
                Modifier.padding(horizontal = if (LocalFortuneLayout.current.shortWide) 12.dp else 16.dp,
                    vertical = if (LocalFortuneLayout.current.shortWide) 8.dp else 16.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                FortuneIcon(icon, null, tint = accent, modifier = Modifier.size(24.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(insight.category.label, color = accent, style = MaterialTheme.typography.labelMedium)
                    Text(insight.headline, style = MaterialTheme.typography.titleSmall,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                if (aiConnected) FortuneIcon(Icons.Default.ChevronRight, null, tint = accent, modifier = Modifier.size(18.dp))
            }
        } else {
            Column(
                Modifier.padding(horizontal = 13.dp, vertical = 15.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FortuneIcon(icon, null, tint = accent, modifier = Modifier.size(18.dp))
                    Text(insight.category.label, color = accent, style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f))
                    if (aiConnected) FortuneIcon(Icons.Default.ChevronRight, null, tint = accent, modifier = Modifier.size(14.dp))
                }
                Text(insight.headline, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold, minLines = 2, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun DailyAiInsightDialog(
    insight: DailyInsight,
    aiContent: String?,
    loading: Boolean,
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    val C = LocalFortunePalette.current
    val configuration = LocalConfiguration.current
    val colors = MaterialTheme.colorScheme
    val palette = colors.dailyInsightColors(insight.category)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            val scrimInteraction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.52f))
                    .clickable(
                        interactionSource = scrimInteraction,
                        indication = null,
                        onClick = onDismiss,
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    color = palette.container,
                    contentColor = palette.content,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .heightIn(max = (configuration.screenHeightDp * 0.82f).dp)
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                            ) {
                                Text(
                                    "${insight.category.label} · 今日解读",
                                    color = palette.content,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    insight.headline,
                                    color = palette.accent,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                            val completeInteraction = remember { MutableInteractionSource() }
                            androidx.compose.material3.TooltipBox(
                                positionProvider = androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider(
                                    androidx.compose.material3.TooltipAnchorPosition.Above),
                                tooltip = { PlainTooltip { Text("完成") } },
                                state = androidx.compose.material3.rememberTooltipState(),
                            ) {
                                FilledIconButton(
                                    onClick = onDismiss,
                                    interactionSource = completeInteraction,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = palette.accent,
                                        contentColor = palette.container,
                                    ),
                                    modifier = Modifier.size(48.dp).then(pressScaleModifier(completeInteraction)),
                                ) {
                                    FortuneIcon(Icons.Default.Check, contentDescription = "完成", modifier = Modifier.size(24.dp))
                                }
                            }
                        }

                        HorizontalDivider(color = palette.accent.copy(alpha = 0.16f))

                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Text(
                                insight.summary,
                                color = palette.content,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            when {
                                loading -> Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = palette.accent,
                                        strokeWidth = 2.dp,
                                    )
                                    Text("正在生成今日解读", color = palette.content)
                                }
                                !aiContent.isNullOrBlank() -> MarkdownText(
                                    aiContent,
                                    color = palette.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    onLight = C.isLight,
                                )
                                error.isNotBlank() -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(error, color = C.rose, style = MaterialTheme.typography.bodyMedium)
                                    TextButton(onClick = onRetry) {
                                        Text("重试", color = palette.accent)
                                    }
                                }
                            }
                        }
                    }
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
            FortuneIcon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = C.line, modifier = Modifier.size(34.dp))
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

    @Volatile
    private var calendarCacheLoaded = false
    private var calendarCacheJob: Job? = null

    fun prepareCalendarCache() {
        if (calendarCacheLoaded || calendarCacheJob?.isActive == true) return
        calendarCacheJob = viewModelScope.launch(calendarComputationDispatcher) {
            ensureCalendarCache()
            calendarCacheLoaded = true
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
    var coinCastingStartedAt by mutableStateOf(0L)
        private set
    var coinCastingThrow by mutableStateOf<CoinLineResult?>(null)
        private set
    var coinCastingLines by mutableStateOf<List<CoinLineResult>>(emptyList())
        private set
    var coinCastingQuestion by mutableStateOf("")
        private set
    var tarotDrawing by mutableStateOf(false)
        private set
    var tarotDrawingStartedAt by mutableStateOf(0L)
        private set
    var tarotDrawingCards by mutableStateOf<List<TarotDraw>>(emptyList())
        private set
    var tarotRevealedCount by mutableIntStateOf(0)
        private set
    var tarotDrawingQuestion by mutableStateOf("")
        private set
    var scheduleItems by mutableStateOf(repo.loadScheduleItems())
        private set
    var selectedScheduleDate by mutableStateOf(LocalDate.now())
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
    var backgroundStyle by mutableStateOf(repo.backgroundStyle)
        private set
    var customBackgroundUri by mutableStateOf(repo.customBackgroundUri)
        private set
    var glassMotionEnabled by mutableStateOf(repo.glassMotionEnabled)
        private set
    var hapticsEnabled by mutableStateOf(repo.hapticsEnabled)
        private set
    private val initialFortuneDate = LocalDate.now()
    var todayFortune by mutableStateOf(
        DailyFortuneEngine.create(
            date = initialFortuneDate,
            nickname = nickname,
            birthDateText = birthDate,
            keywordsText = fortuneKeywords,
            almanacOverride = repo.loadDailyAlmanac(initialFortuneDate),
        ).also { repo.saveDailyAlmanac(it.almanac) }
    )
        private set
    internal var aiProviderId by mutableStateOf(repo.aiProviderId)
        private set
    var aiApiKey by mutableStateOf(repo.aiApiKey)
        private set
    var aiModel by mutableStateOf(repo.aiModel)
        private set
    var aiEndpoint by mutableStateOf(repo.aiEndpoint)
        private set
    var aiConnectionStatus by mutableStateOf(AiConnectionStatus.Unconfigured)
        private set
    var aiConnectionMessage by mutableStateOf("请填写 API Key、模型和接口地址")
        private set
    var dailyAiInsights by mutableStateOf(repo.loadDailyAiInsights(todayFortune.almanac.date))
        private set
    var dailyAiInsightLoading by mutableStateOf<DailyInsightCategory?>(null)
        private set
    var dailyAiInsightErrorCategory by mutableStateOf<DailyInsightCategory?>(null)
        private set
    var dailyAiInsightError by mutableStateOf("")
        private set
    private var aiValidationJob: Job? = null
    internal var cloudSpeechProviderId by mutableStateOf(repo.cloudSpeechProviderId)
        private set
    var cloudSpeechEnabled by mutableStateOf(repo.cloudSpeechEnabled)
        private set
    var cloudSpeechApiKey by mutableStateOf(repo.cloudSpeechApiKey)
        private set
    var cloudSpeechModel by mutableStateOf(repo.cloudSpeechModel)
        private set
    var cloudSpeechEndpoint by mutableStateOf(repo.cloudSpeechEndpoint)
        private set
    var speechConnectionStatus by mutableStateOf(AiConnectionStatus.Unconfigured)
        private set
    var speechConnectionMessage by mutableStateOf("请选择平台、模型并填写语音 API Key")
        private set
    private var speechTestJob: Job? = null

    init {
        restoreAiConnectionState()
        restoreCloudSpeechConnectionState()
    }

    val recentOracleReadings: List<FortuneReading>
        get() {
            val cutoff = System.currentTimeMillis() - 3L * 24 * 60 * 60 * 1_000
            return history
                .asSequence()
                .filter { it.kind == "铜钱卦" || it.kind == "答案之书" || it.kind == "韦特塔罗" }
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
        if (coinCasting || tarotDrawing) return
        val castingQuestion = question.trim()
        question = ""
        coinCasting = true
        coinCastingQuestion = castingQuestion
        coinCastingLines = emptyList()
        coinCastingStartedAt = SystemClock.uptimeMillis()
        viewModelScope.launch {
            val lines = mutableListOf<CoinLineResult>()
            try {
                repeat(6) { index ->
                    awaitOracleMoment(coinCastingStartedAt, OracleMotion.coinRoundStart(index))
                    val line = oracle.tossCoinLine(position = index + 1)
                    coinCastingThrow = line
                    awaitOracleMoment(coinCastingStartedAt, OracleMotion.coinRoundSettled(index))
                    lines += line
                    coinCastingLines = lines.toList()
                }
                awaitOracleMoment(coinCastingStartedAt, OracleMotion.duration(OracleMotionKind.Coins))
                save(oracle.coin(castingQuestion, lines))
            } finally {
                coinCasting = false
                coinCastingLines = emptyList()
                coinCastingThrow = null
                coinCastingQuestion = ""
            }
        }
    }

    fun drawAnswerBook() {
        save(oracle.answerBook(question))
        question = ""
    }

    fun drawTarot() {
        if (coinCasting || tarotDrawing) return
        val drawingQuestion = question.trim()
        val draws = TarotDeck.drawCelticCross()
        question = ""
        tarotDrawing = true
        tarotDrawingQuestion = drawingQuestion
        tarotDrawingCards = draws
        tarotRevealedCount = 0
        tarotDrawingStartedAt = SystemClock.uptimeMillis()
        viewModelScope.launch {
            try {
                draws.indices.forEach { index ->
                    awaitOracleMoment(tarotDrawingStartedAt, OracleMotion.cardTurn(index) + OracleMotion.cardFlipMs / 2)
                    tarotRevealedCount = index + 1
                }
                awaitOracleMoment(tarotDrawingStartedAt, OracleMotion.duration(OracleMotionKind.Tarot))
                save(oracle.tarot(drawingQuestion, draws))
            } finally {
                tarotDrawing = false
                tarotDrawingCards = emptyList()
                tarotRevealedCount = 0
                tarotDrawingQuestion = ""
            }
        }
    }

    fun refreshToday(date: LocalDate = LocalDate.now()) {
        todayFortune = DailyFortuneEngine.create(
            date = date,
            nickname = nickname,
            birthDateText = birthDate,
            keywordsText = fortuneKeywords,
            almanacOverride = repo.loadDailyAlmanac(date),
        )
        repo.saveDailyAlmanac(todayFortune.almanac)
        dailyAiInsights = repo.loadDailyAiInsights(date)
        dailyAiInsightLoading = null
        dailyAiInsightErrorCategory = null
        dailyAiInsightError = ""
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

    fun updateBackgroundStyle(value: AppBackgroundStyle) {
        backgroundStyle = value
        repo.backgroundStyle = value
    }

    fun updateCustomBackgroundUri(value: String) {
        customBackgroundUri = value
        repo.customBackgroundUri = value
        if (value.isNotBlank()) updateBackgroundStyle(AppBackgroundStyle.Custom)
    }

    fun updateGlassMotionEnabled(value: Boolean) {
        glassMotionEnabled = value
        repo.glassMotionEnabled = value
    }

    fun updateHapticsEnabled(value: Boolean) {
        hapticsEnabled = value
        repo.hapticsEnabled = value
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

    internal fun applyAiConfiguration(configuration: AiConfiguration, test: Boolean = false) {
        val next = configuration.normalized()
        if (next.validationError() != null) return
        val changed = next.endpoint != aiEndpoint || next.model != aiModel || next.apiKey != aiApiKey
        repo.saveAiConfiguration(next)
        aiProviderId = next.providerId
        aiEndpoint = next.endpoint
        aiModel = next.model
        aiApiKey = next.apiKey
        if (changed) onAiConfigurationChanged()
        if (test) {
            aiValidationJob?.cancel()
            validateAiConnection(force = true)
        }
    }

    fun ensureAiReady(): Boolean {
        if (aiConnectionStatus == AiConnectionStatus.Connected) return true
        if (aiConnectionStatus != AiConnectionStatus.NoNetwork || !hasValidatedInternet(getApplication())) {
            return false
        }
        val fingerprint = aiConfigurationFingerprint(aiEndpoint, aiModel, aiApiKey)
        return if (fingerprint.isNotBlank() && repo.validatedAiFingerprint == fingerprint) {
            aiConnectionStatus = AiConnectionStatus.Connected
            aiConnectionMessage = "当前配置已通过连接验证"
            true
        } else {
            validateAiConnection(force = false)
            false
        }
    }

    fun requestDailyAiInsight(category: DailyInsightCategory, forceRetry: Boolean = false) {
        val date = todayFortune.almanac.date
        if (!forceRetry && dailyAiInsights[category]?.isNotBlank() == true) return
        if (dailyAiInsightLoading != null) return
        if (aiConnectionStatus != AiConnectionStatus.Connected) {
            dailyAiInsightErrorCategory = category
            dailyAiInsightError = when (aiConnectionStatus) {
                AiConnectionStatus.NoNetwork -> "当前没有可用网络，请联网后重试。"
                AiConnectionStatus.Checking -> "AI 配置正在验证，请稍候。"
                AiConnectionStatus.Failed -> "AI 配置尚未通过验证，请在设置中检查后重试。"
                else -> "请先在“我的 > 设置”中完成 AI 配置和连接测试。"
            }
            return
        }
        if (!hasValidatedInternet(getApplication())) {
            aiConnectionStatus = AiConnectionStatus.NoNetwork
            aiConnectionMessage = "配置未判定失效，当前设备没有可用网络"
            dailyAiInsightErrorCategory = category
            dailyAiInsightError = "当前没有可用网络，请联网后重试。"
            return
        }

        dailyAiInsightLoading = category
        dailyAiInsightErrorCategory = null
        dailyAiInsightError = ""
        val snapshot = todayFortune
        viewModelScope.launch {
            val result = DailyAiInsightClient.explain(
                endpoint = aiEndpoint,
                apiKey = aiApiKey,
                model = aiModel,
                snapshot = snapshot,
                category = category,
                nickname = nickname,
                birthDate = birthDate,
                keywords = fortuneKeywords,
            )
            result.fold(
                onSuccess = { content ->
                    if (todayFortune.almanac.date == date) {
                        dailyAiInsights = repo.saveDailyAiInsight(
                            DailyAiInsight(date.toString(), category, content)
                        )
                    }
                },
                onFailure = { error ->
                    dailyAiInsightErrorCategory = category
                    dailyAiInsightError = "AI 解读失败：${error.message ?: "请稍后重试"}"
                },
            )
            dailyAiInsightLoading = null
        }
    }

    private fun restoreAiConnectionState() {
        if (!isAiConfigurationComplete(aiEndpoint, aiApiKey, aiModel)) {
            aiConnectionStatus = AiConnectionStatus.Unconfigured
            aiConnectionMessage = "请填写 API Key、模型和接口地址"
            return
        }
        val fingerprint = aiConfigurationFingerprint(aiEndpoint, aiModel, aiApiKey)
        if (repo.validatedAiFingerprint == fingerprint) {
            aiConnectionStatus = AiConnectionStatus.Connected
            aiConnectionMessage = "当前配置已通过连接验证"
        } else if (repo.attemptedAiFingerprint == fingerprint) {
            if (repo.lastAiValidationOutcome == AiConnectionStatus.NoNetwork.name) {
                aiConnectionStatus = AiConnectionStatus.NoNetwork
                aiConnectionMessage = "上次验证时没有可用网络，请手动测试连接"
            } else {
                aiConnectionStatus = AiConnectionStatus.Failed
                aiConnectionMessage = "上次连接测试未通过，请手动重试"
            }
        } else {
            aiConnectionStatus = AiConnectionStatus.NeedsValidation
            aiConnectionMessage = "配置已修改，等待自动验证"
            scheduleAiValidation()
        }
    }

    private fun onAiConfigurationChanged() {
        aiValidationJob?.cancel()
        repo.validatedAiFingerprint = ""
        repo.attemptedAiFingerprint = ""
        repo.lastAiValidationOutcome = ""
        if (!isAiConfigurationComplete(aiEndpoint, aiApiKey, aiModel)) {
            aiConnectionStatus = AiConnectionStatus.Unconfigured
            aiConnectionMessage = "请填写 API Key、模型和接口地址"
            return
        }
        aiConnectionStatus = AiConnectionStatus.NeedsValidation
        aiConnectionMessage = "配置已修改，等待自动验证"
        scheduleAiValidation()
    }

    private fun scheduleAiValidation() {
        aiValidationJob?.cancel()
        val expectedFingerprint = aiConfigurationFingerprint(aiEndpoint, aiModel, aiApiKey)
        aiValidationJob = viewModelScope.launch {
            delay(1_500L)
            if (expectedFingerprint == aiConfigurationFingerprint(aiEndpoint, aiModel, aiApiKey)) {
                validateAiConnection(force = false)
            }
        }
    }

    private fun validateAiConnection(force: Boolean) {
        if (aiConnectionStatus == AiConnectionStatus.Checking) return
        if (!isAiConfigurationComplete(aiEndpoint, aiApiKey, aiModel)) {
            aiConnectionStatus = AiConnectionStatus.Unconfigured
            aiConnectionMessage = "请先完整填写 API Key、模型和接口地址"
            return
        }
        val fingerprint = aiConfigurationFingerprint(aiEndpoint, aiModel, aiApiKey)
        if (!force && repo.validatedAiFingerprint == fingerprint) {
            aiConnectionStatus = AiConnectionStatus.Connected
            aiConnectionMessage = "当前配置已通过连接验证"
            return
        }
        if (!hasValidatedInternet(getApplication())) {
            repo.attemptedAiFingerprint = fingerprint
            repo.lastAiValidationOutcome = AiConnectionStatus.NoNetwork.name
            aiConnectionStatus = AiConnectionStatus.NoNetwork
            aiConnectionMessage = "当前没有可用网络，尚未验证配置"
            return
        }

        aiValidationJob?.cancel()
        aiConnectionStatus = AiConnectionStatus.Checking
        aiConnectionMessage = "正在测试模型连接"
        viewModelScope.launch {
            val result = AiConnectionClient.test(aiEndpoint, aiApiKey, aiModel)
            if (fingerprint != aiConfigurationFingerprint(aiEndpoint, aiModel, aiApiKey)) {
                return@launch
            }
            result.fold(
                onSuccess = {
                    repo.validatedAiFingerprint = fingerprint
                    repo.attemptedAiFingerprint = fingerprint
                    repo.lastAiValidationOutcome = AiConnectionStatus.Connected.name
                    aiConnectionStatus = AiConnectionStatus.Connected
                    aiConnectionMessage = "连接成功，模型可以正常使用"
                },
                onFailure = { error ->
                    repo.validatedAiFingerprint = ""
                    repo.attemptedAiFingerprint = fingerprint
                    repo.lastAiValidationOutcome = AiConnectionStatus.Failed.name
                    aiConnectionStatus = AiConnectionStatus.Failed
                    aiConnectionMessage = "连接失败：${error.message ?: "请检查配置"}"
                },
            )
        }
    }

    fun updateCloudSpeechEnabled(value: Boolean) {
        cloudSpeechEnabled = value
        repo.cloudSpeechEnabled = value
    }

    internal fun applyCloudSpeechConfiguration(configuration: CloudSpeechConfig) {
        val next = configuration.normalized()
        if (next.validationError() != null) return
        val changed = next.endpoint != cloudSpeechEndpoint || next.model != cloudSpeechModel || next.apiKey != cloudSpeechApiKey
        if (changed) speechTestJob?.cancel()
        repo.saveCloudSpeechConfiguration(next, changed)
        cloudSpeechProviderId = next.providerId
        cloudSpeechEndpoint = next.endpoint
        cloudSpeechModel = next.model
        cloudSpeechApiKey = next.apiKey
        if (changed) restoreCloudSpeechConnectionState()
    }

    private fun restoreCloudSpeechConnectionState() {
        val restored = restoredSpeechConnectionState(
            currentCloudSpeechConfig(), repo.validatedSpeechFingerprint,
            repo.attemptedSpeechFingerprint, repo.lastSpeechTestOutcome,
        )
        speechConnectionStatus = restored.status
        speechConnectionMessage = restored.message
    }

    private fun currentCloudSpeechConfig() = CloudSpeechConfig(
        cloudSpeechEndpoint, cloudSpeechApiKey, cloudSpeechModel, cloudSpeechProviderId,
    )

    fun testCloudSpeechConnection() {
        if (speechConnectionStatus == AiConnectionStatus.Checking) return
        val config = currentCloudSpeechConfig()
        if (config.apiKey.isBlank() || config.validationError() != null) {
            speechConnectionStatus = AiConnectionStatus.Unconfigured
            speechConnectionMessage = config.validationError() ?: "请先填写语音 API Key"
            return
        }
        val fingerprint = aiConfigurationFingerprint(config.endpoint, config.model, config.apiKey)
        if (!hasValidatedInternet(getApplication())) {
            repo.saveSpeechTestOutcome(fingerprint, AiConnectionStatus.NoNetwork)
            speechConnectionStatus = AiConnectionStatus.NoNetwork
            speechConnectionMessage = "当前没有可用网络，尚未验证语音配置"
            return
        }
        speechConnectionStatus = AiConnectionStatus.Checking
        speechConnectionMessage = "正在使用静音片段测试语音接口"
        speechTestJob = viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { ConfiguredSpeechTranscriber.testConnection(config) }
            if (fingerprint != aiConfigurationFingerprint(cloudSpeechEndpoint, cloudSpeechModel, cloudSpeechApiKey)) return@launch
            result.fold(
                onSuccess = {
                    repo.saveSpeechTestOutcome(fingerprint, AiConnectionStatus.Connected)
                    speechConnectionStatus = AiConnectionStatus.Connected
                    speechConnectionMessage = "语音接口连接成功（静音测试，不代表识别准确率）"
                },
                onFailure = { error ->
                    repo.saveSpeechTestOutcome(fingerprint, AiConnectionStatus.Failed)
                    speechConnectionStatus = AiConnectionStatus.Failed
                    speechConnectionMessage = "语音连接测试失败：${error.message ?: "请检查平台、模型和密钥"}"
                },
            )
        }
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

    fun selectScheduleDate(date: LocalDate) {
        selectedScheduleDate = date
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

    internal var fourCardAiBusyIds by mutableStateOf<Set<Long>>(emptySet())
        private set

    internal fun recordFourCardTarot(session: FourCardSession) {
        if (history.any { it.id == session.id }) return
        val reading = FourCardTarot.reading(session)
        history = repo.save(reading)
        latestReading = reading
    }

    internal fun explainFourCardTarot(id: Long) {
        val reading = history.firstOrNull { it.id == id && it.kind == FourCardTarot.KIND } ?: return
        if (id in fourCardAiBusyIds || reading.aiInterpretation.isNotBlank() ||
            !isAiConfigurationComplete(aiEndpoint, aiApiKey, aiModel)) return
        fourCardAiBusyIds = fourCardAiBusyIds + id
        val pending = reading.copy(aiStatus = "AI 解读生成中")
        history = repo.replaceReading(pending)
        generateAiInterpretation(pending).invokeOnCompletion { fourCardAiBusyIds = fourCardAiBusyIds - id }
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

    private fun generateAiInterpretation(reading: FortuneReading): Job {
        val tarotContext = if (reading.kind == "韦特塔罗" || reading.kind == FourCardTarot.KIND) currentTarotAiContext() else null
        return viewModelScope.launch {
            val result = AiInterpreter.interpret(
                endpoint = aiEndpoint,
                apiKey = aiApiKey,
                model = aiModel,
                reading = reading,
                tarotContext = tarotContext,
            )
            val updated = result.fold(
                onSuccess = { text -> reading.copy(aiInterpretation = text, aiStatus = "") },
                onFailure = { error -> reading.copy(aiStatus = "AI 解读失败：${error.message ?: "请检查网络、Key 或模型配置"}") },
            )
            latestReading = updated
            history = repo.replaceReading(updated)
        }
    }

    private fun currentTarotAiContext(): TarotAiContext {
        val almanac = todayFortune.almanac
        return TarotAiContext(
            nickname = nickname,
            birthDate = birthDate,
            keywords = fortuneKeywords,
            dateLabel = almanac.dateLabel,
            lunarLabel = almanac.lunarLabel,
            dayGanZhi = almanac.dayGanZhi,
            solarTerm = almanac.solarTerm,
            suitable = almanac.suitable,
            avoid = almanac.avoid,
            directions = "喜神${almanac.joyDirection}、福神${almanac.fortuneDirection}、财神${almanac.wealthDirection}",
            clash = listOf(almanac.clash, almanac.shaDirection).filter(String::isNotBlank).joinToString("、"),
        )
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
    val tarotDraws: List<TarotDraw> = emptyList(),
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

    var backgroundStyle: AppBackgroundStyle
        get() = runCatching {
            AppBackgroundStyle.valueOf(
                prefs.getString("background_style", AppBackgroundStyle.Solid.name)
                    ?: AppBackgroundStyle.Solid.name,
            )
        }.getOrDefault(AppBackgroundStyle.Solid)
        set(value) = prefs.edit().putString("background_style", value.name).apply()

    var customBackgroundUri: String
        get() = prefs.getString("custom_background_uri", "").orEmpty()
        set(value) = prefs.edit().putString("custom_background_uri", value).apply()

    var glassMotionEnabled: Boolean
        get() = prefs.getBoolean("glass_motion_enabled", true)
        set(value) = prefs.edit().putBoolean("glass_motion_enabled", value).apply()

    var hapticsEnabled: Boolean
        get() = prefs.getBoolean("haptics_enabled", true)
        set(value) = prefs.edit().putBoolean("haptics_enabled", value).apply()

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

    internal val aiProviderId: String
        get() = prefs.getString("ai_provider_id", AiProviderCatalog.CUSTOM) ?: AiProviderCatalog.CUSTOM

    internal fun saveAiConfiguration(configuration: AiConfiguration) {
        prefs.edit()
            .putString("ai_provider_id", configuration.providerId)
            .putString("ai_api_key", configuration.apiKey)
            .putString("ai_model", configuration.model)
            .putString("ai_endpoint", configuration.endpoint)
            .apply()
    }

    var aiModel: String
        get() = prefs.getString("ai_model", "gpt-4o-mini") ?: "gpt-4o-mini"
        set(value) = prefs.edit().putString("ai_model", value.ifBlank { "gpt-4o-mini" }).apply()

    var aiEndpoint: String
        get() = prefs.getString("ai_endpoint", "https://api.openai.com/v1/chat/completions")
            ?: "https://api.openai.com/v1/chat/completions"
        set(value) = prefs.edit()
            .putString("ai_endpoint", value.ifBlank { "https://api.openai.com/v1/chat/completions" })
            .apply()

    var validatedAiFingerprint: String
        get() = prefs.getString("validated_ai_fingerprint", "") ?: ""
        set(value) = prefs.edit().putString("validated_ai_fingerprint", value).apply()

    var attemptedAiFingerprint: String
        get() = prefs.getString("attempted_ai_fingerprint", "") ?: ""
        set(value) = prefs.edit().putString("attempted_ai_fingerprint", value).apply()

    var lastAiValidationOutcome: String
        get() = prefs.getString("last_ai_validation_outcome", "") ?: ""
        set(value) = prefs.edit().putString("last_ai_validation_outcome", value).apply()

    fun loadDailyAiInsights(date: LocalDate): Map<DailyInsightCategory, String> {
        val raw = prefs.getString("daily_ai_insights", "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            buildMap {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    if (item.optString("date") != date.toString()) continue
                    val category = DailyInsightCategory.entries.firstOrNull {
                        it.key == item.optString("category")
                    } ?: continue
                    item.optString("content").trim().takeIf(String::isNotBlank)?.let { content ->
                        put(category, content)
                    }
                }
            }
        }.getOrDefault(emptyMap())
    }

    fun saveDailyAiInsight(insight: DailyAiInsight): Map<DailyInsightCategory, String> {
        val raw = prefs.getString("daily_ai_insights", "[]") ?: "[]"
        val retained = runCatching {
            val array = JSONArray(raw)
            (0 until array.length())
                .map { array.getJSONObject(it) }
                .filterNot {
                    it.optString("date") == insight.date &&
                        it.optString("category") == insight.category.key
                }
                .filter { item ->
                    runCatching {
                        val itemDate = LocalDate.parse(item.optString("date"))
                        !itemDate.isBefore(LocalDate.parse(insight.date).minusDays(6))
                    }.getOrDefault(false)
                }
        }.getOrDefault(emptyList())
        val next = (retained + JSONObject()
            .put("date", insight.date)
            .put("category", insight.category.key)
            .put("content", insight.content.take(4_000)))
            .takeLast(21)
        val array = JSONArray()
        next.forEach(array::put)
        prefs.edit().putString("daily_ai_insights", array.toString()).apply()
        return loadDailyAiInsights(LocalDate.parse(insight.date))
    }

    fun loadDailyAlmanac(date: LocalDate): DailyAlmanacInfo? {
        val raw = prefs.getString("daily_almanac", null) ?: return null
        return runCatching {
            val item = JSONObject(raw)
            val cachedDate = LocalDate.parse(item.getString("date"))
            if (cachedDate != date || item.optInt("schema") != 2) return@runCatching null
            fun stringList(key: String): List<String> {
                val array = item.optJSONArray(key) ?: return emptyList()
                return (0 until array.length()).map(array::getString)
            }
            DailyAlmanacInfo(
                date = cachedDate,
                dateLabel = item.getString("dateLabel"),
                lunarLabel = item.getString("lunarLabel"),
                dayGanZhi = item.getString("dayGanZhi"),
                solarTerm = item.optString("solarTerm"),
                joyDirection = item.getString("joyDirection"),
                fortuneDirection = item.getString("fortuneDirection"),
                wealthDirection = item.getString("wealthDirection"),
                yangNobleDirection = item.getString("yangNobleDirection"),
                yinNobleDirection = item.getString("yinNobleDirection"),
                suitable = stringList("suitable"),
                avoid = stringList("avoid"),
                clash = item.getString("clash"),
                shaDirection = item.getString("shaDirection"),
                monthGanZhi = item.getString("monthGanZhi"),
            )
        }.getOrNull()
    }

    fun saveDailyAlmanac(info: DailyAlmanacInfo) {
        val suitable = JSONArray().apply { info.suitable.forEach(::put) }
        val avoid = JSONArray().apply { info.avoid.forEach(::put) }
        val item = JSONObject()
            .put("schema", 2)
            .put("date", info.date.toString())
            .put("dateLabel", info.dateLabel)
            .put("lunarLabel", info.lunarLabel)
            .put("dayGanZhi", info.dayGanZhi)
            .put("monthGanZhi", info.monthGanZhi)
            .put("solarTerm", info.solarTerm)
            .put("joyDirection", info.joyDirection)
            .put("fortuneDirection", info.fortuneDirection)
            .put("wealthDirection", info.wealthDirection)
            .put("yangNobleDirection", info.yangNobleDirection)
            .put("yinNobleDirection", info.yinNobleDirection)
            .put("suitable", suitable)
            .put("avoid", avoid)
            .put("clash", info.clash)
            .put("shaDirection", info.shaDirection)
        prefs.edit().putString("daily_almanac", item.toString()).apply()
    }

    var cloudSpeechEnabled: Boolean
        get() = prefs.getBoolean("cloud_speech_enabled", false)
        set(value) = prefs.edit().putBoolean("cloud_speech_enabled", value).apply()

    var cloudSpeechApiKey: String
        get() = prefs.getString("cloud_speech_api_key", "") ?: ""
        set(value) = prefs.edit().putString("cloud_speech_api_key", value).apply()

    internal val cloudSpeechProviderId: String
        get() = prefs.getString("cloud_speech_provider_id", SpeechProviderCatalog.CUSTOM) ?: SpeechProviderCatalog.CUSTOM

    internal val validatedSpeechFingerprint: String
        get() = prefs.getString("validated_speech_fingerprint", "").orEmpty()
    internal val attemptedSpeechFingerprint: String
        get() = prefs.getString("attempted_speech_fingerprint", "").orEmpty()
    internal val lastSpeechTestOutcome: String
        get() = prefs.getString("last_speech_test_outcome", "").orEmpty()

    internal fun saveCloudSpeechConfiguration(configuration: CloudSpeechConfig, changed: Boolean) {
        prefs.edit().apply {
            putString("cloud_speech_provider_id", configuration.providerId)
            putString("cloud_speech_api_key", configuration.apiKey)
            putString("cloud_speech_model", configuration.model)
            putString("cloud_speech_endpoint", configuration.endpoint)
            if (changed) {
                remove("validated_speech_fingerprint")
                remove("attempted_speech_fingerprint")
                remove("last_speech_test_outcome")
            }
        }.apply()
    }

    internal fun saveSpeechTestOutcome(fingerprint: String, outcome: AiConnectionStatus) {
        prefs.edit().apply {
            putString("attempted_speech_fingerprint", fingerprint)
            putString("last_speech_test_outcome", outcome.name)
            if (outcome == AiConnectionStatus.Connected) putString("validated_speech_fingerprint", fingerprint)
            else if (outcome != AiConnectionStatus.NoNetwork) remove("validated_speech_fingerprint")
        }.apply()
    }

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

internal fun FortuneReading.toJson(): JSONObject {
    val coinLinesJson = JSONArray()
    coinLines.forEach { line -> coinLinesJson.put(line.toJson()) }
    val tarotDrawsJson = JSONArray()
    tarotDraws.forEach { draw -> tarotDrawsJson.put(draw.toJson()) }
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
        .put("tarotDraws", tarotDrawsJson)
        .put("primaryHexagram", primaryHexagram)
        .put("transformedHexagram", transformedHexagram)
        .put("classicMethod", classicMethod)
        .put("classicRule", classicRule)
        .put("classicSource", classicSource)
        .put("classicReferences", classicReferencesJson)
        .put("primaryClassic", primaryClassic?.toJson() ?: JSONObject.NULL)
        .put("transformedClassic", transformedClassic?.toJson() ?: JSONObject.NULL)
}

internal fun JSONObject.toReading(): FortuneReading = FortuneReading(
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
    tarotDraws = readTarotDraws(),
)

private fun TarotDraw.toJson(): JSONObject = JSONObject()
    .put("positionIndex", positionIndex)
    .put("cardId", cardId)
    .put("reversed", reversed)

private fun JSONObject.readTarotDraws(): List<TarotDraw> {
    val draws = optJSONArray("tarotDraws") ?: return emptyList()
    return (0 until draws.length()).mapNotNull { index ->
        val item = draws.optJSONObject(index) ?: return@mapNotNull null
        val position = item.optInt("positionIndex")
        val cardId = item.optString("cardId")
        if (position !in 1..10 || runCatching { TarotDeck.card(cardId) }.isFailure) {
            null
        } else {
            TarotDraw(
                positionIndex = position,
                cardId = cardId,
                reversed = item.optBoolean("reversed"),
            )
        }
    }
        .distinctBy(TarotDraw::positionIndex)
        .sortedBy(TarotDraw::positionIndex)
        .take(10)
}

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

internal fun shouldDisableDeepSeekThinking(endpoint: String, model: String): Boolean {
    return aiEndpointHost(endpoint) == "api.deepseek.com" && model.isNotBlank()
}

internal fun isAiConfigurationComplete(endpoint: String, apiKey: String, model: String): Boolean {
    val normalizedEndpoint = endpoint.trim().lowercase()
    return apiKey.isNotBlank() && model.isNotBlank() &&
        (normalizedEndpoint.startsWith("https://") || normalizedEndpoint.startsWith("http://"))
}

internal fun aiConfigurationFingerprint(endpoint: String, model: String, apiKey: String): String {
    if (!isAiConfigurationComplete(endpoint, apiKey, model)) return ""
    val normalized = "${endpoint.trim()}\n${model.trim()}\n${apiKey.trim()}"
    return MessageDigest.getInstance("SHA-256")
        .digest(normalized.toByteArray(Charsets.UTF_8))
        .joinToString("") { byte -> "%02x".format(byte.toInt() and 0xFF) }
}

internal fun hasValidatedInternet(context: Context): Boolean {
    val manager = context.getSystemService(ConnectivityManager::class.java) ?: return false
    val network = manager.activeNetwork ?: return false
    val capabilities = manager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

private object AiConnectionClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    suspend fun test(endpoint: String, apiKey: String, model: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val request = AiApiTransport.buildRequest(
                    endpoint, apiKey, model,
                    JSONArray().put(JSONObject().put("role", "user").put("content", "Reply with OK only.")),
                    AiApiTransport.connectionTestTokenLimit(endpoint), 0.0,
                )
                AiApiTransport.execute(client, request, apiKey)
                Unit
            }
        }
}

internal const val DAILY_INSIGHT_MAX_OUTPUT_TOKENS = 700

internal fun buildDailyAiInsightPrompt(
    snapshot: DailyFortuneSnapshot,
    category: DailyInsightCategory,
    nickname: String,
    birthDate: String,
    keywords: String,
): String {
    val info = snapshot.almanac
    val insight = snapshot.insights.first { it.category == category }
    val suitable = DailyInsightEngine.safeSuitable(info)
        .joinToString("、")
        .ifBlank { "无适合转译为现代生活的明确项目" }
    val avoid = DailyInsightEngine.safeAvoid(info)
        .joinToString("、")
        .ifBlank { "无适合转译为现代生活的明确项目" }
    return """
        以下是同一天、同一用户的固定背景。请只使用这些资料，不推测未提供的信息。
        日期：${info.dateLabel}
        农历：${info.lunarLabel}；日干支：${info.dayGanZhi}；节气：${info.solarTerm.ifBlank { "无" }}
        节月干支：${info.monthGanZhi.ifBlank { "未提供" }}；节令比照：${DailyTraditionEngine.seasonalContext(info).explanation}
        后台节奏参考（仅供判断背景，不得向用户复述分值构成、权重或内部校准）：
        节奏刻度：${snapshot.reading.score}；${snapshot.rhythmBasis}。这是应用自定义权重，不是古籍算法或吉凶概率。
        黄历宜：$suitable
        黄历忌：$avoid
        方位：喜神${info.joyDirection}，福神${info.fortuneDirection}，财神${info.wealthDirection}
        用户资料：昵称${nickname.trim().take(30).ifBlank { "未设置" }}；生日${birthDate.trim().take(10).ifBlank { "未设置" }}；关注词${keywords.trim().take(120).ifBlank { "未设置" }}

        本次只解读“${category.label}”。离线结论：${insight.headline}。离线依据：${insight.summary}
        请结合《周易》的变化、时位与中正观念，以及《道德经》知止、守柔、不过度用力的思想，给出220至320个汉字的当日参考。分为“判断”“依据”“建议”三部分，每部分只保留最关键的1至3点；语言中性、具体、简短，可有偏向但不得夸张，不宣称确定未来，不伪造经文，不给医疗、法律或投资结论。不要重复用户资料和完整黄历，不写总结性套话。
    """.trimIndent()
}

private object DailyAiInsightClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(75, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .build()
    private val SYSTEM_PROMPT = OraclePrompts.CORE + "\n\n" + OraclePrompts.DAILY + "\n\n输出简体中文。"

    suspend fun explain(
        endpoint: String,
        apiKey: String,
        model: String,
        snapshot: DailyFortuneSnapshot,
        category: DailyInsightCategory,
        nickname: String,
        birthDate: String,
        keywords: String,
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val resolvedModel = model.trim()
            val messages = JSONArray()
                .put(JSONObject().put("role", "system").put("content", SYSTEM_PROMPT))
                .put(
                    JSONObject()
                        .put("role", "user")
                        .put(
                            "content",
                            buildDailyAiInsightPrompt(
                                snapshot,
                                category,
                                nickname,
                                birthDate,
                                keywords,
                            ),
                        )
                )
            val request = AiApiTransport.buildRequest(
                endpoint, apiKey, resolvedModel, messages, DAILY_INSIGHT_MAX_OUTPUT_TOKENS, 0.35,
            )
            AiApiTransport.execute(client, request, apiKey).content.take(4_000)
        }
    }
}

private object AiChatClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

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
            val resolvedModel = model.ifBlank { "gpt-4o-mini" }
            val request = AiApiTransport.buildRequest(endpoint, apiKey, resolvedModel, requestMessages, 1_000, 0.55)
            AiApiTransport.execute(client, request, apiKey).content
        }
    }

    private val CHAT_SYSTEM_PROMPT = OraclePrompts.CORE + "\n\n" + OraclePrompts.DIALOGUE +
        "\n\n默认使用简体中文；用户明确选择其他语言时遵循用户选择。"
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
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    private data class CompletionResponse(
        val content: String,
        val finishReason: String,
    )

    suspend fun interpret(
        endpoint: String,
        apiKey: String,
        model: String,
        reading: FortuneReading,
        tarotContext: TarotAiContext? = null,
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val resolvedModel = model.ifBlank { "gpt-4o-mini" }
            val messages = JSONArray()
                .put(JSONObject()
                        .put("role", "system")
                        .put(
                            "content",
                            if (reading.kind == FourCardTarot.KIND) FourCardTarotPrompt.system else OraclePrompts.forReading(reading.kind),
                        )
                    )
                .put(JSONObject()
                    .put("role", "user")
                    .put("content", buildAiPrompt(reading, tarotContext))
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
        val request = AiApiTransport.buildRequest(endpoint, apiKey, model, messages, maxTokens, 0.45)
        val response = AiApiTransport.execute(client, request, apiKey)
        return CompletionResponse(response.content, response.finishReason)
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

    internal fun buildAiPrompt(
        reading: FortuneReading,
        tarotContext: TarotAiContext? = null,
    ): String = OraclePromptBuilder.build(reading, tarotContext)
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

    fun tarot(question: String, draws: List<TarotDraw>): FortuneReading {
        val ordered = draws.sortedBy(TarotDraw::positionIndex)
        require(ordered.map(TarotDraw::positionIndex) == (1..10).toList())
        require(ordered.map(TarotDraw::cardId).distinct().size == 10)
        val local = TarotDeck.interpret(ordered, question)
        return FortuneReading(
            id = System.currentTimeMillis(),
            kind = "韦特塔罗",
            title = local.title,
            question = question.trim(),
            body = local.body,
            advice = local.advice,
            score = 0,
            timeLabel = LocalDateTime.now().format(formatter),
            classicMethod = "韦特十字牌阵：10张牌，不另设预选指示牌；牌位依《塔罗图解钥匙》第三部分。",
            classicSource = "牌面：Pamela Colman Smith 绘制、Rider 于1909年出版的Rider-Waite-Smith牌组；释义依据A. E. Waite公版原始说明作现代中文概括。",
            tarotDraws = ordered,
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
