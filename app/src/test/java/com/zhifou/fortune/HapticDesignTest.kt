package com.zhifou.fortune

import org.junit.Assert.*
import org.junit.Test

class HapticDesignTest {
    @Test fun envelopesAreFiniteTaperedAndBounded() {
        HapticCue.entries.forEach { cue ->
            val points = HapticDesign.points(cue)
            assertTrue(points.size in 2..16)
            assertEquals(0f, points.last().intensity, 0f)
            assertTrue(points.sumOf { it.durationMs } in 40..180)
            assertTrue(points.all { it.durationMs >= 20 && it.intensity in 0f..1f && it.sharpness in 0f..1f })
            val samples = HapticDesign.sample(points)
            assertEquals(samples.timings.size, samples.amplitudes.size)
            assertEquals(0, samples.amplitudes.first())
            assertEquals(0, samples.amplitudes.last())
            assertTrue(samples.amplitudes.all { it in 0..220 })
            assertEquals(points.sumOf { it.durationMs } + 5, samples.timings.sum())
        }
    }

    @Test fun scenesHaveDifferentSignaturesAndEnergyIsNotAmplified() {
        assertEquals(HapticCue.entries.size, HapticCue.entries.map { HapticDesign.points(it) }.distinct().size)
        val base = HapticDesign.points(HapticCue.GesturePull)
        val half = HapticDesign.points(HapticCue.GesturePull, .5f)
        base.zip(half).forEach { (a, b) -> assertEquals(a.intensity / 2f, b.intensity, .0001f) }
        assertTrue(HapticDesign.points(HapticCue.Send, Float.NaN).all { it.intensity == 0f })
    }

    @Test fun gestureTextureIsSpatialAndStopsWhenTheFingerStops() {
        val gesture = HapticGestureTrack()
        assertNull(gesture.advance(.05f, 0))
        val samples = (6..95).mapNotNull { gesture.advance(it / 100f, it * 20L) }
        assertTrue(samples.size in 6..12)
        assertTrue(samples.all { it.cue == HapticCue.GesturePull && it.energy in 0f..1f })
        assertTrue(samples.last().energy > samples.first().energy)
        repeat(50) { assertNull(gesture.advance(.95f, 2_000L + it * 100)) }
        assertNull(gesture.advance(Float.NaN, 8_000))
    }

    @Test fun thresholdCanBeRearmedWithoutChatter() {
        val gesture = HapticGestureTrack(.5f)
        assertEquals(HapticCue.GestureArm, gesture.advance(.51f, 0)?.cue)
        repeat(10) { assertNull(gesture.advance(if (it % 2 == 0) .49f else .51f, 10L * it)) }
        assertEquals(HapticCue.GestureDisarm, gesture.advance(.38f, 250)?.cue)
        assertEquals(HapticCue.GestureArm, gesture.advance(.55f, 450)?.cue)
        gesture.reset()
        assertEquals(HapticCue.GestureArm, gesture.advance(.55f, 500)?.cue)
    }

    @Test fun fastFlingDoesNotCreateABurstOfQueuedDetents() {
        val gesture = HapticGestureTrack()
        val samples = (1..100).mapNotNull { gesture.advance(it / 100f, it.toLong()) }
        assertTrue(samples.size <= 3)
        assertEquals(1, samples.count { it.cue == HapticCue.GestureArm })
    }

    @Test fun coarseFallbackKeepsDistinctRhythmsWithoutLongBuzzes() {
        HapticCue.entries.forEach { cue ->
            val timings = HapticDesign.basicTimings(cue)
            assertEquals(0L, timings.first())
            assertTrue(timings.sum() <= 80L)
            assertTrue(timings.drop(1).all { it > 0L })
            assertTrue(timings.filterIndexed { i, _ -> i % 2 == 1 }.all { it <= 20L })
        }
        assertNotEquals(HapticDesign.basicTimings(HapticCue.CoinFinish).toList(), HapticDesign.basicTimings(HapticCue.DiceFinish).toList())
        assertNotEquals(HapticDesign.basicTimings(HapticCue.CardDraw).toList(), HapticDesign.basicTimings(HapticCue.CardTurn).toList())
    }
}
