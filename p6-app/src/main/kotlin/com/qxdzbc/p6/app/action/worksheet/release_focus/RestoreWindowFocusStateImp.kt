package com.qxdzbc.p6.app.action.worksheet.release_focus

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.common.Rse
import com.qxdzbc.p6.app.document.workbook.WorkbookKey

import com.qxdzbc.p6.ui.app.state.AppState
import com.qxdzbc.common.compose.Ms
import com.github.michaelbull.result.*
import com.qxdzbc.p6.di.P6Singleton
import com.qxdzbc.p6.di.anvil.P6AnvilScope
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@P6Singleton
@ContributesBinding(P6AnvilScope::class)
class RestoreWindowFocusStateImp @Inject constructor(
    private val appStateMs: Ms<AppState>,
) : RestoreWindowFocusState {

    var appState by appStateMs

    /**
     * clear and close cell editor, then restore window focus state to default
     */
    override fun restoreAllWindowFocusState(): Rse<Unit> {
        appState.cellEditorState = appState.cellEditorState.clearAllText().close()
        appState.windowStateMsList.map {
            it.value.focusStateMs
        }.forEach {
            it.value = it.value.restoreDefault()
        }
        return Ok(Unit)
    }

    override fun restoreAllWsFocusIfRangeSelectorIsNotActive(): Rse<Unit> {
        val cellEditorState by appState.cellEditorStateMs
        if (!cellEditorState.allowRangeSelector) {
            return this.restoreAllWindowFocusState()
        } else {
            return Ok(Unit)
        }
    }

    override fun setFocusConsideringRangeSelectorAllWindow(): Rse<Unit> {
        if (appState.cellEditorState.allowRangeSelector) {
            appState.windowStateMsList.forEach {wds->
                wds.value.focusState = wds.value.focusState.focusOnEditor().freeFocusOnCursor()
            }
        }else{
            restoreAllWindowFocusState()
        }
        return Ok(Unit)
    }

    override fun setFocusStateConsideringRangeSelector(wbKey: WorkbookKey): Rse<Unit> {
        val cellEditorState = appState.cellEditorState
        val selectingRangeForEditor =cellEditorState.isOpen && cellEditorState.allowRangeSelector
        if (selectingRangeForEditor) {
            appState.getWindowStateMsByWbKey(wbKey)?.also {wds->
                wds.value.focusState = wds.value.focusState.focusOnEditor().freeFocusOnCursor()
            }
        }else{
            restoreAllWindowFocusState()
        }
        return Ok(Unit)
    }
}
