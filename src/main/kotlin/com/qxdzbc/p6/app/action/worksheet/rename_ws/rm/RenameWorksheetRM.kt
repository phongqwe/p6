package com.qxdzbc.p6.app.action.worksheet.rename_ws.rm

import com.qxdzbc.p6.app.action.worksheet.rename_ws.RenameWorksheetRequest
import com.qxdzbc.p6.app.action.worksheet.rename_ws.RenameWorksheetResponse

interface RenameWorksheetRM {
    fun renameWorksheet(request: RenameWorksheetRequest): RenameWorksheetResponse?
}
