package com.qxdzbc.p6.ui.format

import androidx.compose.ui.graphics.Color
import com.qxdzbc.common.flyweight.FlyweightTable
import com.qxdzbc.common.flyweight.FlyweightTableImp
import com.qxdzbc.p6.di.anvil.P6AnvilScope
import com.qxdzbc.p6.ui.document.cell.state.format.text.TextHorizontalAlignment
import com.qxdzbc.p6.ui.document.cell.state.format.text.TextVerticalAlignment
import com.qxdzbc.p6.ui.format.attr.BoolAttr
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(scope = P6AnvilScope::class)
data class CellFormatFlyweightTableImp @Inject constructor(
    override val floatTable: FlyweightTable<Float>,
    override val colorTable: FlyweightTable<Color>,
    override val boolTable: FlyweightTable<BoolAttr>,
    override val horizontalAlignmentTable: FlyweightTable<TextHorizontalAlignment>,
    override val verticalAlignmentTable: FlyweightTable<TextVerticalAlignment>,
) : CellFormatFlyweightTable {

    constructor() : this(
        FlyweightTableImp(),
        FlyweightTableImp(),
        FlyweightTableImp(),
        FlyweightTableImp(),
        FlyweightTableImp()
    )

    override fun updateFloatTable(i: FlyweightTable<Float>): CellFormatFlyweightTableImp {
        return this.copy(floatTable = i)
    }

    override fun updateColorTable(i: FlyweightTable<Color>): CellFormatFlyweightTableImp {
        return this.copy(colorTable = i)
    }

    override fun updateBoolTable(i: FlyweightTable<BoolAttr>): CellFormatFlyweightTableImp {
        return this.copy(boolTable = i)
    }

    override fun updateVerticalAlignmentTable(i: FlyweightTable<TextVerticalAlignment>): CellFormatFlyweightTableImp {
        return this.copy(verticalAlignmentTable = i)
    }

    override fun updateHorizontalAlignmentTable(i: FlyweightTable<TextHorizontalAlignment>): CellFormatFlyweightTable {
        return this.copy(horizontalAlignmentTable = i)
    }
}