package com.qxdzbc.p6.app.action.worksheet.load_data

import androidx.compose.runtime.getValue
import com.github.michaelbull.result.*
import com.qxdzbc.common.Rse
import com.qxdzbc.common.compose.St
import com.qxdzbc.common.compose.StateUtils.toMs
import com.qxdzbc.p6.app.document.cell.d.CellContentImp
import com.qxdzbc.p6.app.document.cell.d.IndCellImp
import com.qxdzbc.p6.app.document.worksheet.Worksheet
import com.qxdzbc.p6.di.state.app_state.StateContainerSt
import com.qxdzbc.p6.di.state.app_state.TranslatorContainerSt
import com.qxdzbc.p6.rpc.common_data_structure.IndCellPrt
import com.qxdzbc.p6.rpc.worksheet.msg.LoadDataRequest
import com.qxdzbc.p6.rpc.worksheet.msg.LoadType
import com.qxdzbc.p6.translator.P6Translator
import com.qxdzbc.p6.translator.formula.execution_unit.ExUnit
import com.qxdzbc.p6.ui.app.error_router.ErrorRouter
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.qxdzbc.p6.ui.app.state.TranslatorContainer
import javax.inject.Inject

class LoadDataActionImp @Inject constructor(
    @StateContainerSt
    val stateContSt: St<@JvmSuppressWildcards StateContainer>,
    private val errorRouter: ErrorRouter,
    @TranslatorContainerSt
    val translatorContSt: St<@JvmSuppressWildcards TranslatorContainer>
) : LoadDataAction {
    val sc by stateContSt
    val tc by translatorContSt
    override fun loadDataRs(request: LoadDataRequest, publishErrorToUI: Boolean): Rse<Unit> {
        val getWsMsRs = sc.getWsStateMsRs(request)

        val rt = getWsMsRs.flatMap { wsStateMs ->
            val wsMs = wsStateMs.value.wsMs
            val translator = tc.getTranslatorOrCreate(request)
            val newDataRs = loadDataRs(wsMs.value, request, translator)
            newDataRs.onSuccess {
                wsMs.value = it
                wsStateMs.value = wsStateMs.value.refreshCellState()
            }
            newDataRs
        }
        rt.onFailure {
            if (publishErrorToUI) {
                errorRouter.publishToWindow(it, request.wbKey)
            }
        }
        return rt.map { Unit }
    }

    fun loadDataRs(
        ws: Worksheet,
        request: LoadDataRequest,
        translator: P6Translator<ExUnit>
    ): Rse<Worksheet> {
        val cells: List<IndCellPrt> = request.ws.cells
        var rt: Worksheet = ws
        val lt = request.loadType
        for (cell in cells) {
            if(lt == LoadType.KEEP_OLD_DATA_IF_COLLIDE){
                // x: don't overwrite existing old cell
                val oldCell = rt.getCell(cell.address)
                if(oldCell!=null){
                    continue
                }
            }
            val newCell = IndCellImp(
                address = cell.address,
                content = if (cell.value != null) {
                    CellContentImp(cellValueMs = cell.value.toMs())
                } else if (cell.formula != null) {
                    CellContentImp.fromTransRs(translator.translate(cell.formula))
                } else {
                    CellContentImp.empty
                }
            )
            rt = rt.addOrOverwrite(newCell)
        }
        return Ok(rt)
    }
}