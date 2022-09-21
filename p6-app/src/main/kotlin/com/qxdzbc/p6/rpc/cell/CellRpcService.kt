package com.qxdzbc.p6.rpc.cell

import androidx.compose.runtime.getValue
import com.qxdzbc.common.compose.St
import com.qxdzbc.p6.app.action.cell.cell_update.CellUpdateRequest2.Companion.toModel
import com.qxdzbc.p6.app.action.common_data_structure.SingleSignalResponse
import com.qxdzbc.p6.app.common.utils.CoroutineUtils
import com.qxdzbc.p6.app.common.utils.Utils.onNextAndComplete
import com.qxdzbc.p6.app.document.cell.d.Cell
import com.qxdzbc.p6.app.document.cell.d.CellContent
import com.qxdzbc.p6.app.document.cell.d.CellContentImp
import com.qxdzbc.p6.app.document.cell.d.CellValue
import com.qxdzbc.p6.di.AppCoroutineScope
import com.qxdzbc.p6.di.state.app_state.StateContainerSt
import com.qxdzbc.p6.proto.CellProtos
import com.qxdzbc.p6.proto.CommonProtos
import com.qxdzbc.p6.proto.DocProtos
import com.qxdzbc.p6.proto.rpc.CellServiceGrpc
import com.qxdzbc.p6.rpc.common_data_structure.StrMsg
import com.qxdzbc.p6.rpc.cell.msg.CopyCellRequest.Companion.toModel
import com.qxdzbc.p6.rpc.worksheet.msg.CellIdProtoDM
import com.qxdzbc.p6.rpc.worksheet.msg.CellIdProtoDM.Companion.toModel
import com.qxdzbc.p6.ui.app.state.StateContainer
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject


class CellRpcService @Inject constructor(
    @StateContainerSt
    val stateContSt:St<@JvmSuppressWildcards StateContainer>,
    val acts: CellRpcActions,
    @AppCoroutineScope
    val crtScope: CoroutineScope
) : CellServiceGrpc.CellServiceImplBase() {

    val launchOnMain = CoroutineUtils.makeLaunchOnMain(crtScope)
    private val sc by stateContSt

    override fun updateCellContent(
        request: CellProtos.CellUpdateRequestProto?,
        responseObserver: StreamObserver<CommonProtos.SingleSignalResponseProto>?
    ) {
        if(request != null && responseObserver!=null){
            launchOnMain{
                val req = request.toModel()
                val o = acts.updateCell2(req,false)
                responseObserver.onNextAndComplete(SingleSignalResponse.fromRs(o).toProto())
            }
        }
    }

    override fun getDisplayValue(
        request: DocProtos.CellIdProto?,
        responseObserver: StreamObserver<CommonProtos.StrMsgProto>?
    ) {
        if(request != null && responseObserver!=null){
            val cid: CellIdProtoDM = request.toModel()
            val cell: Cell? = sc.getCell(cid)
            val rt = StrMsg(cell?.displayValue ?:"")
            responseObserver.onNextAndComplete(rt.toProto())
        }
    }

    override fun getFormula(
        request: DocProtos.CellIdProto?,
        responseObserver: StreamObserver<CommonProtos.StrMsgProto>?
    ) {
        if(request != null && responseObserver!=null){
            val cid: CellIdProtoDM = request.toModel()
            val cell: Cell? = sc.getCell(cid)
            val rt = StrMsg(cell?.fullFormula ?:"")
            responseObserver.onNextAndComplete(rt.toProto())
        }
    }

    override fun getCellValue(
        request: DocProtos.CellIdProto?,
        responseObserver: StreamObserver<DocProtos.CellValueProto>?
    ) {
        if(request != null && responseObserver!=null){
            val cid: CellIdProtoDM = request.toModel()
            val cell: Cell? = sc.getCell(cid)
            val rt:CellValue = cell?.currentCellValue ?: CellValue.empty
            responseObserver.onNextAndComplete(rt.toProto())
        }
    }

    override fun getCellContent(
        request: DocProtos.CellIdProto?,
        responseObserver: StreamObserver<CellProtos.CellContentProto>?
    ) {
        if(request != null && responseObserver!=null){
            val cid: CellIdProtoDM = request.toModel()
            val cell: Cell? = sc.getCell(cid)
            val rt:CellContent = cell?.content ?: CellContentImp.empty
            responseObserver.onNextAndComplete(rt.toProto())
        }
    }

    override fun copyFrom(
        request: CellProtos.CopyCellRequestProto?,
        responseObserver: StreamObserver<CommonProtos.SingleSignalResponseProto>?
    ) {
        if(request != null && responseObserver!=null){
            launchOnMain{
                val req = request.toModel()
                val rt = acts.copyCell(req)
                responseObserver.onNextAndComplete(SingleSignalResponse.fromRs(rt).toProto())
            }
        }
    }
}
