package com.zhifou.fortune

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OraclePagerIndicatorTest {
    @Test fun endpointsAndHalfwayMatchTheTwoPages() {
        assertEquals(1f, oraclePageEmphasis(0f, 0), 0f)
        assertEquals(0f, oraclePageEmphasis(0f, 1), 0f)
        assertEquals(0f, oraclePageEmphasis(1f, 0), 0f)
        assertEquals(1f, oraclePageEmphasis(1f, 1), 0f)
        assertEquals(.5f, oraclePageEmphasis(.5f, 0), 0f)
        assertEquals(.5f, oraclePageEmphasis(.5f, 1), 0f)
    }

    @Test fun dragProgressMaintainsConstantCombinedWidth() {
        (-10..110).forEach { step ->
            val position = step / 100f
            val left = oraclePageEmphasis(position, 0)
            val right = oraclePageEmphasis(position, 1)
            assertEquals(1f, left + right, .00001f)
            assertTrue(left in 0f..1f && right in 0f..1f)
        }
    }

    @Test fun invalidProgressCannotBreakTheDrawing() {
        listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY).forEach {
            assertEquals(1f, oraclePageEmphasis(it, 0), 0f)
            assertEquals(0f, oraclePageEmphasis(it, 1), 0f)
        }
        assertTrue(ORACLE_INDICATOR_IDLE_MS in 1_000L..2_000L)
    }
}
