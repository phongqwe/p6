package com.qxdzbc.p6.translator.formula.formula.execution_unit

import com.qxdzbc.common.compose.StateUtils.toSt
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.translator.formula.execution_unit.CellAddressUnit.Companion.exUnit
import com.qxdzbc.p6.translator.formula.execution_unit.GetCell
import com.qxdzbc.p6.translator.formula.execution_unit.WbKeyStUnit.Companion.exUnit
import com.qxdzbc.p6.translator.formula.execution_unit.WsNameStUnit
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
        val u = GetCell(
            funcName = "qwe",
            wbKeyUnit = WorkbookKey("Wb1").toSt().exUnit(),
            wsNameUnit = WsNameStUnit("Sheet1".toSt()),
            cellAddressUnit = CellAddress("B2").exUnit(),
            functionMapSt = mock(),

        )
        assertEquals("B2@'Sheet1'@'Wb1'", u.toFormula())
    }

    @Test
    fun toFormulaSelective() {
        val wbk1 = WorkbookKey("Wb1", null)
        val wbk2 = WorkbookKey("Wb2", null)
        val u = GetCell(
            funcName = "qwe",
            wbKeyUnit = wbk1.toSt().exUnit(),
            wsNameUnit = WsNameStUnit("Sheet1".toSt()),
            cellAddressUnit = CellAddress("B2").exUnit(),
            functionMapSt = mock()
        )
        assertEquals("B2@'Sheet1'", u.toShortFormula(wbk1, "Sheet2"))
        assertEquals("B2", u.toShortFormula(wbk1, "Sheet1"))
        assertEquals("B2@'Sheet1'@'Wb1'", u.toShortFormula(wbk2, "Sheet1"))
    }
}
