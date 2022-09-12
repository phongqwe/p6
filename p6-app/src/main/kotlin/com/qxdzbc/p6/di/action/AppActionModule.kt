package com.qxdzbc.p6.di.action

import com.qxdzbc.p6.app.action.app.close_wb.CloseWbAction
import com.qxdzbc.p6.app.action.app.close_wb.CloseWbActionImp
import com.qxdzbc.p6.app.action.app.close_wb.applier.CloseWorkbookApplier
import com.qxdzbc.p6.app.action.app.close_wb.applier.CloseWorkbookApplierImp
import com.qxdzbc.p6.app.action.app.close_wb.applier.CloseWorkbookInternalApplier
import com.qxdzbc.p6.app.action.app.close_wb.applier.CloseWorkbookInternalApplierImp
import com.qxdzbc.p6.app.action.app.create_new_wb.CreateNewWorkbookAction
import com.qxdzbc.p6.app.action.app.create_new_wb.CreateNewWorkbookActionImp
import com.qxdzbc.p6.app.action.app.create_new_wb.applier.CreateNewWorkbookApplier
import com.qxdzbc.p6.app.action.app.create_new_wb.applier.CreateNewWorkbookApplierImp
import com.qxdzbc.p6.app.action.app.create_new_wb.applier.CreateNewWorkbookInternalApplier
import com.qxdzbc.p6.app.action.app.create_new_wb.applier.CreateNewWorkbookInternalApplierImp
import com.qxdzbc.p6.app.action.app.load_wb.LoadWorkbookAction
import com.qxdzbc.p6.app.action.app.load_wb.LoadWorkbookActionImp
import com.qxdzbc.p6.app.action.app.load_wb.applier.LoadWorkbookApplier
import com.qxdzbc.p6.app.action.app.load_wb.applier.LoadWorkbookApplierImp
import com.qxdzbc.p6.app.action.app.load_wb.applier.LoadWorkbookInternalApplier
import com.qxdzbc.p6.app.action.app.load_wb.applier.LoadWorkbookInternalApplierImp
import com.qxdzbc.p6.app.action.app.restart_kernel.applier.RestartKernelApplier
import com.qxdzbc.p6.app.action.app.restart_kernel.applier.RestartKernelApplierImp
import com.qxdzbc.p6.app.action.app.save_wb.SaveWorkbookAction
import com.qxdzbc.p6.app.action.app.save_wb.SaveWorkbookActionImp
import com.qxdzbc.p6.app.action.app.save_wb.applier.SaveWorkbookApplier
import com.qxdzbc.p6.app.action.app.save_wb.applier.SaveWorkbookApplierImp
import com.qxdzbc.p6.app.action.app.save_wb.applier.SaveWorkbookInternalApplier
import com.qxdzbc.p6.app.action.app.save_wb.applier.SaveWorkbookInternalApplierImp
import com.qxdzbc.p6.app.action.app.set_active_wb.SetActiveWorkbookAction
import com.qxdzbc.p6.app.action.app.set_active_wb.SetActiveWorkbookActionImp
import com.qxdzbc.p6.app.action.app.set_wbkey.SetWorkbookKeyAction
import com.qxdzbc.p6.app.action.app.set_wbkey.SetWorkbookKeyActionImp
import com.qxdzbc.p6.app.action.app.set_wbkey.applier.SetWbKeyApplier
import com.qxdzbc.p6.app.action.app.set_wbkey.applier.SetWbKeyApplierImp
import com.qxdzbc.p6.app.action.app.set_wbkey.rm.SetWbKeyRM
import com.qxdzbc.p6.app.action.app.set_wbkey.rm.SetWbKeyRMImp
import com.qxdzbc.p6.app.action.workbook.set_active_ws.applier.SetActiveWorksheetApplier
import com.qxdzbc.p6.app.action.workbook.set_active_ws.applier.SetActiveWorksheetApplierImp
import com.qxdzbc.p6.di.P6Singleton
import dagger.Binds

@dagger.Module
interface AppActionModule {
    @Binds
    @P6Singleton
    fun LoadWorkbookAction(i: LoadWorkbookActionImp):LoadWorkbookAction

    @Binds
    @P6Singleton
    fun SaveWorkbookAction(i: SaveWorkbookActionImp): SaveWorkbookAction

    @Binds
    @P6Singleton
    fun SetActiveWorkbookAction(i: SetActiveWorkbookActionImp):SetActiveWorkbookAction

    @Binds
    @P6Singleton
    fun NewWorkbookAction(i:CreateNewWorkbookActionImp): CreateNewWorkbookAction

    @Binds
    @P6Singleton
    fun CloseWbAction(i:CloseWbActionImp): CloseWbAction

    @Binds
    @P6Singleton
    fun SetWbKeyAction(i: SetWorkbookKeyActionImp): SetWorkbookKeyAction

    @Binds
    @P6Singleton
    fun SetWbKeyApplier(i: SetWbKeyApplierImp): SetWbKeyApplier

    @Binds
    @P6Singleton
    fun SetWbKeyRM(i: SetWbKeyRMImp): SetWbKeyRM

    @Binds
    @P6Singleton
    fun RestartKernelApplier(i: RestartKernelApplierImp): RestartKernelApplier


    @Binds
    @P6Singleton
    fun CreateNewWorkbookInternalApplier(i: CreateNewWorkbookInternalApplierImp): CreateNewWorkbookInternalApplier

    @Binds
    @P6Singleton
    fun CreateNewWorkbookApplier(i: CreateNewWorkbookApplierImp): CreateNewWorkbookApplier

    @Binds
    @P6Singleton
    fun SaveWorkbookInternalApplier(i: SaveWorkbookInternalApplierImp): SaveWorkbookInternalApplier

    @Binds
    @P6Singleton
    fun SaveWorkbookApplier(i: SaveWorkbookApplierImp): SaveWorkbookApplier

    @Binds
    @P6Singleton
    fun SetActiveWorksheetApplier(i: SetActiveWorksheetApplierImp): SetActiveWorksheetApplier

    @Binds
    @P6Singleton
    fun LoadWorkbookInternalApplier(i: LoadWorkbookInternalApplierImp): LoadWorkbookInternalApplier

    @Binds
    @P6Singleton
    fun LoadWorkbookApplier(i: LoadWorkbookApplierImp): LoadWorkbookApplier

    @Binds
    @P6Singleton
    fun CloseWorkbookInternalApplier(i: CloseWorkbookInternalApplierImp): CloseWorkbookInternalApplier

    @Binds
    @P6Singleton
    fun CloseWorkbookApplier(i: CloseWorkbookApplierImp): CloseWorkbookApplier
}
