package com.qxdzbc.p6.ui.document.worksheet.cursor.thumb

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.qxdzbc.common.compose.SizeUtils.toDpSize
import com.qxdzbc.common.compose.StateUtils.ms
import com.qxdzbc.common.compose.layout_coor_wrapper.LayoutCoorWrapper
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.ui.document.worksheet.cursor.state.CursorState
import com.qxdzbc.p6.ui.document.worksheet.select_rect.SelectRectStateImp
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ThumbStateImpTest {
    lateinit var s: ThumbStateImp
    val w = 100f
    val h = 30f
    val celllayoutMap: MutableMap<CellAddress, LayoutCoorWrapper> = mutableMapOf()
    val c5 = CellAddress("C5")

    @BeforeTest
    fun b() {

        for (c in 1..20) {
            for (r in 1..20) {
                celllayoutMap[CellAddress(c, r)] = mock<LayoutCoorWrapper>() {
                    val offset = Offset(
                        x = w * (c - 1),
                        y = h * (r - 1),
                    )
                    val size = Size(width = w, height = h)
                    whenever(it.size) doReturn size.toDpSize()
                    whenever(it.posInWindow) doReturn offset
                    whenever(it.boundInWindow) doReturn Rect(
                        offset = offset,
                        size = size
                    )
                }
            }
        }
        val cursorState = mock<CursorState>() {
            whenever(it.mainCell) doReturn CellAddress("C5")
        }
        s = ThumbStateImp(
            cursorStateSt = ms(cursorState),
            cellLayoutCoorMapSt = ms(celllayoutMap),
        )
    }

    @Test
    fun getCellAtCross() {
        s.getCellAtTheCross().forEach { (c, l) ->
            assertTrue(s.mainCell.colIndex == c.colIndex || s.mainCell.rowIndex == c.rowIndex)
        }
    }

    @Test
    fun `getRelevantCell move down`() {
        // anchor == inside C5, moving point = C8
        val rect = SelectRectStateImp(
            anchorPoint = Offset(x = 2 * w + 3, y = 4 * h + 3),
            movingPoint = Offset(x = 2 * w + 3, y = 7 * h + 1)
        )
        val s1 = s.setSelectRectState(rect)
        val relCellMap = s1.getRelevantCells()
        assertEquals(
            (5..8).map { CellAddress("C${it}") },
            relCellMap.keys.toList()
        )

        val (topCell, botCell) = s1.getTopBotCells()!!
        assertEquals(c5, topCell)
        assertEquals(CellAddress("C8"), botCell)

        assertEquals(celllayoutMap[c5]?.posInWindow, s1.selectedRangeOffset)

        val height = relCellMap.map { (c, l) -> l.size.height.value }.sum()
        val expectedSize = DpSize(relCellMap[c5]!!.size.width, height.dp)
        assertEquals(expectedSize, s1.selectedRangeSize)
    }

    @Test
    fun `getRelevantCell move up`() {
        // anchor == inside C5, moving point = C1
        val rect = SelectRectStateImp(
            anchorPoint = Offset(x = 2 * w + 3, y = 4 * h + 3),
            movingPoint = Offset(x = 2 * w + 3, y = 0 * h + 1)
        )
        val s2 = s.setSelectRectState(rect)
        val relCellMap = s2.getRelevantCells()
        assertEquals(
            (1..5).map { CellAddress("C${it}") },
            relCellMap.keys.toList()
        )

        val (topCell, botCell) = s2.getTopBotCells()!!
        val cellC1 = CellAddress("C1")
        assertEquals(cellC1, topCell)
        assertEquals(c5, botCell)

        assertEquals(celllayoutMap[cellC1]?.posInWindow, s2.selectedRangeOffset)

        val height = relCellMap.map { (c, l) -> l.size.height.value }.sum()
        val expectedSize = DpSize(relCellMap[c5]!!.size.width, height.dp)
        assertEquals(expectedSize, s2.selectedRangeSize)
    }

    @Test
    fun `getRelevantCell move right`() {
        // anchor == inside C5, moving point = E5
        val rect = SelectRectStateImp(
            anchorPoint = Offset(x = 2 * w + 3, y = 4 * h + 3),
            movingPoint = Offset(x = 4 * w + 3, y = 4 * h + 3)
        )
        val s3 = s.setSelectRectState(rect)
        val relCellMap = s3.getRelevantCells()
        assertEquals(
            (3..5).map { CellAddress(it, 5) },
            relCellMap.keys.toList()
        )
        val (topCell, botCell) = s3.getTopBotCells()!!
        val cellE5 = CellAddress("E5")
        assertEquals(c5, topCell)
        assertEquals(cellE5, botCell)

        assertEquals(celllayoutMap[c5]?.posInWindow, s3.selectedRangeOffset)
        val width = relCellMap.map { (c, l) -> l.size.width.value }.sum()
        val expectedSize = DpSize(width.dp, relCellMap[c5]!!.size.height)
        assertEquals(expectedSize, s3.selectedRangeSize)

    }

    @Test
    fun `getRelevantCell move left`() {
        // anchor == inside C5, moving point = A5
        val rect = SelectRectStateImp(
            anchorPoint = Offset(x = 2 * w + 3, y = 4 * h + 3),
            movingPoint = Offset(x = 0 * w + 3, y = 4 * h + 3)
        )
        val s3 = s.setSelectRectState(rect)
        val relCellMap = s3.getRelevantCells()
        assertEquals(
            (1..3).map { CellAddress(it, 5) },
            relCellMap.keys.toList()
        )

        val (topCell, botCell) = s3.getTopBotCells()!!
        val cellA5 = CellAddress("A5")
        assertEquals(cellA5, topCell)
        assertEquals(c5, botCell)

        assertEquals(celllayoutMap[cellA5]?.posInWindow, s3.selectedRangeOffset)
        val width = relCellMap.map { (c, l) -> l.size.width.value }.sum()
        val expectedSize = DpSize(width.dp, relCellMap[c5]!!.size.height)
        assertEquals(expectedSize, s3.selectedRangeSize)


    }
}
