package com.zhifou.fortune

import org.junit.Assert.*
import org.junit.Test

class OracleMotionTest {
    @Test fun theNextTossStartsFromThePreviousFaceWithoutSnapping() {
        repeat(3) { coin -> (2..3).forEach { previous -> (2..3).forEach { next ->
            assertEquals(previous == 3, OracleMotion.coinPose(0f, coin, next, previous).frontIsYang)
            val settled = OracleMotion.coinPose(1_100f, coin, next, previous)
            assertEquals(next == 3, settled.frontIsYang)
            assertEquals(0f, settled.liftDp, .001f)
        } } }
    }

    @Test fun allCoinFacesMatchTheirFixedResultsAfterLanding() {
        repeat(3) { coin ->
            (2..3).forEach { value ->
                val pose = OracleMotion.coinPose(1_100f, coin, value)
                assertEquals(value == 3, pose.frontIsYang)
                assertTrue(pose.landed)
                assertEquals(0f, pose.liftDp, .001f)
                assertEquals(0f, pose.driftDp, .001f)
            }
        }
    }

    @Test fun copperCoinsHaveDistinctLandingsAndFinishBeforeTheNextRound() {
        assertTrue(OracleMotion.coinLanding(0) < OracleMotion.coinLanding(1))
        assertTrue(OracleMotion.coinLanding(1) < OracleMotion.coinLanding(2))
        repeat(5) { assertTrue(OracleMotion.coinRoundSettled(it) < OracleMotion.coinRoundStart(it + 1)) }
    }

    @Test fun coinResultsArePublishedAfterAllThreeCoinsSettle() {
        repeat(6) { round ->
            assertEquals(OracleMotion.coinRoundStart(round) + OracleMotion.coinLanding(2) + OracleMotion.coinSettleMs,
                OracleMotion.coinRoundSettled(round))
        }
        assertTrue(OracleMotion.duration(OracleMotionKind.Coins) - OracleMotion.coinRoundSettled(5) >= 520L)
    }

    @Test fun tarotReservesTenSlotsAndNeverFlashesFutureFaces() {
        repeat(10) { index ->
            val before = OracleMotion.cardPose(OracleMotion.cardStart(index) - 1f, index)
            assertEquals(0f, before.alpha, 0f)
            assertEquals(0f, before.rotationY, 0f)
        }
    }

    @Test fun everyCardFinishesFaceUpWithoutAResidualTiltOrLift() {
        repeat(10) { index ->
            val pose = OracleMotion.cardPose(OracleMotion.cardSettled(index) + 1f, index)
            assertEquals(1f, pose.alpha, .001f)
            assertEquals(1f, pose.deal, .001f)
            assertEquals(180f, pose.rotationY, .001f)
            assertEquals(0f, pose.liftDp, .001f)
        }
    }

    @Test fun midFlipHapticsMatchTheVisualTurn() {
        val cues = OracleMotion.cues(OracleMotionKind.Tarot).filter { it.touch == OracleTouch.CardTurn }
        assertEquals(10, cues.size)
        cues.forEachIndexed { index, cue ->
            assertEquals(90f, OracleMotion.cardPose(cue.atMs.toFloat(), index).rotationY, .001f)
        }
    }

    @Test fun cueSequencesAreMonotonicFiniteAndSafelySpaced() {
        OracleMotionKind.entries.forEach { kind ->
            val cues = OracleMotion.cues(kind)
            assertEquals(1, cues.count { it.touch == OracleTouch.Complete })
            assertTrue(cues.all { it.atMs in 0 until OracleMotion.duration(kind) })
            cues.zipWithNext().forEach { (a, b) -> assertTrue("Crowded cues: $a / $b", b.atMs - a.atMs >= 64L) }
            assertEquals(OracleTouch.Complete, cues.last().touch)
        }
    }

    @Test fun eachCoinLandingHasExactlyOneMatchingImpact() {
        val landings = OracleMotion.cues(OracleMotionKind.Coins).filter { it.touch == OracleTouch.CoinLand }
        assertEquals(18, landings.size)
        repeat(6) { round -> repeat(3) { coin ->
            val cue = landings[round * 3 + coin]
            assertEquals(OracleMotion.coinRoundStart(round) + OracleMotion.coinLanding(coin), cue.atMs)
            assertEquals(coin, cue.index)
        } }
    }

    @Test fun reducedMotionRemovesTheFlightAndFlipRatherThanAnimatingFaster() {
        repeat(10) { index ->
            val pose = OracleMotion.cardPose(OracleMotion.cardTurn(index) + OracleMotion.cardFlipMs / 2 + 100f, index, reducedMotion = true)
            assertEquals(1f, pose.deal, 0f)
            assertEquals(180f, pose.rotationY, 0f)
            assertEquals(0f, pose.liftDp, 0f)
        }
    }

    @Test fun samplingAnimationDoesNotMutateTheReadingValues() {
        val values = listOf(3, 2, 3)
        repeat(3) { coin -> (0..1_200 step 16).forEach { OracleMotion.coinPose(it.toFloat(), coin, values[coin]) } }
        assertEquals(listOf(3, 2, 3), values)
    }
}
