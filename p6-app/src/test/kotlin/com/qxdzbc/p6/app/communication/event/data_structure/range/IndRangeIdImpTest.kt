package com.qxdzbc.p6.app.communication.event.data_structure.range


import com.qxdzbc.p6.app.action.range.IndRangeIdImp
import com.qxdzbc.p6.app.action.range.IndRangeIdImp.Companion.toModel
import com.qxdzbc.p6.app.document.range.address.RangeAddress
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.proto.DocProtos.RangeIdProto
import kotlin.test.Test
import kotlin.test.assertEquals

class IndRangeIdImpTest {

    @Test
    fun toProto() {
        val m= IndRangeIdImp(
            rangeAddress = RangeAddress("C1:J2"),
            wbKey = WorkbookKey("bb"),
            wsName = "QWE"
        )
        val p = m.toProto()
        assertEquals(m.rangeAddress.toProto(), p.rangeAddress)
        assertEquals(m.wbKey.toProto(),p.wbKey)
        assertEquals(m.wsName,p.wsName)
    }

    @Test
    fun fromProto(){
        val m= IndRangeIdImp(
            rangeAddress = RangeAddress("C1:J2"),
            wbKey = WorkbookKey("bb"),
            wsName = "QWE"
        )
        val p = RangeIdProto.newBuilder()
            .setRangeAddress(m.rangeAddress.toProto())
            .setWbKey(m.wbKey.toProto())
            .setWsName(m.wsName)
            .build()
        val m2 = p.toModel()
        assertEquals(m2.rangeAddress, m.rangeAddress)
        assertEquals(m2.wbKey, m.wbKey)
        assertEquals(m2.wsName, m.wsName)
    }
}
