package com.zhifou.fortune

import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlin.math.abs

private data class PagerTouch(val dragging: Boolean, val moving: Boolean, val position: Float, val settled: Int)

/** Observe the existing pager: no new touch region, Back exclusion or programmatic-scroll feedback. */
@Composable
internal fun HapticPagerFeedback(pager: PagerState) {
    val haptics = LocalAppHaptics.current
    val dragging by pager.interactionSource.collectIsDraggedAsState()
    val track = remember(pager) { HapticGestureTrack(activation = .5f) }
    LaunchedEffect(pager, haptics) {
        var active = false
        var origin = pager.settledPage
        snapshotFlow { PagerTouch(dragging, pager.isScrollInProgress,
            pager.currentPage + pager.currentPageOffsetFraction, pager.settledPage) }.collect { touch ->
            if (touch.dragging && !active) {
                active = true
                origin = touch.settled
                track.reset()
            }
            if (active && touch.dragging) haptics.gestureProgress(track, abs(touch.position - origin))
            if (active && !touch.dragging && !touch.moving) {
                haptics.gestureFinished(track, touch.settled != origin)
                active = false
            }
        }
    }
}
