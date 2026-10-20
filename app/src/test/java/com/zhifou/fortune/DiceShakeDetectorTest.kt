package com.zhifou.fortune

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.junit.Assert.*
import org.junit.Test

class DiceShakeDetectorTest {
    private class Trace(val detector: DiceShakeDetector, val hz: Int = 50, val gravity: Float = 0f) {
        var nanos = 1_000_000_000L
        fun feed(x: Float = 0f, y: Float = 0f, z: Float = 0f, enabled: Boolean = true): DiceThrowStrength? {
            nanos += 1_000_000_000L / hz
            return detector.sample(nanos, x, y, z + gravity, enabled)
        }
        fun rest(seconds: Float = .25f) = repeat((hz * seconds).toInt()) { feed() }
        fun shake(amplitude: Float, seconds: Float = .5f, enabled: Boolean = true): List<DiceThrowStrength> =
            (0 until (hz * seconds).toInt()).mapNotNull { index ->
                feed((amplitude * sin(index * 2 * PI * 4 / hz)).toFloat(), enabled = enabled)
            }
    }

    @Test fun lightMediumAndStrongGesturesUseDifferentTiersAtCommonSamplingRates() {
        for (hz in listOf(25, 50, 100)) {
            for ((amplitude, expected) in listOf(10f to DiceThrowStrength.Light, 20f to DiceThrowStrength.Medium, 35f to DiceThrowStrength.Strong)) {
                val trace = Trace(DiceShakeDetector(), hz)
                trace.rest()
                assertEquals("$hz Hz, amplitude $amplitude", listOf(expected), trace.shake(amplitude))
            }
        }
    }

    @Test fun fallbackRemovesGravityAndMeasuresAllThreeStrengths() {
        for (amplitude in listOf(10f, 20f, 35f)) {
            val trace = Trace(DiceShakeDetector(includesGravity = true), gravity = 9.80665f)
            trace.rest()
            assertEquals(listOf(DiceThrowStrength.entries[listOf(10f, 20f, 35f).indexOf(amplitude)]), trace.shake(amplitude))
        }
    }

    @Test fun stationaryPhoneAndSlowRotationNeverThrow() {
        val trace = Trace(DiceShakeDetector(includesGravity = true))
        repeat(250) { trace.feed(z = 9.80665f) }
        repeat(250) { index ->
            val angle = index * PI / 500
            assertNull(trace.feed(y = (sin(angle) * 9.80665).toFloat(), z = (cos(angle) * 9.80665).toFloat()))
        }
    }

    @Test fun noiseAndAnIsolatedBumpDoNotThrow() {
        val trace = Trace(DiceShakeDetector())
        trace.rest()
        assertTrue(trace.shake(3f).isEmpty())
        assertNull(trace.feed(60f))
        repeat(30) { assertNull(trace.feed()) }
        repeat(30) { assertNull(trace.feed(18f)) }
    }

    @Test fun cooldownAndQuietRearmingPreventRepeatedThrows() {
        val trace = Trace(DiceShakeDetector())
        trace.rest()
        assertEquals(1, trace.shake(35f).size)
        assertTrue(trace.shake(35f, seconds = 2f).isEmpty())
        trace.rest()
        assertEquals(listOf(DiceThrowStrength.Medium), trace.shake(20f))
    }

    @Test fun busyThrowCannotQueueAnotherGesture() {
        val trace = Trace(DiceShakeDetector())
        trace.rest()
        assertTrue(trace.shake(35f, enabled = false).isEmpty())
        assertTrue(trace.shake(35f).isEmpty())
        trace.rest()
        assertEquals(listOf(DiceThrowStrength.Light), trace.shake(10f))
    }

    @Test fun invalidOrOutOfOrderSamplesAreIgnored() {
        val trace = Trace(DiceShakeDetector())
        trace.rest()
        assertNull(trace.detector.sample(trace.nanos - 1, 50f, 0f, 0f))
        assertNull(trace.detector.sample(trace.nanos + 1, Float.NaN, 0f, 0f))
        assertNull(trace.detector.sample(trace.nanos + 2, Float.POSITIVE_INFINITY, 0f, 0f))
        assertEquals(listOf(DiceThrowStrength.Medium), trace.shake(20f))
    }

    @Test fun resetsAndSensorGapsDiscardIncompleteGestures() {
        val trace = Trace(DiceShakeDetector())
        trace.rest()
        trace.feed(20f)
        trace.detector.reset()
        assertTrue(trace.shake(35f).isEmpty())
        trace.rest()
        trace.feed(20f)
        trace.nanos += 2_000_000_000L
        assertTrue(trace.shake(35f).isEmpty())
        trace.rest()
        assertEquals(listOf(DiceThrowStrength.Strong), trace.shake(35f))
    }

    @Test fun lowerRangeSensorsCanStillReachTheStrongTier() {
        val trace = Trace(DiceShakeDetector(maximumRange = 19.6f))
        trace.rest()
        assertEquals(listOf(DiceThrowStrength.Strong), trace.shake(20f))
    }

    @Test fun gestureMagnitudeDoesNotDependOnWhichAxisIsShaken() {
        repeat(3) { axis ->
            val trace = Trace(DiceShakeDetector())
            trace.rest()
            val throws = (0..24).mapNotNull { index ->
                val value = (20f * sin(index * 2 * PI * 4 / 50)).toFloat()
                trace.feed(if (axis == 0) value else 0f, if (axis == 1) value else 0f, if (axis == 2) value else 0f)
            }
            assertEquals(listOf(DiceThrowStrength.Medium), throws)
        }
    }
}
