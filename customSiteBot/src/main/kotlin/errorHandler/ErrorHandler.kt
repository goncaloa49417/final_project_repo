package org.example.errorHandler

import kotlinx.serialization.json.Json
import org.example.CssCase
import org.example.FileManager
import org.example.httpRequests.CssResp
import org.example.httpRequests.HttpClient
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.PromptBuilder
import org.example.httpRequests.OllamaRequestBody
import org.example.httpRequests.OllamaRequestBodyFormat


class ErrorHandler(
    val projectFileManager: FileManager,
    val ollamaClient: HttpClient,
    val promptBuilder: PromptBuilder
) {

    fun errorHandler(
        e: ElementNotFoundByCssSelector,
        cssCase: CssCase,
        pageBody: String
    ) {
        val cssFixPrompt = promptBuilder
            .populateCssTemplate(cssCase.element, cssCase.cssSelector, cssCase.description, pageBody)

        val cssResp = requestCssFixModel(cssFixPrompt, ollamaClient)

        projectFileManager.editCssFile(
            e.invalidCssSelector,
            CssCase(
                cssResp.new_element,
                cssResp.new_css_selector,
                cssResp.new_description,
                cssCase.failureCount + 1,
            )
        )
    }

    private fun requestPruningModel(prompt: String, ollamaClient: HttpClient): String {
        val request = OllamaRequestBody("mistral-nemo-prunning:latest", prompt, false)
        return ollamaClient.request(request)
    }

    private fun requestCssFixModel(prompt: String, ollamaClient: HttpClient): CssResp {
        val request = OllamaRequestBodyFormat("gemma3-css:latest", prompt, ModelAnswerSchemas.cssFormat, false)
        val response = ollamaClient.request(request)

        return Json.decodeFromString<CssResp>(response)
    }
}