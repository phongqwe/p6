package com.qxdzbc.p6.app.action.cell.multi_cell_update

import androidx.compose.runtime.getValue
import com.qxdzbc.common.compose.St
import com.qxdzbc.p6.app.communication.res_req_template.request.remote.RequestToP6WithWorkbookKey
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.rpc.common_data_structure.IndCellDM

/**
 * this is for internal use
 */
data class MultiCellUpdateRequest(
    val wbKeySt:St<WorkbookKey>,
    val wsNameSt:St<String>,
    val cellUpdateList: List<IndCellDM>
) : RequestToP6WithWorkbookKey {
    override val wbKey: WorkbookKey by wbKeySt
    val wsName: String by wsNameSt
}

