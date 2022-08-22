package com.qxdzbc.p6.ui.app.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.p6.app.action.common_data_structure.WbWs
import com.qxdzbc.p6.app.action.common_data_structure.WbWsSt
import com.qxdzbc.p6.app.action.range.RangeId
import com.qxdzbc.p6.app.common.utils.Rs
import com.qxdzbc.p6.app.common.utils.Rse
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.app.document.cell.d.Cell
import com.qxdzbc.p6.app.document.range.Range
import com.qxdzbc.p6.app.document.range.address.RangeAddress
import com.qxdzbc.p6.app.document.script.ScriptContainer
import com.qxdzbc.p6.app.document.script.ScriptContainerImp
import com.qxdzbc.p6.app.document.wb_container.WorkbookContainer
import com.qxdzbc.p6.app.document.workbook.Workbook
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.app.document.worksheet.Worksheet
import com.qxdzbc.p6.app.oddity.OddityContainer
import com.qxdzbc.p6.app.oddity.OddityContainerImp
import com.qxdzbc.p6.common.exception.error.ErrorReport
import com.qxdzbc.p6.di.False
import com.qxdzbc.p6.di.state.app_state.*
import com.qxdzbc.p6.ui.app.ActiveWindowPointer
import com.qxdzbc.p6.ui.app.ActiveWindowPointerImp
import com.qxdzbc.p6.ui.common.compose.Ms
import com.qxdzbc.p6.ui.common.compose.StateUtils.ms
import com.qxdzbc.p6.ui.document.workbook.state.WorkbookStateFactory
import com.qxdzbc.p6.ui.document.workbook.state.cont.WorkbookStateContainer
import com.qxdzbc.p6.ui.app.cell_editor.in_cell.state.CellEditorState
import com.qxdzbc.p6.ui.common.compose.St
import com.qxdzbc.p6.ui.document.workbook.state.WorkbookState
import com.qxdzbc.p6.ui.document.worksheet.cursor.state.CursorState
import com.qxdzbc.p6.ui.document.worksheet.state.WorksheetState
import com.qxdzbc.p6.ui.script_editor.code_container.CentralScriptContainer
import com.qxdzbc.p6.ui.script_editor.state.CodeEditorState
import com.qxdzbc.p6.ui.window.focus_state.WindowFocusState
import com.qxdzbc.p6.ui.window.state.WindowState
import com.qxdzbc.p6.ui.window.state.WindowStateFactory
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.unwrapError
import javax.inject.Inject

