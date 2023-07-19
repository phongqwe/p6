package com.qxdzbc.p6.ui.document.worksheet.state

import androidx.compose.runtime.getValue
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.St
import com.qxdzbc.common.compose.StateUtils.ms
import com.qxdzbc.common.compose.StateUtils.toMs
import com.qxdzbc.common.compose.layout_coor_wrapper.LayoutCoorWrapper
import com.qxdzbc.common.compose.layout_coor_wrapper.LayoutCoorWrapper.Companion.replaceWith
import com.qxdzbc.p6.app.command.CommandStack
import com.qxdzbc.p6.app.command.CommandStacks
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.app.document.worksheet.Worksheet
import com.qxdzbc.p6.ui.document.cell.state.CellState
import com.qxdzbc.p6.ui.document.cell.state.CellStateImp
import com.qxdzbc.p6.ui.document.cell.state.CellStates
import com.qxdzbc.p6.ui.document.worksheet.WorksheetConstants
import com.qxdzbc.p6.ui.document.worksheet.cursor.state.CursorState
import com.qxdzbc.p6.ui.document.worksheet.resize_bar.ResizeBarState
import com.qxdzbc.p6.ui.document.worksheet.resize_bar.ResizeBarStateImp
import com.qxdzbc.p6.ui.document.worksheet.ruler.RulerState
import com.qxdzbc.p6.ui.document.worksheet.ruler.RulerType
import com.qxdzbc.p6.ui.document.worksheet.select_rect.SelectRectState
import com.qxdzbc.p6.ui.document.worksheet.select_rect.SelectRectStateImp
import com.qxdzbc.p6.ui.document.worksheet.slider.GridSlider
import com.qxdzbc.p6.ui.format.CellFormatTable
import com.qxdzbc.p6.ui.format.CellFormatTableImp
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


/**
 * a GridSlider + col/row limit
 */
