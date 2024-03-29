package com.qxdzbc.p6.ui.window.tool_bar.color_selector.action

import androidx.compose.ui.graphics.Color
import com.qxdzbc.p6.ui.window.tool_bar.color_selector.state.ColorSelectorStateImp
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import test.BaseAppStateTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class TextColorSelectorActionTest : BaseAppStateTest(){

    lateinit var action:TextColorSelectorAction
    val color = Color(123)

    @BeforeTest
    fun b2(){
        action = TextColorSelectorAction(
            ts.sc,
            mock(),
            mock()
        )
    }

    @Test
    fun clearColor() {
        val e = ColorSelectorStateImp(null)
        ts.sc.getTextColorSelectorStateMs(ts.window1Id)!!.value = ColorSelectorStateImp(color)
        ts.sc.getTextColorSelectorState(ts.window1Id) shouldNotBe e
        action.clearColor(ts.window1Id)
        ts.sc.getTextColorSelectorState(ts.window1Id) shouldBe e
    }

    @Test
    fun pickColor() {
        test(
            """
               trigger pickColor action, 
               expect all cells and ranges selected by the current cursor
               to have the selected color as their new text color 
            """.trimIndent()
        ){
            val e = ColorSelectorStateImp(color)
            ts.sc.getTextColorSelectorState(ts.window1Id) shouldNotBe e
            action.pickColor(ts.window1Id,color)
            ts.sc.getTextColorSelectorState(ts.window1Id) shouldBe e
            verify(action.updateCellFormatAction,times(1)).setTextColorOnSelectedCells(color,undoable=true)
        }
    }
}
