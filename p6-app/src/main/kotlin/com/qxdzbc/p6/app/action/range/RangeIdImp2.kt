package com.qxdzbc.p6.app.action.range

import com.qxdzbc.common.compose.St
import com.qxdzbc.p6.app.document.range.address.RangeAddress
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.proto.DocProtos

data class RangeIdImp2(
    override val rangeAddress: RangeAddress,
    private val wbKeySt: St<WorkbookKey>,
    private val wsNameSt: St<String>
): RangeId {
    override val wbKey: WorkbookKey
        get()  = wbKeySt.value
    override val wsName: String
        get()  = wsNameSt.value

    override fun toProto(): DocProtos.RangeIdProto {
        val proto = DocProtos.RangeIdProto.newBuilder()
            .setRangeAddress(this.rangeAddress.toProto())
            .setWorkbookKey(this.wbKey.toProto())
            .setWorksheetName(this.wsName)
            .build()
        return proto
    }
}