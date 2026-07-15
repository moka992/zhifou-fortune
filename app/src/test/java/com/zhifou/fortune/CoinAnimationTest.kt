package com.zhifou.fortune

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoinAnimationTest {
    @Test
    fun visibleFaceAlternatesEveryHalfTurn() {
        assertFalse(isCharacterCoinSideVisible(0f))
        assertTrue(isCharacterCoinSideVisible(100f))
        assertTrue(isCharacterCoinSideVisible(180f))
        assertFalse(isCharacterCoinSideVisible(280f))
        assertFalse(isCharacterCoinSideVisible(360f))
    }

    @Test
    fun targetRotationAlwaysSettlesOnRequestedFace() {
        val flowerFromCharacter = coinTargetRotation(180f, characterSide = false, fullTurns = 5)
        val characterFromFlower = coinTargetRotation(0f, characterSide = true, fullTurns = 6)
        val characterFromArbitrary = coinTargetRotation(73f, characterSide = true, fullTurns = 5)

        assertEquals(0f, normalized(flowerFromCharacter), 0.001f)
        assertEquals(180f, normalized(characterFromFlower), 0.001f)
        assertEquals(180f, normalized(characterFromArbitrary), 0.001f)
    }

    private fun normalized(value: Float): Float = ((value % 360f) + 360f) % 360f
}
