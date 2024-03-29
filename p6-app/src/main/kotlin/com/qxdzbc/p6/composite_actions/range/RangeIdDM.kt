package com.qxdzbc.p6.composite_actions.range

import com.qxdzbc.p6.document_data_layer.range.address.RangeAddress
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddressUtils.toModel
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.document_data_layer.workbook.toModel
import com.qxdzbc.p6.proto.DocProtos.RangeIdProto

data class RangeIdDM(
    override val rangeAddress: RangeAddress,
    override val wbKey: WorkbookKey,
    override val wsName: String
):RangeId {
    override fun toProto():RangeIdProto{
        val proto = RangeIdProto.newBuilder()
            .setRangeAddress(this.rangeAddress.toProto())
            .setWbKey(this.wbKey.toProto())
            .setWsName(this.wsName)
            .build()
        return proto
    }

    override fun toDm(): RangeId {
        return this
    }

    companion object{
        fun RangeIdProto.toModel(): RangeIdDM {
            return RangeIdDM(
                rangeAddress = rangeAddress.toModel(),
                wbKey = wbKey.toModel(),
                wsName = wsName
            )
        }
    }
}



