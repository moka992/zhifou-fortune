package com.zhifou.fortune

import org.junit.Assert.*
import org.junit.Test

class AdaptiveLayoutPolicyTest {
    @Test fun shortWideLayoutUsesWindowShapeRatherThanDeviceName() {
        assertTrue(isShortWideWindow(640f, 360f))
        assertTrue(isShortWideWindow(731f, 329f))
        assertTrue(isShortWideWindow(960f, 480f))
        assertFalse(isShortWideWindow(360f, 800f))
        assertFalse(isShortWideWindow(800f, 1280f))
        assertFalse(isShortWideWindow(1280f, 800f))
        assertFalse(isShortWideWindow(500f, 320f))
    }

    @Test fun toolCatalogUsesAvailablePaneWidthAndLeavesRoomForLargeText() {
        assertEquals(4, toolCatalogColumns(560f, true, 1f))
        assertEquals(3, toolCatalogColumns(560f, true, 1.3f))
        assertEquals(2, toolCatalogColumns(560f, true, 2f))
        assertEquals(1, toolCatalogColumns(260f, false, 1f))
        assertEquals(2, toolCatalogColumns(340f, false, 1f))
        assertEquals(3, toolCatalogColumns(900f, false, 1f))
    }

    @Test fun fourFiveAndSixWeekMonthsFitAnOrdinaryLandscapeViewport() {
        for (rows in 4..6) {
            val cell = landscapeCalendarCellHeight(250f, 16f, rows, 1f)
            assertTrue(cell in 36f..48f)
            assertTrue(16f + rows * (cell + 2f) <= 250.1f)
        }
    }

    @Test fun accessibilityTextIsNotShrunkToForceAShortWindowToFit() {
        for (rows in 4..6) for (font in listOf(1f, 1.3f, 2f)) {
            val cell = landscapeCalendarCellHeight(180f, 16f * font, rows, font)
            assertTrue(cell.isFinite())
            assertTrue(cell >= 32f * font + 4f)
        }
    }

    @Test fun rhythmDialLeavesAnInsetForThreeDigitScoresAtLargeFontScales() {
        for (shortWide in listOf(false, true)) for (font in listOf(1f, 1.3f, 2f)) {
            val size = if (shortWide) 24f else 32f
            assertTrue(rhythmDialDiameter(shortWide, false, font) - 40f >= 3f * .6f * size * font - .01f)
        }
        assertEquals(116f, rhythmDialDiameter(false, false, 1f), .001f)
        assertEquals(100f, rhythmDialDiameter(false, true, 1f), .001f)
    }

    @Test fun diceCounterLeavesTheMajorityOfLandscapeWidthForTheTable() {
        for (width in listOf(320f, 440f, 576f, 820f, 1100f)) {
            for (font in listOf(1f, 1.3f, 2f)) {
                val counter = diceLandscapeCounterWidth(width, font)
                assertTrue(counter.isFinite())
                assertTrue(counter <= width * .4f)
                assertTrue(width - counter - 12f >= width * .55f)
                assertTrue(counter >= 128f)
            }
        }
        assertEquals(138.24f, diceLandscapeCounterWidth(576f, 1f), .01f)
        assertEquals(160f, diceLandscapeCounterWidth(576f, 2f), .01f)
        assertEquals(188f, diceLandscapeCounterWidth(1100f, 1f), .01f)
        assertEquals(0f, diceLandscapeCounterWidth(0f, 1f), .01f)
    }

    @Test fun landscapeRailReservesIconGapAndPaddingBeyondTheActualLabelWidth() {
        for (label in listOf(34f, 36f, 47f, 72f, 90f)) {
            val width = landscapeRailWidth(label)
            assertTrue(width >= 80f)
            assertTrue(width - label >= 45f)
        }
        assertEquals(80f, landscapeRailWidth(34f), .01f)
        assertEquals(117f, landscapeRailWidth(72f), .01f)
    }

    @Test fun landscapeToolbarAllowsLargeTextButtonsWithoutCuttingTheirOutline() {
        for (line in listOf(20f, 26f, 40f, 48f)) {
            val height = landscapeToolbarHeight(line)
            assertTrue(height >= 48f)
            assertTrue(height - line >= 24f)
        }
        assertEquals(48f, landscapeToolbarHeight(20f), .01f)
        assertEquals(64f, landscapeToolbarHeight(40f), .01f)
    }
}
