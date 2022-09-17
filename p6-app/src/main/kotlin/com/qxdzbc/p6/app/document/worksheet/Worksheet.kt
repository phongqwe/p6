package com.qxdzbc.p6.app.document.worksheet

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.qxdzbc.common.Rse
import com.qxdzbc.common.WithSize
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.St
import com.qxdzbc.common.error.ErrorReport
import com.qxdzbc.p6.app.action.common_data_structure.WbWsSt
import com.qxdzbc.p6.app.common.table.TableCR
import com.qxdzbc.p6.app.document.cell.CellId
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.app.document.cell.d.Cell
import com.qxdzbc.p6.app.document.cell.d.CellContent
import com.qxdzbc.p6.app.document.cell.d.CellImp
import com.qxdzbc.p6.app.document.cell.d.IndCellImp
import com.qxdzbc.p6.app.document.range.Range
import com.qxdzbc.p6.app.document.range.address.RangeAddress
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.proto.DocProtos.WorksheetProto
import com.qxdzbc.p6.translator.P6Translator
import com.qxdzbc.p6.translator.formula.execution_unit.ExUnit
import com.qxdzbc.p6.ui.document.worksheet.state.RangeConstraint
import com.qxdzbc.p6.ui.document.worksheet.state.WorksheetId

interface Worksheet : WithSize, WbWsSt {

//    fun isSimilar(ws:Worksheet):Boolean

    fun reRun(): Worksheet

    val idMs: Ms<WorksheetId>
    var id: WorksheetId

    val nameMs: Ms<String>
    val name: String

    fun setWbKeySt(wbKeySt: St<WorkbookKey>): Worksheet

    val usedRange: RangeAddress

    val table: TableCR<Int, Int, Ms<Cell>>
    val cells: List<Cell> get() = cellMsList.map { it.value }
    val cellMsList: List<Ms<Cell>>
    val rangeConstraint: RangeConstraint
    override val size: Int get() = table.itemCount

    fun toProto(): WorksheetProto

    /**
     * return a range derived from this worksheet
     */
    fun range(address: RangeAddress): Result<Range, ErrorReport>

    fun updateCellValue(cellAddress: CellAddress, value: Any?): Result<Worksheet, ErrorReport>
    fun updateCellContentRs(cellAddress: CellAddress, cellContent: CellContent): Result<Worksheet, ErrorReport>

    fun getCellsInRange(rangeAddress: RangeAddress): List<Cell> {
        return this.getCellMsInRange(rangeAddress).map { it.value }
    }

    fun getCellMsInRange(rangeAddress: RangeAddress): List<Ms<Cell>>

    fun getCellMs(cellAddress: CellAddress): Ms<Cell>? {
        return table.getElement(cellAddress.colIndex, cellAddress.rowIndex)
    }

    fun getCellMs(colIndex: Int, rowIndex: Int): Ms<Cell>? {
        return table.getElement(colIndex, rowIndex)
    }

    fun getCellMs(label: String): Ms<Cell>? {
        return table.getElement(CellAddress(label))
    }

    fun getCellMsRs(cellAddress: CellAddress): Rse<Ms<Cell>> {
        val cellMs: Ms<Cell>? = this.table.getElement(cellAddress)
        val rt: Rse<Ms<Cell>> = cellMs?.let {
            Ok(it)
        } ?: WorksheetErrors.InvalidCell.report(cellAddress).toErr()
        return rt
    }

    fun getCell(cellAddress: CellAddress): Cell? {
        return getCellMs(cellAddress)?.value
    }

    fun getCell(colIndex: Int, rowIndex: Int): Cell? {
        return getCellMs(colIndex, rowIndex)?.value
    }

    fun getCell(label: String): Cell? {
        return getCellMs(CellAddress(label))?.value
    }

    fun getCellOrDefaultRs(cellAddress: CellAddress): Result<Cell, ErrorReport> {
        if (rangeConstraint.contains(cellAddress)) {
            return Ok(getCell(cellAddress) ?: CellImp(CellId(cellAddress,wbKeySt, wsNameSt)))
        } else {
            return Err(WorksheetErrors.InvalidCell(cellAddress))
        }
    }

    fun addOrOverwrite(cell: Cell): Worksheet

    fun getColMs(colIndex: Int): List<Ms<Cell>> {
        return table.getCol(colIndex)
    }

    fun getRowMs(rowIndex: Int): List<Ms<Cell>> {
        return table.getRow(rowIndex)
    }

    fun getCol(colIndex: Int): List<Cell> {
        return getColMs(colIndex).map { it.value }
    }

    fun getRow(rowIndex: Int): List<Cell> {
        return getRowMs(rowIndex).map { it.value }
    }

    fun removeCol(colIndex: Int): Worksheet
    fun removeRow(rowIndex: Int): Worksheet
    fun removeCell(colKey: Int, rowKey: Int): Worksheet
    fun removeCell(cellAddress: CellAddress): Worksheet {
        return this.removeCell(cellAddress.colIndex, cellAddress.rowIndex)
    }

    fun removeCells(cells: Collection<CellAddress>): Worksheet
    fun setWsName(newName: String): Worksheet
    fun withNewData(wsProto: WorksheetProto, translator: P6Translator<ExUnit>): Worksheet
}
