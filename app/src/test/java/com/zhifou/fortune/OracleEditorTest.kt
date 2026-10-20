package com.zhifou.fortune

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import org.junit.Assert.*
import org.junit.Test

class OracleEditorTest {
    @Test fun typingPreservesSelectionAndComposition() {
        val text = "A long question ".repeat(100)
        val editor = TextFieldValue(text, TextRange(text.length), TextRange(text.length - 3, text.length))
        assertSame(editor, syncOracleEditorValue(editor, text))
    }

    @Test fun cursorSelectionIsNotResetWhenTextIsUnchanged() {
        val editor = TextFieldValue("a question", TextRange(2, 6))
        assertEquals(TextRange(2, 6), syncOracleEditorValue(editor, editor.text).selection)
    }

    @Test fun voiceAndRecommendationEditsPlaceCaretAtEnd() {
        val result = syncOracleEditorValue(TextFieldValue(""), "新的问题")
        assertEquals(TextRange(4), result.selection)
        assertNull(result.composition)
    }

    @Test fun clearingQuestionResetsCaretSafely() {
        val result = syncOracleEditorValue(TextFieldValue("question", TextRange(8)), "")
        assertEquals(TextRange.Zero, result.selection)
    }

    @Test fun transcriptionBusyIsNotReportedAsMicrophoneFailure() {
        assertTrue(SpeechStartResult.Transcribing.message!!.contains("正在转写"))
        assertFalse(SpeechStartResult.Transcribing.message!!.contains("麦克风"))
        assertNull(SpeechStartResult.Started.message)
    }
}
