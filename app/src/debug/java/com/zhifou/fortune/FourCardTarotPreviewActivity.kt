package com.zhifou.fortune

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

/** Shell-only physical-device fixture: no account, persistent history or network client. */
class FourCardTarotPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            android.graphics.RuntimeShader(TAROT_BACK_SHADER)
            android.graphics.RuntimeShader(TAROT_COAT_SHADER)
            Log.i("FourCardCheck", "OPTICS compiled=2")
        }
        val bars = if (intent.getBooleanExtra("dark", false)) SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            else SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = bars, navigationBarStyle = bars)
        setContent {
            ZhiFouTheme(darkTheme = intent.getBooleanExtra("dark", false)) {
                val haptics = rememberAppHaptics(true)
                val lamp = remember { GlassLightController().apply {
                    update(intent.getFloatExtra("lightX", -.18f), intent.getFloatExtra("lightY", -.24f))
                } }
                var stored by rememberSaveable { mutableStateOf<String?>(null) }
                val history = remember(stored) { stored?.let { listOf(org.json.JSONObject(it).toReading()) } ?: emptyList() }
                val density = LocalDensity.current
                CompositionLocalProvider(LocalAppHaptics provides haptics,
                    LocalGlassLightController provides lamp,
                    LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                    LocalReducedFortuneMotion provides (LocalReducedFortuneMotion.current || intent.getBooleanExtra("reduced", false)),
                    LocalDensity provides Density(density.density, intent.getFloatExtra("fontScale", density.fontScale))) {
                CompositionLocalProvider(LocalFortuneLayout provides rememberFortuneLayoutMetrics()) {
                    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding()) {
                        Box(Modifier.weight(1f)) {
                            FourCardTarotExperience(history, aiConfigured = false, aiBusyIds = emptySet(),
                                onRecord = { session ->
                                    val reading = FourCardTarot.reading(session)
                                    stored = reading.toJson().toString()
                                    Log.i("FourCardCheck", "COMPLETE count=${reading.tarotDraws.size} unique=${reading.tarotDraws.map { it.cardId }.distinct().size} id=${reading.id}")
                                },
                                onExplain = { error("Fixture must not invoke an AI provider") },
                                onBack = { finish() })
                        }
                        Text("本地交互测试 · 不写入账户记录", style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.fillMaxWidth().height(68.dp).padding(16.dp))
                    }
                }
                }
            }
        }
    }
}
