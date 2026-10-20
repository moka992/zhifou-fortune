package com.zhifou.fortune

import org.junit.Assert.*
import org.junit.Test

class TarotDeckHapticTrackTest {
    private fun sweep(speed: Float, frameMs: Long = 16L): List<Pair<Long, TarotDeckTick>> {
        val track = TarotDeckHapticTrack()
        track.advance(40f, 0, dragging = true, moving = true)
        return (frameMs..2_400L step frameMs).mapNotNull { time ->
            track.advance(40f + speed * time / 1_000f, time, dragging = true, moving = true)?.let { time to it }
        }
    }

    @Test fun exactlyOneTickPerCardCenterNotSeveralDistancePulses() {
        val track = TarotDeckHapticTrack()
        track.advance(12.25f, 0, dragging = true, moving = true)
        val events = listOf(12.5f, 12.99f, 13.01f, 13.6f, 13.99f, 14.01f)
            .mapIndexedNotNull { i, p -> track.advance(p, (i + 1) * 50L, dragging = true, moving = true) }
        assertEquals(listOf(13, 14), events.map { it.cardIndex })
        assertTrue(events.all { it.direction == 1 && kotlin.math.abs(it.position - it.cardIndex) < .02f })
    }

    @Test fun crossingPhaseDoesNotDependOnWhereTheFingerStarted() {
        for (start in listOf(12.2f, 12.6f, 12.8f)) {
            val track = TarotDeckHapticTrack()
            track.advance(start, 0, dragging = true, moving = true)
            assertNull(track.advance(12.99f, 50, dragging = true, moving = true))
            assertEquals(13, track.advance(13.01f, 100, dragging = true, moving = true)?.cardIndex)
        }
    }

    @Test fun actualItemCentersAndContentPaddingDetermineTheCrossing() {
        assertEquals(4f, TarotDeckHapticTrack.position(4, 160f, 160f, 128f), 0f)
        assertEquals(4.5f, TarotDeckHapticTrack.position(4, 96f, 160f, 128f), 0f)
        assertEquals(4f, TarotDeckHapticTrack.position(3, 32f, 160f, 128f), 0f)
        assertEquals(4f, TarotDeckHapticTrack.position(4, 134f, 134f, 128f), 0f)
        assertTrue(TarotDeckHapticTrack.position(4, 160f, 160f, 0f).isNaN())
    }

    @Test fun fasterTravelChangesRateAndEnergyWithoutChangingToothSpacing() {
        val slow = sweep(.6f)
        val medium = sweep(3f)
        val fast = sweep(15f)
        assertEquals(listOf(41), slow.map { it.second.cardIndex })
        assertEquals((41..47).toList(), medium.map { it.second.cardIndex })
        assertEquals((41..76).toList(), fast.map { it.second.cardIndex })
        assertTrue(slow.map { it.second.energy }.average() < fast.map { it.second.energy }.average())
        assertTrue(fast.all { it.second.energy in .45f.. .93f })
    }

    @Test fun stationaryHoldsAndJitterAroundACenteredCardStaySilent() {
        val track = TarotDeckHapticTrack()
        track.advance(12f, 0, dragging = true, moving = true)
        repeat(120) { i ->
            assertNull(track.advance(12f + if (i % 2 == 0) .02f else -.02f,
                (i + 1) * 16L, dragging = true, moving = true))
        }
        repeat(120) { i -> assertNull(track.advance(12f, 2_000L + i * 16, dragging = true, moving = true)) }
    }

    @Test fun reversingUsesTheSameToothAndHysteresisStopsChatter() {
        val track = TarotDeckHapticTrack()
        track.advance(12.3f, 0, dragging = true, moving = true)
        val first = track.advance(11.99f, 100, dragging = true, moving = true)!!
        assertEquals(12, first.cardIndex)
        assertEquals(-1, first.direction)
        repeat(20) { i ->
            assertNull(track.advance(if (i % 2 == 0) 12.01f else 11.99f,
                120L + i * 16, dragging = true, moving = true))
        }
        assertNull(track.advance(11.75f, 500, dragging = true, moving = true))
        val reverse = track.advance(12.01f, 600, dragging = true, moving = true)!!
        assertEquals(12, reverse.cardIndex)
        assertEquals(1, reverse.direction)
    }

    @Test fun programmaticAndInitialPositioningStaySilent() {
        val track = TarotDeckHapticTrack()
        repeat(100) { i -> assertNull(track.advance(i.toFloat(), i * 16L, dragging = false, moving = true)) }
        assertNull(track.advance(30.4f, 2_000, dragging = true, moving = true))
        assertEquals(31, track.advance(31.01f, 2_100, dragging = true, moving = true)?.cardIndex)
    }

