package com.zhifou.fortune

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IconPartMotionTest {
    @Test fun connectedLensAndFilledPhotosNeverSplitAcrossAMovingMask() {
        val whole = listOf(IconMotionRegion(0f, 0f, 24f, 24f))
        assertEquals(whole, semanticIconRegions(SemanticIconKind.Search, "Filled.Search"))
        assertEquals(whole, semanticIconRegions(SemanticIconKind.Picture, "Filled.Image"))
        assertEquals(whole, semanticIconRegions(SemanticIconKind.Picture, "Filled.AddPhotoAlternate"))
        assertEquals(whole, semanticIconRegions(SemanticIconKind.Shield, "Filled.Shield"))
        assertTrue(semanticIconRegions(SemanticIconKind.Calendar).all { it.bottom < 20f })
    }

    @Test fun startAndEverySettledFrameUseTheExactIdentityPose() {
        val samples = listOf(-1f, 0f, ICON_PART_MOTION_END, .80f, ICON_PART_REST_START, .999f, 1f, 2f,
            Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)
        SemanticIconKind.entries.forEach { kind ->
            semanticIconRegions(kind).indices.forEach { part -> samples.forEach { phase ->
                assertEquals("$kind / $part / $phase", IconPartPose(), semanticIconPose(kind, part, phase, kind.name))
            } }
        }
        assertTrue(ICON_PART_MOTION_END < ICON_PART_REST_START)
    }

    @Test fun canonicalPixelRegionsAreBoundedAndNeverOverlap() {
        SemanticIconKind.entries.forEach { kind ->
            val regions = semanticIconRegions(kind)
            assertEquals(kind == SemanticIconKind.None, regions.isEmpty())
            regions.forEachIndexed { index, a ->
                assertTrue(a.left >= 0f && a.right <= 24f && a.top >= 0f && a.bottom <= 24f)
                assertTrue(a.left < a.right && a.top < a.bottom)
                regions.drop(index + 1).forEach { b ->
                    assertFalse("Overlapping masks: $kind", maxOf(a.left, b.left) < minOf(a.right, b.right) &&
                        maxOf(a.top, b.top) < minOf(a.bottom, b.bottom))
                }
            }
        }
    }

    @Test fun partsStayFiniteAndEveryAnimatedMeaningHasItsOwnMotion() {
        val moving = mutableSetOf<SemanticIconKind>()
        SemanticIconKind.entries.forEach { kind ->
            semanticIconRegions(kind).indices.forEach { part -> (0..100).forEach { frame ->
                val pose = semanticIconPose(kind, part, frame / 100f, kind.name)
                assertTrue(listOf(pose.x, pose.y, pose.scaleX, pose.scaleY, pose.rotation, pose.opacity,
                    pose.pivotX, pose.pivotY).all(Float::isFinite))
                assertTrue(pose.opacity in 0f..1f)
                assertTrue(pose.scaleX in .4f..1.2f && pose.scaleY in .4f..1.2f)
                if (pose.x != 0f || pose.y != 0f || pose.scaleX != 1f || pose.scaleY != 1f ||
                    pose.rotation != 0f || pose.opacity != 1f) moving += kind
            } }
        }
        assertEquals(SemanticIconKind.entries.toSet() - SemanticIconKind.None, moving)
    }

    @Test fun excursionHasNoHardCutAtEitherEndpoint() {
        assertEquals(0f, iconPartExcursion(0f, .02f, .78f), 0f)
        assertEquals(0f, iconPartExcursion(1f, .02f, .78f), 0f)
        assertTrue(iconPartExcursion(.0201f, .02f, .78f) < .00001f)
        assertTrue(iconPartExcursion(.7799f, .02f, .78f) < .00001f)
        assertEquals(0f, iconPartExcursion(Float.NaN, .02f, .78f), 0f)
    }
}
