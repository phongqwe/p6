package com.qxdzbc.p6.composite_actions.worksheet.rename_ws

import com.google.protobuf.ByteString
import com.qxdzbc.common.error.ErrorReport
import com.qxdzbc.p6.common.proto.ProtoUtils.toModel
import com.qxdzbc.p6.common.proto.ProtoUtils.toProto
import com.qxdzbc.p6.rpc.communication.res_req_template.response.ResponseWith_WbKey
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.document_data_layer.workbook.toModel
import com.qxdzbc.p6.proto.WorksheetProtos

data class RenameWorksheetResponse(
    override val wbKey: WorkbookKey,
    val oldName: String,
    val newName: String,
    override val isError: Boolean = false,
    override val errorReport: ErrorReport? = null
) : ResponseWith_WbKey {

    companion object {
        fun fromProtoBytes(protoBytes: ByteString): RenameWorksheetResponse {
            return WorksheetProtos.RenameWorksheetResponseProto.newBuilder().mergeFrom(protoBytes).build().toModel()
        }
    }

    /**
     * for testing, consider move it to test package
     */
    fun toProto(): WorksheetProtos.RenameWorksheetResponseProto {
        val builder = WorksheetProtos.RenameWorksheetResponseProto.newBuilder()
            .setWorkbookKey(this.wbKey.toProto())
            .setOldName(oldName)
            .setNewName(newName)
            .setIsError(isError)
        if (this.errorReport != null) {
            builder.setErrorReport(this.errorReport.toProto())
        }
        val rt = builder.build()
        return rt
    }
}

fun WorksheetProtos.RenameWorksheetResponseProto.toModel(): RenameWorksheetResponse {
    val rt = RenameWorksheetResponse(
        wbKey = workbookKey.toModel(),
        oldName = oldName,
        newName = newName,
        isError = isError,
        errorReport = if (this.hasErrorReport()) {
            this.errorReport.toModel()
        } else {
            null
        }
    )
    return rt
}

