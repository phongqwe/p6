package com.qxdzbc.p6.ui.app.state

import com.qxdzbc.common.Rs
import com.qxdzbc.common.Rse
import com.qxdzbc.p6.document_data_layer.workbook.Workbook
import com.qxdzbc.p6.ui.app.cell_editor.state.CellEditorState
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.St
import com.qxdzbc.common.error.SingleErrorReport
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWs
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWsSt
import com.qxdzbc.p6.command.CommandStack
import com.qxdzbc.p6.document_data_layer.cell.CellId
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.ui.app.ActiveWindowPointer
import com.qxdzbc.p6.ui.cell.state.CellState
import com.qxdzbc.p6.ui.workbook.state.WorkbookState
import com.qxdzbc.p6.ui.workbook.state.cont.WorkbookStateContainer
import com.qxdzbc.p6.ui.workbook.state.cont.WorkbookStateGetter
import com.qxdzbc.p6.ui.worksheet.cursor.state.CursorState
import com.qxdzbc.p6.ui.worksheet.cursor.thumb.state.ThumbState
import com.qxdzbc.p6.ui.worksheet.ruler.RulerSig
import com.qxdzbc.p6.ui.worksheet.ruler.RulerState
import com.qxdzbc.p6.ui.worksheet.ruler.RulerType
import com.qxdzbc.p6.ui.worksheet.slider.GridSlider
import com.qxdzbc.p6.ui.worksheet.state.WorksheetState
import com.qxdzbc.p6.ui.format.CellFormatTable
import com.qxdzbc.p6.ui.window.focus_state.WindowFocusState
import com.qxdzbc.p6.ui.window.state.OuterWindowState
import com.qxdzbc.p6.ui.window.state.WindowState
import com.qxdzbc.p6.ui.window.tool_bar.color_selector.state.ColorSelectorState
import com.qxdzbc.p6.ui.window.tool_bar.state.ToolBarState
import com.qxdzbc.p6.ui.window.tool_bar.text_size_selector.state.TextSizeSelectorState

/**
 * top-level state container, can access all state + document in the app
 */
interface StateContainer : DocumentContainer, WorkbookStateGetter {

    val activeWindowPointer: ActiveWindowPointer

    fun getActiveWindowStateMs():WindowState?
    fun getActiveWindowState():WindowState?

    val cellEditorStateMs:Ms<CellEditorState>
    var cellEditorState: CellEditorState

    fun getActiveWorkbook(): Workbook?
    fun getActiveWorkbookRs(): Rse<Workbook>

    /**
     * get window state respective to [windowId],
     * if [windowId] is null, get the active window, or the first in the window state list.
     */
    fun getWindowState_OrDefault_Rs(windowId: String?):Rse<WindowState>

    /**
     * get cursor state ms of the active worksheet inside the active workbook
     */
    fun getActiveCursorStateMs(): Ms<CursorState>?
    fun getActiveCursorState(): CursorState?

    fun getActiveWbState(): WorkbookState?

    fun getUndoStackMs(wbwsSt: WbWsSt):Ms<CommandStack>?
    fun getUndoStackMs(wbws: WbWs):Ms<CommandStack>?

    fun getRedoStackMs(wbwsSt: WbWsSt):Ms<CommandStack>?
    fun getRedoStackMs(wbws: WbWs):Ms<CommandStack>?

    fun getCellFormatTableMs(wbwsSt: WbWsSt):Ms<CellFormatTable>?
    fun getCellFormatTableMs(wbws: WbWs):Ms<CellFormatTable>?

    fun getCellFormatTableMsRs(wbwsSt: WbWsSt):Rse<Ms<CellFormatTable>>
    fun getCellFormatTableMsRs(wbws: WbWs):Rse<Ms<CellFormatTable>>

    fun getCellFormatTable(wbwsSt: WbWsSt): CellFormatTable?
    fun getCellFormatTable(wbws: WbWs): CellFormatTable?

