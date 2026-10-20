package com.zhifou.fortune

import java.net.ServerSocket
import java.net.SocketTimeoutException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class AiProviderConfigurationTest {
    private val messages: JSONArray
        get() = JSONArray().put(JSONObject().put("role", "system").put("content", "Existing system prompt"))
            .put(JSONObject().put("role", "user").put("content", "Existing question"))

    private fun request(provider: String, model: String? = null): Request {
        val preset = AiProviderCatalog.find(provider)!!
        return AiApiTransport.buildRequest(preset.endpoint, "test-not-a-real-key", model ?: preset.models.first(), messages, 700, 0.35)
    }

    private fun Request.json(): JSONObject {
        val buffer = Buffer()
        body!!.writeTo(buffer)
        return JSONObject(buffer.readUtf8())
    }

    @Test fun catalogContainsNinePlatformsWithDistinctRegionalEndpoints() {
        val presets = AiProviderCatalog.presets
        assertEquals(12, presets.size)
        assertEquals(presets.size, presets.map { it.id }.distinct().size)
        assertEquals(presets.size, presets.map { it.endpoint }.distinct().size)
        presets.forEach {
            assertTrue(it.endpoint.startsWith("https://"))
            assertTrue(it.models.isNotEmpty())
            assertEquals(it.models.size, it.models.distinct().size)
            assertNull(AiConfiguration(it.id, it.endpoint, it.models.first(), "").validationError())
        }
    }

    @Test fun legacyCustomConfigurationIsPreservedVerbatim() {
        val old = AiConfiguration("custom", "https://example.com/legacy/completion", "DeepSeek-V4-Flash-0731", "legacy-secret")
        assertEquals(old, old.selectProvider("custom"))
        assertEquals("DeepSeek-V4-Flash-0731", old.normalized().model)
        assertFalse(old.toString().contains("legacy-secret"))
    }

    @Test fun switchingProvidersAndRegionsDoesNotReuseCredentials() {
        val old = AiConfiguration("deepseek", AiProviderCatalog.find("deepseek")!!.endpoint, "deepseek-flash", "old-key")
        assertEquals("", old.selectProvider("openai").apiKey)
        val regional = old.selectProvider("qwen-cn").copy(apiKey = "regional-key")
        assertEquals("", regional.selectProvider("qwen-intl").apiKey)
        assertEquals("old-key", old.selectProvider("custom").apiKey)
        assertEquals("old-key", old.selectProvider("custom").selectProvider("deepseek").apiKey)
    }

    @Test fun customOriginChangeClearsKeyButPathChangeDoesNot() {
        val config = AiConfiguration("custom", "https://example.com/v1/chat/completions", "model", "secret")
        assertEquals("secret", config.editEndpoint("https://EXAMPLE.com:443/chat/completions").apiKey)
        assertEquals("", config.editEndpoint("https://example.org/v1/chat/completions").apiKey)
        assertEquals("", config.editEndpoint("http://example.com/v1/chat/completions").apiKey)
        assertEquals("", config.editEndpoint("https://example.com:8443/v1/chat/completions").apiKey)
    }

    @Test fun queryCredentialsAndHeaderInjectionAreRejected() {
        val config = AiConfiguration("custom", "https://example.com/v1/chat/completions", "model", "secret")
        listOf("https://user:password@example.com/v1", "https://example.com/v1?key=secret", "not-a-url").forEach {
            assertNotNull(config.copy(endpoint = it).validationError())
        }
        assertNotNull(config.copy(apiKey = "secret\nheader").validationError())
        assertNotNull(config.copy(model = " ").validationError())
        assertNull(config.copy(apiKey = "").validationError())
    }

    @Test fun changingOnlyProviderLabelDoesNotInvalidateTheConnectionFingerprint() {
        val preset = AiProviderCatalog.find("deepseek")!!
        val config = AiConfiguration("custom", preset.endpoint, preset.models.first(), "secret")
        val selected = config.selectProvider(preset.id)
        assertEquals(aiConfigurationFingerprint(config.endpoint, config.model, config.apiKey),
            aiConfigurationFingerprint(selected.endpoint, selected.model, selected.apiKey))
    }

    @Test fun officialDeepSeekAndHostedDeepSeekUseDifferentThinkingParameters() {
        assertEquals("disabled", request("deepseek").json().getJSONObject("thinking").getString("type"))
        val hosted = request("siliconflow").json()
        assertFalse(hosted.getBoolean("enable_thinking"))
        assertFalse(hosted.has("thinking"))
        assertFalse(shouldDisableDeepSeekThinking(AiProviderCatalog.find("siliconflow")!!.endpoint, "deepseek-ai/DeepSeek-V4-Flash"))
        assertFalse(shouldDisableDeepSeekThinking("https://api.deepseek.com.evil.example/chat/completions", "deepseek"))
    }

    @Test fun qwenHybridThinkingIsExplicitlyDisabled() {
        listOf("qwen-cn", "qwen-intl").forEach {
            val body = request(it).json()
            assertFalse(body.getBoolean("enable_thinking"))
            assertFalse(body.getBoolean("preserve_thinking"))
        }
    }

    @Test fun kimiOmitsUnsupportedSamplingTemperature() {
        val body = request("kimi-cn").json()
        assertFalse(body.has("temperature"))
        assertEquals("disabled", body.getJSONObject("thinking").getString("type"))
        assertEquals(700, body.getInt("max_tokens"))
    }

    @Test fun minimaxSeparatesThoughtsAndUsesTheCurrentTokenParameter() {
        val body = request("minimax-cn").json()
        assertTrue(body.getBoolean("reasoning_split"))
        assertEquals("disabled", body.getJSONObject("thinking").getString("type"))
        assertEquals(700, body.getInt("max_completion_tokens"))
        assertFalse(body.has("max_tokens"))
    }

    @Test fun openAiUsesCompletionTokensWithoutUnsupportedSampling() {
        AiProviderCatalog.find("openai")!!.models.forEach { model ->
            val body = request("openai", model).json()
            assertEquals(700, body.getInt("max_completion_tokens"))
            assertEquals("none", body.getString("reasoning_effort"))
            assertFalse(body.has("max_tokens"))
            assertFalse(body.has("temperature"))
        }
    }

    @Test fun geminiUsesItsDocumentedMinimalReasoningLevel() {
        val body = request("gemini").json()
        assertEquals("minimal", body.getString("reasoning_effort"))
        assertFalse(body.has("thinking"))
        assertTrue(AiApiTransport.connectionTestTokenLimit(AiProviderCatalog.find("gemini")!!.endpoint) <= 512)
        assertEquals(16, AiApiTransport.connectionTestTokenLimit(AiProviderCatalog.find("deepseek")!!.endpoint))
    }

    @Test fun claudeUsesNativeAuthenticationAndSeparatesSystemMessages() {
        val request = request("claude")
        val body = request.json()
        assertEquals("test-not-a-real-key", request.header("x-api-key"))
        assertEquals("2023-06-01", request.header("anthropic-version"))
        assertNull(request.header("Authorization"))
        assertEquals("Existing system prompt", body.getString("system"))
        assertEquals(1, body.getJSONArray("messages").length())
        assertEquals("Existing question", body.getJSONArray("messages").getJSONObject(0).getString("content"))
        assertFalse(body.has("temperature"))
        assertEquals("disabled", body.getJSONObject("thinking").getString("type"))
        assertEquals("between_tools", request("claude", "claude-sonnet-5-5").json().getJSONObject("thinking").getString("type"))
    }

    @Test fun customCompatibleRequestsKeepExistingPromptsAndTokenBudget() {
        val body = AiApiTransport.buildRequest("https://custom.example/v1/chat/completions", "test", "custom-model", messages, 700, 0.35).json()
        assertEquals(messages.toString(), body.getJSONArray("messages").toString())
        assertEquals(700, body.getInt("max_tokens"))
        assertEquals(0.35, body.getDouble("temperature"), 0.0001)
        assertFalse(body.has("thinking"))
    }

    @Test fun nativeClaudeProtocolIsNotAppliedToAChatCompletionsPath() {
        val request = AiApiTransport.buildRequest("https://api.anthropic.com/v1/chat/completions", "test-key", "claude-custom", messages, 700, 0.35)
        assertEquals("Bearer test-key", request.header("Authorization"))
        assertNull(request.header("x-api-key"))
        assertEquals(messages.toString(), request.json().getJSONArray("messages").toString())
    }

    @Test fun unknownModelsDoNotReceiveUnverifiedThinkingOverrides() {
        assertFalse(request("zhipu", "glm-5.3").json().has("thinking"))
        assertFalse(request("kimi-cn", "kimi-k3").json().has("thinking"))
    }

    @Test fun allPresetRequestsPreserveDailyOutputBudgetAndSourceMessages() {
        AiProviderCatalog.presets.forEach { provider -> provider.models.forEach { model ->
            val body = request(provider.id, model).json()
            assertEquals(700, body.optInt("max_completion_tokens", body.optInt("max_tokens")))
            if (provider.id != "claude") assertEquals(messages.toString(), body.getJSONArray("messages").toString())
        } }
    }

    @Test fun parsesTextBlocksWithoutDisplayingPrivateThinking() {
        val normal = """{"choices":[{"message":{"content":"<think>private</think>Final answer"},"finish_reason":"stop"}]}"""
        assertEquals("Final answer", AiApiTransport.parseResponse(normal).content)
        val claude = """{"content":[{"type":"thinking","thinking":"private"},{"type":"text","text":"Final answer"}],"stop_reason":"max_tokens"}"""
        val answer = AiApiTransport.parseResponse(claude, anthropic = true)
        assertEquals("Final answer", answer.content)
        assertEquals("length", answer.finishReason)
        assertTrue(AiInterpreter.needsContinuation(answer.finishReason, answer.content))
        val blocks = """{"choices":[{"message":{"content":[{"type":"text","text":"OK"}]},"finish_reason":"stop"}]}"""
        assertEquals("OK", AiApiTransport.parseResponse(blocks).content)
    }

    @Test fun rejectsEmptyOrReasoningOnlySuccessResponses() {
        listOf("""{"choices":[{"message":{"content":null,"reasoning_content":"hidden"},"finish_reason":"length"}]}""",
            """{"choices":[{"message":{"content":"<think>unfinished"},"finish_reason":"length"}]}""").forEach {
            assertTrue(runCatching { AiApiTransport.parseResponse(it) }.isFailure)
        }
    }

    @Test fun apiErrorsNeverEchoTheConfiguredKey() {
        val message = AiApiTransport.failureMessage(400, """{"error":{"message":"Bad key test-secret-value, Bearer leaked-token sk-other-token"}}""", "test-secret-value")
        assertFalse(message.contains("test-secret-value"))
        assertFalse(message.contains("leaked-token"))
        assertFalse(message.contains("sk-other-token"))
        assertTrue(AiApiTransport.failureMessage(401, "", "test").contains("API Key"))
    }

    @Test fun invalidHeaderCharactersAreRejectedWithoutEchoingTheKey() {
        val key = "secret-value中文"
        val failure = runCatching {
            AiApiTransport.buildRequest(AiProviderCatalog.find("openai")!!.endpoint, key, "gpt-5.4-mini", messages, 16, 0.0)
        }.exceptionOrNull()
        assertNotNull(failure)
        assertFalse(failure!!.message.orEmpty().contains(key))
        assertTrue(isSafeAiApiKey("  test-not-a-real-key \n"))
        assertFalse(isSafeAiApiKey("secret key"))
    }

    @Test fun redirectDoesNotForwardTheKeyOrStartAnotherRequest() {
        val calls = AtomicInteger()
        val server = ServerSocket(0, 8, java.net.InetAddress.getByName("127.0.0.1"))
        server.soTimeout = 1_000
        val worker = thread {
            try {
                while (!server.isClosed) server.accept().use { socket ->
                    socket.soTimeout = 1_000
                    val input = socket.getInputStream().bufferedReader()
                    val headers = generateSequence { input.readLine() }.takeWhile { it.isNotEmpty() }.toList()
                    val length = headers.firstOrNull { it.startsWith("Content-Length:", ignoreCase = true) }
                        ?.substringAfter(':')?.trim()?.toInt() ?: 0
                    repeat(length) { input.read() }
                    val count = calls.incrementAndGet()
                    val response = if (count == 1) {
                        "HTTP/1.1 307 Temporary Redirect\r\nLocation: http://127.0.0.1:${server.localPort}/leak\r\nContent-Length: 0\r\nConnection: close\r\n\r\n"
                    } else {
                        val body = """{"choices":[{"message":{"content":"OK"},"finish_reason":"stop"}]}"""
                        "HTTP/1.1 200 OK\r\nContent-Length: ${body.length}\r\nConnection: close\r\n\r\n$body"
                    }
                    socket.getOutputStream().write(response.toByteArray(Charsets.US_ASCII))
                }
            } catch (_: SocketTimeoutException) {
                // No redirected request should reach the listener.
            } catch (_: java.net.SocketException) {
                // Listener closed by the test's finally block.
            }
        }
        try {
            val client = OkHttpClient.Builder().followRedirects(false).followSslRedirects(false).build()
            val request = AiApiTransport.buildRequest("http://127.0.0.1:${server.localPort}/redirect", "test-key", "model", messages, 16, 0.0)
            assertTrue(runCatching { AiApiTransport.execute(client, request, "test-key") }.isFailure)
            assertEquals(1, calls.get())
        } finally { server.close(); worker.join(2_000) }
    }
}
