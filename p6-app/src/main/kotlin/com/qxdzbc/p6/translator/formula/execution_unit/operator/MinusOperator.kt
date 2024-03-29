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
 * An [ExUnit] representing the "-" operator
 */
data class MinusOperator(val u1: ExUnit, val u2: ExUnit) : ExUnit {
    override fun getRangeIds(): List<RangeId> {
        return u1.getRangeIds() + u2.getRangeIds()
    }
    override fun getCellRangeExUnit(): List<ExUnit> {
        return u1.getCellRangeExUnit() + u2.getCellRangeExUnit()
    }

    override fun getRanges(): List<RangeAddress> {
        return u1.getRanges() + u2.getRanges()
    }

    override fun toColorFormula(
        colorMap: ColorMap,
        wbKey: WorkbookKey?,
        wsName: String?
    ): AnnotatedString? {
        val f1 = u1.toColorFormula(colorMap, wbKey, wsName)
        val f2 = u2.toColorFormula(colorMap, wbKey, wsName)
        if (f1 != null && f2 != null) {
            return buildAnnotatedString {
                append(f1)
                append("-")
                append(f2)
            }
        } else {
            return null
        }
    }

    override fun shift(
        oldAnchorCell: CRAddress<Int, Int>,
        newAnchorCell: CRAddress<Int, Int>
    ): ExUnit {
        return this.copy(
            u1 = u1.shift(oldAnchorCell, newAnchorCell),
            u2 = u2.shift(oldAnchorCell, newAnchorCell)
        )
    }

    override fun toFormula(): String? {
        val f1 = u1.toFormula()
        val f2 = u2.toFormula()
        if (f1 != null && f2 != null) {
            return "${f1} - ${f2}"
        } else {
            return null
        }
    }

    override fun toShortFormula(wbKey: WorkbookKey?, wsName: String?): String? {
        val f1 = u1.toShortFormula(wbKey, wsName)
        val f2 = u2.toShortFormula(wbKey, wsName)
        if (f1 != null && f2 != null) {
            return "${f1} - ${f2}"
        } else {
            return null
        }
    }

    override fun runRs(): Rse<Double> {
        val r1Rs = u1.runRs()
        val rt = r1Rs.andThen { r1 ->
            val r2Rs = u2.runRs()
            r2Rs.andThen { r2 ->
                val trueR1 = r1?.let{ ExUnits.extractFromCellOrNull(r1) }?:0
                val trueR2 = r2?.let{ ExUnits.extractFromCellOrNull(r2) }?:0
                if (trueR1 is Number && trueR2 is Number) {
                    Ok(trueR1.toDouble() - (trueR2.toDouble()))
                } else {
                    ExUnitErrors.IncompatibleType.report(
                        "Expect two numbers, but got ${trueR1::class.simpleName} and ${trueR2::class.simpleName}"
                    ).toErr()
                }
            }
        }
        return rt
    }
}

