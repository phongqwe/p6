package com.qxdzbc.p6.ui.document.worksheet.cursor.thumb.action

import androidx.compose.ui.geometry.Offset
import com.qxdzbc.p6.app.action.common_data_structure.WbWsSt
import com.qxdzbc.p6.app.document.cell.address.CellAddress

interface DragThumbAction {
    fun startDrag_forTest(wbws: WbWsSt, cellAddress: CellAddress)
    fun startDrag(wbws: WbWsSt, mouseWindowOffset:Offset)

    fun drag_forTest(wbws: WbWsSt, cellAddress: CellAddress)
    fun drag(wbws: WbWsSt, mouseWindowOffset:Offset)

    fun endDrag_forTest(wbws: WbWsSt, cellAddress: CellAddress)
    fun endDrag(wbws: WbWsSt, mouseWindowOffset:Offset)
}
