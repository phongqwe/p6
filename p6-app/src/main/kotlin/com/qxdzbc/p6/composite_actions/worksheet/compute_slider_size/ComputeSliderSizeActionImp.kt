package com.qxdzbc.p6.composite_actions.worksheet.compute_slider_size

import androidx.compose.ui.unit.*
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.di.P6AnvilScope
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.qxdzbc.p6.ui.worksheet.slider.GridSlider
import com.qxdzbc.p6.ui.worksheet.state.WorksheetState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ContributesBinding(P6AnvilScope::class)
class ComputeSliderSizeActionImp @Inject constructor(
    val stateCont: StateContainer,
) : ComputeSliderSizeAction {

    override fun computeSliderPropertiesForAvailableSpace(
        wsState: WorksheetState,
        availableSpace: DpSize,
    ) {
        val newSlider = computeSliderProperties(
            oldGridSlider = wsState.slider,
            availableSpace = availableSpace,
            anchorCell = wsState.slider.topLeftCell,
            getColWidth = wsState::getColumnWidthOrDefault,
            getRowHeight = wsState::getRowHeightOrDefault,
        )
        wsState.sliderMs.value = newSlider
    }

    fun computeSliderProperties(
        oldGridSlider: GridSlider,
        availableSpace: DpSize,
        /**
         * [anchorCell] should be the top-left cell of the current slider
         **/
        anchorCell: CellAddress,
        getColWidth: (colIndex: Int) -> Dp,
        getRowHeight: (rowIndex: Int) -> Dp,
    ): GridSlider {

        val limitWidth = availableSpace.width
        val limitHeight = availableSpace.height

        val rowResult = computeRow(
            oldGridSlider = oldGridSlider,
            anchorCell = anchorCell,
            limitHeight = limitHeight,
            getRowHeight = getRowHeight
        )

        val colResult = computeCol(
            oldGridSlider = oldGridSlider,
            anchorCell = anchorCell,
            limitWidth = limitWidth,
            getColWidth = getColWidth
        )


        val newSlider = oldGridSlider.let { slider ->
            var s: GridSlider? = slider
            val rowRange = rowResult.itemRangeIncludingMargin

            /**
             * set visible col and row
             */
            s = rowRange?.let {
                s?.setVisibleRowRange(it)
            }
            val colRange = colResult.itemRangeIncludingMargin
            s = colRange?.let {
                s?.setVisibleColRange(it)
            }
            /**
             * set visible margin
             */
            s = s?.setMarginRow(rowResult.margin)
                ?.setMarginCol(colResult.margin)

            /**
             * final slider
             */
            s ?: slider
        }

        return newSlider
    }

    /**
     * Compute visible row range including margin row to be used in a grid slider
     */
    fun computeCol(
        oldGridSlider: GridSlider,
        anchorCell: CellAddress,
        limitWidth: Dp,
        getColWidth: (colIndex: Int) -> Dp,
    ): TwoSideResult {
        val rt = computeTwoWay(
            initIndex = anchorCell.colIndex,
            limitSize = limitWidth,
            getItemSize = getColWidth,
            checkIndexValidity = { colIndex ->
                colIndex in oldGridSlider.colLimit && colIndex >= oldGridSlider.topLeftCell.colIndex
            }
        )
        return rt
    }


    /**
     * Compute visible row range including margin row to be used in a grid slider
     */
    private fun computeRow(
        oldGridSlider: GridSlider,
        anchorCell: CellAddress,
        limitHeight: Dp,
        getRowHeight: (rowIndex: Int) -> Dp,
    ): TwoSideResult {
        val rt = computeTwoWay(
            initIndex = anchorCell.rowIndex,
            limitSize = limitHeight,
            getItemSize = getRowHeight,
            checkIndexValidity = { rowIndex ->
                rowIndex in oldGridSlider.rowLimit && rowIndex >= oldGridSlider.topLeftCell.rowIndex
            }
        )
        return rt
    }

    /**
     * Compute a range of indices by calling both [computeUp] and [computeDown], then combine the two output into one [TwoSideResult].
     */
    fun computeTwoWay(
        initIndex: Int,
        limitSize: Dp,
        getItemSize: (rowIndex: Int) -> Dp,
        checkIndexValidity: (Int) -> Boolean,
    ): TwoSideResult {
        val upRs = computeUp(
            initIndex = initIndex,
            limitSize = limitSize,
            getItemSize = getItemSize,
            checkIndexValidity = checkIndexValidity
        )


        val downRs = upRs.toIndex?.let { toIndex ->
            computeDown(
                initIndex = toIndex + 1,
                limitSize = limitSize,
                initSize = upRs.accumSize,
                checkIndexValidity = checkIndexValidity,
                getItemSize = getItemSize
            )
        }

        return TwoSideResult.from(
            upRs = upRs,
            downRs = downRs
        )
    }


    /**
     * Compute a range of index starting from [initIndex] by "moving up" (think moving up row by row).
     *
     * This function calculates a range of indices that satisfy size constraints. It starts from an initial index and iterates backward, accumulating item sizes until the accumulated size reaches the limit size
     */
    fun computeUp(
        /**
         * initial index. Will be included in the final result
         */
        initIndex: Int,
        /**
         * Maximum size
         */
        limitSize: Dp,
        /**
         * a function to get item size by index
         */
        getItemSize: (index: Int) -> Dp,
        /**
         * a predicate to check if an index is valid.
         * This is to prevent invalid indices.
         */
        checkIndexValidity: (Int) -> Boolean,
    ): UpResult {

        var toIndex = initIndex
        var accumSize = getItemSize(initIndex)

        if (accumSize > limitSize) {
            return UpResult(
                fromIndex = null,
                toIndex = null,
                margin = toIndex,
                accumSize = accumSize
            )
        } else {
            while (accumSize < limitSize) {
                val nextIndex = toIndex - 1
                if (checkIndexValidity(nextIndex)) {
                    val itemSize = getItemSize(nextIndex)
                    val nextSize = accumSize + itemSize
                    if (nextSize < limitSize) {
                        accumSize = nextSize
                        toIndex = nextIndex
                    } else {
                        break
                    }
                } else {
                    break
                }
            }

            return UpResult(
                fromIndex = toIndex,
                toIndex = initIndex,
                margin = null,
                accumSize = accumSize
            )
        }
    }


    /**
     * Compute downward, [initIndex] will be included into the result.
     *
     * This works as follows:
     * - This function iterates through indices and accumulate item sizes until reaching the limit.
     * - The result, including start and end indices and a potential margin index, is returned in a DownResult object.
     * - This function essentially finds a range of indices fitting the size criteria.
     *
     * Margin index is the index of the last item that if its size is added to the accumulated size will make the accumulated value larger than the size limit.
     */
    fun computeDown(
        /**
         * starting index
         */
        initIndex: Int,
        /**
         * maximum size
         */
        limitSize: Dp,
        /**
         * initial size
         */
        initSize: Dp,
        /**
         * to check if an index is valid
         */
        checkIndexValidity: (Int) -> Boolean,
        /**
         * a function to get item size from index
         */
        getItemSize: (colIndex: Int) -> Dp,
    ): DownResult {
        var toIndex = initIndex
        var accumSize = initSize + getItemSize(initIndex)

        if (accumSize > limitSize) {
            // the init item does not fit in the remaining space -> it is a margin item
            return DownResult(
                fromIndex = null,
                toIndex = null,
                margin = toIndex
            )
        } else if (accumSize == limitSize) {
            // whole item -> no margin
            return DownResult(toIndex, toIndex, null)
        } else {
            var moreThanSizeLimit = false
            while (accumSize < limitSize) {
                val nextIndex = toIndex + 1
                if (checkIndexValidity(nextIndex)) {
                    val itemSize = getItemSize(nextIndex)
                    val nextSize = accumSize + itemSize
                    if (nextSize <= limitSize) {
                        accumSize = nextSize
                        toIndex = nextIndex
                    } else {
                        if (nextSize > limitSize) {
                            moreThanSizeLimit = true
                            break
                        }
                    }
                } else {
                    break
                }
            }

            val margin = if (moreThanSizeLimit) {
                toIndex + 1
            } else {
                null
            }

            return DownResult(
                fromIndex = initIndex,
                toIndex = toIndex,
                margin = margin,
            )
        }
    }


    /**
     * Result for "up" computation
     */
    data class UpResult(
        val fromIndex: Int?,
        val toIndex: Int?,
        val margin: Int?,
        val accumSize: Dp,
    )

    /**
     * Result for "down" computation
     */
    data class DownResult(
        val fromIndex: Int?,
        val toIndex: Int?,
        val margin: Int?,
    )

    /**
     * A combined result, combined from both up and down
     */
    data class TwoSideResult(
        val fromIndex: Int?,
        val toIndex: Int?,
        val margin: Int?,
    ) {
        val itemRangeIncludingMargin: IntRange?
            get() {
                val f = fromIndex
                val t = margin ?: toIndex
                if (f != null && t != null) {
                    return f..t
                } else {
                    return null
                }
            }

        companion object {
            fun from(
                upRs: UpResult,
                downRs: DownResult?
            ): TwoSideResult {
                return TwoSideResult(
                    fromIndex = upRs.fromIndex,
                    toIndex = downRs?.toIndex ?: upRs.toIndex,
                    margin = upRs.margin ?: downRs?.margin
                )
            }
        }
    }

}
