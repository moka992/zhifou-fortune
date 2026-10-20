package com.zhifou.fortune

import kotlin.math.exp
import kotlin.math.sqrt

/** Streaming, allocation-free gesture measurement in m/s² and sensor monotonic nanoseconds. */
internal class DiceShakeDetector(
    private val includesGravity: Boolean = false,
    maximumRange: Float = 40f,
) {
    private val strongThreshold = if (maximumRange.isFinite()) (maximumRange * .8f).coerceIn(18f, 24f) else 24f
    private val mediumThreshold = minOf(14f, strongThreshold * .65f)
    private var lastTimestamp = Long.MIN_VALUE
    private var gravityX = 0f
    private var gravityY = 0f
    private var gravityZ = 0f
    private var armed = false
    private var quietSince = Long.MIN_VALUE
    private var blockedUntil = Long.MIN_VALUE
    private var started = Long.MIN_VALUE
    private var peak = 0f
    private var energy = 0f
    private var measuredSeconds = 0f
    private var activeSeconds = 0f
    private var firstX = 0f
    private var firstY = 0f
    private var firstZ = 0f
    private var firstMagnitude = 0f
    private var reversed = false

    fun reset() {
        lastTimestamp = Long.MIN_VALUE
        armed = false
        quietSince = Long.MIN_VALUE
        blockedUntil = Long.MIN_VALUE
        clearGesture()
    }

    fun sample(timestampNanos: Long, x: Float, y: Float, z: Float, enabled: Boolean = true): DiceThrowStrength? {
        if (!x.isFinite() || !y.isFinite() || !z.isFinite() || timestampNanos < 0) return null
        if (lastTimestamp != Long.MIN_VALUE && timestampNanos <= lastTimestamp) return null
        val first = lastTimestamp == Long.MIN_VALUE
        val gap = if (first) 0L else timestampNanos - lastTimestamp
        if (gap > 300_000_000L) {
            armed = false
            quietSince = Long.MIN_VALUE
            clearGesture()
        }
        if (first || gap > 300_000_000L) {
            gravityX = x; gravityY = y; gravityZ = z
        }
        val dt = if (first) .02f else (gap / 1_000_000_000f).coerceIn(.001f, .05f)
        lastTimestamp = timestampNanos
        var ax = x; var ay = y; var az = z
        if (includesGravity) {
            val retention = exp(-dt / .35f)
            gravityX = gravityX * retention + x * (1f - retention)
            gravityY = gravityY * retention + y * (1f - retention)
            gravityZ = gravityZ * retention + z * (1f - retention)
            ax -= gravityX; ay -= gravityY; az -= gravityZ
        }
        val magnitude = sqrt(ax * ax + ay * ay + az * az).coerceAtMost(80f)
        if (!enabled || timestampNanos < blockedUntil) {
            armed = false
            quietSince = Long.MIN_VALUE
            clearGesture()
            return null
        }
        // A fresh deliberate gesture needs a quiet interval, including after a busy throw.
        if (!armed) {
            if (magnitude < 3.5f) {
                if (quietSince == Long.MIN_VALUE) quietSince = timestampNanos
                if (timestampNanos - quietSince >= 100_000_000L) armed = true
            } else quietSince = Long.MIN_VALUE
            return null
        }
        if (started == Long.MIN_VALUE) {
            if (magnitude < 7.5f) return null
            started = timestampNanos
            firstX = ax; firstY = ay; firstZ = az
            firstMagnitude = magnitude
        }
        peak = maxOf(peak, magnitude)
        energy += magnitude * magnitude * dt
        measuredSeconds += dt
        if (magnitude >= 7.5f) {
            activeSeconds += dt
            if (ax * firstX + ay * firstY + az * firstZ < -.2f * magnitude * firstMagnitude) reversed = true
        }
        val elapsed = timestampNanos - started
        if (elapsed >= 160_000_000L && activeSeconds >= .04f && reversed) {
            val rms = sqrt(energy / measuredSeconds)
            if (rms >= 6f) {
                val effort = peak * .75f + rms * .25f
                val strength = when {
                    effort >= strongThreshold -> DiceThrowStrength.Strong
                    effort >= mediumThreshold -> DiceThrowStrength.Medium
                    else -> DiceThrowStrength.Light
                }
                armed = false
                quietSince = Long.MIN_VALUE
                blockedUntil = timestampNanos + 1_200_000_000L
                clearGesture()
                return strength
            }
        }
        if (elapsed >= 280_000_000L) clearGesture()
        return null
    }

    private fun clearGesture() {
        started = Long.MIN_VALUE
        peak = 0f
        energy = 0f
        measuredSeconds = 0f
        activeSeconds = 0f
        reversed = false
    }
}
