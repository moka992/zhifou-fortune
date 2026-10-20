package com.zhifou.fortune

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Base64
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

data class CloudSpeechConfig(
    val endpoint: String,
    val apiKey: String,
    val model: String,
    val providerId: String = "custom",
) {
    override fun toString() = "CloudSpeechConfig(providerId=$providerId, credentials=redacted)"
}

interface SpeechTranscriber {
    fun transcribe(samples: FloatArray, sampleRate: Int): Result<String>
}

class ConfiguredSpeechTranscriber(private val config: CloudSpeechConfig) : SpeechTranscriber {
    override fun transcribe(samples: FloatArray, sampleRate: Int): Result<String> = runCatching {
        val request = CloudSpeechApi.buildRequest(config, samples, sampleRate)
        CloudSpeechApi.execute(client, request, config, allowEmpty = false)
    }

    companion object {
        private fun client(timeoutSeconds: Long) = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(timeoutSeconds + 15, TimeUnit.SECONDS)
            .followRedirects(false).followSslRedirects(false)
            .retryOnConnectionFailure(false)
            .build()

        private val client = client(90)
        private val testClient = client(25)

        /** A synthetic silent WAV verifies auth, model access and request schema, never microphone audio. */
        fun testConnection(config: CloudSpeechConfig): Result<Unit> = runCatching {
            val request = CloudSpeechApi.buildRequest(config, FloatArray(16_000), 16_000)
            CloudSpeechApi.execute(testClient, request, config, allowEmpty = true)
            Unit
        }
    }
}

internal object CloudSpeechApi {
    private val jsonType = "application/json; charset=utf-8".toMediaType()
    private const val MAX_WAVE_BYTES = 25_000_000

    fun buildRequest(config: CloudSpeechConfig, samples: FloatArray, sampleRate: Int): Request {
        require(config.validationError() == null) { config.validationError().orEmpty() }
        require(config.apiKey.isNotBlank()) { "未配置语音 API Key" }
        require(samples.isNotEmpty()) { "没有录到有效语音" }
        require(sampleRate in 8_000..48_000) { "不支持的录音采样率" }
        val format = speechApiFormat(config.endpoint)
        val waveSize = 44L + samples.size.toLong() * 2
        require(waveSize <= MAX_WAVE_BYTES) { "录音超过单次转写大小限制，请分段输入" }
        if (format == SpeechApiFormat.QwenAudioChat) {
            require(samples.size.toLong() <= sampleRate.toLong() * 300 && ((waveSize + 2) / 3) * 4 <= 10_000_000) {
                "录音超过通义千问单次时长或大小限制，请分段输入"
            }
        }
        val wave = encodeWave(samples, sampleRate)
        val request = Request.Builder().url(config.endpoint.trim())
        val body = when (format) {
            SpeechApiFormat.QwenAudioChat -> {
                val audio = JSONObject().put("type", "input_audio").put("input_audio",
                    JSONObject().put("data", "data:audio/wav;base64,${Base64.getEncoder().encodeToString(wave)}"))
                JSONObject().put("model", config.model.trim()).put("stream", false)
                    .put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", JSONArray().put(audio))))
                    .put("asr_options", JSONObject().put("enable_itn", true))
                    .toString().toRequestBody(jsonType)
            }
            else -> MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                addFormDataPart(if (format == SpeechApiFormat.ElevenLabs) "model_id" else "model", config.model.trim())
                addFormDataPart("file", "speech.wav", wave.toRequestBody("audio/wav".toMediaType()))
                if (format == SpeechApiFormat.OpenAiMultipart) addFormDataPart("response_format", "json")
                if (format == SpeechApiFormat.ElevenLabs) {
                    addFormDataPart("tag_audio_events", "false")
                    addFormDataPart("diarize", "false")
                }
            }.build()
        }
        if (format == SpeechApiFormat.ElevenLabs) request.header("xi-api-key", config.apiKey.trim())
        else request.header("Authorization", "Bearer ${config.apiKey.trim()}")
        return request.post(body).build()
    }

    fun execute(client: OkHttpClient, request: Request, config: CloudSpeechConfig, allowEmpty: Boolean): String {
        check(!client.followRedirects && !client.followSslRedirects) { "语音接口禁止转发认证信息" }
        return client.newCall(request).execute().use { response ->
            val raw = response.body?.string().orEmpty()
            if (!response.isSuccessful) error(AiApiTransport.failureMessage(response.code, raw, config.apiKey.trim()))
            parseResponse(config, raw, allowEmpty)
        }
    }

    fun parseResponse(config: CloudSpeechConfig, raw: String, allowEmpty: Boolean = false): String {
        val root = runCatching { JSONObject(raw) }.getOrElse { error("语音接口未返回有效 JSON，请检查接口地址") }
        require(root.opt("error") == null || root.opt("error") == JSONObject.NULL) { "语音服务返回错误，请检查模型与账户权限" }
        val value = if (speechApiFormat(config.endpoint) == SpeechApiFormat.QwenAudioChat) {
            val message = root.optJSONArray("choices")?.optJSONObject(0)?.optJSONObject("message")
                ?: error("语音接口返回格式不兼容，请检查模型与接口")
            when (val content = message.opt("content")) {
                is String -> content
                is JSONArray -> buildString {
                    for (i in 0 until content.length()) {
                        val block = content.optJSONObject(i) ?: continue
                        if (block.opt("text") is String) append(block.getString("text"))
                    }
                }
                else -> error("语音接口没有返回转写字段")
            }
        } else {
            root.opt("text") as? String ?: error("语音接口没有返回转写字段")
        }
        val text = if (config.model.contains("SenseVoiceSmall", ignoreCase = true)) {
            value.replace(Regex("<\\|[^|<>]+\\|>"), "").trim()
        } else value.trim()
        require(allowEmpty || text.isNotEmpty()) { "未识别到语音，请靠近麦克风重试" }
        return text
    }

    fun encodeWave(samples: FloatArray, sampleRate: Int): ByteArray {
        val dataSize = samples.size * 2
        return ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray(Charsets.US_ASCII)); putInt(36 + dataSize)
            put("WAVEfmt ".toByteArray(Charsets.US_ASCII)); putInt(16)
            putShort(1); putShort(1); putInt(sampleRate); putInt(sampleRate * 2); putShort(2); putShort(16)
            put("data".toByteArray(Charsets.US_ASCII)); putInt(dataSize)
            samples.forEach { sample ->
                val value = if (sample.isFinite()) sample.coerceIn(-1f, 1f) else 0f
                putShort((value * Short.MAX_VALUE).toInt().toShort())
            }
        }.array()
    }
}
