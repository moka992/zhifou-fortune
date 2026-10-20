package com.zhifou.fortune

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal enum class OracleMotionKind { Coins, Tarot }
internal enum class OracleTouch { CoinLift, CoinRoll, CoinLand, Shuffle, CardDraw, CardTurn, CardSettle, Complete }
internal data class OracleTouchCue(val atMs: Long, val touch: OracleTouch, val index: Int = 0)

/** Visuals, result publication and touch feedback share one monotonic timeline. */
internal object OracleMotion {
    const val coinRoundMs = 1_100L
    const val coinFirstMs = 120L
    const val coinFlightMs = 620L
    const val coinLandingGapMs = 86L
    const val coinSettleMs = 200L
    const val shuffleMs = 720L
    const val cardIntervalMs = 720L
    const val cardDealMs = 280L
    const val cardFlipMs = 1_080L

    fun coinRoundStart(index: Int) = coinFirstMs + index * coinRoundMs
    fun coinLanding(index: Int) = coinFlightMs + index * coinLandingGapMs
    fun coinRoundSettled(index: Int) = coinRoundStart(index) + coinLanding(2) + coinSettleMs
    fun cardStart(index: Int) = shuffleMs + index * cardIntervalMs
    fun cardTurn(index: Int) = cardStart(index) + cardDealMs
    fun cardSettled(index: Int) = cardTurn(index) + cardFlipMs
    fun duration(kind: OracleMotionKind) = when (kind) {
        OracleMotionKind.Coins -> coinRoundSettled(5) + 600L
        OracleMotionKind.Tarot -> cardSettled(9) + 180L
    }

    fun cues(kind: OracleMotionKind): List<OracleTouchCue> = buildList {
        when (kind) {
            OracleMotionKind.Coins -> repeat(6) { round ->
                val start = coinRoundStart(round)
                add(OracleTouchCue(start, OracleTouch.CoinLift))
                add(OracleTouchCue(start + 225L, OracleTouch.CoinRoll))
                repeat(3) { coin -> add(OracleTouchCue(start + coinLanding(coin), OracleTouch.CoinLand, coin)) }
            }
            OracleMotionKind.Tarot -> {
                repeat(3) { add(OracleTouchCue(80L + it * 180L, OracleTouch.Shuffle)) }
                repeat(10) { card ->
                    add(OracleTouchCue(cardStart(card), OracleTouch.CardDraw))
                    add(OracleTouchCue(cardTurn(card) + cardFlipMs / 2L, OracleTouch.CardTurn))
                    add(OracleTouchCue(cardSettled(card), OracleTouch.CardSettle))
                }
            }
        }
        add(OracleTouchCue(duration(kind) - 100L, OracleTouch.Complete))
    }.sortedBy { it.atMs }

    fun smooth(value: Float): Float = value.coerceIn(0f, 1f).let { it * it * (3f - 2f * it) }

    fun coinPose(elapsedMs: Float, coin: Int, value: Int, previousValue: Int = 3): CoinMotionPose {
        require(coin in 0..2 && value in 2..3 && previousValue in 2..3)
        val contact = coinLanding(coin).toFloat()
        val flight = ((elapsedMs - coin * 24f) / (contact - coin * 24f)).coerceIn(0f, 1f)
        val arc = sin(PI.toFloat() * flight)
        val bounce = ((elapsedMs - contact) / coinSettleMs).coerceIn(0f, 1f)
        val lift = arc * 50f + sin(PI.toFloat() * bounce) * (1f - bounce) * 9f
        val target = (4 + coin) * 360f + if (value == 2) 180f else 0f
        val start = if (previousValue == 2) 180f else 0f
        val rotation = start + (target - start) * (1f - (1f - flight) * (1f - flight))
        return CoinMotionPose(
            liftDp = lift,
            driftDp = arc * (coin - 1) * 12f,
            rotationY = rotation,
            rotationZ = (coin - 1) * 9f + arc * (coin - 1) * 24f,
            frontIsYang = cos(rotation * PI.toFloat() / 180f) >= 0f,
            landed = elapsedMs >= contact,
        )
    }

    fun cardPose(elapsedMs: Float, index: Int, reducedMotion: Boolean = false): TarotMotionPose {
        require(index in 0..9)
        val elapsed = elapsedMs - cardStart(index)
        val deal = smooth(elapsed / cardDealMs)
        val flip = smooth((elapsed - cardDealMs) / cardFlipMs)
        val lift = sin(PI.toFloat() * flip) * 8f
        return TarotMotionPose(
            alpha = if (elapsed < 0f) 0f else if (reducedMotion) 1f else (deal * 3f).coerceAtMost(1f),
            deal = if (reducedMotion) 1f else deal,
            rotationY = if (reducedMotion) { if (flip > .5f) 180f else 0f } else flip * 180f,
            liftDp = if (reducedMotion) 0f else lift,
        )
    }
}

internal data class CoinMotionPose(
    val liftDp: Float, val driftDp: Float, val rotationY: Float, val rotationZ: Float,
    val frontIsYang: Boolean, val landed: Boolean,
)

internal data class TarotMotionPose(val alpha: Float, val deal: Float, val rotationY: Float, val liftDp: Float)
