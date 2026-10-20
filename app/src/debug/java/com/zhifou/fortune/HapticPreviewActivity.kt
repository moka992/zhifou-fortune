package com.zhifou.fortune

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Shell-only motor fixture. No repository, credentials, sensors or network client. */
class HapticPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZhiFouTheme(darkTheme = false) {
                val haptics = rememberAppHaptics(true)
                var round by remember { mutableIntStateOf(if (intent.getBooleanExtra("auto", false)) 1 else 0) }
                var label by remember { mutableStateOf("触觉测试") }
                Column(Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(label, style = MaterialTheme.typography.titleLarge)
                    Text("本地隔离测试，不读写账户数据。")
                    Button(onClick = { round++ }) { Text("播放一轮") }
                }
                LaunchedEffect(round) {
                    if (round == 0) return@LaunchedEffect
                    delay(450)
                    Log.i("HapticCheck", haptics.capabilityReport())
                    listOf(HapticCue.NavigateIn, HapticCue.NavigateOut, HapticCue.ToggleOn, HapticCue.ToggleOff,
                        HapticCue.MenuOpen, HapticCue.MenuClose, HapticCue.Selection, HapticCue.FineSelection,
                        HapticCue.VoiceStart, HapticCue.GestureArm, HapticCue.GestureDisarm, HapticCue.VoiceCancel,
                        HapticCue.Send, HapticCue.CardDraw,
                        HapticCue.CardTurn, HapticCue.CardLand, HapticCue.CoinSpin, HapticCue.CoinFinish,
                        HapticCue.WheelFinish, HapticCue.DiceFinish).forEach { cue ->
                        label = cue.name
                        val accepted = haptics.scene(cue)
                        Log.i("HapticCheck", "cue=${cue.name} accepted=$accepted ${haptics.playbackReport()}")
                        delay(500)
                    }
                    val track = HapticGestureTrack()
                    label = "渐进阻力"
                    var gestureSubmitted = 0
                    (0..30).forEach { step ->
                        if (haptics.gestureProgress(track, step / 30f)) gestureSubmitted++
                        delay(45)
                    }
                    delay(160)
                    if (haptics.gestureProgress(track, .60f)) gestureSubmitted++
                    delay(160)
                    if (haptics.gestureProgress(track, 1.05f)) gestureSubmitted++
                    delay(160)
                    haptics.gestureFinished(track, true)
                    delay(220)
                    haptics.setEnabled(false)
                    check(!haptics.scene(HapticCue.Send)) { "Disabled feedback was submitted" }
                    haptics.setEnabled(true)
                    label = "测试完成"
                    Log.i("HapticCheck", "complete cues=20 gestureSubmitted=$gestureSubmitted disabled=silent ${haptics.playbackReport()}")
                }
            }
        }
    }
}
