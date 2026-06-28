package com.zhifou.fortune

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

data class CloudSpeechConfig(
    val endpoint: String,
    val apiKey: String,
    val model: String,
)

interface SpeechTranscriber {
    fun transcribe(samples: FloatArray, sampleRate: Int): Result<String>
}

class OpenAiCompatibleSpeechTranscriber(
    private val config: CloudSpeechConfig,
) : SpeechTranscriber {
    override fun transcribe(samples: FloatArray, sampleRate: Int): Result<String> = runCatching {
        require(samples.isNotEmpty()) { "没有录到有效语音" }
        require(config.endpoint.startsWith("https://")) { "语音接口必须使用 HTTPS" }
        require(config.apiKey.isNotBlank()) { "未配置语音接口 API Key" }
        require(config.model.isNotBlank()) { "未配置语音识别模型" }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("model", config.model)
            .addFormDataPart(
                "file",
                "speech.wav",
                encodeWave(samples, sampleRate).toRequestBody("audio/wav".toMediaType()),
            )
            .addFormDataPart("response_format", "json")
            .build()
        val request = Request.Builder()
            .url(config.endpoint)
            .header("Authorization", "Bearer ${config.apiKey}")
            .post(requestBody)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("语音接口请求失败（HTTP ${response.code}）")
            }
            val responseBody = response.body?.string().orEmpty()
            val text = JSONObject(responseBody).optString("text").trim()
            require(text.isNotEmpty()) { "语音接口没有返回识别文字" }
            text
        }
    }

    private fun encodeWave(samples: FloatArray, sampleRate: Int): ByteArray {
        val dataSize = samples.size * 2
        return ByteBuffer.allocate(44 + dataSize)
            .order(ByteOrder.LITTLE_ENDIAN)
            .apply {
                put("RIFF".toByteArray(Charsets.US_ASCII))
                putInt(36 + dataSize)
                put("WAVE".toByteArray(Charsets.US_ASCII))
                put("fmt ".toByteArray(Charsets.US_ASCII))
                putInt(16)
                putShort(1)
                putShort(1)
                putInt(sampleRate)
                putInt(sampleRate * 2)
                putShort(2)
                putShort(16)
                put("data".toByteArray(Charsets.US_ASCII))
                putInt(dataSize)
                samples.forEach { sample ->
                    putShort((sample.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort())
                }
            }
            .array()
    }

    private companion object {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}
