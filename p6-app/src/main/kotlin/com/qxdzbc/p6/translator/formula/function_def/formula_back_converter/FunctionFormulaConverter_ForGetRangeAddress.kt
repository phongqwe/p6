package com.qxdzbc.p6.translator.formula.function_def.formula_back_converter

import com.qxdzbc.p6.translator.formula.execution_unit.ExUnit

class FunctionFormulaConverter_ForGetRangeAddress : FunctionFormulaConverter {
    override fun toFormula(u: ExUnit.Func): String? {
        val args = u.args
        if (args.size == 3) {
            val a1 = args[0]
            val a2 = args[1]
            val a3 = args[2]
            if(a1 is ExUnit.WbKeyStUnit && a2 is ExUnit.WsNameStUnit && a3 is ExUnit.RangeAddressUnit){
                val wb:String = a1.toFormula()
                val ws:String = a2.toFormula()
                val range:String = a3.toFormula()
                return range+ws+wb
            }else{
                return null
            }
        } else {
            return null
        }
    }
}
