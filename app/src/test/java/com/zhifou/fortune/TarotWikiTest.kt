package com.zhifou.fortune

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TarotWikiTest {
    @Test
    fun wikiCoversEveryCardWithCompleteOfflineSections() {
        val entries = TarotWikiLibrary.entries

        assertEquals(78, entries.size)
        assertEquals(78, entries.map { it.card.id }.distinct().size)
        entries.forEach { entry ->
            assertTrue(entry.deckRole.isNotBlank())
            assertTrue(entry.artworkAndSymbols.contains(entry.card.imageDescription))
            assertTrue(entry.usage.isNotBlank())
            assertTrue(entry.interpretation.contains(entry.card.uprightMeaning))
            assertTrue(entry.interpretation.contains(entry.card.reversedMeaning))
            assertTrue(entry.historicalBackground.isNotBlank())
            assertTrue(entry.distinction.isNotBlank())
            assertEquals(3, entry.reflectionPrompts.size)
        }
    }

    @Test
    fun wikiFiltersMatchTheCanonicalDeckStructure() {
        assertEquals(78, TarotWikiLibrary.search("", TarotWikiFilter.ALL).size)
        assertEquals(22, TarotWikiLibrary.search("", TarotWikiFilter.MAJOR).size)
        assertEquals(14, TarotWikiLibrary.search("", TarotWikiFilter.WANDS).size)
        assertEquals(14, TarotWikiLibrary.search("", TarotWikiFilter.CUPS).size)
        assertEquals(14, TarotWikiLibrary.search("", TarotWikiFilter.SWORDS).size)
        assertEquals(14, TarotWikiLibrary.search("", TarotWikiFilter.PENTACLES).size)
    }

    @Test
    fun wikiSearchMatchesChineseEnglishKeywordsAndMeanings() {
        assertEquals(
            "major_00",
            TarotWikiLibrary.search("The Fool", TarotWikiFilter.ALL).single().card.id,
        )
        assertTrue(
            TarotWikiLibrary.search("圣杯二", TarotWikiFilter.CUPS)
                .any { it.card.id == "cups_02" },
        )
        assertTrue(
            TarotWikiLibrary.search("公平", TarotWikiFilter.ALL)
                .any { it.card.id == "major_11" },
        )
    }
}
