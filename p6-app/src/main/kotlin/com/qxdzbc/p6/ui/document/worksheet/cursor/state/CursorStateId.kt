package com.qxdzbc.p6.ui.document.worksheet.cursor.state

import androidx.compose.runtime.State
import com.qxdzbc.common.compose.St
import com.qxdzbc.p6.app.action.common_data_structure.WbWsSt
import com.qxdzbc.p6.ui.document.worksheet.state.WorksheetId

/**
 * For identifying a cursor
 */
interface CursorStateId : WbWsSt{
    val wsStateIDMs: St<WorksheetId>
    fun setWsStateIdSt(wsStateIDSt: State<WorksheetId>):CursorStateId
}
