package com.zhifou.fortune

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

internal data class AiCompletion(val content: String, val finishReason: String)

/** Provider-specific wire formats only; divination prompts and output budgets stay with callers. */
internal object AiApiTransport {
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    fun buildRequest(
        endpoint: String,
        apiKey: String,
        model: String,
        messages: JSONArray,
        maxTokens: Int,
        temperature: Double,
    ): Request {
        require(isSafeAiApiKey(apiKey)) { "API Key 格式不正确，请勿包含空格、换行或中文字符" }
        val host = aiEndpointHost(endpoint)
        val normalizedModel = model.trim().lowercase()
        val body = JSONObject().put("model", model.trim()).put("messages", messages)
            .put("max_tokens", maxTokens).put("temperature", temperature)
        val request = Request.Builder().url(endpoint.trim()).header("Content-Type", "application/json")

        when {
            isAnthropicMessagesEndpoint(endpoint) -> {
                val system = mutableListOf<String>()
                val conversation = JSONArray()
                for (i in 0 until messages.length()) {
                    val message = messages.getJSONObject(i)
                    if (message.optString("role") == "system") system += message.getString("content")
                    else conversation.put(message)
                }
                body.put("messages", conversation).remove("temperature")
                if (system.isNotEmpty()) body.put("system", system.joinToString("\n\n"))
                when {
                    normalizedModel.startsWith("claude-sonnet-5-5") -> {
                        body.put("thinking", JSONObject().put("type", "between_tools"))
                        body.put("output_config", JSONObject().put("effort", "low"))
                    }
                    normalizedModel.startsWith("claude-haiku-5-5") -> {
                        body.put("thinking", JSONObject().put("type", "disabled"))
                        body.put("output_config", JSONObject().put("effort", "low"))
                    }
                }
                request.header("x-api-key", apiKey.trim()).header("anthropic-version", "2023-06-01")
            }
            else -> {
                request.header("Authorization", "Bearer ${apiKey.trim()}")
                when (host) {
                    "api.deepseek.com" -> if (shouldDisableDeepSeekThinking(endpoint, model)) body.disableThinking()
                    "api.openai.com" -> if (normalizedModel.startsWith("gpt-5")) {
                        body.put("max_completion_tokens", body.remove("max_tokens"))
                        body.remove("temperature")
                        if (normalizedModel.startsWith("gpt-5.4") || normalizedModel.startsWith("gpt-5.5")) {
                            body.put("reasoning_effort", "none")
                        }
                    }
                    "dashscope.aliyuncs.com", "dashscope-intl.aliyuncs.com" ->
                        if (normalizedModel.startsWith("qwen3") && !normalizedModel.contains("thinking")) {
                            body.put("enable_thinking", false).put("preserve_thinking", false)
                        }
                    "open.bigmodel.cn" -> if (normalizedModel.startsWith("glm-4.7")) body.disableThinking()
                    "api.moonshot.cn", "api.moonshot.ai" -> if (normalizedModel.startsWith("kimi-k2.6")) {
                        body.disableThinking().remove("temperature")
                    }
                    "api.minimax.cn", "api.minimax.io" -> {
                        body.put("reasoning_split", true)
                        body.put("max_completion_tokens", body.remove("max_tokens"))
                        if (normalizedModel == "minimax-m3") body.disableThinking()
                    }
                    "api.siliconflow.cn" -> if (normalizedModel.contains("deepseek-v4")) {
                        body.put("enable_thinking", false)
                    }
                    "generativelanguage.googleapis.com" -> {
                        if (normalizedModel.startsWith("gemini-3")) body.put("reasoning_effort", "minimal")
                    }
                }
            }
        }
        return request.post(body.toString().toRequestBody(jsonType)).build()
    }

    fun execute(client: OkHttpClient, request: Request, apiKey: String): AiCompletion {
        check(!client.followRedirects && !client.followSslRedirects) { "AI transport must not forward credentials through redirects" }
        return client.newCall(request).execute().use { response ->
            val raw = response.body?.string().orEmpty()
            if (!response.isSuccessful) error(failureMessage(response.code, raw, apiKey))
            parseResponse(raw, isAnthropicMessagesEndpoint(request.url.toString()))
        }
    }

    fun parseResponse(raw: String, anthropic: Boolean = false): AiCompletion {
        val root = JSONObject(raw)
        val finishReason: String
        val content: String
        if (anthropic) {
            finishReason = root.optString("stop_reason", "").let { if (it == "max_tokens") "length" else it }
            content = textBlocks(root.optJSONArray("content"))
        } else {
            val choice = root.optJSONArray("choices")?.optJSONObject(0) ?: error("接口没有返回有效内容")
            finishReason = choice.optString("finish_reason", "")
            val message = choice.optJSONObject("message") ?: error("接口没有返回有效内容")
            content = when (val value = message.opt("content")) {
                is String -> value
                is JSONArray -> textBlocks(value)
                else -> ""
            }
        }
        // Some compatible endpoints embed private reasoning in the final-content field.
        val visible = content.replace(Regex("<think>.*?</think>", RegexOption.DOT_MATCHES_ALL), "")
            .substringBefore("<think>").trim()
        if (visible.isBlank()) {
            if (finishReason in setOf("length", "max_tokens")) error("模型达到输出上限但未返回正文，请尝试直答模型")
            error("接口没有返回有效内容，请确认模型支持文本对话")
        }
        return AiCompletion(visible, finishReason)
    }

    fun connectionTestTokenLimit(endpoint: String): Int =
        if (aiEndpointHost(endpoint) == "generativelanguage.googleapis.com") 512 else 16

    fun failureMessage(code: Int, raw: String, apiKey: String): String {
        val detail = runCatching { JSONObject(raw).optJSONObject("error")?.optString("message").orEmpty() }
            .getOrDefault("")
            .let { if (apiKey.isBlank()) it else it.replace(apiKey, "[redacted]") }
            .replace(Regex("(?i)(bearer\\s+|sk-)[A-Za-z0-9._-]+"), "[redacted]")
            .take(180)
        val hint = when (code) {
            301, 302, 303, 307, 308 -> "接口要求跳转，为保护密钥已停止请求，请使用最终接口地址"
            401 -> "API Key 无效或不属于当前平台 / 地区"
            403 -> "该密钥或当前地区没有模型访问权限"
            404 -> "模型或接口不存在，请检查模型 ID 和地区"
            429 -> "余额、配额或请求频率受限"
            else -> detail
        }
        return "HTTP $code" + if (hint.isBlank()) "" else "：$hint"
    }

    private fun JSONObject.disableThinking(): JSONObject = put("thinking", JSONObject().put("type", "disabled"))

    private fun isAnthropicMessagesEndpoint(endpoint: String): Boolean =
        aiEndpointHost(endpoint) == "api.anthropic.com" &&
            runCatching { java.net.URI(endpoint).path.trimEnd('/') == "/v1/messages" }.getOrDefault(false)

    private fun textBlocks(blocks: JSONArray?): String = buildString {
        if (blocks != null) for (i in 0 until blocks.length()) {
            val block = blocks.optJSONObject(i) ?: continue
            if (block.optString("type") == "text") append(block.optString("text", ""))
        }
    }
}
