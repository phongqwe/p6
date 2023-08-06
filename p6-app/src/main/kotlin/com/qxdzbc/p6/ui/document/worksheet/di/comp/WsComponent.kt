package com.qxdzbc.p6.ui.document.worksheet.di.comp

import com.qxdzbc.common.compose.Ms
import com.qxdzbc.p6.app.document.worksheet.Worksheet
import com.qxdzbc.p6.ui.document.worksheet.state.WorksheetState
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.BindsInstance
import dagger.Subcomponent


@WsScope
@MergeSubcomponent(
    scope = WsAnvilScope::class,
    modules = [
        WorksheetStateModule::class
    ]
)
interface WsComponent {
    @Subcomponent.Builder
    interface Builder {
        fun setWs(@BindsInstance wsMs: Ms<Worksheet>): Builder
        fun build(): WsComponent
    }

    fun wsState(): WorksheetState
}


