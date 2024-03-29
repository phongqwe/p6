package com.qxdzbc.p6.communication.event.data_structure.workbook_event

import com.qxdzbc.p6.common.proto.ProtoUtils.toProto
import com.qxdzbc.p6.composite_actions.app.save_wb.SaveWorkbookResponse
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.document_data_layer.workbook.toModel
import com.qxdzbc.common.error.SingleErrorReport
import com.qxdzbc.p6.proto.AppProtos
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SaveWorkbookResponseTest{
    @Test
    fun fromProtoBytes(){
        val proto = AppProtos.SaveWorkbookResponseProto.newBuilder()
            .setWbKey(WorkbookKey("B").toProto())
            .setPath("folder123/file.txt")
            .build()

        val o = SaveWorkbookResponse.fromProtoBytes(proto.toByteString())
        assertEquals(proto.wbKey.toModel(),o.wbKey)
        assertEquals(proto.path, o.path)
        assertNull(o.errorReport)
    }

    @Test
    fun `fromProtoBytes error case`(){
        val er = SingleErrorReport.random()

        val proto = AppProtos.SaveWorkbookResponseProto.newBuilder()
            .setWbKey(WorkbookKey("B").toProto())
            .setErrorReport(er.toProto())
            .build()

        val o = SaveWorkbookResponse.fromProtoBytes(proto.toByteString())
        assertEquals(proto.wbKey.toModel(),o.wbKey)
        assertEquals(proto.path, o.path)
        assertTrue { o.errorReport?.isType(er.header) ?: false}
    }
}
