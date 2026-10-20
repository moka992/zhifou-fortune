package com.zhifou.fortune

/** Bounded launch energy; it never participates in selecting the result. */
internal enum class DiceThrowStrength(
    val speedMin: Float,
    val speedSpread: Float,
    val tumbleMin: Float,
    val tumbleSpread: Float,
) {
    Light(1.6f, .6f, 20f, 7f),
    Medium(2.5f, .8f, 33f, 9f),
    Strong(3.7f, 1f, 49f, 11f),
}
