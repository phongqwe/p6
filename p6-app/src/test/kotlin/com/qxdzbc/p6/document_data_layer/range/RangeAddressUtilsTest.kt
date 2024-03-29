package com.qxdzbc.p6.document_data_layer.range

import com.qxdzbc.p6.common.utils.CellLabelNumberSystem
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddresses
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddressImp
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddressUtils
import com.github.michaelbull.result.Ok
import com.qxdzbc.p6.ui.worksheet.WorksheetConstants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RangeAddressUtilsTest {

    @Test
    fun fromLabel() {
        val inputMap = mapOf(
            "A1:B2" to RangeAddressImp(CellAddress("A1"), CellAddress("B2")),
            "a1:b2" to RangeAddressImp(CellAddress("A1"), CellAddress("B2")),
            "A$1:\$B2" to RangeAddressImp(CellAddress("A$1"), CellAddress("\$B2")),
            "\$A$1:\$B$2" to RangeAddressImp(CellAddress("\$A$1"), CellAddress("\$B$2")),
            "A1:B32" to RangeAddressImp(CellAddress("A1"), CellAddress("B32")),
            "F:X" to RangeAddressImp(CellAddress("F1"), CellAddress("X${WorksheetConstants.rowLimit}")),
            "\$F:\$X" to RangeAddressImp(CellAddress("\$F$1"), CellAddress("\$X$${WorksheetConstants.rowLimit}")),
            "33:44" to RangeAddressImp(
                CellAddress("A33"),
                CellAddress(CellLabelNumberSystem.numberToLabel(WorksheetConstants.colLimit) + "44")
            ),
            "\$33:\$44" to RangeAddressImp(
                CellAddress("\$A$33"),
                CellAddress("\$" + CellLabelNumberSystem.numberToLabel(WorksheetConstants.colLimit) + "$44")
            )
        )

        for ((l, r) in inputMap) {
            val rs = RangeAddressUtils.rangeFromLabelRs(l)
            assertTrue(rs is Ok)
            assertEquals(r, rs.component1())
        }

        assertFailsWith(IllegalArgumentException::class) {
            RangeAddressUtils.rangeFromLabel("123abc")
        }

        assertFailsWith(IllegalArgumentException::class) {
            RangeAddressUtils.rangeFromLabel("1:A")
        }
        assertFailsWith(IllegalArgumentException::class) {
            RangeAddressUtils.rangeFromLabel("a:1")
        }
    }

    @Test
    fun fromCells() {
        val c1 = CellAddresses.fromIndices(1, 1)
        val c2 = CellAddresses.fromIndices(3, 2)
        val p1 = RangeAddressUtils.rangeFor2Cells(c1, c2)
        val p2 = RangeAddressUtils.rangeFor2Cells(c2, c1)
        val e = RangeAddressImp(c1, c2)
        assertEquals(e, p1)
        assertEquals(e, p2)
    }

    @Test
    fun singleCell() {
        val c2 = CellAddresses.fromIndices(3, 2)
        val p1 = RangeAddressUtils.rangeFromSingleCell(c2)
        assertEquals(c2, p1.topLeft)
        assertEquals(c2, p1.botRight)
    }

    @Test
    fun wholeCol() {
        val p = RangeAddressUtils.rangeForWholeCol(333)
        assertEquals(CellAddresses.fromIndices(333, 1), p.topLeft)
        assertEquals(CellAddresses.fromIndices(333, WorksheetConstants.rowLimit), p.botRight)
    }

    @Test
    fun wholeRow() {
        val p = RangeAddressUtils.rangeForWholeRow(312)
        assertEquals(CellAddresses.fromIndices(1, 312), p.topLeft)
        assertEquals(CellAddresses.fromIndices(WorksheetConstants.colLimit, 312), p.botRight)
    }

    @Test
    fun fromManyCell() {
        val cells = listOf(CellAddress(1, 2), CellAddress(2, 3), CellAddress(4, 2))
        val p = RangeAddressUtils.rangeForMultiCells(cells)
        assertEquals(CellAddress(1, 2), p.topLeft)
        assertEquals(CellAddress(4, 3), p.botRight)
    }

    @Test
    fun wholeMultiRow() {
        val p = RangeAddressUtils.rangeForWholeMultiRow(2, 4)
        assertEquals(CellAddress(1, 2), p.topLeft)
        assertEquals(CellAddress(WorksheetConstants.colLimit, 4), p.botRight)
        assertEquals(RangeAddressUtils.rangeForWholeMultiRow(2, 4), RangeAddressUtils.rangeForWholeMultiRow(4, 2))
    }

    @Test
    fun wholeMultiCol() {
        val p = RangeAddressUtils.rangeForWholeMultiCol(2, 4)
        assertEquals(CellAddress(2, 1), p.topLeft)
        assertEquals(CellAddress(4, WorksheetConstants.rowLimit), p.botRight)
        assertEquals(RangeAddressUtils.rangeForWholeMultiCol(2, 4), RangeAddressUtils.rangeForWholeMultiCol(4, 2))
    }
}
