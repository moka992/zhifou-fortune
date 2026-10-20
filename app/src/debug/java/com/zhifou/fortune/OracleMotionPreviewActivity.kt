package com.zhifou.fortune

import android.os.Bundle
import android.os.SystemClock
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

/** Shell-only debug fixture: no repository, account, network client or saved reading. */
class OracleMotionPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ZhiFouTheme(darkTheme = intent.getBooleanExtra("dark", false)) {
            CompositionLocalProvider(LocalFortuneLayout provides rememberFortuneLayoutMetrics()) { Preview() }
        } }
    }
}

@Composable
private fun Preview() {
    var kind by remember { mutableStateOf(OracleMotionKind.Coins) }
    var startedAt by remember { mutableStateOf(0L) }
    var running by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    var lines by remember { mutableStateOf<List<CoinLineResult>>(emptyList()) }
    var current by remember { mutableStateOf<CoinLineResult?>(null) }
    var revealed by remember { mutableIntStateOf(0) }
    val fixture = remember { listOf(listOf(3, 2, 3), listOf(2, 2, 2), listOf(3, 3, 2), listOf(3, 3, 3), listOf(2, 3, 2), listOf(3, 2, 2)) }
    val draws = remember { TarotDeck.cards.take(10).mapIndexed { i, card -> TarotDraw(i + 1, card.id, i % 3 == 0) } }
    val haptics = rememberAppHaptics(enabled)
    LaunchedEffect(startedAt) {
        if (startedAt == 0L) return@LaunchedEffect
        lines = emptyList()
        current = null
        revealed = 0
        running = true
        if (kind == OracleMotionKind.Coins) {
            repeat(6) { index ->
                awaitOracleMoment(startedAt, OracleMotion.coinRoundStart(index))
                current = CoinLineResult(index + 1, fixture[index])
                awaitOracleMoment(startedAt, OracleMotion.coinRoundSettled(index))
                lines = lines + current!!
            }
        } else repeat(10) { index ->
            awaitOracleMoment(startedAt, OracleMotion.cardTurn(index) + OracleMotion.cardFlipMs / 2)
            revealed = index + 1
        }
        awaitOracleMoment(startedAt, OracleMotion.duration(kind))
        running = false
    }
    CompositionLocalProvider(LocalAppHaptics provides haptics) {
        if (running) OracleMotionFeedback(startedAt, kind)
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { kind = OracleMotionKind.Coins; startedAt = SystemClock.uptimeMillis() },
                    modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 12.dp)) { Text("铜钱动画", maxLines = 1) }
                Button(onClick = { kind = OracleMotionKind.Tarot; startedAt = SystemClock.uptimeMillis() },
                    modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 12.dp)) { Text("塔罗动画", maxLines = 1) }
                Switch(enabled, onCheckedChange = { enabled = it }, modifier = Modifier.semantics { contentDescription = "演示触觉反馈" })
            }
            Text(if (running) "动画进行中" else "演示就绪", style = MaterialTheme.typography.labelSmall)
            BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
            val availableHeight = maxHeight
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                if (startedAt != 0L) {
                    if (kind == OracleMotionKind.Coins) CoinCastingProgressCard(lines, "", current, startedAt, availableHeight)
                    else TarotDrawingProgressCard(draws, revealed, "", startedAt, availableHeight)
                }
            }
            }
        }
    }
}
