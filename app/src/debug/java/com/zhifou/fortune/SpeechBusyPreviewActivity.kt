package com.zhifou.fortune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Exercises real AudioRecord ownership with a delayed in-memory decoder, never a provider API. */
class SpeechBusyPreviewActivity : ComponentActivity() {
    private lateinit var speech: OfflineSpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speech = OfflineSpeechRecognizer(this) { _, _, _ ->
            Thread.sleep(15_000)
            "Synthetic transcription completed"
        }
        setContent {
            ZhiFouTheme(darkTheme = false) {
                var status by remember { mutableStateOf("就绪") }
                Column(Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp)) {
                    Text(status)
                    Button(onClick = {
                        val result = speech.start(
                            cloudConfig = CloudSpeechConfig("https://invalid.example", "fixture", "fixture"),
                            onFinal = { status = "转写完成" },
                            onLevel = {},
                            onError = { status = it },
                        )
                        status = result.message ?: "录音已开始"
                    }) { Text("测试录音") }
                    Button(onClick = { speech.stop(false); status = "转写中" }) { Text("结束录音") }
                    Button(onClick = { speech.stop(true); status = "已取消" }) { Text("取消录音") }
                }
            }
        }
    }

    override fun onDestroy() {
        Thread { speech.close() }.start()
        super.onDestroy()
    }
}
