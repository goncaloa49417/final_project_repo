package org.example

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement


fun formatHtml(html: String): String {
    val document = Jsoup.parse(html)
    document.outputSettings(
        Document.OutputSettings().prettyPrint(true).indentAmount(4).escapeMode(Entities.EscapeMode.xhtml)
    )
    return document.outerHtml()
}