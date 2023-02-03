package com.qxdzbc.p6.app.document.worksheet

import com.qxdzbc.common.error.SingleErrorReport
import com.github.michaelbull.result.Result
interface WsNameGenerator {
    fun nextName(oldName:List<String> = emptyList()):Result<String,SingleErrorReport>
}
