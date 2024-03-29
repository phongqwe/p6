package com.qxdzbc.p6.translator.formula.function_def

/**
 * Contain the default and internal function/formula definitions in this app.
 */
interface P6FunctionDefinitions {

    companion object {
        val getWbRs = "__getWbRs"
        /**
         * name of internal function for getting sheets
         */
        val getSheetRs = "__getSheetRs"
        /**
         * name of internal function for getting ranges
         */
        val getRangeRs = "__getRangeRs"
        /**
         * name of internal function for getting cells
         */
        val getCellRs = "__getCellRs"
    }
    val functionMap: Map<String, FunctionDef>
}
