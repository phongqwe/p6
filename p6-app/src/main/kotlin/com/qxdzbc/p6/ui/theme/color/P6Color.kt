package com.qxdzbc.p6.ui.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Color theme of the app
 */
@Immutable
class P6Color(
    val ws: WsColor = WsColor(),
    val uiColor: UIColor = UIColor(),
){
    companion object{
        fun light():P6Color{
            return P6Color()
        }
        fun dark():P6Color{
            return P6Color(
                ws = WsColor.dark(),
                uiColor = UIColor.dark()
            )
        }
        val local = staticCompositionLocalOf(
            defaultFactory = {
                light()
            }
        )
    }
}