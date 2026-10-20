package com.zhifou.fortune

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
internal data class DailyInsightColors(val container: Color, val accent: Color, val content: Color)

internal fun ColorScheme.dailyInsightColors(category: DailyInsightCategory): DailyInsightColors = when (category) {
    DailyInsightCategory.Career -> DailyInsightColors(secondaryContainer, secondary, onSecondaryContainer)
    DailyInsightCategory.Relationship -> DailyInsightColors(tertiaryContainer, tertiary, onTertiaryContainer)
    DailyInsightCategory.Finance -> DailyInsightColors(primaryContainer, primary, onPrimaryContainer)
}
