package com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.action


/**
 * Action of a scroll bar. This layer serve as an easy-to-implement middle man, so that actions object and views can evolve independently.
 */
interface ScrollBarAction {
    fun runAction(data: ScrollBarActionData)
}