package com.zhifou.fortune

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnswerBookTest {
    @Test
    fun containsExactlyOneHundredContinuousPages() {
        assertEquals(100, AnswerBook.entries.size)
        assertEquals((1..100).toList(), AnswerBook.entries.map { it.page })
    }

    @Test
    fun everyPageHasUniqueNonBlankContent() {
        val entries = AnswerBook.entries

        assertEquals(entries.size, entries.map { it.answer }.distinct().size)
        assertEquals(entries.size, entries.map { it.advice }.distinct().size)
        assertTrue(entries.all { it.answer.isNotBlank() && it.advice.isNotBlank() })
    }
}
