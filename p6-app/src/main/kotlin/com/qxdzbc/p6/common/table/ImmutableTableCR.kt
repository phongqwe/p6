package com.qxdzbc.p6.common.table

import com.qxdzbc.p6.document_data_layer.cell.address.CRAddress

/**
 */
data class ImmutableTableCR<C, R, E>(
    override val dataMap: Map<C, Map<R, E>> = mapOf(),
) : AbstractTableCR<C, R, E>(), Map<C, Map<R, E>> by dataMap {

    override fun remove(colKey: C, rowKey: R): ImmutableTableCR<C, R, E> {
        val newRowMap: Map<R, E>? = dataMap[colKey]?.minus(rowKey)
        if (newRowMap != null) {
            val newMap: Map<C, Map<R, E>> = if (newRowMap.isEmpty()) {
                dataMap - colKey
            } else {
                (dataMap - colKey) + (colKey to newRowMap)
            }
            return ImmutableTableCR(newMap)
        } else {
            return this
        }
    }

    override fun set(crAddress: CRAddress<C, R>, element: E): ImmutableTableCR<C, R, E> {
        return this.set(crAddress.colIndex, crAddress.rowIndex, element)
    }

    override fun set(colKey: C, rowKey: R, element: E): ImmutableTableCR<C, R, E> {
        val newRowMap: Map<R, E> = (dataMap[colKey] ?: emptyMap())
            .minus(rowKey)
            .plus(rowKey to element)
        val newMap = dataMap - colKey + (colKey to newRowMap)
        return ImmutableTableCR(newMap)
    }

    override fun getCol(colKey: C): List<E> {
        return dataMap[colKey]?.values?.toList() ?: emptyList()
    }

    override fun removeCol(colKey: C): ImmutableTableCR<C, R, E> {
        return ImmutableTableCR(this.dataMap - colKey)
    }

    override fun getRow(rowKey: R): List<E> {
        val rt = mutableListOf<E>()
        dataMap.map { (_, m) ->
            val e = m[rowKey]
            if (e != null) {
                rt.add(e)
            }
        }
        return rt
    }

    override fun removeRow(rowKey: R): ImmutableTableCR<C, R, E> {
        val newMap: Map<C, Map<R, E>> = dataMap.map { (k, m) ->
            k to (m - rowKey)
        }.toMap()
        return ImmutableTableCR(newMap)
    }

    override val allElements: List<E>
        get() {
            val rt: List<E> = dataMap.flatMap { (_, m) -> m.values }
            return rt
        }

    override fun removeAll(): TableCR<C, R, E> {
        return ImmutableTableCR()
    }

    override fun isEmpty(): Boolean {
        return dataMap.isEmpty()
    }
}
