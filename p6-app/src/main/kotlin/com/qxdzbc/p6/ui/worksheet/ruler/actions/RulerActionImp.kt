package com.qxdzbc.p6.ui.worksheet.ruler.actions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import com.qxdzbc.common.compose.density_converter.FloatToDpConverter
import com.qxdzbc.common.compose.layout_coor_wrapper.P6Layout
import com.qxdzbc.p6.composite_actions.cell_editor.update_range_selector_text.RefreshRangeSelectorText
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWsSt
import com.qxdzbc.p6.composite_actions.worksheet.ruler.change_col_row_size.ChangeRowAndColumnSizeAction
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddresses
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddress
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddressUtils
import com.qxdzbc.p6.di.P6AnvilScope

import com.qxdzbc.p6.ui.app.state.StateContainer
import com.qxdzbc.p6.ui.worksheet.ruler.RulerSig
import com.qxdzbc.p6.ui.worksheet.ruler.RulerState
import com.qxdzbc.p6.ui.worksheet.ruler.RulerType
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(P6AnvilScope::class,boundType=RulerAction::class)
class RulerActionImp @Inject constructor(
    private val stateCont:StateContainer,
    val updateCellEditorText: RefreshRangeSelectorText,
    val changColRowSizeAction: ChangeRowAndColumnSizeAction
) : RulerAction , ChangeRowAndColumnSizeAction by changColRowSizeAction{

    private val sc = stateCont

    private fun resizerIsNotActivate(wbwsSt: WbWsSt): Boolean {
        val wsState = sc.getWsState(wbwsSt)
        if (wsState != null) {
            return !wsState.colResizeBarState.isShowBar && !wsState.rowResizeBarState.isShowBar
        } else {
            return false
        }
    }

    private fun makeWholeColOrRowAddress(ii: Int, type:RulerType): RangeAddress {
        return when (type) {
            RulerType.Col -> RangeAddressUtils.rangeForWholeCol(ii)
            RulerType.Row -> RangeAddressUtils.rangeForWholeRow(ii)
        }
    }

    /**
     * Update cell editor text if range selector is allowed. Otherwise, close cell editor
     */
    private fun updateCellEditorTextIfNeed(){
        updateCellEditorText.refreshRangeSelectorTextInCurrentCellEditor()
        if (sc.cellEditorState.isOpen) {
            if (!sc.cellEditorState.allowRangeSelector) {
                sc.cellEditorState = sc.cellEditorState.close()
            }
        }
    }

    override fun clickRulerItem(itemIndex: Int, rulerSig: RulerSig) {

        val cursorStateMs = sc.getCursorStateMs(rulerSig)
        val rulerState = sc.getRulerStateMs(rulerSig)?.value

        if (rulerState != null && resizerIsNotActivate(rulerState) && cursorStateMs != null) {
            val cursorState by cursorStateMs
            when (rulerSig.type) {
                RulerType.Col -> {
                    cursorStateMs.value = cursorState
                        .removeAllExceptMainCell()
                        .selectWholeCol(itemIndex)
                        .setMainCell(CellAddresses.firstOfCol(itemIndex))
                }
                RulerType.Row -> {
                    cursorStateMs.value = cursorState
                        .removeAllExceptMainCell()
                        .selectWholeRow(itemIndex)
                        .setMainCell(CellAddresses.firstOfRow(itemIndex))
                }
            }
            this.updateCellEditorTextIfNeed()
        }
    }

    override fun showColResizeBarThumb(index: Int, wbwsSt: WbWsSt) {
        sc.getWsState(wbwsSt)?.also { wsState ->
            val colRulerState = wsState.colRulerState
            val resizerLayout = colRulerState.getResizerLayout(index)
            val wsLayout = wsState.wsLayoutCoorWrapper?.layout
            if (wsLayout != null && wsLayout.isAttached) {
                if (resizerLayout != null && resizerLayout.isAttached) {
                    val p = wsLayout.windowToLocal(resizerLayout.positionInWindow())
                    wsState.colResizeBarStateMs.value =
                        wsState.colResizeBarStateMs.value.setResizeBarOffset(p).showThumb()
                }
            }
        }
    }

    override fun hideColResizeBarThumb(wbwsSt: WbWsSt) {
        sc.getWsState(wbwsSt)?.also { wsState ->
            var colResizeBar by wsState.colResizeBarStateMs
            if (!colResizeBar.isActive) {
                wsState.colResizeBarStateMs.value = colResizeBar.hideThumb()
            }
        }
    }

    override fun startColResizing(currentPos: Offset, wbwsSt: WbWsSt) {
        sc.getWsState(wbwsSt)?.also { wsState ->
            val colResizeBar by wsState.colResizeBarStateMs
            val wsLayout = wsState.wsLayoutCoors
            if (wsLayout != null && wsLayout.isAttached) {
                val p = wsLayout.windowToLocal(currentPos).copy(y = colResizeBar.resizeBarOffset.y)
                wsState.colResizeBarStateMs.value = colResizeBar
                    .setResizeBarOffset(p)
                    .setAnchorPointOffset(p)
                    .activate()
                    .showBar()
            }
        }
    }

    override fun moveColResizer(currentPos: Offset, wbwsSt: WbWsSt) {
        sc.getWsState(wbwsSt)?.also { wsState ->
            val colResizeBar by wsState.colResizeBarStateMs
            if (colResizeBar.isActive) {
                val wsLayout = wsState.wsLayoutCoorWrapper?.layout
                if (wsLayout != null && wsLayout.isAttached) {
                    val p = wsLayout.windowToLocal(currentPos).copy(y = colResizeBar.resizeBarOffset.y)
                    wsState.colResizeBarStateMs.value = colResizeBar.setResizeBarOffset(p).showThumb().showBar()
                }
            }
        }
    }

    override fun finishColResizing(colIndex: Int, wbwsSt: WbWsSt,converter: FloatToDpConverter) {
        sc.getWsState(wbwsSt)?.also { wsState ->
            val colResizeBar by wsState.colResizeBarStateMs
            if (colResizeBar.isShowBar) {
                val sizeDiff = converter.toDp(colResizeBar.resizeBarOffset.x - colResizeBar.anchorPointOffset.x)
                this.changeColWidth(colIndex, sizeDiff, wbwsSt,true)
                wsState.colResizeBarStateMs.value = colResizeBar
                    .deactivate()
                    .hideThumb()
                    .hideBar()
                sc.getWbState(wsState.wbKeySt)?.needSave = true
            }
        }
    }

    override fun showRowResizeBarThumb(index: Int, wbwsSt: WbWsSt) {
        sc.getWsState(wbwsSt)?.also { wsState ->
            val rowRulerState by wsState.rowRulerStateMs
            val rowResizeBar by wsState.rowResizeBarStateMs
            val resizerLayout = rowRulerState.getResizerLayout(index)
            val wsLayout = wsState.wsLayoutCoorWrapper?.layout
            if (wsLayout != null && wsLayout.isAttached) {
                if (resizerLayout != null && resizerLayout.isAttached) {
                    val p = wsLayout.windowToLocal(resizerLayout.positionInWindow())
                    wsState.rowResizeBarStateMs.value = rowResizeBar.setResizeBarOffset(p).showThumb()
                }
            }
        }

    }

    override fun hideRowResizeBarThumb(wbwsSt: WbWsSt) {
        sc.getWsState(wbwsSt)?.also { wsState ->
            val rowResizeBar by wsState.rowResizeBarStateMs
            if (!rowResizeBar.isActive) {
                wsState.rowResizeBarStateMs.value = rowResizeBar.hideThumb()
            }
        }
    }

    override fun startRowResizing(currentPos: Offset, wbwsSt: WbWsSt) {
        sc.getWsState(wbwsSt)?.also { wsState ->
            val rowResizeBar by wsState.rowResizeBarStateMs
            val wsLayout = wsState.wsLayoutCoors
            if (wsLayout != null && wsLayout.isAttached) {
                val p = wsLayout.windowToLocal(currentPos).copy(x = rowResizeBar.resizeBarOffset.x)
                wsState.rowResizeBarStateMs.value = rowResizeBar
                    .setResizeBarOffset(p)
                    .setAnchorPointOffset(p)
                    .activate()
                    .showBar()
            }
        }
    }

    override fun moveRowResizer(currentPos: Offset, wbwsSt: WbWsSt) {
        sc.getWsState(wbwsSt)?.also { wsState ->
            val rowResizeBar by wsState.rowResizeBarStateMs
            if (rowResizeBar.isActive) {
                val wsLayout = wsState.wsLayoutCoorWrapper?.layout
                if (wsLayout != null && wsLayout.isAttached) {
                    val p = wsLayout.windowToLocal(currentPos).copy(x = rowResizeBar.resizeBarOffset.x)
                    wsState.rowResizeBarStateMs.value = rowResizeBar.setResizeBarOffset(p).showThumb().showBar()
                }
            }
        }
    }

    override fun finishRowResizing(rowIndex: Int, wbwsSt: WbWsSt,converter: FloatToDpConverter) {
        sc.getWsState(wbwsSt)?.also { wsState ->
            val rowResizeBar by wsState.rowResizeBarStateMs
            if (rowResizeBar.isShowBar) {
                val sizeDiff = converter.toDp(rowResizeBar.resizeBarOffset.y - rowResizeBar.anchorPointOffset.y)
                this.changeRowHeight(rowIndex, sizeDiff, wbwsSt,true)
                wsState.rowResizeBarStateMs.value = rowResizeBar.hideBar().hideThumb().deactivate()
                sc.getWbState(wsState.wbKeySt)?.needSave = true
            }
        }
    }

    override fun startDragSelection(mousePosition: Offset, rulerSig: RulerSig) {
        if (resizerIsNotActivate(rulerSig)) {
            sc.getWsState(rulerSig)?.also { wsState ->
                val rulerState = wsState.getRulerState(rulerSig.type)
                val selectRectStateMs = rulerState.itemSelectRectMs
                val selectRectState by selectRectStateMs
                val rulerLayout = rulerState.rulerLayout
                val mouseWindowPos = if (rulerLayout != null && rulerLayout.isAttached) {
                    rulerLayout.localToWindow(mousePosition)
                } else {
                    mousePosition
                }
                selectRectStateMs.value = selectRectState.activate().setAnchorPoint(mouseWindowPos)
                this.updateCellEditorTextIfNeed()
            }
        }
    }

    override fun makeMouseDragSelectionIfPossible(mousePosition: Offset, rulerSig: RulerSig) {
        sc.getWsState(rulerSig)?.also { wsState ->
            val rulerState = wsState.getRulerState(rulerSig.type)
            var selectRectState by rulerState.itemSelectRectMs
            val cursorStateMs = wsState.cursorStateMs
            val cursorState by cursorStateMs
            if (selectRectState.isActive) {
                val rulerLayout = rulerState.rulerLayout
                val mouseWindowPos = if (rulerLayout != null && rulerLayout.isAttached) {
                    rulerLayout.localToWindow(mousePosition)
                } else {
                    mousePosition
                }
                selectRectState = selectRectState.setMovingPoint(mouseWindowPos).show()

                val selectedItems = rulerState.itemLayoutMap.entries.filter { (_, itemLayout) ->
                    selectRectState.rect.overlaps(itemLayout.boundInWindowOrZero)
                }
                if (selectedItems.isNotEmpty()) {
                    val mergedRange:RangeAddress = selectedItems.fold(
                        makeWholeColOrRowAddress(selectedItems.first().key, rulerState.type)
                    ) { acc:RangeAddress, (i:Int, l:P6Layout) ->
                        acc.mergeWith(makeWholeColOrRowAddress(i, rulerState.type))
                    }
                    val newAnchorCell: CellAddress = if (cursorState.mainCell in mergedRange) {
                        cursorState.mainCell
                    } else {
                        mergedRange.topLeft
                    }
                    cursorStateMs.value = cursorState.setMainRange(mergedRange).setMainCell(newAnchorCell)
                } else {
                    cursorStateMs.value = cursorState.removeMainRange()
                }
                this.updateCellEditorTextIfNeed()
            }
        }

    }

    override fun stopDragSelection(rulerSig: RulerSig) {
        val rs = sc.getRulerState(rulerSig)
        rs?.also {
            val srMs = it.itemSelectRectMs
            srMs.value = srMs.value.deactivate().hide()
            this.updateCellEditorTextIfNeed()
        }
    }

    override fun updateItemLayout(itemIndex: Int, itemLayout: P6Layout, rulerSig: RulerSig) {
        sc.getRulerStateMs(rulerSig)?.also {
            it.value = it.value.addItemLayout(itemIndex, itemLayout)
            val srMs = it.value.itemSelectRectMs
            srMs.value = srMs.value.deactivate().hide()
        }
    }

    override fun updateRulerLayout(layout: LayoutCoordinates, rulerSig: RulerSig) {
        sc.getRulerStateMs(rulerSig)?.also {
            it.value = it.value.setLayout(layout)
        }
    }

    override fun shiftClick(itemIndex: Int, rulerSig: RulerSig) {
        sc.getWsState(rulerSig)?.also { wsState ->
            var cursorState by wsState.cursorStateMs
            val newRange = when (rulerSig.type) {
                RulerType.Col -> {
                    val currentCol = cursorState.mainCell.colIndex
                    RangeAddressUtils.rangeForWholeMultiCol(currentCol, itemIndex)
                }
                RulerType.Row -> {
                    val currentRow = cursorState.mainCell.rowIndex
                    RangeAddressUtils.rangeForWholeMultiRow(currentRow, itemIndex)
                }
            }
            wsState.cursorStateMs.value = cursorState
                .setMainRange(newRange)
                .removeAllFragmentedCells()
                .removeAllSelectedFragRange()
        }
    }

    override fun ctrlClick(itemIndex: Int, rulerSig: RulerSig) {
        sc.getWsState(rulerSig)?.also { wsState ->
            var cursorState by wsState.cursorStateMs
            val newRange = when (rulerSig.type) {
                RulerType.Col -> {
                    RangeAddressUtils.rangeForWholeCol(itemIndex)
                }
                RulerType.Row -> {
                    RangeAddressUtils.rangeForWholeRow(itemIndex)
                }
            }
            val newCursorState = if (newRange in cursorState.fragmentedRanges) {
                 cursorState.removeFragRange(newRange)
            } else {
                 cursorState.addFragRange(newRange)
            }
            wsState.cursorStateMs.value = newCursorState
        }
    }

    override fun updateResizerLayout(itemIndex: Int, layout: LayoutCoordinates, rulerState: RulerState) {
        val rulerStateMs = sc.getRulerStateMs(rulerState)
        if (rulerStateMs != null) {
            rulerStateMs.value = rulerStateMs.value.addResizerLayout(itemIndex, layout)
        }
    }
}
