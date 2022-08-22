package com.qxdzbc.p6.app.action.workbook.delete_worksheet.applier

import com.qxdzbc.p6.app.common.utils.Rse

import com.qxdzbc.p6.app.document.workbook.Workbook

interface DeleteWorksheetApplier {
    fun applyResRs(deletedWsName:String,rs: Rse<Workbook>): Rse<Unit>
}

