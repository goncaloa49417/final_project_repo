package org.example

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver


fun formatHtml(html: String): String {
    val document = Jsoup.parse(html)
    document.outputSettings(
        Document.OutputSettings().prettyPrint(true).indentAmount(4).escapeMode(Entities.EscapeMode.xhtml)
    )
    return document.outerHtml()
}

fun divSplitter(driver: WebDriver): List<String> {
    val divList = driver.findElements(By.tagName("div")).map { it.getDomProperty("outerHTML") ?: "NULL" }
    val list = divList.map { it.substringBefore(">") + ">" }.filter { it != "<div>" }
    return list.mapIndexed { i, div -> "${i + 1}. $div" }
}