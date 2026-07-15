package com.zhifou.fortune

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OracleTimelineStateTest {
    private val resting = OracleFollowSnapshot(
        timelineSize = 5,
        chatSending = false,
        coinCasting = false,
        coinLineCount = 0,
    )

    @Test
    fun returningToThePageDoesNotForceAJump() {
        assertFalse(shouldFollowOracleContent(resting, resting.copy()))
    }

    @Test
    fun newlyAddedContentStillFollowsTheConversation() {
        assertTrue(shouldFollowOracleContent(resting, resting.copy(timelineSize = 6)))
        assertTrue(shouldFollowOracleContent(resting, resting.copy(chatSending = true)))
        assertTrue(shouldFollowOracleContent(resting, resting.copy(coinCasting = true)))
        assertTrue(shouldFollowOracleContent(resting, resting.copy(coinLineCount = 1)))
    }

    @Test
    fun deletingContentDoesNotForceAJump() {
        assertFalse(shouldFollowOracleContent(resting, resting.copy(timelineSize = 4)))
    }
}
