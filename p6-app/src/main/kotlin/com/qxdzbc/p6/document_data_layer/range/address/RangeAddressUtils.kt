package com.qxdzbc.p6.document_data_layer.range.address

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.mapBoth
import com.qxdzbc.common.ResultUtils.toOk
import com.qxdzbc.common.Rse
import com.qxdzbc.p6.document_data_layer.cell.address.CR
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddresses
import com.qxdzbc.p6.document_data_layer.cell.address.toModel
import com.qxdzbc.p6.document_data_layer.range.RangeErrors
import com.qxdzbc.p6.proto.DocProtos.RangeAddressProto
import com.qxdzbc.p6.ui.worksheet.WorksheetConstants


object RangeAddressUtils {

    val InvalidRange = RangeAddressImp(CellAddresses.InvalidCell, CellAddresses.InvalidCell)

    // A1:B2, $A1:B$2, $A$1:$B$2
    val rangeAddressPattern = Regex("[$]?[a-zA-Z]+[$]?[1-9][0-9]*:[$]?[a-zA-Z]+[$]?[1-9][0-9]*")

    // whole col : A:A, $A:$A, A:$A, $A:A
    // whole row : 123:233, $123:$123, $123:123, 123:$123
    val wholeRangeAddressPattern = Regex("([$]?[a-zA-Z]+|[$]?[1-9][0-9]*):([$]?[a-zA-Z]+|[$]?[1-9][0-9]*)")
    val singleWholeColAddressPattern = Regex("[$]?[a-zA-Z]+")
    val singleWholeRowAddressPattern = Regex("[$]?[1-9][0-9]*")

    fun rangeFromLabelRs(label: String): Rse<RangeAddress> {
        val rt = CellAddresses.fromLabelRs(label).mapBoth(
            success = {
                Ok(RangeAddress(it))
            },
            failure = {
                val isNormalRange = rangeAddressPattern.matchEntire(label) != null
                if (isNormalRange) {
                    val splits = label.split(":")
                    val cell1 = CellAddress(splits[0])
                    val cell2 = CellAddress(splits[1])
                    return RangeAddress(cell1, cell2).toOk()
                }
                // x: whole range = whole col or whole row
                val isWholeRange = wholeRangeAddressPattern.matchEntire(label) != null
                if (isWholeRange) {
                    val splits = label.split(":")
                    val firstPart: String = splits[0]
                    val lastPart: String = splits[1]

                    val firstPartIsCol = singleWholeColAddressPattern.matchEntire(firstPart) != null
                    val firstPartIsRow = singleWholeRowAddressPattern.matchEntire(firstPart) != null

                    val lastPartIsCol = singleWholeColAddressPattern.matchEntire(lastPart) != null
                    val lastPartIsRow = singleWholeRowAddressPattern.matchEntire(lastPart) != null

                    if (firstPartIsCol && lastPartIsCol) {
                        val firstColCR = CR.fromLabel(firstPart)
                        val lastColCR = CR.fromLabel(lastPart)
                        if (firstColCR == null || lastColCR == null) {
                            return RangeErrors.InvalidRangeAddress.report(label).toErr()
                        } else {
                            val firstCR = CellAddresses.minOf(firstColCR, lastColCR)
                            val lastCR = CellAddresses.maxOf(firstColCR, lastColCR)
                            val rt = RangeAddress(
                                CellAddress(firstCR, CR(1, firstCR.isLocked)),
                                CellAddress(lastCR, CR(WorksheetConstants.rowLimit, lastCR.isLocked))
                            ).toOk()
                            return rt
                        }
                    } else if (firstPartIsRow && lastPartIsRow) {
                        val fp = CR.fromLabel(firstPart)
                        val lp = CR.fromLabel(lastPart)
                        if (fp == null || lp == null) {
                            return RangeErrors.InvalidRangeAddress.report(label).toErr()
                        } else {
                            val first = CellAddresses.minOf(fp, lp)
                            val last = CellAddresses.maxOf(fp, lp)

                            return RangeAddress(
                                CellAddress(CR(1, first.isLocked), first),
                                CellAddress(CR(WorksheetConstants.colLimit, last.isLocked), last)
                            ).toOk()
                        }
                    } else {
                        RangeErrors.InvalidRangeAddress.report(label).toErr()
                    }
                }
                RangeErrors.InvalidRangeAddress.report(label).toErr()
            }
        )
        return rt
    }

    /**
     * Create a [RangeAddress] from a label, such as "A1:B3", "22:33", "F:Z"
     */
    fun rangeFromLabel(label: String): RangeAddress {
        val z = this.rangeFromLabelRs(label)
        when (z) {
            is Ok -> return z.value
            else -> throw IllegalArgumentException("Illegal range address format: ${label}")
        }
    }

