package com.qxdzbc.p6.app.common.utils.key_event

import androidx.compose.ui.input.key.*
import com.qxdzbc.p6.ui.common.compose.KeyEventUtils.isCtrlPressedAlone
import com.qxdzbc.p6.ui.common.compose.KeyEventUtils.isCtrlShiftPressed
import com.qxdzbc.p6.ui.common.compose.KeyEventUtils.isShiftPressedAlone

data class PKeyEventImp(override val keyEvent: KeyEvent) : AbsPKeyEvent() {
    override val key: Key
        get() = keyEvent.key
    override val isCtrlShiftPressed: Boolean
        get() = keyEvent.isCtrlShiftPressed
    override val isShiftPressedAlone: Boolean
        get() = keyEvent.isShiftPressedAlone
    override val isCtrlPressedAlone: Boolean
        get() = keyEvent.isCtrlPressedAlone
    override val type: KeyEventType
        get() = keyEvent.type
}
