package com.qxdzbc.p6.app.action.cell.multi_cell_update

import com.github.michaelbull.result.*
import com.qxdzbc.common.Rse
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.error.ErrorReport
import com.qxdzbc.p6.app.action.cell.cell_update.CommonReactionWhenAppStatesChanged
import com.qxdzbc.p6.di.P6Singleton
import com.qxdzbc.p6.di.anvil.P6AnvilScope
import com.qxdzbc.p6.rpc.common_data_structure.IndependentCellDM
import com.qxdzbc.p6.ui.app.error_router.ErrorRouter
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.qxdzbc.p6.ui.app.state.TranslatorContainer
import com.qxdzbc.p6.ui.document.worksheet.state.WorksheetState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@P6Singleton
@ContributesBinding(P6AnvilScope::class)
class UpdateMultiCellActionImp @Inject constructor(
    val stateCont:StateContainer,
    val tc: TranslatorContainer,
    val errorRouter: ErrorRouter,
    val commonReactionWhenAppStatesChanged: CommonReactionWhenAppStatesChanged
) : UpdateMultiCellAction {

    val sc  = stateCont

    override fun updateMultiCellDM(request: UpdateMultiCellRequestDM, publishErr: Boolean): Rse<Unit> {
        val wsStateMsRs = sc.getWsStateMsRs(request)
        val rt = updateMultiCell(wsStateMsRs,request.cellUpdateList)
        rt.onFailure {
            if (publishErr) {
                errorRouter.publishToWindow(it, request.wbKey)
            }
        }.onSuccess {
            commonReactionWhenAppStatesChanged.onUpdateMultipleCells(request)
        }
        return rt
    }

    override fun updateMultiCell(request: UpdateMultiCellRequest, publishErr: Boolean): Rse<Unit> {
        val wsStateMsRs = sc.getWsStateMsRs(request)
        val rt = this.updateMultiCell(wsStateMsRs,request.cellUpdateList)
        rt.onFailure {
            if (publishErr) {
                errorRouter.publishToWindow(it, request.wbKey)
            }
        }.onSuccess {
            commonReactionWhenAppStatesChanged.onUpdateMultipleCells(request)
        }
        return rt
    }

    fun updateMultiCell(wsStateMsRs:Rse<Ms<WorksheetState>>, cellUpdateList:List<IndependentCellDM>): Rse<Unit> {
        val rt = wsStateMsRs.flatMap { wsStateMs ->
            var ws = wsStateMs.value.worksheet
            var err: Err<ErrorReport>? = null
            val translator = tc.getTranslatorOrCreate(
                wbKeySt = ws.wbKeySt, wsNameSt = ws.nameMs
            )
            // x: update ws with new data
            for (indCell in cellUpdateList) {
                val updateRs = ws.updateCellContentRs(
                    cellAddress = indCell.address,
                    cellContent = indCell.content.toCellContent(translator)
                )
                updateRs.onSuccess {
                    ws = it
                }
                if (updateRs is Err) {
                    err = updateRs
                    break
                }
            }
            val noErr = err == null
            if (noErr) {
                // x: update state obj
                if (ws != wsStateMs.value.wsMs.value) {
                    wsStateMs.value.wsMs.value = ws
                    wsStateMs.value.refresh()
                }
                sc.wbStateCont.allWbStates.forEach {
                    it.wbMs.value = it.wb.reRunAndRefreshDisplayText()
                    it.refresh()
                }
            }
            err ?: Ok(Unit)
        }
        return rt
    }
}
