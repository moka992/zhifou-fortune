package com.zhifou.fortune

import kotlin.math.abs

internal data class GestureHaptic(val cue: HapticCue, val energy: Float)

/** Spatial detents, speed-sensitive texture and a reversible threshold; no timer-driven buzzing. */
internal class HapticGestureTrack(private val activation: Float = 1f) {
    private var previous = 0f
    private var anchor = 0f
    private var lastTime = Long.MIN_VALUE
    private var lastPulse = Long.MIN_VALUE
    private var armed = false

    init { require(activation > 0f && activation.isFinite()) }

    fun advance(progress: Float, nowMs: Long): GestureHaptic? {
        if (!progress.isFinite()) return null
        val value = progress.coerceIn(0f, activation * 1.5f)
        val delta = abs(value - previous)
        val dt = if (lastTime == Long.MIN_VALUE) 16L else (nowMs - lastTime).coerceAtLeast(1)
        val speed = (delta * 1_000f / dt / activation).coerceIn(0f, 5f)
        previous = value
        lastTime = nowMs
        val threshold = when {
            !armed && value >= activation -> { armed = true; HapticCue.GestureArm }
            armed && value < activation * .78f -> { armed = false; HapticCue.GestureDisarm }
            else -> null
        }
        if (threshold != null) {
            anchor = value
            lastPulse = nowMs
            return GestureHaptic(threshold, 1f)
        }
        val tension = (value / activation).coerceIn(0f, 1f)
        val spacing = activation * (.14f - tension * .065f)
        val interval = if (speed > 2f) 85L else 60L
        if (value < activation * .10f || abs(value - anchor) < spacing ||
            (lastPulse != Long.MIN_VALUE && nowMs - lastPulse < interval)) return null
        anchor = value
        lastPulse = nowMs
        return GestureHaptic(HapticCue.GesturePull, (.22f + tension * .40f + speed * .045f).coerceAtMost(.85f))
    }

    fun reset() {
        previous = 0f
        anchor = 0f
        lastTime = Long.MIN_VALUE
        lastPulse = Long.MIN_VALUE
        armed = false
    }
}

/** Never queue stale touch feedback. Important landings cannot be cut off by a tiny UI tick. */
internal class HapticArbiter {
    private var lastCue: HapticCue? = null
    private var lastTime = Long.MIN_VALUE
    private var reservedUntil = Long.MIN_VALUE
    private var priority = -1

    fun acquire(cue: HapticCue, nowMs: Long, durationMs: Long): Boolean {
        val nextPriority = priority(cue)
        if (lastTime != Long.MIN_VALUE) {
            val elapsed = nowMs - lastTime
            if (cue == lastCue && elapsed < minimumInterval(cue)) return false
            if ((elapsed < 28L && nextPriority <= priority) || (nowMs < reservedUntil && nextPriority <= priority)) return false
            // A save/send followed by route recomposition is one user action, not two vibrations.
            if (elapsed < 170L && priority >= 3 && nextPriority <= 1) return false
        }
        lastCue = cue
        lastTime = nowMs
        reservedUntil = nowMs + durationMs.coerceIn(20, 220)
        priority = nextPriority
        return true
    }

    fun reset() {
        lastCue = null
        lastTime = Long.MIN_VALUE
        reservedUntil = Long.MIN_VALUE
        priority = -1
    }

    private fun priority(cue: HapticCue): Int = when (cue) {
        HapticCue.GesturePull, HapticCue.FineSelection, HapticCue.Shuffle,
        HapticCue.CoinSpin, HapticCue.WheelTick -> 0
        HapticCue.NavigateIn, HapticCue.NavigateOut, HapticCue.MenuOpen, HapticCue.MenuClose,
        HapticCue.Selection, HapticCue.GestureDisarm, HapticCue.GestureCancel -> 1
        HapticCue.CardDraw, HapticCue.CardTurn, HapticCue.CardLand, HapticCue.CoinLift,
        HapticCue.CoinImpact, HapticCue.DiceImpact, HapticCue.GestureArm -> 2
        else -> 3
    }

    private fun minimumInterval(cue: HapticCue): Long = when (cue) {
        HapticCue.GesturePull, HapticCue.FineSelection, HapticCue.WheelTick -> 48
        HapticCue.CoinSpin, HapticCue.Shuffle, HapticCue.DiceImpact, HapticCue.CoinImpact -> 65
        HapticCue.GestureArm, HapticCue.GestureDisarm -> 120
        else -> 80
    }
}
