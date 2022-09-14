package com.qxdzbc.p6.app.action.cell_editor.open_cell_editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.p6.app.action.common_data_structure.WbWs
import com.qxdzbc.common.Rse
import com.qxdzbc.p6.app.document.cell.CellErrors
import com.qxdzbc.p6.app.document.cell.d.Cell
import com.qxdzbc.p6.di.state.app_state.AppStateMs
import com.qxdzbc.p6.di.state.app_state.DocumentContainerSt
import com.qxdzbc.p6.di.state.app_state.SubAppStateContainerSt
import com.qxdzbc.p6.ui.app.error_router.ErrorRouter
import com.qxdzbc.p6.ui.app.state.AppState
import com.qxdzbc.p6.ui.app.state.DocumentContainer
import com.qxdzbc.p6.ui.app.state.SubAppStateContainer
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.St
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import javax.inject.Inject

class OpenCellEditorImp @Inject constructor(
    @SubAppStateContainerSt
    val stateContMs:St<@JvmSuppressWildcards SubAppStateContainer>,
    @DocumentContainerSt
    val docContSt:St<@JvmSuppressWildcards DocumentContainer>,
    @AppStateMs
    val appStateMs:Ms<AppState>,
    val errorRouter: ErrorRouter,
) : OpenCellEditorAction {
    val appState by appStateMs
    val docCont by docContSt
    val stateCont by stateContMs
    override fun openCellEditor(wsId: WbWs) {
        val ws = docCont.getWs(wsId)
        val cursorStateMs = stateCont.getCursorStateMs(wsId)
        if(ws!=null && cursorStateMs!=null){
            val cursorState by cursorStateMs
            var cellEditorState by appState.cellEditorStateMs
            if(!cellEditorState.isActive){
                val cursorMainCell: Rse<Cell> = ws.getCellOrDefaultRs(cursorState.mainCell)
                val windowStateMs = appState.getWindowStateMsByWbKey(wsId.wbKey)
                val windowId = windowStateMs?.value?.id
                val fcsMs = windowStateMs?.value?.focusStateMs
                cursorMainCell.onSuccess { cell->
                    if (cell.isEditable) {
                        if(fcsMs!=null){
                            fcsMs.value =fcsMs.value.focusOnEditor()
                        }
                        val cellText = cell.editableValue(cursorState.wbKey,cursorState.wsName)
                        cellEditorState = cellEditorState
                            .setCurrentText(cellText)
                            .setEditTarget(cursorState.mainCell)
                            .open(cursorState.idMs)
                    } else {
                        errorRouter.publishToWindow(CellErrors.NotEditable.report(cell.address), windowId)
                    }
                }.onFailure {
                    errorRouter.publishToWindow(it, windowId)
                }
            }
        }
    }
}
