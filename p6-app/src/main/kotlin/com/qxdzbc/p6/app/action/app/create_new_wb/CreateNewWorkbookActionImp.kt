package com.qxdzbc.p6.app.action.app.create_new_wb

import androidx.compose.runtime.getValue
import com.github.michaelbull.result.mapBoth
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.p6.app.action.window.pick_active_wb.PickDefaultActiveWbAction
import com.qxdzbc.p6.app.document.workbook.Workbook
import com.qxdzbc.p6.app.document.workbook.WorkbookFactory
import com.qxdzbc.p6.di.P6Singleton
import com.qxdzbc.p6.di.anvil.P6AnvilScope
import com.qxdzbc.p6.ui.app.error_router.ErrorRouter
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.squareup.anvil.annotations.ContributesBinding
import java.util.*
import javax.inject.Inject

@P6Singleton
@ContributesBinding(P6AnvilScope::class)
class CreateNewWorkbookActionImp @Inject constructor(
    private val pickDefaultActiveWb: PickDefaultActiveWbAction,
    private val errorRouter: ErrorRouter,
    private val stateContMs: Ms<StateContainer>,
    private val wbf: WorkbookFactory,
) : CreateNewWorkbookAction {

    private val stateCont by stateContMs
    private val globalWbCont by stateCont.wbContMs
    private val globalWbStateCont by stateCont.wbStateContMs

    override fun createNewWb(request: CreateNewWorkbookRequest): CreateNewWorkbookResponse {
        val rt = wbf.createWbRs(request.wbName)
            .mapBoth(
                success = {newWb->
                    iapply(newWb, request.windowId)
                    CreateNewWorkbookResponse(null, newWb, request.windowId)
                },
                failure = {err->
                    errorRouter.publishToWindow(err, request.windowId)
                    CreateNewWorkbookResponse(err, null, request.windowId)
                }
            )
        return rt
    }

    fun iapply(wb: Workbook?, windowId: String?) {
        if (wb != null) {
            stateCont.wbContMs.value = globalWbCont.addOrOverWriteWb(wb)
            globalWbStateCont.getWbStateMs(wb.key)?.also {
                it.value = it.value.setWindowId(windowId)
            }
            var useNewWindow = false
            /*
             * If the request contains a non-existing window id, a new window will be created with that id to hold the newly create wb.
             * If the request contains null window id, a default window will be picked (active window, then first window) if possible, if no window is available, a new window will be created.
             */
            val windowStateMs = stateCont.getWindowStateMsDefaultRs(windowId).component1() ?: run {
                // x: only create new window state if no window is available
                val newWindowId = windowId ?: UUID.randomUUID().toString()
                val (newStateCont, newWindowState) = stateCont.createNewWindowStateMs(newWindowId)
                stateContMs.value = newStateCont
                useNewWindow = true
                newWindowState.value.innerWindowStateMs
            }
            val windowWasEmptyBeforeAdding = windowStateMs.value.isEmpty()
            windowStateMs.value = windowStateMs.value.addWbKey(wb.keyMs)
            if (useNewWindow || windowWasEmptyBeforeAdding) {
                pickDefaultActiveWb.pickAndUpdateActiveWbPointer(windowStateMs.value)
            }
        }
    }
}
