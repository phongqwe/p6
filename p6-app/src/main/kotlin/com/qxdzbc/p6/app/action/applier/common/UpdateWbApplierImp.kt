package com.qxdzbc.p6.app.action.applier.common

import com.qxdzbc.p6.app.document.workbook.Workbook

import com.qxdzbc.p6.ui.app.state.StateContainer
import javax.inject.Inject

class UpdateWbApplierImp @Inject constructor(
    val stateCont:StateContainer,
) : UpdateWbApplier {
   
    override fun updateWb(newWb: Workbook?) {
        if (newWb != null) {
            stateCont.wbCont.overwriteWB(newWb)
        }
    }
}
