package com.qxdzbc.p6.common.formatter

import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import com.qxdzbc.p6.di.P6AnvilScope
import com.squareup.anvil.annotations.ContributesBinding
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.path.absolutePathString

@Singleton
@ContributesBinding(P6AnvilScope::class)
class RangeAddressFormatterImp @Inject constructor() : RangeAddressFormatter {
    override fun format(cell: CellAddress, wsName: String?, wbKey: WorkbookKey?): String {
        return format(cell.label, wsName, wbKey)
    }

    override fun format(range: RangeAddress, wsName: String?, wbKey: WorkbookKey?): String {
        return format(range.label, wsName, wbKey)
    }

    override fun format(rangeStr: String, wsName: String?, wbKey: WorkbookKey?): String {
        return formatStr(rangeStr, wsName, wbKey?.name, wbKey?.path)
    }

    override fun formatStr(rangeStr: String, wsName: String?, wbKey: String?, path: Path?): String {
        val c1 = rangeStr
        val c2 = wsName?.let { "@${it}" } ?: ""
        val c3 = wbKey?.let { "@${it}" } ?: ""
        val c4 = path?.let { "@" + it.absolutePathString() } ?: ""
        return c1 + c2 + c3 + c4
    }
}
