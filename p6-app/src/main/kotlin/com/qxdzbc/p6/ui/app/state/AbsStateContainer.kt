package com.qxdzbc.p6.ui.app.state

import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.qxdzbc.common.Rs
import com.qxdzbc.common.Rse
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.St
import com.qxdzbc.common.error.SingleErrorReport
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWs
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWsSt
import com.qxdzbc.p6.command.CommandStack
import com.qxdzbc.p6.document_data_layer.cell.CellId
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.ui.cell.state.CellState
import com.qxdzbc.p6.ui.worksheet.cursor.state.CursorState
import com.qxdzbc.p6.ui.worksheet.state.WorksheetState
import com.qxdzbc.p6.ui.format.CellFormatTable
import com.qxdzbc.p6.ui.window.focus_state.WindowFocusState
import com.qxdzbc.p6.ui.window.state.WindowState
import com.qxdzbc.p6.ui.window.tool_bar.color_selector.state.ColorSelectorState
import com.qxdzbc.p6.ui.window.tool_bar.state.ToolBarState
import com.qxdzbc.p6.ui.window.tool_bar.text_size_selector.state.TextSizeSelectorState

abstract class AbsStateContainer : StateContainer {

    override fun getCellFormatTableMsRs(wbwsSt: WbWsSt): Rse<Ms<CellFormatTable>> {
        return this.getWsStateRs(wbwsSt).map {
            it.cellFormatTableMs
        }
    }

    override fun getCellFormatTableMsRs(wbws: WbWs): Rse<Ms<CellFormatTable>> {
        return this.getWsStateRs(wbws).map {
            it.cellFormatTableMs
        }
    }

    override fun getCellFormatTableRs(wbwsSt: WbWsSt): Rse<CellFormatTable> {
        return this.getWsStateRs(wbwsSt).map {
            it.cellFormatTableMs.component1()
        }
    }

    override fun getCellFormatTableRs(wbws: WbWs): Rse<CellFormatTable> {
        return this.getWsStateRs(wbws).map {
            it.cellFormatTableMs.component1()
        }
    }

    override fun getRedoStackMs(wbwsSt: WbWsSt): Ms<CommandStack>? {
        return getWsState(wbwsSt)?.redoStackMs
    }

    override fun getRedoStackMs(wbws: WbWs): Ms<CommandStack>? {
        return getWsState(wbws)?.redoStackMs
    }

    override fun getUndoStackMs(wbwsSt: WbWsSt): Ms<CommandStack>? {
        return getWsState(wbwsSt)?.undoStackMs
    }

    override fun getUndoStackMs(wbws: WbWs): Ms<CommandStack>? {
        return getWsState(wbws)?.undoStackMs
    }

    override fun getCellFormatTableMs(wbws: WbWs): Ms<CellFormatTable>? {
        return this.getWsState(wbws)?.cellFormatTableMs
    }

    override fun getCellFormatTable(wbwsSt: WbWsSt): CellFormatTable? {
        return this.getWsState(wbwsSt)?.cellFormatTableMs?.value
    }

    override fun getCellFormatTable(wbws: WbWs): CellFormatTable? {
        return this.getWsState(wbws)?.cellFormatTableMs?.value
    }

    override fun getCellFormatTableMs(wbwsSt: WbWsSt): Ms<CellFormatTable>? {
        return this.getWsState(wbwsSt)?.cellFormatTableMs
    }

    override fun getTextColorSelectorStateMs(windowId: String): Ms<ColorSelectorState>? {
        return getToolbarState(windowId)?.textColorSelectorStateMs
    }

    override fun getTextColorSelectorState(windowId: String): ColorSelectorState? {
        return getToolbarState(windowId)?.textColorSelectorStateMs?.value
    }

    override fun getTextColorSelectorStateMs(wbKey: WorkbookKey): Ms<ColorSelectorState>? {
        return this.getWindowStateByWbKey(wbKey)?.toolBarState?.textColorSelectorStateMs
    }

