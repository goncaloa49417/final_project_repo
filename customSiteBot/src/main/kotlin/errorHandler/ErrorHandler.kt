package org.example.errorHandler

import kotlinx.serialization.json.Json
import org.example.CssCase
import org.example.FileManager
import org.example.httpRequests.CssResp
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.PromptBuilder
import org.example.httpRequests.OllamaRequestBody
import org.example.httpRequests.OllamaRequestBodyFormat
import org.example.httpRequests.requestOllama

fun errorHandler(
    e: ElementNotFoundByCssSelector,
    fileManager: FileManager,
    promptBuilder: PromptBuilder,
    pageBody: String
) {
    //val pruningPrompt = promptBuilder.populatePruningTemplate(pageBody)
    //val prunedPageBody = requestPruningModel(pruningPrompt, promptBuilder)

    val invalidCssSelector = e.message ?: throw Exception("Css selector not found")
    val cssCase = fileManager.extractCssCase(invalidCssSelector)

    val cssFixPrompt = promptBuilder
        .populateCssTemplate(cssCase.element, invalidCssSelector, cssCase.description, pageBody)

    val cssResp = requestCssFixModel(cssFixPrompt, promptBuilder)

    fileManager.editCssFile(
        invalidCssSelector,
        CssCase(
            cssResp.new_element,
            cssResp.new_css_selector,
            cssResp.new_description
        )
    )
}

private fun requestPruningModel(prompt: String, promptBuilder: PromptBuilder): String {
    val request = OllamaRequestBody("mistral-nemo-prunning:latest", prompt, false)
    return requestOllama(request)
}

private fun requestCssFixModel(prompt: String, promptBuilder: PromptBuilder): CssResp {
    val request = OllamaRequestBodyFormat("mistral-nemo-css:latest", prompt, ModelAnswerSchemas.cssFormat, false)
    val response = requestOllama(request)

    return Json.decodeFromString<CssResp>(response)
}