package com.qxdzbc.p6.composite_actions.common_data_structure

import com.qxdzbc.p6.common.proto.ProtoUtils.toModel
import com.qxdzbc.p6.common.proto.ProtoUtils.toProto
import com.qxdzbc.p6.proto.CommonProtos.SingleSignalResponseProto
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.google.protobuf.ByteString
import com.qxdzbc.common.Rse
import com.qxdzbc.common.error.ErrorReport
import com.qxdzbc.p6.rpc.communication.res_req_template.WithErrorReport

open class SingleSignalResponse(
    override val errorReport: ErrorReport? = null
) : SingleSignalResponseInterface {
    companion object {
        val ok = SingleSignalResponse()
        fun fromProtoBytes(data: ByteString): SingleSignalResponse {
            val proto = SingleSignalResponseProto.newBuilder().mergeFrom(data).build()
            return SingleSignalResponse(
                errorReport = proto.errorReport.toModel()
            )
        }
        fun fromWithErr(i: WithErrorReport):SingleSignalResponse{
            if(i.errorReport!=null){
                return SingleSignalResponse(i.errorReport)
            }else{
                return ok
            }
        }
        fun fromRs(rs: Rse<Any>): SingleSignalResponse {
            val out = when (rs) {
                is Ok -> {
                    SingleSignalResponse()
                }
                is Err -> {
                    SingleSignalResponse(rs.error)
                }
            }
            return out
        }
    }

    override fun toProto(): SingleSignalResponseProto {
        return SingleSignalResponseProto.newBuilder()
            .apply {
                val e = this@SingleSignalResponse.errorReport
                if(e!=null){
                    setErrorReport(e.toProto())
                }
            }
            .build()
    }
}
