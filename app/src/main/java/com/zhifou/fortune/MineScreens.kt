package com.zhifou.fortune

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
internal fun MineScreen(vm: FortuneViewModel) {
    var page by rememberSaveable { mutableStateOf(MinePage.Overview) }
    val stateHolder = rememberSaveableStateHolder()
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    var unsavedConfiguration by remember { mutableStateOf(false) }
    var confirmDiscard by rememberSaveable { mutableStateOf(false) }
    HapticPageFeedback(page, generateSequence(page) { it.parent }.count() - 1)

    fun navigate(destination: MinePage) {
        focus.clearFocus(force = true)
        keyboard?.hide()
        unsavedConfiguration = false
        page = destination
    }

    fun requestBack() {
        val parent = page.parent ?: return
        if (unsavedConfiguration) {
            keyboard?.hide()
            confirmDiscard = true
        } else {
            navigate(parent)
        }
    }

    BackHandler(enabled = page.parent != null) { requestBack() }
    Box(Modifier.fillMaxSize()) {
    stateHolder.SaveableStateProvider(page.name) {
        when (page) {
            MinePage.Overview -> MineOverview(vm, onOpen = ::navigate)
            MinePage.History -> HistoryScreen(vm, onBack = ::requestBack)
            MinePage.Settings -> MineSettingsOverview(vm, onBack = ::requestBack, onOpen = ::navigate)
            else -> Column(Modifier.fillMaxSize()) {
                SubScreenHeader(title = page.title, onBack = ::requestBack) {}
                MineScrollableContent {
                    when (page) {
                        MinePage.Appearance -> MineAppearanceSettings(vm)
                        MinePage.Interaction -> MineInteractionSettings(vm)
                        MinePage.Ai -> AiSettingsSection(vm, onUnsavedChangesChanged = { unsavedConfiguration = it })
                        MinePage.Speech -> CloudSpeechSettingsSection(vm, onUnsavedChangesChanged = { unsavedConfiguration = it })
                        MinePage.Privacy -> MinePrivacySettings()
                        else -> Unit
                    }
                }
            }
        }
    }
    }
    if (confirmDiscard) {
        AlertDialog(
            onDismissRequest = { confirmDiscard = false },
            title = { Text("放弃未保存的配置？") },
            text = { Text("当前修改尚未保存，原有配置不会改变。") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDiscard = false
                    page.parent?.let(::navigate)
                }) { Text("放弃修改") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDiscard = false }) { Text("继续编辑") }
            },
        )
    }
}

@Composable
private fun MineScrollableContent(content: @Composable ColumnScope.() -> Unit) {
    val layout = LocalFortuneLayout.current
    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState())
            .padding(horizontal = layout.horizontalPadding, vertical = layout.verticalPadding),
        verticalArrangement = Arrangement.spacedBy(layout.sectionSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier.widthIn(max = 760.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(layout.sectionSpacing),
            content = content,
        )
    }
}

