package com.qxdzbc.p6.rpc.cell.msg

import com.qxdzbc.p6.app.action.common_data_structure.WbWs
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.app.document.cell.address.toModel
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.app.document.workbook.toModel
import com.qxdzbc.p6.proto.DocProtos.CellIdProto

/**
 * A direct mapping to [CellIdProto]
 */
data class CellIdDM(
    val address: CellAddress,
    override val wbKey:WorkbookKey,
    override val wsName:String,
) :WbWs{
    companion object{
        fun CellIdProto.toModel():CellIdDM{
            return CellIdDM(
                address = this.cellAddress.toModel(),
                wbKey = this.wbKey.toModel(),
                wsName = this.wsName
            )
        }
    }
    fun toProto():CellIdProto{
        return CellIdProto.newBuilder()
            .setWbKey(this.wbKey.toProto())
            .setWsName(this.wsName)
            .setCellAddress(this.address.toProto())
            .build()
    }
}