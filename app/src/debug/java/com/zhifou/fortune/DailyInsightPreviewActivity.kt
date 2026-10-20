package com.zhifou.fortune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

/** Shell-only visual fixture with synthetic content; no repository or AI client. */
class DailyInsightPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var dark by remember { mutableStateOf(false) }
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
            ZhiFouTheme(darkTheme = dark) {
                CompositionLocalProvider(LocalFortuneLayout provides rememberFortuneLayoutMetrics()) {
                    InsightPreview(dark, onDarkChange = { dark = it },
                        initialState = intent.getStringExtra("state") ?: "ready",
                        longContent = intent.getBooleanExtra("long", false))
                }
            }
        }
    }
}

@Composable
private fun InsightPreview(dark: Boolean, onDarkChange: (Boolean) -> Unit, initialState: String, longContent: Boolean) {
    var selected by remember { mutableStateOf<DailyInsight?>(null) }
    var previewState by remember { mutableStateOf(initialState) }
    val samples = remember {
        listOf("稳步推进", "先听后说", "控制变量").mapIndexed { index, title ->
            DailyInsight(DailyInsightCategory.entries[index], title,
                "先确认事实与边界，再根据实际情况安排。", DailyInsightTone.Balanced)
        }
    }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .safeDrawingPadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("今日关照配色", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            Switch(dark, onCheckedChange = onDarkChange, modifier = Modifier.semantics { contentDescription = "深色模式" })
        }
        samples.forEach { insight ->
            InsightTile(insight, aiConnected = true, modifier = Modifier.fillMaxWidth(), onClick = { selected = insight })
        }
    }
    selected?.let { insight ->
        val sample = "### 今日提示\n\n关注能够确认的信息，给计划留出调整空间。\n\n### 可行的一步\n\n选择一件可以完成的小事，完成后再复盘。"
        DailyAiInsightDialog(insight,
            aiContent = if (previewState == "ready") if (longContent) (sample + "\n\n").repeat(12) else sample else null,
            loading = previewState == "loading",
            error = if (previewState == "error") "暂时无法连接，请稍后重试。" else "",
            onRetry = { previewState = "ready" }, onDismiss = { selected = null })
    }
}
