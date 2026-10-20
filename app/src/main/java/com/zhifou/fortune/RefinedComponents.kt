package com.zhifou.fortune

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.roundToInt

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
    val shortWide = LocalFortuneLayout.current.shortWide
    val railScroll = rememberScrollState()
    if (shortWide) {
        val density = LocalDensity.current
        val labelStyle = MaterialTheme.typography.labelMedium
        val measurer = rememberTextMeasurer()
        val widestLabel = with(density) {
            Tab.entries.maxOf { measurer.measure(AnnotatedString(it.label), labelStyle).size.width }.toDp()
        }
        Column(
            Modifier.width(landscapeRailWidth(widestLabel.value).dp).fillMaxHeight()
                .background(LocalFortunePalette.current.panel)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
                .verticalScroll(railScroll).padding(horizontal = 4.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            Tab.entries.forEach { destination ->
                val selected = selectedTab == destination
                val interaction = remember(destination) { MutableInteractionSource() }
                val fill by animateColorAsState(
                    colors.primaryContainer.copy(alpha = if (selected) 1f else 0f),
                    fortuneEffectsSpec(), label = "compact-rail-selection")
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(fill)
                        .selectable(selected, role = Role.Tab, interactionSource = interaction,
                            indication = null, onClick = { onTabSelected(destination) })
                        .fortunePress(interaction)
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FortuneIcon(destination.icon, null, active = selected, interactionSource = interaction,
                        tint = if (selected) colors.primary else colors.onSurfaceVariant,
                        modifier = Modifier.size(22.dp))
                    Text(destination.label, style = labelStyle,
                        color = if (selected) colors.primary else colors.onSurfaceVariant, maxLines = 1,
                        softWrap = false)
                }
            }
        }
        return
    }
    Column(
        Modifier.width(80.dp).fillMaxHeight()
            .background(LocalFortunePalette.current.panel)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.union(WindowInsets.mandatorySystemGestures)
                    .only(WindowInsetsSides.Vertical),
            )
            .padding(vertical = 4.dp, horizontal = 4.dp),
    ) {
        Tab.entries.forEach { destination ->
            val selected = selectedTab == destination
            val oracle = destination == Tab.Oracle
            val interaction = remember(destination) { MutableInteractionSource() }
            val fill by animateColorAsState(
                if (oracle) colors.primary else colors.primaryContainer.copy(alpha = if (selected) 1f else 0f),
                fortuneEffectsSpec(), label = "rail-selection")
            Column(
                    (if (compactHeight) Modifier.weight(1f) else Modifier.height(80.dp))
                        .fillMaxWidth().clip(MaterialTheme.shapes.medium)
                    .selectable(selected = selected, role = Role.Tab, interactionSource = interaction,
                        indication = null, onClick = { onTabSelected(destination) })
                    .fortunePress(interaction),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    color = fill,
                    shape = if (oracle) CircleShape else MaterialTheme.shapes.small,
                    modifier = Modifier.size(width = if (oracle) 40.dp else 44.dp, height = if (oracle) 40.dp else 32.dp),
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        FortuneIcon(destination.icon, null, active = selected, interactionSource = interaction,
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
    val shortWide = LocalFortuneLayout.current.shortWide
    val rhythm = when {
        reading.score >= 72 -> "顺势向前"
        reading.score >= 62 -> "稳步展开"
        else -> "放慢一点"
    }
    Column(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(if (shortWide) 10.dp else 16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("今日节奏", color = colors.primary, style = MaterialTheme.typography.labelLarge)
                Text(rhythm, style = if (shortWide) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineLarge)
                Text(snapshot.almanac.lunarLabel, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            RhythmDial(reading.score, Modifier.size(rhythmDialDiameter(shortWide, compact,
                androidx.compose.ui.platform.LocalDensity.current.fontScale).dp))
        }
        Text(reading.body, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
            Box(Modifier.width(3.dp).height(38.dp).background(colors.primary, CircleShape))
            Text(reading.advice.removePrefix("今日建议："), style = MaterialTheme.typography.bodyMedium, color = colors.onSurface)
        }
    }
}

@Composable
private fun RhythmDial(score: Int, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    var revealed by rememberSaveable(score) { mutableStateOf(false) }
    LaunchedEffect(score) { revealed = true }
    val progress = animateFloatAsState(
        if (revealed) score.coerceIn(0, 100) / 100f else 0f,
        if (LocalReducedFortuneMotion.current) snap() else tween(850), label = "daily-rhythm",
    )
    Box(modifier.semantics { contentDescription = "今日节奏仪表盘" }, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().padding(2.dp)) {
            val value = progress.value
            val outerRadius = size.minDimension / 2f - 1.dp.toPx()
            repeat(41) { index ->
                val angle = Math.toRadians(135.0 + index * 270.0 / 40)
                val direction = Offset(cos(angle).toFloat(), sin(angle).toFloat())
                val tickLength = if (index % 5 == 0) 5.dp.toPx() else 2.5.dp.toPx()
                drawLine(
                    if (index / 40f <= value) colors.primary.copy(alpha = 0.72f) else colors.outlineVariant,
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
            drawArc(colors.primary, 135f, value * 270f, false, origin, arcSize, style = Stroke(width, cap = StrokeCap.Round))
            val angle = Math.toRadians((135 + value * 270).toDouble())
            val tip = center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * radius
            drawCircle(colors.primary, 4.dp.toPx(), tip)
            drawCircle(colors.onPrimary, 1.5.dp.toPx(), tip)
        }
        RhythmDialValue(score, progress, Modifier.align(Alignment.Center).offset(y = (-2).dp))
        Text("今日", style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp))
    }
}

@Composable
private fun RhythmDialValue(score: Int, progress: State<Float>, modifier: Modifier) {
    val shortWide = LocalFortuneLayout.current.shortWide
    val displayed by remember(score, progress) {
        derivedStateOf { rhythmDisplayValue((progress.value * 100f).roundToInt().coerceIn(0, score.coerceIn(0, 100))) }
    }
    Text(displayed.toString(), fontSize = if (shortWide) 24.sp else 32.sp,
        lineHeight = if (shortWide) 28.sp else 36.sp, fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        maxLines = 1, softWrap = false,
        modifier = modifier.clearAndSetSemantics { contentDescription = "今日节奏 $score" })
}

@Composable
internal fun SectionHeading(title: String, subtitle: String = "") {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
