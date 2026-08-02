package com.zhifou.fortune

import java.util.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TarotDeckTest {
    @Test
    fun riderWaiteDeckContainsTheCompleteUniqueStructure() {
        val cards = TarotDeck.cards

        assertEquals(78, cards.size)
        assertEquals(78, cards.map(TarotCard::id).distinct().size)
        assertEquals(78, cards.map(TarotCard::imageRes).distinct().size)
        assertEquals(22, cards.count { it.arcana == TarotArcana.MAJOR })
        TarotSuit.entries.forEach { suit ->
            assertEquals(14, cards.count { it.suit == suit })
        }
        cards.forEach { card ->
            assertTrue(card.nameZh.isNotBlank())
            assertTrue(card.nameEn.isNotBlank())
            assertTrue(card.imageDescription.isNotBlank())
            assertTrue(card.uprightKeywords.size >= 3)
            assertTrue(card.reversedKeywords.size >= 3)
            assertTrue(card.uprightMeaning.isNotBlank())
            assertTrue(card.reversedMeaning.isNotBlank())
        }
    }

    @Test
    fun celticCrossDrawHasTenUniqueCardsAndIndependentOrientations() {
        val draw = TarotDeck.drawCelticCross(Random(20260802L))

        assertEquals((1..10).toList(), draw.map(TarotDraw::positionIndex))
        assertEquals(10, draw.map(TarotDraw::cardId).distinct().size)
        assertTrue(draw.any(TarotDraw::reversed))
        assertTrue(draw.any { !it.reversed })
    }

    @Test
    fun localReadingUsesQuestionAndKeySpreadPositions() {
        val draw = TarotDeck.drawCelticCross(Random(42L))
        val result = TarotDeck.interpret(draw, "我该怎样安排下一阶段的学习？")

        assertTrue(result.title.contains(draw.last().card.nameZh))
        assertTrue(result.body.contains("下一阶段的学习"))
        assertTrue(result.body.contains(draw.first().card.nameZh))
        assertTrue(result.body.contains(draw[1].card.nameZh))
        assertTrue(result.body.contains(draw[5].card.nameZh))
        assertTrue(result.body.contains(draw[9].card.nameZh))
        assertTrue(result.advice.contains("不替代"))
    }

    @Test
    fun tarotAiPromptPreservesTheDrawAndFiltersSensitiveAlmanacTerms() {
        val draw = TarotDeck.drawCelticCross(Random(7L))
        val reading = FortuneOracle().tarot("这段合作下一步怎样推进？", draw)
        val context = TarotAiContext(
            nickname = "测试用户",
            birthDate = "1996-03-08",
            keywords = "合作、工作",
            dateLabel = "2026年8月2日",
            suitable = listOf("交易", "安葬", "诸事不宜"),
            avoid = listOf("立券", "出殡", "破屋"),
        )

        val prompt = AiInterpreter.buildAiPrompt(reading, context)

        assertTrue(prompt.contains("这段合作下一步怎样推进"))
        assertTrue(prompt.contains("测试用户"))
        assertTrue(prompt.contains("交易"))
        assertTrue(prompt.contains("立券"))
        assertTrue(prompt.contains("唯一有效的抽牌结果"))
        draw.forEach { card ->
            assertTrue(prompt.contains(card.card.nameZh))
            assertTrue(prompt.contains(card.orientationLabel))
        }
        listOf("安葬", "诸事不宜", "出殡", "破屋").forEach {
            assertFalse(prompt.contains(it))
        }
    }
}
