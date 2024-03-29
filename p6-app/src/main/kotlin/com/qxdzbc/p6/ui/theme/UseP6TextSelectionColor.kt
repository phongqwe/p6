package com.qxdzbc.p6.ui.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.qxdzbc.p6.ui.theme.color.UIColor

/**
 * The problem with the current compose lib is that it uses the primary color as text selection color.
 * My primary color is nearly white. This makes the text selection color invisible. So I must use this function to enforce a visible text selection color.
 */
@Composable
fun UseP6TextSelectionColor(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalTextSelectionColors provides UIColor.textSelectionColors) {
        content()
    }
}
