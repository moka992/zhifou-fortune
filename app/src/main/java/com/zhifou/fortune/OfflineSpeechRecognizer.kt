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
import com.k2fsa.sherpa.onnx.OnlineModelConfig
import com.k2fsa.sherpa.onnx.OnlineRecognizer
import com.k2fsa.sherpa.onnx.OnlineRecognizerConfig
import com.k2fsa.sherpa.onnx.OnlineTransducerModelConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

private const val SAMPLE_RATE = 16_000
private const val MODEL_DIR = "asr/sherpa-onnx-streaming-zipformer-small-bilingual-zh-en-2023-02-16"

class OfflineSpeechRecognizer(private val context: Context) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val modelLock = Any()

    @Volatile
    private var recognizer: OnlineRecognizer? = null

    @Volatile
    private var recording = false

    @Volatile
    private var discardResult = false

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null

    suspend fun prepare(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            synchronized(modelLock) {
                if (recognizer != null) return@synchronized
                val modelConfig = OnlineModelConfig(
                    transducer = OnlineTransducerModelConfig(
                        encoder = "$MODEL_DIR/encoder-epoch-99-avg-1.int8.onnx",
                        decoder = "$MODEL_DIR/decoder-epoch-99-avg-1.onnx",
                        joiner = "$MODEL_DIR/joiner-epoch-99-avg-1.int8.onnx",
                    ),
                    tokens = "$MODEL_DIR/tokens.txt",
                    numThreads = 1,
                    provider = "cpu",
                    modelType = "zipformer",
                )
                recognizer = OnlineRecognizer(
                    assetManager = context.assets,
                    config = OnlineRecognizerConfig(
                        featConfig = FeatureConfig(sampleRate = SAMPLE_RATE, featureDim = 80),
                        modelConfig = modelConfig,
                        enableEndpoint = false,
                        decodingMethod = "greedy_search",
                        maxActivePaths = 1,
                    ),
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun start(
        onPartial: (String) -> Unit,
        onFinal: (String) -> Unit,
        onLevel: (Float) -> Unit,
        onError: (String) -> Unit,
    ): Boolean {
        val activeRecognizer = recognizer ?: return false
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
                processAudio(activeRecognizer, recorder, onPartial, onFinal, onLevel, onError)
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
    }

    private fun processAudio(
        recognizer: OnlineRecognizer,
        recorder: AudioRecord,
        onPartial: (String) -> Unit,
        onFinal: (String) -> Unit,
        onLevel: (Float) -> Unit,
        onError: (String) -> Unit,
    ) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
        val stream = recognizer.createStream()
        val buffer = ShortArray(1_600)
        var lastText = ""
        try {
            while (recording) {
                val count = recorder.read(buffer, 0, buffer.size)
                if (count <= 0) continue
                val samples = FloatArray(count) { buffer[it] / 32768f }
                stream.acceptWaveform(samples, SAMPLE_RATE)
                while (recognizer.isReady(stream)) recognizer.decode(stream)

                val text = recognizer.getResult(stream).text.trim()
                if (text.isNotBlank() && text != lastText) {
                    lastText = text
                    mainHandler.post { if (!discardResult) onPartial(text) }
                }

                var energy = 0.0
                for (index in 0 until count) {
                    val value = buffer[index].toDouble() / 32768.0
                    energy += value * value
                }
                val rms = sqrt(energy / count).toFloat()
                val level = (rms * 12f).coerceIn(0.06f, 1f)
                mainHandler.post { if (recording) onLevel(level) }
            }

            if (!discardResult) {
                stream.inputFinished()
                while (recognizer.isReady(stream)) recognizer.decode(stream)
                val finalText = recognizer.getResult(stream).text.trim().ifBlank { lastText }
                mainHandler.post { if (!discardResult) onFinal(finalText) }
            }
        } catch (_: Throwable) {
            if (!discardResult) mainHandler.post { onError("离线语音识别失败，请重试") }
        } finally {
            try {
                recorder.stop()
            } catch (_: Throwable) {
                // Recorder may already be stopped by the UI thread.
            }
            recorder.release()
            if (audioRecord === recorder) audioRecord = null
            if (recordingThread === Thread.currentThread()) recordingThread = null
            stream.release()
        }
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
