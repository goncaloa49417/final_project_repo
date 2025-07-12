import kotlinx.serialization.json.Json
import org.example.CSS_FILE
import org.example.ProjectFileManager
import org.example.formatHtml
import org.example.httpRequests.CssResp
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.PromptBuilder
import org.example.httpRequests.OllamaRequestBody
import org.example.httpRequests.OllamaRequestBodyFormat
import org.example.navigation.ChromeDriverExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openqa.selenium.By


class Test() {

    @Test
    fun test() {
        val driver: ChromeDriverExtension = ChromeDriverExtension(null)
        val projectFileManager = ProjectFileManager(CSS_FILE)
        val website = projectFileManager.getPathFromFile()

        try {
            driver.get(website)
            val size =
                driver
                    .waitForElementsByCssSelector(
                        "nav._qwert a"
                    ).size

            assertEquals(5, size)

        } finally {
            driver.quit()
        }
    }

    @Test
    fun test2() {
        val driver: ChromeDriverExtension = ChromeDriverExtension(null)
        val ollamaHttpClient = OllamaHttpClient()
        val projectFileManager = ProjectFileManager(CSS_FILE)
        val website = projectFileManager.getPathFromFile()
        driver.get(website)

        val body = driver.findElement(By.tagName("body")).getDomProperty("outerHTML")
            ?: throw Exception("Couldn't find page source")

        val htmlContent = formatHtml(body)

        driver.quit()

        val promptBuilder = PromptBuilder()

        val prompt1 = promptBuilder.populatePruningTemplate(htmlContent)

        println(prompt1)

        val ollamaRequest = OllamaRequestBody("gemma3-pruning", prompt1, false)
        val response = ollamaHttpClient.request(ollamaRequest)

        println(response)
    }

    @Test
    fun test3() {
        val ollamaHttpClient = OllamaHttpClient()
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

        val prompt = promptBuilder
            .populateCssTemplate(element, invalidCssSelector, description, htmlPruned)

        println(prompt)
        val ollamaRequest = OllamaRequestBodyFormat("mistral-nemo:latest", prompt, ModelAnswerSchemas.cssFormat, false)
        val response = ollamaHttpClient.request(ollamaRequest)

        val cssResp = Json.decodeFromString<CssResp>(response)
        println(response)
        println(cssResp)
    }
}