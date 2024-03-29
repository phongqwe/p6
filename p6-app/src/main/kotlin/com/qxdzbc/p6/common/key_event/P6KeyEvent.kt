package com.qxdzbc.p6.common.key_event

import androidx.compose.ui.input.key.KeyEvent
import com.qxdzbc.common.compose.key_event.MKeyEvent

/**
 * A wrapper for keyboard event
 */
interface P6KeyEvent : MKeyEvent {
    companion object{
        fun KeyEvent.toP6KeyEvent():P6KeyEvent{
            return P6KeyEventWrapper(this)
        }
    }
    /**
     * A key can be consumed by a range selector when it is either a range selector nav key or a range selector non-reactive key.
     */
    fun canBeConsumedByRangeSelector():Boolean

    /**
     * if a key is a navigation key that can change the position of a range selector. Including:
     *  - arrow keys
     *  - ctrl + shift + arrow keys
     *  - ctrl + arrow keys
     *  - shift + arrow keys
     *  - home key
     *  - end key
     */
    fun canMoveRangeSelector():Boolean

    /**
     * is a key a non-navigation key that does not change a range selector activation status. That means I can press these keys as much as I want and the range selector will not react. Including:
     *  - ctrl
     *  - shift
     *  - alt
     */
    fun isRangeSelectorNonReactiveKey():Boolean

    /**
     * is this key event a textual key event. A textual key event denotes:
     *  - a lower/upper case alphabet character
     *  - number
     *  - special character
     */
    fun isTextual():Boolean
}
