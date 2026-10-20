package com.zhifou.fortune

import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sin

internal data class CoinScenePose(val x: Float, val y: Float, val rotation: Float, val tilt: Float)

internal fun coinScenePose(index: Int, count: Int, angle: Float, rolling: Boolean): CoinScenePose {
    require(count in 1..10 && index in 0 until count)
    val columns = min(count, 5)
    val rows = ceil(count / columns.toFloat()).toInt()
    val row = index / columns
    val rowSize = min(columns, count - row * columns)
    val motion = if (rolling) sin(Math.toRadians(angle.toDouble())).toFloat() else 0f
    return CoinScenePose(
        x = (index % columns - (rowSize - 1) / 2f) * 2.65f,
        y = ((rows - 1) / 2f - row) * 2.8f + kotlin.math.abs(motion) * .14f,
        rotation = angle,
        tilt = motion * 11f,
    )
}
