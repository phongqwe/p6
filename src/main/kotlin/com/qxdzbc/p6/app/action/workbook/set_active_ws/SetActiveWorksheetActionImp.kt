package com.qxdzbc.p6.app.action.workbook.set_active_ws

import com.qxdzbc.p6.app.action.workbook.set_active_ws.applier.SetActiveWorksheetApplier
import com.qxdzbc.p6.app.action.workbook.set_active_ws.rm.SetActiveWorksheetRM
import com.qxdzbc.p6.app.common.utils.RseNav
import javax.inject.Inject

class SetActiveWorksheetActionImp @Inject constructor(
    private val rm: SetActiveWorksheetRM,
    private val applier: SetActiveWorksheetApplier,
//    private val releaseWsFocusAct: ReleaseWsFocusAction,
//    @AppStateMs
//    val appStateMs:Ms<AppState>
) : SetActiveWorksheetAction {
//    var appState by appStateMs
    override fun setActiveWs(request: SetActiveWorksheetRequest): RseNav<SetActiveWorksheetResponse2> {
        val r1 = rm.setActiveWs(request)
        val r2 = applier.apply(r1)
        return r2
    }

    override fun setActiveWsUsingIndex(request: SetActiveWorksheetWithIndexRequest): RseNav<SetActiveWorksheetResponse2> {
        val r1 = rm.setActiveWsWithIndex(request)
        val r2 = applier.apply(r1)
        return r2
    }
}
