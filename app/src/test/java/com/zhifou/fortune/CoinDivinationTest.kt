package com.zhifou.fortune

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoinDivinationTest {
    @Test
    fun allThreeCoinCombinationsFollowOneThreeThreeOneDistribution() {
        val lines = buildList {
            for (first in listOf(2, 3)) {
                for (second in listOf(2, 3)) {
                    for (third in listOf(2, 3)) {
                        add(CoinLineResult(1, listOf(first, second, third)))
                    }
                }
            }
        }

        assertEquals(mapOf(6 to 1, 7 to 3, 8 to 3, 9 to 1), lines.groupingBy { it.value }.eachCount())
    }

    @Test
    fun lineValuesMapToCorrectPolarityAndMovement() {
        val oldYin = CoinLineResult(1, listOf(2, 2, 2))
        val youngYang = CoinLineResult(2, listOf(3, 2, 2))
        val youngYin = CoinLineResult(3, listOf(3, 3, 2))
        val oldYang = CoinLineResult(4, listOf(3, 3, 3))

        assertEquals("老阴", oldYin.typeLabel)
        assertFalse(oldYin.isYang)
        assertTrue(oldYin.isMoving)
        assertTrue(oldYin.transformedIsYang)

        assertEquals("少阳", youngYang.typeLabel)
        assertTrue(youngYang.isYang)
        assertFalse(youngYang.isMoving)
        assertTrue(youngYang.transformedIsYang)

        assertEquals("少阴", youngYin.typeLabel)
        assertFalse(youngYin.isYang)
        assertFalse(youngYin.isMoving)
        assertFalse(youngYin.transformedIsYang)

        assertEquals("老阳", oldYang.typeLabel)
        assertTrue(oldYang.isYang)
        assertTrue(oldYang.isMoving)
        assertFalse(oldYang.transformedIsYang)
    }

    @Test
    fun firstCastIsBottomLineAndMovingOldYangProducesCorrectChangedHexagram() {
        val lines = listOf(
            CoinLineResult(1, listOf(3, 3, 3)),
            CoinLineResult(2, listOf(3, 2, 2)),
            CoinLineResult(3, listOf(3, 2, 2)),
            CoinLineResult(4, listOf(3, 2, 2)),
            CoinLineResult(5, listOf(3, 2, 2)),
            CoinLineResult(6, listOf(3, 2, 2)),
        )

        val reading = FortuneOracle().coin("测试问题", lines.reversed())

        assertTrue(reading.primaryHexagram.startsWith("乾为天"))
        assertTrue(reading.transformedHexagram.startsWith("天风姤"))
        assertEquals((1..6).toList(), reading.coinLines.map { it.position })
        assertTrue(reading.body.contains("初爻动"))
    }

    @Test
    fun offlineCorpusContainsEveryHexagramAndEveryLine() {
        assertEquals(64, ZhouyiClassics.all.size)
        assertEquals(64, ZhouyiClassics.all.map { it.number }.distinct().size)
        assertEquals(64, ZhouyiClassics.all.map { it.pattern }.distinct().size)
        assertTrue(ZhouyiClassics.all.all { it.lineTexts.size == 6 })
        assertTrue(ZhouyiClassics.all.all { it.lineCommentaries.size == 6 })
        assertTrue(ZhouyiClassics.all.all { record -> record.lineCommentaries.all { it.startsWith("《象》曰") } })
        assertTrue(ZhouyiClassics.all.all { it.judgment.isNotBlank() })
        assertTrue(ZhouyiClassics.all.all { it.tuan.startsWith("《彖》曰") })
        assertTrue(ZhouyiClassics.all.all { it.image.startsWith("《象》曰") })
        assertEquals("用九：見群龍无首，吉。", ZhouyiClassics.byNumber(1).useText)
        assertEquals("用六：利永貞。", ZhouyiClassics.byNumber(2).useText)
        assertTrue(ZhouyiClassics.byNumber(1).useCommentary.orEmpty().contains("天德不可為首"))
        assertTrue(ZhouyiClassics.byNumber(2).useCommentary.orEmpty().contains("以大終"))
        assertTrue(ZhouyiClassics.all.drop(2).all { it.useText == null })
    }

    @Test
    fun corpusLookupUsesBottomToTopLineOrder() {
        assertEquals(1, ZhouyiClassics.fromLines(List(6) { true }).number)
        assertEquals(2, ZhouyiClassics.fromLines(List(6) { false }).number)
        assertEquals(
            44,
            ZhouyiClassics.fromLines(listOf(false, true, true, true, true, true)).number,
        )
    }

    @Test
    fun changingLineSelectionCoversZeroThroughSixMovingLines() {
        val qian = ZhouyiClassics.byNumber(1)
        val kun = ZhouyiClassics.byNumber(2)

        val zero = ZhouyiSelectionRules.select(qian, qian, emptyList())
        assertEquals(listOf("卦辞"), zero.references.map { it.textType })
        assertEquals(qian.judgment, zero.references.single().text)

        val one = ZhouyiSelectionRules.select(qian, kun, listOf(3))
        assertEquals(3, one.references.single().linePosition)
        assertEquals(qian.lineText(3), one.references.single().text)

        val two = ZhouyiSelectionRules.select(qian, kun, listOf(1, 5))
        assertEquals(listOf(5, 1), two.references.map { it.linePosition })
        assertTrue(two.references.first().isPrimary)
        assertFalse(two.references.last().isPrimary)

        val three = ZhouyiSelectionRules.select(qian, kun, listOf(1, 3, 6))
        assertEquals(listOf(1, 2), three.references.map { it.hexagramNumber })
        assertEquals(listOf("卦辞", "卦辞"), three.references.map { it.textType })

        val four = ZhouyiSelectionRules.select(qian, kun, listOf(1, 2, 3, 4))
        assertEquals(listOf(5, 6), four.references.map { it.linePosition })
        assertEquals(listOf(2, 2), four.references.map { it.hexagramNumber })

        val five = ZhouyiSelectionRules.select(qian, kun, listOf(1, 2, 3, 4, 5))
        assertEquals(6, five.references.single().linePosition)
        assertEquals(2, five.references.single().hexagramNumber)

        val sixQian = ZhouyiSelectionRules.select(qian, kun, (1..6).toList())
        assertEquals("用九", sixQian.references.single().textType)
        assertEquals(qian.useText, sixQian.references.single().text)

        val sixOther = ZhouyiSelectionRules.select(
            ZhouyiClassics.byNumber(3),
            ZhouyiClassics.byNumber(50),
            (1..6).toList(),
        )
        assertEquals(50, sixOther.references.single().hexagramNumber)
        assertEquals("卦辞", sixOther.references.single().textType)
    }

    @Test
    fun aiPromptMakesAConcreteQuestionTheMainTaskAndHidesVerification() {
        val reading = FortuneOracle().coin(
            question = "我是否应该接受这份新工作？",
            lines = stableQianLines(),
        )

        val prompt = AiInterpreter.buildAiPrompt(reading)

        assertTrue(prompt.contains("用户明确提出的问题是本次解读的核心：我是否应该接受这份新工作？"))
        assertTrue(prompt.contains("1. 针对所问"))
        assertTrue(prompt.contains("现实对象、条件、阻力、时机与可选行动"))
        assertTrue(prompt.contains("禁止输出“起卦复核”"))
        assertFalse(prompt.contains("1. 起卦复核"))
    }

    @Test
    fun aiPromptSupportsSilentDivinationWithoutGuessingTheQuestion() {
        val reading = FortuneOracle().coin(
            question = "",
            lines = stableQianLines(),
        )

        val prompt = AiInterpreter.buildAiPrompt(reading)

        assertTrue(prompt.contains("可能选择在心中默念"))
        assertTrue(prompt.contains("不得猜测、复述或虚构用户心中的问题"))
        assertTrue(prompt.contains("1. 整体提示"))
        assertTrue(prompt.contains("3. 自省方向"))
        assertFalse(prompt.contains("1. 针对所问"))
    }

    private fun stableQianLines(): List<CoinLineResult> = (1..6).map { position ->
        CoinLineResult(position, listOf(3, 2, 2))
    }
}