data class WorksheetStateImp (
    // ====Assisted inject properties ======//
    override val wsMs: Ms<Worksheet>,
    override val sliderMs: Ms<GridSlider>,
    override val cursorStateMs: Ms<CursorState>,
    override val colRulerStateMs: Ms<RulerState>,
    override val rowRulerStateMs: Ms<RulerState>,
    override val cellLayoutCoorMapMs: Ms<Map<CellAddress, LayoutCoorWrapper>>,

    //====Automatically injected properties====//
    override val cellGridLayoutCoorWrapperMs: Ms<LayoutCoorWrapper?> = ms(null),
    override val wsLayoutCoorWrapperMs: Ms<LayoutCoorWrapper?> = ms(null),
    val cellStateContMs: Ms<CellStateContainer> = CellStateContainers.immutable().toMs(),
    override val selectRectStateMs: Ms<SelectRectState> = ms(SelectRectStateImp()),
    override val colResizeBarStateMs: Ms<ResizeBarState> = ms(
        ResizeBarStateImp(
            dimen = RulerType.Col,
            size = WorksheetConstants.defaultRowHeight
        )
    ),
    override val rowResizeBarStateMs: Ms<ResizeBarState> = ms(
        ResizeBarStateImp(
            dimen = RulerType.Row,
            size = WorksheetConstants.rowRulerWidth
        )
    ),
    override val colRange: IntRange = WorksheetConstants.defaultColRange,
    override val rowRange: IntRange = WorksheetConstants.defaultRowRange,
    override val cellFormatTableMs: Ms<CellFormatTable> = ms(CellFormatTableImp()),
    override val undoStackMs: Ms<CommandStack>,
    override val redoStackMs: Ms<CommandStack>,
) : BaseWorksheetState() {

    @AssistedInject
    constructor(
        @Assisted("1") wsMs: Ms<Worksheet>,
        @Assisted("2") sliderMs: Ms<GridSlider>,
        @Assisted("3") cursorStateMs: Ms<CursorState>,
        @Assisted("4") colRulerStateMs: Ms<RulerState>,
        @Assisted("5") rowRulerStateMs: Ms<RulerState>,
        @Assisted("6") cellLayoutCoorMapMs: Ms<Map<CellAddress, LayoutCoorWrapper>>,
    ) : this(
        wsMs, sliderMs, cursorStateMs, colRulerStateMs, rowRulerStateMs, cellLayoutCoorMapMs,
        cellGridLayoutCoorWrapperMs = ms(null),
        wsLayoutCoorWrapperMs = ms(null),
        cellStateContMs = CellStateContainers.immutable().toMs(),
        selectRectStateMs = ms(SelectRectStateImp()),
        colResizeBarStateMs = ms(ResizeBarStateImp(dimen = RulerType.Col, size = WorksheetConstants.defaultRowHeight)),
        rowResizeBarStateMs = ms(ResizeBarStateImp(dimen = RulerType.Row, size = WorksheetConstants.rowRulerWidth)),
        colRange = WorksheetConstants.defaultColRange,
        rowRange = WorksheetConstants.defaultRowRange,
        cellFormatTableMs = ms(CellFormatTableImp()),
        undoStackMs = ms(CommandStacks.stdCommandStack()),
        redoStackMs = ms(CommandStacks.stdCommandStack()),
    )


    override val id: WorksheetId
        get() {
            return idMs.value
        }

    override fun addCellLayoutCoor(cellAddress: CellAddress, layoutCoor: LayoutCoorWrapper) {
        val oldLayout: LayoutCoorWrapper? = this.cellLayoutCoorMap[cellAddress]
        val newLayout = oldLayout.replaceWith(layoutCoor) ?: layoutCoor
        val newMap = this.cellLayoutCoorMap + (cellAddress to newLayout)
        this.cellLayoutCoorMapMs.value = newMap
    }

    override fun removeCellLayoutCoor(cellAddress: CellAddress) {
        if (cellAddress in this.cellLayoutCoorMap.keys) {
            this.cellLayoutCoorMapMs.value = this.cellLayoutCoorMap - (cellAddress)
        }
    }

    override fun removeAllCellLayoutCoor() {
        if (this.cellLayoutCoorMap.isNotEmpty()) {
            this.cellLayoutCoorMapMs.value = emptyMap()
        }
    }

    /**
     * Refresh all cell states so that the resulting cell states in this worksheet state are either:
     *  - point to a valid cell
     *  - contain format data
     *  Cell states that don't point to a valid cell and containt no format data are removed.
     */
    override fun refreshCellState() {
        var newCellMsCont = CellStateContainers.immutable()
        val existingCells = this.worksheet.cellMsList
        val existingCellAddresses = existingCells.map { it.value.address }.toSet()
        /*
        Update all cell state ms with the latest cell.
        Update cell state container too.
        Create missing cell state for new data cell.
         */
        for (cellMs in existingCells) {
            val cellAddress = cellMs.value.address
            val cellStateMs = this.getCellStateMs(cellAddress)
            if (cellStateMs != null) {
                cellStateMs.value = cellStateMs.value.setCellMs(cellMs)
                newCellMsCont = newCellMsCont.set(cellAddress, cellStateMs)
            } else {
                val newCellState: Ms<CellState> = ms(CellStateImp(cellAddress, cellMs))
                newCellMsCont = newCellMsCont.set(cellAddress, newCellState)
            }
        }

        val currentCellStateContainer = this.cellStateCont
        // x: remove cell state if the cell is not in the current cell container
        for (cellStateMs in currentCellStateContainer.allElements) {
            val cellState = cellStateMs.value
            val addr = cellState.address
            if (addr !in existingCellAddresses) {
                newCellMsCont = newCellMsCont.remove(addr)
            }
        }

        cellStateContMs.value = newCellMsCont
    }

    override val wbKeySt: St<WorkbookKey>
        get() = this.id.wbKeySt

    override val wsNameSt: St<String>
        get() {
            return this.id.wsNameSt
        }

    override fun setSliderAndRefreshDependentStates(i: GridSlider) {
        this.sliderMs.value = i
        this.removeAllCellLayoutCoor()

        colRulerStateMs.value = colRulerState
            .clearItemLayoutCoorsMap()
            .clearResizerLayoutCoorsMap()

        rowRulerStateMs.value = rowRulerState
            .clearItemLayoutCoorsMap()
            .clearResizerLayoutCoorsMap()
    }

    override val cellStateCont: CellStateContainer
        get() = cellStateContMs.value

    override fun removeCellState(vararg addresses: CellAddress) {
        val cont = addresses.fold(cellStateCont) { accCont: CellStateContainer, cellAddress ->
            accCont.remove(cellAddress)
        }
        cellStateContMs.value = cont
    }

    override fun removeCellState(addresses: Collection<CellAddress>) {
        val cont = addresses.fold(cellStateCont) { accCont, cellAddress ->
            accCont.remove(cellAddress)
        }
        cellStateContMs.value = cont
    }

    override fun createAndAddNewCellStateMs(cellState: CellState) {
        cellStateContMs.value = cellStateCont.set(cellState.address, ms(cellState))
    }

    override fun addOrOverwriteCellState(cellState: CellState) {
        val cellStateMs = this.getCellStateMs(cellState.address)
        if (cellStateMs != null) {
            cellStateMs.value = cellState
        } else {
            this.createAndAddNewCellStateMs(cellState)
        }
    }

    override fun addBlankCellState(address: CellAddress) {
        val blankState = CellStates.blank(address)
        createAndAddNewCellStateMs(blankState)
    }

    override fun addBlankCellState(label: String) {
        addBlankCellState(CellAddress(label))
    }

    override fun removeAllCellState() {
        cellStateContMs.value = cellStateCont.removeAll()
    }

    override fun getCellStateMs(cellAddress: CellAddress): Ms<CellState>? {
        val cellMs = cellStateCont.getElement(cellAddress)
        return cellMs
    }

    override fun getCellStateMs(label: String): Ms<CellState>? {
        return getCellStateMs(CellAddress(label))
    }

    override val wbKey: WorkbookKey get() = this.id.wbKey

    override val cellGridLayoutCoorWrapper: LayoutCoorWrapper? by this.cellGridLayoutCoorWrapperMs

    override fun setCellGridLayoutCoorWrapper(i: LayoutCoorWrapper) {
        this.cellGridLayoutCoorWrapperMs.value = i
    }

    override val wsLayoutCoorWrapper: LayoutCoorWrapper? by this.wsLayoutCoorWrapperMs

    override fun setWsLayoutCoorWrapper(i: LayoutCoorWrapper) {
        wsLayoutCoorWrapperMs.value = i
    }

    override val worksheet: Worksheet by wsMs
    override val idMs: Ms<WorksheetId> = worksheet.idMs

    override val name: String
        get() = this.idMs.value.wsName

}
