package com.zhifou.fortune

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

// Preserve IME composition and selection; only external edits move the caret to the end.
internal fun syncOracleEditorValue(editor: TextFieldValue, text: String): TextFieldValue =
    if (editor.text == text) editor else TextFieldValue(text, TextRange(text.length))
