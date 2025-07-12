package cssSelectorGenerationTest

import kotlinx.serialization.json.Json
import org.example.httpRequests.CssResp
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.OllamaRequestBodyFormat
import org.example.httpRequests.PromptBuilder
import org.jsoup.Jsoup
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CssSelectorGenerationTest {

    private val model = "gemma3-12b-css-general:latest"

    private fun writeInFile(path: String, response: String) {
        val path = Paths.get(path)
        if (!Files.exists(path)) Files.createFile(path)

        Files.write(
            path,
            "$response\n\n".toByteArray(),
            StandardOpenOption.APPEND
        )
    }

    private fun validation(htmlPruned: String, cssResp: CssResp, c: String): String {
        val doc = Jsoup.parse(htmlPruned)

        val actualElements = doc.select(cssResp.new_css_selector)
        val expectedElements = doc.select(c)

        return if (actualElements == expectedElements) "Success" else "Failure"
    }

    @Test
    fun a() {
        `check the success percentage`()
    }

    //@AfterAll
    fun `check the success percentage`() {
        val baseDir = File("src\\test\\kotlin\\cssSelectorGenerationTest")
        val modelFileNamePart = model.replace(":", "-")

        val txtFiles = baseDir.listFiles { file ->
            file.isFile && file.name.endsWith(".txt") && modelFileNamePart in file.name
        } ?: emptyArray()

        txtFiles.forEach { file ->
            val content = file.readText()
            val successCount = Regex("Success").findAll(content).count()
            val failureCount = Regex("Failure").findAll(content).count()

            val total = successCount + failureCount
            val accuracy = (successCount.toDouble() / total) * 100

            println("\nModel test file: ${file.name}")
            println("Successes: $successCount")
            println("Failures: $failureCount")
            println("Accuracy: %.2f%%".format(accuracy))
            println("###############################################")
        }

    }

    @RepeatedTest(5)
    fun `changing attribute values`() {
        val ollamaClient = OllamaHttpClient()
        val promptBuilder = PromptBuilder()
        val element = "<div class='price'>$...</div>"
        val invalidCssSelector = ".price"
        val description = "Div element that holds the price of a product"
        val htmlPruned = """
            <html>
                <body>
                    <div class='product-title'>Working Laptop</div>
                    <div class='product-price'>$999</div>
                    <div class='product-title'>Playstation 5</div>
                    <div class='product-price'>$599</div>
                    <div class='product-title'>Nintendo Switch 2</div>
                    <div class='product-price'>$599</div>
                    <div class='product-title'>Ipad</div>
                    <div class='product-price'>$399</div>
                    <div class='product-title'>Laptop Gamer</div>
                    <div class='product-price'>$2.999</div>
                </body>
            </html>
        """.trimIndent()

        val prompt = promptBuilder
            .populateCssTemplate(element, invalidCssSelector, description, htmlPruned)

        val ollamaRequest = OllamaRequestBodyFormat(
            model,
            prompt,
            ModelAnswerSchemas.cssFormat,
            false
        )
        val response = ollamaClient.request(ollamaRequest)

        val cssResp = Json.Default.decodeFromString<CssResp>(response)

        val verdict = validation(htmlPruned, cssResp, "div.product-price")

        writeInFile(
            "C:\\Projeto de licenciatura\\trivagoBot\\src\\test\\kotlin\\cssSelectorGenerationTest\\${
                model.replace(
                    ":",
                    "-"
                )
            }-changing-attribute-values.txt",
            "$verdict:\n$response"
        )
    }

    @RepeatedTest(5)
    fun `removing existing attribute`() {
        val ollamaClient = OllamaHttpClient()
        val promptBuilder = PromptBuilder()
        val element = "<a href=\"/product/laptop\" data-id=\"laptop\">View Laptops</a>"
        val invalidCssSelector = "a[data-id=\"laptop\"]"
        val description = "Link that navigates to the laptop product page"
        val htmlPruned = """
            <html>
                <body>
                    <nav>
                        <a href="/product/laptop">View Laptops</a>
                        <a href="/product/console">View Consoles</a>
                        <a href="/product/computer">View Computers</a>
                        <a href="/product/tablet">View Tablets</a>
                        <a href="/product/radio">View Radios</a>
                        <a href="/product/phone">View Phones</a>
                    </nav>
                </body>
            </html>
        """.trimIndent()

        val prompt = promptBuilder
            .populateCssTemplate(element, invalidCssSelector, description, htmlPruned)

        val ollamaRequest = OllamaRequestBodyFormat(
            model,
            prompt,
            ModelAnswerSchemas.cssFormat,
            false
        )
        val response = ollamaClient.request(ollamaRequest)

        val cssResp = Json.Default.decodeFromString<CssResp>(response)

        val verdict = validation(htmlPruned, cssResp, "a[href=\"/product/laptop\"]")

        writeInFile(
            "C:\\Projeto de licenciatura\\trivagoBot\\src\\test\\kotlin\\cssSelectorGenerationTest\\${
                model.replace(
                    ":",
                    "-"
                )
            }-removing-existing-attribute.txt",
            "$verdict:\n$response"
        )
    }

    @RepeatedTest(5)
    fun `changing element tags`() {
        val ollamaClient = OllamaHttpClient()
        val promptBuilder = PromptBuilder()
        val element = "<div class=\"product-title\">...</div>"
        val invalidCssSelector = "div.product-title"
        val description = "Css selector that leads to div elements with the name of the products"
        val htmlPruned = """
            <div class="products">
                <h2 class="product-title">Wireless Headphones</h2>
                <p>Enjoy high-quality sound with these wireless headphones. Long battery life and noise cancellation included.</p>
                <h2 class="product-title">Smart Fitness Watch</h2>
                <p>Track your workouts, heart rate, and sleep with this sleek smart fitness watch. Water-resistant and compatible with iOS and Android.</p>
                <h2 class="product-title">4K Action Camera</h2>
                <p>Capture every adventure in stunning 4K resolution. Compact, durable, and perfect for outdoor activities and travel.</p>
            </div>
        """.trimIndent()

        val prompt = promptBuilder
            .populateCssTemplate(element, invalidCssSelector, description, htmlPruned)

        val ollamaRequest = OllamaRequestBodyFormat(
            model,
            prompt,
            ModelAnswerSchemas.cssFormat,
            false
        )
        val response = ollamaClient.request(ollamaRequest)

        val cssResp = Json.Default.decodeFromString<CssResp>(response)

        val verdict = validation(htmlPruned, cssResp, "h2.product-title")

        writeInFile(
            "C:\\Projeto de licenciatura\\trivagoBot\\src\\test\\kotlin\\cssSelectorGenerationTest\\${
                model.replace(
                    ":",
                    "-"
                )
            }-changing-element-tags.txt",
            "$verdict:\n$response"
        )
    }

    @RepeatedTest(5)
    fun `changing element tags interactive`() {
        val ollamaClient = OllamaHttpClient()
        val promptBuilder = PromptBuilder()
        val element = "<button class=\"product-search\"><span>Search</span></button>"
        val invalidCssSelector = "button.product-search"
        val description = "Button to search for products"
        val htmlPruned = """
            <div role="button" class="product-search"><span>Search</span></div>
        """.trimIndent()

        val prompt = promptBuilder
            .populateCssTemplate(element, invalidCssSelector, description, htmlPruned)

        val ollamaRequest = OllamaRequestBodyFormat(
            model,
            prompt,
            ModelAnswerSchemas.cssFormat,
            false
        )
        val response = ollamaClient.request(ollamaRequest)

        val cssResp = Json.Default.decodeFromString<CssResp>(response)

        val verdict = validation(htmlPruned, cssResp, "div.product-search")

        writeInFile(
            "C:\\Projeto de licenciatura\\trivagoBot\\src\\test\\kotlin\\cssSelectorGenerationTest\\${
                model.replace(
                    ":",
                    "-"
                )
            }-changing-element-tags-interactive.txt",
            "$verdict:\n$response"
        )
    }

    @RepeatedTest(5)
    fun `reorganizing element placement`() {
        val ollamaClient = OllamaHttpClient()
        val promptBuilder = PromptBuilder()
        val element = "<span class=\"product-price\">$...</span>"
        val invalidCssSelector = "div.product > span.product-price"
        val description = "Css selector that leads to a span elements with the price of the products"
        val htmlPruned = """
            <span class="product-price">$899</span>
            <div class="product"> 
	            <span class="product-name">Smartphone 9</span> 
            </div>
            <span class="product-price">$799</span>
            <div class="product"> 
	            <span class="product-name">Smartphone 8</span> 
            </div>
            <span class="product-price">$699</span>
            <div class="product"> 
	            <span class="product-name">Smartphone 7</span> 
            </div>
            <span class="product-price">$999</span>
            <div class="product"> 
	            <span class="product-name">Smartphone X</span> 
            </div>
        """.trimIndent()

        val prompt = promptBuilder
            .populateCssTemplate(element, invalidCssSelector, description, htmlPruned)

        val ollamaRequest = OllamaRequestBodyFormat(
            model,
            prompt,
            ModelAnswerSchemas.cssFormat,
            false
        )
        val response = ollamaClient.request(ollamaRequest)

        val cssResp = Json.Default.decodeFromString<CssResp>(response)

        val verdict = validation(htmlPruned, cssResp, "span.product-price")

        writeInFile(
            "C:\\Projeto de licenciatura\\trivagoBot\\src\\test\\kotlin\\cssSelectorGenerationTest\\${
                model.replace(
                    ":",
                    "-"
                )
            }-reorganizing-element-placement.txt",
            "$verdict:\n$response"
        )
    }

}