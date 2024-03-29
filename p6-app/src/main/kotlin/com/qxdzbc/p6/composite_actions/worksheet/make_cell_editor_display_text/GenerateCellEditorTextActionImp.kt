package com.qxdzbc.p6.composite_actions.worksheet.make_cell_editor_display_text

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.qxdzbc.p6.common.formatter.RangeAddressFormatter
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.di.P6AnvilScope
import com.qxdzbc.p6.ui.app.cell_editor.state.CellEditorState
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.qxdzbc.p6.ui.worksheet.cursor.state.CursorState
import com.qxdzbc.p6.ui.worksheet.cursor.state.CursorId
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(P6AnvilScope::class)
class GenerateCellEditorTextActionImp @Inject constructor(
    val stateCont:StateContainer,
    val formatter: RangeAddressFormatter,
) : GenerateCellEditorTextAction {

    override fun generateRangeSelectorText(editorState: CellEditorState): TextFieldValue {
        if (editorState.allowRangeSelector) {
            return generateRangeSelectorText(
                editorState.currentTextField,
                editorState.rangeSelectorId,
                editorState.targetCursorId
            )
        } else {
            return editorState.currentTextField
        }
    }

    override fun generateRangeSelectorText(
        currentTextField: TextFieldValue,
        selectorId: CursorId?,
        cursorId: CursorId?
    ): TextFieldValue {
        val rangeSelector: CursorState? = selectorId?.let { stateCont.getCursorState(it) }
        val rsWsName: String? = selectorId?.wsName
        val rsRange: RangeAddress? = rangeSelector?.let { getSelectedRange(it) }
        val rsWbKey: WorkbookKey? = selectorId?.wbKey

        if (rsWsName != null && rsRange != null && rsWbKey != null) {
            // range selector exists

            val rangeStr: String = if (rsRange.isCell()) {
                rsRange.topLeft.label
            } else {
                rsRange.rawLabel
            }
            val isSameCursor: Boolean = cursorId == selectorId
            val selectedRangeText: String = if (isSameCursor) {
                rangeStr
            } else {
                val sameWb = rsWbKey == cursorId?.wbKey
                if (sameWb) {
                    formatter.formatStr(rangeStr,rsWsName,null,null)
                } else {
                    formatter.format(rangeStr,rsWsName,rsWbKey)
                }
            }
            val currentText = currentTextField.text
            val currentSelection = currentTextField.selection

            val preText = currentText.substring(0, currentSelection.start)
            val postText = currentText.substring(currentSelection.end)
            val newText = "${preText}${selectedRangeText}${postText}"

            val newSelection = TextRange(currentSelection.end + selectedRangeText.length)

            return TextFieldValue(text = newText, selection = newSelection)
        } else {
            return currentTextField
        }
    }


    fun getSelectedRange(cursorState: CursorState): RangeAddress {
        val mainRange = cursorState.mainRange
        if (mainRange != null) {
            return mainRange
        } else {
            val mainCell = cursorState.mainCell
            return RangeAddress(mainCell)
        }
    }
}
