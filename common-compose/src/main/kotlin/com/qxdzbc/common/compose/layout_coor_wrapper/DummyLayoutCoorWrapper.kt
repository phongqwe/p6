package com.qxdzbc.common.compose.layout_coor_wrapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import com.qxdzbc.common.compose.SizeUtils.toDpSize

/**
 */
class DummyLayoutCoorWrapper(
    private val _size: IntSize? = IntSize.Zero,
    override val boundInWindow: Rect? = Rect.Zero,
    override val posInWindow: Offset? = Offset.Zero,
    override val isAttached: Boolean = true,
) : LayoutCoorWrapper {

    override val boundInWindowOrZero: Rect get() = boundInWindow ?: Rect.Zero
    override val posInWindowOrZero: Offset get() = posInWindow ?: Offset.Zero

    override val pixelSizeOrZero: IntSize get() = pixelSize ?: IntSize.Zero
    @Deprecated("wrong calculation")
    override val pixelSize: IntSize? = _size

    override fun dbSize(density: Density): DpSize? {
        return with(density){
            _size?.let{
                DpSize(
                    it.width.toDp(),
                    it.height.toDp()
                )
            }
        }
    }

    override fun dbSizeOrZero(density: Density): DpSize {
        return dbSize(density)?: DpSize.Zero
    }

    override val refreshVar: Boolean get() = throw UnsupportedOperationException()
    override val layout: LayoutCoordinates get() = throw UnsupportedOperationException()

    override fun localToWindow(local: Offset): Offset {
        throw UnsupportedOperationException()
    }

    override fun windowToLocal(window: Offset): Offset {
        throw UnsupportedOperationException()
    }

    override fun ifAttached(f: (lc: LayoutCoorWrapper) -> Unit) {
        throw UnsupportedOperationException()
    }
    @Composable
    override fun ifAttachedComposable(f:@Composable (lc: LayoutCoorWrapper) -> Unit) {
        throw UnsupportedOperationException()
    }

    override fun forceRefresh(i: Boolean): LayoutCoorWrapper {
        throw UnsupportedOperationException()
    }

    override fun forceRefresh(): LayoutCoorWrapper {
        throw UnsupportedOperationException()
    }
}
