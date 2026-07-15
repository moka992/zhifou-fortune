package com.zhifou.fortune

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlmanacDayDetailTest {
    @Test
    fun referenceDateContainsCompleteOfflineAlmanacData() {
        val detail = AlmanacDayDetailEngine.create(LocalDate.of(2026, 7, 14))

        assertEquals("二", detail.weekday)
        assertEquals("巨蟹", detail.constellation)
        assertEquals("六月", detail.lunarMonth)
        assertEquals("初一", detail.lunarDay)
        assertEquals(
            listOf(
                AlmanacPillar("年", "丙午", "马", "天河水"),
                AlmanacPillar("月", "乙未", "羊", "沙中金"),
                AlmanacPillar("日", "己丑", "牛", "霹雳火"),
            ),
            detail.pillars,
        )
        assertEquals("土", detail.dayStemElement)
        assertEquals("破", detail.dayOfficer)
        assertEquals("朱雀", detail.dayGod)
        assertEquals("黑道", detail.dayGodType)
        assertEquals("凶", detail.dayGodLuck)
        assertEquals(listOf("破屋", "坏垣", "馀事勿取"), detail.suitable)
        assertEquals(listOf("诸事不宜"), detail.avoid)
        assertTrue(detail.previousSolarTerm.startsWith("小暑"))
        assertTrue(detail.nextSolarTerm.startsWith("大暑"))
    }

    @Test
    fun referenceDateDirectionsVoidsAndAnnualCustomsMatchRules() {
        val detail = AlmanacDayDetailEngine.create(LocalDate.of(2026, 7, 14))

        assertEquals("东北", detail.joyDirection)
        assertEquals("正北", detail.fortuneDirection)
        assertEquals("正北", detail.wealthDirection)
        assertEquals("正北", detail.yangNobleDirection)
        assertEquals("西南", detail.yinNobleDirection)
        assertEquals("寅卯", detail.yearVoid)
        assertEquals("辰巳", detail.monthVoid)
        assertEquals("午未", detail.dayVoid)
        assertEquals("二黑土", detail.nineStar)
        assertEquals(
            listOf("四牛耕田", "五人分饼", "七龙治水", "十日得金"),
            detail.annualFolkCustoms,
        )
    }
}
