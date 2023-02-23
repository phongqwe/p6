package com.qxdzbc.common.compose.layout_coor_wrapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.DpSize

/**
 * The purpose of this interface is to provide an easily mock-able abstraction layer,
 * so that classes relying on LayoutCoordinates can depend on this instead.
 */
interface LayoutCoorWrapper {
    val layout: LayoutCoordinates

    val sizeOrZero:DpSize
    val size:DpSize?

    /**
     * return zero Rect if the current bound is not available
     */
    val boundInWindowOrZero: Rect

    /**
     * Bound in window is null if [layout] is not attached
     */
    val boundInWindow: Rect?

    /**
     * return zero offset if the current position is not available
     */
    val posInWindowOrZero:Offset

    /**
     * position in window is null if [layout] is not attached
     */
    val posInWindow:Offset?

    /**
     * convert a [local] offset to a window offset
     */
    fun localToWindow(local:Offset):Offset
    /**
     * convert a [window] offset to a local offset relative to this layout coordinates
     */
    fun windowToLocal(window:Offset):Offset
    val isAttached:Boolean

    /**
     * If [layout] is attached, invoke the function [f]
     */
    fun ifAttached(f:(lc: LayoutCoorWrapper)->Unit)

    /**
     * If [layout] is attached, invoke the composable [f]
     */
    @Composable
    fun ifAttachedComposable(f:@Composable (lc: LayoutCoorWrapper)->Unit)


    val refreshVar:Boolean
    /**
     * [layout] is sometimes mutated directly by compose runtime. This mean re-setting layout may or may not cause re-composition. To force re-composition, call this function with [i] being the opposite of [refreshVar] of this wrapper or the wrapper to be replaced by this wrapper.
     */
    fun forceRefresh(i:Boolean):LayoutCoorWrapper
    fun forceRefresh():LayoutCoorWrapper

    companion object{
        /**
         * Replace a [LayoutCoorWrapper] with another. If they are the same, force refresh.
         */
        fun LayoutCoorWrapper?.replaceWith(i: LayoutCoorWrapper?): LayoutCoorWrapper? {
            if (i != this) {
                return i
            } else {
                // same = same layout + same force var
                return this?.forceRefresh()
            }
        }
    }
}
