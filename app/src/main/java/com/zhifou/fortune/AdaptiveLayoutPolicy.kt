package com.zhifou.fortune

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

internal fun isShortWideWindow(widthDp: Float, heightDp: Float): Boolean =
    widthDp >= 600f && heightDp < 600f && widthDp > heightDp

internal fun toolCatalogColumns(widthDp: Float, shortWide: Boolean, fontScale: Float): Int = when {
    shortWide -> floor(widthDp / (128f * fontScale.coerceAtLeast(1f))).toInt().coerceIn(1, 4)
    widthDp >= 820f -> 3
    widthDp >= 300f -> 2
    else -> 1
}

internal fun landscapeCalendarCellHeight(availableHeightDp: Float, weekdayHeightDp: Float, rows: Int, fontScale: Float): Float {
    require(rows in 4..6)
    return max(32f * fontScale + 4f, min(48f, (availableHeightDp - weekdayHeightDp - rows * 2f) / rows))
}

internal fun rhythmDialDiameter(shortWide: Boolean, compactWidth: Boolean, fontScale: Float): Float {
    val base = if (shortWide) 88f else if (compactWidth) 100f else 116f
    val numeralSize = if (shortWide) 24f else 32f
    return max(base, 40f + 1.8f * numeralSize * fontScale)
}

internal fun diceLandscapeCounterWidth(contentWidthDp: Float, fontScale: Float): Float {
    val width = contentWidthDp.coerceAtLeast(0f)
    val minimum = 128f + 32f * (fontScale.coerceAtLeast(1f) - 1f)
    return min(width * .4f, max(minimum, min(188f, width * .24f)))
}

internal fun landscapeRailWidth(widestLabelDp: Float): Float = max(80f, widestLabelDp + 45f)

internal fun landscapeToolbarHeight(actionLineHeightDp: Float): Float = max(48f, actionLineHeightDp + 24f)
