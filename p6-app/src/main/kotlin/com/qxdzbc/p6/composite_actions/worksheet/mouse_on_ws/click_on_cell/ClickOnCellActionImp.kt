package com.qxdzbc.p6.composite_actions.worksheet.mouse_on_ws.click_on_cell

import androidx.compose.runtime.getValue
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWs
import com.qxdzbc.p6.composite_actions.cell_editor.update_range_selector_text.RefreshRangeSelectorText
import com.qxdzbc.p6.composite_actions.worksheet.release_focus.RestoreWindowFocusState
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress


import com.qxdzbc.p6.ui.app.state.AppState
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWsSt
import com.qxdzbc.p6.di.P6AnvilScope
import com.qxdzbc.p6.composite_actions.cell_editor.run_formula.RunFormulaOrSaveValueToCellAction
import com.qxdzbc.p6.composite_actions.cursor.on_cursor_changed_reactor.CommonSideEffectWhenCursorChanged
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.qxdzbc.p6.ui.worksheet.cursor.state.CursorState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(P6AnvilScope::class)
class ClickOnCellActionImp @Inject constructor(
    val appState:AppState,
    private val stateCont: StateContainer,
    private val restoreWindowFocusState: RestoreWindowFocusState,
    private val refreshRangeSelectorText: RefreshRangeSelectorText,
    private val runFormulaAction: RunFormulaOrSaveValueToCellAction,
    private val commonSideEffectWhenCursorChanged: CommonSideEffectWhenCursorChanged,
) : ClickOnCellAction {

    fun clickOnCell(cellAddress: CellAddress, cursorStateMs:Ms<CursorState>?) {
        cursorStateMs?.also {
            val editorStateMs = appState.cellEditorStateMs
            val editorState by editorStateMs
            val cursorState by cursorStateMs
            val cellEditorState by cursorState.cellEditorStateMs
            restoreWindowFocusState.setFocusStateConsideringRangeSelector(cursorState.wbKey)
            cursorStateMs.value = cursorState
                .setMainCell(cellAddress)
                .removeMainRange()
                .removeAllSelectedFragRange()
                .removeAllFragmentedCells()
            val rangeSelectorIsActivated:Boolean = cellEditorState.isOpen && cellEditorState.rangeSelectorAllowState.isAllowedOrAllowMouse()
            if (rangeSelectorIsActivated) {
                editorStateMs.value = editorState.setRangeSelectorId(cursorState.idMs)
                refreshRangeSelectorText.refreshRangeSelectorTextInCurrentCellEditor()
            }else{
                runFormulaAction.runFormulaOrSaveValueToCell(true)
                editorStateMs.value=editorState.close()
            }
            commonSideEffectWhenCursorChanged.run(cursorStateMs.value.id)
        }
    }

    /**
     * when click on a cell, remove all previous selection, then assign the clicked cell as the new main cell
     */
    override fun clickOnCell(cellAddress: CellAddress, cursorLocation: WbWs) {
        clickOnCell(cellAddress,stateCont.getCursorStateMs(cursorLocation))
    }

    override fun clickOnCell(cellAddress: CellAddress, cursorLocation: WbWsSt) {
        clickOnCell(cellAddress,stateCont.getCursorStateMs(cursorLocation))
    }
}
