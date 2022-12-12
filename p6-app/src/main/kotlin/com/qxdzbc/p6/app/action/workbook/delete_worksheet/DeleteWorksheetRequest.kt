package com.qxdzbc.p6.app.action.workbook.delete_worksheet

import com.qxdzbc.p6.app.communication.res_req_template.request.RequestWithWorkbookKey
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.proto.WorkbookProtos

data class DeleteWorksheetRequest(
    override val wbKey: WorkbookKey,
    val targetWorksheet:String
): RequestWithWorkbookKey {
//    override fun toProtoBytes(): ByteString {
//        return this.toProto().toByteString()
//    }
    fun toProto(): WorkbookProtos.DeleteWorksheetRequestProto {
        val rt = WorkbookProtos.DeleteWorksheetRequestProto.newBuilder()
            .setWorkbookKey(wbKey.toProto())
            .setTargetWorksheet(targetWorksheet)
            .build()
        return rt
    }
}
