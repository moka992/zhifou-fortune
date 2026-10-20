package com.zhifou.fortune

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * A form control with one container and a focus outline sharing identical bounds.
 */
@Composable
internal fun GeminiGlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    shape: Shape = MaterialTheme.shapes.large,
    tone: GeminiGlassTone = GeminiGlassTone.Neutral,
) {
    val palette = LocalFortunePalette.current
    val colors = MaterialTheme.colorScheme
    var focused by remember { mutableStateOf(false) }
    val outlineAlpha by animateFloatAsState(
        targetValue = when {
            isError && focused -> 0.86f
            isError -> 0.62f
            focused -> 0.76f
            else -> 0.45f
        },
        animationSpec = tween(durationMillis = 150),
        label = "glassTextFieldFocus",
    )
    val outlineColor = when {
        isError -> colors.error
        focused -> colors.primary
        else -> colors.outlineVariant
    }
    val activeTone = when {
        isError -> GeminiGlassTone.Rose
        focused -> GeminiGlassTone.Neutral
        else -> tone
    }

    Box(modifier = modifier) {
        GeminiGlassPanel(
            tone = activeTone,
            shape = shape,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused },
                enabled = enabled,
                readOnly = readOnly,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                supportingText = supportingText,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                minLines = minLines,
                maxLines = maxLines,
                shape = shape,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = palette.textMain,
                    unfocusedTextColor = palette.textMain,
                    disabledTextColor = palette.textSub,
                    errorTextColor = palette.textMain,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    cursorColor = colors.secondary,
                    errorCursorColor = colors.error,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedLabelColor = colors.secondary,
                    unfocusedLabelColor = palette.textSub,
                    disabledLabelColor = palette.textSub.copy(alpha = 0.62f),
                    errorLabelColor = colors.error,
                    focusedPlaceholderColor = palette.textSub,
                    unfocusedPlaceholderColor = palette.textSub,
                    focusedSupportingTextColor = palette.textSub,
                    unfocusedSupportingTextColor = palette.textSub,
                    errorSupportingTextColor = colors.error,
                ),
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = 1.5.dp,
                    color = outlineColor.copy(alpha = outlineAlpha),
                    shape = shape,
                ),
        )
    }
}
