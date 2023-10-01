package com.qxdzbc.p6.ui.worksheet.slider.edge_slider.action.internal_action

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.p6.ui.worksheet.di.WsAnvilScope
import com.qxdzbc.p6.ui.worksheet.di.WsScope
import com.qxdzbc.p6.ui.worksheet.slider.GridSlider
import com.qxdzbc.p6.ui.worksheet.slider.edge_slider.action.EdgeSliderActionType
import com.qxdzbc.p6.ui.worksheet.slider.edge_slider.di.qualifiers.ForVerticalWsEdgeSlider
import com.qxdzbc.p6.ui.worksheet.slider.edge_slider.state.ThumbPositionConverter
import com.qxdzbc.p6.ui.worksheet.state.WorksheetStateGetter
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@WsScope
@ContributesBinding(scope = WsAnvilScope::class)
class InternalEdgeSliderActionImp @Inject constructor(
    private val wsStateGetter: WorksheetStateGetter,
    private val sliderMs: Ms<GridSlider>,
    @ForVerticalWsEdgeSlider
    private val thumbPositionConverter: ThumbPositionConverter,
) : InternalEdgeSliderAction {

    private var slider by sliderMs

    override fun drag(data: EdgeSliderActionType.Drag) {

        val edgeRowRange = slider.edgeSliderRowRange
        val newEdgeRow = thumbPositionConverter.convertThumbPositionToIndex(edgeRowRange)

        // move the top row to this
        val shiftCount = newEdgeRow - slider.topLeftCell.rowIndex
        val newSlider = slider.shiftDown(shiftCount)

        if (newSlider != slider) {
            wsStateGetter.get()?.setSliderAndRefreshDependentStates(newSlider)
        }
        slider = newSlider
    }
}