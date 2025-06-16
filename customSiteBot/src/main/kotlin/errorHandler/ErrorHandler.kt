package org.example.errorHandler

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.example.CssCase
import org.example.FileManager
import org.example.divSplitter
import org.example.httpRequests.CssResp
import org.example.httpRequests.DivResp
import org.example.httpRequests.PromptBuilder
import org.example.httpRequests.RequestBody
import org.example.httpRequests.RequestBodyFormat
import org.example.httpRequests.requestOllama
import org.openqa.selenium.By.cssSelector
import javax.management.Query.div

fun errorHandler(
    e: ElementNotFoundByCssSelector,
    fileManager: FileManager,
    promptBuilder: PromptBuilder,
    divList: List<String>,
    pageSource: String
) {
    val prompt1 = promptBuilder.populatePruningTemplate(pageSource)
    val ollamaRequest1 = RequestBody("mistral-nemo:latest", prompt1)
    val response1 = requestOllama(ollamaRequest1)
    val cssSelector = e.message ?: throw Exception("Css selector not found")
    val cssCase = fileManager.extractCssCase(cssSelector)
    val prompt2 = promptBuilder
        .populateCssTemplate(cssCase.element, cssSelector, cssCase.description, response1)
    val ollamaRequest2 = RequestBodyFormat("mistral-nemo-css:latest", prompt2, promptBuilder.cssFormat)
    val response2 = requestOllama(ollamaRequest2)
    val cssResp = Json.decodeFromString<CssResp>(response2).new_css_selector

    fileManager.editCssCase(CssCase("", "cssResp", ""))
}