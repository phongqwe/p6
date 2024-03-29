package com.qxdzbc.p6.rpc.workbook.msg

import com.qxdzbc.p6.proto.WorksheetProtos
import com.qxdzbc.p6.ui.worksheet.state.WorksheetId

class GetWorksheetResponse(
    val wsId: WorksheetId?
) {
    fun toProto(): WorksheetProtos.GetWorksheetResponseProto {
        return WorksheetProtos.GetWorksheetResponseProto.newBuilder()
            .apply {
                this@GetWorksheetResponse.wsId?.also {
                    this.setWsId(it.toProto())
                }
            }.build()
    }
}
