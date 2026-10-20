package com.zhifou.fortune

import kotlin.math.abs
import org.junit.Assert.*
import org.junit.Test

class CoinSceneLayoutTest {
    @Test fun allCountsHaveNonOverlappingCoins() {
        for (count in 1..10) {
            val poses = List(count) { coinScenePose(it, count, 0f, false) }
            for (a in poses.indices) for (b in a + 1 until poses.size) {
                assertTrue(abs(poses[a].x - poses[b].x) > 2f || abs(poses[a].y - poses[b].y) > 2f)
            }
        }
    }
    @Test fun finalOrientationMatchesBothFacesWithoutTilt() {
        for (count in 1..10) for (index in 0 until count) for (character in listOf(true, false)) {
            val target = coinTargetRotation(18f, character, 6)
            assertEquals(character, isCharacterCoinSideVisible(target))
            assertEquals(0f, coinScenePose(index, count, target, false).tilt)
        }
    }
    @Test fun incompleteLastRowIsCentered() {
        for (count in 6..10) {
            val lastRow = (5 until count).map { coinScenePose(it, count, 0f, false) }
            assertEquals(0f, lastRow.sumOf { it.x.toDouble() }.toFloat(), .0001f)
        }
    }
}
