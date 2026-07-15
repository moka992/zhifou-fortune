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
