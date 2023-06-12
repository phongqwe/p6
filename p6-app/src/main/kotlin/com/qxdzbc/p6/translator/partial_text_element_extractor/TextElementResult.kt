package com.qxdzbc.p6.translator.partial_text_element_extractor

import com.qxdzbc.p6.translator.partial_text_element_extractor.text_element.*

/**
 * An encapsulation containing [TextElement].
 * The ferry properties ([ferryBasicTextElement], [ferryWsNameElement], [ferryWbElement]) are for internal intermediate actions inside parser visitors. End users should pay them no mind.
 *
 * [TextElementResult] is for generating colored formula both inside cell editor, and for previewing cell's formula.
 */
data class TextElementResult(
    val cellRangeElements: List<CellRangeElement> = emptyList(),
    val basicTexts: List<BasicTextElement> = emptyList(),
    val errs:List<ErrTextElement> = emptyList(),
    val ferryBasicTextElement: BasicTextElement? = null,
    val ferryWsNameElement: WsNameElement? = null,
    val ferryWbElement: WbElement? = null,
) {
    companion object {
        fun from(i: CellRangeElement): TextElementResult {
            return TextElementResult(cellRangeElements = listOf(i))
        }

        fun from(i: BasicTextElement): TextElementResult {
            return TextElementResult(basicTexts = listOf(i))
        }

        fun ferry(i: BasicTextElement): TextElementResult {
            return TextElementResult(ferryBasicTextElement = i)
        }

        fun ferry(i: WsNameElement): TextElementResult {
            return TextElementResult(ferryWsNameElement = i)
        }

        fun ferry(i: WbElement): TextElementResult {
            return TextElementResult(ferryWbElement = i)
        }

        val empty = TextElementResult()
    }

    val all: List<TextElement> get() = cellRangeElements + basicTexts
    val allWithErr: List<TextElement> get() = cellRangeElements + basicTexts +errs

    fun allSorted(): List<TextElement> {
        return all.sortedBy { it.start }
    }

    val allWithErrSorted: List<TextElement> get(){
        return allWithErr.sortedBy { it.start }
    }

    /**
     * scan the sorted list of element, detect elements that are not continuous (stop index of prev element != start index of the next element + 1)
     */
    fun allSortedWithPadding(): List<TextElement> {
        val all = allSorted()
        val rt = mutableListOf<TextElement>()
        for ((i, e) in all.withIndex()) {
            rt.add(e)
            val nextE = all.getOrNull(i + 1)
            if (nextE != null) {
                val nextStart = nextE.start
                val currentStop = e.stop
                val diff = nextStart - currentStop
                if (diff > 1) {
                    rt.add(
                        BasicTextElement(
                            text = " ".repeat(diff - 1),
                            range = (currentStop + 1)..(nextStart - 1)
                        )
                    )
                }

            }
        }
        return rt
    }


    fun mergeWith(other: TextElementResult): TextElementResult {
        return this.copy(
            cellRangeElements = cellRangeElements + other.cellRangeElements,
            basicTexts = basicTexts + other.basicTexts,
            errs = errs + other.errs
        )
    }

    operator fun plus(other: TextElementResult): TextElementResult {
        return this.mergeWith(other)
    }

    operator fun plus(i: CellRangeElement): TextElementResult {
        return this.copy(
            cellRangeElements = cellRangeElements + i
        )
    }

    operator fun plus(i: BasicTextElement): TextElementResult {
        return this.copy(
            basicTexts = basicTexts + i
        )
    }
}
