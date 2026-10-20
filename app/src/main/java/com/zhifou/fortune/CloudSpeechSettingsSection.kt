package com.zhifou.fortune

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
internal fun CloudSpeechSettingsSection(vm: FortuneViewModel, onUnsavedChangesChanged: (Boolean) -> Unit = {}) {
    val palette = LocalFortunePalette.current
    val haptics = LocalAppHaptics.current
    val uriHandler = LocalUriHandler.current
    val saved = CloudSpeechConfig(vm.cloudSpeechEndpoint, vm.cloudSpeechApiKey, vm.cloudSpeechModel, vm.cloudSpeechProviderId)
    // Unsaved credentials are never placed in Activity saved state.
    val initialDraft = remember(vm) {
        if (saved.apiKey.isBlank() && saved.model == "whisper-1" &&
            saved.endpoint == "https://api.openai.com/v1/audio/transcriptions" && saved.providerId == SpeechProviderCatalog.CUSTOM
        ) saved.selectProvider("speech-siliconflow") else saved
    }
    var draft by remember(vm) { mutableStateOf(initialDraft) }
    var cleanDraft by remember(vm) { mutableStateOf(initialDraft.normalized()) }
    var customModel by remember { mutableStateOf(false) }
    var keyVisible by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }
    val preset = SpeechProviderCatalog.find(draft.providerId)
    val selectedModel = preset?.models?.firstOrNull { it.id == draft.model }
    val dirty = draft.normalized() != saved
    SideEffect {
        if (!dirty) cleanDraft = draft.normalized()
        onUnsavedChangesChanged(dirty && draft.normalized() != cleanDraft)
    }
    val checking = vm.speechConnectionStatus == AiConnectionStatus.Checking
    val connected = vm.speechConnectionStatus == AiConnectionStatus.Connected && !dirty

    fun save(test: Boolean) {
        formError = draft.validationError()
        haptics.scene(if (formError != null) HapticCue.Reject else if (test) HapticCue.Selection else HapticCue.Confirm)
        if (formError == null) {
            vm.applyCloudSpeechConfiguration(draft.normalized())
            keyVisible = false
            if (test) vm.testCloudSpeechConnection()
        }
    }

    GeminiGlassPanel(
        tone = GeminiGlassTone.Neutral,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.widthIn(max = 760.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("AI 语音转写", color = palette.textMain, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { save(test = true) },
                    enabled = !checking && draft.apiKey.isNotBlank() && draft.model.isNotBlank(),
                    modifier = Modifier.semantics { contentDescription = "语音转写连接测试" },
                ) {
                    FortuneIcon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("连接测试", style = MaterialTheme.typography.labelMedium)
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("使用云端转写", color = palette.textMain, style = MaterialTheme.typography.bodyLarge)
                    Text("关闭时使用本机离线识别", color = palette.textSub, style = MaterialTheme.typography.bodySmall)
                }
                Switch(
                    checked = vm.cloudSpeechEnabled, onCheckedChange = { haptics.toggle(it); vm.updateCloudSpeechEnabled(it) },
                    modifier = Modifier.semantics { contentDescription = "使用云端语音转写" },
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                FortuneIcon(if (connected) Icons.Default.CheckCircle else Icons.Default.Info, contentDescription = null,
                    tint = if (connected) palette.mint else palette.textSub, modifier = Modifier.size(16.dp))
                Text(if (dirty && vm.cloudSpeechEnabled) "语音配置尚未保存" else vm.speechConnectionMessage,
                    color = if (connected) palette.mint else palette.textSub, style = MaterialTheme.typography.bodySmall)
            }
            AnimatedVisibility(vm.cloudSpeechEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AiConfigurationDropdown(
                        label = "转写平台", value = preset?.name ?: "自定义 · OpenAI 兼容接口",
                        choices = SpeechProviderCatalog.presets.map { it.id to it.name } +
                            (SpeechProviderCatalog.CUSTOM to "自定义 · OpenAI 兼容接口"),
                        onSelect = {
                            draft = draft.selectProvider(it)
                            customModel = false; keyVisible = false; formError = null
                        },
                    )
                    if (preset != null) {
                        val manual = customModel || selectedModel == null
                        AiConfigurationDropdown(
                            label = "转写模型", value = if (manual) "其他模型 · 输入模型 ID" else selectedModel.label,
                            choices = preset.models.map { it.id to it.label } + ("" to "其他模型 · 输入模型 ID"),
                            onSelect = { customModel = it.isBlank(); draft = draft.copy(model = it); formError = null },
                        )
                        if (manual) {
                            GeminiGlassTextField(
                                value = draft.model, onValueChange = { draft = draft.copy(model = it); formError = null },
                                label = { Text("语音模型 ID") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Text(selectedModel?.billingNote ?: "自定义模型的价格、权限和协议以平台为准，不承诺免费。",
                            color = palette.textSub, style = MaterialTheme.typography.bodySmall)
                        Text(preset.endpoint, color = palette.textSub, style = MaterialTheme.typography.bodySmall)
                    } else {
                        GeminiGlassTextField(
                            value = draft.endpoint, onValueChange = { draft = draft.editEndpoint(it); formError = null },
                            label = { Text("音频转写 endpoint") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                        )
                        GeminiGlassTextField(
                            value = draft.model, onValueChange = { draft = draft.copy(model = it); formError = null },
                            label = { Text("语音模型 ID") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    GeminiGlassTextField(
                        value = draft.apiKey, onValueChange = { draft = draft.copy(apiKey = it); formError = null },
                        label = { Text("语音 API Key") }, placeholder = { Text("填写所选平台对应地区的密钥") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false),
                        trailingIcon = {
                            IconButton(onClick = { keyVisible = !keyVisible }) {
                                FortuneIcon(if (keyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (keyVisible) "隐藏语音密钥" else "显示语音密钥")
                            }
                        },
                    )
                    formError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                        if (dirty) TextButton(onClick = { draft = saved; customModel = false; keyVisible = false; formError = null }) { Text("取消") }
                        Button(onClick = { save(test = false) }, enabled = dirty) { Text("保存语音配置") }
                    }
                    Text("开启后，松手确认的录音会发送到所选平台。密钥仅保存在本机，切换平台地区或接口域名需重新填写。",
                        color = palette.textSub, style = MaterialTheme.typography.bodySmall)
                    Text("连接测试只发送 1 秒静音音频，不启用麦克风；可能消耗额度，部分平台有最低计费时长。保存和启动应用均不会自动测试。",
                        color = palette.textSub, style = MaterialTheme.typography.bodySmall)
                    if (preset != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { uriHandler.openUri(preset.documentation) }) { Text("接口文档") }
                            TextButton(onClick = { uriHandler.openUri(preset.pricing) }) { Text("价格与额度") }
                        }
                        Text("价格核对：2026-10-18，最新政策以官方为准。", color = palette.textSub, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
