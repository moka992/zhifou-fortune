package com.zhifou.fortune

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OracleDailySuggestionsTest {
    @Test
    fun suggestionsAreDeterministicDistinctAndDrivenByDailyAlmanac() {
        val snapshot = DailyFortuneEngine.create(
            date = LocalDate.of(2026, 7, 14),
            nickname = "",
            birthDateText = "",
            keywordsText = "",
        )

        val first = dailyOracleSuggestions(snapshot)
        val second = dailyOracleSuggestions(snapshot)

        assertEquals(first, second)
        assertEquals(4, first.size)
        assertEquals(4, first.distinct().size)
        assertTrue(first.any { "积压" in it || "专注" in it || "优先" in it })
        assertTrue(first.any { "暂缓" in it || "观察" in it || "避免" in it })
        assertFalse(first.any { "今日宜“" in it || "今日忌“" in it })
    }

    @Test
    fun suggestionsChangeWithDateAndAlmanac() {
        val first = dailyOracleSuggestions(
            DailyFortuneEngine.create(LocalDate.of(2026, 7, 13), "", "", "")
        )
        val second = dailyOracleSuggestions(
            DailyFortuneEngine.create(LocalDate.of(2026, 7, 14), "", "", "")
        )

        assertNotEquals(first, second)
    }

    @Test
    fun promptGateOnlyOpensOncePerCalendarDay() {
        val today = LocalDate.of(2026, 7, 14)

        assertTrue(shouldShowDailyOraclePrompt("2026-07-13", today))
        assertFalse(shouldShowDailyOraclePrompt("2026-07-14", today))
        assertTrue(shouldShowDailyOraclePrompt("", today))
    }

    @Test
    fun pageGestureRequiresAnIntentionalUpwardDrag() {
        assertFalse(isUpwardOraclePageSwipe(totalDragY = -40f, thresholdPx = 72f))
        assertTrue(isUpwardOraclePageSwipe(totalDragY = -72f, thresholdPx = 72f))
        assertFalse(isUpwardOraclePageSwipe(totalDragY = 120f, thresholdPx = 72f))
    }

    @Test
    fun returningFromDailyPromptUsesTheOppositeDirection() {
        assertFalse(isDownwardOraclePageSwipe(totalDragY = 40f, thresholdPx = 72f))
        assertTrue(isDownwardOraclePageSwipe(totalDragY = 72f, thresholdPx = 72f))
        assertFalse(isDownwardOraclePageSwipe(totalDragY = -120f, thresholdPx = 72f))
    }

    @Test
    fun sensitiveAndUnknownAlmanacActivitiesAreNeverUsedInRecommendations() {
        val blocked = listOf(
            "安葬", "祭祀", "祈福", "求嗣", "治病", "针灸", "词讼",
            "入殓", "移柩", "启钻", "破土", "开生坟", "造庙", "未知事项",
        )

        repeat(100) { seed ->
            val suitable = safeSuitableOracleQuestion(blocked, seed)
            val avoid = safeAvoidOracleQuestion(blocked, seed)
            val questions = listOf(suitable, avoid)

            assertFalse(questions.any { question -> blocked.any(question::contains) })
            assertFalse(questions.any { "今日宜“" in it || "今日忌“" in it })
        }
    }

    @Test
    fun approvedActivitiesUseReviewedEverydayLanguage() {
        assertEquals(
            "今天出行或外出办事应注意什么？",
            safeSuitableOracleQuestion(listOf("安葬", "出行"), 0),
        )
        assertEquals(
            "今天在合约和财务上应避免什么？",
            safeAvoidOracleQuestion(listOf("祭祀", "交易"), 0),
        )
    }
}