    fun rangeForMultiCells(cells: List<CellAddress>): RangeAddress {
        if (cells.isEmpty()) {
            throw IllegalArgumentException("Can't construct range address from 0 cell address")
        } else {
            var maxRow = -1
            var maxCol = -1
            var minRow = Int.MAX_VALUE
            var minCol = Int.MAX_VALUE
            for (cell in cells) {
                val r = cell.rowIndex
                val c = cell.colIndex
                if (r >= maxRow) maxRow = r
                if (r <= minRow) minRow = r
                if (c >= maxCol) maxCol = c
                if (c <= minCol) minCol = c
            }
            return RangeAddressImp(
                topLeft = CellAddresses.fromIndices(minCol, minRow),
                botRight = CellAddresses.fromIndices(maxCol, maxRow)
            )
        }
    }

    fun rangeFor2Cells(address1: CellAddress, address2: CellAddress): RangeAddress {
        val firstAddress = CellAddresses.fromCR(
            colCR = CellAddresses.minOf(address1.colCR, address2.colCR),
            rowCR = CellAddresses.minOf(address1.rowCR, address2.rowCR)
        )
        val lastAddress = CellAddresses.fromCR(
            colCR = CellAddresses.maxOf(address1.colCR, address2.colCR),
            rowCR = CellAddresses.maxOf(address1.rowCR, address2.rowCR)
        )
        return RangeAddressImp(firstAddress, lastAddress)
    }

    fun rangeFromSingleCell(cellAddress: CellAddress): RangeAddress {
        return RangeAddressImp(cellAddress, cellAddress)
    }

    fun rangeForWholeCol(colIndex: Int): RangeAddress {
        return rangeFor2Cells(
            address1 = CellAddresses.fromIndices(colIndex, 1),
            address2 = CellAddresses.fromIndices(colIndex, WorksheetConstants.rowLimit),
        )
    }

    fun rangeForWholeRow(rowIndex: Int): RangeAddress {
        return rangeFor2Cells(
            address1 = CellAddresses.fromIndices(1, rowIndex),
            address2 = CellAddresses.fromIndices(WorksheetConstants.colLimit, rowIndex),
        )
    }

    fun rangeForWholeMultiRow(row1: Int, row2: Int): RangeAddress {
        val fromRow = minOf(row1, row2)
        val toRow = maxOf(row1, row2)
        var rt = rangeForWholeRow(fromRow)
        for (x in fromRow + 1..toRow) {
            rt = rt.mergeWith(rangeForWholeRow(x))
        }
        return rt
    }

    fun rangeForWholeMultiCol(col1: Int, col2: Int): RangeAddress {
        val fromCol = minOf(col1, col2)
        val toCol = maxOf(col1, col2)
        var rt = rangeForWholeCol(fromCol)
        for (x in fromCol + 1..toCol) {
            rt = rt.mergeWith(rangeForWholeCol(x))
        }
        return rt
    }

    fun RangeAddressProto.toModel(): RangeAddress {
        return rangeFor2Cells(
            address1 = this.topLeft.toModel(),
            address2 = this.botRight.toModel()
        )
    }

    internal fun exhaustiveMergeCellsIntoOneRange(
        cells: List<CellAddress>,
        rangeAddress: RangeAddress
    ): Pair<RangeAddress, List<CellAddress>> {
        val cRanges = cells.map { RangeAddress(it) }
        val (resultRange, unUsedRanges) = exhaustiveMergeRangesIntoOneRange(cRanges, rangeAddress)
        return Pair(
            resultRange,
            unUsedRanges.map { it.topLeft }
        )
    }


    /**
     * attempt to exhaustively merge a list of ranges into one range
     */
    internal fun exhaustiveMergeRangesIntoOneRange(
        ranges: List<RangeAddress>,
        rangeAddress: RangeAddress
    ): Pair<RangeAddress, List<RangeAddress>> {
        var iterRange = rangeAddress
        val unUsedRanges = mutableListOf<RangeAddress>()
        var candidateRanges = ranges
        var merged = false
        while (true) {
            for (range in candidateRanges) {
                val tr = iterRange.strictMerge(range)
                if (tr != null) {
                    iterRange = tr
                    merged = true
                } else {
                    unUsedRanges.add(range)
                }
            }
            if (!merged) {
                break
            }
            candidateRanges = unUsedRanges.map { it }
            unUsedRanges.clear()
            merged = false
        }
        return Pair(iterRange, unUsedRanges)
    }

