package com.zhifou.fortune

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.roundToInt

internal data class TarotDeckTick(val cardIndex: Int, val position: Float, val direction: Int,
    val energy: Float, val cardsPerSecond: Float)

/** An integer position means that specific card's center crosses the visible rail's center. */
internal class TarotDeckHapticTrack {
    private var tracking = false
    private var previous = 0f
    private var previousTime = 0L
    private var speed = 0f
    private var lastCard: Int? = null
    private var rearmed = true

    fun advance(position: Float, nowMs: Long, dragging: Boolean, moving: Boolean,
        enabled: Boolean = true): TarotDeckTick? {
        if (!enabled || !position.isFinite() || (!dragging && !moving)) {
            reset()
            return null
        }
        if (!tracking) {
            if (!dragging) return null
            tracking = true
            anchor(position, nowMs)
            return null
        }
        val dt = nowMs - previousTime
        val delta = position - previous
        val from = previous
        previous = position
        previousTime = nowMs
        if (dt <= 0 || dt > 180 || abs(delta) > 3f) {
            anchor(position, nowMs)
            return null
        }
        val instantaneous = (abs(delta) * 1_000f / dt).coerceAtMost(40f)
        val smoothing = 1f - exp(-dt / 45f)
        speed += (instantaneous - speed) * smoothing
        lastCard?.let { if (abs(position - it) >= REARM_CARDS) rearmed = true }
        val direction = when {
            delta > .0001f -> 1
            delta < -.0001f -> -1
            else -> return null
        }
        val card = if (direction > 0) floor(position + EPSILON).toInt() else ceil(position - EPSILON).toInt()
        val before = if (direction > 0) floor(from + EPSILON).toInt() else ceil(from - EPSILON).toInt()
        if (card == before || card !in 0..77 || (card == lastCard && !rearmed)) return null
        lastCard = card
        rearmed = false
        val velocity = (speed / (speed + 5f)).coerceIn(0f, 1f)
        return TarotDeckTick(card, position, direction, .45f + velocity * .48f, speed)
    }

    private fun anchor(position: Float, nowMs: Long) {
        previous = position
        previousTime = nowMs
        speed = 0f
        lastCard = position.roundToInt().takeIf { abs(position - it) < REARM_CARDS }
        rearmed = lastCard == null
    }

    fun reset() {
        tracking = false
        previous = 0f
        previousTime = 0L
        speed = 0f
        lastCard = null
        rearmed = true
    }

    companion object {
        const val MIN_INTERVAL_MS = 16L
        private const val REARM_CARDS = .12f
        private const val EPSILON = .0001f

        fun position(index: Int, itemCenterPx: Float, viewportCenterPx: Float, stridePx: Float): Float =
            if (stridePx.isFinite() && stridePx > 0f) index + (viewportCenterPx - itemCenterPx) / stridePx
            else Float.NaN
    }
}
