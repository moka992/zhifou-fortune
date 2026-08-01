package com.zhifou.fortune

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.os.Process
import com.k2fsa.sherpa.onnx.FeatureConfig
import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.math.sqrt

private const val SAMPLE_RATE = 16_000
private const val MODEL_DIR = "asr/sherpa-onnx-whisper-tiny-int8"
private const val ENCODER_FILE = "$MODEL_DIR/tiny-encoder.int8.onnx"
private const val DECODER_FILE = "$MODEL_DIR/tiny-decoder.int8.onnx"
private const val TOKENS_FILE = "$MODEL_DIR/tiny-tokens.txt"

class OfflineSpeechRecognizer(private val context: Context) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val modelLock = Any()
    private val modelDispatcher = Executors.newSingleThreadExecutor { task ->
        Thread(
            {
                Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST)
                task.run()
            },
            "zhifou-asr-model",
        ).apply { isDaemon = true }
    }.asCoroutineDispatcher()

    @Volatile
    private var recognizer: OfflineRecognizer? = null

    val isPrepared: Boolean
        get() = recognizer != null

    @Volatile
    private var recording = false

    @Volatile
    private var discardResult = false

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null

    suspend fun prepare(): Result<Unit> = withContext(modelDispatcher) {
        runCatching {
            synchronized(modelLock) {
                if (recognizer != null) return@synchronized
                listOf(ENCODER_FILE, DECODER_FILE, TOKENS_FILE).forEach(::requireAsset)
                val modelConfig = OfflineModelConfig(
                    whisper = OfflineWhisperModelConfig(
                        encoder = ENCODER_FILE,
                        decoder = DECODER_FILE,
                        language = "",
                        task = "transcribe",
                        tailPaddings = -1,
                    ),
                    tokens = TOKENS_FILE,
                    numThreads = 1,
                    provider = "cpu",
                )
                recognizer = OfflineRecognizer(
                    assetManager = context.assets,
                    config = OfflineRecognizerConfig(
                        featConfig = FeatureConfig(sampleRate = SAMPLE_RATE, featureDim = 80),
                        modelConfig = modelConfig,
                        decodingMethod = "greedy_search",
                        maxActivePaths = 4,
                    ),
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun start(
        cloudConfig: CloudSpeechConfig? = null,
        onFinal: (String) -> Unit,
        onLevel: (Float) -> Unit,
        onError: (String) -> Unit,
    ): Boolean {
        val activeRecognizer = if (cloudConfig == null) recognizer ?: return false else recognizer
        if (recording || recordingThread?.isAlive == true) return false

        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        if (minBufferSize <= 0) return false

        val recorder = createAudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, minBufferSize)
            ?: createAudioRecord(MediaRecorder.AudioSource.MIC, minBufferSize)
            ?: return false
        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            return false
        }

        return try {
            discardResult = false
            recording = true
            audioRecord = recorder
            recorder.startRecording()
            recordingThread = Thread {
                captureThenRecognize(activeRecognizer, cloudConfig, recorder, onFinal, onLevel, onError)
            }.apply {
                name = "zhifou-offline-asr"
                start()
            }
            true
        } catch (_: Throwable) {
            recording = false
            recorder.release()
            audioRecord = null
            false
        }
    }

    fun stop(cancelled: Boolean) {
        discardResult = cancelled
        recording = false
        try {
            audioRecord?.stop()
        } catch (_: Throwable) {
            // The recording thread also closes the recorder.
        }
    }

    fun close() {
        stop(cancelled = true)
        val activeThread = recordingThread
        try {
            activeThread?.join(1_500)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        if (activeThread?.isAlive == true) return
        recordingThread = null
        synchronized(modelLock) {
            recognizer?.release()
            recognizer = null
        }
        modelDispatcher.close()
    }

    private fun captureThenRecognize(
        recognizer: OfflineRecognizer?,
        cloudConfig: CloudSpeechConfig?,
        recorder: AudioRecord,
        onFinal: (String) -> Unit,
        onLevel: (Float) -> Unit,
        onError: (String) -> Unit,
    ) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
        val buffer = ShortArray(1_600)
        val recordedChunks = ArrayList<FloatArray>()
        var stream: com.k2fsa.sherpa.onnx.OfflineStream? = null
        try {
            while (recording) {
                val count = try {
                    recorder.read(buffer, 0, buffer.size)
                } catch (error: Throwable) {
                    if (!recording) break else throw error
                }
                if (count <= 0) continue
                recordedChunks += FloatArray(count) { buffer[it] / 32768f }
                var energy = 0.0
                for (index in 0 until count) {
                    val value = buffer[index].toDouble() / 32768.0
                    energy += value * value
                }
                val level = (sqrt(energy / count).toFloat() * 12f).coerceIn(0.06f, 1f)
                mainHandler.post { if (recording) onLevel(level) }
            }

            if (!discardResult) {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
                val finalText = if (cloudConfig != null) {
                    OpenAiCompatibleSpeechTranscriber(cloudConfig)
                        .transcribe(mergeChunks(recordedChunks), SAMPLE_RATE)
                        .getOrThrow()
                        .trim()
                } else {
                    requireNotNull(recognizer)
                    val decodeStream = recognizer.createStream()
                    stream = decodeStream
                    recordedChunks.forEach { samples ->
                        decodeStream.acceptWaveform(samples, SAMPLE_RATE)
                    }
                    recognizer.decode(decodeStream)
                    recognizer.getResult(decodeStream).text.trim()
                }
                mainHandler.post { if (!discardResult) onFinal(finalText) }
            }
        } catch (error: Throwable) {
            if (!discardResult) {
                val message = if (cloudConfig == null) {
                    "离线语音识别失败，请重试"
                } else {
                    error.message ?: "AI 语音识别失败，请检查接口配置"
                }
                mainHandler.post { onError(message) }
            }
        } finally {
            try {
                recorder.stop()
            } catch (_: Throwable) {
                // Recorder may already be stopped by the UI thread.
            }
            recorder.release()
            if (audioRecord === recorder) audioRecord = null
            if (recordingThread === Thread.currentThread()) recordingThread = null
            stream?.release()
        }
    }

    private fun requireAsset(path: String) {
        context.assets.open(path).use { stream ->
            check(stream.read() >= 0) { "语音模型文件为空：$path" }
        }
    }

    private fun mergeChunks(chunks: List<FloatArray>): FloatArray {
        val merged = FloatArray(chunks.sumOf { it.size })
        var offset = 0
        chunks.forEach { chunk ->
            chunk.copyInto(merged, destinationOffset = offset)
            offset += chunk.size
        }
        return merged
    }

    @SuppressLint("MissingPermission")
    private fun createAudioRecord(audioSource: Int, minBufferSize: Int): AudioRecord? {
        val recorder = try {
            AudioRecord(
                audioSource,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize * 2,
            )
        } catch (_: Throwable) {
            return null
        }
        if (recorder.state == AudioRecord.STATE_INITIALIZED) return recorder
        recorder.release()
        return null
    }
}
