package com.qxdzbc.p6.ui.common.view.dialog

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import com.qxdzbc.p6.ui.common.p6R
import com.qxdzbc.p6.ui.common.compose.TestApp
import com.qxdzbc.common.compose.view.MBox
import com.qxdzbc.p6.ui.theme.P6AllWhiteColors
import com.qxdzbc.p6.ui.theme.P6DefaultTypoGraphy


fun main() {
    TestApp() {
        MaterialTheme(colors = P6AllWhiteColors, typography = P6DefaultTypoGraphy) {
            MBox(modifier = Modifier.then(p6R.border.mod.black)) {
                Dialogs.SingleTextDialog ()
            }
        }
    }
}
