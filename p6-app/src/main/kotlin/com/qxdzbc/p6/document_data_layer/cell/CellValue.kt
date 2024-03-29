package com.qxdzbc.p6.document_data_layer.cell

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.qxdzbc.common.Rse
import com.qxdzbc.common.compose.St
import com.qxdzbc.common.compose.StateUtils.ms
import com.qxdzbc.common.error.SingleErrorReport
import com.qxdzbc.common.error.ErrorReport
import com.qxdzbc.p6.common.utils.TypeUtils.checkStAndCast
import com.qxdzbc.p6.document_data_layer.range.Range
import com.qxdzbc.p6.proto.DocProtos.CellValueProto

/**
 * A class that holds the value (value only, not including the formula) of a cell
 */
data class CellValue (
    val number: Double? = null,
    val bool: Boolean? = null,
    val str: String? = null,
    val range: Range? = null,
    val cellSt: St<Cell>? = null,
    val errorReport: ErrorReport? = null,
    val transErrorReport: ErrorReport? = null,
) {
    val cell: Cell? get() = cellSt?.value
    override fun hashCode(): Int {
        var result = number?.hashCode() ?: 0
        result = 31 * result + (bool?.hashCode() ?: 0)
        result = 31 * result + (str?.hashCode() ?: 0)
        result = 31 * result + (range?.hashCode() ?: 0)
        result = 31 * result + (errorReport?.hashCode() ?: 0)
        result = 31 * result + (transErrorReport?.hashCode() ?: 0)
        result = 31 * result + (cell?.id?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other is CellValue) {
            val c1 = number == other.number
            val c2 = bool == other.bool
            val c3 = str == other.str
            val c4 = range == other.range
            val c5 = cell?.id == other.cell?.id
            val c6 = errorReport == other.errorReport
            val c7 = transErrorReport == other.transErrorReport
            return c1 && c2 && c3 && c4 && c5 && c6 && c7
        } else {
            return false
        }
    }

    init {
        val nonNullCount = listOfNotNull(number, bool, str, errorReport, range, cell).size
        if (nonNullCount > 1) {
            throw IllegalArgumentException("There are $nonNullCount non-null values. CellValue can only hold 0 or 1 non-null value.")
        }
    }

    override fun toString(): String {
        /*
         val number: Double? = null,
    val bool: Boolean? = null,
    val str: String? = null,
    val range: Range? = null,
    val cell: Cell? = null,
    val errorReport: ErrorReport? = null,
    val transErrorReport: ErrorReport? = null,
         */
        return this.hashCode().toString()
    }

    companion object {
        val empty = CellValue()
        fun fromRs(rs: Rse<Any?>): CellValue {
            when (rs) {
                is Err -> {
                    return CellValue(errorReport = rs.error)
                }

                is Ok -> {
                    return fromAny(rs.value)
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun fromAny(any: Any?): CellValue {
            if (any == null) {
                return empty
            }
            val i = any
            when (i) {
                is String -> return from(i)
                is Number -> return from(i.toDouble())
                is Boolean -> return from(i)
                is Range -> return from(i)
                is SingleErrorReport -> return from(i)
                else -> {
                    val casted: St<Cell>? = i.checkStAndCast()
                    if(casted!=null){
                        val rt:CellValue= from(casted)
                        return rt
                    }else{
                        return from(CellErrors.InvalidCellValue.report(i))
                    }
                }
            }
        }

        fun fromTransError(errorReport: ErrorReport): CellValue {
            return CellValue(transErrorReport = errorReport)
        }

        fun from(i: St<Cell>): CellValue {
            return CellValue(cellSt = i)
        }

        fun fromCellForTest(i: Cell): CellValue {
            return CellValue(cellSt = ms(i))
        }

        fun from(i: Range): CellValue {
            return CellValue(range = i)
        }

        fun from(i: Number): CellValue {
            return CellValue(number = i.toDouble())
        }

        fun Number.toCellValue(): CellValue {
            return CellValue(number = this.toDouble())
        }

        fun from(i: Boolean): CellValue {
            return CellValue(bool = i)
        }

        fun Boolean.toCellValue(): CellValue {
            return CellValue(bool = this)
        }

        fun from(str: String): CellValue {
            return CellValue(str = str)
        }

        fun String.toCellValue(): CellValue {
            return CellValue(str = this)
        }

        fun from(i: ErrorReport): CellValue {
            return CellValue(errorReport = i)
        }

        fun SingleErrorReport.toCellValue(): CellValue {
            return CellValue(errorReport = this)
        }

        fun CellValueProto.toModel(): CellValue {
            if (this.hasBool()) {
                return this.bool.toCellValue()
            }
            if (this.hasNum()) {
                return this.num.toCellValue()
            }
            if (this.hasStr()) {
                return this.str.toCellValue()
            }
            return empty
        }
    }

    val all = listOfNotNull(number, bool, str, errorReport, range, cellSt, transErrorReport)

    val value: Any? get() = all.firstOrNull()

    val isBool get() = this.bool != null
    val isNumber get() = this.number != null
    val isStr get() = this.str != null
    val isErr get() = this.errorReport != null
    val isTranslatorErr get() = this.transErrorReport != null
    val isRange get() = this.range != null
    val isCell get() = cell != null

    fun setValue(any: Any?): CellValue {
        return fromAny(any)
    }

    /**
     * A cell content is only legal if it contains at most 1 non-null value
     */
    fun isLegal(): Boolean {
        return all.size <= 1
    }

    fun isEmpty(): Boolean {
        return all.isEmpty()
    }

    /**
     * @return: a string value that can be edited by cell editor
     */
    val editableValue: String?
        get() {
            // range, cell, and err report are only created by formula, therefore they can't be edited directly
            if (this.isRange || this.isCell || this.isErr || this.isTranslatorErr) {
                return null
            } else {
                return displayText
            }
        }

    /**
     * @return a string value for displaying inside a cell. This is what the user see in the cell on a worksheet.
     */
    val displayText: String
        get() {
            if (number != null) {
                // handle int number
                if (number.toInt().toDouble() == number) {
                    return number.toInt().toString()
                }
                return number.toString()
            }
            if (bool != null) {
                if (bool) {
                    return "TRUE"
                } else {
                    return "FALSE"
                }
            }
            if (str != null) {
                return str
            }
            if (errorReport != null) {
                return errorReport.toString()
            }
            if (transErrorReport != null) {
                return transErrorReport.toString()
            }
            if (cell != null) {
                return cell!!.attemptToAccessDisplayText()
            }
            if (range != null) {
                if (range.isCell) {
                    val cell = range.cells[0]
                    return cell.attemptToAccessDisplayText()
                } else {
                    return "Range[${range.address.label}]"
                }
            }
            return ""
        }

    fun toProto(): CellValueProto {
        val rt = CellValueProto.newBuilder().let { bd ->
            number?.also { bd.setNum(it) }
            str?.also { bd.setStr(it) }
            bool?.also { bd.setBool(it) }
            bd
        }.build()
        return rt
    }
}
