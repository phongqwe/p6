package com.qxdzbc.p6.rpc.document.workbook.msg

import com.qxdzbc.p6.common.proto.ProtoUtils.toProto
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.document_data_layer.worksheet.WorksheetImp
import com.qxdzbc.common.compose.StateUtils.ms
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.qxdzbc.p6.rpc.workbook.msg.GetAllWorksheetsResponse
import test.TestSample
import kotlin.test.*

class GetAllWorksheetsResponseTest {

    val l = listOf(WorksheetImp(nameMs = ms("ws1"), wbKeySt = ms(WorkbookKey("wb1"))))

    @Test
    fun toProto() {
        val i1 = GetAllWorksheetsResponse(l)
        val p1 = i1.toProto()
        assertFalse(p1.hasErrorReport())
        assertEquals(i1.worksheets?.get(0)?.toProto(), p1.worksheetsList[0])

        val i2 = GetAllWorksheetsResponse(errorReport = TestSample.sampleErrorReport)
        val p2 = i2.toProto()
        assertTrue(p2.hasErrorReport())
        assertEquals(i2.errorReport?.toProto(), p2.errorReport)
    }

    @Test
    fun fromRs() {
        val rs1 = Ok(l)
        val g1 = GetAllWorksheetsResponse.fromRs(rs1)
        assertEquals(l, g1.worksheets)

        val rs2 = Err(TestSample.sampleErrorReport)
        val g2 = GetAllWorksheetsResponse.fromRs(rs2)
        assertEquals(rs2.error, g2.errorReport)
    }
}
