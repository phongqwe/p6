package com.qxdzbc.p6.ui.format2

import com.qxdzbc.common.CollectionUtils.replaceKey
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.app.document.range.address.RangeAddress
import com.qxdzbc.p6.app.document.range.address.RangeAddresses

data class FormatTableImp<T>(
    val valueMap: Map<RangeAddressSet, T> = emptyMap(),
) : FormatTable<T> {

    override fun addValue(cellAddress: CellAddress, formatValue: T): FormatTableImp<T> {
        return this.addValue(RangeAddress(cellAddress), formatValue)
    }

    override fun addValue(rangeAddress: RangeAddress, formatValue: T): FormatTableImp<T> {
        var newMap: Map<RangeAddressSet, T> = this.valueMap
        val toBeBroken = mutableListOf<Map.Entry<RangeAddressSet, T>>()
        var addValueByMerge = false
        for (entry in valueMap) {
            val (radSet: RangeAddressSet, ts) = entry
            if (radSet.hasIntersectionWith(rangeAddress)) {
                if (ts == formatValue) {
                    return this
                } else {
                    toBeBroken.add(entry)
                }
            } else {
                if (ts == formatValue) {
                    val newRangeAddressSet = radSet.addRanges(rangeAddress)
                    newMap = newMap - radSet + (newRangeAddressSet to ts)
                    addValueByMerge = true
                    break
                }
            }
        }
        if (!addValueByMerge) {
            newMap = newMap + (RangeAddressSetImp(rangeAddress) to formatValue)
        }

        newMap = toBeBroken.fold(newMap) { acc, (radSet, ts) ->
            val newRadSet = radSet.removeRange(rangeAddress)
            acc.filterKeys { it != radSet }.let {
                if (newRadSet.isNotEmpty()) {
                    it + (newRadSet to ts)
                } else {
                    it
                }
            }
        }
        return this.copy(valueMap = newMap)

    }

    override fun getFirstValue(cellAddress: CellAddress): T? {
        return this.valueMap.entries.firstOrNull { (k, v) -> k.contains(cellAddress) }?.value
    }

    override fun getFirstValue(rangeAddress: RangeAddress): T? {
        return this.valueMap.entries.firstOrNull { (k, v) -> k.hasIntersectionWith(rangeAddress) }?.value
    }

    override fun getMultiValue(rangeAddress: RangeAddress): List<Pair<RangeAddressSet, T>> {
        val rt = mutableListOf<Pair<RangeAddressSet, T>>()
        for((radSet,t) in this.valueMap){
            rt.add(radSet.getAllIntersectionWith(rangeAddress) to t)
        }
        return rt
    }

    override fun getMultiValueIncludeNullFormat(rangeAddress: RangeAddress): List<Pair<RangeAddressSet, T?>> {
        val availableFormats = getMultiValue(rangeAddress)
        val nullFormatRangeSet=RangeAddressSetImp(rangeAddress).getNotIn(
            availableFormats.flatMap { it.first.ranges }.toSet()
        )
        return availableFormats + (nullFormatRangeSet to null)
    }

    override fun getMultiValueFromRanges(rangeAddresses: Collection<RangeAddress>): List<Pair<RangeAddressSet, T>> {
        val rt= rangeAddresses.fold(emptyList<Pair<RangeAddressSet, T>>()){acc,rangeAddress->
            acc + this.getMultiValue(rangeAddress)
        }
        return rt
    }

    override fun getMultiValueFromRangesIncludeNullFormat(rangeAddresses: Collection<RangeAddress>): List<Pair<RangeAddressSet, T?>> {
        val availableFormats = getMultiValueFromRanges(rangeAddresses)
        val nullFormatRangeSet=RangeAddressSetImp(rangeAddresses).getNotIn(
            availableFormats.flatMap { it.first.ranges }.toSet()
        )
        return availableFormats + (nullFormatRangeSet to null)
    }

    override fun getMultiValueFromCells(cellAddresses: Collection<CellAddress>): List<Pair<RangeAddressSet, T>> {
        return getMultiValueFromRanges(cellAddresses.map { RangeAddress(it) })
    }

    override fun getMultiValueFromCellsIncludeNullFormat(cellAddresses: Collection<CellAddress>): List<Pair<RangeAddressSet, T?>> {
        val rangeAddresses = RangeAddresses.exhaustiveMergeRanges(cellAddresses.map { RangeAddress(it) })
        val availableFormats = getMultiValueFromRanges(rangeAddresses)
        val nullFormatRangeSet=RangeAddressSetImp(rangeAddresses).getNotIn(
            availableFormats.flatMap { it.first.ranges }.toSet()
        )
        return availableFormats + (nullFormatRangeSet to null)
    }

    override fun removeValue(cellAddress: CellAddress): FormatTable<T> {
        return this.removeValue(RangeAddress(cellAddress))
    }

    override fun removeValue(rangeAddress: RangeAddress): FormatTable<T> {
        var newMap = this.valueMap
        for ((radSet, t) in newMap) {
            if (radSet.hasIntersectionWith(rangeAddress)) {
                val newRadSet = radSet.removeRange(rangeAddress)
                if (newRadSet.isEmpty()) {
                    newMap = newMap - radSet
                } else {
                    newMap = newMap.replaceKey(radSet, newRadSet)
                }
            }
        }
        return this.copy(valueMap = newMap)
    }


    override fun removeValueFromMultiCells(cellAddresses: Collection<CellAddress>): FormatTable<T> {
        val rt = cellAddresses.fold(this){acc:FormatTable<T>,ca->
            acc.removeValue(ca)
        }
        return rt
    }

    override fun removeValueFromMultiRanges(rangeAddresses: Collection<RangeAddress>): FormatTable<T> {
        val rt = rangeAddresses.fold(this){acc:FormatTable<T>,ra->
            acc.removeValue(ra)
        }
        return rt
    }

    override fun addValueForMultiCells(cellAddresses: Collection<CellAddress>, formatValue: T): FormatTable<T> {
        val rt = cellAddresses.fold(this){acc,ca->
            acc.addValue(ca,formatValue)
        }
        return rt
    }

    override fun addValueForMultiRanges(rangeAddresses: Collection<RangeAddress>, formatValue: T): FormatTable<T> {
        val rt = rangeAddresses.fold(this){acc,rangeAddress->
            acc.addValue(rangeAddress,formatValue)
        }
        return rt
    }
}
