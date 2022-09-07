package com.qxdzbc.p6.di.action

import com.qxdzbc.p6.app.action.P6ResponseLegalityChecker
import com.qxdzbc.p6.app.action.P6ResponseLegalityCheckerImp
import com.qxdzbc.p6.di.P6Singleton
import com.qxdzbc.p6.ui.document.cell.action.UpdateCellAction
import com.qxdzbc.p6.ui.document.cell.action.UpdateCellActionImp
import dagger.Binds

@dagger.Module(
    includes = [
        AppActionModule::class,
        GlobalActionModule::class,
        WorkbookActionModule::class,
        WorksheetActionModule::class,
        RangeActionModule::class,
        WindowActionModule::class,
        CellEditorActionModule::class,
        CellActionModule::class,
    ]
)
interface ActionModule {

    @Binds
    @P6Singleton
    fun CellAction(i:UpdateCellActionImp):UpdateCellAction

    @Binds
    @P6Singleton
    fun P6ResponseLegalityCheckerImp(i: P6ResponseLegalityCheckerImp): P6ResponseLegalityChecker

}
