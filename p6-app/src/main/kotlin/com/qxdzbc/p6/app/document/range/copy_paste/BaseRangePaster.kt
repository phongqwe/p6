package com.qxdzbc.p6.app.document.range.copy_paste

import com.qxdzbc.common.compose.Ms
import com.qxdzbc.common.copiers.binary_copier.BinaryTransferable
import com.qxdzbc.p6.app.action.common_data_structure.WbWsSt
import com.qxdzbc.p6.app.action.worksheet.paste_range.RangeCopyDM
import com.qxdzbc.p6.app.document.range.RangeCopy
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.ui.app.state.StateContainer
import com.qxdzbc.p6.ui.app.state.TranslatorContainer
import java.awt.Toolkit

abstract class BaseRangePaster : RangePaster {

    abstract val stateCont: StateContainer
    abstract val transContMs: Ms<TranslatorContainer>
    /**
     * extract the range copy from the clipboard
     */
    override fun readDataFromClipboard(wbKey: WorkbookKey, wsName: String): RangeCopy? {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val wbwsSt: WbWsSt? = stateCont.getWbWsSt(wbKey, wsName)
        if(wbwsSt!=null){
            val translator = transContMs.value.getTranslatorOrCreate(wbwsSt)
            val bytes = clipboard.getData(BinaryTransferable.binFlavor) as ByteArray
            val rangeCopy = RangeCopy.fromProtoBytes(bytes, translator)
            return rangeCopy
        }else{
            return null
        }
    }
    override fun readRangeCopyDMFromClipboard(): RangeCopyDM? {
        try{
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val bytes = clipboard.getData(BinaryTransferable.binFlavor) as ByteArray
            val rangeCopy = RangeCopyDM.fromProtoBytes(bytes)
            return rangeCopy
        }catch(e:Exception){
            return null
        }
    }
}