    override fun getTextColorSelectorState(wbKey: WorkbookKey): ColorSelectorState? {
        return this.getWindowStateByWbKey(wbKey)?.toolBarState?.textColorSelectorStateMs?.value
    }

    override fun getCellBackgroundColorSelectorStateMs(windowId: String): Ms<ColorSelectorState>? {
        return this.getWindowStateById(windowId)?.toolBarState?.cellBackgroundColorSelectorStateMs
    }

    override fun getCellBackgroundColorSelectorState(windowId: String): ColorSelectorState? {
        return this.getWindowStateById(windowId)?.toolBarState?.cellBackgroundColorSelectorStateMs?.value
    }

    override fun getCellBackgroundColorSelectorStateMs(wbKey: WorkbookKey): Ms<ColorSelectorState>? {
        return this.getWindowStateByWbKey(wbKey)?.toolBarState?.cellBackgroundColorSelectorStateMs
    }

    override fun getCellBackgroundColorSelectorState(wbKey: WorkbookKey): ColorSelectorState? {
        return this.getWindowStateByWbKey(wbKey)?.toolBarState?.cellBackgroundColorSelectorStateMs?.value
    }

    override fun getTextSizeSelectorStateMs(wbKey: WorkbookKey): Ms<TextSizeSelectorState>? {
        return this.getWindowStateByWbKey(wbKey)?.toolBarState?.textSizeSelectorStateMs
    }

    override fun getTextSizeSelectorState(wbKey: WorkbookKey): TextSizeSelectorState? {
        return this.getWindowStateByWbKey(wbKey)?.toolBarState?.textSizeSelectorState
    }

    override fun getToolbarStateMs(windowId: String): Ms<ToolBarState>? {
        return getWindowStateById(windowId)?.toolBarStateMs
    }

    override fun getToolbarState(windowId: String): ToolBarState? {
        return getWindowStateById(windowId)?.toolBarStateMs?.value
    }

    override fun getTextSizeSelectorStateMs(windowId: String): Ms<TextSizeSelectorState>? {
        return getWindowStateById(windowId)?.toolBarState?.textSizeSelectorStateMs
    }

    override fun getTextSizeSelectorState(windowId: String): TextSizeSelectorState? {
        return getWindowStateById(windowId)?.toolBarState?.textSizeSelectorStateMs?.value
    }

    override fun getCellStateMs(cellId: CellId): Ms<CellState>? {
        return getCellStateMsRs(cellId).component1()
    }

    override fun getCellState(cellId: CellId): CellState? {
        return getCellStateMs(cellId)?.value
    }

    override fun getCellState(wbwsSt: WbWsSt, cellAddress: CellAddress): CellState? {
        return getCellStateMs(wbwsSt, cellAddress)?.value
    }

    override fun getCellStateMs(wbwsSt: WbWsSt, cellAddress: CellAddress): Ms<CellState>? {
        return getCellStateMsRs(wbwsSt, cellAddress).component1()
    }

    override fun getCursorStateMs(wbwsSt: WbWsSt): Ms<CursorState>? {
        return this.getWsState(wbwsSt)?.cursorStateMs
    }

    override fun getWindowStateByIdRs(windowId: String): Rse<WindowState> {
        return getWindowStateMsByIdRs(windowId)
    }

    override fun getWindowStateById(windowId: String): WindowState? {
        return getWindowStateMsById(windowId)
    }

    override fun getWindowStateByWbKeyRs(wbKey: WorkbookKey): Rse<WindowState> {
        return getWindowStateMsByWbKeyRs(wbKey)
    }

    override fun getWindowStateByWbKey(wbKey: WorkbookKey): WindowState? {
        return getWindowStateMsByWbKey(wbKey)
    }

    override fun getWindowStateMsById(windowId: String): WindowState? {
        return getWindowStateMsByIdRs(windowId).component1()
    }

