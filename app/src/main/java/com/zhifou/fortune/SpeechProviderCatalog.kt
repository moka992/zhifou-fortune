package com.zhifou.fortune

internal enum class SpeechApiFormat { OpenAiMultipart, Multipart, QwenAudioChat, ElevenLabs }

internal data class SpeechModelPreset(val id: String, val label: String, val billingNote: String)

internal data class SpeechProviderPreset(
    val id: String,
    val name: String,
    val endpoint: String,
    val format: SpeechApiFormat,
    val models: List<SpeechModelPreset>,
    val documentation: String,
    val pricing: String,
)

/** Short-recording APIs, checked against official documentation on 2026-10-18. */
internal object SpeechProviderCatalog {
    const val CUSTOM = "custom"
    private const val ACCOUNT_BILLING = "按量或套餐额度计费；是否有试用额度，以平台账户权益为准。"
    val presets = listOf(
        SpeechProviderPreset(
            "speech-siliconflow", "硅基流动 · 免费模型", "https://api.siliconflow.cn/v1/audio/transcriptions",
            SpeechApiFormat.Multipart,
            listOf(SpeechModelPreset("FunAudioLLM/SenseVoiceSmall", "SenseVoice Small", "官方当前列为免费模型，受账户权限和限流约束；免费政策可能调整。")),
            "https://docs.siliconflow.cn/docs/api/audio-transcriptions-post", "https://www.siliconflow.cn/pricing",
        ),
        SpeechProviderPreset(
            "speech-groq", "Groq · 有限额免费层", "https://api.groq.com/openai/v1/audio/transcriptions",
            SpeechApiFormat.OpenAiMultipart,
            listOf(
                SpeechModelPreset("whisper-large-v3-turbo", "Whisper Large V3 Turbo", "Free 层提供有限额免费调用；付费账户可能计费。地区和额度以控制台为准。"),
                SpeechModelPreset("whisper-large-v3", "Whisper Large V3", "Free 层提供有限额免费调用；付费账户可能计费。地区和额度以控制台为准。"),
            ),
            "https://console.groq.com/docs/speech-to-text", "https://console.groq.com/docs/rate-limits",
        ),
        SpeechProviderPreset(
            "speech-qwen-cn", "通义千问 · 中国内地", "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions",
            SpeechApiFormat.QwenAudioChat,
            listOf(SpeechModelPreset("qwen3-asr-flash", "Qwen3 ASR Flash", ACCOUNT_BILLING)),
            "https://help.aliyun.com/en/model-studio/qwen-asr-api-reference", "https://help.aliyun.com/en/model-studio/new-free-quota",
        ),
        SpeechProviderPreset(
            "speech-qwen-intl", "通义千问 · 新加坡", "https://dashscope-intl.aliyuncs.com/compatible-mode/v1/chat/completions",
            SpeechApiFormat.QwenAudioChat,
            listOf(SpeechModelPreset("qwen3-asr-flash", "Qwen3 ASR Flash", ACCOUNT_BILLING)),
            "https://help.aliyun.com/en/model-studio/qwen-asr-api-reference", "https://help.aliyun.com/en/model-studio/new-free-quota",
        ),
        SpeechProviderPreset(
            "speech-openai", "OpenAI", "https://api.openai.com/v1/audio/transcriptions",
            SpeechApiFormat.OpenAiMultipart,
            listOf("gpt-transcribe", "gpt-4o-mini-transcribe", "gpt-4o-transcribe", "whisper-1")
                .map { SpeechModelPreset(it, it, "按量计费，不承诺免费 API 额度；ChatGPT 订阅不等于 API 额度。") },
            "https://developers.openai.com/api/docs/guides/speech-to-text", "https://developers.openai.com/api/docs/pricing",
        ),
        SpeechProviderPreset(
            "speech-mistral", "Mistral", "https://api.mistral.ai/v1/audio/transcriptions",
            SpeechApiFormat.Multipart,
            listOf(SpeechModelPreset("voxtral-mini-latest", "Voxtral Mini Transcribe 2", ACCOUNT_BILLING)),
            "https://docs.mistral.ai/studio/audio/speech_to_text/offline_transcription", "https://docs.mistral.ai/models/voxtral-mini-transcribe-26-02",
        ),
        SpeechProviderPreset(
            "speech-elevenlabs", "ElevenLabs", "https://api.elevenlabs.io/v1/speech-to-text",
            SpeechApiFormat.ElevenLabs,
            listOf(SpeechModelPreset("scribe_v2", "Scribe v2", ACCOUNT_BILLING)),
            "https://elevenlabs.io/docs/api-reference/speech-to-text/convert", "https://elevenlabs.io/pricing/api",
        ),
    )

