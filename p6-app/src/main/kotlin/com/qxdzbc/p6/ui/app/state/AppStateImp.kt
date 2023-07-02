package com.qxdzbc.p6.ui.app.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.app.err.ErrorContainer
import com.qxdzbc.p6.app.err.ErrorContainerImp
import com.qxdzbc.p6.di.state.app_state.*
import com.qxdzbc.p6.ui.app.ActiveWindowPointer
import com.qxdzbc.p6.ui.app.ActiveWindowPointerImp
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.StateUtils.ms
import com.qxdzbc.p6.ui.app.cell_editor.state.CellEditorState
import com.qxdzbc.p6.ui.window.state.WindowState
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.unwrapError
import com.qxdzbc.p6.di.anvil.P6AnvilScope
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(P6AnvilScope::class)
data class AppStateImp @Inject constructor(
    @AppErrorContMs
    override val errorContainerMs: Ms<ErrorContainer> = ms(ErrorContainerImp()),
    override val activeWindowPointer: ActiveWindowPointer = ActiveWindowPointerImp(),
    override var subAppStateCont: SubAppStateContainer,
    override var documentContainer: DocumentContainer,
    override val cellEditorStateMs: Ms<CellEditorState>,
    override var translatorContainer: TranslatorContainer,
) : AppState {


    override var cellEditorState: CellEditorState by cellEditorStateMs
    override var errorContainer: ErrorContainer by errorContainerMs

    override val activeWindowStateMs: Ms<WindowState>?
        get() = activeWindowPointer.windowId?.let {
            this.subAppStateCont.getWindowStateMsById(it)
        }
    override val activeWindowState: WindowState?
        get() = activeWindowStateMs?.value
}