    fun getCellFormatTableRs(wbwsSt: WbWsSt):Rse<CellFormatTable>
    fun getCellFormatTableRs(wbws: WbWs):Rse<CellFormatTable>

    fun getToolbarStateMs(windowId: String):Ms<ToolBarState>?
    fun getToolbarState(windowId: String): ToolBarState?

    fun getTextSizeSelectorStateMs(windowId:String):Ms<TextSizeSelectorState>?
    fun getTextSizeSelectorState(windowId:String): TextSizeSelectorState?

    fun getTextSizeSelectorStateMs(wbKey:WorkbookKey):Ms<TextSizeSelectorState>?
    fun getTextSizeSelectorState(wbKey:WorkbookKey): TextSizeSelectorState?

    fun getTextColorSelectorStateMs(windowId:String):Ms<ColorSelectorState>?
    fun getTextColorSelectorState(windowId:String): ColorSelectorState?

    fun getTextColorSelectorStateMs(wbKey:WorkbookKey):Ms<ColorSelectorState>?
    fun getTextColorSelectorState(wbKey:WorkbookKey): ColorSelectorState?

    fun getCellBackgroundColorSelectorStateMs(windowId:String):Ms<ColorSelectorState>?
    fun getCellBackgroundColorSelectorState(windowId:String): ColorSelectorState?
    fun getCellBackgroundColorSelectorStateMs(wbKey:WorkbookKey):Ms<ColorSelectorState>?
    fun getCellBackgroundColorSelectorState(wbKey:WorkbookKey): ColorSelectorState?

    fun getCellStateMsRs(wbwsSt: WbWsSt, cellAddress: CellAddress):Rse<Ms<CellState>>
    fun getCellStateMsRs(cellId: CellId):Rse<Ms<CellState>>

    fun getCellStateMs(wbwsSt: WbWsSt, cellAddress: CellAddress):Ms<CellState>?
    fun getCellState(wbwsSt: WbWsSt, cellAddress: CellAddress): CellState?

    fun getCellStateMs(cellId: CellId):Ms<CellState>?
    fun getCellState(cellId: CellId): CellState?

    val windowStateMapMs: Ms<Map<String, Ms<OuterWindowState>>>
    var windowStateMap: Map<String, Ms<OuterWindowState>>

    val outerWindowStateMsList: List<Ms<OuterWindowState>>
    val windowStateMsList: List<WindowState>

    val wbStateCont: WorkbookStateContainer

    /**
     * Get a set of state related to a [workbookKey].
     * @return an Error if no state object can be found, the Error contain information about why there are not any State obj for [workbookKey]
     */
    fun getStateByWorkbookKeyRs(workbookKey: WorkbookKey): Rse<QueryByWorkbookKeyResult>

    /**
     * Get a set of state related to a [workbookKey].
     * @return an null if no state object can be found
     */
    fun getStateByWorkbookKey(workbookKey: WorkbookKey): QueryByWorkbookKeyResult?

    /**
     * create and add a new wb state for [wb] if it yet to have a state of its own
     */
    fun addWbStateFor(wb: Workbook)

    fun removeWindowState(windowState: WindowState)
    fun removeWindowState(windowId: String)
    fun addWindowState(windowState: WindowState)
    fun createNewWindowStateMs(): Ms<OuterWindowState>
    fun createNewWindowStateMs(windowId: String): Ms<OuterWindowState>

    fun getWsStateMsRs(wbKeySt: St<WorkbookKey>, wsNameSt: St<String>): Rse<WorksheetState>
    fun getWsStateRs(wbKeySt: St<WorkbookKey>, wsNameSt: St<String>): Rse<WorksheetState>
    fun getWsStateMs(wbKeySt: St<WorkbookKey>, wsNameSt: St<String>): WorksheetState?
    fun getWsState(wbKeySt: St<WorkbookKey>, wsNameSt: St<String>): WorksheetState?

