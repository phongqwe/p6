package com.qxdzbc.p6.ui.window.tool_bar.color_selector.action

import androidx.compose.ui.graphics.Color
import com.qxdzbc.p6.composite_actions.cell.update_cell_format.UpdateCellFormatAction
import com.qxdzbc.p6.composite_actions.tool_bar.return_focus_to_cell.ReturnFocusToCellCursor
import com.qxdzbc.p6.di.qualifiers.CellBackgroundColorSelectorActionQualifier
import com.qxdzbc.p6.di.P6AnvilScope
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(P6AnvilScope::class)
@CellBackgroundColorSelectorActionQualifier
class CellBackgroundColorSelectorAction @Inject constructor(
    private val stateContainerSt:StateContainer,
    val updateCellFormatAction: UpdateCellFormatAction,
    val returnFocusToCellCursor: ReturnFocusToCellCursor,
) : ColorSelectorAction {

    private val sc  = stateContainerSt

    override fun clearColor(windowId: String) {
        this.pickColor(windowId,null)
    }

    override fun pickColor(windowId: String, color: Color?) {
        updateCellFormatAction.setBackgroundColorOnSelectedCells(color,undoable=true)
        sc.getCellBackgroundColorSelectorStateMs(windowId)?.also {
            it.value = it.value.setCurrentColor(color)
        }
        returnFocusToCellCursor.returnFocusToCurrentCellCursor()
    }
}
