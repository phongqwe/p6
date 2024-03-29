package com.qxdzbc.p6.translator.formula.formula.execution_unit

import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddress
import com.qxdzbc.p6.translator.formula.execution_unit.obj_type_in_app.RangeAddressUnit
import kotlin.test.*

class RangeAddressUnitTest {
    val r = RangeAddress("A1:B2")
    val u = RangeAddressUnit(rangeAddress = r)

    @Test
    fun getRanges(){
        assertEquals(listOf(r),u.getRanges())
    }

    @Test
    fun shift() {
        val from = CellAddress("F2")
        val toCell = CellAddress("Q9")

        assertEquals(RangeAddressUnit(r.shift(from, toCell)), u.shift(from, toCell))
    }

    @Test
    fun toFormula() {
        assertEquals("A1:B2", u.toFormula())
    }
}
