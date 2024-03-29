package com.qxdzbc.p6.ui.app.cell_editor.actions.differ

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.qxdzbc.p6.di.P6AnvilScope
import com.squareup.anvil.annotations.ContributesBinding
import org.apache.commons.text.diff.StringsComparator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(P6AnvilScope::class)
class TextDifferImp @Inject constructor() : TextDiffer {

    override fun extractTextAdditionWithRangeOfNewText(
        oldTextFieldValue: TextFieldValue,
        newTextFieldValue: TextFieldValue
    ): TextAndRange? {

        val rs = runTextDif(oldTextFieldValue, newTextFieldValue)
        when (rs) {
            is TextDiffResult.Addition -> {
                return rs.addition.copy(
                    range = newTextFieldValue.selection
                )
            }
            else -> {
                return null
            }
        }
    }

    override fun runTextDif(
        oldTextFieldValue: TextFieldValue,
        newTextFieldValue: TextFieldValue,
    ): TextDiffResult {

        if (oldTextFieldValue == newTextFieldValue) {
            return TextDiffResult.NoChange
        }

        if (oldTextFieldValue.text == newTextFieldValue.text && oldTextFieldValue.selection == newTextFieldValue.selection) {
            return TextDiffResult.TextSelectionWasChanged(
                oldTextFieldValue.selection,
                newTextFieldValue.selection,
            )
        }

        val comparator = StringsComparator(oldTextFieldValue.text, newTextFieldValue.text)
        val visitor = CharCommandsVisitorForRunDiff()

        comparator.script.visit(visitor)

        val addition = visitor.addition
        if (addition != null) {
            return TextDiffResult.Addition(
                TextAndRange(
                    text = addition,
                    range = TextRange(start = visitor.addFrom!!, end = visitor.addTo!!)
                )
            )
        } else {
            val df = visitor.deleteFrom
            val dt = visitor.deleteTo
            if (df != null && dt != null) {
                return TextDiffResult.Deletion(
                    TextRange(start = df, end = dt)
                )
            } else {
                return TextDiffResult.NoChange
            }
        }
    }
}
