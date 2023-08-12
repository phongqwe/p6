package com.qxdzbc.p6.composite_actions.rpc

import com.qxdzbc.p6.composite_actions.app.close_wb.CloseWorkbookAction
import com.qxdzbc.p6.composite_actions.app.create_new_wb.CreateNewWorkbookAction
import com.qxdzbc.p6.composite_actions.app.get_wb.GetWorkbookAction
import com.qxdzbc.p6.composite_actions.app.load_wb.LoadWorkbookAction
import com.qxdzbc.p6.composite_actions.app.save_wb.SaveWorkbookAction
import com.qxdzbc.p6.composite_actions.app.set_active_wb.SetActiveWorkbookAction
import com.qxdzbc.p6.composite_actions.app.set_wbkey.ReplaceWorkbookKeyAction
import com.qxdzbc.p6.composite_actions.workbook.create_new_ws.CreateNewWorksheetAction
import com.qxdzbc.p6.composite_actions.workbook.delete_worksheet.DeleteWorksheetAction
import com.qxdzbc.p6.composite_actions.workbook.new_worksheet.NewWorksheetAction
import com.qxdzbc.p6.composite_actions.workbook.rename_ws.RenameWorksheetAction
import com.qxdzbc.p6.composite_actions.workbook.set_active_ws.SetActiveWorksheetAction

/**
 * A collection of actions for rpc services.
 * This serve as an aggregation point and must not have any functions of its own
 */
interface AppRpcActions:
    CreateNewWorksheetAction, ReplaceWorkbookKeyAction,
    NewWorksheetAction, DeleteWorksheetAction,
    RenameWorksheetAction, SetActiveWorksheetAction,
    CreateNewWorkbookAction, SetActiveWorkbookAction,
    SaveWorkbookAction,LoadWorkbookAction, CloseWorkbookAction, GetWorkbookAction


