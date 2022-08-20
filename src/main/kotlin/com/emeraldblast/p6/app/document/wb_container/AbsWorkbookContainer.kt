package com.emeraldblast.p6.app.document.wb_container

import com.emeraldblast.p6.app.common.utils.ErrorUtils.getOrThrow
import com.emeraldblast.p6.app.document.workbook.Workbook
import com.emeraldblast.p6.app.document.workbook.WorkbookKey
import com.emeraldblast.p6.common.exception.error.ErrorReport
import com.emeraldblast.p6.ui.common.compose.Ms
import com.emeraldblast.p6.ui.common.compose.St
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import java.nio.file.Path

abstract class AbsWorkbookContainer : WorkbookContainer {

    override fun getWbRs(wbKeySt: St<WorkbookKey>): Result<Workbook, ErrorReport> {
        return getWbMsRs(wbKeySt).map { it.value }
    }

    override fun getWb(wbKeySt: St<WorkbookKey>): Workbook? {
        return getWbMs(wbKeySt)?.component1()
    }

    override fun getWbMs(wbKeySt: St<WorkbookKey>): Ms<Workbook>? {
        return getWbMsRs(wbKeySt).component1()
    }

    override fun addWb(wb: Workbook): WorkbookContainer {
        return this.addWbRs(wb).getOrThrow()
    }

    override fun getWbMs(wbKey: WorkbookKey): Ms<Workbook>? {
        return this.getWbMsRs(wbKey).component1()
    }

    override fun getWb(wbKey: WorkbookKey): Workbook? {
        val rs = this.getWbRs(wbKey)
        return rs.component1()
    }

    override fun getWbRs(wbKey: WorkbookKey): Result<Workbook, ErrorReport> {
        return this.getWbMsRs(wbKey).map { it.value }
    }

    override fun getWb(path: Path): Workbook? {
        return this.getWbRs(path).component1()
    }

    override fun getWbRs(path: Path):Result<Workbook,ErrorReport>{
        return this.getWbMsRs(path).map { it.value }
    }
}
