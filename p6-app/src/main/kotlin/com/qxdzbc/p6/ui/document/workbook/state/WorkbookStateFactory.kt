package com.qxdzbc.p6.ui.document.workbook.state

import com.qxdzbc.p6.app.document.workbook.Workbook
import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.compose.StateUtils.ms
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface WorkbookStateFactory {
    fun create(
        @Assisted("1") wbMs: Ms<Workbook>,
    ): WorkbookStateImp

    companion object {
        /**
         * Create a new workbook state using [WorkbookStateFactory], and refresh it immediately.
         * Refreshing will create state object for worksheets and cells that does not have a state.
         */
        fun WorkbookStateFactory.createAndRefresh(
            wbMs: Ms<Workbook>,
        ): WorkbookState {
            return this.create(
                wbMs,
            ).apply {
                refresh()
            }
        }
    }
}
