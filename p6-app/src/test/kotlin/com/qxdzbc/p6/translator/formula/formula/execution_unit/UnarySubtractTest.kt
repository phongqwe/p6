package com.qxdzbc.p6.translator.formula.formula.execution_unit

import com.github.michaelbull.result.Err
import com.qxdzbc.common.ResultUtils.toOk
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddress
import com.qxdzbc.p6.translator.formula.execution_unit.obj_type_in_app.CellAddressUnit
import com.qxdzbc.p6.translator.formula.execution_unit.primitive.IntUnit.Companion.toExUnit
import com.qxdzbc.p6.translator.formula.execution_unit.operator.UnarySubtract
import kotlin.test.*

class UnarySubtractTest:OperatorBaseTest(){

    @Test
    fun `error case`(){
        val u = UnarySubtract(ots.makeMockCellUnit("qwe"))
        assertTrue(u.runRs() is Err)
    }

    @Test
    fun `-cell`(){
        val u = UnarySubtract(ots.makeMockCellUnit(1))
        assertEquals((-1.0).toOk(),u.runRs())
    }

    @Test
    fun `-num`(){
        val u = UnarySubtract(1.toExUnit())
        assertEquals((-1.0).toOk(),u.runRs())
    }

    @Test
    fun getRange(){
        val u = UnarySubtract(
            CellAddressUnit(CellAddress("C3")),
        )
        assertEquals(
            listOf(RangeAddress(CellAddress("C3"))),
            u.getRanges()
        )
    }
}