    /**
     * @return a list of merged range and a list of un-used cells
     */
    internal fun exhaustiveMergeCell(cellList: List<CellAddress>): Pair<List<RangeAddress>, List<CellAddress>> {
        val r = exhaustiveMergeRanges(cellList.map { RangeAddress(it) })
        val unUsedCells = r.filter { it.isCell() }.map { it.topLeft }
        val resultRanges = r.filter { !it.isCell() }
        return Pair(resultRanges, unUsedCells)
    }

    internal fun exhaustiveMergeCellsToRanges(
        cells: List<CellAddress>,
        ranges: List<RangeAddress>
    ): Pair<List<RangeAddress>, List<CellAddress>> {
        val cellRanges = cells.map { RangeAddress(it) }
        val rs = exhaustiveMergeRanges(ranges + cellRanges)
        val unUsedCell = rs.filter { it.isCell() }.map { it.topLeft }
        val newRanges = rs.filter { it.isCell().not() }
        return Pair(newRanges, unUsedCell)
    }

    /**
     * attempt to merge a cell into a list of range.
     * If the cell is merged into one of the range in the list, proceed to exhaustively merge all the range if possible.
     * @return a list of ranges that cannot be further merged
     */
    internal fun exhaustiveMergeRanges(
        cellAddress: CellAddress,
        fragRanges: List<RangeAddress>
    ): Pair<Boolean, List<RangeAddress>> {
        val expandedRangeList = mutableListOf<RangeAddress>()
        var ignoreTheRest = false
        var cellWasConsumed = false
        for (range in fragRanges) {
            if (!ignoreTheRest) {
                val expandedRange = range.strictMerge(cellAddress)
                if (expandedRange != null) {
                    expandedRangeList.add(expandedRange)
                    cellWasConsumed = true
                    ignoreTheRest = true
                } else {
                    expandedRangeList.add(range)
                }
            } else {
                expandedRangeList.add(range)
            }
        }
        return Pair(cellWasConsumed, exhaustiveMergeRanges(expandedRangeList))
    }

    /**
     * exhaustively merge a list of range.
     * @return a list of ranges that cannot be further merged
     */
    fun exhaustiveMergeRanges(rangeList: Collection<RangeAddress>): List<RangeAddress> {
        var rt = rangeList
        while (true) {
            val newL = exhaustiveMergeRange_OneIteration2(rt)
            if (rt.size == newL.size) {
                break
            } else {
                rt = newL
            }
        }
        return rt.toList()
    }

    /**
     * TODO Check if this has any use, delete if it does not.
     */
    private fun exhaustiveMergeRange_OneIteration(rangeList: List<RangeAddress>): List<RangeAddress> {
        if (rangeList.isEmpty() || rangeList.size == 1) {
            return rangeList
        } else {
            val l = mutableListOf<RangeAddress>()
            val used = mutableSetOf<RangeAddress>()
            for ((x, r1) in rangeList.withIndex()) {
                if (r1 !in used) {
                    var merged = false
                    for (y in x + 1 until rangeList.size) {
                        val r2 = rangeList[y]
                        if (r2 !in used) {
                            val r = r1.strictMerge(r2)
                            if (r != null) {
                                l.add(r)
                                used.add(r1)
                                used.add(r2)
                                merged = true
                                break
                            }
                        }
                    }
                    val r1IsUnableToMergeWithAnyOtherRange = !merged && r1 !in used
                    if (r1IsUnableToMergeWithAnyOtherRange) {
                        l.add(r1)
                    }
                }
            }
            return l
        }
    }

    private fun exhaustiveMergeRange_OneIteration2(rangeList: Collection<RangeAddress>): List<RangeAddress> {
        if (rangeList.isEmpty() || rangeList.size == 1) {
            return rangeList.toList()
        } else {
            val used = mutableSetOf<RangeAddress>()
            val rt = rangeList.toMutableList()
            var startIndex = 0
            var keepGoing = true
            while (keepGoing) {
                for (x in startIndex until rt.size) {
                    val r1 = rt[x]
                    var redo = false
                    if (r1 !in used) {
                        for (y in x + 1 until rt.size) {
                            val r2 = rt[y]
                            if (r2 !in used) {
                                val r = r1.strictMerge(r2)
                                if (r != null) {
                                    used.add(r1)
                                    used.add(r2)
                                    startIndex = x
                                    rt[x] = r
                                    rt.removeAt(y)
                                    redo = true
                                    break
                                }
                            }
                        }
                        keepGoing = x != (rt.size - 1)
                        if (redo) {
                            break
                        }
                    }
                }
            }
            return rt
        }
    }

}
