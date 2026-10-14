package com.zhifou.fortune

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun PageHeading(
    title: String,
    subtitle: String = "",
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val layout = LocalFortuneLayout.current
    val shortWide = layout.compactHeight && layout.windowWidth != FortuneWindowWidth.Compact
    Row(
        Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = if (shortWide) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium)
            if (subtitle.isNotBlank() && !shortWide) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        trailing()
    }
}

@Composable
internal fun FortuneSideRail(selectedTab: Tab, onTabSelected: (Tab) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val compactHeight = LocalFortuneLayout.current.compactHeight
    Column(
        Modifier.width(80.dp).fillMaxHeight()
            .background(LocalFortunePalette.current.panel)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
            .padding(vertical = 4.dp, horizontal = 4.dp),
    ) {
        Tab.entries.forEach { destination ->
            val selected = selectedTab == destination
            val oracle = destination == Tab.Oracle
            Column(
                    (if (compactHeight) Modifier.weight(1f) else Modifier.height(80.dp))
                        .fillMaxWidth().clip(MaterialTheme.shapes.medium)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onTabSelected(destination) }),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    color = when {
                        oracle -> colors.primary
                        selected -> colors.primaryContainer
                        else -> androidx.compose.ui.graphics.Color.Transparent
                    },
                    shape = if (oracle) CircleShape else MaterialTheme.shapes.small,
                    modifier = Modifier.size(width = if (oracle) 40.dp else 44.dp, height = if (oracle) 40.dp else 32.dp),
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(destination.icon, null,
                            tint = when {
                                oracle -> colors.onPrimary
                                selected -> colors.primary
                                else -> colors.onSurfaceVariant
                            },
                            modifier = Modifier.size(23.dp))
                    }
                }
                Text(destination.label, style = MaterialTheme.typography.labelMedium,
                    color = if (selected) colors.primary else colors.onSurfaceVariant, maxLines = 1)
            }
        }
    }
}

@Composable
internal fun DailyOverview(snapshot: DailyFortuneSnapshot) {
    val reading = snapshot.reading
    val colors = MaterialTheme.colorScheme
    val compact = LocalFortuneLayout.current.compactWidth
    val rhythm = when {
        reading.score >= 72 -> "顺势向前"
        reading.score >= 62 -> "稳步展开"
        else -> "放慢一点"
    }
    Column(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("今日节奏", color = colors.primary, style = MaterialTheme.typography.labelLarge)
                Text(rhythm, style = MaterialTheme.typography.headlineLarge)
                Text(snapshot.almanac.lunarLabel, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            RhythmDial(reading.score, Modifier.size(if (compact) 100.dp else 116.dp))
        }
        Text(reading.body, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
            Box(Modifier.width(3.dp).height(38.dp).background(colors.primary, CircleShape))
            Text(reading.advice.removePrefix("今日建议："), style = MaterialTheme.typography.bodyMedium, color = colors.onSurface)
        }
        Text("节奏刻度仅作每日参考", style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
    }
}

@Composable
private fun RhythmDial(score: Int, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    var revealed by rememberSaveable(score) { mutableStateOf(false) }
    LaunchedEffect(score) { revealed = true }
    val progress by animateFloatAsState(
        if (revealed) score.coerceIn(0, 100) / 100f else 0f,
        tween(850), label = "daily-rhythm",
    )
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().padding(2.dp)) {
            val outerRadius = size.minDimension / 2f - 1.dp.toPx()
            repeat(41) { index ->
                val angle = Math.toRadians(135.0 + index * 270.0 / 40)
                val direction = Offset(cos(angle).toFloat(), sin(angle).toFloat())
                val tickLength = if (index % 5 == 0) 5.dp.toPx() else 2.5.dp.toPx()
                drawLine(
                    if (index / 40f <= progress) colors.primary.copy(alpha = 0.72f) else colors.outlineVariant,
                    center + direction * (outerRadius - tickLength),
                    center + direction * outerRadius,
                    strokeWidth = 1.dp.toPx(), cap = StrokeCap.Round,
                )
            }
            val width = 4.dp.toPx()
            val radius = outerRadius - 11.dp.toPx()
            val arcSize = Size(radius * 2, radius * 2)
            val origin = center - Offset(radius, radius)
            drawArc(colors.outlineVariant, 135f, 270f, false, origin, arcSize, style = Stroke(width, cap = StrokeCap.Round))
            drawArc(colors.primary, 135f, progress * 270f, false, origin, arcSize, style = Stroke(width, cap = StrokeCap.Round))
            val angle = Math.toRadians((135 + progress * 270).toDouble())
            val tip = center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * radius
            drawCircle(colors.primary, 4.dp.toPx(), tip)
            drawCircle(colors.onPrimary, 1.5.dp.toPx(), tip)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(score.toString(), fontSize = 32.sp, lineHeight = 36.sp, fontWeight = FontWeight.Medium,
                color = colors.primary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
            Text("今日", style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
internal fun SectionHeading(title: String, subtitle: String = "") {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
