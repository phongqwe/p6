package com.qxdzbc.p6.translator.formula.execution_unit.primitive

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.qxdzbc.common.error.SingleErrorReport
import com.qxdzbc.p6.document_data_layer.cell.address.CRAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.translator.formula.execution_unit.ExUnit

/**
 * An [ExUnit] representing nothing
 */
object NothingUnit : ExUnit {
    override fun shift(
        oldAnchorCell: CRAddress<Int, Int>,
        newAnchorCell: CRAddress<Int, Int>
    ): ExUnit {
        return this
    }

    override fun toFormula(): String? {
        return null
    }

    override fun toShortFormula(wbKey: WorkbookKey?, wsName: String?): String? {
        return null
    }

    override fun runRs(): Result<Unit, SingleErrorReport> {
        return Ok(Unit)
    }
}
