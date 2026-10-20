package com.zhifou.fortune

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MineNavigationTest {
    @Test fun rootHasNoParent() {
        assertNull(MinePage.Overview.parent)
    }

    @Test fun recordsAndSettingsReturnToMine() {
        assertEquals(MinePage.Overview, MinePage.History.parent)
        assertEquals(MinePage.Overview, MinePage.Settings.parent)
    }

    @Test fun eachSettingsDetailReturnsToSettingsInsteadOfMine() {
        listOf(MinePage.Appearance, MinePage.Interaction, MinePage.Ai, MinePage.Speech, MinePage.Privacy).forEach {
            assertEquals(MinePage.Settings, it.parent)
        }
    }

    @Test fun everyDestinationHasAnAcyclicPathToRoot() {
        MinePage.entries.forEach { start ->
            val visited = mutableSetOf<MinePage>()
            var current: MinePage? = start
            while (current != null) {
                assertTrue("Navigation loop at $current", visited.add(current))
                current = current.parent
            }
            assertTrue(visited.contains(MinePage.Overview))
        }
    }
}
