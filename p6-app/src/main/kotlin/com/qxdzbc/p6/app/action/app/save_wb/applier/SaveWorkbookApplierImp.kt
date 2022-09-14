package com.qxdzbc.p6.app.action.app.save_wb.applier

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.p6.app.action.applier.BaseApplier
import com.qxdzbc.p6.app.action.app.save_wb.SaveWorkbookResponse
import com.qxdzbc.p6.app.document.workbook.Workbook
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.di.state.app_state.AppStateMs
import com.qxdzbc.p6.di.state.app_state.StateContainerMs
import com.qxdzbc.p6.ui.app.state.AppState
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.qxdzbc.p6.ui.document.workbook.state.WorkbookState
import com.qxdzbc.p6.ui.window.state.WindowState
import java.nio.file.Path
import javax.inject.Inject

class SaveWorkbookApplierImp @Inject constructor(
    private val baseApplier: BaseApplier,
    @AppStateMs private val appStateMs: Ms<AppState>,
    @StateContainerMs val stateContMs:Ms<StateContainer>,
) : SaveWorkbookApplier {

    private var stateCont by stateContMs
    var appState by appStateMs
    private var wbCont by stateCont.wbContMs

    override fun applyRes(res: SaveWorkbookResponse?) {
        baseApplier.applyRes(res){
            apply(it.wbKey,it.path)
        }
    }
    fun apply(workbookKey: WorkbookKey, path: String) {
        val savedPath = Path.of(path)
        if (workbookKey.path != savedPath) {
            val oldWb: Workbook? = wbCont.getWb(workbookKey)
            val newWbKey: WorkbookKey = workbookKey.setPath(savedPath).setName(savedPath.fileName.toString())
            appState.queryStateByWorkbookKey(workbookKey).ifOk {
                val windowState: WindowState by it.windowStateMs
                val wbState: WorkbookState by it.workbookStateMs
                if (oldWb != null) {
                    // x: update wb key in the old Workbook in wbCont
                    val newWb = oldWb.setKey(newWbKey)
                    wbCont = wbCont.removeWb(workbookKey).addWb(newWb)
                    // x: update wb key in the old WorkbookState
                    it.workbookStateMs.value = wbState
                        .setWorkbookKeyAndRefreshState(newWbKey)
                        .setNeedSave(false) // x: mark wb as not need save because it was already saved
                    // x: update the active workbook pointer if need
                    val activeWBPointerMs = windowState.activeWorkbookPointerMs
                    if (activeWBPointerMs.value.wbKey == workbookKey) {
                        activeWBPointerMs.value = activeWBPointerMs.value.pointTo(newWbKey)
                    }
                }
            }
            if (oldWb != null) {
                appState.codeEditorState = appState.codeEditorState.replaceWorkbookKey(oldWb.key, newWbKey)
            }
        } else {
            stateCont.getWbStateMs(workbookKey)?.also {
                it.value = it.value.setNeedSave(false)
            }
        }
    }
}
