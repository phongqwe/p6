package com.qxdzbc.p6.translator.partial_text_element_extractor

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.map
import com.qxdzbc.p6.translator.partial_text_element_extractor.text_element.BasicTextElement
import com.qxdzbc.p6.translator.partial_text_element_extractor.text_element.CellRangeElement
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.shouldBe
import test.BaseAppStateTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PartialTextElementTranslatorTest : BaseAppStateTest() {
    lateinit var extractor: PartialTextElementTranslator

    @BeforeTest
    override fun _b() {
        super._b()
        extractor = ts.comp.partialTextElementExtractor()
    }

    @Test
    fun `extract partial range address`() {
        val qRs = extractor.translate("=B1:")
        val all = qRs.component1()!!.allSorted()
        all shouldBe listOf(
            BasicTextElement("=", 0, 0),
            BasicTextElement("B1:", 1, 3),
        )
    }

    @Test
    fun `extract-test cell range element only`() {
        val m = mapOf(
            "=F1(A1+F2(A2)+\"A3\"+11)" +
                    "\n\n" +
                    "+F3()*F4(A1:B9)" to listOf(
                CellRangeElement(
                    cellRangeLabel = "A1",
                    start = 4,
                    stop = 5,
                ),
                CellRangeElement(
                    cellRangeLabel = "A2",
                    start = 10,
                    stop = 11,
                ),
                CellRangeElement(
                    cellRangeLabel = "A1:B9",
                    start = 33,
                    stop = 37,
                ),
            ),
        )

        for ((formula, expectation) in m) {
            val o = extractor.translate(formula).map { it.cellRangeElements }
            assertEquals(Ok(expectation), o, formula)
        }
    }

    @Test
    fun `extract - check all elements`() {
        val m = mapOf(
            "=A1@" to listOf(
                BasicTextElement("=", 0),
                CellRangeElement(
                    cellRangeLabel = "A1",
                    start = 1,
                    stop = 2
                ),
                BasicTextElement("@", 3)
            ),
            "=A1 B2" to listOf(
                BasicTextElement("=", 0..0),
                CellRangeElement(
                    cellRangeLabel = "A1",
                    start = 1,
                    stop = 2,
                ),
                BasicTextElement(
                    "B2",
                    4..5
                ),
            ),
            "=F1()" to listOf(
                BasicTextElement("=", 0..0),
                BasicTextElement("F1", 1..2),
                BasicTextElement("(", 3..3),
                BasicTextElement(")", 4..4),
            ),
            "=1+2+\"q\"" to listOf(
                BasicTextElement("=", 0..0),
                BasicTextElement("1", 1..1),
                BasicTextElement("+", 2..2),
                BasicTextElement("2", 3..3),
                BasicTextElement("+", 4..4),
                BasicTextElement("\"q\"", 5..7),
            ),
            "=1^2" to listOf(
                BasicTextElement("=", 0..0),
                BasicTextElement("1", 1..1),
                BasicTextElement("^", 2..2),
                BasicTextElement("2", 3..3),
            ),
            "=1%2" to listOf(
                BasicTextElement("=", 0..0),
                BasicTextElement("1", 1..1),
                BasicTextElement("%", 2..2),
                BasicTextElement("2", 3..3),
            ),
            "=TRUE && FALSE || 1" to listOf(
                BasicTextElement("=", 0..0),
                BasicTextElement("TRUE", 1..4),
                BasicTextElement("&&", 6..7),
                BasicTextElement("FALSE", 9..13),
                BasicTextElement("||", 15..16),
                BasicTextElement("1", 18..18)
            ),
            "=FX(1,2)" to listOf(
                BasicTextElement("=", 0),
                BasicTextElement("FX", 1..2),
                BasicTextElement("(", 3),
                BasicTextElement("1", 4),
                BasicTextElement(",", 5),
                BasicTextElement("2", 6),
                BasicTextElement(")", 7),
            ),
            "=FX(1>2,!(3+2>3),A3)" to listOf(
                BasicTextElement("=", 0),
                BasicTextElement("FX", 1..2),
                BasicTextElement("(", 3),
                BasicTextElement("1", 4),
                BasicTextElement(">", 5),
                BasicTextElement("2", 6),
                BasicTextElement(",", 7),
                BasicTextElement("!", 8),
                BasicTextElement("(", 9),
                BasicTextElement("3", 10),
                BasicTextElement("+", 11),
                BasicTextElement("2", 12),
                BasicTextElement(">", 13),
                BasicTextElement("3", 14),
                BasicTextElement(")", 15),
                BasicTextElement(",", 16),
                CellRangeElement(cellRangeLabel = "A3", start = (17), (18)),
                BasicTextElement(")", 19),
            )
        )
        for ((i, e) in m.entries.toList().subList(0, 1)) {
            val o = extractor.translate(i).component1()?.allSorted()
            i.asClue {
                o.shouldContainOnly(e)
            }
        }
    }
}
