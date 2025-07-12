package org.example

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

const val CONTEXT_WINDOW = 8192


fun formatHtml(html: String): String {
    val document = Jsoup.parse(html)
    document.outputSettings(
        Document.OutputSettings().prettyPrint(true).indentAmount(4).escapeMode(Entities.EscapeMode.xhtml)
    )
    return document.outerHtml()
}

fun htmlSplitter(driver: WebDriver): List<String> {
    val parentElement = driver.findElement(By.tagName("body"))
    val webPage = parentElement.getDomProperty("outerHTML") ?: "NULL"

    val elList = listOf(webPage)
    val webElList = listOf<WebElement>(parentElement)
    return split(webElList, elList)
}

fun split(webElList: List<WebElement>, elList: List<String>): List<String> {
    for (i in elList.indices) {
        if (elList[i].length > CONTEXT_WINDOW) {
            val newChildren = webElList[i].findElements(By.cssSelector(":scope > *"))
            val updatedWebList = webElList.take(i) + newChildren + webElList.drop(i + 1)
            val newElList = newChildren.map { it.getDomProperty("outerHTML") ?: "NULL" }
            val updatedElList = elList.take(i) + newElList + elList.drop(i + 1)

            return split(updatedWebList, updatedElList)
        }
    }
    return elList
}

fun divSplitter(driver: WebDriver): List<String> {
    val divList = driver.findElements(By.tagName("div")).map { it.getDomProperty("outerHTML") ?: "NULL" }
    val list = divList.map { it.substringBefore(">") + ">" }.filter { it != "<div>" }
    return list.mapIndexed { i, div -> "${i + 1}. $div" }
}