package org.example

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities


fun formatHtml(html: String): String {
    val document = Jsoup.parse(html)
    document.outputSettings(
        Document.OutputSettings().prettyPrint(true).indentAmount(4).escapeMode(Entities.EscapeMode.xhtml)
    )
    return document.outerHtml()
}