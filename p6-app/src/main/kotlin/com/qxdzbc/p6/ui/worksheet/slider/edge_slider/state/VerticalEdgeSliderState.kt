package com.qxdzbc.p6.ui.worksheet.slider.edge_slider.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.StateUtils
import com.qxdzbc.common.compose.layout_coor_wrapper.P6LayoutCoor
import com.qxdzbc.p6.ui.worksheet.di.comp.WsAnvilScope
import com.qxdzbc.p6.ui.worksheet.di.comp.WsScope
import com.qxdzbc.p6.ui.worksheet.slider.edge_slider.EdgeSliderConstants
import com.qxdzbc.p6.ui.worksheet.slider.edge_slider.di.qualifiers.VerticalWsEdgeSliderStateQualifier
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

/**
 * State for vertical edge slider. Exist as a singleton under [WsScope]
 */
@WsScope
@VerticalWsEdgeSliderStateQualifier
@ContributesBinding(scope = WsAnvilScope::class, boundType = EdgeSliderState::class)
class VerticalEdgeSliderState(
    thumbLengthRatioMs: Ms<Float>,
    thumbPositionRatioMs: Ms<Float>,
    maxLengthRatio: Float,
    minLengthRatio: Float,
    reductionRatio: Float,
    moveBackRatio: Float,
    thumbLayoutCoorMs: Ms<P6LayoutCoor?>,
    railLayoutCoorMs: Ms<P6LayoutCoor?>
) : AbsEdgeSliderState(
    thumbLengthRatioMs = thumbLengthRatioMs,
    thumbPositionRatioMs = thumbPositionRatioMs,
    maxLengthRatio = maxLengthRatio,
    minLengthRatio = minLengthRatio,
    reductionRatio = reductionRatio,
    moveBackRatio = moveBackRatio,
    thumbLayoutCoorMs = thumbLayoutCoorMs,
    railLayoutCoorMs = railLayoutCoorMs
) {

    @Inject
    constructor() : this(
        thumbLengthRatioMs = StateUtils.ms(EdgeSliderConstants.maxLength),
        maxLengthRatio = EdgeSliderConstants.maxLength,
        minLengthRatio = EdgeSliderConstants.minLength,
        reductionRatio = EdgeSliderConstants.reductionRate,
        moveBackRatio = EdgeSliderConstants.moveBackRatio,
        thumbLayoutCoorMs = StateUtils.ms(null),
        railLayoutCoorMs = StateUtils.ms(null),
        thumbPositionRatioMs = StateUtils.ms(EdgeSliderConstants.startingThumbPositionRatio)
    )

    override val thumbStartInParentPx: Float?
        get() = thumbLayoutCoor?.boundInParent?.top

    override val railEndInWindowPx: Float?
        get() = railLayoutCoor?.boundInWindow?.bottom

    override val thumbEndInWindowPx: Float?
        get() = thumbLayoutCoor?.boundInWindow?.bottom

    override val thumbStartInWindowPx: Float?
        get() = thumbLayoutCoor?.boundInWindow?.top

    override val railStartInWindowPx: Float?
        get() = railLayoutCoor?.boundInWindow?.top

    override val type: EdgeSliderType
        get() = EdgeSliderType.Vertical

    override val railLengthPx: Float?
        get() = railLayoutCoor?.boundInWindow?.height

    override fun computeThumbOffset(density: Density): DpOffset {
        val rt = DpOffset(
            x = 0.dp,
            y = with(density) { thumbPositionInPx.toDp() },
        )
        return rt
    }
}