package com.qxdzbc.p6.app.action.range.paste_range

import com.qxdzbc.p6.app.action.common_data_structure.WbWsImp
import com.qxdzbc.p6.app.communication.res_req_template.request.RequestWithWorkbookKeyAndWindowId
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.proto.RangeProtos.PasteRangeRequestProto

class PasteRangeRequest(
    val anchorCell: CellAddress,
    val wbWs: WbWsImp,
    override val windowId: String?
): RequestWithWorkbookKeyAndWindowId {
    fun toProto():PasteRangeRequestProto{
        return PasteRangeRequestProto.newBuilder()
            .setAnchorCell(anchorCell.toProto())
            .setWsWb(this.wbWs.toProto())
            .apply {
                this@PasteRangeRequest.windowId?.also {
                    setWindowId(it)
                }
            }
            .build()
    }

//    override fun toProtoBytes(): ByteString {
//        return this.toProto().toByteString()
//    }

    override val wbKey: WorkbookKey?
        get() = wbWs.wbKey
}
