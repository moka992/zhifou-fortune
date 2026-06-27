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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private val Ink = Color(0xFF111318)
private val Panel = Color(0xFF1B1F27)
private val PanelAlt = Color(0xFF242832)
private val Line = Color(0xFF343A46)
private val Gold = Color(0xFFE2C16B)
private val Mint = Color(0xFF7ED7C1)
private val Rose = Color(0xFFE48A9A)
private val TextMain = Color(0xFFF3F0E8)
private val TextSub = Color(0xFFB7B2A6)

class MainActivity : ComponentActivity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(17, 19, 24)
        window.navigationBarColor = android.graphics.Color.rgb(27, 31, 39)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility = 0
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Ink,
                    surface = Panel,
                    primary = Gold,
                    secondary = Mint,
                    tertiary = Rose,
                    onBackground = TextMain,
                    onSurface = TextMain,
                    onPrimary = Ink,
                ),
            ) {
                Surface(Modifier.fillMaxSize(), color = Ink) {
                    FortuneApp()
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
    val context = LocalContext.current
    val offlineRecognizer = remember(context) { OfflineSpeechRecognizer(context.applicationContext) }
    var tab by rememberTabState()
    var showDiceTool by remember { mutableStateOf(false) }

    DisposableEffect(offlineRecognizer) {
        onDispose {
            Thread({ offlineRecognizer.close() }, "zhifou-asr-cleanup").start()
        }
    }

    Scaffold(
        containerColor = Ink,
        topBar = { AppTopBar() },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().height(144.dp)) {
                NavigationBar(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                    containerColor = Panel,
                    tonalElevation = 0.dp,
                ) {
                    Tab.entries.forEach { item ->
                        NavigationBarItem(
                            selected = tab == item,
                            onClick = {
                                tab = item
                                if (item != Tab.Tools) showDiceTool = false
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
                                selectedIconColor = Gold,
                                selectedTextColor = if (item == Tab.Oracle) Gold else TextMain,
                                indicatorColor = if (item == Tab.Oracle) Color.Transparent else Gold.copy(alpha = 0.14f),
                                unselectedIconColor = TextSub,
                                unselectedTextColor = TextSub,
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
                        color = Gold,
                        shape = CircleShape,
                        border = if (tab == Tab.Oracle) BorderStroke(2.dp, Mint) else null,
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .size(64.dp)
                            .graphicsLayer { translationY = -2.dp.toPx() },
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Tab.Oracle.icon,
                                contentDescription = Tab.Oracle.label,
                                tint = Ink,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                    Text(
                        Tab.Oracle.label,
                        color = if (tab == Tab.Oracle) Gold else TextSub,
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
                .background(Ink)
                .padding(padding),
        ) {
            when (tab) {
                Tab.Home -> HomeScreen(vm, onOpenOracle = { tab = Tab.Oracle })
                Tab.Oracle -> OracleScreen(vm, offlineRecognizer)
                Tab.Tools -> if (showDiceTool) DiceScreen(onBack = { showDiceTool = false }) else ToolsScreen(onOpenDice = { showDiceTool = true })
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
    CenterAlignedTopAppBar(
        title = {
            Text(
                "知否运势",
                color = TextMain,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        },
        colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Ink,
            titleContentColor = TextMain,
        ),
    )
}

@Composable
private fun HomeScreen(vm: FortuneViewModel, onOpenOracle: () -> Unit) {
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
            Button(
                onClick = { vm.refreshToday() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Gold),
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("今日运势")
            }
            FilledTonalButton(onClick = onOpenOracle, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("去占卜")
            }
        }
        InsightStrip()
    }
}

@Composable
private fun OracleScreen(vm: FortuneViewModel, offlineRecognizer: OfflineSpeechRecognizer) {
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

    LaunchedEffect(offlineRecognizer) {
        val result = offlineRecognizer.prepare()
        modelReady = result.isSuccess
        if (result.isFailure) showVoiceMessage("离线语音模型加载失败")
    }

    fun startRecording() {
        if (isRecording) return
        if (!modelReady) {
            showVoiceMessage("离线语音模型正在加载，请稍后再试")
            return
        }
        questionBeforeRecording = vm.question
        keyboardController?.hide()
        isCancelling = false
        voiceLevel = 0.06f
        isRecording = offlineRecognizer.start(
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
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val reading = vm.latestReading
            if (reading == null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        "今天想问些什么？",
                        color = TextMain,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    suggestedQuestions.forEach { question ->
                        Surface(
                            onClick = { vm.question = question },
                            color = PanelAlt,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Line),
                        ) {
                            Text(
                                question,
                                color = TextMain,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                ) {
                    ReadingCard(reading)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { vm.castCoins() }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Casino, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("三币起卦")
            }
            OutlinedButton(onClick = { vm.drawAnswerBook() }, modifier = Modifier.weight(1f)) {
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
                OutlinedTextField(
                    value = vm.question,
                    onValueChange = {
                        vm.question = it
                        keyboardEdited = it.isNotBlank()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(questionFocusRequester)
                        .onFocusChanged { state ->
                            if (!state.isFocused) textEditing = false
                        }
                        .then(
                            if (textEditing || keyboardEdited) {
                                Modifier
                            } else {
                                Modifier.pointerInput(modelReady, voiceHoldThresholdMs) {
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
                Text(
                    "轻触输入文字，按住输入语音",
                    color = TextSub,
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

@Composable
private fun VoiceCapturePanel(
    level: Float,
    cancelling: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (cancelling) Rose else Gold
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
private fun ToolsScreen(onOpenDice: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("小工具", color = TextMain, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Surface(
            onClick = onOpenDice,
            color = Panel,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Line),
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
                    Text("摇骰子", color = TextMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("选择骰子个数和面数，合上骰盅后摇动手机或点击按钮。", color = TextSub, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun DiceScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val rotations = remember { List(6) { Animatable(0f) } }
    val bounces = remember { List(6) { Animatable(0f) } }
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
            playShakeSound()
            repeat(8) { step ->
                faces = List(roll.quantity) { Random.nextInt(1, roll.sides + 1) }
                roll.rolls.indices.forEach { index ->
                    bounces[index].snapTo(if ((step + index) % 2 == 0) 0.55f else 0.15f)
                }
                rotations.take(roll.quantity).forEachIndexed { index, animatable ->
                    launch {
                        animatable.animateTo(
                            targetValue = animatable.value + 18f + step * 2f + index * 6f,
                            animationSpec = tween(durationMillis = 70, easing = FastOutSlowInEasing),
                        )
                    }
                }
                delay(90)
            }
            delay(80)
            faces = roll.rolls
            resultText = roll.displayText
            roll.rolls.indices.forEach { index ->
                launch {
                    bounces[index].animateTo(0f, animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing))
                }
                launch {
                    rotations[index].animateTo(rotations[index].value + 8f + index * 3f, animationSpec = tween(durationMillis = 120))
                }
            }
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
                    startCupRoll()
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
            colors = CardDefaults.cardColors(containerColor = Panel),
            border = BorderStroke(1.dp, Line),
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
                    Text("摇骰子", color = TextMain, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
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
                        rotations = rotations.map { it.value },
                        bounces = bounces.map { it.value },
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
                    color = if (rolling || cupClosed) TextSub else Gold,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Button(
                    onClick = { startCupRoll() },
                    enabled = !rolling,
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Casino, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (rolling) "摇动中" else "合盅摇骰")
                }
                Text(
                    "可下滑合上骰盅，点击按钮或摇动手机开始；声音结束后上滑打开。",
                    color = TextSub,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        if (history.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = PanelAlt),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("最近结果", color = TextMain, fontWeight = FontWeight.SemiBold)
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
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = TextSub, style = MaterialTheme.typography.labelMedium)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(value, color = TextMain, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                color = Color(0xFF2A2420),
                topLeft = Offset(size.width * 0.08f, size.height * 0.74f),
                size = Size(size.width * 0.84f, trayHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx(), 24.dp.toPx()),
            )
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF776041), Color(0xFF362A20)),
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
                color = Gold.copy(alpha = 0.86f),
                topLeft = Offset(size.width * 0.38f, size.height * 0.02f),
                size = Size(size.width * 0.24f, size.height * 0.12f),
            )
            drawRoundRect(
                color = Color(0xAA111318),
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
                color = TextMain,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                if (rolling) "沙沙声结束后打开" else "下滑可再次合上",
                color = TextSub,
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
    rotations: List<Float>,
    bounces: List<Float>,
    rolling: Boolean,
) {
    val rows = faces.chunked(3)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (faces.size <= 3) 12.dp else 8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        rows.forEachIndexed { rowIndex, row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEachIndexed { columnIndex, face ->
                    val index = rowIndex * 3 + columnIndex
                    DiceVisual(
                        face = face,
                        sides = sides,
                        rotation = rotations.getOrElse(index) { 0f },
                        bounce = bounces.getOrElse(index) { 0f },
                        rolling = rolling,
                        modifier = Modifier.size(
                            when (faces.size) {
                                1 -> 208.dp
                                2, 3 -> 132.dp
                                else -> 108.dp
                            },
                        ),
                    )
                }
            }
        }
    }
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
    val tilt = if (rolling) sin(rotation / 18f) * 7f else sin(rotation / 42f) * 2.5f
    val shapeColor = diceShapeColor(sides)
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

private fun diceShapeColor(sides: Int): Color {
    return when (sides) {
        4 -> Color(0xFF23A84A)
        6 -> Color(0xFF2AB7C9)
        8 -> Color(0xFF8738D9)
        10 -> Color(0xFFE23A8C)
        12 -> Color(0xFFE33327)
        20 -> Color(0xFFFF7400)
        else -> Gold
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
            colors = CardDefaults.cardColors(containerColor = Panel),
            border = BorderStroke(1.dp, Line),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "记录 ${selectedDate.monthValue}月${selectedDate.dayOfMonth}日",
                    color = TextMain,
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
                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                ) {
                    Icon(Icons.AutoMirrored.Filled.EventNote, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("保存到日程")
                }
            }
        }

        Text(
            "${selectedDate.monthValue}月${selectedDate.dayOfMonth}日日程",
            color = TextMain,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (selectedItems.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = PanelAlt),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("当日还没有日程。", color = TextSub, modifier = Modifier.padding(16.dp))
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
    val today = remember { LocalDate.now() }
    val animationScope = rememberCoroutineScope()
    var visibleMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var picker by remember { mutableStateOf<CalendarPicker?>(null) }
    var calendarOffset by remember { mutableStateOf(Offset.Zero) }
    var calendarAnimationJob by remember { mutableStateOf<Job?>(null) }
    val days = remember(visibleMonth) { calendarCells(visibleMonth).map(::calendarDateInfo) }
    val scheduledDates = remember(scheduleItems) { scheduleItems.mapTo(hashSetOf()) { it.date } }
    val selectedInfo = remember(selectedDate) { calendarDateInfo(selectedDate) }
    val showToday = visibleMonth != YearMonth.from(today) || selectedDate != today

    fun showMonth(month: YearMonth) {
        val safeYear = month.year.coerceIn(CALENDAR_MIN_YEAR, CALENDAR_MAX_YEAR)
        val safeMonth = YearMonth.of(safeYear, month.monthValue)
        visibleMonth = safeMonth
        onSelectedDateChange(safeMonth.atDay(selectedDate.dayOfMonth.coerceAtMost(safeMonth.lengthOfMonth())))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Panel),
        border = BorderStroke(1.dp, Line),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("今天", color = TextSub, style = MaterialTheme.typography.labelLarge)
                Text(
                    formatFullCalendarDate(today),
                    color = TextMain,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { picker = CalendarPicker.Year }) {
                        Text(
                            "${visibleMonth.year}年",
                            color = Gold,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(onClick = { picker = CalendarPicker.Month }) {
                        Text(
                            "${visibleMonth.monthValue}月",
                            color = Gold,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                if (showToday) {
                    TextButton(onClick = {
                        visibleMonth = YearMonth.from(today)
                        onSelectedDateChange(today)
                    }, modifier = Modifier.align(Alignment.CenterEnd)) { Text("今日") }
                }
            }
            Column(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = calendarOffset.x
                        translationY = calendarOffset.y
                    }
                    .pointerInput(visibleMonth, selectedDate) {
                    var dragX = 0f
                    var dragY = 0f
                    var dragAxis: CalendarDragAxis? = null

                    fun settleCalendar(target: Offset = Offset.Zero, durationMillis: Int = 140) {
                        val start = calendarOffset
                        calendarAnimationJob?.cancel()
                        calendarAnimationJob = animationScope.launch {
                            animate(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
                            ) { progress, _ ->
                                calendarOffset = Offset(
                                    x = start.x + (target.x - start.x) * progress,
                                    y = start.y + (target.y - start.y) * progress,
                                )
                            }
                        }
                    }

                    detectDragGestures(
                        onDragStart = {
                            dragX = 0f
                            dragY = 0f
                            dragAxis = null
                            calendarAnimationJob?.cancel()
                            calendarOffset = Offset.Zero
                        },
                        onDrag = { change, amount ->
                            change.consume()
                            dragX += amount.x
                            dragY += amount.y
                            if (dragAxis == null && kotlin.math.abs(dragX) + kotlin.math.abs(dragY) > 16.dp.toPx()) {
                                dragAxis = if (kotlin.math.abs(dragX) > kotlin.math.abs(dragY)) {
                                    CalendarDragAxis.Horizontal
                                } else {
                                    CalendarDragAxis.Vertical
                                }
                            }
                            calendarOffset = when (dragAxis) {
                                CalendarDragAxis.Horizontal -> Offset(dragX.coerceIn(-180.dp.toPx(), 180.dp.toPx()), 0f)
                                CalendarDragAxis.Vertical -> Offset(0f, dragY.coerceIn(-120.dp.toPx(), 120.dp.toPx()))
                                null -> Offset.Zero
                            }
                        },
                        onDragEnd = {
                            val horizontalThreshold = 72.dp.toPx()
                            val verticalThreshold = 88.dp.toPx()
                            if (dragAxis == CalendarDragAxis.Horizontal && kotlin.math.abs(dragX) > horizontalThreshold) {
                                showMonth(if (dragX < 0f) visibleMonth.plusMonths(1) else visibleMonth.minusMonths(1))
                                calendarOffset = Offset(if (dragX < 0f) 56.dp.toPx() else -56.dp.toPx(), 0f)
                                settleCalendar(durationMillis = 160)
                            } else if (dragAxis == CalendarDragAxis.Vertical && kotlin.math.abs(dragY) > verticalThreshold) {
                                showMonth(if (dragY < 0f) visibleMonth.plusYears(1) else visibleMonth.minusYears(1))
                                calendarOffset = Offset(0f, if (dragY < 0f) 36.dp.toPx() else -36.dp.toPx())
                                settleCalendar(durationMillis = 150)
                            } else {
                                settleCalendar(durationMillis = 120)
                            }
                        },
                        onDragCancel = { settleCalendar(durationMillis = 120) },
                    )
                },
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("一", "二", "三", "四", "五", "六", "日").forEach { label ->
                        Text(
                            label,
                            color = TextSub,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                days.chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        week.forEach { info ->
                            CalendarDayCell(
                                info = info,
                                visibleMonth = visibleMonth,
                                selectedDate = selectedDate,
                                today = today,
                                hasSchedule = info.date.toString() in scheduledDates,
                                onClick = {
                                    visibleMonth = YearMonth.from(info.date)
                                    onSelectedDateChange(info.date)
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
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
    val date = info.date
    val inMonth = date.monthValue == visibleMonth.monthValue
    val isToday = date == today
    val isSelected = date == selectedDate
    Surface(
        onClick = onClick,
        color = when {
            isSelected -> Gold.copy(alpha = 0.18f)
            isToday -> Mint.copy(alpha = 0.14f)
            else -> Color.Transparent
        },
        shape = RoundedCornerShape(8.dp),
        border = when {
            isSelected -> BorderStroke(1.dp, Gold)
            isToday -> BorderStroke(1.dp, Mint)
            else -> null
        },
        modifier = modifier.height(58.dp),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                date.dayOfMonth.toString(),
                color = when {
                    !inMonth -> TextSub.copy(alpha = 0.42f)
                    isSelected -> Gold
                    else -> TextMain
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                info.cellLabel,
                color = when {
                    !inMonth -> TextSub.copy(alpha = 0.36f)
                    info.solarTerm.isNotBlank() || info.traditionalFestivals.isNotEmpty() -> Gold
                    else -> TextSub
                },
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (hasSchedule) {
                Box(Modifier.size(3.dp).background(Mint, CircleShape))
            }
        }
    }
}

@Composable
private fun DateDetail(info: CalendarDateInfo, scheduleItems: List<ScheduleItem>) {
    Surface(color = PanelAlt, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "${info.date.monthValue}月${info.date.dayOfMonth}日 周${info.weekday}",
                color = TextMain,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text("公历 ${info.date.year}年 · 当年第${info.date.dayOfYear}天", color = TextSub)
            Text(
                "农历 ${info.ganZhiYear}年 · 生肖${info.zodiac} · ${info.lunarMonth}${info.lunarDay}",
                color = TextSub,
            )
            if (info.solarTerm.isNotBlank()) {
                CalendarDetailRow(label = "节气", value = info.solarTerm, accent = Gold)
            }
            if (info.traditionalFestivals.isNotEmpty()) {
                CalendarDetailRow(label = "传统节日", value = info.traditionalFestivals.joinToString("、"), accent = Rose)
            }
            CalendarDetailRow(
                label = "日程",
                value = if (scheduleItems.isEmpty()) "暂无事项" else scheduleItems.joinToString(" · ") { it.title },
                accent = Mint,
            )
        }
    }
}

@Composable
private fun CalendarDetailRow(label: String, value: String, accent: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Text(label, color = accent, style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(68.dp))
        Text(value, color = TextMain, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
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
                        color = if (value == selected) Gold.copy(alpha = 0.18f) else Color.Transparent,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                label(value),
                                color = if (value == selected) Gold else TextMain,
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

private data class CalendarDateInfo(
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
    Card(
        colors = CardDefaults.cardColors(containerColor = if (item.done) PanelAlt else Panel),
        border = BorderStroke(1.dp, Line),
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
                    tint = if (item.done) Mint else TextSub,
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.title, color = TextMain, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                if (item.note.isNotBlank()) {
                    Text(item.note, color = TextSub, style = MaterialTheme.typography.bodyMedium)
                }
                Text(item.createdAt, color = TextSub, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除日程", tint = TextSub)
            }
        }
    }
}

@Composable
private fun HistoryScreen(vm: FortuneViewModel) {
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("历史记录", color = TextMain, style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { vm.clearHistory() }, enabled = vm.history.isNotEmpty()) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("清空")
            }
        }
        if (vm.history.isEmpty()) {
            EmptyState("还没有占卜记录")
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MineScreen(vm: FortuneViewModel) {
    var section by remember { mutableStateOf(MineSection.Profile) }
    Column(Modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = section.ordinal,
            containerColor = Ink,
            contentColor = Gold,
        ) {
            MineSection.entries.forEach { item ->
                Tab(
                    selected = section == item,
                    onClick = { section = item },
                    text = { Text(item.label) },
                )
            }
        }
        when (section) {
            MineSection.Profile -> MineProfile(vm, onOpenSettings = { section = MineSection.Settings })
            MineSection.History -> HistoryScreen(vm)
            MineSection.Settings -> SettingsScreen(vm)
        }
    }
}

private enum class MineSection(val label: String) {
    Profile("我的"),
    History("记录"),
    Settings("设置"),
}

@Composable
private fun MineProfile(vm: FortuneViewModel, onOpenSettings: () -> Unit) {
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
            Surface(color = Gold.copy(alpha = 0.16f), shape = CircleShape, modifier = Modifier.size(64.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Gold, modifier = Modifier.size(42.dp))
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    vm.nickname.ifBlank { "知否用户" },
                    color = TextMain,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text("本地账户", color = TextSub, style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = onOpenSettings) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("设置")
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileMetric("占卜记录", vm.history.size.toString(), Modifier.weight(1f))
            ProfileMetric("日程事项", vm.scheduleItems.size.toString(), Modifier.weight(1f))
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            border = BorderStroke(1.dp, Line),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("个人资料", color = TextMain, fontWeight = FontWeight.SemiBold)
                Text("昵称", color = TextSub, style = MaterialTheme.typography.labelMedium)
                Text(vm.nickname.ifBlank { "未设置" }, color = TextMain)
                Text("生日或长期关键词", color = TextSub, style = MaterialTheme.typography.labelMedium)
                Text(vm.birthHint.ifBlank { "未设置" }, color = TextMain)
            }
        }
    }
}

@Composable
private fun ProfileMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(color = PanelAlt, shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(value, color = Gold, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(label, color = TextSub, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SettingsScreen(vm: FortuneViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
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
            colors = CardDefaults.cardColors(containerColor = Panel),
            border = BorderStroke(1.dp, Line),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("AI 解读", color = TextMain, fontWeight = FontWeight.SemiBold)
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
                    "AI Key、模型和接口地址只写入本机设置，不会进入 Git 仓库。未配置 Key 时应用仍可使用本地占卜。",
                    color = TextSub,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            border = BorderStroke(1.dp, Line),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("应用定位", color = TextMain, fontWeight = FontWeight.SemiBold)
                Text("知否运势是安装即用的手机端应用，所有基础记录都保存在本机。后续可以继续扩展账号同步、AI 解读、提醒和会员能力。", color = TextSub)
            }
        }
    }
}

@Composable
private fun ReadingCard(reading: FortuneReading, compact: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Panel),
        border = BorderStroke(1.dp, Line),
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
                    Text(reading.title, color = TextMain, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(reading.timeLabel, color = TextSub, style = MaterialTheme.typography.bodySmall)
                }
                Badge(reading.kind)
            }
            if (reading.question.isNotBlank()) {
                Text("问：${reading.question}", color = TextSub, style = MaterialTheme.typography.bodyMedium)
            }
            Text(reading.body, color = TextMain, style = MaterialTheme.typography.bodyLarge)
            Text(reading.advice, color = Gold, style = MaterialTheme.typography.bodyMedium)
            if (reading.aiStatus.isNotBlank()) {
                Text(reading.aiStatus, color = TextSub, style = MaterialTheme.typography.bodyMedium)
            }
            if (reading.aiInterpretation.isNotBlank()) {
                Surface(color = PanelAlt, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Line)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("AI 解读", color = Mint, fontWeight = FontWeight.SemiBold)
                        Text(reading.aiInterpretation, color = TextMain, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String) {
    Surface(color = PanelAlt, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, Line)) {
        Text(text, color = Gold, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

@Composable
private fun FortuneDial(score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Line),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(112.dp)) {
                Canvas(Modifier.fillMaxSize()) {
                    val stroke = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    drawArc(Color(0xFF303640), -220f, 260f, false, style = stroke, size = Size(size.width, size.height))
                    drawArc(
                        brush = Brush.sweepGradient(listOf(Mint, Gold, Rose, Mint)),
                        startAngle = -220f,
                        sweepAngle = 260f * (score / 100f),
                        useCenter = false,
                        style = stroke,
                        size = Size(size.width, size.height),
                    )
                }
                Text("$score", color = TextMain, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("今日能量", color = TextMain, style = MaterialTheme.typography.titleMedium)
                Text("结合日期与个人关键词生成，适合每天打开一次。", color = TextSub)
            }
        }
    }
}

@Composable
private fun InsightStrip() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        listOf("事业", "关系", "财务").forEachIndexed { index, label ->
            Card(
                colors = CardDefaults.cardColors(containerColor = PanelAlt),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    val color = listOf(Mint, Rose, Gold)[index]
                    Box(Modifier.size(8.dp).background(color, CircleShape))
                    Spacer(Modifier.height(8.dp))
                    Text(label, color = TextMain)
                    Text(listOf("稳中推进", "先听后说", "控制变量")[index], color = TextSub, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = TextSub)
    }
}

class FortuneViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = FortuneRepository(application)
    private val oracle = FortuneOracle()

    var question by mutableStateOf("")
    var latestReading by mutableStateOf<FortuneReading?>(null)
        private set
    var history by mutableStateOf(repo.loadHistory())
        private set
    var scheduleItems by mutableStateOf(repo.loadScheduleItems())
        private set
    var nickname by mutableStateOf(repo.nickname)
        private set
    var birthHint by mutableStateOf(repo.birthHint)
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

    fun clearHistory() {
        repo.clearHistory()
        history = emptyList()
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

class FortuneRepository(context: Context) {
    private val prefs = context.getSharedPreferences("zhifou_fortune", Context.MODE_PRIVATE)

    var nickname: String
        get() = prefs.getString("nickname", "") ?: ""
        set(value) = prefs.edit().putString("nickname", value).apply()

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

    fun clearHistory() {
        prefs.edit().remove("history").apply()
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
