package com.qxdzbc.p6.translator.formula.execution_unit.obj_type_in_app

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.qxdzbc.common.compose.St
import com.qxdzbc.common.error.SingleErrorReport
import com.qxdzbc.p6.document_data_layer.cell.address.CRAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import androidx.compose.runtime.State
import com.qxdzbc.p6.translator.formula.execution_unit.ExUnit

/**
 * An [ExUnit] representing a [State] holding a [WorkbookKey]
 */
data class WbKeyStUnit(val wbKeySt: St<WorkbookKey>) : ExUnit {
    companion object{
        fun St<WorkbookKey>.toExUnit(): WbKeyStUnit {
            return WbKeyStUnit(this)
        }
    }
    override fun shift(
        oldAnchorCell: CRAddress<Int, Int>,
        newAnchorCell: CRAddress<Int, Int>
    ): ExUnit {
        return this
    }

    override fun toFormula(): String {
        val p = wbKeySt.value.path
        if (p != null) {
            return "@\'${wbKeySt.value.name}\'@\'${p.toAbsolutePath()}\'"
        } else {
            return "@\'${wbKeySt.value.name}\'"
        }
    }

    override fun toShortFormula(wbKey: WorkbookKey?, wsName: String?): String {
        if (wbKey == this.wbKeySt.value) {
            return ""
        } else {
            return toFormula()
        }
    }

    override fun runRs(): Result<St<WorkbookKey>, SingleErrorReport> {
        return Ok(wbKeySt)
    }
}

