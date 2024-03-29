package com.qxdzbc.p6.composite_actions.cell.cell_update

import com.qxdzbc.common.compose.St
import com.qxdzbc.p6.composite_actions.cell.multi_cell_update.UpdateMultiCellRequest
import com.qxdzbc.p6.composite_actions.cell.multi_cell_update.UpdateMultiCellRequestDM
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWs
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWsSt
import com.qxdzbc.p6.composite_actions.cursor.on_cursor_changed_reactor.CommonSideEffectWhenCursorChanged
import com.qxdzbc.p6.document_data_layer.cell.CellId
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.di.P6AnvilScope
import com.qxdzbc.p6.rpc.cell.msg.CellIdDM
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(P6AnvilScope::class,boundType=CommonReactionWhenAppStatesChanged::class)
class CommonReactionWhenAppStatesChangedImp @Inject constructor(
    val stateContainerSt:StateContainer,
    val commonReactionWhenCursorChanged:CommonSideEffectWhenCursorChanged,
) : CommonReactionWhenAppStatesChanged, CommonSideEffectWhenCursorChanged by commonReactionWhenCursorChanged {

    val sc  = stateContainerSt

    override fun onWbChange(wbKey:WorkbookKey){
        sc.getWbState(wbKey)?.also {
            it.needSave = true
        }
    }

    override fun onWsChanged(wbwsSt: WbWsSt) {
        onWbChange(wbwsSt.wbKeySt)
    }

    override fun onWsChanged(wbws: WbWs) {
        onWbChange(wbws.wbKey)
    }

    override fun onOneCellChanged(request: CellUpdateRequest) {
        onWbChange(request.wbKeySt)
    }

    override fun onOneCellChanged(request: CellUpdateRequestDM) {
        onWbChange(request.wbKey)
    }

    override fun onOneCellChanged(cellId: CellId) {
        onWbChange(cellId.wbKeySt)
    }

    override fun onOneCellChanged(cellId: CellIdDM) {
        onWbChange(cellId.wbKey)
    }

    override fun onUpdateMultipleCells(request: UpdateMultiCellRequestDM) {
        onWbChange(request.wbKey)
    }

    override fun onUpdateMultipleCells(request: UpdateMultiCellRequest) {
        onWbChange(request.wbKeySt)
    }

    override fun onUpdateMultipleCells(cellIds: List<CellId>) {
        val wbKeys = cellIds.map{it.wbKeySt}.toSet()
        wbKeys.forEach {
            this.onWbChange(it)
        }
    }

    override fun onUpdateMultipleCellsDM(cellIds: List<CellIdDM>) {
        val wbKeys = cellIds.map{it.wbKey}.toSet()
        wbKeys.forEach {
            this.onWbChange(it)
        }
    }

    override fun onWbChange(wbKeySt: St<WorkbookKey>) {
        sc.getWbState(wbKeySt)?.needSave = true
    }
}
