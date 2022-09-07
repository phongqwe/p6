package com.qxdzbc.p6.app.document.cell.d

import com.github.michaelbull.result.map
import com.qxdzbc.common.Rse
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.app.document.cell.address.toModel
import com.qxdzbc.p6.app.document.cell.d.CellValue.Companion.toModel
import com.qxdzbc.p6.proto.DocProtos
import com.qxdzbc.p6.proto.DocProtos.CellProto
import com.qxdzbc.p6.translator.P6Translator
import com.qxdzbc.p6.translator.formula.execution_unit.ExUnit
import com.qxdzbc.common.compose.StateUtils.toMs
import com.qxdzbc.p6.app.document.cell.address.GenericCellAddress
import java.util.*

data class CellImp(
    override val address: CellAddress,
    override val content: CellContent = CellContentImp()
) : BaseCell() {

    companion object {
        fun CellProto.toModel(translator: P6Translator<ExUnit>): Cell {
            if(this.hasFormula() && this.formula.isNotEmpty()){
                val transRs = translator.translate(formula)
                val content = CellContentImp.fromTransRs(transRs)
                return CellImp(
                    address = address.toModel(),
                    content = content
                )
            }else{
                return CellImp(
                    address = address.toModel(),
                    content = CellContentImp(
                        cellValueMs = this.value.toModel().toMs(),
                    )
                )
            }
        }
    }

    override fun shift(oldAnchorCell: GenericCellAddress<Int, Int>, newAnchorCell: GenericCellAddress<Int, Int>): Cell {
        val newAddress:CellAddress = address.shift(oldAnchorCell, newAnchorCell)
        val newContent:CellContent = content.shift(oldAnchorCell, newAnchorCell)
        return this.copy(
            address=newAddress,
            content = newContent
        )
    }

    override fun reRun(): Cell? {
        return reRunRs().component1()
    }

    override fun reRunRs(): Rse<Cell> {
        val c = content.reRunRs()
        val rt = c.map { this.copy(content = it) }
        return rt
    }

    override fun setAddress(newAddress: CellAddress): Cell {
        return this.copy(address = newAddress)
    }

    override fun setContent(content: CellContent): Cell {
        return this.copy(content = content)
    }

    override fun setCellValue(i: CellValue): Cell {
        val rs = this.content
            .setValue(i)
        return this.setContent(rs)
    }

    override fun toProto(): DocProtos.CellProto {
        val rt = CellProto.newBuilder()
            .setAddress(this.address.toProto())
            .apply {
                if (this@CellImp.isFormula) {
                    this.setFormula(this@CellImp.formula)
                }
            }
            .setValue(this@CellImp.cellValueAfterRun.toProto())
            .build()
        return rt
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is Cell) {
            return this.address == other.address && this.content == other.content
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(this.address, this.content)
    }

    override fun toString(): String {
        return "DCellImp[address=${address},content=${content}]"
    }
}

