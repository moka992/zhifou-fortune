package com.zhifou.fortune

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import org.junit.Assert.assertEquals
import org.junit.Test

class FortuneMotionTest {
    @Test fun disabledControlsNeverScale() {
        listOf(true, false).forEach { pressed ->
            assertEquals(1f, fortunePressTarget(pressed, enabled = false, reduced = false, depth = .9f), 0f)
        }
    }

    @Test fun reducedMotionSuppressesPressAndCustomTransitions() {
        assertEquals(1f, fortunePressTarget(true, true, true, .965f), 0f)
        val specs = listOf(fortuneSpatialMotionSpec<Float>(true, false),
            fortuneSpatialMotionSpec(true, true), fortuneEffectsMotionSpec(true))
        specs.forEach { spec ->
            assertEquals(0L, spec.vectorize(Float.VectorConverter).getDurationNanos(
                AnimationVector1D(0f), AnimationVector1D(1f), AnimationVector1D(0f)))
        }
    }

    @Test fun pressDepthIsBoundedAndReleaseIsExact() {
        assertEquals(.965f, fortunePressTarget(true, true, false, .965f), 0f)
        assertEquals(.9f, fortunePressTarget(true, true, false, -1f), 0f)
        assertEquals(1f, fortunePressTarget(true, true, false, 2f), 0f)
        assertEquals(1f, fortunePressTarget(false, true, false, .965f), 0f)
    }

}
