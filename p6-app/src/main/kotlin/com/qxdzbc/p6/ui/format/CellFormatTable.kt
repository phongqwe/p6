package com.qxdzbc.p6.ui.format

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddress
import com.qxdzbc.p6.proto.DocProtos
import com.qxdzbc.p6.proto.DocProtos.CellFormatTableProto
import com.qxdzbc.p6.ui.cell.state.format.text.CellFormat
import com.qxdzbc.p6.ui.cell.state.format.text.TextHorizontalAlignment
import com.qxdzbc.p6.ui.cell.state.format.text.TextVerticalAlignment
import com.qxdzbc.p6.ui.format.FormatTable.Companion.toColorModel
import com.qxdzbc.p6.ui.format.FormatTable.Companion.toFontStyleModel
import com.qxdzbc.p6.ui.format.FormatTable.Companion.toFontWeightModel
import com.qxdzbc.p6.ui.format.FormatTable.Companion.toModel
import com.qxdzbc.p6.ui.format.FormatTable.Companion.toTextHorizontalModel
import com.qxdzbc.p6.ui.format.FormatTable.Companion.toTextVerticalModel
import kotlin.random.Random
import kotlin.random.nextULong


interface CellFormatTable {

    fun toProto(): DocProtos.CellFormatTableProto

    val textSizeTable: FormatTable<Float>
    fun setTextSizeTable(i: FormatTable<Float>): CellFormatTable

    val textColorTable: FormatTable<Color>
    fun setTextColorTable(i: FormatTable<Color>): CellFormatTable

    val textUnderlinedTable: FormatTable<Boolean>
    fun setTextUnderlinedTable(i: FormatTable<Boolean>): CellFormatTable

    val textCrossedTable: FormatTable<Boolean>
    fun setTextCrossedTable(i: FormatTable<Boolean>): CellFormatTable

    val fontWeightTable: FormatTable<FontWeight>
    fun setFontWeightTable(i: FormatTable<FontWeight>): CellFormatTable

    val fontStyleTable: FormatTable<FontStyle>
    fun setFontStyleTable(i: FormatTable<FontStyle>): CellFormatTable

    val textVerticalAlignmentTable: FormatTable<TextVerticalAlignment>
    fun setTextVerticalAlignmentTable(i: FormatTable<TextVerticalAlignment>): CellFormatTable

    val textHorizontalAlignmentTable: FormatTable<TextHorizontalAlignment>
    fun setTextHorizontalAlignmentTable(i: FormatTable<TextHorizontalAlignment>): CellFormatTable

    val cellBackgroundColorTable: FormatTable<Color>
    fun setCellBackgroundColorTable(i: FormatTable<Color>): CellFormatTable


    fun setFormat(rangeAddress: RangeAddress, cellFormat: CellFormat): CellFormatTable
    fun setFormat(cellAddress: CellAddress, cellFormat: CellFormat): CellFormatTable
    fun setFormatForMultiRanges(ranges: Collection<RangeAddress>, cellFormat: CellFormat): CellFormatTable
    fun setFormatForMultiCells(cellAddressList: Collection<CellAddress>, cellFormat: CellFormat): CellFormatTable

    fun removeFormat(cellAddress: CellAddress):CellFormatTable
    fun removeFormat(rangeAddress: RangeAddress):CellFormatTable
    fun removeFormatForMultiRanges(ranges: Collection<RangeAddress>): CellFormatTable
    fun removeFormatForMultiCells(cellAddressList: Collection<CellAddress>): CellFormatTable

    /**
     * Get [CellFormat] at [cellAddress]
     */
    fun getFormat(cellAddress: CellAddress): CellFormat

    /**
     * Get a modifier meant for the cell box
     */
    fun getCellModifier(cellAddress: CellAddress): Modifier?

    /**
     * get a [FormatConfig] including null format for [config]
     */
    fun getFormatConfigForRange(rangeAddress: RangeAddress): FormatConfig

    /**
     * get a [FormatConfig] including null format for all ranges in [ranges]
     */
    fun getFormatConfigForRanges(ranges: Collection<RangeAddress>): FormatConfig

    /**
     * get a [FormatConfig] including null format for all cell in [cells]
     */
    fun getFormatConfigForCells(cells: Collection<CellAddress>): FormatConfig
    fun getFormatConfigForCells(vararg cells: CellAddress): FormatConfig

    /**
     * get a respective [FormatConfig] including null format for all ranges in [config].
     * The result format config mirrors the ranges of each category in the input [config]
     */
    fun getFormatConfigForConfig_Respectively(config: FormatConfig): FormatConfig

    /**
     * get a [FormatConfig] including null format for all ranges in [config].
     * Ranges of all categories are flatten, and used to produce each new category in the result format config obj
     */
    fun getFormatConfigForConfig_Flat(config: FormatConfig): FormatConfig

    /**
     * Remove all format in for all ranges in [config] by each categories in [config]
     */
    fun removeFormatByConfig_Respectively(config: FormatConfig): CellFormatTable

    /**
     * Remove all format in for all ranges in [config]
     */
    fun removeFormatByConfig_Flat(config: FormatConfig): CellFormatTable

    /**
     * apply [config] to this table to create a new table
     */
    fun applyConfig(config: FormatConfig): CellFormatTable

    fun getValidFormatConfigForRange(rangeAddress: RangeAddress): FormatConfig

    companion object {
        fun CellFormatTableProto.toModel(): CellFormatTable {
            val rt = CellFormatTableImp(
                textSizeTable = textSizeTable.toModel(),
                textColorTable = textColorTable.toColorModel(),
                textUnderlinedTable = textUnderlinedTable.toModel(),
                textCrossedTable = textCrossedTable.toModel(),
                fontWeightTable = fontWeightTable.toFontWeightModel(),
                fontStyleTable = fontStyleTable.toFontStyleModel(),
                textVerticalAlignmentTable = textVerticalAlignmentTable.toTextVerticalModel(),
                textHorizontalAlignmentTable = textHorizontalAlignmentTable.toTextHorizontalModel(),
                cellBackgroundColorTable = cellBackgroundColorTable.toColorModel(),
            )
            return rt
        }

        fun random():CellFormatTable{
            return CellFormatTableImp(
                textSizeTable = FormatTable.random { Random.nextFloat() },
                textColorTable = FormatTable.random { Color(Random.nextULong()) },
                textUnderlinedTable = FormatTable.random { Random.nextBoolean() },
                textCrossedTable = FormatTable.random { Random.nextBoolean() },
                fontWeightTable = FormatTable.random { FontWeight((1 .. 1000).random()) },
                fontStyleTable = FormatTable.random { FontStyle.Normal },
                textVerticalAlignmentTable = FormatTable.random { TextVerticalAlignment.random() },
                textHorizontalAlignmentTable = FormatTable.random { TextHorizontalAlignment.random() },
                cellBackgroundColorTable = FormatTable.random { Color(Random.nextULong()) },
            )
        }
    }
}
