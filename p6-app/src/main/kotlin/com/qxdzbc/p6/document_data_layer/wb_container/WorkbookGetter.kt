package com.qxdzbc.p6.document_data_layer.wb_container

import com.qxdzbc.common.Rse
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.St
import com.qxdzbc.p6.document_data_layer.workbook.Workbook
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import java.nio.file.Path

/**
 * An interface only for getting [Workbook]
 */
interface WorkbookGetter{
    val allWbs:List<Workbook>
    val allWbMs:List<Ms<Workbook>>
    fun getWb(wbKeySt: St<WorkbookKey>): Workbook?
    fun getWbMs(wbKeySt: St<WorkbookKey>): Ms<Workbook>?
    fun getWbRs(wbKeySt: St<WorkbookKey>): Rse<Workbook>
    fun getWbMsRs(wbKeySt: St<WorkbookKey>): Rse<Ms<Workbook>>

    fun getWb(wbKey: WorkbookKey): Workbook?
    fun getWbMs(wbKey: WorkbookKey): Ms<Workbook>?
    fun getWbRs(wbKey: WorkbookKey): Rse<Workbook>
    fun getWbMsRs(wbKey: WorkbookKey): Rse<Ms<Workbook>>

    fun getWb(path: Path): Workbook?
    fun getWbRs(path: Path): Rse<Workbook>
    fun getWbMsRs(path: Path): Rse<Ms<Workbook>>
}
