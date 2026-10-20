package com.zhifou.fortune

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect

@Composable
internal fun FortuneIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    active: Boolean = true,
    replayKey: Any? = Unit,
    interactionSource: MutableInteractionSource? = null,
) {
    val kind = remember(imageVector) { semanticIconKind(imageVector.name) }
    if (kind == SemanticIconKind.None) {
        Icon(imageVector, contentDescription, modifier, tint)
        return
    }
    val reduced = LocalReducedFortuneMotion.current
    val visible = LocalFortuneMotionVisible.current
    val animate = shouldAnimateSemanticIcon(kind, active, reduced, visible)
    val phase = remember(imageVector) { Animatable(1f) }
    val requests = remember(imageVector) { Channel<Unit>(Channel.CONFLATED) }
    var played by remember(imageVector) { mutableStateOf(false) }
    var lastKey by remember(imageVector) { mutableStateOf(replayKey) }
    val spec = remember { tween<Float>(SEMANTIC_ICON_DURATION_MS, easing = LinearEasing) }

    LaunchedEffect(imageVector, animate, reduced, visible) {
        if (!animate) {
            while (requests.tryReceive().isSuccess) { /* Discard inactive cues. */ }
            if (!reduced && visible && kind != SemanticIconKind.Send && phase.value < ICON_PART_REST_START) {
                phase.animateTo(1f, tween(120, easing = LinearEasing))
            }
            phase.snapTo(1f)
        } else {
            for (request in requests) {
                if (phase.value > 0f && phase.value < ICON_PART_REST_START) {
                    phase.animateTo(1f, tween(120, easing = LinearEasing))
                }
                phase.snapTo(0f)
                phase.animateTo(1f, spec)
                // Rapid clicks must not interrupt the current pose or leave a playback backlog.
                while (requests.tryReceive().isSuccess) { }
            }
        }
    }
    LaunchedEffect(imageVector, active, reduced, visible, replayKey) {
        if (!active) played = false
        if (animate && (!played || lastKey != replayKey)) {
            lastKey = replayKey
            played = true
            requests.trySend(Unit)
        }
    }
    LaunchedEffect(interactionSource, animate) {
        if (animate) interactionSource?.interactions?.collect { interaction ->
            if (interaction is PressInteraction.Release && !phase.isRunning) requests.trySend(Unit)
        }
    }
    Icon(rememberSemanticIconPainter(imageVector, phase.asState()), contentDescription, modifier, tint)
}
