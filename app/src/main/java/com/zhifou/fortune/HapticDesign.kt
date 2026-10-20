package com.zhifou.fortune

import kotlin.math.ceil

internal enum class HapticCue {
    NavigateIn, NavigateOut, PageSnap, Send, Confirm, Reject,
    GesturePull, GestureRelease, CoinSpin, CoinFinish, CardDraw, CardTurn, CardLand,
    Shuffle, DiceFinish, WheelFinish,
    Selection, FineSelection, ToggleOn, ToggleOff, MenuOpen, MenuClose,
    GestureArm, GestureDisarm, GestureCancel, VoiceStart, VoiceStop, VoiceCancel,
    CoinLift, CoinImpact, DiceImpact, WheelTick,
}

internal data class HapticPoint(val intensity: Float, val sharpness: Float, val durationMs: Long)
internal data class HapticSamples(val timings: LongArray, val amplitudes: IntArray)

/** Perceptual envelopes, not measured or extracted Threads/iOS motor waveforms. */
internal object HapticDesign {
    /** Timing-only fallback when the platform exposes no fine motor controls. */
    fun basicTimings(cue: HapticCue): LongArray = when (cue) {
        HapticCue.NavigateIn -> longArrayOf(0, 10)
        HapticCue.NavigateOut -> longArrayOf(0, 7)
        HapticCue.PageSnap -> longArrayOf(0, 8)
        HapticCue.Send -> longArrayOf(0, 11, 24, 5)
        HapticCue.Confirm -> longArrayOf(0, 10, 34, 7)
        HapticCue.Reject -> longArrayOf(0, 13, 36, 13)
        HapticCue.GesturePull -> longArrayOf(0, 7)
        HapticCue.GestureRelease -> longArrayOf(0, 12)
        HapticCue.CoinSpin -> longArrayOf(0, 6)
        HapticCue.CoinFinish -> longArrayOf(0, 9, 29, 5)
        HapticCue.CardDraw -> longArrayOf(0, 15)
        HapticCue.CardTurn -> longArrayOf(0, 8, 25, 5)
        HapticCue.CardLand -> longArrayOf(0, 5)
        HapticCue.Shuffle -> longArrayOf(0, 6)
        HapticCue.DiceFinish -> longArrayOf(0, 17, 32, 6)
        HapticCue.WheelFinish -> longArrayOf(0, 12, 21, 4)
        HapticCue.Selection -> longArrayOf(0, 8)
        HapticCue.FineSelection -> longArrayOf(0, 5)
        HapticCue.ToggleOn -> longArrayOf(0, 12)
        HapticCue.ToggleOff -> longArrayOf(0, 7)
        HapticCue.MenuOpen -> longArrayOf(0, 9)
        HapticCue.MenuClose -> longArrayOf(0, 6)
        HapticCue.GestureArm -> longArrayOf(0, 11)
        HapticCue.GestureDisarm -> longArrayOf(0, 6)
        HapticCue.GestureCancel -> longArrayOf(0, 7)
        HapticCue.VoiceStart -> longArrayOf(0, 10, 24, 5)
        HapticCue.VoiceStop -> longArrayOf(0, 9)
        HapticCue.VoiceCancel -> longArrayOf(0, 6, 35, 6)
        HapticCue.CoinLift -> longArrayOf(0, 12)
        HapticCue.CoinImpact -> longArrayOf(0, 14, 22, 5)
        HapticCue.DiceImpact -> longArrayOf(0, 18)
        HapticCue.WheelTick -> longArrayOf(0, 6)
    }

