package com.qxdzbc.p6.ui.worksheet.slider.edge_slider.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.StateUtils.ms
import com.qxdzbc.p6.ui.worksheet.di.comp.WsScope
import com.qxdzbc.p6.ui.worksheet.slider.GridSlider
import com.qxdzbc.p6.ui.worksheet.slider.edge_slider.EdgeSliderUtils
import javax.inject.Inject
import kotlin.math.max

@WsScope
data class VerticalEdgeSliderStateImp(
    val thumbPositionMs: Ms<DpOffset>,
    val thumbLengthRatioMs: Ms<Float>,
    val maxLengthRatio: Float,
    val minLengthRatio: Float,
    val reductionRatio: Float,
    val sliderStateMs: Ms<GridSlider>,
    val step:Int,
) : VerticalEdgeSliderState {

    @Inject
    constructor(
        sliderStateMs: Ms<GridSlider>,
    ) : this(
        thumbPositionMs = ms(DpOffset.Zero),
        thumbLengthRatioMs = ms(EdgeSliderUtils.maxLength),
        maxLengthRatio = EdgeSliderUtils.maxLength,
        minLengthRatio = EdgeSliderUtils.minLength,
        reductionRatio = EdgeSliderUtils.reductionRate,
        sliderStateMs = sliderStateMs,
        step=10,
    )

    private var lengthRatio by thumbLengthRatioMs

    private var sliderState by sliderStateMs

    override fun thumbLength(railLength: Dp): Dp {
        return railLength * thumbLengthRatioMs.value
    }

    override fun setThumbLengthRatio(ratio: Float) {
        lengthRatio = ratio
    }

    override var thumbPosition: DpOffset by thumbPositionMs

    /**
     * When thumb is dragged by [dragDelta] px, change the offset of the thumb so that it follow the pointer.
     * Impose a cap on thumb offset so that it cannot get below 0.
     */
    override fun setThumbOffsetWhenDrag(
        density: Density, dragDelta: Float, railLength: Dp,
    ) {
        val sliderThumbYPx = with(density) {
            max(thumbPosition.y.toPx() + dragDelta, 0f)
        }

        val sliderOffset = run {
            val slideThumYOffsetDp = with(density) {
                sliderThumbYPx.toDp()
            }
            DpOffset(0.dp, slideThumYOffsetDp)
        }
        val oldPos = thumbPosition
        thumbPosition = sliderOffset

        // TODO need to move the slider when thumb is moved.

        moveSliderWhenThumbIsDragged(
            railLength,oldPos,sliderOffset
        )
    }

    override fun recomputeStateWhenThumbReachRailBottom(railLength: Dp) {
        // recompute the thumb length
        lengthRatio = max(lengthRatio * reductionRatio, minLengthRatio)

        val lastRow = sliderState.lastVisibleRow

        // TODO move the slider too


        // TODO recompute slider size and position, so that it can be used again
        val newSize = lastRow + step
        val newPos = lastRow.toFloat()/newSize
        thumbPosition =  DpOffset(0.dp,railLength*newPos)


    }

    /**
     * When reach the top, reset the thumb length to the max value
     */
    override fun recomputeStateWhenThumbReachRailTop() {
        lengthRatio = maxLengthRatio
        // TODO scroll the slider too
    }

    fun moveSliderWhenThumbIsDragged(
//        density: Density,
//        dragDelta: Float,
        railLength: Dp,
        oldThumbOffset: DpOffset,
        newThumbOffset: DpOffset,
    ){
        /*
        need to translate the drag distance -> number of row to change

        val distancePerRow = railLength/(last show row -> 1st row)
        val rowNum = dragDistance/distancePerRow
        sliderState.shiftDown/up
         */

        val effectiveRailLength = railLength*(1-lengthRatio)

        val distancePerRow = effectiveRailLength/sliderState.lastVisibleRow
        val oldY = oldThumbOffset.y
        val newY = newThumbOffset.y
        val movingDown = newY>oldY
        if(movingDown){
            val newRow = (newY/distancePerRow).toInt()
            // see if it has cross any threadhold

        }




//        sliderState = sliderState.shiftUp(row)


    }


}