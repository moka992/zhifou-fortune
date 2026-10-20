package com.zhifou.fortune

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.*
import org.junit.Test

class DailyInsightColorsTest {
    @Test fun categoriesUseTheSameSemanticRolesForCardsAndDetails() {
        val scheme = lightColorScheme(
            primaryContainer = Color(0xFFCCEEDD), primary = Color(0xFF115533), onPrimaryContainer = Color(0xFF113322),
            secondaryContainer = Color(0xFFDDEEFF), secondary = Color(0xFF335577), onSecondaryContainer = Color(0xFF112244),
            tertiaryContainer = Color(0xFFFFDDDD), tertiary = Color(0xFF994444), onTertiaryContainer = Color(0xFF442222),
        )
        assertEquals(DailyInsightColors(scheme.secondaryContainer, scheme.secondary, scheme.onSecondaryContainer), scheme.dailyInsightColors(DailyInsightCategory.Career))
        assertEquals(DailyInsightColors(scheme.tertiaryContainer, scheme.tertiary, scheme.onTertiaryContainer), scheme.dailyInsightColors(DailyInsightCategory.Relationship))
        assertEquals(DailyInsightColors(scheme.primaryContainer, scheme.primary, scheme.onPrimaryContainer), scheme.dailyInsightColors(DailyInsightCategory.Finance))
    }

    @Test fun categoryColorsFollowTheThemeWithoutTranslucentOverlays() {
        val light = lightColorScheme()
        val dark = darkColorScheme()
        for (category in DailyInsightCategory.entries) {
            val day = light.dailyInsightColors(category)
            val night = dark.dailyInsightColors(category)
            assertNotEquals(day.container, night.container)
            assertEquals(1f, day.container.alpha, .001f)
            assertEquals(1f, night.container.alpha, .001f)
        }
    }

    @Test fun categoryRolesRemainStableWhenCardsAreReordered() {
        val scheme = lightColorScheme()
        val categories = listOf(DailyInsightCategory.Finance, DailyInsightCategory.Career, DailyInsightCategory.Relationship)
        assertEquals(listOf(scheme.primaryContainer, scheme.secondaryContainer, scheme.tertiaryContainer),
            categories.map { scheme.dailyInsightColors(it).container })
    }

    @Test fun containerTextUsesItsMatchedReadableContentColor() {
        for (scheme in listOf(lightColorScheme(), darkColorScheme())) {
            for (category in DailyInsightCategory.entries) {
                val palette = scheme.dailyInsightColors(category)
                val light = maxOf(palette.container.luminance(), palette.content.luminance())
                val dark = minOf(palette.container.luminance(), palette.content.luminance())
                assertTrue((light + .05f) / (dark + .05f) >= 4.5f)
            }
        }
    }
}
