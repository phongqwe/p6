package com.qxdzbc.p6.common.table

data class TableCRColumnImp<C, E>(
    override val colIndex: C,
    override val elements: List<E>
) : TableCRColumn<C, E>, List<E> by elements
