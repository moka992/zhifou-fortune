package com.zhifou.fortune

import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import kotlin.math.abs

@Composable
internal fun hapticAction(cue: HapticCue = HapticCue.Selection, action: () -> Unit): () -> Unit {
    val haptics = LocalAppHaptics.current
    return { haptics.scene(cue); action() }
}

private data class WheelTouch(val dragging: Boolean, val moving: Boolean, val centerIndex: Int?)

/** Only deliberate picker drags and their fling: initial positioning and normal list scrolling stay silent. */
@Composable
internal fun HapticWheelFeedback(state: LazyListState) {
    val haptics = LocalAppHaptics.current
    val dragging by state.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(state, haptics) {
        var tracking = false
        var last: Int? = null
        var changed = false
        snapshotFlow {
            val layout = state.layoutInfo
            val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
            WheelTouch(dragging, state.isScrollInProgress,
                layout.visibleItemsInfo.minByOrNull { abs(it.offset + it.size / 2 - center) }?.index)
        }.collect { touch ->
            if (touch.dragging && !tracking) {
                tracking = true
                last = touch.centerIndex
                changed = false
            }
            if (tracking && touch.centerIndex != null && touch.centerIndex != last) {
                haptics.scene(HapticCue.FineSelection)
                last = touch.centerIndex
                changed = true
            }
            if (tracking && !touch.dragging && !touch.moving) {
                if (changed) haptics.scene(HapticCue.PageSnap)
                tracking = false
            }
        }
    }
}

private data class DeckTouch(val dragging: Boolean, val moving: Boolean, val position: Float)

/** Observe actual list offsets, including a user fling, without intercepting card taps or Back. */
@Composable
internal fun HapticTarotDeckFeedback(state: LazyListState, stridePx: Float, enabled: Boolean) {
    val haptics = LocalAppHaptics.current
    val dragging by state.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(state, haptics, stridePx, enabled) {
        if (!enabled || !stridePx.isFinite() || stridePx <= 0f) return@LaunchedEffect
        val track = TarotDeckHapticTrack()
        snapshotFlow {
            val layout = state.layoutInfo
            val item = layout.visibleItemsInfo.firstOrNull()
            val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2f
            DeckTouch(dragging, state.isScrollInProgress,
                if (item == null) Float.NaN else TarotDeckHapticTrack.position(item.index,
                    item.offset + item.size / 2f, center, stridePx))
        }.collect { touch ->
            track.advance(touch.position, android.os.SystemClock.uptimeMillis(), touch.dragging, touch.moving)
                ?.let { haptics.cardBrowse(it) }
        }
    }
}
