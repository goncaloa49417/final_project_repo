package org.example.errorHandler

import kotlinx.serialization.json.Json
import org.example.CssCase
import org.example.FileManager
import org.example.httpRequests.CssResp
import org.example.httpRequests.DivResp
import org.example.httpRequests.DivRespFinal
import org.example.httpRequests.HttpClient
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.OllamaChatRequest
import org.example.httpRequests.OllamaRequestBody
import org.example.httpRequests.OllamaRequestBodyFormat
import org.example.httpRequests.PreparedConversations
import org.example.httpRequests.PromptBuilder


class ErrorHandler(
    val projectFileManager: FileManager,
    val ollamaClient: HttpClient,
    val promptBuilder: PromptBuilder
) {

    fun getCssSelectorParentDiv(cssCase: CssCase, divList: List<String>): String {
        val chunkedDivList = divList.chunked(20)
        chunkedDivList.forEachIndexed { idx, it ->
            println("List ${idx+1}")
            it.forEach { div ->
                println(div)
            }
            println()
        }
        val responseList1 = requestSemanticDivList(chunkedDivList, promptBuilder, ollamaClient)

        println("Semantic:\n")
        responseList1.forEachIndexed { idx, it ->
            println("List ${idx+1}")
            println("$it\n")
        }

        val responseList2 =
            requestPossibleParentDivList(responseList1, cssCase.description, promptBuilder, ollamaClient)

        println("\nChosen Divs")
        responseList2.forEachIndexed { idx, response ->
            println("List ${idx+1}")
            println("$response\n")
        }

        val prompt = promptBuilder.populateParentDivSearchFinal(responseList2, cssCase.description)
        println("\nChosen Div")

        return requestDivCssSelector(prompt, ollamaClient)
    }

    fun generateNewCssSelector(
        e: ElementNotFoundByCssSelector,
        cssCase: CssCase,
        pageBody: String
    ) {
        val pruningPrompt = promptBuilder.populatePruningTemplate(pageBody)
        val prunedPageBody = requestPruningModel(pruningPrompt, ollamaClient)

        println("\nPruned HTML:\n")
        println(prunedPageBody)

        val cssFixPrompt = promptBuilder
            .populateCssTemplate(
                cssCase.element,
                cssCase.cssSelector,
                cssCase.description,
                prunedPageBody
            )

        val cssResp = requestCssFixModel(cssFixPrompt, ollamaClient)

        println("New element: ${cssResp.new_element}")
        println("New CSS selector: ${cssResp.new_css_selector}")
        println("New description: ${cssResp.new_description}")

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

    private fun requestSemanticDivList(
        chunkedDivList: List<List<String>>,
        promptBuilder: PromptBuilder,
        ollamaClient: HttpClient
    ) =
        chunkedDivList.map { divList ->
            val prompt = promptBuilder.populateDivClassificationTemplate(divList)
            val ollamaChatRequest =
                OllamaChatRequest
                    .build("llama3.2:3b-instruct-fp16", PreparedConversations.semanticDivMessages, prompt)

            ollamaClient.request(ollamaChatRequest)
        }

    private fun requestPossibleParentDivList(
        responseList: List<String>,
        description: String,
        promptBuilder: PromptBuilder,
        ollamaClient: HttpClient
    ) =
        responseList.map { newDivList ->
            val prompt = promptBuilder.populateParentDivSearch(newDivList, description)

            val ollamaRequest = OllamaRequestBodyFormat(
                "gemma3:12b", prompt, ModelAnswerSchemas.divSearchFormat, false
            )

            val response = ollamaClient.request(ollamaRequest)
            val resp = Json.decodeFromString<DivResp>(response)

            resp.div_element
        }

    private fun requestDivCssSelector(prompt: String, ollamaClient: HttpClient): String {
        val ollamaRequest = OllamaRequestBodyFormat(
            "gemma3:12b", prompt, ModelAnswerSchemas.divSearchFormatFinal, false
        )

        val response = ollamaClient.request(ollamaRequest)
        val resp = Json.decodeFromString<DivRespFinal>(response)

        println("${resp.div_element} - ${resp.div_css_selector}")
        return resp.div_css_selector
    }

    private fun requestPruningModel(prompt: String, ollamaClient: HttpClient): String {
        val request = OllamaRequestBody("mistral-nemo-prunning:latest", prompt, false)
        return ollamaClient.request(request)
    }

    private fun requestCssFixModel(prompt: String, ollamaClient: HttpClient): CssResp {
        val request = OllamaRequestBodyFormat("gemma3-12b-css-trivago:latest", prompt, ModelAnswerSchemas.cssFormat, false)
        val response = ollamaClient.request(request)

        return Json.decodeFromString<CssResp>(response)
    }
}