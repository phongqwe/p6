package com.qxdzbc.p6.communication.event.data_structure.range.range_to_clipboard

import com.qxdzbc.p6.composite_actions.range.range_to_clipboard.RangeToClipboardRequest
import com.qxdzbc.p6.composite_actions.range.RangeIdDM
import com.qxdzbc.p6.document_data_layer.range.address.RangeAddress
import com.qxdzbc.p6.document_data_layer.workbook.WorkbookKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RangeToClipboardRequestTest {

    @Test
    fun toProto() {
        val m = RangeToClipboardRequest(
            rangeId = RangeIdDM(
                rangeAddress = RangeAddress("C1:J2"),
                wbKey = WorkbookKey("bb"),
                wsName = "QWE"
            ),
            windowId = null
        )
        val p = m.toProto()
        assertEquals(m.rangeId.toProto(), p.rangeId)
        assertFalse(p.hasWindowId())

        val m2 = RangeToClipboardRequest(
            rangeId = m.rangeId,
            windowId = "windowId"
        )

        val p2 = m2.toProto()
        assertEquals(m2.rangeId.toProto(), p2.rangeId)
        assertTrue(p2.hasWindowId())
        assertEquals(m2.windowId, p2.windowId)
    }
}
