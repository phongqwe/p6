package com.qxdzbc.p6.composite_actions.worksheet.remove_all_cell

import com.github.michaelbull.result.map
import com.qxdzbc.common.Rse
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWs
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWsSt
import com.qxdzbc.p6.di.P6AnvilScope

import com.qxdzbc.p6.ui.app.state.StateContainer
import com.qxdzbc.p6.ui.worksheet.state.WorksheetState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(P6AnvilScope::class)
class RemoveAllCellActionImp @Inject constructor(
    private val stateCont:StateContainer,
) : RemoveAllCellAction {

    val sc  = stateCont

    fun removeAllCell(wsState: WorksheetState) {
        val wsMs = wsState.wsMs
        wsMs.value.removeAllCell()
        wsState.refreshCellState()
    }

    override fun removeAllCell(wbWsSt: WbWsSt): Rse<Unit> {
        val o: Rse<Unit> = sc.getWsStateRs(wbWsSt).map { wsState ->
            removeAllCell(wsState)
        }
        return o
    }

    override fun removeAllCell(wbWs: WbWs): Rse<Unit> {
        val o: Rse<Unit> = sc.getWsStateRs(wbWs).map { wsState ->
            removeAllCell(wsState)
        }
        return o
    }
}
