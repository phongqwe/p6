package com.qxdzbc.p6.ui.workbook.state

import com.qxdzbc.common.error.ErrorHeader
import com.qxdzbc.common.error.SingleErrorReport

object WorkbookStateErrors {
    val prefix = "WorkbookStateContainerErrors_"
    object WorksheetStateNotExist{
        val header = ErrorHeader("${prefix}0", "Worksheet state does not exist")
        fun report(detail:String? = null): SingleErrorReport {
            return SingleErrorReport(
                header = detail?.let { header.setDescription(it) } ?: header
            )
        }
    }

    object CantOverWriteWorkbook{
        val header = ErrorHeader("${prefix}1", "Can't overwrite workbook in workbook state")
        fun report(detail:String? = null): SingleErrorReport {
            return SingleErrorReport(
                header = detail?.let { header.setDescription(it) } ?: header
            )
        }
    }

}
