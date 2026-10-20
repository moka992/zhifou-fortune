package com.zhifou.fortune

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
internal fun AiSettingsSection(vm: FortuneViewModel, onUnsavedChangesChanged: (Boolean) -> Unit = {}) {
    val palette = LocalFortunePalette.current
    val haptics = LocalAppHaptics.current
    val uriHandler = LocalUriHandler.current
    val saved = AiConfiguration(vm.aiProviderId, vm.aiEndpoint, vm.aiModel, vm.aiApiKey)
    // API keys deliberately stay out of rememberSaveable / Activity saved-state bundles.
    val initialDraft = remember(vm) {
        if (saved.apiKey.isBlank() && saved.endpoint == "https://api.openai.com/v1/chat/completions" &&
            saved.model == "gpt-4o-mini" && saved.providerId == AiProviderCatalog.CUSTOM
        ) saved.selectProvider("deepseek") else saved
    }
    var draft by remember(vm) { mutableStateOf(initialDraft) }
    var cleanDraft by remember(vm) { mutableStateOf(initialDraft.normalized()) }
    var keyVisible by remember { mutableStateOf(false) }
    var customModel by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }
    val preset = AiProviderCatalog.find(draft.providerId)
    val dirty = draft.normalized() != saved
    SideEffect {
        if (!dirty) cleanDraft = draft.normalized()
        onUnsavedChangesChanged(dirty && draft.normalized() != cleanDraft)
    }
    val checking = vm.aiConnectionStatus == AiConnectionStatus.Checking
    val connected = vm.aiConnectionStatus == AiConnectionStatus.Connected && !dirty

    fun save(test: Boolean) {
        formError = draft.validationError()
        haptics.scene(if (formError != null) HapticCue.Reject else if (test) HapticCue.Selection else HapticCue.Confirm)
        if (formError == null) {
            vm.applyAiConfiguration(draft.normalized(), test)
            keyVisible = false
        }
    }

    GeminiGlassPanel(
        tone = GeminiGlassTone.Neutral,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.widthIn(max = 760.dp).fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("AI 解读与对话", color = palette.textMain, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { save(test = true) },
                    enabled = !checking && draft.apiKey.isNotBlank() && draft.model.isNotBlank(),
                ) {
                    FortuneIcon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("连接测试", style = MaterialTheme.typography.labelMedium)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                FortuneIcon(
                    if (connected) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (connected) palette.mint else palette.textSub,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    if (dirty) "配置尚未保存" else vm.aiConnectionMessage,
                    color = if (connected) palette.mint else palette.textSub,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            AiConfigurationDropdown(
                label = "API 平台",
                value = preset?.name ?: "自定义 · OpenAI 兼容接口",
                choices = AiProviderCatalog.presets.map { it.id to it.name } +
                    (AiProviderCatalog.CUSTOM to "自定义 · OpenAI 兼容接口"),
                onSelect = { id ->
                    draft = draft.selectProvider(id)
                    customModel = false
                    keyVisible = false
                    formError = null
                },
            )
            if (preset != null) {
                val manual = customModel || draft.model !in preset.models
                AiConfigurationDropdown(
                    label = "模型",
                    value = if (manual) "其他模型 · 输入模型 ID" else draft.model,
                    choices = preset.models.map { it to it } + ("" to "其他模型 · 输入模型 ID"),
                    onSelect = { model ->
                        customModel = model.isEmpty()
                        draft = draft.copy(model = model)
                        formError = null
                    },
                )
                if (manual) {
                    GeminiGlassTextField(
                        value = draft.model,
                        onValueChange = { draft = draft.copy(model = it); formError = null },
                        label = { Text("模型 ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Text(preset.endpoint, color = palette.textSub, style = MaterialTheme.typography.bodySmall)
            } else {
                GeminiGlassTextField(
                    value = draft.endpoint,
                    onValueChange = { draft = draft.editEndpoint(it); formError = null },
                    label = { Text("OpenAI-compatible endpoint") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                GeminiGlassTextField(
                    value = draft.model,
                    onValueChange = { draft = draft.copy(model = it); formError = null },
                    label = { Text("模型 ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            GeminiGlassTextField(
                value = draft.apiKey,
                onValueChange = { draft = draft.copy(apiKey = it); formError = null },
                label = { Text("API Key") },
                placeholder = { Text("填写当前平台对应地区的密钥") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false),
                trailingIcon = {
                    IconButton(onClick = { keyVisible = !keyVisible }) {
                        FortuneIcon(if (keyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (keyVisible) "隐藏 API Key" else "显示 API Key")
                    }
                },
            )
            formError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            if (draft.endpoint.trim().startsWith("http://")) {
                Text("此地址未使用 HTTPS，密钥和内容可能以明文传输。", color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                if (dirty) {
                    TextButton(onClick = { draft = saved; customModel = false; keyVisible = false; formError = null }) {
                        Text("取消")
                    }
                }
                Button(onClick = { save(test = false) }, enabled = dirty) { Text("保存配置") }
            }
            Text(
                "密钥仅保存在本机；切换服务地区或接口域名需重新填写。AI 内容仅发送到所选服务，费用及模型权限以平台账户为准。",
                color = palette.textSub, style = MaterialTheme.typography.bodySmall,
            )
            if (preset != null) {
                TextButton(onClick = { uriHandler.openUri(preset.documentation) }) { Text("平台官方文档") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AiConfigurationDropdown(label: String, value: String, choices: List<Pair<String, String>>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val haptics = LocalAppHaptics.current
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { haptics.menu(it); expanded = it }) {
        GeminiGlassTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            maxLines = 2,
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .semantics { contentDescription = label },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { haptics.menu(false); expanded = false }, modifier = Modifier.heightIn(max = 360.dp)) {
            choices.forEach { (id, title) ->
                DropdownMenuItem(text = { Text(title) }, onClick = { if (title != value) haptics.selection(); expanded = false; onSelect(id) })
            }
        }
    }
}
