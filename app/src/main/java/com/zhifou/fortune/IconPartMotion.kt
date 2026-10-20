package com.zhifou.fortune

import kotlin.math.PI
import kotlin.math.sin

internal const val ICON_PART_REST_START = .82f
internal const val ICON_PART_MOTION_END = .78f

internal data class IconMotionRegion(val left: Float, val top: Float, val right: Float, val bottom: Float)

internal data class IconPartPose(
    val x: Float = 0f,
    val y: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rotation: Float = 0f,
    val opacity: Float = 1f,
    val pivotX: Float = 12f,
    val pivotY: Float = 12f,
)

private val RestingIconPart = IconPartPose()
private val WholeIcon = listOf(IconMotionRegion(0f, 0f, 24f, 24f))
private fun isFilledPhoto(name: String): Boolean = name == "Filled.Image" || name == "Filled.AddPhotoAlternate"

/** Regions select existing pixels, never substitute a second drawing or a different icon variant. */
internal fun semanticIconRegions(kind: SemanticIconKind, name: String = ""): List<IconMotionRegion> = when (kind) {
    SemanticIconKind.Home -> listOf(IconMotionRegion(9f, 12f, 15f, 22f))
    SemanticIconKind.Calendar -> listOf(IconMotionRegion(5f, 9f, 19f, 15f), IconMotionRegion(5f, 15f, 19f, 19f))
    SemanticIconKind.Sparkles -> listOf(IconMotionRegion(16f, 0f, 24f, 10f), IconMotionRegion(16f, 14f, 24f, 24f))
    SemanticIconKind.Tools -> listOf(IconMotionRegion(0f, 0f, 12f, 12f), IconMotionRegion(12f, 0f, 24f, 12f),
        IconMotionRegion(0f, 12f, 12f, 24f), IconMotionRegion(12f, 12f, 24f, 24f))
    SemanticIconKind.Profile -> listOf(IconMotionRegion(8f, 4f, 16f, 12f))
    SemanticIconKind.Book, SemanticIconKind.Heart ->
        listOf(IconMotionRegion(0f, 0f, 12f, 24f), IconMotionRegion(12f, 0f, 24f, 24f))
    SemanticIconKind.Tarot -> listOf(IconMotionRegion(8f, 0f, 24f, 22f))
    SemanticIconKind.Wallet -> listOf(IconMotionRegion(13f, 9f, 24f, 18f))
    SemanticIconKind.Work -> listOf(IconMotionRegion(3f, 7f, 21f, 14f))
    SemanticIconKind.Clock -> listOf(IconMotionRegion(10f, 6f, 18f, 17f))
    SemanticIconKind.Search -> WholeIcon
    SemanticIconKind.Mic -> listOf(IconMotionRegion(8f, 1f, 16f, 15f))
    SemanticIconKind.Haptics -> listOf(IconMotionRegion(6f, 0f, 18f, 24f),
        IconMotionRegion(0f, 0f, 6f, 24f), IconMotionRegion(18f, 0f, 24f, 24f))
    SemanticIconKind.Picture -> if (isFilledPhoto(name)) WholeIcon else listOf(IconMotionRegion(5f, 5f, 19f, 19f))
    SemanticIconKind.Add -> listOf(IconMotionRegion(10f, 0f, 14f, 24f))
    SemanticIconKind.Send -> listOf(IconMotionRegion(0f, 0f, 24f, 12f), IconMotionRegion(0f, 12f, 24f, 24f))
    SemanticIconKind.None -> emptyList()
    else -> WholeIcon
}

internal fun iconPartExcursion(progress: Float, start: Float, end: Float): Float {
    if (!progress.isFinite()) return 0f
    val middle = (start + end) / 2f
    return if (progress <= middle) iconStage(progress, start, middle)
    else 1f - iconStage(progress, middle, end)
}

