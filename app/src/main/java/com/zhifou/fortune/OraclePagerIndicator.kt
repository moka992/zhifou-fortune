package com.zhifou.fortune

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs

internal const val ORACLE_INDICATOR_IDLE_MS = 1_200L

internal fun oraclePageEmphasis(position: Float, page: Int): Float {
    val safe = if (position.isFinite()) position.coerceIn(0f, 1f) else 0f
    return (1f - abs(safe - page)).coerceIn(0f, 1f)
}

/** A transient overlay: no reserved row, touch target, timer loop, or change to pager gestures. */
@Composable
internal fun OraclePagerIndicator(
    state: PagerState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var visible by remember(state) { mutableStateOf(false) }
    LaunchedEffect(state, enabled) {
        if (!enabled) {
            visible = false
            return@LaunchedEffect
        }
        snapshotFlow { state.isScrollInProgress }.collectLatest { moving ->
            if (moving) visible = true
            else if (visible) {
                delay(ORACLE_INDICATOR_IDLE_MS)
                visible = false
            }
        }
    }
    val effects = fortuneEffectsSpec<Float>()
    val colors = MaterialTheme.colorScheme
    val short = LocalFortuneLayout.current.shortWide
    AnimatedVisibility(visible, modifier, enter = fadeIn(effects), exit = fadeOut(effects)) {
        Canvas(Modifier.width(48.dp).height(14.dp).semantics {
            contentDescription = if (state.settledPage == ORACLE_INSPIRATION_PAGE)
                "今日灵感，第1页，共2页" else "消息，第2页，共2页"
        }) {
            val position = state.currentPage + state.currentPageOffsetFraction
            val small = 4.dp.toPx()
            val growth = 12.dp.toPx()
            val gap = 6.dp.toPx()
            val height = (if (short) 3.dp else 4.dp).toPx()
            var x = (size.width - small * 2f - growth - gap) / 2f
            repeat(ORACLE_PAGE_COUNT) { page ->
                val emphasis = oraclePageEmphasis(position, page)
                val width = small + growth * emphasis
                drawRoundRect(
                    color = lerp(colors.outlineVariant, colors.primary, emphasis),
                    topLeft = Offset(x, (size.height - height) / 2f), size = Size(width, height),
                    cornerRadius = CornerRadius(height / 2f),
                )
                x += width + gap
            }
        }
    }
}
