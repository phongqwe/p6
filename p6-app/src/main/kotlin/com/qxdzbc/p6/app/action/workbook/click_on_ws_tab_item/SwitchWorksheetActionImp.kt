package com.qxdzbc.p6.app.action.workbook.click_on_ws_tab_item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.p6.app.action.workbook.set_active_ws.SetActiveWorksheetAction
import com.qxdzbc.p6.app.action.workbook.set_active_ws.SetActiveWorksheetRequest
import com.qxdzbc.p6.app.action.workbook.set_active_ws.SetActiveWorksheetResponse2
import com.qxdzbc.p6.app.action.worksheet.release_focus.RestoreWindowFocusState
import com.qxdzbc.p6.app.common.utils.RseNav

import com.qxdzbc.p6.ui.app.state.AppState
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.p6.di.P6Singleton
import com.qxdzbc.p6.di.anvil.P6AnvilScope
import com.qxdzbc.p6.ui.app.state.SubAppStateContainer
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
@P6Singleton
@ContributesBinding(P6AnvilScope::class)
class SwitchWorksheetActionImp @Inject constructor(
    val setActiveWorksheetAction: SetActiveWorksheetAction,
    val restoreWindowFocusAction: RestoreWindowFocusState,
    val appState:AppState,
    val stateContMs: Ms<SubAppStateContainer>,
) : SwitchWorksheetAction {

    private var sc by stateContMs

    override fun switchToWorksheet(request: SetActiveWorksheetRequest): RseNav<SetActiveWorksheetResponse2> {
        restoreWindowFocusAction.setFocusStateConsideringRangeSelector(request.wbKey)
        var cellEditorState by appState.cellEditorStateMs
        if(cellEditorState.isOpen && cellEditorState.allowRangeSelector){
            sc.getCursorStateMs(request)?.also {
                cellEditorState = cellEditorState.setRangeSelectorId(it.value.idMs)
            }
        }else{
            cellEditorState = cellEditorState.close()
        }
        val rt = setActiveWorksheetAction.setActiveWs(request)
        return rt
    }
}
