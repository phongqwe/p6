package com.qxdzbc.p6.translator.formula.execution_unit.operator

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.andThen
import com.qxdzbc.common.Rse
import com.qxdzbc.p6.composite_actions.range.RangeId
import com.qxdzbc.p6.document_data_layer.cell.address.CRAddress
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.translator.formula.execution_unit.ExUnit
import com.qxdzbc.p6.translator.formula.execution_unit.ExUnitErrors
import com.qxdzbc.p6.translator.formula.execution_unit.ExUnits
import com.qxdzbc.p6.ui.common.color_generator.ColorMap

/**
 * An [ExUnit] representing the unary "-"
 */
data class UnarySubtract(val u: ExUnit) : ExUnit {
    override fun getRangeIds(): List<RangeId> {
        return u.getRangeIds()
    }
    override fun getCellRangeExUnit(): List<ExUnit> {
        return u.getCellRangeExUnit()
    }

    override fun getRanges(): List<RangeAddress> {
        return u.getRanges()
    }

    override fun toColorFormula(
        colorMap: ColorMap,
        wbKey: WorkbookKey?,
        wsName: String?
    ): AnnotatedString? {
        val f1 = u.toColorFormula(colorMap, wbKey, wsName)
        if (f1 != null) {
            return buildAnnotatedString {
                append("-")
                append(f1)
            }
        } else {
            return null
        }
    }

    override fun shift(
        oldAnchorCell: CRAddress<Int, Int>,
        newAnchorCell: CRAddress<Int, Int>
    ): ExUnit {
        return this.copy(u = u.shift(oldAnchorCell, newAnchorCell))
    }

    override fun toFormula(): String? {
        val f1 = u.toFormula()
        if (f1 != null) {
            return "-${f1}"
        } else {
            return null
        }
    }

    override fun toShortFormula(wbKey: WorkbookKey?, wsName: String?): String? {
        val f1 = u.toShortFormula(wbKey, wsName)
        if (f1 != null) {
            return "-${f1}"
        } else {
            return null
        }
    }

    override fun runRs(): Rse<Double> {
        val runRs = u.runRs()
        val rt = runRs.andThen { rs ->
            val trueR = rs?.let { ExUnits.extractFromCellOrNull(rs) }?:0
            val negated = when (trueR) {
                is Int -> Ok(-trueR.toDouble())
                is Double -> Ok(-trueR)
                is Float -> Ok(-trueR.toDouble())
                else -> ExUnitErrors.IncompatibleType.report("Expect a number, but got ${trueR::class.simpleName}")
                    .toErr()
            }
            negated
        }
        return rt
    }
}

