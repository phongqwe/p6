package com.qxdzbc.p6.composite_actions.common_data_structure

import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.common.compose.St

/**
 * An interface contain info to identify a worksheet using a [WorkbookKey] and a ws name
 */
interface WbWs{
    val wbKey: WorkbookKey
    val wsName:String

    fun isSameWbWs(another: WbWs):Boolean{
        val c1=this.wbKey == another.wbKey
        val c2 = this.wsName == another.wsName
        return c1 && c2
    }
}

fun WbWs(wbKey: WorkbookKey,
         wsName:String):WbWs{
    return WbWsImp(wbKey, wsName)
}

interface WbWsSt:WbWs{
    val wbKeySt: St<WorkbookKey>
    val wsNameSt:St<String>
    override val wbKey: WorkbookKey
        get() = wbKeySt.value
    override val wsName: String
        get() = wsNameSt.value
}

data class WbWsStImp(override val wbKeySt: St<WorkbookKey>, override val wsNameSt: St<String>):WbWsSt
fun WbWsSt(wbKeySt: St<WorkbookKey>, wsNameSt: St<String>):WbWsSt{
    return WbWsStImp(wbKeySt, wsNameSt)
}
