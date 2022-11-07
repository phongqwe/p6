package test.integration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.qxdzbc.common.compose.StateUtils.ms
import com.qxdzbc.common.compose.StateUtils.toMs
import com.qxdzbc.p6.app.action.cell.cell_update.CellUpdateRequestDM
import com.qxdzbc.p6.app.action.common_data_structure.WbWs
import com.qxdzbc.p6.app.action.worksheet.delete_multi.DeleteMultiCellAction
import com.qxdzbc.p6.app.action.worksheet.delete_multi.RemoveMultiCellRequest
import com.qxdzbc.p6.app.document.cell.address.CellAddress
import com.qxdzbc.p6.app.document.cell.CellContentImp
import com.qxdzbc.p6.app.document.cell.IndCellImp
import com.qxdzbc.p6.app.document.cell.CellValue
import com.qxdzbc.p6.app.document.workbook.WorkbookImp
import com.qxdzbc.p6.app.document.workbook.WorkbookKey
import com.qxdzbc.p6.app.document.worksheet.WorksheetImp
import com.qxdzbc.p6.rpc.cell.msg.CellContentDM
import com.qxdzbc.p6.rpc.cell.msg.CellIdDM
import com.qxdzbc.p6.translator.formula.FunctionMapImp
import com.qxdzbc.p6.translator.formula.function_def.P6FunctionDefinitionsImp
import com.qxdzbc.p6.translator.jvm_translator.JvmFormulaTranslator
import com.qxdzbc.p6.translator.jvm_translator.JvmFormulaVisitor
import com.qxdzbc.p6.translator.jvm_translator.tree_extractor.TreeExtractorImp
import test.TestSample
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntegrationTest {

    lateinit var ts: TestSample
    val appState get() = ts.appState
    val sc get() = ts.stateCont

    @BeforeTest
    fun b() {
        ts = TestSample()
    }

    /**
     * A1: =B1
     * B1: =A1+2
     * Both should have error
     */
    @Test
    fun `bug case - circular reference`(){
        val updateCellAct = ts.p6Comp.updateCellAction()
        val wbws = WbWs(ts.wbKey1,ts.wsn1)
        updateCellAct.updateCellDM(
            request= CellUpdateRequestDM(
                cellId = CellIdDM(CellAddress("A1"),wbws),
                cellContent = CellContentDM.fromFormula("=B1")
            ),
            publishError = false
        )

        updateCellAct.updateCellDM(
            request= CellUpdateRequestDM(
                cellId = CellIdDM(CellAddress("B1"),wbws),
                cellContent = CellContentDM.fromFormula("=A1+2")
            ),
            publishError = false
        )

        val a1 = sc.getCell(wbws,CellAddress("A1"))!!
        val b1 = sc.getCell(wbws,CellAddress("B1"))!!
        assertTrue(a1.currentCellValue.isErr, a1.currentValue.toString())
        assertTrue(b1.currentCellValue.isErr,b1.currentValue.toString())

    }

    /**
     * input "=A1" into A1 => receive an error message inside the cell
     * input "=A1+1" into B1 => error msg in A1 should remain the same
     * delete B1 => error msg in A1 should remain the same
     */
    @Test
    fun `bug case- cell error should not change during worksheet operation`(){
        val updateCellAct = ts.p6Comp.updateCellAction()
        val deleteMultiCellAction:DeleteMultiCellAction = ts.p6Comp.deleteMultiCellAction()
        val wbws = WbWs(ts.wbKey1,ts.wsn1)
        updateCellAct.updateCellDM(
            request= CellUpdateRequestDM(
                cellId = CellIdDM(CellAddress("A1"),wbws),
                cellContent = CellContentDM.fromFormula("=A1")
            ),
            publishError = false
        )
        val cell1 = sc.getCell(wbws, CellAddress("A1"))!!

        val originalErrMsg = cell1.displayStr
       cell1.valueAfterRun
        val ms2= cell1.displayStr
        assertEquals(ms2,originalErrMsg)

        updateCellAct.updateCellDM(
            request= CellUpdateRequestDM(
                cellId = CellIdDM(CellAddress("B1"),wbws),
                cellContent = CellContentDM.fromFormula("=A1+1")
            ),
            publishError = false
        )
        val cell2 = sc.getCell(wbws, CellAddress("A1"))!!
        assertEquals(originalErrMsg,cell2.displayStr)

        deleteMultiCellAction.deleteMultiCell(
            RemoveMultiCellRequest(
                cells= listOf(CellAddress("B1")),
                wbKey = wbws.wbKey,
                wsName = wbws.wsName
            )
        )
        assertEquals(originalErrMsg,sc.getCell(wbws, CellAddress("A1"))!!.displayStr)
    }

    /**
     * Scenario: Give a worksheet with 4 cells:
     * A1 = 1
     * A2 = 2
     * A3 = 3
     * C1 = SUM(A1:A3)
     * Remove A2, rerun the worksheet, expect C1 value is correctly computed (which is 1+3 = 3)
     */
    @Test
    fun `test cell auto cell computation update after removing cell`() {
        val appMs = ts.sampleAppStateMs()
        var wbCont by appMs.value.wbContMs
        val wbKeySt = WorkbookKey("Wb1").toMs()
        val wsNameSt = "S1".toMs()
        val translator = JvmFormulaTranslator(
            visitor = JvmFormulaVisitor(
                wbKeySt = wbKeySt,
                wsNameSt = wsNameSt,
                functionMapMs = FunctionMapImp(
                    P6FunctionDefinitionsImp(
                        appStateMs = appMs,
                        docContSt = appMs.value.docContMs,
                    ).functionMap
                ).toMs(),
                appMs.value.docContMs
            ),
            treeExtractor = TreeExtractorImp()
        )
        val wb = WorkbookImp(
            keyMs = wbKeySt,
            worksheetMsList = listOf(
                WorksheetImp(wsNameSt, wbKeySt = wbKeySt).let { ws ->
                    ws.addOrOverwrite(
                        IndCellImp(
                            address = CellAddress("A1"),
                            content = CellContentImp(
                                cellValueMs = CellValue.from(1).toMs()
                            )
                        )
                    ).addOrOverwrite(
                        IndCellImp(
                            address = CellAddress("A2"),
                            content = CellContentImp(
                                cellValueMs = CellValue.from(2).toMs()
                            )
                        )
                    ).addOrOverwrite(
                        IndCellImp(
                            address = CellAddress("A3"),
                            content = CellContentImp(
                                cellValueMs = CellValue.from(3).toMs()
                            )
                        )
                    ).addOrOverwrite(
                        IndCellImp(
                            address = CellAddress("C1"),
                            content = CellContentImp(
//                                formula = "=SUM(A1:A3)",
                                exUnit = translator.translate("=SUM(A1:A3)").component1()!!
                            )
                        )
                    )
                }
            ).map { ms(it) }
        )
        wbCont = wbCont.addWb(wb)

        val wb2 = wb.reRun()
        assertEquals(6.0, wb2.getWs(wsNameSt)!!.getCell(CellAddress("C1"))!!.cellValueAfterRun.valueAfterRun)


        val newWs = wb2.getWs(wsNameSt)!!.removeCell(CellAddress("A2"))
        val wb3 = wb2.addSheetOrOverwrite(newWs).reRun()
        wbCont = wbCont.overwriteWB(wb3)
        val wb4 = wb3.reRun()

        assertEquals(4.0, wb4.getWs(wsNameSt)!!.getCell(CellAddress("C1"))!!.cellValueAfterRun.valueAfterRun)
    }
}
