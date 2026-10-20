package com.zhifou.fortune

import java.util.Random
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class FourCardTarotTest {
    private fun session(reversals: Boolean = true) = FourCardTarot.shuffled(
        "下一阶段如何安排学习？", reversals, Random(122L), 1_786_000_000_000L)

    private fun complete() = listOf(0, 18, 36, 77).fold(session()) { result, slot -> result.pick(slot) }

    @Test fun fullDeckIsCommittedBeforeSelection() {
        val session = session()
        assertEquals(78, session.deck.size)
        assertEquals(TarotDeck.cards.map { it.id }.toSet(), session.deck.map { it.cardId }.toSet())
        assertTrue(session.deck.any { it.reversed })
        assertTrue(session.deck.any { !it.reversed })
        val picked = session.pick(77).pick(0).pick(18).pick(36)
        assertEquals(session.deck, picked.deck)
        assertEquals(listOf(77, 0, 18, 36).map { session.deck[it].cardId }, picked.draws.map { it.cardId })
        assertEquals((1..4).toList(), picked.draws.map { it.positionIndex })
    }

    @Test fun shuffleAndOrientationAreNotBiasedByWhereUserPicks() {
        val firstSlots = mutableSetOf<String>()
        repeat(200) { seed ->
            val game = FourCardTarot.shuffled("", true, Random(seed.toLong()))
            firstSlots += game.deck.first().cardId
            assertEquals(78, game.deck.map { it.cardId }.distinct().size)
            val draw = (0..3).fold(game) { current, slot -> current.pick(slot) }
            FourCardTarot.validate(draw.draws)
        }
        assertTrue(firstSlots.size > 50)
        assertTrue(session(false).deck.none { it.reversed })
    }

    @Test fun duplicateFifthAndInvalidPicksAreRejected() {
        assertThrows(IllegalArgumentException::class.java) { session().pick(0).pick(0) }
        assertThrows(IllegalArgumentException::class.java) { session().pick(-1) }
        assertThrows(IllegalArgumentException::class.java) { session().pick(78) }
        assertThrows(IllegalArgumentException::class.java) { complete().pick(1) }
        assertThrows(IllegalArgumentException::class.java) { FourCardTarot.reading(session()) }
    }

    @Test fun sessionRotationRestorationPreservesEntireShuffleAndSelectionOrder() {
        val partial = session().pick(70).pick(3)
        val restored = FourCardTarot.decode(partial.encode())!!
        assertEquals(partial, restored)
        assertEquals(partial.pick(4).pick(65), restored.pick(4).pick(65))
        assertNull(FourCardTarot.decode("not json"))
        val malformed = JSONObject(partial.encode()).put("picked", org.json.JSONArray(listOf(3, 3)))
        assertNull(FourCardTarot.decode(malformed.toString()))
        val duplicated = JSONObject(partial.encode())
        duplicated.getJSONArray("deck").put(1, duplicated.getJSONArray("deck").get(0))
        assertNull(FourCardTarot.decode(duplicated.toString()))
    }

    @Test fun localInterpretationUsesAllFourPositionsAndPersistsWithoutCelticRelabeling() {
        val result = FourCardTarot.reading(complete())
        val restored = JSONObject(result.toJson().toString()).toReading()
        assertEquals(result, restored)
        assertEquals(FourCardTarot.KIND, restored.kind)
        assertEquals(listOf("现状", "阻碍", "助力", "趋向"), restored.tarotDraws.map { FourCardTarot.position(it).name })
        restored.tarotDraws.forEach {
            assertTrue(FourCardTarot.positionReading(it).contains(it.meaning))
        }
        assertTrue(result.body.contains(restored.tarotDraws.last().card.nameZh))
        assertTrue(result.aiInterpretation.isEmpty())
    }

    @Test fun aiPayloadIsFeatureScopedAndKeepsAllOutcomesAndOptionalQuestion() {
        val result = FourCardTarot.reading(complete())
        val prompt = AiInterpreter.buildAiPrompt(result, TarotAiContext(nickname = "测试昵称", keywords = "学习",
            birthDate = "1998-01-01", suitable = listOf("安葬")))
        result.tarotDraws.forEach { draw ->
            assertTrue(prompt.contains(draw.cardId))
            assertTrue(prompt.contains(draw.card.nameZh))
            assertTrue(prompt.contains(FourCardTarot.position(draw).name))
            assertTrue(prompt.contains(draw.orientationLabel))
        }
        assertTrue(prompt.contains("测试昵称"))
        assertTrue(prompt.contains("如何安排学习"))
        assertTrue(prompt.contains("【解读完成】"))
        assertFalse(prompt.contains("1998-01-01"))
        assertFalse(prompt.contains("安葬"))
        val silent = AiInterpreter.buildAiPrompt(result.copy(question = ""))
        assertTrue(silent.contains("未知具体内容"))
        assertTrue(silent.contains("不猜测心事"))
        val celtic = FortuneOracle().tarot("测试", TarotDeck.drawCelticCross(Random(8)))
        assertTrue(AiInterpreter.buildAiPrompt(celtic).contains("唯一有效的抽牌结果"))
        assertTrue(AiInterpreter.buildAiPrompt(celtic).contains("希望与担忧"))
    }

    @Test fun invalidFourCardPayloadCannotReachAi() {
        val result = FourCardTarot.reading(complete())
        assertThrows(IllegalArgumentException::class.java) {
            FourCardTarotPrompt.build(result.copy(tarotDraws = result.tarotDraws.take(3)), null)
        }
        assertThrows(IllegalArgumentException::class.java) {
            FourCardTarot.validate(result.tarotDraws.map { it.copy(cardId = result.tarotDraws.first().cardId) })
        }
    }

    @Test fun revealPoseStartsAndEndsExactlyWithoutMirroredFinalFace() {
        assertEquals(0f, FourCardTarotMotion.pose(0f).turn)
        assertEquals(1f, FourCardTarotMotion.pose(1f).turn)
        assertEquals(1f, FourCardTarotMotion.pose(1f).travel)
        assertEquals(1f, FourCardTarotMotion.pose(1f).settle)
        assertEquals(0f, FourCardTarotMotion.pose(1f).lift)
        assertEquals(0f, FourCardTarotMotion.pose(1f).glint, .00001f)
        var previous = 0f
        repeat(1001) {
            val pose = FourCardTarotMotion.pose(it / 1000f)
            assertTrue(pose.turn in previous..1f)
            assertTrue(pose.travel in 0f..1f)
            assertTrue(pose.lift.isFinite() && pose.glint.isFinite())
            previous = pose.turn
            assertEquals(0f, FourCardTarotMotion.pose(it / 1000f, true).lift)
        }
        assertEquals(.5f to .22f, FourCardTarotMotion.anchor(2))
        assertEquals(.16f to .69f, FourCardTarotMotion.anchor(3))
        assertEquals(.84f to .69f, FourCardTarotMotion.anchor(4))
    }
}
