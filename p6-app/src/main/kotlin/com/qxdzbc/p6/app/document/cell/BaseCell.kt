package com.qxdzbc.p6.app.document.cell

import androidx.compose.ui.text.AnnotatedString
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.ui.common.color_generator.ColorProvider

abstract class BaseCell : Cell {
    override fun reRun(): Cell? {
        return reRunRs().component1()
    }

    override fun isSimilar(c: Cell): Boolean {
        val sameAddress = address == c.address
        val similarContent = content == c.content
        return sameAddress && similarContent
    }

    override fun formula(wbKey: WorkbookKey?, wsName: String?): String? {
        return content.shortFormula(wbKey, wsName)
    }

    override fun colorEditableValue(
        colorProvider: ColorProvider,
        wbKey: WorkbookKey?,
        wsName: String
    ): AnnotatedString {
        if(this.isFormula){
            return this.content.colorFormula(colorProvider,wbKey, wsName) ?: AnnotatedString("")
        }else{
            return AnnotatedString(this.cellValueAfterRun.editableValue ?: "")
        }
    }

    override fun editableValue(wbKey: WorkbookKey?, wsName: String): String {
        if(this.isFormula){
            return this.formula(wbKey, wsName) ?: ""
        }else{
            return this.cellValueAfterRun.editableValue ?: ""
        }
    }

    override val editableValue: String
        get() {
            if(this.isFormula){
                return this.fullFormula ?: ""
            }else{
                return this.cellValueAfterRun.editableValue ?: ""
            }
        }
    override val fullFormula: String? get() = content.fullFormula
    override val shortFormula: String? get() = content.shortFormula(this.wbKey,this.wsName)
    override val displayValue: String get() {
        try{
            return content.displayStr
        }catch (e:Throwable){
            return "ERR"
        }
    }
    override val isEditable: Boolean get() = true
    override fun hasContent(): Boolean {
        return content.isNotEmpty()
    }
    override val cellValueAfterRun: CellValue get() = content.cellValueAfterRun
    override val valueAfterRun: Any? get() = this.cellValueAfterRun.valueAfterRun
    override val currentCellValue: CellValue
        get() = content.currentCellValue
    override val currentValue: Any?
        get() = content.currentCellValue.currentValue
    override val isFormula: Boolean get() = content.isFormula
}