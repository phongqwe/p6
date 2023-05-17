package com.qxdzbc.p6.translator.formula.execution_unit

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.qxdzbc.common.error.SingleErrorReport
import com.qxdzbc.p6.app.document.cell.address.CRAddress
import com.qxdzbc.p6.app.document.workbook.WorkbookKey

/**
 * An [ExUnit] representing a double
 */
data class DoubleUnit(val v: Double):ExUnit {
    companion object{
        fun Double.toExUnit(): DoubleUnit {
            return DoubleUnit(this)
        }
        fun Float.toExUnit(): DoubleUnit {
            return DoubleUnit(this.toDouble())
        }
    }
    override fun runRs(): Result<Double, SingleErrorReport> {
        return Ok(v)
    }

    override fun shift(
        oldAnchorCell: CRAddress<Int, Int>,
        newAnchorCell: CRAddress<Int, Int>
    ): ExUnit {
        return this
    }

    override fun toFormula(): String {
        return v.toString()
    }

    override fun toShortFormula(wbKey: WorkbookKey?, wsName: String?): String {
        return v.toString()
    }
}

