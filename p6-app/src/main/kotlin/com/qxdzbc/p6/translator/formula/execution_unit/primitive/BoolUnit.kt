package com.qxdzbc.p6.translator.formula.execution_unit.primitive

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.qxdzbc.common.error.SingleErrorReport
import com.qxdzbc.p6.common.utils.Utils.toTF
import com.qxdzbc.p6.document_data_layer.cell.address.CRAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.translator.formula.execution_unit.ExUnit

/**
 * An [ExUnit] representing a boolean.
 */

class BoolUnit(val v: Boolean) : ExUnit {
    companion object{
        val TRUE = BoolUnit(true)
        val FALSE = BoolUnit(false)
        fun Boolean.toExUnit(): BoolUnit {
            return BoolUnit(this)
        }
    }
    override fun shift(
        oldAnchorCell: CRAddress<Int, Int>,
        newAnchorCell: CRAddress<Int, Int>
    ): ExUnit {
        return this
    }

    override fun toFormula(): String {
        return v.toTF()
    }

    override fun toShortFormula(wbKey: WorkbookKey?, wsName: String?): String {
        return v.toTF()
    }

    override fun runRs(): Result<Boolean, SingleErrorReport> {
        return Ok(v)
    }
}

