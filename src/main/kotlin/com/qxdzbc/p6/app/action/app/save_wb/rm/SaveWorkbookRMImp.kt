package com.qxdzbc.p6.app.action.app.save_wb.rm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.p6.app.action.app.save_wb.SaveWorkbookRequest
import com.qxdzbc.p6.app.action.app.save_wb.SaveWorkbookResponse
import com.qxdzbc.p6.di.state.app_state.AppStateMs
import com.qxdzbc.p6.app.file.saver.P6Saver
import com.qxdzbc.p6.di.state.app_state.StateContainerMs
import com.qxdzbc.p6.ui.app.state.AppState
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.qxdzbc.p6.ui.common.compose.Ms
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import javax.inject.Inject
import kotlin.io.path.Path

class SaveWorkbookRMImp @Inject constructor(
    private val saver: P6Saver,
    @StateContainerMs val stateContMs:Ms<StateContainer>,
) : SaveWorkbookRM {
    private var stateCont by stateContMs
    override fun saveWb(request: SaveWorkbookRequest): SaveWorkbookResponse? {
        println("save rm")
        val wbRs = stateCont.getWbRs(request.wbKey)
        when (wbRs) {
            is Ok ->{
                val saveRs=saver.save(wbRs.value, Path(request.path))
                when(saveRs){
                    is Ok->{
                        return SaveWorkbookResponse(
                            isError = false,
                            errorReport = null,
                            wbKey = request.wbKey,
                            path = request.path
                        )
                    }
                    is Err->{
                        return SaveWorkbookResponse(
                            isError = true,
                            errorReport = saveRs.error,
                            wbKey = request.wbKey,
                            path = request.path
                        )
                    }
                }
            }
            is Err ->{
                return SaveWorkbookResponse(
                    isError = true,
                    errorReport = wbRs.error,
                    wbKey = request.wbKey,
                    path = request.path
                )
            }
        }
    }
}
