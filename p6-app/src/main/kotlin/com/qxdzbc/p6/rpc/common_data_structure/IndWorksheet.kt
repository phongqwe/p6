package com.qxdzbc.p6.rpc.common_data_structure

import com.qxdzbc.p6.composite_actions.common_data_structure.WbWs
import com.qxdzbc.p6.proto.DocProtos
import com.qxdzbc.p6.rpc.common_data_structure.IndependentCellDM.Companion.toModel
import com.qxdzbc.p6.rpc.worksheet.msg.WorksheetIdDM
import com.qxdzbc.p6.rpc.worksheet.msg.WorksheetIdDM.Companion.toModelDM

data class IndWorksheet(
    val id: WorksheetIdDM,
    val cells: List<IndependentCellDM>
):WbWs by id {
    companion object {
        fun DocProtos.IndWorksheetProto.toModel(): IndWorksheet {
            return IndWorksheet(
                id = id.toModelDM(),
                cells = this.cellsList.map { it.toModel() }
            )
        }
    }
}
