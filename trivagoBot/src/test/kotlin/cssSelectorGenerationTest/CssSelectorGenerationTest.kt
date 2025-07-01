package cssSelectorGenerationTest

import kotlinx.serialization.json.Json
import org.example.httpRequests.CssResp
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.OllamaRequestBodyFormat
import org.example.httpRequests.PromptBuilder
import org.junit.jupiter.api.Test

class CssSelectorGenerationTest {

    @Test
    fun `Css selector generation`() {
        val ollamaClient = OllamaHttpClient()
        val promptBuilder = PromptBuilder()
        val element = "<div class='price'>$2.999</div>"
        val invalidCssSelector = ".price"
        val description = "Div element that holds the price of a product"
        val htmlPruned = """
            <html>
                <body>
                    <div class='product-title'>Laptop Gamer</div>
                    <div class='product-price'>$2.999</div>
                </body>
            </html>
        """.trimIndent()

        val prompt2 = promptBuilder
            .populateCssTemplate(element, invalidCssSelector, description, htmlPruned)

        println(prompt2)
        val ollamaRequest2 = OllamaRequestBodyFormat(
            "mistral-nemo:latest",
            prompt2,
            ModelAnswerSchemas.cssFormat,
            false
        )
        val response2 = ollamaClient.request(ollamaRequest2)

        val cssResp = Json.Default.decodeFromString<CssResp>(response2)
        println(response2)
        println(cssResp)
    }

}