package com.qxdzbc.p6.document_data_layer.cell

import com.github.michaelbull.result.map
import com.qxdzbc.common.Rse
import com.qxdzbc.common.compose.St
import com.qxdzbc.common.error.ErrorReport
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.proto.DocProtos.CellProto
import com.qxdzbc.p6.document_data_layer.cell.address.CRAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey

/**
 * a [Cell] implementation that is not tied to workbook key state nor worksheet name state.
 * For test and data conversion only
 */
data class IndCellImp(
    override val address: CellAddress,
    override val content: CellContent = CellContentImp.empty,
    override val externalEvalError: ErrorReport? = null,
    override val cachedDisplayText: String = "",
) : BaseCell() {

    companion object {
        fun random():IndCellImp{
            return IndCellImp.random(CellAddress.random())
        }
        fun random(address: CellAddress):IndCellImp{
            val ct = CellContent.randomNumericContent()
            return IndCellImp(
                address = address,
                content = ct,
                cachedDisplayText = ct.displayText
            )
        }
    }

    override fun setExternalEvalError(i: ErrorReport?): Cell {
        return this.copy(externalEvalError=i)
    }

    override fun shift(oldAnchorCell: CRAddress<Int, Int>, newAnchorCell: CRAddress<Int, Int>): Cell {
        val newAddress:CellAddress = address.shift(oldAnchorCell, newAnchorCell)
        val newContent: CellContent = content.shift(oldAnchorCell, newAnchorCell)
        return this.copy(
            address=newAddress,
            content = newContent
        )
    }

    override fun reRunRs(): Rse<Cell> {
        val c = content.reRunRs()
        val rt = c.map { this.copy(content = it) }
        return rt
    }

    override val id: CellId
        get() = throw UnsupportedOperationException()

    override fun attemptToAccessDisplayText(): String {
        return cachedDisplayText
    }

    override fun evaluateDisplayText(): Cell {
        throw UnsupportedOperationException()
    }

    override val wbKeySt: St<WorkbookKey>
        get() = throw UnsupportedOperationException()
    override val wbKey: WorkbookKey
        get() = throw UnsupportedOperationException()
    override val wsNameSt: St<String>
        get() = throw UnsupportedOperationException()
    override val wsName: String
        get() = throw UnsupportedOperationException()

    override fun setAddress(newAddress: CellAddress): Cell {
        return this.copy(address = newAddress)
    }

    override fun setContent(content: CellContent): Cell {
        return this.copy(content = content)
    }

    override fun setCellValue(i: CellValue): Cell {
        val newContent = this.content
            .setValueAndDeleteExUnit(i)
        return this.setContent(newContent)
    }

    override fun toProto(): CellProto {
        throw UnsupportedOperationException()
    }

    override fun toString(): String {
        return "IndCellImp[address=${address},content=${content}]"
    }
}

