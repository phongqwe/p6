package com.qxdzbc.p6.app.document.cell

import androidx.compose.ui.text.AnnotatedString
import com.qxdzbc.common.Rse
import com.qxdzbc.common.error.ErrorReport
import com.qxdzbc.p6.app.action.common_data_structure.WbWsSt
import com.qxdzbc.p6.app.document.Shiftable
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.app.document.cell.address.GenericCellAddress
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.proto.DocProtos.CellProto
import com.qxdzbc.p6.ui.common.color_generator.ColorMap


interface Cell :Shiftable,WbWsSt{

    /**
     * This is error caused by evaluation that happens outside of cell content
     */
    val error0: ErrorReport?
    fun setError0(i: ErrorReport?):Cell

    fun isSimilar(c: Cell):Boolean

    override fun shift(
        oldAnchorCell: GenericCellAddress<Int, Int>,
        newAnchorCell: GenericCellAddress<Int, Int>
    ): Cell

    fun reRun(): Cell?
    fun reRunRs():Rse<Cell>

    /**
     * A cell's address never changes, so no need for a Ms
     */
    val id:CellId
    val address: CellAddress

    val content: CellContent
    val fullFormula: String?
    val shortFormula: String?
    fun formula(wbKey: WorkbookKey? = null, wsName: String? = null): String?

    /**
     * value to be displayed on the cell UI
     */
    fun attemptToAccessDisplayText(): String
    val cachedDisplayText:String

    /**
     * Evaluate display text
     */
    fun evaluateDisplayText():Cell

    /**
     * a shortcut to the [CellValue] store in [content]
     */
    val cellValueAfterRun: CellValue
    val currentCellValue: CellValue
    val editableValue: String
    fun editableValue(wbKey: WorkbookKey?, wsName: String): String
    fun colorEditableValue(colorMap: ColorMap, wbKey: WorkbookKey?, wsName: String): AnnotatedString

    /**
     * reRun the cell, refresh the internal value cache, then return it
     */
    val valueAfterRun: Any?
    val currentValue: Any?
    val isFormula: Boolean
    val isEditable: Boolean
    fun setAddress(newAddress: CellAddress): Cell

    fun setCellValue(i: CellValue): Cell
    fun setContent(content: CellContent): Cell

    fun hasContent(): Boolean
    fun toProto(): CellProto
}

