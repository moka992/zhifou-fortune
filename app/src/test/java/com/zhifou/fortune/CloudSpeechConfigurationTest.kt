package com.zhifou.fortune

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Base64
import java.util.concurrent.atomic.AtomicInteger
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class CloudSpeechConfigurationTest {
    private fun config(id: String = "speech-siliconflow"): CloudSpeechConfig {
        val provider = SpeechProviderCatalog.find(id)!!
        return CloudSpeechConfig(provider.endpoint, "dummy-not-a-real-key", provider.models.first().id, id)
    }

    private fun request(id: String) = CloudSpeechApi.buildRequest(config(id), FloatArray(16_000), 16_000)

    private fun Request.json(): JSONObject {
        val buffer = Buffer(); body!!.writeTo(buffer)
        return JSONObject(buffer.readUtf8())
    }

    private fun Request.form(): Map<String, String> = (body as MultipartBody).parts.associate { part ->
        val name = Regex("name=\"([^\"]+)\"").find(part.headers!!["Content-Disposition"]!!)!!.groupValues[1]
        val buffer = Buffer(); part.body.writeTo(buffer)
        name to if (name == "file") "WAV" else buffer.readUtf8()
    }

    private fun mockClient(code: Int, response: String, calls: AtomicInteger = AtomicInteger()): OkHttpClient =
        OkHttpClient.Builder().followRedirects(false).followSslRedirects(false).addInterceptor { chain ->
            calls.incrementAndGet()
            Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(code).message("Test")
                .header("Location", "https://other.example/transcriptions")
                .body(response.toResponseBody("application/json".toMediaType())).build()
        }.build()

    @Test fun catalogHasSixPlatformsWithSeparateQwenRegions() {
        val presets = SpeechProviderCatalog.presets
        assertEquals(7, presets.size)
        assertEquals(presets.size, presets.map { it.id }.distinct().size)
        assertEquals(presets.size, presets.map { it.endpoint }.distinct().size)
        presets.forEach { provider ->
            assertTrue(provider.endpoint.startsWith("https://"))
            assertTrue(provider.models.isNotEmpty())
            assertEquals(provider.format, speechApiFormat(provider.endpoint))
            assertNull(config(provider.id).validationError())
        }
    }

    @Test fun pricingDoesNotLabelEveryProviderAsFree() {
        assertTrue(SpeechProviderCatalog.find("speech-siliconflow")!!.models.first().billingNote.contains("免费模型"))
        assertTrue(SpeechProviderCatalog.find("speech-groq")!!.models.first().billingNote.contains("有限额"))
        assertFalse(SpeechProviderCatalog.find("speech-openai")!!.name.contains("免费"))
        assertTrue(SpeechProviderCatalog.find("speech-qwen-cn")!!.models.first().billingNote.contains("账户权益"))
    }

    @Test fun legacyEndpointModelAndSecretRemainUnchanged() {
        val old = CloudSpeechConfig("https://legacy.example/audio/transcriptions", "legacy-key", "legacy-model")
        assertEquals(old, old.selectProvider("custom"))
        assertEquals("legacy-model", old.normalized().model)
        assertFalse(old.toString().contains("legacy-key"))
        assertEquals(SpeechApiFormat.OpenAiMultipart, speechApiFormat(old.endpoint))
    }

    @Test fun crossProviderAndRegionalChangesClearSecrets() {
        val original = config()
        assertEquals("", original.selectProvider("speech-groq").apiKey)
        val china = config("speech-qwen-cn")
        assertEquals("", china.selectProvider("speech-qwen-intl").apiKey)
        assertEquals(original.apiKey, original.selectProvider("custom").apiKey)
        assertEquals("", original.copy(providerId = "custom").editEndpoint("https://different.example/v1/audio/transcriptions").apiKey)
        assertEquals(original.apiKey, original.copy(providerId = "custom").editEndpoint("https://api.siliconflow.cn:443/custom/path").apiKey)
    }

    @Test fun speechSettingsRequireHttpsAndRejectKeysInUrls() {
        val original = config().copy(providerId = "custom")
        listOf("http://example.com/asr", "not-a-url", "https://example.com/asr?key=secret", "https://user:pass@example.com/asr").forEach {
            assertNotNull(original.copy(endpoint = it).validationError())
        }
        assertNotNull(original.copy(apiKey = "secret\nheader").validationError())
        assertNotNull(original.copy(apiKey = "secret中文").validationError())
        assertNotNull(original.copy(model = " ").validationError())
    }

    @Test fun restoredVerifiedConfigurationNeedsNoNetworkProbe() {
        val config = config()
        val fingerprint = aiConfigurationFingerprint(config.endpoint, config.model, config.apiKey)
        repeat(5) {
            val state = restoredSpeechConnectionState(config, fingerprint, fingerprint, AiConnectionStatus.Connected.name)
            assertEquals(AiConnectionStatus.Connected, state.status)
        }
    }

    @Test fun changedAndMissingConfigurationRestoreDifferentStates() {
        val config = config()
        val fingerprint = aiConfigurationFingerprint(config.endpoint, config.model, config.apiKey)
        assertEquals(AiConnectionStatus.NeedsValidation,
            restoredSpeechConnectionState(config.copy(model = "other-model"), fingerprint, fingerprint, "Connected").status)
        assertEquals(AiConnectionStatus.Unconfigured,
            restoredSpeechConnectionState(config.copy(apiKey = ""), fingerprint, fingerprint, "Connected").status)
    }

    @Test fun offlineAndInvalidCredentialsAreNotTheSameState() {
        val config = config()
        val fp = aiConfigurationFingerprint(config.endpoint, config.model, config.apiKey)
        assertEquals(AiConnectionStatus.NoNetwork, restoredSpeechConnectionState(config, "", fp, "NoNetwork").status)
        assertEquals(AiConnectionStatus.Failed, restoredSpeechConnectionState(config, "", fp, "Failed").status)
        assertEquals(AiConnectionStatus.Connected, restoredSpeechConnectionState(config, fp, fp, "NoNetwork").status)
    }

    @Test fun syntheticWaveIsOneSecondMonoPcmWithNoRecordedAudio() {
        val wave = CloudSpeechApi.encodeWave(FloatArray(16_000), 16_000)
        assertEquals(32_044, wave.size)
        assertEquals("RIFF", wave.copyOfRange(0, 4).toString(Charsets.US_ASCII))
        assertEquals("WAVE", wave.copyOfRange(8, 12).toString(Charsets.US_ASCII))
        val header = ByteBuffer.wrap(wave).order(ByteOrder.LITTLE_ENDIAN)
        assertEquals(16_000, header.getInt(24))
        assertEquals(1, header.getShort(22).toInt())
        assertEquals(16, header.getShort(34).toInt())
        assertTrue(wave.drop(44).all { it == 0.toByte() })
    }

    @Test fun waveEncodingClampsUnsafeSamples() {
        val wave = CloudSpeechApi.encodeWave(floatArrayOf(-2f, 2f, Float.NaN, Float.POSITIVE_INFINITY), 16_000)
        val samples = ByteBuffer.wrap(wave).order(ByteOrder.LITTLE_ENDIAN)
        assertEquals(-32767, samples.getShort(44).toInt())
        assertEquals(32767, samples.getShort(46).toInt())
        assertEquals(0, samples.getShort(48).toInt())
        assertEquals(0, samples.getShort(50).toInt())
    }

    @Test fun openAiGroqAndCustomUseTheExistingMultipartSchema() {
        listOf("speech-openai", "speech-groq").forEach { id ->
            val request = request(id)
            assertEquals("Bearer dummy-not-a-real-key", request.header("Authorization"))
            assertEquals(setOf("model", "file", "response_format"), request.form().keys)
            assertEquals("json", request.form()["response_format"])
            assertFalse(request.form().containsKey("language"))
        }
        val custom = config().copy(endpoint = "https://custom.example/audio/transcriptions", providerId = "custom", model = "custom-asr")
        assertEquals("custom-asr", CloudSpeechApi.buildRequest(custom, FloatArray(1_600), 16_000).form()["model"])
    }

    @Test fun siliconFlowAndMistralOmitUndocumentedMultipartOptions() {
        listOf("speech-siliconflow", "speech-mistral").forEach {
            assertEquals(setOf("model", "file"), request(it).form().keys)
        }
    }

    @Test fun qwenUsesBase64AudioNotTextChatOrAnUploadedPublicUrl() {
        val body = request("speech-qwen-cn").json()
        val messages = body.getJSONArray("messages")
        assertEquals(1, messages.length())
        assertEquals("user", messages.getJSONObject(0).getString("role"))
        val content = messages.getJSONObject(0).getJSONArray("content").getJSONObject(0)
        assertEquals("input_audio", content.getString("type"))
        val data = content.getJSONObject("input_audio").getString("data")
        assertTrue(data.startsWith("data:audio/wav;base64,"))
        assertEquals(32_044, Base64.getDecoder().decode(data.substringAfter(',')).size)
        assertFalse(body.getBoolean("stream"))
        assertTrue(body.getJSONObject("asr_options").getBoolean("enable_itn"))
        assertFalse(body.getJSONObject("asr_options").has("language"))
        assertFalse(body.has("temperature"))
        assertFalse(body.has("max_tokens"))
    }

    @Test fun elevenLabsUsesItsOwnHeaderAndModelField() {
        val request = request("speech-elevenlabs")
        assertEquals("dummy-not-a-real-key", request.header("xi-api-key"))
        assertNull(request.header("Authorization"))
        assertEquals("scribe_v2", request.form()["model_id"])
        assertFalse(request.form().containsKey("model"))
        assertEquals("false", request.form()["diarize"])
        assertEquals("false", request.form()["tag_audio_events"])
    }

    @Test fun nativeAuthIsNotAppliedToLookalikeDomains() {
        assertEquals(SpeechApiFormat.OpenAiMultipart, speechApiFormat("https://api.elevenlabs.io.example/v1/speech-to-text"))
        assertEquals(SpeechApiFormat.OpenAiMultipart, speechApiFormat("https://api.elevenlabs.io/v1/audio/transcriptions"))
        assertEquals(SpeechApiFormat.QwenAudioChat, speechApiFormat("https://workspace.cn-beijing.maas.aliyuncs.com/compatible-mode/v1/chat/completions"))
    }

    @Test fun preservesMixedChineseEnglishTextAndRemovesSenseVoiceTags() {
        val raw = """{"text":"<|zh|><|NEUTRAL|><|Speech|><|withitn|>明天 meeting 10:30 开始。"}"""
        assertEquals("明天 meeting 10:30 开始。", CloudSpeechApi.parseResponse(config(), raw))
        assertEquals("今天 test OK", CloudSpeechApi.parseResponse(config("speech-groq"), """{"text":"今天 test OK"}"""))
    }

    @Test fun parsesBothQwenResponseShapes() {
        val stringResponse = """{"choices":[{"message":{"content":"今天 English test"}}]}"""
        val blockResponse = """{"choices":[{"message":{"content":[{"text":"今天 "},{"text":"English test"}]}}]}"""
        assertEquals("今天 English test", CloudSpeechApi.parseResponse(config("speech-qwen-cn"), stringResponse))
        assertEquals("今天 English test", CloudSpeechApi.parseResponse(config("speech-qwen-cn"), blockResponse))
    }

    @Test fun emptyTranscriptIsValidOnlyForTheExplicitSilentProbe() {
        assertEquals("", CloudSpeechApi.parseResponse(config(), """{"text":""}""", allowEmpty = true))
        assertTrue(runCatching { CloudSpeechApi.parseResponse(config(), """{"text":""}""") }.isFailure)
        listOf("{}", "{\"text\":null}", "{\"choices\":[]}", "{\"text\":\"\",\"error\":{\"message\":\"denied\"}}", "not-json").forEach {
            assertTrue(runCatching { CloudSpeechApi.parseResponse(config(), it, allowEmpty = true) }.isFailure)
        }
    }

    @Test fun probeValidatesResponseSchemaRatherThanJustHttp200() {
        val request = request("speech-siliconflow")
        val calls = AtomicInteger()
        assertEquals("", CloudSpeechApi.execute(mockClient(200, """{"text":""}""", calls), request, config(), true))
        assertEquals(1, calls.get())
        assertTrue(runCatching { CloudSpeechApi.execute(mockClient(200, "{}"), request, config(), true) }.isFailure)
    }

    @Test fun redirectsAndAuthenticationErrorsDoNotLeakCredentials() {
        val request = request("speech-siliconflow")
        val calls = AtomicInteger()
        val redirect = runCatching { CloudSpeechApi.execute(mockClient(307, "", calls), request, config(), true) }.exceptionOrNull()
        assertNotNull(redirect)
        assertEquals(1, calls.get())
        val failure = runCatching {
            CloudSpeechApi.execute(mockClient(400, """{"error":{"message":"Bad key dummy-not-a-real-key"}}"""), request, config(), true)
        }.exceptionOrNull()
        assertFalse(failure!!.message.orEmpty().contains("dummy-not-a-real-key"))
    }

    @Test fun rejectsBadAudioBeforeMakingARequest() {
        assertTrue(runCatching { CloudSpeechApi.buildRequest(config(), floatArrayOf(), 16_000) }.isFailure)
        assertTrue(runCatching { CloudSpeechApi.buildRequest(config(), FloatArray(160), 1) }.isFailure)
        assertTrue(runCatching { CloudSpeechApi.buildRequest(config().copy(apiKey = ""), FloatArray(160), 16_000) }.isFailure)
        val tooLong = FloatArray(8_000 * 301)
        assertTrue(runCatching { CloudSpeechApi.buildRequest(config("speech-qwen-cn"), tooLong, 8_000) }.isFailure)
    }

    @Test fun qwenBase64LimitIncludesEncodingOverhead() {
        val samples = FloatArray(48_000 * 100)
        assertTrue(runCatching { CloudSpeechApi.buildRequest(config("speech-qwen-cn"), samples, 48_000) }.isFailure)
    }
}
