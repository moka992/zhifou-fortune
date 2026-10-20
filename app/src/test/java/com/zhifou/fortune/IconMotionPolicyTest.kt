package com.zhifou.fortune

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IconMotionPolicyTest {
    @Test fun primaryFeaturesUseDifferentSemanticArtwork() {
        val examples = mapOf(
            "Outlined.Home" to SemanticIconKind.Home,
            "Outlined.CalendarMonth" to SemanticIconKind.Calendar,
            "Filled.AutoAwesome" to SemanticIconKind.Sparkles,
            "Outlined.GridView" to SemanticIconKind.Tools,
            "Outlined.PersonOutline" to SemanticIconKind.Profile,
            "Filled.Book" to SemanticIconKind.Book,
            "Filled.Toll" to SemanticIconKind.Coins,
            "Filled.Style" to SemanticIconKind.Tarot,
            "Filled.Casino" to SemanticIconKind.Dice,
            "Outlined.AccountBalanceWallet" to SemanticIconKind.Wallet,
            "Outlined.WorkOutline" to SemanticIconKind.Work,
            "Outlined.FavoriteBorder" to SemanticIconKind.Heart,
            "Filled.Settings" to SemanticIconKind.Settings,
            "Filled.Schedule" to SemanticIconKind.Clock,
            "Filled.Edit" to SemanticIconKind.Pencil,
            "Filled.Search" to SemanticIconKind.Search,
            "Filled.Mic" to SemanticIconKind.Mic,
            "Filled.Vibration" to SemanticIconKind.Haptics,
            "Filled.Wallpaper" to SemanticIconKind.Picture,
            "Filled.Shield" to SemanticIconKind.Shield,
            "Filled.CheckCircle" to SemanticIconKind.Check,
            "AutoMirrored.Filled.Send" to SemanticIconKind.Send,
        )
        examples.forEach { (name, kind) -> assertEquals(name, kind, semanticIconKind(name)) }
        assertEquals(examples.size, examples.values.toSet().size)
    }

    @Test fun warningsDestructiveActionsAndUnrecognizedSymbolsStayUnambiguous() {
        listOf("Filled.Delete", "Filled.Info", "Filled.Warning", "Filled.Error", "Filled.Close",
            "Filled.Visibility", "Filled.VisibilityOff", "CustomBrand.Mark").forEach {
            assertEquals(SemanticIconKind.None, semanticIconKind(it))
        }
    }

    @Test fun inactiveDisabledReducedAndBackgroundIconsDoNotAnimate() {
        for (kind in SemanticIconKind.entries) {
            assertFalse(shouldAnimateSemanticIcon(kind, false, false, true))
            assertFalse(shouldAnimateSemanticIcon(kind, true, true, true))
            assertFalse(shouldAnimateSemanticIcon(kind, true, false, false))
        }
        assertTrue(shouldAnimateSemanticIcon(SemanticIconKind.Send, true, false, true))
        assertFalse(shouldAnimateSemanticIcon(SemanticIconKind.None, true, false, true))
    }

    @Test fun independentPartTimelinesAreBoundedAndEndPrecisely() {
        val samples = (-10..110).map { it / 100f } + listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)
        samples.forEach { t ->
            assertTrue(iconStage(t, .2f, .8f) in 0f..1f)
        }
        assertEquals(1f, iconStage(1f, .2f, .8f), 0f)
        assertTrue(SEMANTIC_ICON_DURATION_MS in 500..900)
    }
}
