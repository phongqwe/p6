package com.qxdzbc.p6.composite_actions.worksheet.make_slider_follow_cell

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.qxdzbc.common.Rse
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWs
import com.qxdzbc.p6.composite_actions.common_data_structure.WbWsSt
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.di.anvil.P6AnvilScope

import com.qxdzbc.p6.ui.app.error_router.ErrorRouter
import com.qxdzbc.p6.ui.app.error_router.ErrorRouters.publishErrIfNeedSt
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(P6AnvilScope::class)
class MoveSliderActionImp @Inject constructor(
    val stateCont:StateContainer,
    val errorRouter: ErrorRouter,
) : MoveSliderAction {

    private val sc  = stateCont

    override fun makeSliderFollowCell(wbws: WbWs, cell: CellAddress, publishErr: Boolean): Rse<Unit> {
        return sc.getWbWsStRs(wbws).map {
            this.makeSliderFollowCell(it,cell, publishErr)
        }
    }

    override fun makeSliderFollowCell(wbwsSt: WbWsSt, cell: CellAddress, publishErr: Boolean): Rse<Unit> {
        val rs=sc.getWsStateRs(wbwsSt).flatMap { wsState->
            val sliderMs = wsState.sliderMs
            val oldSlider = sliderMs.value
            val newSlider = oldSlider.followCell(cell)
            if (newSlider != oldSlider) {
                wsState.setSliderAndRefreshDependentStates(newSlider)
            }
            Ok(Unit)
        }
        if(publishErr){
            rs.publishErrIfNeedSt(errorRouter,null,wbwsSt.wbKeySt)
        }
        return rs
    }

    override fun shiftSlider(cursorLoc: WbWsSt, rowCount: Int, colCount: Int, publishErr: Boolean) {
        val rs=sc.getWsStateRs(cursorLoc).flatMap { wsState->
            val sliderMs = wsState.sliderMs
            val oldSlider = sliderMs.value
            val newSlider = oldSlider.shiftDown(rowCount).shiftRight(colCount)
            if (newSlider != oldSlider) {
                wsState.setSliderAndRefreshDependentStates(newSlider)
            }
            Ok(Unit)
        }
        if(publishErr){
            rs.publishErrIfNeedSt(errorRouter,null,cursorLoc.wbKeySt)
        }
    }
}