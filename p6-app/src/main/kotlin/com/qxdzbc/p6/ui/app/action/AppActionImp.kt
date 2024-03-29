package com.qxdzbc.p6.ui.app.action

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ApplicationScope

import com.qxdzbc.p6.ui.app.state.AppState
import com.qxdzbc.common.compose.Ms
import javax.inject.Inject

class AppActionImp @Inject constructor(
    private val appScope: ApplicationScope?,
) : AppAction {


    override fun exitApp() {
        appScope?.exitApplication()
    }
}
