package com.zhifou.fortune

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.pow

class TarotAtelierTest {
    @Test fun compactAndPortraitLayoutsKeepFourDistinctNumberedPositions() {
        listOf(false, true).forEach { compact ->
            val anchors = (1..4).map { tarotAtelierAnchor(it, compact) }
            assertEquals(4, anchors.distinct().size)
            assertTrue(anchors.all { it.first in .1f.. .9f && it.second in .1f.. .9f })
        }
        assertEquals(tarotAtelierAnchor(1, false).second, tarotAtelierAnchor(2, false).second)
        assertEquals(tarotAtelierAnchor(3, false).second, tarotAtelierAnchor(4, false).second)
        assertTrue(tarotAtelierAnchor(1, false).second < tarotAtelierAnchor(3, false).second)
        assertThrows(IllegalArgumentException::class.java) { tarotAtelierAnchor(5, false) }
    }

    @Test fun primaryAndSecondaryTextRemainReadableInBothMaterials() {
        fun channel(v: Float) = if (v <= .04045f) v / 12.92f else ((v + .055f) / 1.055f).pow(2.4f)
        fun luminance(c: androidx.compose.ui.graphics.Color) = .2126f * channel(c.red) + .7152f * channel(c.green) + .0722f * channel(c.blue)
        listOf(false, true).forEach { light ->
            val c = tarotAtelierPalette(light)
            listOf(c.ink, c.muted).forEach { text ->
                val a = luminance(text)
                val b = luminance(c.background)
                val contrast = (maxOf(a, b) + .05f) / (minOf(a, b) + .05f)
                assertTrue("Text contrast $contrast", contrast >= 4.5f)
            }
        }
    }
}
