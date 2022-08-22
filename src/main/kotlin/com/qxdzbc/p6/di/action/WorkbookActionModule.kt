package com.qxdzbc.p6.di.action

import com.qxdzbc.p6.app.action.workbook.add_ws.AddWorksheetAction
import com.qxdzbc.p6.app.action.workbook.add_ws.AddWorksheetActionImp
import com.qxdzbc.p6.app.action.workbook.delete_worksheet.DeleteWorksheetAction
import com.qxdzbc.p6.app.action.workbook.delete_worksheet.DeleteWorksheetActionImp
import com.qxdzbc.p6.app.action.workbook.new_worksheet.NewWorksheetAction
import com.qxdzbc.p6.app.action.workbook.new_worksheet.NewWorksheetActionImp
import com.qxdzbc.p6.app.action.workbook.rename_ws.RenameWorksheetAction
import com.qxdzbc.p6.app.action.workbook.rename_ws.RenameWorksheetActionImp
import com.qxdzbc.p6.app.action.workbook.set_active_ws.SetActiveWorksheetAction
import com.qxdzbc.p6.app.action.workbook.set_active_ws.SetActiveWorksheetActionImp
import com.qxdzbc.p6.di.P6Singleton
import com.qxdzbc.p6.app.action.workbook.WorkbookAction
import com.qxdzbc.p6.app.action.workbook.WorkbookActionImp
import com.qxdzbc.p6.app.action.workbook.click_on_ws_tab_item.SwitchWorksheetAction
import com.qxdzbc.p6.app.action.workbook.click_on_ws_tab_item.SwitchWorksheetActionImp
import dagger.Binds

@dagger.Module
interface WorkbookActionModule {
    @Binds
    @P6Singleton
    fun ClickOnWorksheetTabItem(i: SwitchWorksheetActionImp):SwitchWorksheetAction

    @Binds
    @P6Singleton
    fun SetActiveWorksheetAction(i: SetActiveWorksheetActionImp):SetActiveWorksheetAction

    @Binds
    @P6Singleton
    fun AddWorksheetAction(i: AddWorksheetActionImp): AddWorksheetAction

    @Binds
    @P6Singleton
    fun NewWorksheetAction(i: NewWorksheetActionImp): NewWorksheetAction

    @Binds
    @P6Singleton
    fun DeleteWorksheetAction(i: DeleteWorksheetActionImp): DeleteWorksheetAction

    @Binds
    @P6Singleton
    fun RenameWorksheetAction(i: RenameWorksheetActionImp): RenameWorksheetAction

    @Binds
    @P6Singleton
    fun WorkbookAction(i: WorkbookActionImp): WorkbookAction

}
