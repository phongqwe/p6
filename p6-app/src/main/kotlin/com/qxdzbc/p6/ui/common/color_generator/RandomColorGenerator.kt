package com.qxdzbc.p6.ui.common.color_generator

import androidx.compose.ui.graphics.Color
import java.util.*
import javax.inject.Inject

/**
 * Generate color randomly
 */
class RandomColorGenerator constructor(
    private val rand:Random
) : ColorGenerator {

    @Inject constructor():this(Random(22L))

    override fun nextColor(): Color {
//        val r: Float = rand.nextFloat() / 2f + 0.5f
//        val g: Float = rand.nextFloat() / 2f + 0.5f
//        val b: Float = rand.nextFloat() / 2f + 0.5f
        val r: Float = rand.nextFloat()
        val g: Float = rand.nextFloat()
        val b: Float = rand.nextFloat()
        return Color(r,g,b)
    }
}
