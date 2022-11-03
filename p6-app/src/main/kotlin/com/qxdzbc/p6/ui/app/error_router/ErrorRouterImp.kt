package com.qxdzbc.p6.ui.app.error_router

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.p6.app.common.utils.RseNav
import com.qxdzbc.p6.app.common.err.ErrorReportWithNavInfo

import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.common.error.ErrorReport
import com.qxdzbc.p6.ui.app.state.AppState
import com.qxdzbc.common.compose.Ms
import com.github.michaelbull.result.Err
import com.qxdzbc.common.compose.St
import javax.inject.Inject

class ErrorRouterImp @Inject constructor(
    private val appStateMs: Ms<AppState>
) : ErrorRouter {
    private var appState by appStateMs
    private var codeEditorState by appState.codeEditorStateMs
    private var oddityContInCodeEditor by codeEditorState.errorContainerMs

    override fun publishToApp(errorReport: ErrorReport?) {
        appState.errorContainer = appState.errorContainer.addErrorReport(errorReport)
    }

    override fun publishToScriptWindow(errorReport: ErrorReport?) {
        oddityContInCodeEditor = oddityContInCodeEditor.addErrorReport(errorReport)
    }

    override fun publishToWindow(errorReport: ErrorReport?, windowId: String?) {
        if (windowId != null) {
            val windowStateMs = appState.getWindowStateMsById(windowId)
            if (windowStateMs != null) {
//            val stackTrace = errorReport?.toException()?.stackTraceToString()?:""
                val er2 = errorReport

                windowStateMs.value.errorContainer =
                    windowStateMs.value.errorContainer.addErrorReport(er2)
            } else {
                publishToApp(errorReport)
            }
        } else {
            publishToApp(errorReport)
        }

    }

    override fun publishToWindow(errorReport: ErrorReport?, workbookKey: WorkbookKey?) {
        if (workbookKey != null) {
            val windowStateMs = appState.getWindowStateMsByWbKey(workbookKey)
            if (windowStateMs != null) {
                val ne = errorReport?.toException()?.stackTrace.toString()


                windowStateMs.value.errorContainer =
                    windowStateMs.value.errorContainer.addErrorReport(errorReport)
            } else {
                publishToApp(errorReport)
            }
        } else {
            publishToApp(errorReport)
        }
    }

    override fun publishToWindow(errorReport: ErrorReport?, windowId: String?, workbookKey: WorkbookKey?) {
        val windowStateMs = windowId?.let { appState.getWindowStateMsById(it) }
            ?: workbookKey?.let { appState.getWindowStateMsByWbKey(it) }
        if (windowStateMs != null) {
            windowStateMs.value.errorContainer =
                windowStateMs.value.errorContainer.addErrorReport(errorReport)
        } else {
            publishToApp(errorReport)
        }
    }

    override fun publishToWindow(errorReport: ErrorReport?, windowId: String?, wbKeySt: St<WorkbookKey>?) {
        TODO("Not yet implemented")
    }

    override fun publish(errorReport: ErrorReportWithNavInfo) {
        this.publishToWindow(errorReport.errorReport, errorReport.windowId, errorReport.wbKey)
    }

    override fun <T> publishIfPossible(resNav: RseNav<T>) {
        if (resNav is Err) {
            this.publish(resNav.error)
        }
    }
}
