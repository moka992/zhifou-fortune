package com.zhifou.fortune

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import org.junit.Assert.assertEquals
import org.junit.Test

class OraclePagerGesturesTest {
    private val drag = Offset(120f, -90f)
    private val fling = Velocity(-900f, 500f)

    private fun OraclePagerGestureGate.start(x: Float, left: Float = 24f, right: Float = 24f, width: Float = 400f) =
        begin(x, width, minimumEdge = 32f, systemLeft = left, systemRight = right, clearance = 8f)

    @Test
    fun `inspiration is the left page and messages the right page`() {
        assertEquals(0, ORACLE_INSPIRATION_PAGE)
        assertEquals(1, ORACLE_MESSAGES_PAGE)
        assertEquals(2, ORACLE_PAGE_COUNT)
    }

    @Test
    fun `both physical edges block horizontal paging but preserve vertical movement`() {
        val gate = OraclePagerGestureGate()
        for (x in listOf(0f, 20f, 31f, 368f, 390f, 400f)) {
            gate.start(x)
            assertEquals(Offset(120f, 0f), gate.consumeScroll(drag))
            assertEquals(Velocity(-900f, 0f), gate.consumeFling(fling))
        }
    }

    @Test
    fun `central starts leave native paging and flinging untouched`() {
        val gate = OraclePagerGestureGate()
        for (x in listOf(32f, 100f, 200f, 367f)) {
            gate.start(x)
            assertEquals(Offset.Zero, gate.consumeScroll(drag))
            assertEquals(Velocity.Zero, gate.consumeFling(fling))
        }
    }

    @Test
    fun `wider system gesture insets get additional clearance on the correct physical side`() {
        val gate = OraclePagerGestureGate()
        gate.start(67f, left = 60f, right = 40f)
        assertEquals(Offset(120f, 0f), gate.consumeScroll(drag))
        gate.start(68f, left = 60f, right = 40f)
        assertEquals(Offset.Zero, gate.consumeScroll(drag))
        gate.start(352f, left = 60f, right = 40f)
        assertEquals(Offset(120f, 0f), gate.consumeScroll(drag))
        gate.start(351f, left = 60f, right = 40f)
        assertEquals(Offset.Zero, gate.consumeScroll(drag))
    }

    @Test
    fun `minimum margin is retained with three button navigation or no reported insets`() {
        val gate = OraclePagerGestureGate()
        gate.start(31f, left = 0f, right = 0f)
        assertEquals(Offset(120f, 0f), gate.consumeScroll(drag))
        gate.start(32f, left = 0f, right = 0f)
        assertEquals(Offset.Zero, gate.consumeScroll(drag))
    }

    @Test
    fun `gesture origin remains authoritative through motion and release`() {
        val gate = OraclePagerGestureGate()
        gate.start(20f)
        repeat(3) { assertEquals(Offset(120f, 0f), gate.consumeScroll(drag)) }
        assertEquals(Velocity(-900f, 0f), gate.consumeFling(fling))
        gate.start(200f)
        repeat(3) { assertEquals(Offset.Zero, gate.consumeScroll(drag)) }
        assertEquals(Velocity.Zero, gate.consumeFling(fling))
    }

    @Test
    fun `window coordinates do not create another dead strip at an inset page boundary`() {
        val gate = OraclePagerGestureGate()
        // A tablet rail can put the page's left edge at x=96 within the window.
        gate.start(100f)
        assertEquals(Offset.Zero, gate.consumeScroll(drag))
    }

    @Test
    fun `invalid or not yet measured coordinates cannot accidentally start paging`() {
        val gate = OraclePagerGestureGate()
        gate.start(Float.NaN)
        assertEquals(Offset(120f, 0f), gate.consumeScroll(drag))
        gate.start(100f, width = 0f)
        assertEquals(Offset(120f, 0f), gate.consumeScroll(drag))
        gate.start(100f, width = Float.POSITIVE_INFINITY)
        assertEquals(Offset(120f, 0f), gate.consumeScroll(drag))
    }
}
