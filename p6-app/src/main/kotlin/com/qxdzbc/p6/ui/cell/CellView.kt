package com.qxdzbc.p6.ui.cell

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.qxdzbc.common.compose.StateUtils.toMs
import com.qxdzbc.common.compose.view.MBox
import com.qxdzbc.common.compose.view.testApp
import com.qxdzbc.p6.document_data_layer.cell.Cell
import com.qxdzbc.p6.document_data_layer.cell.IndCellImp
import com.qxdzbc.p6.document_data_layer.cell.address.CellAddress

import com.qxdzbc.p6.ui.cell.state.CellState
import com.qxdzbc.p6.ui.cell.state.CellStateImp
import com.qxdzbc.p6.ui.cell.state.format.text.CellFormat
import com.qxdzbc.p6.ui.cell.state.format.text.CellFormatImp
import com.qxdzbc.p6.ui.worksheet.WorksheetConstants

/**
 * View of a cell. This view by default will fill max size. So it size is decided by the caller.
 */
@Composable
fun CellView(
    state: CellState,
    format: CellFormat,
    boxModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
) {
    val cell: Cell? = state.cell
    MBox(
        modifier = boxModifier
            .fillMaxSize()
            .let { mod ->
                format.cellModifier?.let {
                    mod.then(it)
                } ?: mod
            }

    ) {
        if (cell != null) {

            Text(
                cell.cachedDisplayText,
                modifier = textModifier.align(format.textAlignment),
                style = format.toTextStyle(),
            )
        }
    }
}

fun main() = testApp {
    val address = CellAddress(1, 1)
    Column {

        Box(modifier = Modifier.size(WorksheetConstants.defaultCellSize).border(1.dp, Color.Black)) {
            CellView(
                CellStateImp(
                    address = address,
                    cellMs = IndCellImp.random(address).toMs()
                ),
                format= CellFormatImp()
            )
        }


        Box(modifier = Modifier.size(WorksheetConstants.defaultCellSize).border(1.dp, Color.Black)) {
            CellView(
                CellStateImp(
                    address = address,
                    cellMs = IndCellImp.random(address).toMs(),
                ),
                format= CellFormatImp()
            )
        }
    }
}
