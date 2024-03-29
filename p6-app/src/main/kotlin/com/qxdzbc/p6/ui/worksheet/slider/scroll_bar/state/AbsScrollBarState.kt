package com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.qxdzbc.common.FloatUtils.guardFloat01
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.layout_coor_wrapper.P6Layout
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.ScrollBarConstants.moveThumbByClickRatio
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.OnDragThumbData
import kotlin.math.max


/**
 * This contains implementation for function that is common to both vertical and horizontal edge slider state.
 */
sealed class AbsScrollBarState(
    val thumbLengthRatioMs: Ms<Float>,
    val thumbPositionRatioMs: Ms<Float>,
    val maxLengthRatio: Float,
    val minLengthRatio: Float,
    val reductionRatio: Float,
    val thumbLayoutCoorMs: Ms<P6Layout?>,
    val railLayoutCoorMs: Ms<P6Layout?>,
) : ScrollBarState {

    protected abstract val railEndInWindowPx: Float?

    protected abstract val thumbEndInWindowPx: Float?

    /**
     * starting point (offset) of the thumb in px relative to the offset of the rail.
     */
    protected abstract val thumbStartInParentPx: Float?

    private var _thumbLengthRatio by thumbLengthRatioMs

    override val thumbLengthRatio: Float get() = _thumbLengthRatio

    override var thumbLayoutCoor: P6Layout? by thumbLayoutCoorMs

    override var railLayoutCoor: P6Layout? by railLayoutCoorMs

    override val thumbPositionInPx: Float by derivedStateOf {
        val rt = (railLengthPx ?: 0f) * thumbPositionRatio
        rt
    }

    private val thumbLengthInPx: Float by derivedStateOf {
        (railLengthPx ?: 0f) * _thumbLengthRatio
    }

    override fun computeThumbLength(density: Density): Dp {
        return with(density) { thumbLengthInPx.toDp() }
    }

    override fun setThumbLengthRatio(ratio: Float) {
        _thumbLengthRatio = max(ratio, minLengthRatio)
    }

    override fun setThumbPositionRatioViaEffectivePositionRatio(effectivePositionRatio: Float) {
        railLengthPx?.also { rlPx ->
            val newThumbPositionRatio = effectiveRailLengthPx
                ?.times(effectivePositionRatio)
                ?.div(rlPx)
            newThumbPositionRatio?.also {
                thumbPositionRatioMs.value = newThumbPositionRatio
            }
        }
    }

    override val effectiveRailLengthPx: Float? by derivedStateOf {
        railLengthPx?.minus(thumbLengthInPx)
    }

    /**
     * When thumb is dragged by [dragDelta] px, change the offset ratio of the thumb so that it follow the pointer.
     */
    fun computeThumbOffsetWhenDrag(dragDelta: Float) {
        /**
         * thumb position ratio = thumb top offset / (rail length - thumb length).
         */
        val newThumbPositionRatio = railLengthPx?.let { rl ->
            val thumbStart = thumbStartInParentPx ?: 0f
            val newThumbOffsetPx = max(thumbStart + dragDelta, 0f)
            val ratio = newThumbOffsetPx / rl
            ratio
        }?.coerceIn(0f, 1f) ?: 0f
        thumbPositionRatioMs.value = newThumbPositionRatio
    }

    /**
     * Prevent thumb from moving pass rail end
     */
    private fun preventThumbMovingPassRailEnd() {
        val railLen = railLengthPx
        if (railLen != null && railLen > 0f) {
            val thumbEnd = thumbEndInWindowPx
            val railEnd = railEndInWindowPx
            if (thumbEnd != null && railEnd != null) {
                if (thumbEnd >= railEnd) {
                    // reset thumb position ratio so that thumb bot is equal to rail bot
                    val newThumbPositionRatio = (railLen - thumbLengthInPx) / railLen
                    thumbPositionRatioMs.value = newThumbPositionRatio
                }
            }
        }
    }

    /**
     * When reach the top, reset the thumb length to the max value
     */
    override fun resetThumbLength() {
        setThumbLengthRatio(maxLengthRatio)
    }

    /**
     * When the thumb is dragged, recompute the entire scroll bar state so that the thumb position, size, and other things reflect the drag action.
     */
    override fun recomputeStateWhenThumbIsDragged(delta: Float) {

        computeThumbOffsetWhenDrag(delta)

        if (thumbReachRailEnd && delta > 0f) {
            preventThumbMovingPassRailEnd()
        }
    }

    override val thumbPositionRatio: Float by thumbPositionRatioMs

    override val effectiveThumbPositionRatio: Float
        get() {
            val thumbStart = railLengthPx?.times(thumbPositionRatio)
            val effectiveRL = effectiveRailLengthPx
            val rt = if (thumbStart != null && effectiveRL != null && effectiveRL != 0f) {
                thumbStart / effectiveRL
            } else {
                0f
            }.coerceIn(0f,1f)
            return rt
        }

    override fun computePositionRatioOnFullRail(yPx: Float): Float? {
        return computePositionRatioWithOffset(yPx, 0f)
    }

    override fun performMoveThumbWhenClickOnRail(point: Float) {

        guardFloat01(point, "point")

        val percent = if (point >= thumbPositionRatio) {
            moveThumbByClickRatio
        } else {
            -moveThumbByClickRatio
        }
        val newRatio = (thumbPositionRatio + percent).coerceIn(0f, 1f)
        thumbPositionRatioMs.value = newRatio
    }


    private fun computePositionRatioWithOffset(yPx: Float, offset: Float): Float? {
        val ratio =
            railEndInWindowPx?.let { railBotY ->
                if (railBotY != 0f) {
                    (yPx - offset) / (railBotY - offset)
                } else {
                    null
                }
            }

        val rt = ratio?.coerceIn(0f, 1f)

        return rt
    }


    /**
     * if the thumb has reached the bottom of the rail or not.
     * A thumb is considered "reach rail bottom" if its bottom edge touch the bottom edge of the rail
     */
    override val thumbReachRailEnd: Boolean
        get() {
            val thumbYBottom = thumbEndInWindowPx
            val railYBottom = railEndInWindowPx
            if (thumbYBottom != null && railYBottom != null) {
                return thumbYBottom >= railYBottom
            } else {
                return false
            }
        }

    protected abstract val thumbStartInWindowPx: Float?
    protected abstract val railStartInWindowPx: Float?

    /**
     * Tell if the thumb has reached the top of the rail or not.
     * A thumb is considered "reach rail top" if its top edge touch the top edge of the rail
     */
    override val thumbReachRailStart: Boolean
        get() {
            val thumbYTop = thumbStartInWindowPx
            val railYTop = railStartInWindowPx
            val rt = railYTop != null && thumbYTop != null && railYTop == thumbYTop
            return rt
        }

    override fun convertThumbPositionToIndex(indexRange: IntRange): Int {

        val last = indexRange.last
        val posRatio = effectiveThumbPositionRatio
        val position = (last * posRatio).toInt()
        val rt = position + indexRange.first

        return rt
    }

    override val onDragData: OnDragThumbData by derivedStateOf {
        OnDragThumbData(
            realPositionRatio = thumbPositionRatio,
            virtualPositionRatio = effectiveThumbPositionRatio,
            scaleRatio = this._thumbLengthRatio,
            scrollBarType = type,
            thumbReachRailEnd = thumbReachRailEnd
        )
    }

    override fun resetThumbPosition() {
        thumbPositionRatioMs.value = 0f
    }
}