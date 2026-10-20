package com.zhifou.fortune

import kotlin.math.PI
import kotlin.math.sin

internal data class FourCardPose(
    val turn: Float, val lift: Float, val travel: Float, val glint: Float, val settle: Float,
)

internal object FourCardTarotMotion {
    const val SHUFFLE_MS = 1800
    const val REVEAL_MS = 2100

    fun pose(progress: Float, reduced: Boolean = false): FourCardPose {
        val value = progress.coerceIn(0f, 1f)
        if (reduced) return FourCardPose(if (value >= .5f) 1f else 0f, 0f, 1f, 0f, 1f)
        if (value == 0f) return FourCardPose(0f, 0f, 0f, 0f, 0f)
        if (value == 1f) return FourCardPose(1f, 0f, 1f, 0f, 1f)
        val travel = OracleMotion.smooth(value / .16f)
        val turn = OracleMotion.smooth((value - .16f) / .60f)
        val settle = OracleMotion.smooth((value - .78f) / .22f)
        return FourCardPose(turn, sin(PI.toFloat() * value) * 12f, travel,
            sin(PI.toFloat() * turn).coerceAtLeast(0f), settle)
    }

    /** The documented modern cross: resistance above, resources left, direction right. */
    fun anchor(position: Int): Pair<Float, Float> = when (position) {
        1 -> .5f to .69f
        2 -> .5f to .22f
        3 -> .16f to .69f
        4 -> .84f to .69f
        else -> error("无效四牌位置")
    }
}
