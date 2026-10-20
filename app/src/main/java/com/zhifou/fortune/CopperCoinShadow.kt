package com.zhifou.fortune

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos

internal data class CopperCoinShadowPose(
    val offsetX: Float, val offsetY: Float, val radiusX: Float, val radiusY: Float,
    val opacity: Float, val contactX: Float, val contactRadiusX: Float, val contactOpacity: Float,
)

internal fun copperCoinShadow(pose: CoinMotionPose, reduced: Boolean): CopperCoinShadowPose {
    val height = if (reduced) 0f else pose.liftDp.coerceAtLeast(0f)
    val drift = if (reduced) 0f else pose.driftDp
    val facing = if (reduced) 1f else abs(cos((pose.rotationY % 360f) * PI / 180)).toFloat()
    return CopperCoinShadowPose(
        offsetX = drift + 1f + height * .11f,
        offsetY = 1.5f + height * .035f,
        radiusX = 13f + facing * 7f + height * .08f,
        radiusY = 3.4f + height * .055f,
        opacity = .14f / (1f + height / 24f),
        contactX = drift,
        contactRadiusX = 9f + facing * 6f,
        contactOpacity = .24f / (1f + height * height / 18f),
    )
}
