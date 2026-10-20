package com.zhifou.fortune

import java.net.URI

internal data class AiProviderPreset(
    val id: String,
    val name: String,
    val endpoint: String,
    val models: List<String>,
    val documentation: String,
)

/** Curated text-generation APIs, verified against the providers' docs on 2026-10-18. */
internal object AiProviderCatalog {
    const val CUSTOM = "custom"
    val presets = listOf(
        AiProviderPreset("deepseek", "DeepSeek", "https://api.deepseek.com/chat/completions",
            listOf("deepseek-flash", "deepseek-v4-pro"), "https://api-docs.deepseek.com/zh-cn/"),
        AiProviderPreset("qwen-cn", "通义千问 · 中国内地", "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions",
            listOf("qwen3.8-flash", "qwen3.7-plus", "qwen3.8-max"), "https://help.aliyun.com/zh/model-studio/models"),
        AiProviderPreset("qwen-intl", "通义千问 · 新加坡", "https://dashscope-intl.aliyuncs.com/compatible-mode/v1/chat/completions",
            listOf("qwen3.8-flash", "qwen3.7-plus", "qwen3.8-max"), "https://help.aliyun.com/zh/model-studio/base-url"),
        AiProviderPreset("zhipu", "智谱 GLM", "https://open.bigmodel.cn/api/paas/v4/chat/completions",
            listOf("glm-4.7-flash", "glm-4.7"), "https://docs.bigmodel.cn/cn/guide/models/free/glm-4.7-flash"),
        AiProviderPreset("kimi-cn", "Kimi · 中国内地", "https://api.moonshot.cn/v1/chat/completions",
            listOf("kimi-k2.6"), "https://platform.kimi.com/docs/guide/kimi-k2-6-quickstart"),
        AiProviderPreset("kimi-intl", "Kimi · 国际", "https://api.moonshot.ai/v1/chat/completions",
            listOf("kimi-k2.6"), "https://platform.kimi.ai/docs/overview"),
        AiProviderPreset("minimax-cn", "MiniMax · 中国内地", "https://api.minimax.cn/v1/chat/completions",
            listOf("MiniMax-M3"), "https://platform.minimax.cn/docs/api-reference/text-openai-api"),
        AiProviderPreset("minimax-intl", "MiniMax · 国际", "https://api.minimax.io/v1/chat/completions",
            listOf("MiniMax-M3"), "https://platform.minimax.io/docs/api-reference/text-openai-api"),
        AiProviderPreset("siliconflow", "硅基流动", "https://api.siliconflow.cn/v1/chat/completions",
            listOf("deepseek-ai/DeepSeek-V4-Flash", "Pro/deepseek-ai/DeepSeek-V4"), "https://docs.siliconflow.cn/docs/api/chat-completions-post"),
        AiProviderPreset("openai", "OpenAI", "https://api.openai.com/v1/chat/completions",
            listOf("gpt-5.4-mini", "gpt-5.4", "gpt-5.5"), "https://developers.openai.com/api/docs/models"),
        AiProviderPreset("gemini", "Google Gemini", "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions",
            listOf("gemini-3.8-flash", "gemini-3.1-flash-lite"), "https://ai.google.dev/gemini-api/docs/openai"),
        AiProviderPreset("claude", "Anthropic Claude", "https://api.anthropic.com/v1/messages",
            listOf("claude-haiku-5-5", "claude-sonnet-5-5"), "https://platform.claude.com/docs/en/models/overview"),
    )

    fun find(id: String): AiProviderPreset? = presets.firstOrNull { it.id == id }
}

internal data class AiConfiguration(
    val providerId: String,
    val endpoint: String,
    val model: String,
    val apiKey: String,
) {
    override fun toString() = "AiConfiguration(providerId=$providerId, credentials=redacted)"

    fun selectProvider(id: String): AiConfiguration {
        if (id == providerId) return this
        val preset = AiProviderCatalog.find(id)
        if (preset == null) return copy(providerId = AiProviderCatalog.CUSTOM)
        return copy(
            providerId = preset.id,
            endpoint = preset.endpoint,
            model = preset.models.first(),
            // Keys are scoped to their original service, including regional deployments.
            apiKey = if (aiCredentialOrigin(endpoint) == aiCredentialOrigin(preset.endpoint)) apiKey else "",
        )
    }

    fun editEndpoint(value: String): AiConfiguration = copy(
        endpoint = value,
        apiKey = if (aiCredentialOrigin(endpoint) == aiCredentialOrigin(value)) apiKey else "",
    )

    fun normalized(): AiConfiguration = copy(
        endpoint = endpoint.trim(), model = model.trim(), apiKey = apiKey.trim(),
    )

    fun validationError(): String? {
        val url = runCatching { URI(endpoint.trim()) }.getOrNull()
        if (url == null || url.host.isNullOrBlank() || url.scheme !in setOf("http", "https") ||
            url.userInfo != null || url.fragment != null || url.query != null
        ) return "请输入完整接口地址，地址中不要包含密钥、查询参数或账号密码"
        if (model.isBlank()) return "请选择模型或填写模型 ID"
        if (!isSafeAiApiKey(apiKey)) return "API Key 格式不正确，请勿包含空格、换行或中文字符"
        val preset = AiProviderCatalog.find(providerId)
        if (preset != null && endpoint.trim() != preset.endpoint) return "预设接口已变化，请重新选择平台或使用自定义配置"
        return null
    }
}

internal fun aiCredentialOrigin(endpoint: String): String? = runCatching {
    val uri = URI(endpoint.trim())
    val scheme = uri.scheme?.lowercase() ?: return null
    val host = uri.host?.lowercase() ?: return null
    val port = uri.port.takeIf { it != -1 } ?: if (scheme == "https") 443 else 80
    "$scheme://$host:$port"
}.getOrNull()

internal fun aiEndpointHost(endpoint: String): String =
    runCatching { URI(endpoint.trim()).host?.lowercase().orEmpty() }.getOrDefault("")

internal fun isSafeAiApiKey(value: String): Boolean = value.trim().all { it.code in 33..126 }
