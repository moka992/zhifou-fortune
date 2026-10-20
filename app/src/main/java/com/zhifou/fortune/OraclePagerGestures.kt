package com.zhifou.fortune

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.max

internal const val ORACLE_INSPIRATION_PAGE = 0
internal const val ORACLE_MESSAGES_PAGE = 1
internal const val ORACLE_PAGE_COUNT = 2

internal class OraclePagerGestureGate {
    private var edgeStart = true

    fun begin(
        windowX: Float,
        windowWidth: Float,
        minimumEdge: Float,
        systemLeft: Float,
        systemRight: Float,
        clearance: Float,
    ) {
        val left = max(minimumEdge, systemLeft + clearance)
        val right = max(minimumEdge, systemRight + clearance)
        edgeStart = !windowX.isFinite() || !windowWidth.isFinite() || windowWidth <= 0f ||
            windowX < left || windowX >= windowWidth - right
    }

    fun consumeScroll(available: Offset): Offset =
        if (edgeStart) Offset(available.x, 0f) else Offset.Zero

    fun consumeFling(available: Velocity): Velocity =
        if (edgeStart) Velocity(available.x, 0f) else Velocity.Zero
}

private class OraclePagerCoordinates {
    var value: LayoutCoordinates? = null
}

@Composable
internal fun oraclePagerGestureRegion(): Modifier {
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current
    val view = LocalView.current
    val gestures = WindowInsets.systemGestures
    val minimumEdge = with(density) { 32.dp.toPx() }
    val clearance = with(density) { 8.dp.toPx() }
    val left = gestures.getLeft(density, direction).toFloat()
    val right = gestures.getRight(density, direction).toFloat()
    val gate = remember { OraclePagerGestureGate() }
    val coordinates = remember { OraclePagerCoordinates() }
    val connection = remember(gate) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
                if (source == NestedScrollSource.UserInput) gate.consumeScroll(available) else Offset.Zero

            override suspend fun onPreFling(available: Velocity): Velocity = gate.consumeFling(available)
        }
    }

    return Modifier
        .onGloballyPositioned { coordinates.value = it }
        .pointerInput(view, minimumEdge, clearance, left, right) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                val layout = coordinates.value
                val windowX = if (layout?.isAttached == true) layout.localToWindow(down.position).x else Float.NaN
                // Observe the initial down without consuming taps, vertical scrolling or system Back.
                gate.begin(windowX, view.rootView.width.toFloat(), minimumEdge, left, right, clearance)
            }
        }
        .nestedScroll(connection)
}
