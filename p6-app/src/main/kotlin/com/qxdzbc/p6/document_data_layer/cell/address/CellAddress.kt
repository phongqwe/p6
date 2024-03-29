package com.qxdzbc.p6.document_data_layer.cell.address

import com.qxdzbc.p6.common.utils.CellLabelNumberSystem
import com.qxdzbc.p6.document_data_layer.Shiftable
import com.qxdzbc.p6.proto.DocProtos

/**
 * factory method to create a standard cell address from col, row indices
 */
fun CellAddress(col: Int, row: Int): CellAddress {
    return CellAddresses.fromIndices(col, row)
}

fun CellAddress(colCR: CR, rowCR: CR): CellAddress {
    return CellAddresses.fromCR(colCR, rowCR)
}

/**
 * factory method to create a standard cell address from a cell label
 */
fun CellAddress(label: String): CellAddress {
    return CellAddresses.fromLabel(label)
}

interface CellAddress : CRAddress<Int, Int>, com.qxdzbc.p6.document_data_layer.Shiftable {

    companion object {
        fun random(colRange: IntRange = 1..20, rowRange: IntRange = 1..20): CellAddress {
            return CellAddress(colRange.random(), rowRange.random())
        }
    }

    override val colIndex: Int
    override val rowIndex: Int

    val colCR: CR
    val rowCR: CR

    fun nextLockState(): CellAddress

    val isColLocked: Boolean
    fun setLockCol(i: Boolean): CellAddress
    fun unlockCol(): CellAddress
    fun lockCol(): CellAddress

    val isRowLocked: Boolean
    fun setLockRow(i: Boolean): CellAddress
    fun unlockRow(): CellAddress
    fun lockRow(): CellAddress

    fun lock(): CellAddress
    fun unlock(): CellAddress

    /**
     * Shift this address using the vector defined by: [oldAnchorCell] --> [newAnchorCell].
     * See the test for more detail
     */
    override fun shift(
        oldAnchorCell: CRAddress<Int, Int>,
        newAnchorCell: CRAddress<Int, Int>
    ): CellAddress

    fun increaseRowBy(v: Int): CellAddress
    fun increaseColBy(v: Int): CellAddress


    /**
     * decrease row index by 1
     */
    fun upOneRow(): CellAddress {
        return increaseRowBy(-1)
    }

    /**
     * increase row index by 1
     */
    fun downOneRow(): CellAddress {
        return increaseRowBy(1)
    }

    /**
     * increase col index by 1
     */
    fun rightOneCol(): CellAddress {
        return increaseColBy(1)
    }

    /**
     * decrease col index by 1
     */
    fun leftOneCol(): CellAddress {
        return increaseColBy(-1)
    }

    val label: String
        get() {
            val colLabel: String = CellLabelNumberSystem.numberToLabel(colIndex)
            return "${if (isColLocked) "\$" else ""}${colLabel}${if (isRowLocked) "\$" else ""}${rowIndex}"
        }

    fun toProto(): DocProtos.CellAddressProto

    fun isValid(): Boolean {
        return colIndex >= 1 && rowIndex >= 1
    }

    fun isNotValid(): Boolean {
        return !this.isValid()
    }

    fun isSame(other: CellAddress): Boolean

    /**
     * Generate a sequence of cell address starting from this cell to the cell at:
     *  - row == this cell's row
     *  - col == [col]
     *  @param col target column
     *  @param includeThis include this cell in the result or not
     */
    fun generateCellSequenceToCol(col: Int, includeThis: Boolean = true): List<CellAddress>

    /**
     * Generate a sequence of cell address starting from this cell to the cell at:
     *  - col == this cell's col
     *  - row == [row]
     *  @param row target column
     *  @param includeThis include this cell in the result or not
     */
    fun generateCellSequenceToRow(row: Int, includeThis: Boolean = true): List<CellAddress>
}