    fun points(cue: HapticCue, amount: Float = 1f): List<HapticPoint> {
        val energy = if (amount.isFinite()) amount.coerceIn(0f, 1f) else 0f
        fun p(i: Float, s: Float, ms: Long = 20) = HapticPoint(i * energy, s, ms)
        return when (cue) {
            HapticCue.NavigateIn -> listOf(p(.28f, .28f), p(.12f, .35f), p(0f, .25f))
            HapticCue.NavigateOut -> listOf(p(.19f, .25f), p(0f, .12f, 40))
            HapticCue.PageSnap -> listOf(p(.30f, .55f), p(0f, .30f))
            HapticCue.Send -> listOf(p(.34f, .45f), p(.13f, .30f), p(0f, .20f))
            HapticCue.Confirm -> listOf(p(.36f, .40f), p(0f, .30f), p(.15f, .55f), p(0f, .30f))
            HapticCue.Reject -> listOf(p(.29f, .24f), p(0f, .20f), p(.25f, .24f), p(0f, .15f))
            HapticCue.GesturePull -> listOf(p(.12f, .12f), p(.36f, .32f), p(0f, .15f))
            HapticCue.GestureRelease -> listOf(p(.32f, .48f), p(.12f, .28f), p(0f, .12f))
            HapticCue.CoinSpin -> listOf(p(.25f, .70f), p(.11f, .48f), p(0f, .30f))
            HapticCue.CoinFinish -> listOf(p(.43f, .60f), p(0f, .30f), p(.17f, .50f), p(0f, .18f))
            HapticCue.CardDraw -> listOf(p(.16f, .12f), p(.32f, .28f, 40), p(0f, .12f))
            HapticCue.CardTurn -> listOf(p(.32f, .52f), p(.18f, .36f), p(0f, .15f, 40))
            HapticCue.CardLand -> listOf(p(.20f, .18f), p(0f, .10f))
            HapticCue.Shuffle -> listOf(p(.16f, .60f), p(0f, .25f))
            HapticCue.DiceFinish -> listOf(p(.54f, .42f), p(0f, .30f), p(.20f, .24f), p(0f, .10f))
            HapticCue.WheelFinish -> listOf(p(.38f, .60f), p(.14f, .35f), p(0f, .15f, 40))
            HapticCue.Selection -> listOf(p(.25f, .62f), p(0f, .32f))
            HapticCue.FineSelection -> listOf(p(.14f, .46f), p(0f, .20f))
            HapticCue.ToggleOn -> listOf(p(.19f, .30f), p(.36f, .56f), p(0f, .24f))
            HapticCue.ToggleOff -> listOf(p(.27f, .34f), p(.10f, .16f), p(0f, .10f))
            HapticCue.MenuOpen -> listOf(p(.13f, .18f), p(.24f, .34f), p(0f, .18f))
            HapticCue.MenuClose -> listOf(p(.18f, .28f), p(0f, .12f))
            HapticCue.GestureArm -> listOf(p(.42f, .64f), p(0f, .30f))
            HapticCue.GestureDisarm -> listOf(p(.17f, .26f), p(0f, .12f))
            HapticCue.GestureCancel -> listOf(p(.12f, .20f), p(0f, .08f, 40))
            HapticCue.VoiceStart -> listOf(p(.16f, .24f), p(.40f, .52f), p(0f, .22f))
            HapticCue.VoiceStop -> listOf(p(.31f, .48f), p(0f, .18f))
            HapticCue.VoiceCancel -> listOf(p(.21f, .18f), p(0f, .12f), p(.12f, .20f), p(0f, .10f))
            HapticCue.CoinLift -> listOf(p(.14f, .24f), p(.35f, .68f), p(0f, .32f))
            HapticCue.CoinImpact -> listOf(p(.44f, .70f), p(.19f, .50f), p(0f, .18f))
            HapticCue.DiceImpact -> listOf(p(.56f, .24f), p(.22f, .20f), p(0f, .10f))
            HapticCue.WheelTick -> listOf(p(.23f, .65f), p(0f, .35f))
        }
    }

    /** Last-resort amplitude-capable fallback: finite, smoothly tapered, never repeating. */
    fun sample(points: List<HapticPoint>, stepMs: Long = 5): HapticSamples {
        require(stepMs in 1..20 && points.isNotEmpty() && points.last().intensity == 0f)
        val timings = mutableListOf(0L)
        val amplitudes = mutableListOf(0)
        var previous = 0f
        points.forEach { point ->
            require(point.durationMs > 0 && point.intensity in 0f..1f && point.sharpness in 0f..1f)
            val steps = ceil(point.durationMs.toDouble() / stepMs).toInt()
            var elapsed = 0L
            repeat(steps) { i ->
                val next = minOf((i + 1) * stepMs, point.durationMs)
                val t = (elapsed + next) / (2f * point.durationMs)
                val eased = t * t * (3f - 2f * t)
                timings += next - elapsed
                amplitudes += ((previous + (point.intensity - previous) * eased) * 220f).toInt().coerceIn(0, 220)
                elapsed = next
            }
            previous = point.intensity
        }
        timings += 5L
        amplitudes += 0
        return HapticSamples(timings.toLongArray(), amplitudes.toIntArray())
    }
}

