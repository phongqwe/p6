package com.qxdzbc.p6.composite_actions.workbook.new_worksheet

import com.qxdzbc.p6.composite_actions.common_data_structure.WbWs
import com.qxdzbc.p6.rpc.communication.res_req_template.request.RequestWithWorkbookKey
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.document_data_layer.workbook.toModel
import com.qxdzbc.p6.proto.WorkbookProtos

data class CreateNewWorksheetRequest(
    override val wbKey: WorkbookKey,
    val newWorksheetName: String?
) : RequestWithWorkbookKey,WbWs {

    companion object
    {
        fun WorkbookProtos.CreateNewWorksheetRequestProto.toModel(): CreateNewWorksheetRequest {
            return CreateNewWorksheetRequest(
                wbKey= this.wbKey.toModel(),
                newWorksheetName = if(this.hasNewWorksheetName()) this.newWorksheetName else null
            )
        }
    }

//    override fun toProtoBytes(): ByteString {
//        return this.toProto().toByteString()
//    }
    fun toProto(): WorkbookProtos.CreateNewWorksheetRequestProto {
        val rt = WorkbookProtos.CreateNewWorksheetRequestProto.newBuilder()
            .setWbKey(wbKey.toProto())
            .apply{
                this@CreateNewWorksheetRequest.newWorksheetName?.also {
                    setNewWorksheetName(it)
                }
            }
            .build()
        return rt
    }

    override val wsName: String
        get() = this.newWorksheetName ?: ""
}


