package com.qxdzbc.p6.ui.worksheet.state

import com.qxdzbc.common.compose.Ms
import com.qxdzbc.p6.document_data_layer.worksheet.Worksheet
import com.qxdzbc.p6.di.P6AnvilScope
import com.qxdzbc.p6.ui.worksheet.di.WsComponent
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton


@Singleton
@ContributesBinding(P6AnvilScope::class)
class WorksheetStateFactoryImp @Inject constructor(
    /**
     * This [Provider] provides a new instance of [WsComponent.Builder] every time it is called.
     */
    private val componentBuilderProvider: Provider<WsComponent.Builder>
) : WorksheetStateFactory {

    override fun create(wsMs: Ms<Worksheet>): WorksheetState {
        val compBuilder = componentBuilderProvider.get()
        val comp = compBuilder
            .setWs(wsMs)
            .build()
        val rt = comp.wsState()
        rt.refresh()
        return rt
    }

}
