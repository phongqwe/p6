package com.qxdzbc.p6.ui.worksheet.action

import com.qxdzbc.p6.ui.worksheet.di.WsAnvilScope
import com.qxdzbc.p6.ui.worksheet.di.WsScope
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.action.ScrollBarAction
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject


@WsScope
@ContributesBinding(scope= WsAnvilScope::class)
class WorksheetLocalActionsImp @Inject constructor(
    override val verticalScrollBarAction: ScrollBarAction,
    override val horizontalScrollBarAction: ScrollBarAction,
) : WorksheetLocalActions