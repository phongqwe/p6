package com.qxdzbc.p6.app.action.cell.multi_cell_update

import com.qxdzbc.common.Rse

interface MultiCellUpdateAction {
    /**
     * this one is for rpc call, slower than the other
     */
    fun updateMultiCellDM(request:MultiCellUpdateRequestDM, publishErr:Boolean = true):Rse<Unit>
    fun updateMultiCell(request:MultiCellUpdateRequest, publishErr:Boolean = true):Rse<Unit>
}
