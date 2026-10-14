package com.zhifou.fortune

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeContrastTest {
    @Test
    fun bodyAndSecondaryCopyRemainReadableInBothThemes() {
        listOf(LightPalette, DarkPalette).forEach { palette ->
            listOf(palette.ink, palette.panel, palette.panelAlt).forEach { background ->
                assertContrast(palette.textMain, background, 7f)
                assertContrast(palette.textSub, background, 4.5f)
            }
        }
    }

    @Test
    fun chatAndPrimaryActionUseAccessibleColorPairs() {
        listOf(LightPalette, DarkPalette).forEach { palette ->
            assertContrast(palette.chatText, palette.chatBubbleAssistant, 4.5f)
            assertContrast(palette.chatText, palette.chatBubbleUser, 4.5f)
            assertContrast(palette.onAccentFill, palette.accentFill, 4.5f)
        }
    }

    private fun assertContrast(foreground: Color, background: Color, minimum: Float) {
        val a = foreground.luminance()
        val b = background.luminance()
        val ratio = (maxOf(a, b) + 0.05f) / (minOf(a, b) + 0.05f)
        assertTrue("Contrast $ratio is below $minimum", ratio >= minimum)
    }
}
