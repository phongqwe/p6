package com.qxdzbc.p6.ui.document.worksheet.state

import androidx.compose.runtime.MutableState
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.ui.document.cell.state.CellState

abstract class BaseWorksheetState : WorksheetState {

    override fun getCellState(label: String): CellState? {
        return this.getCellState(CellAddress(label))
    }

    override fun refresh(): WorksheetState {
        return this.refreshCellState()
    }

    override fun getCellState(colIndex: Int, rowIndex: Int): CellState? {
        return this.getCellState(CellAddress(colIndex, rowIndex))
    }

    override fun getCellStateMs(colIndex: Int, rowIndex: Int): MutableState<CellState>? {
        return this.getCellStateMs(CellAddress(colIndex, rowIndex))
    }

    override fun getCellState(cellAddress: CellAddress): CellState? {
        return this.getCellStateMs(cellAddress)?.value
    }

    override fun getColumnWidthOrDefault(colIndex: Int): Int {
        return this.getColumnWidth(colIndex) ?: defaultColWidth
    }

    override fun changeColSize(colIndex: Int, sizeDiff: Float): WorksheetState {
        val sd = sizeDiff.toInt()
        if (sd != 0) {
            val oldSize = this.getColumnWidth(colIndex) ?: defaultColWidth
            val newSize = oldSize + sd
            if (newSize == defaultColWidth) {
                return this.restoreColumnWidthToDefault(colIndex)
            } else {
                return this.addColumnWidth(colIndex, maxOf(oldSize + sd, 0))
            }
        }
        return this
    }

    override fun changeRowSize(rowIndex: Int, sizeDiff: Float): WorksheetState {
        val sd = sizeDiff.toInt()
        if (sd != 0) {
            val wsState = this
            val oldSize = wsState.getRowHeight(rowIndex) ?: defaultRowHeight
            val newSize = oldSize + sd
            if (newSize == defaultRowHeight) {
                return wsState.restoreRowHeightToDefault(rowIndex)
            } else {
                return wsState.addRowHeight(rowIndex, maxOf(oldSize + sd, 0))
            }
        }
        return this
    }

    private val columnWidthMap: Map<Int, Int> get() = colRulerState.itemSizeMap
    private val rowHeightMap: Map<Int, Int> get() = rowRulerState.itemSizeMap
    override val defaultColWidth: Int get() = colRulerState.defaultItemSize
    override val defaultRowHeight: Int get() = rowRulerState.defaultItemSize

    override fun getRowHeightOrDefault(rowIndex: Int): Int {
        return this.getRowHeight(rowIndex) ?: defaultRowHeight
    }

    override fun getColumnWidth(colIndex: Int): Int? {
        return this.columnWidthMap[colIndex]
    }

    override fun addColumnWidth(colIndex: Int, colWidth: Int): WorksheetState {
        this.colRulerStateMs.value = this.colRulerState.setItemSize(colIndex,colWidth)
        return this
    }

    override fun restoreColumnWidthToDefault(colIndex: Int): WorksheetState {
        this.colRulerStateMs.value = this.colRulerState.removeItemSize(colIndex)
        return this
    }

    override fun getRowHeight(rowIndex: Int): Int? {
        return this.rowHeightMap[rowIndex]
    }

    override fun addRowHeight(rowIndex: Int, rowHeight: Int): WorksheetState {
        this.rowRulerStateMs.value = this.rowRulerState.setItemSize(rowIndex,  rowHeight)
        return this
    }

    override fun restoreRowHeightToDefault(rowIndex: Int): WorksheetState {
        rowRulerStateMs.value = rowRulerState.removeItemSize(rowIndex)
        return this
    }
}