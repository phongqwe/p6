package com.qxdzbc.p6.translator.formula.formula.execution_unit

import com.qxdzbc.common.compose.StateUtils.toSt
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.translator.formula.execution_unit.obj_type_in_app.CellAddressUnit.Companion.toExUnit
import com.qxdzbc.p6.translator.formula.execution_unit.function.GetCellUnit
import com.qxdzbc.p6.translator.formula.execution_unit.obj_type_in_app.WbKeyStUnit.Companion.toExUnit
import com.qxdzbc.p6.translator.formula.execution_unit.obj_type_in_app.WsNameStUnit
import org.mockito.kotlin.mock
import test.TestSample
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ExUnit_GetCell {
    lateinit var ts: TestSample

    @BeforeTest
    fun b() {
        ts = TestSample()

    }

    @Test
    fun toFormula() {
        val u = GetCellUnit(
            funcName = "qwe",
            wbKeyUnit = WorkbookKey("Wb1").toSt().toExUnit(),
            wsNameUnit = WsNameStUnit("Sheet1".toSt()),
            cellAddressUnit = CellAddress("B2").toExUnit(),
            functionMapSt = mock(),

        )
        assertEquals("B2@'Sheet1'@'Wb1'", u.toFormula())
    }

    @Test
    fun toFormulaSelective() {
        val wbk1 = WorkbookKey("Wb1", null)
        val wbk2 = WorkbookKey("Wb2", null)
        val u = GetCellUnit(
            funcName = "qwe",
            wbKeyUnit = wbk1.toSt().toExUnit(),
            wsNameUnit = WsNameStUnit("Sheet1".toSt()),
            cellAddressUnit = CellAddress("B2").toExUnit(),
            functionMapSt = mock()
        )
        assertEquals("B2@'Sheet1'", u.toShortFormula(wbk1, "Sheet2"))
        assertEquals("B2", u.toShortFormula(wbk1, "Sheet1"))
        assertEquals("B2@'Sheet1'@'Wb1'", u.toShortFormula(wbk2, "Sheet1"))
    }
}