    fun find(id: String): SpeechProviderPreset? = presets.firstOrNull { it.id == id }
}

internal fun CloudSpeechConfig.normalized(): CloudSpeechConfig = copy(
    endpoint = endpoint.trim(), apiKey = apiKey.trim(), model = model.trim(),
)

internal fun CloudSpeechConfig.validationError(): String? {
    AiConfiguration(AiProviderCatalog.CUSTOM, endpoint, model, apiKey).validationError()?.let { return it }
    if (!endpoint.trim().startsWith("https://")) return "语音接口必须使用 HTTPS，以保护录音和密钥"
    val preset = SpeechProviderCatalog.find(providerId)
    if (preset != null && endpoint.trim() != preset.endpoint) return "请选择对应平台，或改用自定义接口"
    return null
}

internal fun CloudSpeechConfig.selectProvider(id: String): CloudSpeechConfig {
    if (id == providerId) return this
    val preset = SpeechProviderCatalog.find(id) ?: return copy(providerId = SpeechProviderCatalog.CUSTOM)
    return copy(
        providerId = preset.id, endpoint = preset.endpoint, model = preset.models.first().id,
        apiKey = if (aiCredentialOrigin(endpoint) == aiCredentialOrigin(preset.endpoint)) apiKey else "",
    )
}

internal fun CloudSpeechConfig.editEndpoint(value: String): CloudSpeechConfig = copy(
    endpoint = value,
    apiKey = if (aiCredentialOrigin(endpoint) == aiCredentialOrigin(value)) apiKey else "",
)

internal fun speechApiFormat(endpoint: String): SpeechApiFormat {
    SpeechProviderCatalog.presets.firstOrNull { it.endpoint == endpoint.trim() }?.let { return it.format }
    val url = runCatching { java.net.URI(endpoint.trim()) }.getOrNull()
    val path = url?.path.orEmpty().trimEnd('/')
    val host = aiEndpointHost(endpoint)
    return when {
        host == "api.elevenlabs.io" && path == "/v1/speech-to-text" -> SpeechApiFormat.ElevenLabs
        host in setOf("dashscope.aliyuncs.com", "dashscope-intl.aliyuncs.com") &&
            path == "/compatible-mode/v1/chat/completions" -> SpeechApiFormat.QwenAudioChat
        host.endsWith(".maas.aliyuncs.com") && path == "/compatible-mode/v1/chat/completions" -> SpeechApiFormat.QwenAudioChat
        host in setOf("api.siliconflow.cn", "api.mistral.ai") && path == "/v1/audio/transcriptions" -> SpeechApiFormat.Multipart
        else -> SpeechApiFormat.OpenAiMultipart
    }
}

internal data class SpeechConnectionState(val status: AiConnectionStatus, val message: String)

internal fun restoredSpeechConnectionState(
    config: CloudSpeechConfig,
    validatedFingerprint: String,
    attemptedFingerprint: String,
    lastOutcome: String,
): SpeechConnectionState {
    val fingerprint = aiConfigurationFingerprint(config.endpoint, config.model, config.apiKey)
    return when {
        config.apiKey.isBlank() || config.validationError() != null ->
            SpeechConnectionState(AiConnectionStatus.Unconfigured, "请选择平台、模型并填写语音 API Key")
        fingerprint == validatedFingerprint ->
            SpeechConnectionState(AiConnectionStatus.Connected, "此语音配置已通过连接测试")
        fingerprint == attemptedFingerprint && lastOutcome == AiConnectionStatus.NoNetwork.name ->
            SpeechConnectionState(AiConnectionStatus.NoNetwork, "上次测试时没有可用网络，可手动重试")
        fingerprint == attemptedFingerprint && lastOutcome == AiConnectionStatus.Failed.name ->
            SpeechConnectionState(AiConnectionStatus.Failed, "上次语音连接测试未通过，可手动重试")
        else -> SpeechConnectionState(AiConnectionStatus.NeedsValidation, "配置已保存，可手动测试连接")
    }
}
