package com.zhifou.fortune

import org.junit.Assert.*
import org.junit.Test

class HapticInteractionTest {
    @Test fun terminalFeedbackPreemptsTextureButNotTheOtherWayAround() {
        val gate = HapticArbiter()
        assertTrue(gate.acquire(HapticCue.FineSelection, 0, 40))
        assertTrue(gate.acquire(HapticCue.Confirm, 10, 100))
        assertFalse(gate.acquire(HapticCue.FineSelection, 80, 40))
        assertFalse(gate.acquire(HapticCue.NavigateOut, 150, 40))
        assertTrue(gate.acquire(HapticCue.NavigateOut, 190, 40))
    }

    @Test fun repeatAndOverlappingPhysicalEventsAreBoundedNotQueued() {
        val gate = HapticArbiter()
        assertTrue(gate.acquire(HapticCue.CardTurn, 0, 100))
        assertFalse(gate.acquire(HapticCue.CardLand, 35, 50))
        assertTrue(gate.acquire(HapticCue.CardLand, 110, 40))
        assertFalse(gate.acquire(HapticCue.CardLand, 150, 40))
        gate.reset()
        assertTrue(gate.acquire(HapticCue.CardLand, 151, 40))
    }

    @Test fun aGestureReleaseIsNotSwallowedByItsActivationThreshold() {
        val gate = HapticArbiter()
        assertTrue(gate.acquire(HapticCue.GestureArm, 0, 40))
        assertTrue(gate.acquire(HapticCue.GestureRelease, 12, 60))
    }

    @Test fun compositionsUseOnlySupportedPrimitivesAndHaveBoundedScores() {
        val configurations = listOf(emptySet(), setOf(HapticPrimitive.Tick), setOf(HapticPrimitive.Click),
            setOf(HapticPrimitive.LowTick), HapticPrimitive.entries.toSet())
        configurations.forEach { supported ->
            HapticCue.entries.forEach { cue ->
                val atoms = HapticComposition.score(cue, supported)
                if (supported.isEmpty()) assertNull(atoms) else {
                    assertNotNull("$supported $cue", atoms)
                    assertTrue(atoms!!.all { it.primitive in supported && it.scale in .01f..1f && it.delayMs in 0..100 })
                    assertTrue(atoms.sumOf { it.delayMs + 20 } <= 220)
                }
            }
        }
    }

    @Test fun tickOnlyDevicesStillReceiveDifferentCardAndToolRhythms() {
        val onlyTick = setOf(HapticPrimitive.Tick)
        val signatures = listOf(HapticCue.CardDraw, HapticCue.CardTurn, HapticCue.CardLand,
            HapticCue.CoinSpin, HapticCue.CoinFinish, HapticCue.GesturePull)
            .map { HapticComposition.score(it, onlyTick) }
        assertEquals(signatures.size, signatures.distinct().size)
    }

    @Test fun unexpectedlyLongVendorPrimitivesFallBackToAShortSupportedScore() {
        val all = HapticPrimitive.entries.toSet()
        val score = HapticComposition.score(HapticCue.CardDraw, all, mapOf(HapticPrimitive.SlowRise to 500))!!
        assertTrue(score.none { it.primitive == HapticPrimitive.SlowRise })
        assertNull(HapticComposition.score(HapticCue.CardDraw, all, all.associateWith { 500 }))
    }
}
