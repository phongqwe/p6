package com.qxdzbc.p6.app.action.app.set_active_wd

import com.qxdzbc.common.Rse

interface SetActiveWindowAction {
    fun setActiveWindow(windowId:String):Rse<Unit>
}