    @Test fun feedbackFollowsTheUserFlingThenStopsWithoutAnExtraLanding() {
        val track = TarotDeckHapticTrack()
        track.advance(10.3f, 0, dragging = true, moving = true)
        assertEquals(11, track.advance(11.01f, 80, dragging = true, moving = true)?.cardIndex)
        assertEquals(12, track.advance(12.01f, 160, dragging = false, moving = true)?.cardIndex)
        assertNull(track.advance(12.01f, 180, dragging = false, moving = false))
        repeat(20) { i -> assertNull(track.advance(12.01f, 200L + i * 80, dragging = false, moving = false)) }
        assertNull(track.advance(13f, 2_000, dragging = false, moving = true))
    }

    @Test fun commonDisplayRefreshRatesPreserveTheExactCardSequence() {
        for (frame in listOf(8L, 16L, 33L)) {
            val events = sweep(10f, frame)
            val last = kotlin.math.floor(40f + (2_400 / frame * frame) * .01f + .0001f).toInt()
            assertEquals((41..last).toList(), events.map { it.second.cardIndex })
        }
        assertEquals(sweep(2f).map { it.second.cardIndex - 40 }, sweep(-2f).map { 40 - it.second.cardIndex })
    }

    @Test fun fastNormalSwipesDoNotLoseEveryOtherCardToTheOldSixtyMsGate() {
        val gate = HapticArbiter()
        val events = sweep(25f).filter { it.second.cardIndex <= 77 }
        assertTrue(events.size > 20)
        events.forEach { (time, tick) ->
            assertTrue("Card ${tick.cardIndex} was suppressed", gate.acquire(HapticCue.CardBrowse, time, 20))
        }
    }

    @Test fun multipleCrossingsInOneFrameNeverQueueLateVibrations() {
        val track = TarotDeckHapticTrack()
        track.advance(10.2f, 0, dragging = true, moving = true)
        assertEquals(12, track.advance(12.7f, 16, dragging = true, moving = true)?.cardIndex)
        repeat(100) { i -> assertNull(track.advance(12.7f, 32L + i * 16, dragging = true, moving = true)) }
    }

    @Test fun disabledInvalidClockAndLayoutDiscontinuitiesReanchorSilently() {
        val track = TarotDeckHapticTrack()
        track.advance(0f, 0, dragging = true, moving = true)
        assertNull(track.advance(1f, 80, dragging = true, moving = true, enabled = false))
        assertNull(track.advance(10f, 100, dragging = true, moving = true))
        assertNull(track.advance(70f, 116, dragging = true, moving = true))
        assertNull(track.advance(70.5f, 500, dragging = true, moving = true))
        assertNull(track.advance(71f, 0, dragging = true, moving = true))
        assertNull(track.advance(Float.NaN, 16, dragging = true, moving = true))
        assertNull(track.advance(Float.POSITIVE_INFINITY, 32, dragging = true, moving = true))
        assertNull(track.advance(30.4f, 48, dragging = true, moving = true))
        assertEquals(31, track.advance(31.01f, 148, dragging = true, moving = true)?.cardIndex)
    }

    @Test fun browsingStillCannotInterruptCardRevealsOrConfirmation() {
        val gate = HapticArbiter()
        assertTrue(gate.acquire(HapticCue.CardBrowse, 0, 20))
        assertTrue(gate.acquire(HapticCue.CardDraw, 10, 80))
        assertFalse(gate.acquire(HapticCue.CardBrowse, 70, 20))
        assertTrue(gate.acquire(HapticCue.Confirm, 100, 100))
        assertFalse(gate.acquire(HapticCue.CardBrowse, 200, 20))
        assertTrue(gate.acquire(HapticCue.CardBrowse, 280, 20))
    }

    @Test fun amplitudeDetentIsASingleShortTaperedTooth() {
        val soft = HapticDesign.cardDetentSamples(.5f)
        val fast = HapticDesign.cardDetentSamples(.9f)
        assertTrue(soft.timings.sum() <= TarotDeckHapticTrack.MIN_INTERVAL_MS)
        assertEquals(soft.timings.size, soft.amplitudes.size)
        assertEquals(0, soft.amplitudes.first())
        assertEquals(0, soft.amplitudes.last())
        assertTrue(soft.amplitudes.max() < fast.amplitudes.max())
        assertTrue(fast.amplitudes.all { it in 0..96 })
        assertTrue(HapticDesign.cardDetentSamples(Float.NaN).amplitudes.all { it == 0 })
        assertTrue(HapticDesign.cardDetentSamples(0f).amplitudes.all { it == 0 })
        assertEquals(1, HapticComposition.score(HapticCue.CardBrowse, setOf(HapticPrimitive.Tick))!!.size)
    }
}
