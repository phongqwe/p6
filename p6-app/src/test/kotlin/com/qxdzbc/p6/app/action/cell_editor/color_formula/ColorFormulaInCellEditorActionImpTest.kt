package com.qxdzbc.p6.app.action.cell_editor.color_formula

import com.qxdzbc.p6.app.action.common_data_structure.WbWsSt
import com.qxdzbc.p6.ui.app.cell_editor.actions.CellEditorAction
import kotlin.test.*
import test.BaseTest

internal class ColorFormulaInCellEditorActionImpTest : BaseTest(){
    lateinit var act:ColorFormulaInCellEditorActionImp
    lateinit var cellEditorAct:CellEditorAction
    lateinit var wbwsSt:WbWsSt
    val cellEditorState get()=ts.sc.cellEditorState
    @BeforeTest
    override fun b() {
        super.b()
        act = ts.p6Comp.colorFormulaActionImp()
        cellEditorAct = ts.p6Comp.cellEditorAction()
        wbwsSt = ts.sc.getWbWsSt(ts.wbKey1,ts.wsn1)!!
    }
    @Test
    fun `colorFormula preserve text content`(){
        val wbwsSt = ts.sc.getWbWsSt(ts.wbKey1,ts.wsn1)!!
        cellEditorAct.openCellEditor(wbwsSt)
        cellEditorAct.changeText("=F1")

        assertEquals("=F1",ts.sc.cellEditorState.currentText)

        val t3 = "=A1+B2+C2"
        cellEditorAct.changeText(t3)
        val currentText = cellEditorState.currentTextField.text
        val newState=act.formatFormulaInCellEditor(cellEditorState)
        // test that text content is preserved after coloring
//        assertEquals(t3,newState.displayText)
        assertEquals(currentText,newState.currentText)
    }
}
