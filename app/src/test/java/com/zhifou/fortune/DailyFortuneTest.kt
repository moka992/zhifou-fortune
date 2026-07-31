package com.zhifou.fortune

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyFortuneTest {
    @Test
    fun standardAlmanacDirectionsMatchReferenceDate() {
        val info = DailyFortuneEngine.almanac(LocalDate.of(2026, 7, 13))

        assertEquals("东南", info.joyDirection)
        assertEquals("东北", info.fortuneDirection)
        assertEquals("正北", info.wealthDirection)
        assertEquals("东北", info.yangNobleDirection)
        assertEquals("西南", info.yinNobleDirection)
        assertTrue(info.suitable.isNotEmpty())
        assertTrue(info.avoid.isNotEmpty())
    }

    @Test
    fun noProfilePermissionNetworkOrEnvironmentIsRequired() {
        val snapshot = DailyFortuneEngine.create(
            date = LocalDate.of(2026, 7, 13),
            nickname = "",
            birthDateText = "",
            keywordsText = "",
            environmentSummary = null,
        )

        assertEquals("今日运势", snapshot.reading.title)
        assertEquals(listOf("日期与离线黄历"), snapshot.personalizationBasis)
        assertNull(snapshot.environmentSummary)
        assertTrue(snapshot.reading.body.isNotBlank())
        assertTrue(snapshot.reading.advice.isNotBlank())
        assertTrue(snapshot.reading.score in 52..80)
    }

    @Test
    fun sameInputsProduceTheSameDailyFortune() {
        val date = LocalDate.of(2026, 7, 13)
        val first = DailyFortuneEngine.create(date, "小知", "1995-04-18", "工作、健康")
        val second = DailyFortuneEngine.create(date, "小知", "1995-04-18", "工作、健康")

        assertEquals(first.reading, second.reading)
        assertEquals(first.almanac, second.almanac)
        assertEquals(listOf("日期与离线黄历", "昵称", "生日", "关键词"), first.personalizationBasis)
        assertTrue(first.reading.body.contains("关注词"))
        assertEquals(first.insights, second.insights)
        assertEquals(DailyInsightCategory.entries.toSet(), first.insights.map { it.category }.toSet())
        assertTrue(first.insights.all { it.headline.length <= 12 })
        assertTrue(first.insights.none { insight ->
            listOf("安葬", "破屋", "坏垣", "诸事不宜").any { it in insight.headline || it in insight.summary }
        })
    }

    @Test
    fun offlineInsightHeadlinesVaryAcrossDatesButRemainCompact() {
        val headlines = (0L..20L).map { offset ->
            DailyFortuneEngine.create(
                LocalDate.of(2026, 7, 13).plusDays(offset),
                "",
                "",
                "",
            ).insights.map { it.headline }
        }

        assertTrue(headlines.distinct().size > 3)
        assertTrue(headlines.flatten().all { it.length <= 12 })
    }

    @Test
    fun emptyOrSensitiveAlmanacTermsProduceSafeModernConclusions() {
        val base = DailyFortuneEngine.almanac(LocalDate.of(2026, 8, 1))
        val empty = base.copy(suitable = emptyList(), avoid = emptyList())
        val sensitive = base.copy(
            suitable = listOf("诸事不宜", "安葬", "出殡"),
            avoid = listOf("馀事勿取", "破屋", "坏垣", "入殓"),
        )
        val emptyInsights = DailyInsightEngine.create(empty, "empty")
        val sensitiveInsights = DailyInsightEngine.create(sensitive, "sensitive")
        val excluded = listOf(
            "诸事不宜", "馀事勿取", "安葬", "出殡", "破屋", "坏垣", "入殓",
        )

        assertEquals(3, emptyInsights.size)
        assertEquals(3, sensitiveInsights.size)
        assertTrue(sensitiveInsights.all { it.tone == DailyInsightTone.Cautious })
        assertTrue((emptyInsights + sensitiveInsights).none { insight ->
            excluded.any { it in insight.headline || it in insight.summary }
        })
        assertTrue(DailyInsightEngine.safeSuitable(sensitive).isEmpty())
        assertTrue(DailyInsightEngine.safeAvoid(sensitive).isEmpty())
    }

    @Test
    fun invalidOrFutureBirthdayDoesNotBlockGeneration() {
        val today = LocalDate.of(2026, 7, 13)

        assertNull(DailyFortuneEngine.parseBirthDate("不是日期", today))
        assertNull(DailyFortuneEngine.parseBirthDate("2030-01-01", today))
        assertEquals(LocalDate.of(1995, 4, 18), DailyFortuneEngine.parseBirthDate("1995-04-18", today))
        assertEquals(LocalDate.of(2019, 10, 1), DailyFortuneEngine.parseBirthDate("2019.10.1", today))
        assertEquals(LocalDate.of(2019, 10, 1), DailyFortuneEngine.parseBirthDate("2019年10月1日", today))

        val snapshot = DailyFortuneEngine.create(today, "用户", "2030-01-01", "学习")
        assertFalse("生日" in snapshot.personalizationBasis)
        assertTrue("关键词" in snapshot.personalizationBasis)
    }
}
