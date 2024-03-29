package com.qxdzbc.p6.ui.worksheet.ruler

import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Dp
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.common.compose.*
import com.qxdzbc.common.compose.LayoutCoorsUtils.toP6Layout
import com.qxdzbc.common.compose.StateUtils.ms
import com.qxdzbc.common.compose.layout_coor_wrapper.P6Layout
import com.qxdzbc.p6.ui.worksheet.WorksheetConstants
import com.qxdzbc.p6.ui.worksheet.select_rect.SelectRectState
import com.qxdzbc.p6.ui.worksheet.select_rect.SelectRectStateImp
import com.qxdzbc.p6.ui.worksheet.slider.GridSlider
import com.qxdzbc.p6.ui.worksheet.state.WorksheetId

data class RulerStateImp constructor(
    override val wsIdSt:St<WorksheetId>,
    override val type: RulerType,
    override val sliderMs: Ms<GridSlider>,
    // ====== TODO move this to the DI graph
    override val defaultItemSize: Dp = if (type == RulerType.Col) WorksheetConstants.defaultColumnWidth else WorksheetConstants.defaultRowHeight,
    private val itemLayoutMapMs: Ms<Map<Int, P6Layout>> = ms(emptyMap()),
    override val itemSelectRectMs: Ms<SelectRectState> = ms(SelectRectStateImp(
        false,false, Offset.Zero,Offset.Zero
    )),
    private val rulerLayoutMs: Ms<P6Layout?> = ms(null),
    private val itemSizeMapMs: Ms<Map<Int, Dp>> = ms(emptyMap()),
    override val resizerLayoutMap: Map<Int, LayoutCoordinates> = emptyMap(),
) : RulerState {
    override val wsId: WorksheetId
        get() = wsIdSt.value

    override fun setWsIdSt(wsIdSt: St<WorksheetId>): RulerState {
        return this.copy(wsIdSt=wsIdSt)
    }

    override fun getResizerLayout(itemIndex: Int): LayoutCoordinates? {
        return this.resizerLayoutMap[itemIndex]
    }

    override fun addResizerLayout(itemIndex: Int, layoutCoordinates: LayoutCoordinates): RulerState {
        return this.copy(resizerLayoutMap = resizerLayoutMap + (itemIndex to layoutCoordinates))
    }

    override fun clearResizerLayoutCoorsMap(): RulerState {
        if(this.resizerLayoutMap.isNotEmpty()){
            return this.copy(resizerLayoutMap = emptyMap())
        }
        return this
    }

    override val rulerLayout: P6Layout? by rulerLayoutMs
    override fun setLayout(layout: LayoutCoordinates): RulerState {
        rulerLayoutMs.value = layout.toP6Layout()
        return this
    }

    override fun setLayout(layout: P6Layout): RulerState {
        rulerLayoutMs.value = layout
        return this
    }

    override val itemLayoutMap: Map<Int, P6Layout> by itemLayoutMapMs
    override fun addItemLayout(itemIndex: Int, layoutCoordinates: P6Layout): RulerState {
        this.itemLayoutMapMs.value = this.itemLayoutMap + (itemIndex to layoutCoordinates)
        return this
    }

    override fun clearItemLayoutCoorsMap(): RulerState {
        if (this.itemLayoutMap.isNotEmpty()) {
            this.itemLayoutMapMs.value = emptyMap()
        }
        return this
    }

    override val itemSizeMap: Map<Int, Dp> by this.itemSizeMapMs

//    override fun changeItemSize(itemIndex: Int, diff: Float): RulerState {
//        val sd = diff.toInt()
//        if (sd != 0) {
//            val oldSize = this.getItemSizeOrDefault(itemIndex)
//            val newSize = oldSize + sd
//            if (newSize == this.defaultItemSize) {
//                return this.removeItemSize(itemIndex)
//            } else {
//                this.itemSizeMapMs.value = this.itemSizeMap + (itemIndex to newSize)
//            }
//        }
//        return this
//    }

    override fun setItemSize(itemIndex: Int, size: Dp): RulerState {
        this.itemSizeMapMs.value = this.itemSizeMap + (itemIndex to size)
        return this
    }

    override fun setMultiItemSize(itemMap: Map<Int, Dp>): RulerState {
        this.itemSizeMapMs.value = this.itemSizeMap + itemMap
        return this
    }

    override fun getItemSizeOrDefault(itemIndex: Int): Dp {
        return itemSizeMap[itemIndex] ?: this.defaultItemSize
    }

    override fun removeItemSize(itemIndex: Int): RulerState {
        if (itemIndex in itemSizeMap.keys) {
            itemSizeMapMs.value = itemSizeMap - itemIndex
        }
        return this
    }

    override val wbKeySt: St<WorkbookKey>
        get() = wsId.wbKeySt
    override val wsNameSt: St<String>
        get() = wsId.wsNameSt

    override val wbKey: WorkbookKey
        get() = wsId.wbKey
    override val wsName: String
        get() = wsId.wsName
}
