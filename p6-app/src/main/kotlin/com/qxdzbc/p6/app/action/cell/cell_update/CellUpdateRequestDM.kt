package com.qxdzbc.p6.app.action.cell.cell_update

import com.qxdzbc.p6.app.action.common_data_structure.WbWs
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.proto.CellProtos.CellUpdateRequestProto
import com.qxdzbc.p6.rpc.cell.msg.CellContentDM
import com.qxdzbc.p6.rpc.cell.msg.CellContentDM.Companion.toModel
import com.qxdzbc.p6.rpc.cell.msg.CellIdDM
import com.qxdzbc.p6.rpc.cell.msg.CellIdDM.Companion.toModel

/**
 * This direct-mapping (DM) request is for Rpc, prefer not to use it for in-app operation because it is slower the other
 */
data class CellUpdateRequestDM(
    val cellId:CellIdDM,
    val cellContent: CellContentDM
) : WbWs by cellId{
    val cellAddress: CellAddress get()=cellId.address
    companion object {
        fun CellUpdateRequestProto.toModel():CellUpdateRequestDM{
            return CellUpdateRequestDM(
                cellId = cellId.toModel(),
                cellContent = this.cellContent.toModel()
            )
        }
    }
}
