package org.example.errorHandler

import kotlinx.serialization.json.Json
import org.example.CssCase
import org.example.FileManager
import org.example.divSplitter
import org.example.httpRequests.CssResp
import org.example.httpRequests.HttpClient
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.OllamaRequestBody
import org.example.httpRequests.OllamaRequestBodyFormat
import org.example.httpRequests.PromptBuilder
import org.example.navegation.COUNT
import org.openqa.selenium.By.cssSelector
import javax.management.Query.div

class ErrorHandler() {

    fun errorHandler(
        e: ElementNotFoundByCssSelector,
        projectFileManager: FileManager,
        ollamaClient: HttpClient,
        promptBuilder: PromptBuilder,
        pageBody: String
    ) {
        //val pruningPrompt = promptBuilder.populatePruningTemplate(pageBody)
        //val prunedPageBody = requestPruningModel(pruningPrompt, promptBuilder)

        val cssCase = projectFileManager.extractCssCase(e.invalidCssSelector)

        if (cssCase.failureCount >= COUNT)
            throw UnableToGenerateWorkingCssSelector("")

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