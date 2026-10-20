package com.zhifou.fortune

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppNavigationTest {
    @Test
    fun `home leaves system Back to the activity`() {
        assertNull(mainBackDestination(Tab.Home))
    }

    @Test
    fun `other main destinations return to home after their child back handlers`() {
        for (tab in Tab.entries.filterNot { it == Tab.Home }) {
            assertEquals(Tab.Home, mainBackDestination(tab))
        }
    }
}
