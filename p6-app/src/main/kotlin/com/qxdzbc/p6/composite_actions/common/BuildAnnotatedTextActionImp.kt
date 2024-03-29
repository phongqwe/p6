package com.qxdzbc.p6.composite_actions.common

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.qxdzbc.p6.di.P6AnvilScope
import com.qxdzbc.p6.translator.partial_text_element_extractor.text_element.BasicTextElement
import com.qxdzbc.p6.translator.partial_text_element_extractor.text_element.TextElement
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(P6AnvilScope::class)
class BuildAnnotatedTextActionImp @Inject constructor() : BuildAnnotatedTextAction {
    override fun buildAnnotatedText(
        textElements: List<TextElement>,
        spans: List<SpanStyle>
    ): AnnotatedString {
        val sortedTextElements = textElements.sortedBy { it.start }
        val spanElements = textElements.filter { it !is BasicTextElement }
        var prevIndex = 0
        val rt=buildAnnotatedString {
            for ((i, e) in sortedTextElements.withIndex()) {
                if(e.start>prevIndex){
                    val dif = e.start - prevIndex
                    append(" ".repeat(dif))
                }
                when(e){
                    is BasicTextElement ->{
                        append(e.text)
                    }
                    else ->{
                        val spanIndex=spanElements.indexOf(e)
                        spans.getOrNull(spanIndex)?.also { span ->
                            withStyle(style = span) {
                                append(e.text)
                            }
                        } ?: run {
                            append(e.text)
                        }
                    }
                }
                prevIndex = e.stopForSubStr
            }
        }
        return rt
    }
}