internal fun semanticIconPose(kind: SemanticIconKind, part: Int, progress: Float, name: String): IconPartPose {
    if (!progress.isFinite() || progress <= 0f || progress >= ICON_PART_MOTION_END) return RestingIconPart
    val cue = iconPartExcursion(progress, .02f, .78f)
    val stagger = iconPartExcursion(progress, .06f + part * .035f, .66f + part * .035f)
    val turn = iconStage(progress, .02f, .78f)
    return when (kind) {
        SemanticIconKind.Home -> IconPartPose(scaleX = 1f - .28f * cue, pivotX = 9f, pivotY = 18f)
        SemanticIconKind.Calendar -> IconPartPose(y = -1.4f * stagger, scaleY = 1f - .12f * stagger, pivotY = 13f + part * 4f)
        SemanticIconKind.Sparkles -> IconPartPose(scaleX = 1f + .08f * stagger, scaleY = 1f + .08f * stagger,
            opacity = 1f - .42f * stagger, pivotX = 19f, pivotY = 5f + part * 14f)
        SemanticIconKind.Tools -> IconPartPose(x = (if (part % 2 == 0) -1f else 1f) * stagger,
            y = (if (part < 2) -1f else 1f) * stagger)
        SemanticIconKind.Profile -> IconPartPose(y = -.7f * cue)
        SemanticIconKind.Book -> IconPartPose(scaleX = 1f - .20f * stagger,
            rotation = (if (part == 0) -3f else 3f) * stagger)
        SemanticIconKind.Coins -> IconPartPose(scaleX = 1f - .46f * cue)
        SemanticIconKind.Tarot -> IconPartPose(rotation = -7f * cue, pivotX = 17f, pivotY = 19f)
        SemanticIconKind.Dice -> IconPartPose(y = -1.5f * cue, rotation = sin(turn * PI.toFloat() * 2f) * cue * 11f)
        SemanticIconKind.Wallet -> IconPartPose(scaleX = 1f - .20f * cue, pivotX = 21f, pivotY = 14f)
        SemanticIconKind.Work -> IconPartPose(y = -.6f * cue)
        SemanticIconKind.Heart -> IconPartPose(x = (if (part == 0) -1f else 1f) * stagger)
        SemanticIconKind.Settings -> IconPartPose(rotation = 360f * turn)
        SemanticIconKind.Clock -> IconPartPose(rotation = 360f * turn)
        SemanticIconKind.Search -> IconPartPose(x = sin(turn * PI.toFloat() * 2f) * cue * .7f, y = -.4f * cue)
        SemanticIconKind.Pencil -> IconPartPose(x = sin(turn * PI.toFloat() * 2f) * cue * 1.2f, y = -.6f * cue)
        SemanticIconKind.Mic -> IconPartPose(scaleY = 1f - .09f * cue, opacity = 1f - .20f * cue, pivotY = 8f)
        SemanticIconKind.Haptics -> if (part == 0) {
            IconPartPose(x = sin(turn * PI.toFloat() * 6f) * cue * .45f)
        } else IconPartPose(x = (if (part == 1) -.6f else .6f) * stagger, opacity = 1f - .35f * stagger)
        SemanticIconKind.Picture -> if (isFilledPhoto(name)) IconPartPose(opacity = 1f - .22f * cue)
            else IconPartPose(y = .8f * cue)
        SemanticIconKind.Shield -> IconPartPose(opacity = 1f - .18f * cue)
        SemanticIconKind.Check -> IconPartPose(opacity = 1f - .28f * cue)
        SemanticIconKind.Add -> IconPartPose(scaleY = 1f - .30f * cue)
        SemanticIconKind.Arrow -> IconPartPose(x = (if (name.endsWith("Right")) .8f else -.8f) * cue)
        SemanticIconKind.Refresh -> IconPartPose(rotation = 360f * turn)
        SemanticIconKind.Send -> IconPartPose(scaleY = 1f - .17f * stagger, pivotX = 22f)
        SemanticIconKind.None -> RestingIconPart
    }
}
