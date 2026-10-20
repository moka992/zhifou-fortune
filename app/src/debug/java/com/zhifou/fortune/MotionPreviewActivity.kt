package com.zhifou.fortune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/** Shell-only motion fixture. No repository, permissions, personal content, or network clients. */
class MotionPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val pagerPreview = intent.getBooleanExtra("oracle_pager", false)
        setContent {
            ZhiFouTheme(darkTheme = false) {
                CompositionLocalProvider(LocalFortuneLayout provides rememberFortuneLayoutMetrics()) {
                    if (pagerPreview) OraclePagerPreview() else MotionPreview()
                }
            }
        }
    }
}

@Composable
private fun OraclePagerPreview() {
    val pager = rememberPagerState { ORACLE_PAGE_COUNT }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .safeDrawingPadding().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.fillMaxWidth().weight(1f).then(oraclePagerGestureRegion())
            .semantics { contentDescription = "演示分页区域" }) {
            HorizontalPager(pager, modifier = Modifier.fillMaxSize()) { page ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (page == 0) "今日灵感" else "消息", style = MaterialTheme.typography.headlineMedium)
                }
            }
            OraclePagerIndicator(pager, Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp))
        }
        Surface(Modifier.fillMaxWidth().height(72.dp), color = MaterialTheme.colorScheme.surfaceContainer,
            shape = MaterialTheme.shapes.large) {
            Box(contentAlignment = Alignment.Center) { Text("模拟输入区") }
        }
    }
}

@Composable
private fun MotionPreview() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var count by rememberSaveable { mutableIntStateOf(0) }
    var reverse by rememberSaveable { mutableStateOf(false) }
    var dialog by rememberSaveable { mutableStateOf(false) }
    val press = remember { MutableInteractionSource() }
    val disabled = remember { MutableInteractionSource() }
    val fade = fortuneEffectsSpec<Float>()
    val placement = fortuneSpatialSpec<IntOffset>()
    val rows = if (reverse) (1..4).reversed().toList() else (1..4).toList()
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding()
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(if (LocalReducedFortuneMotion.current) "动效已关闭" else "动效已开启", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { count++ }, interactionSource = press, modifier = Modifier.fortunePress(press)) { Text("按压反馈") }
            Button(onClick = { count++ }, enabled = false, interactionSource = disabled,
                modifier = Modifier.fortunePress(disabled, enabled = false)) { Text("不可发送") }
            Text("计数 $count")
        }
        Button(onClick = { expanded = !expanded }) { Text("展开收起") }
        FortuneAnimatedVisibility(expanded) {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.large) {
                Text("离线演示内容", Modifier.fillMaxWidth().padding(20.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { reverse = !reverse }) { Text("切换排序") }
            Button(onClick = { dialog = true }) { Text("详情预览") }
        }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rows, key = { it }) { row ->
                Surface(Modifier.fillMaxWidth().animateItem(fade, placement, fade),
                    color = MaterialTheme.colorScheme.surfaceContainer, shape = MaterialTheme.shapes.medium) {
                    Text("演示项目 $row", Modifier.padding(20.dp))
                }
            }
        }
    }
    if (dialog) DailyAiInsightDialog(
        DailyInsight(DailyInsightCategory.Career, "稳步推进", "先确认事实，再安排下一步。", DailyInsightTone.Balanced),
        aiContent = "离线演示内容，不发送模型请求。", loading = false, error = "",
        onRetry = {}, onDismiss = { dialog = false },
    )
}