    fun getWsStateRs(wbwsSt: WbWsSt): Rse<WorksheetState>
    fun getWsState(wbwsSt: WbWsSt): WorksheetState?

    fun getWsStateRs(wbKey: WorkbookKey, wsName: String): Rse<WorksheetState>
    fun getWsState(wbKey: WorkbookKey, wsName: String): WorksheetState?

    fun getWsStateRs(wbws: WbWs): Rse<WorksheetState>
    fun getWsState(wbws: WbWs): WorksheetState?

    fun getWindowStateMsByWbKeyRs(wbKey: WorkbookKey): Rs<WindowState, SingleErrorReport>
    fun getWindowStateMsByWbKey(wbKey: WorkbookKey): WindowState?

    fun getFocusStateMsByWbKeyRs(wbKey: WorkbookKey): Rs<Ms<WindowFocusState>, SingleErrorReport>
    fun getFocusStateMsByWbKey(wbKey: WorkbookKey): Ms<WindowFocusState>?

    fun getWindowStateByIdRs(windowId: String): Rse<WindowState>
    fun getWindowStateById(windowId: String): WindowState?
    fun getWindowStateByWbKeyRs(wbKey: WorkbookKey): Rse<WindowState>
    fun getWindowStateByWbKey(wbKey: WorkbookKey): WindowState?

    /**
     * TODO rename this, remove the Ms part
     */
    fun getWindowStateMsByIdRs(windowId: String): Rs<WindowState, SingleErrorReport>
    fun getWindowStateMsById(windowId: String): WindowState?

    fun getCursorStateMs(wbKey: WorkbookKey, wsName: String): Ms<CursorState>?
    fun getCursorState(wbKey: WorkbookKey, wsName: String): CursorState?
    fun getCursorStateMs(wbws: WbWs): Ms<CursorState>?
    fun getCursorStateMs(wbwsSt: WbWsSt): Ms<CursorState>?
    fun getCursorState(wbws: WbWs): CursorState?
    fun getCursorState(wbws: WbWsSt): CursorState?

    /**
     * get cursor state ms of the active worksheet inside the workbook whose key is [wbKey]
     */
    fun getActiveCursorMs(wbKey: WorkbookKey): Ms<CursorState>?
    /**
     * get cursor state ms of the active worksheet inside the workbook whose key is [wbKey]
     */
    fun getActiveCursorMs(wbKeyMs: Ms<WorkbookKey>): Ms<CursorState>?

    fun addOuterWindowState(windowState: Ms<OuterWindowState>)
    fun removeOuterWindowState(windowState: Ms<OuterWindowState>)

    fun getRulerStateMsRs(wbws: WbWs, type: RulerType): Rse<Ms<RulerState>>
    fun getRulerStateMsRs(wbwsSt: WbWsSt, type: RulerType): Rse<Ms<RulerState>>
    fun getRulerStateMsRs(rulerSig: RulerSig): Rse<Ms<RulerState>>

    fun getRulerStateMs(wbws: WbWs, type: RulerType): Ms<RulerState>?
    fun getRulerStateMs(wbwsSt: WbWsSt, type: RulerType): Ms<RulerState>?
    fun getRulerStateMs(rulerSig: RulerSig): Ms<RulerState>?

    fun getRulerState(wbws: WbWs, type: RulerType): RulerState?
    fun getRulerState(wbwsSt: WbWsSt, type: RulerType): RulerState?
    fun getRulerState(rulerSig: RulerSig): RulerState?

    fun getSliderMsRs(wbwsSt: WbWsSt): Rse<Ms<GridSlider>>
    fun getSliderMs(wbwsSt: WbWsSt): Ms<GridSlider>?

    fun getThumbStateMsRs(wbwsSt: WbWsSt):Rse<Ms<ThumbState>>
    fun getThumbStateMs(wbwsSt: WbWsSt):Ms<ThumbState>?
}
