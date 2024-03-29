package com.qxdzbc.p6.ui.worksheet.slider.scroll_bar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.qxdzbc.common.compose.LayoutCoorsUtils.toP6Layout
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.StateUtils.rms
import com.qxdzbc.common.compose.view.HSpacer
import com.qxdzbc.common.compose.view.testApp
import com.qxdzbc.p6.ui.worksheet.slider.GridSlider
import com.qxdzbc.p6.ui.worksheet.slider.GridSliderImp
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.action.ScrollBarAction
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.action.ScrollBarActionDoNothing
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.action.ScrollBarActionData
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.component.Rail
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.component.Thumb
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.state.ScrollBarState
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.state.HorizontalScrollBarState
import com.qxdzbc.p6.ui.worksheet.slider.scroll_bar.state.VerticalScrollBarState

/**
 * Scroll bar is a slider at the edge of a worksheet.
 * User can drag on this slider to scroll the worksheet vertically or horizontally.
 * A scroll bar consist of a [Rail] and a [Thumb].
 * - [Rail] takes up the entire length of the slider,
 * - [Thumb] resides on top of the rail, and can move back and fort.
 * A [ScrollBar] give its consumer the following information:
 * - in [onDrag], callers get access to position data of thumb on the rail. This data can be translated into position at the caller's end.
 * - in [onClickOnRail], callers get access to the position ratio [0,1] of the click position on the rail.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScrollBar(
    state: ScrollBarState,
    actions:ScrollBarAction,
    railModifier: Modifier = Modifier,
    thumbModifier: Modifier = Modifier,
    onDrag: (ScrollBarActionData.Drag) -> Unit,
    onClickOnRail: (ScrollBarActionData.ClickOnRail) -> Unit,
) {
    val density = LocalDensity.current

    var isPressed by rms(false)
    var isDragged by rms(false)

    Rail(
        type = state.type,
        modifier = railModifier
            .onGloballyPositioned {
                state.railLayoutCoor = it.toP6Layout(state.thumbLayoutCoor?.refreshVar)
            }
            .onPointerEvent(PointerEventType.Press) {
                isPressed = true
            }
            .onPointerEvent(PointerEventType.Move) {
                if (isPressed) {
                    isDragged = true
                }
            }
            .onPointerEvent(PointerEventType.Release) { pte ->
                if (!isDragged) {
                    pte.changes.firstOrNull()?.position?.also { clickPointOffset ->

                        val clickPosition = when (state) {
                            is HorizontalScrollBarState -> clickPointOffset.x
                            is VerticalScrollBarState -> clickPointOffset.y
                        }

                        state.computePositionRatioOnFullRail(clickPosition)?.let { ratio ->
                            state.performMoveThumbWhenClickOnRail(ratio)
                            onClickOnRail(ScrollBarActionData.ClickOnRail(ratio, state))
                        }
                    }
                }else{
                    /**
                     * This one will handle:
                     * - release thumb drag midway
                     * - release thumb when thumb touch the start or the end of the rail
                     */
                    actions.runAction(ScrollBarActionData.ReleaseFromDrag(state))
                }
                isPressed = false
                isDragged = false
            }
    ) {
        val dragOrientation = remember {
            when (state) {
                is HorizontalScrollBarState -> Orientation.Horizontal
                is VerticalScrollBarState -> Orientation.Vertical
            }
        }


        val thumbOffset = state.computeThumbOffset(density)
        Thumb(
            type = state.type,
            length = state.computeThumbLength(density),
            offset = thumbOffset,
            modifier = thumbModifier
                .onGloballyPositioned { layout ->
                    state.thumbLayoutCoor = layout.toP6Layout(state.thumbLayoutCoor)
                }
                .draggable(
                    orientation = dragOrientation,
                    state = rememberDraggableState { delta ->
                        state.recomputeStateWhenThumbIsDragged(delta)
                        onDrag(ScrollBarActionData.Drag(state))
                    }
                )
        )
    }
}


@Preview
@Composable
fun Preview_VerticalScrollBar() {

    val state = remember {
        VerticalScrollBarState()
    }

    var currentState: ScrollBarState? by rms(null)
    var clickRatio: Float? by rms(null)

    Row {
        ScrollBar(
            state = state,
            actions = ScrollBarActionDoNothing,
            onDrag = {
                currentState = it.state
            },
            onClickOnRail = {cr->
                clickRatio = cr.clickPosition
            },
        )
        HSpacer(50.dp)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Click ratio: ${clickRatio}")

            Text("Drag ratio: ${currentState}")

            Text("Thumb position ratio: ${state.thumbPositionRatio}")

            Text("Thumb scroll ratio: ${state.effectiveThumbPositionRatio}")


        }

    }

}



@Preview
@Composable
fun Preview_HorizontalBar() {

    val sliderState: Ms<GridSlider> = rms(GridSliderImp.forPreview())
    val state = remember {
        HorizontalScrollBarState()
    }

    var currentState: ScrollBarState? by rms(null)
    var clickRatio: Float? by rms(null)

    Column {
        ScrollBar(
            state = state,
            onDrag = {
                currentState = it.state
            },
            actions = ScrollBarActionDoNothing,
            onClickOnRail = {cr->
                clickRatio = cr.clickPosition
            },
        )
        HSpacer(50.dp)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Click ratio: ${clickRatio}")

            Text("Drag ratio: ${currentState}")

            Text("Thumb position ratio: ${state.thumbPositionRatio}")

            Text("${sliderState}")
        }

    }

}
fun main() {
    testApp {
        Preview_VerticalScrollBar()
    }
}