data class AppStateImp @Inject constructor(
    @False
    override val codeEditorIsOpen: Boolean,
    @AppOddityContMs
    override val oddityContainerMs: Ms<OddityContainer> = ms(OddityContainerImp()),
    @WindowActivePointerMs
    override val activeWindowPointerMs: Ms<ActiveWindowPointer> = ms(ActiveWindowPointerImp(null)),
    @AppScriptContMs
    val appScriptContainerMs: Ms<ScriptContainer> = ms(ScriptContainerImp()),
    @CentralScriptContMs
    override val centralScriptContainerMs: Ms<CentralScriptContainer>,
    @CodeEditorStateMs
    override val codeEditorStateMs: Ms<CodeEditorState>,
    val windowStateFactory: WindowStateFactory,
    private val wbStateFactory: WorkbookStateFactory,
    @SubAppStateContainerMs
    override val subAppStateContMs: Ms<SubAppStateContainer>,
    @DocumentContainerMs
    override val docContMs: Ms<DocumentContainer>,
    @TranslatorContainerMs
    override val translatorContMs: Ms<TranslatorContainer>,
    @CellEditorStateMs
    override val cellEditorStateMs: Ms<CellEditorState>,
) : AppState,AbsSubAppStateContainer() {

    override var docCont by docContMs
    override var stateCont by subAppStateContMs
    override var translatorContainer: TranslatorContainer by translatorContMs
    override val globalWbContMs: Ms<WorkbookContainer>
        get() = docCont.globalWbContMs
    override var cellEditorState: CellEditorState by cellEditorStateMs
    override val windowStateMsListMs: Ms<List<Ms<WindowState>>>
        get() = subAppStateContMs.value.windowStateMsListMs
    override var windowStateMsList: List<MutableState<WindowState>> by windowStateMsListMs
    override val globalWbStateContMs: Ms<WorkbookStateContainer>
        get() = subAppStateContMs.value.globalWbStateContMs
    override var oddityContainer: OddityContainer by oddityContainerMs
    override var globalWbStateCont: WorkbookStateContainer by globalWbStateContMs
    override var activeWindowPointer: ActiveWindowPointer by activeWindowPointerMs
    override val activeWindowStateMs: Ms<WindowState>?
        get() = activeWindowPointer.windowId?.let{this.getWindowStateMsById(it)}
    override val activeWindowState: WindowState?
        get() = activeWindowStateMs?.value
    override var globalWbCont: WorkbookContainer by globalWbContMs
    override var centralScriptContainer: CentralScriptContainer by centralScriptContainerMs
    override var codeEditorState: CodeEditorState by codeEditorStateMs

    override fun getWbWsSt(wbKey: WorkbookKey, wsName: String): WbWsSt? {
        return docCont.getWbWsSt(wbKey, wsName)
    }

    override fun getWbWsSt(wbWs: WbWs): WbWsSt? {
        return docCont.getWbWsSt(wbWs)
    }

    override fun getWbRs(wbKey: WorkbookKey): Rs<Workbook, ErrorReport> {
        return docCont.getWbRs(wbKey)
    }

    override fun getWb(wbKey: WorkbookKey): Workbook? {
        return docCont.getWb(wbKey)
    }

    override fun getWsRs(wbKey: WorkbookKey, wsName: String): Rs<Worksheet, ErrorReport> {
        return docCont.getWsRs(wbKey, wsName)
    }

    override fun getWs(wbKey: WorkbookKey, wsName: String): Worksheet? {
        return docCont.getWs(wbKey,wsName)
    }

    override fun getWs(wbws: WbWs): Worksheet? {
        return docCont.getWs(wbws)
    }

    override fun getRangeRs(wbKey: WorkbookKey, wsName: String, rangeAddress: RangeAddress): Rs<Range, ErrorReport> {
        return docCont.getRangeRs(wbKey, wsName, rangeAddress)
    }

    override fun getRangeRs(rangeId: RangeId): Rs<Range, ErrorReport> {
        return docCont.getRangeRs(rangeId)
    }

    override fun getRange(wbKey: WorkbookKey, wsName: String, rangeAddress: RangeAddress): Range? {
        return docCont.getRange(wbKey, wsName, rangeAddress)
    }

    override fun getLazyRange(wbKey: WorkbookKey, wsName: String, rangeAddress: RangeAddress): Range? {
        return docCont.getLazyRange(wbKey, wsName, rangeAddress)
    }

    override fun getLazyRangeRs(
        wbKey: WorkbookKey,
        wsName: String,
        rangeAddress: RangeAddress
    ): Rs<Range, ErrorReport> {
        return docCont.getLazyRangeRs(wbKey, wsName, rangeAddress)
    }

    override fun getCellRs(wbKey: WorkbookKey, wsName: String, cellAddress: CellAddress): Rs<Cell, ErrorReport> {
        return docCont.getCellRs(wbKey, wsName, cellAddress)
    }

    override fun getCell(wbKey: WorkbookKey, wsName: String, cellAddress: CellAddress): Cell? {
        return docCont.getCell(wbKey, wsName, cellAddress)
    }

    override fun getStateByWorkbookKeyRs(workbookKey: WorkbookKey): Rse<QueryByWorkbookKeyResult2> {
        return stateCont.getStateByWorkbookKeyRs(workbookKey)
    }

    override fun getWbStateMsRs(wbKey: WorkbookKey): Rse<Ms<WorkbookState>> {
        return stateCont.getWbStateMsRs(wbKey)
    }

    override fun getWsStateMsRs(wbKey: WorkbookKey, wsName: String): Rse<Ms<WorksheetState>> {
        return stateCont.getWsStateMsRs(wbKey, wsName)
    }

    override fun getWindowStateMsByWbKeyRs(wbKey: WorkbookKey): Result<Ms<WindowState>, ErrorReport> {
        return stateCont.getWindowStateMsByWbKeyRs(wbKey)
    }

    override fun getFocusStateMsByWbKeyRs(wbKey: WorkbookKey): Rs<Ms<WindowFocusState>, ErrorReport> {
        return stateCont.getFocusStateMsByWbKeyRs(wbKey)
    }

    override fun getWindowStateMsByIdRs(windowId: String): Rs<Ms<WindowState>, ErrorReport> {
        return stateCont.getWindowStateMsByIdRs(windowId)
    }

    override fun getCursorStateMs(wbKey: WorkbookKey, wsName: String): Ms<CursorState>? {
        return stateCont.getCursorStateMs(wbKey, wsName)
    }

    override fun openCodeEditor(): AppState {
        return this.copy(codeEditorIsOpen = true)
    }

    override fun closeCodeEditor(): AppState {
        return this.copy(codeEditorIsOpen = false)
    }

    override fun replaceWb(newWb: Workbook): AppState {
        docCont = docCont.replaceWb(newWb)
        return this
    }

    override fun addWbStateFor(wb: Workbook): AppState {
        stateCont = stateCont.addWbStateFor(wb)
        return this
    }

    override fun removeWindowState(windowState: Ms<WindowState>): AppState {
        stateCont = stateCont.removeWindowState(windowState)
        return this
    }

    override fun removeWindowState(windowId: String): AppState {
        stateCont = stateCont.removeWindowState(windowId)
        return this
    }

    override fun createNewWindowStateMs(): Pair<AppState, Ms<WindowState>> {
        val p = stateCont.createNewWindowStateMs()
        stateCont = p.first
        return Pair(this, p.second)
    }

    override fun createNewWindowStateMs(windowId: String): Pair<AppState, Ms<WindowState>> {
        val p = stateCont.createNewWindowStateMs(windowId)
        stateCont = p.first
        return Pair(this, p.second)
    }

    override fun getWbStateMsRs(wbKeySt: St<WorkbookKey>): Rse<Ms<WorkbookState>> {
        return this.subAppStateContMs.value.getWbStateMsRs(wbKeySt)
    }

    override fun addWindowState(windowState: Ms<WindowState>): AppState {
        stateCont = stateCont.addWindowState(windowState)
        return this
    }

    override fun queryStateByWorkbookKey(workbookKey: WorkbookKey): QueryByWorkbookKeyResult {
        val windowStateMsRs = this.getWindowStateMsByWbKeyRs(workbookKey)
        if (windowStateMsRs is Ok) {
            val windowstateMs = windowStateMsRs.value
            val workbookStateMsRs = globalWbStateCont.getWbStateMsRs(workbookKey)

            if (workbookStateMsRs is Ok) {
                return QueryByWorkbookKeyResult(
                    windowStateOrNull = windowstateMs,
                    workbookStateMsOrNull = workbookStateMsRs.value,
                    oddityContainerMs = windowstateMs.value.oddityContainerMs
                )
            } else {
                return QueryByWorkbookKeyResult(
                    _errorReport = workbookStateMsRs.unwrapError(),
                    oddityContainerMs = windowstateMs.value.oddityContainerMs
                )
            }
        } else {
            return QueryByWorkbookKeyResult(
                _errorReport = windowStateMsRs.unwrapError(),
                oddityContainerMs = this.oddityContainerMs
            )
        }
    }
}