@Composable
private fun MineOverview(vm: FortuneViewModel, onOpen: (MinePage) -> Unit) {
    val layout = LocalFortuneLayout.current
    val haptics = LocalAppHaptics.current
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    var editing by rememberSaveable { mutableStateOf(false) }
    var nickname by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var keywords by rememberSaveable { mutableStateOf("") }
    val validBirthDate = birthDate.isBlank() || DailyFortuneEngine.parseBirthDate(birthDate, LocalDate.now()) != null

    fun closeEditor() {
        focus.clearFocus(force = true)
        keyboard?.hide()
        editing = false
    }

    BackHandler(enabled = editing) { closeEditor() }
    val profile: @Composable () -> Unit = {
        MinePanel {
            Column(Modifier.padding(layout.cardPadding), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("个人资料", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    if (editing) {
                        TextButton(onClick = ::closeEditor) {
                            Text("取消", style = MaterialTheme.typography.labelMedium)
                        }
                        TextButton(onClick = {
                            vm.updateProfile(nickname, birthDate, keywords)
                            haptics.scene(HapticCue.Confirm)
                            closeEditor()
                        }, enabled = validBirthDate) {
                            Text("完成", fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        IconButton(onClick = {
                            nickname = vm.nickname
                            birthDate = vm.birthDate
                            keywords = vm.fortuneKeywords
                            editing = true
                            haptics.menu(true)
                        }) { FortuneIcon(Icons.Default.Edit, contentDescription = "修改个人资料") }
                    }
                }
                if (editing) {
                    GeminiGlassTextField(
                        value = nickname, onValueChange = { nickname = it },
                        label = { Text("昵称") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    )
                    GeminiGlassTextField(
                        value = birthDate, onValueChange = { birthDate = it.take(10) },
                        label = { Text("生日（公历）") }, placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true, isError = !validBirthDate, modifier = Modifier.fillMaxWidth(),
                        supportingText = if (!validBirthDate) {
                            { Text("请输入有效且不晚于今天的日期") }
                        } else null,
                    )
                    GeminiGlassTextField(
                        value = keywords, onValueChange = { keywords = it.take(120) },
                        label = { Text("运势关注词") }, placeholder = { Text("例如：工作、健康、关系") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(
                            Modifier.size(if (layout.compactWidth) 48.dp else 56.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(vm.nickname.firstOrNull()?.toString() ?: "知",
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.titleLarge)
                        }
                        Text(
                            vm.nickname.ifBlank { "知否用户" }, style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                    MineProfileValue("生日（公历）", vm.birthDate.ifBlank { "未设置" })
                    MineProfileValue("运势关注词", vm.fortuneKeywords.ifBlank { "未设置" })
                }
            }
        }
    }
    val entries: @Composable () -> Unit = {
            MinePanel {
                MineEntryRow(
                    icon = Icons.AutoMirrored.Filled.List, title = "占卜记录", subtitle = "历史占卜与解读",
                    value = "${vm.history.size} 条", onClick = { onOpen(MinePage.History) },
                )
                MineRowDivider()
                MineEntryRow(
                    icon = Icons.Default.Settings, title = "设置", subtitle = "外观、交互与 AI 服务",
                    onClick = { onOpen(MinePage.Settings) },
                )
            }
    }
    MineScrollableContent {
        PageHeading("我的")
        if (layout.shortWide && !editing) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.weight(1f)) { profile() }
                Box(Modifier.weight(1f)) { entries() }
            }
        } else {
            profile()
            if (!editing) entries()
        }
    }
}

@Composable
private fun MineProfileValue(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun MineSettingsOverview(vm: FortuneViewModel, onBack: () -> Unit, onOpen: (MinePage) -> Unit) {
    val aiSummary = when {
        vm.aiApiKey.isBlank() -> "未配置"
        vm.aiConnectionStatus == AiConnectionStatus.Connected -> "已连接 · ${vm.aiModel}"
        vm.aiConnectionStatus == AiConnectionStatus.Checking -> "正在检测连接"
        vm.aiConnectionStatus == AiConnectionStatus.NoNetwork -> "暂无网络"
        vm.aiConnectionStatus == AiConnectionStatus.Failed -> "连接异常"
        else -> "已配置 · ${vm.aiModel}"
    }
    Column(Modifier.fillMaxSize()) {
        SubScreenHeader(title = "设置", onBack = onBack) {}
        MineScrollableContent {
            MinePanel {
                MineEntryRow(Icons.Default.Wallpaper, "外观与背景", "明暗模式与壁纸", onClick = { onOpen(MinePage.Appearance) })
                MineRowDivider()
                MineEntryRow(Icons.Default.Vibration, "交互反馈", "触觉反馈与动态高光", onClick = { onOpen(MinePage.Interaction) })
            }
            MinePanel {
                MineEntryRow(Icons.Default.AutoAwesome, "AI 解读与对话",
                    aiSummary,
                    onClick = { onOpen(MinePage.Ai) })
                MineRowDivider()
                MineEntryRow(Icons.Default.Mic, "语音输入",
                    if (vm.cloudSpeechEnabled) "云端转写" else "本机离线识别",
                    onClick = { onOpen(MinePage.Speech) })
            }
            MinePanel {
                MineEntryRow(Icons.Default.Shield, MinePage.Privacy.title, "公开来源、计算原则、隐私与使用声明", onClick = { onOpen(MinePage.Privacy) })
            }
        }
    }
}

@Composable
private fun MinePanel(content: @Composable ColumnScope.() -> Unit) {
    GeminiGlassPanel(tone = GeminiGlassTone.Neutral, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
private fun MineEntryRow(icon: ImageVector, title: String, subtitle: String, value: String? = null, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    Surface(onClick = onClick, interactionSource = interaction, modifier = Modifier.fortunePress(interaction),
        color = Color.Transparent, shape = MaterialTheme.shapes.large) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FortuneIcon(icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(23.dp), interactionSource = interaction)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            if (value != null) Text(value, style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
            FortuneIcon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null,
                tint = colors.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun MineRowDivider() {
    HorizontalDivider(Modifier.padding(start = 51.dp, end = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MineAppearanceSettings(vm: FortuneViewModel) {
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            vm.updateCustomBackgroundUri(uri.toString())
        }
    }
    MinePanel {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("明暗模式", style = MaterialTheme.typography.titleSmall)
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                val labels = listOf("深色" to ThemeMode.Dark, "浅色" to ThemeMode.Light, "跟随系统" to ThemeMode.System)
                labels.forEachIndexed { index, (label, mode) ->
                    SegmentedButton(selected = vm.themeMode == mode, onClick = hapticAction { vm.updateThemeMode(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index, labels.size)) { Text(label) }
                }
            }
        }
    }
    MinePanel {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("背景", style = MaterialTheme.typography.titleSmall)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BackgroundChoice(AppBackgroundStyle.Solid.label, selected = vm.backgroundStyle == AppBackgroundStyle.Solid,
                    onClick = hapticAction { vm.updateBackgroundStyle(AppBackgroundStyle.Solid) })
                AppBackgroundStyle.entries.filter { it.drawableRes != null }.forEach { style ->
                    BackgroundChoice(style.label, imageRes = style.drawableRes, selected = vm.backgroundStyle == style,
                        onClick = hapticAction { vm.updateBackgroundStyle(style) })
                }
                BackgroundChoice(AppBackgroundStyle.Custom.label, custom = true, customImageUri = vm.customBackgroundUri,
                    selected = vm.backgroundStyle == AppBackgroundStyle.Custom, onClick = {
                        if (vm.customBackgroundUri.isBlank()) picker.launch(arrayOf("image/*"))
                        else vm.updateBackgroundStyle(AppBackgroundStyle.Custom)
                    })
            }
            if (vm.backgroundStyle == AppBackgroundStyle.Custom && vm.customBackgroundUri.isNotBlank()) {
                TextButton(onClick = { picker.launch(arrayOf("image/*")) }) {
                    FortuneIcon(Icons.Default.Wallpaper, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("更换背景图片")
                }
            }
        }
    }
}

@Composable
private fun MineInteractionSettings(vm: FortuneViewModel) {
    MinePanel {
        MineSwitchRow("触觉反馈", checked = vm.hapticsEnabled, onCheckedChange = vm::updateHapticsEnabled)
        MineRowDivider()
        MineSwitchRow("动态玻璃高光", checked = vm.glassMotionEnabled, onCheckedChange = vm::updateGlassMotionEnabled)
    }
}

@Composable
private fun MineSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val haptics = LocalAppHaptics.current
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = { haptics.toggle(it); onCheckedChange(it) }, modifier = Modifier.semantics { contentDescription = title })
    }
}

@Composable
private fun MinePrivacySettings() {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    AppDataDisclosure.sections.forEach { section ->
        Text(section.title, style = MaterialTheme.typography.titleMedium)
        MinePanel {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                section.items.forEach { item ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(item.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                            item.url?.let { url ->
                                IconButton(onClick = {
                                    runCatching { uriHandler.openUri(url) }.onFailure {
                                        Toast.makeText(context, "暂时无法打开链接，请检查浏览器设置", Toast.LENGTH_SHORT).show()
                                    }
                                }) {
                                    FortuneIcon(Icons.AutoMirrored.Filled.OpenInNew, "查看${item.title}的来源",
                                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                        Text(item.description, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
