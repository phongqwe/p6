package com.qxdzbc.p6.ui.common.view.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.qxdzbc.common.compose.view.MBox
import com.qxdzbc.p6.ui.common.compose.P6TestApp
import com.qxdzbc.p6.ui.common.view.BorderBox
import com.qxdzbc.p6.ui.common.view.BorderStyle
import com.qxdzbc.p6.ui.theme.P6AllWhiteColors
import com.qxdzbc.p6.ui.theme.P6DefaultTypoGraphy

@Composable
fun DraggableDialog(
    title: String = "",
    size: DpSize = DpSize(100.dp, 100.dp),
    onCloseRequest: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {},
) {

    DraggableDialog(
        title = {
            if (title.isNotEmpty()) {
                BasicText(title, modifier = Modifier.align(Alignment.Center).padding(top = 10.dp, bottom = 5.dp))
            }
        },
        size, onCloseRequest, content
    )
}

/**
 * A custom, empty dialog.
 */
@Composable
fun DraggableDialog(
    title: @Composable BoxScope.() -> Unit = {},
    size: DpSize = DpSize(100.dp, 100.dp),
    onCloseRequest: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {},
) {
    Dialog(
        onCloseRequest = onCloseRequest,
        state = DialogState(size = size),
        resizable = false, title = "",
        undecorated = true,
    ) {

        Surface {
            WindowDraggableArea(modifier = Modifier.fillMaxSize()) {}
            Column(modifier = Modifier.fillMaxSize()) {
                BorderBox(style = BorderStyle.NONE, modifier = Modifier.fillMaxWidth()) {
                    title()
                }
                MBox(modifier = Modifier.fillMaxWidth().weight(1.0F)) {
                    content()
                }
            }
        }
    }
}

fun main() = P6TestApp {
    MaterialTheme(colors = P6AllWhiteColors, typography = P6DefaultTypoGraphy) {
        DraggableDialog("Title ABC") {
            Text("QWE")
        }
    }
}
