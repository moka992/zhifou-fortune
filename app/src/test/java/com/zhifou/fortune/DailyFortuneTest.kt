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
        assertTrue(snapshot.reading.score in 42..94)
        assertFalse(snapshot.reading.score == 44)
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
        val emptyInsights = DailyInsightEngine.create(empty)
        val sensitiveInsights = DailyInsightEngine.create(sensitive)
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

    @Test
    fun personalDetailsDoNotRandomizeTheRhythmScore() {
        val date = LocalDate.of(2026, 10, 19)
        val anonymous = DailyFortuneEngine.create(date, "", "", "")
        val personal = DailyFortuneEngine.create(date, "新昵称", "1995-10-19", "工作、预算", "晴天")

        assertEquals(anonymous.reading.score, personal.reading.score)
        assertEquals(anonymous.rhythmBasis, personal.rhythmBasis)
        assertTrue(personal.reading.body.contains("生日节点"))
        assertTrue(personal.insights.first().summary.contains("工作"))
    }

    @Test
    fun duplicateAndSynonymousActivitiesDoNotInflateTheScore() {
        val base = blankAlmanac()
        val single = base.copy(suitable = listOf("开市", "交易"))
        val duplicate = base.copy(suitable = listOf("交易", "立券", "开工", "开市", "交易", " 开市 "))
        assertEquals(DailyTraditionEngine.rhythm(single), DailyTraditionEngine.rhythm(duplicate))
        assertEquals(DailyInsightEngine.signals(single).map { it.balance },
            DailyInsightEngine.signals(duplicate).map { it.balance })
    }

    @Test
    fun oppositeSignalsCancelAndTriggerReviewInsteadOfAuspiciousClaims() {
        val info = blankAlmanac().copy(suitable = listOf("交易"), avoid = listOf("立券"))
        val result = DailyTraditionEngine.rhythm(info)
        assertEquals(0, result.almanacAdjustment)
        assertEquals(-2, result.conflictAdjustment)
        assertEquals(62, result.score)
        assertTrue(DailyInsightEngine.create(info).all { it.tone == DailyInsightTone.Balanced })
        assertTrue(DailyInsightEngine.create(info).first().summary.contains("有交叉"))
    }

    @Test
    fun emptyUnknownOrFuneralOnlyAlmanacDoesNotInventSignals() {
        val base = blankAlmanac()
        val unsafe = base.copy(suitable = listOf("安葬", "出殡", "安葬交易", "未知项目"),
            avoid = listOf("入殓", "修坟", "交易出殡", "破屋"))
        assertEquals(64, DailyTraditionEngine.rhythm(base).score)
        assertEquals(DailyTraditionEngine.rhythm(base), DailyTraditionEngine.rhythm(unsafe))
        assertEquals(DailyInsightEngine.create(base), DailyInsightEngine.create(unsafe))
        assertTrue(DailyInsightEngine.safeSuitable(unsafe).isEmpty())
        assertTrue(DailyInsightEngine.safeAvoid(unsafe).isEmpty())
    }

    @Test
    fun restrictiveDaysCannotBePromotedByPositiveTerms() {
        val info = blankAlmanac().copy(suitable = listOf("诸事不宜", "开市", "会亲友", "纳财"),
            avoid = listOf("诸事不宜"))
        assertTrue(DailyInsightEngine.signals(info).all { it.balance < 0 })
        assertTrue(DailyInsightEngine.create(info).all { it.tone == DailyInsightTone.Cautious })
        assertTrue(DailyTraditionEngine.rhythm(info).score < 64)
        assertTrue(DailyInsightEngine.create(info).none { "诸事不宜" in it.summary })
    }

    @Test
    fun fivePhaseGenerationAndControlFormTheCorrectCycles() {
        FivePhase.entries.forEach { phase ->
            assertFalse(phase.generates(phase))
            assertFalse(phase.controls(phase))
            assertEquals(1, FivePhase.entries.count(phase::generates))
            assertEquals(1, FivePhase.entries.count(phase::controls))
        }
        assertTrue(FivePhase.Wood.generates(FivePhase.Fire))
        assertTrue(FivePhase.Fire.generates(FivePhase.Earth))
        assertTrue(FivePhase.Earth.generates(FivePhase.Metal))
        assertTrue(FivePhase.Metal.generates(FivePhase.Water))
        assertTrue(FivePhase.Water.generates(FivePhase.Wood))
        assertTrue(FivePhase.Wood.controls(FivePhase.Earth))
        assertTrue(FivePhase.Water.controls(FivePhase.Fire))
        assertEquals(FivePhase.Wood, FivePhase.ofStem('乙'))
        assertEquals(FivePhase.Earth, FivePhase.ofBranch('未'))
        assertNull(FivePhase.ofStem('子'))
        assertNull(FivePhase.ofBranch('甲'))
    }

    @Test
    fun allTwentyFiveSeasonalRelationsAreBoundedAndKnown() {
        val stems = listOf("甲", "丙", "戊", "庚", "壬")
        val branches = listOf("寅", "巳", "辰", "申", "亥")
        stems.forEach { stem -> branches.forEach { branch ->
            val context = DailyTraditionEngine.seasonalContext(blankAlmanac().copy(
                dayGanZhi = stem + "子", monthGanZhi = "甲" + branch))
            assertTrue(context.adjustment in -2..3)
            assertTrue(context.explanation.isNotBlank())
            assertTrue(context.day != null && context.month != null)
        } }
        assertEquals(3, DailyTraditionEngine.seasonalContext(blankAlmanac().copy(
            dayGanZhi = "丙子", monthGanZhi = "甲寅")).adjustment)
        assertEquals(-2, DailyTraditionEngine.seasonalContext(blankAlmanac().copy(
            dayGanZhi = "戊子", monthGanZhi = "甲寅")).adjustment)
    }

    @Test
    fun solarMonthChangesAtJieRatherThanGregorianOrLunarMonthStart() {
        assertEquals('丑', DailyFortuneEngine.almanac(LocalDate.of(2026, 2, 3)).monthGanZhi[1])
        assertEquals('寅', DailyFortuneEngine.almanac(LocalDate.of(2026, 2, 4)).monthGanZhi[1])
        assertEquals('寅', DailyFortuneEngine.almanac(LocalDate.of(2026, 2, 17)).monthGanZhi[1])
    }

    @Test
    fun scoreChangesWithEvidenceAndNotWithClashDirectionsOrMissingPermissions() {
        val base = blankAlmanac()
        val positive = base.copy(suitable = listOf("开市", "交易", "会亲友", "纳采", "纳财"))
        val negative = base.copy(avoid = positive.suitable)
        assertTrue(DailyTraditionEngine.rhythm(positive).score > DailyTraditionEngine.rhythm(base).score)
        assertTrue(DailyTraditionEngine.rhythm(negative).score < DailyTraditionEngine.rhythm(base).score)
        assertEquals(DailyTraditionEngine.rhythm(base), DailyTraditionEngine.rhythm(base.copy(
            clash = "冲鼠", shaDirection = "北", wealthDirection = "南")))
    }

    @Test
    fun fullYearRemainsStableSafeCompactAndGroundedInClassics() {
        val scores = mutableSetOf<Int>()
        repeat(365) { offset ->
            val date = LocalDate.of(2026, 1, 1).plusDays(offset.toLong())
            val snapshot = DailyFortuneEngine.create(date, "", "", "")
            assertTrue(snapshot.reading.score in 42..94)
            assertFalse(snapshot.reading.score == 44)
            scores += snapshot.reading.score
            assertTrue(snapshot.rhythmBasis.contains("基准 64"))
            assertTrue(snapshot.reading.body.contains("《尚书·洪范》"))
            assertTrue(snapshot.insights.all { it.headline.length <= 12 })
            assertTrue(snapshot.insights.all { "《道德经》" in it.summary && "现代参考：" in it.summary })
            assertTrue(snapshot.insights.none { insight ->
                listOf("安葬", "出殡", "破屋", "疾病", "必定", "灾祸", "诅咒").any { it in insight.summary }
            })
        }
        assertTrue(scores.size > 10)
    }

    @Test
    fun internalCalibrationAccountsForTheDisplayedScore() {
        val allTerms = listOf("开市", "交易", "入学", "会亲友", "嫁娶", "出行", "纳财", "置产")
        val info = blankAlmanac().copy(dayGanZhi = "戊子", monthGanZhi = "甲寅",
            suitable = listOf("交易", "会亲友", "纳财", "诸事不宜"), avoid = allTerms)
        val rhythm = DailyTraditionEngine.rhythm(info)
        assertEquals(42, rhythm.score)
        assertTrue(rhythm.calibrationAdjustment > 0)
        assertTrue(rhythm.basis.contains("内部校准"))
        assertEquals(rhythm.score, 64 + rhythm.almanacAdjustment + rhythm.seasonalAdjustment +
            rhythm.conflictAdjustment + rhythm.calibrationAdjustment)
    }

    @Test
    fun calibrationSpansTheScaleAndPreservesEvidenceOrder() {
        val base = blankAlmanac()
        val allTerms = listOf("开市", "交易", "入学", "修造", "会亲友", "嫁娶", "出行", "纳财", "置产")
        val favorable = base.copy(dayGanZhi = "丙子", monthGanZhi = "甲寅", suitable = allTerms)
        val cautious = base.copy(dayGanZhi = "戊子", monthGanZhi = "甲寅",
            suitable = listOf("交易", "会亲友", "纳财", "诸事不宜"), avoid = allTerms)
        assertEquals(94, DailyTraditionEngine.rhythm(favorable).score)
        assertEquals(42, DailyTraditionEngine.rhythm(cautious).score)

        val scenarios = listOf(emptyList(), listOf("交易"), listOf("会亲友"), listOf("纳财"), allTerms)
        val stems = listOf("甲", "丙", "戊", "庚", "壬", "")
        val branches = listOf("寅", "巳", "辰", "申", "亥", "")
        val results = stems.flatMap { stem -> branches.flatMap { branch ->
            scenarios.flatMap { suitable -> scenarios.map { avoid ->
                DailyTraditionEngine.rhythm(base.copy(dayGanZhi = stem + "子", monthGanZhi = "甲" + branch,
                    suitable = suitable, avoid = avoid))
            } }
        } }
        results.forEach { result ->
            assertTrue(result.score in 42..94)
            assertFalse(result.score == 44)
        }
        val ordered = results.groupBy { it.almanacAdjustment + it.seasonalAdjustment + it.conflictAdjustment }
            .toSortedMap().values.map { group ->
                assertEquals(1, group.map { it.score }.distinct().size)
                group.first().score
            }
        assertTrue(ordered.zipWithNext().all { (lower, upper) -> lower <= upper })
        assertTrue(ordered.distinct().size > 25)
    }

    @Test
    fun animatedValuesFollowTheSameDisplayPolicy() {
        val values = (0..100).map(::rhythmDisplayValue)
        assertFalse(44 in values)
        assertEquals(0, values.first())
        assertEquals(100, values.last())
        assertTrue(values.zipWithNext().all { (lower, upper) -> lower <= upper })
    }

    private fun blankAlmanac(): DailyAlmanacInfo = DailyFortuneEngine.almanac(LocalDate.of(2026, 7, 13))
        .copy(suitable = emptyList(), avoid = emptyList(), dayGanZhi = "", monthGanZhi = "")
}
