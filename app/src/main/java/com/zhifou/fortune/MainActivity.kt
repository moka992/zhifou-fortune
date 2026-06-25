package com.zhifou.fortune

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.security.SecureRandom
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    Oracle("占卜", Icons.Default.Casino),
    Schedule("日程", Icons.AutoMirrored.Filled.EventNote),
    History("记录", Icons.Default.History),
    Settings("设置", Icons.Default.Settings),
}

@Composable
private fun FortuneApp(vm: FortuneViewModel = viewModel()) {
    var tab by rememberTabState()

    Scaffold(
        containerColor = Ink,
        topBar = { AppTopBar() },
        bottomBar = {
            NavigationBar(containerColor = Panel, tonalElevation = 0.dp) {
                Tab.entries.forEach { item ->
                    NavigationBarItem(
                        selected = tab == item,
                        onClick = { tab = item },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
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
                Tab.Oracle -> OracleScreen(vm)
                Tab.Schedule -> ScheduleScreen(vm)
                Tab.History -> HistoryScreen(vm)
                Tab.Settings -> SettingsScreen(vm)
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
private fun OracleScreen(vm: FortuneViewModel) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var recognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    val speechHandler = remember { Handler(Looper.getMainLooper()) }
    val speechIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.SIMPLIFIED_CHINESE.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.SIMPLIFIED_CHINESE.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出你想占卜的问题")
        }
    }
    fun stopVoiceInput() {
        isListening = false
        speechHandler.removeCallbacksAndMessages(null)
        recognizer?.cancel()
        recognizer?.destroy()
        recognizer = null
        vm.voiceMessage = ""
    }
    fun restartListening() {
        if (!isListening) return
        speechHandler.postDelayed({
            if (isListening) {
                try {
                    recognizer?.startListening(speechIntent)
                } catch (_: Throwable) {
                    vm.voiceMessage = "语音识别暂时不可用，请重新点击麦克风"
                    stopVoiceInput()
                }
            }
        }, 250L)
    }
    fun startVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            vm.voiceMessage = "当前设备没有可用的系统语音识别服务"
            return
        }
        if (recognizer == null) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        vm.voiceMessage = "正在聆听"
                    }

                    override fun onBeginningOfSpeech() = Unit
                    override fun onRmsChanged(rmsdB: Float) = Unit
                    override fun onBufferReceived(buffer: ByteArray?) = Unit
                    override fun onEndOfSpeech() = Unit

                    override fun onError(error: Int) {
                        if (!isListening) return
                        vm.voiceMessage = when (error) {
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "缺少麦克风权限"
                            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "语音识别网络异常，继续等待"
                            SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "继续聆听"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "语音识别正在准备"
                            else -> "继续聆听"
                        }
                        if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                            stopVoiceInput()
                        } else {
                            restartListening()
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        val text = results
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                            .orEmpty()
                            .trim()
                        if (text.isNotBlank()) {
                            vm.question = text
                            vm.voiceMessage = "已识别：$text"
                        }
                        restartListening()
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val text = partialResults
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                            .orEmpty()
                            .trim()
                        if (text.isNotBlank()) {
                            vm.question = text
                            vm.voiceMessage = "正在识别：$text"
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) = Unit
                })
            }
        }
        isListening = true
        vm.voiceMessage = "正在聆听"
        recognizer?.startListening(speechIntent)
    }
    fun toggleVoiceInput() {
        if (isListening) {
            stopVoiceInput()
        } else {
            startVoiceInput()
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            startVoiceInput()
        } else {
            vm.voiceMessage = "需要麦克风权限才能语音提问"
        }
    }
    DisposableEffect(Unit) {
        onDispose { stopVoiceInput() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = vm.question,
            onValueChange = { vm.question = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("你想问什么") },
            placeholder = { Text("例如：这周适合推进新计划吗？") },
            minLines = 2,
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (isListening || ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            toggleVoiceInput()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = if (isListening) "正在语音输入" else "语音输入",
                        tint = if (isListening) Gold else TextSub,
                    )
                }
            },
        )
        if (vm.voiceMessage.isNotBlank()) {
            Text(vm.voiceMessage, color = TextSub, style = MaterialTheme.typography.bodyMedium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    stopVoiceInput()
                    vm.castCoins()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = Ink),
            ) {
                Icon(Icons.Default.Casino, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("三币起卦")
            }
            Button(
                onClick = {
                    stopVoiceInput()
                    vm.drawAnswerBook()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Rose, contentColor = Ink),
            ) {
                Icon(Icons.Default.Book, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("答案之书")
            }
        }
        AnimatedVisibility(visible = vm.latestReading != null) {
            vm.latestReading?.let { ReadingCard(it) }
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            border = BorderStroke(1.dp, Line),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("使用方式", color = TextMain, fontWeight = FontWeight.SemiBold)
                Text("点击输入框右侧麦克风可开启或关闭语音输入。点击占卜按钮时会自动停止语音识别。配置 AI Key 后，占卜完成会自动生成更完整的解释。", color = TextSub)
            }
        }
    }
}

@Composable
private fun ScheduleScreen(vm: FortuneViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            border = BorderStroke(1.dp, Line),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("记录日程", color = TextMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
                    label = { Text("备注或日期") },
                    placeholder = { Text("例如：2026-06-28 之前") },
                    minLines = 2,
                )
                Button(
                    onClick = { vm.addScheduleItem() },
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

        Text("待办事项", color = TextMain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (vm.scheduleItems.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = PanelAlt),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("还没有日程。把重要日期、灵感和待办写在这里。", color = TextSub, modifier = Modifier.padding(16.dp))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                vm.scheduleItems.forEach { item ->
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
    var voiceMessage by mutableStateOf("")
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

    fun addScheduleItem() {
        val title = scheduleTitle.trim()
        if (title.isBlank()) return
        val item = ScheduleItem(
            id = System.currentTimeMillis(),
            title = title,
            note = scheduleNote.trim(),
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
    .put("createdAt", createdAt)
    .put("done", done)

private fun JSONObject.toScheduleItem(): ScheduleItem = ScheduleItem(
    id = optLong("id"),
    title = optString("title"),
    note = optString("note"),
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
