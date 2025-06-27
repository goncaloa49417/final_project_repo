import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.example.WEBSITE
import org.example.httpRequests.CssResp
import org.example.httpRequests.DivResp
import org.example.httpRequests.PromptBuilder
import org.example.httpRequests.RequestBodyFormat
import org.example.httpRequests.requestOllama
import org.example.navegation.ChromeDriverExtension
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


class ParentDivTest {

    private val prompt = """"
        ###Task###  
        You are a HTML code analyst expert. Given a description of the element and a list of div elements, decide what div would be the parent of the child element and create a css selector to access it using only it's attributes.
        
        ###Context###  
        A button with the purpose of starting the search after the a location has been selected.
          
        ###Div List###  
        1. <div id="__next">
        2. <div class="_7Mr3YA">
        3. <div data-testid="page-header-wrapper" class="">
        4. <div class="FfmyqR e4D1FP jngrXy">
        5. <div class="vTDE1M">
        6. <div class="j4pLyK">
        7. <div data-testid="desktop-dropdown-menu" class="_4DcEqf">
        8. <div class="tbKdsQ">
        9. <div class="FfmyqR T99TF6 e4D1FP A5QoPl">
        10. <div class="jkemPj">
        11. <div class="FfmyqR e4D1FP jngrXy">
        12. <div class="vzC9TR FrYDhH REZdEJ" data-testid="search-form">
        13. <div class="_3axGO1">
        14. <div class="">
        15. <div role="combobox" aria-expanded="false" aria-controls="suggestion-list" class="If79lQ yXXD2G">
        16. <div role="button" class="HxkFDQ aaN4L7" tabindex="0">
        
        ###Answer Format###  
        Div element: (completed chosen div)  
        Css selector: div(complete the css selector with the most unique attribute to be select by a selenium bot)  
        Stick strictly to this format. Do not add additional text outside the answer format.
    """.trimIndent()

    private val testSemaphore = Semaphore(5)

    @Test
    fun `Parent div of target HTML element`() = runBlocking {
        val format = buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("div_element") {
                    put("type", "string")
                }
                putJsonObject("div_css_selector") {
                    put("type", "string")
                }
            }
            putJsonArray("required") {
                add("div_element")
                add("div_css_selector")
            }
        }

        val model = "gemma3:12b"

        val ollamaRequest = RequestBodyFormat(
            model, prompt, format, false
        )

        val startTime = System.currentTimeMillis()

        val list = (1..20).map {
            async {
                testSemaphore.withPermit {
                    try {
                        val response = requestOllama(ollamaRequest)
                        val resp = Json.decodeFromString<DivResp>(response)

                        val driver = ChromeDriverExtension(null)
                        driver.get(WEBSITE)
                        val expected = driver.waitForElementByCssSelector("div[data-testid='search-form']")
                        val actual = driver.waitForElementByCssSelector(resp.div_css_selector)
                        driver.quit()

                        val message = "${resp.div_css_selector}; ${resp.div_element}"

                        if (expected != actual)
                            throw Exception(message)
                        "success $message"
                    } catch (e: Exception) {
                        "failure ${e.message}"
                    }
                }
            }
        }.awaitAll()

        val endTime = System.currentTimeMillis()

        val successCount = list.count { result -> "success" in result }
        val failureCount = 20 - successCount
        val percentage: Double = (successCount * 100).toDouble() / 20

        val path =
            Paths.get("C:\\Projeto de licenciatura\\trivagoBot\\src\\test\\kotlin\\gemma3-12b-without-semantic-1.txt")
        if (!Files.exists(path)) Files.createFile(path)

        list.forEach {
            Files.write(
                path,
                "$it\n".toByteArray(),
                StandardOpenOption.APPEND
            )
        }

        Files.write(
            path,
            (
                    "Success: $successCount, Failure: $failureCount\n" +
                            "Percentage: $percentage%\n" +
                            "Duration: ${(endTime - startTime) / 1000.0}s\n"
                    ).toByteArray(),
            //StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        )

        list.forEach { println(it) }
        println("Success: $successCount, Failure: $failureCount")
        println("Percentage: $percentage%")
        println("Duration: ${(endTime - startTime) / 1000.0}s")

    }

}