    override fun getFocusStateMsByWbKey(wbKey: WorkbookKey): Ms<WindowFocusState>? {
        return getFocusStateMsByWbKeyRs(wbKey).component1()
    }

    override fun getWindowStateMsByWbKey(wbKey: WorkbookKey): WindowState? {
        return this.getWindowStateMsByWbKeyRs(wbKey).component1()
    }

    override fun getWsStateRs(wbwsSt: WbWsSt): Rse<WorksheetState> {
        return this.getWsStateMsRs(wbwsSt.wbKeySt, wbwsSt.wsNameSt)
    }

    override fun getWsState(wbwsSt: WbWsSt): WorksheetState? {
        return getWsStateRs(wbwsSt).component1()
    }

    override fun getWsStateMsRs(wbKeySt: St<WorkbookKey>, wsNameSt: St<String>): Rse<WorksheetState> {
        return this.getWbStateRs(wbKeySt).flatMap {
            it.getWsStateMsRs(wsNameSt)
        }
    }

    override fun getWsStateRs(wbKeySt: St<WorkbookKey>, wsNameSt: St<String>): Rse<WorksheetState> {
        return getWsStateMsRs(wbKeySt, wsNameSt)
    }

    override fun getWsStateMs(wbKeySt: St<WorkbookKey>, wsNameSt: St<String>): WorksheetState? {
        return getWsStateMsRs(wbKeySt, wsNameSt).component1()
    }

    override fun getWsState(wbKeySt: St<WorkbookKey>, wsNameSt: St<String>): WorksheetState? {
        return getWsStateMs(wbKeySt, wsNameSt)
    }

    override fun getCursorStateMs(wbKey: WorkbookKey, wsName: String): Ms<CursorState>? {
        return this.getWsState(wbKey, wsName)?.cursorStateMs
    }

    override fun getFocusStateMsByWbKeyRs(wbKey: WorkbookKey): Rs<Ms<WindowFocusState>, SingleErrorReport> {
        return this.getWindowStateMsByWbKeyRs(wbKey).map {
            it.focusStateMs
        }
    }

    override fun getWsStateRs(wbKey: WorkbookKey, wsName: String): Rse<WorksheetState> {
        return this.getWbStateRs(wbKey).flatMap { it.getWsStateMsRs(wsName) }
    }

    override fun getWsState(wbKey: WorkbookKey, wsName: String): WorksheetState? {
        return getWsStateRs(wbKey, wsName).component1()
    }

    override fun getWsStateRs(wbws: WbWs): Rse<WorksheetState> {
        return this.getWsStateRs(wbws.wbKey, wbws.wsName)
    }

    override fun getWsState(wbws: WbWs): WorksheetState? {
        return getWsStateRs(wbws.wbKey, wbws.wsName).component1()
    }

    override fun getCursorState(wbKey: WorkbookKey, wsName: String): CursorState? {
        return getCursorStateMs(wbKey, wsName)?.value
    }

    override fun getCursorStateMs(wbws: WbWs): Ms<CursorState>? {
        return this.getCursorStateMs(wbws.wbKey, wbws.wsName)
    }

    override fun getCursorState(wbws: WbWs): CursorState? {
        return this.getCursorStateMs(wbws.wbKey, wbws.wsName)?.value
    }

    override fun getCursorState(wbws: WbWsSt): CursorState? {
        return this.getCursorStateMs(wbws)?.value
    }

    override fun getActiveCursorMs(wbKey: WorkbookKey): Ms<CursorState>? {
        val rt = this.getWbState(wbKey)?.let { wbState ->
            wbState.activeSheetState?.cursorStateMs
        }
        return rt
    }

    override fun getActiveCursorMs(wbKeyMs: Ms<WorkbookKey>): Ms<CursorState>? {
        val rt = this.getWbState(wbKeyMs)?.let { wbState ->
            wbState.activeSheetState?.cursorStateMs
        }
        return rt
    }
}
