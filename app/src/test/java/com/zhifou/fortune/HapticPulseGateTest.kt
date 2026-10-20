package com.zhifou.fortune

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HapticPulseGateTest {
    @Test
    fun acceptsFirstPulseAndSuppressesDuplicateAnimationEvents() {
        val gate = HapticPulseGate()
        assertTrue(gate.tryAcquire(0, 65))
        assertFalse(gate.tryAcquire(0, 65))
        assertFalse(gate.tryAcquire(64, 65))
        assertTrue(gate.tryAcquire(65, 65))
    }

    @Test
    fun rejectedEventsDoNotExtendTheCooldown() {
        val gate = HapticPulseGate()
        assertTrue(gate.tryAcquire(100, 50))
        assertFalse(gate.tryAcquire(140, 50))
        assertTrue(gate.tryAcquire(150, 50))
    }

    @Test
    fun cancellationAllowsTheNextInteractionImmediately() {
        val gate = HapticPulseGate()
        assertTrue(gate.tryAcquire(100, 65))
        gate.reset()
        assertTrue(gate.tryAcquire(101, 65))
    }

    @Test
    fun clockResetDoesNotSilenceFutureFeedback() {
        val gate = HapticPulseGate()
        assertTrue(gate.tryAcquire(10_000, 65))
        assertTrue(gate.tryAcquire(0, 65))
        assertFalse(gate.tryAcquire(64, 65))
        assertTrue(gate.tryAcquire(65, 65))
    }
}
