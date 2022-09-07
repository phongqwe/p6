package com.qxdzbc.p6.app.action.worksheet.delete_multi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.p6.app.action.applier.WorkbookUpdateCommonApplier
import com.qxdzbc.p6.app.action.cell.cell_multi_update.CellMultiUpdateRequest
import com.qxdzbc.p6.app.action.cell.cell_multi_update.CellUpdateContent
import com.qxdzbc.p6.app.action.cell.cell_multi_update.CellUpdateEntry
import com.qxdzbc.p6.app.action.worksheet.delete_multi.applier.DeleteMultiApplier
import com.qxdzbc.p6.app.action.worksheet.delete_multi.rm.DeleteMultiRM
import com.qxdzbc.p6.app.action.worksheet.update_multi_cell.rm.UpdateMultiCellRM
import com.qxdzbc.p6.app.command.BaseCommand
import com.qxdzbc.p6.app.common.utils.RseNav
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.di.state.app_state.AppStateMs
import com.qxdzbc.p6.ui.app.state.AppState
import com.qxdzbc.p6.ui.document.worksheet.cursor.state.CursorState
import javax.inject.Inject

class DeleteMultiCellActionImp @Inject constructor(
    val rm: DeleteMultiRM,
    val applier: DeleteMultiApplier,
    @AppStateMs
    private val appStateMs: Ms<AppState>,
    private val cellUpdateRM: UpdateMultiCellRM,
    private val wbUpdateApplier: WorkbookUpdateCommonApplier,
) : DeleteMultiCellAction {

    private var appState by appStateMs

    override fun deleteMultiCellAtCursor(request: DeleteMultiAtCursorRequest): RseNav<DeleteMultiResponse> {
        createCommandAtCursor(request)
        return internalApplyAtCursor(request)
    }

    override fun deleteMultiCell(request: DeleteMultiRequest): RseNav<DeleteMultiResponse> {
        createCommand(request)
        return internalApply(request)
    }

    private fun internalApplyAtCursor(request: DeleteMultiAtCursorRequest): RseNav<DeleteMultiResponse> {
        val response = rm.deleteMultiCellAtCursor(request)
        applier.apply(response)
        return response
    }

    private fun internalApply(request: DeleteMultiRequest): RseNav<DeleteMultiResponse> {
        val response = rm.deleteMultiCell(request)
        applier.apply(response)
        return response
    }

    private fun createCommandAtCursor(request: DeleteMultiAtCursorRequest) {
        val k = request.wbKey
        val n = request.wsName
        val ws = appState.getWs(k, n)
        if (ws != null) {
            val cursorState: CursorState? = appState.getCursorState(k, n)
            if (cursorState != null) {
                createCommand(
                    DeleteMultiRequest(
                    ranges = cursorState.allRanges,
                    cells = cursorState.allFragCells,
                    wbKey = request.wbKey,
                    wsName = request.wsName,
                    clearFormat = request.clearFormat,
                    windowId = request.windowId
                )
                )
            }
        }
    }


    private fun createCommand(request: DeleteMultiRequest) {
        val k = request.wbKey
        val n = request.wsName
        val ws = appState.getWs(k, n)
        if (ws != null) {
            val command = object : BaseCommand() {
                val allCell: Set<CellAddress> = request.ranges.fold(setOf<CellAddress>()) { acc, range ->
                    val z = ws.getCellsInRange(range).map { it.address }
                    acc + z
                } + request.cells
                val updateList: List<CellUpdateEntry> = allCell.mapNotNull {
                    val cell = ws.getCell(it)
                    if (cell != null) {
                        CellUpdateEntry(
                            cellAddress = it,
                            cellUpdateContent = CellUpdateContent(
                                formula = cell.formula ?: "",
                                displayValue = cell.displayValue,
                                cellValue = cell.cellValueAfterRun.valueAfterRun
                            )
                        )
                    } else {
                        null
                    }
                }

                override fun run() {
                    internalApply(request)
                }

                override fun undo() {
                    // need to implement multi update request
                    val cellMultiUpdateRequest = CellMultiUpdateRequest(
                        wbKeySt = ws.wbKeySt,
                        wsNameSt = ws.wsNameSt,
                        cellUpdateList = updateList
                    )
                    val r = cellUpdateRM.cellMultiUpdate(cellMultiUpdateRequest)
                    if (r != null) {
                        wbUpdateApplier.apply(r)
                    }
                }
            }
            appState.queryStateByWorkbookKey(k).ifOk {
                val commandStackMs = it.workbookState.commandStackMs
                commandStackMs.value = commandStackMs.value.add(command)
            }
        }
    }
}
