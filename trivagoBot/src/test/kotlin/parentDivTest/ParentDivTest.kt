package parentDivTest

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
import org.example.httpRequests.DivResp
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.OllamaRequestBodyFormat
import org.example.navegation.ChromeDriverExtension
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


class ParentDivTest {

    private val prompt = """"
        ###Task###  
        You are a HTML code analyst expert. Given a description of the element and a list of div elements each rated by level of semantic, decide what div would be the parent of the child element and create a css selector to access it using only it's attributes.
        
        ###Context###  
        A button with the purpose of starting the search after the a location has been selected.
          
        ###Div List###  
        1. `<div id="__next">` // low – random ID
        2. `<div class="_7Mr3YA">` // low – random class
        3. `<div data-testid="page-header-wrapper" class="">` // medium – descriptive testid, empty classes
        4. `<div class="FfmyqR e4D1FP jngrXy">` // low – random classes
        5. `<div class="vTDE1M">` // low – random class
        6. `<div class="j4pLyK">` // low – random class
        7. `<div data-testid="desktop-dropdown-menu" class="_4DcEqf">` // high – descriptive testid, ARIA attributes
        8. `<div class="tbKdsQ">` // low – random class
        9. `<div class="FfmyqR T99TF6 e4D1FP A5QoPl">` // low – random classes
        10. `<div class="jkemPj">` // low – random class
        11. `<div class="FfmyqR e4D1FP jngrXy">` // low – random classes
        12. `<div class="vzC9TR FrYDhH REZdEJ" data-testid="search-form">` // high – descriptive testid, ARIA attributes
        13. `<div class="_3axGO1">` // low – random class
        14. `<div class="">` // low – empty classes
        15. `<div role="combobox" aria-expanded="false" aria-controls="suggestion-list" class="If79lQ yXXD2G">` // high – explicit ARIA attributes
        16. `<div role="button" class="HxkFDQ aaN4L7" tabindex="0">` // medium – descriptive role, empty classes
        
        ###Answer Format###  
        Div element: (completed chosen div)  
        Css selector: div(complete the css selector with the most unique attribute to be select by a selenium bot)  
        Stick strictly to this format. Do not add additional text outside the answer format.
    """.trimIndent()

    private val testSemaphore = Semaphore(2)

    @Test
    fun `Parent div of target HTML element`() = runBlocking {

        val model = "gemma3:12b"

        val ollamaClient = OllamaHttpClient()

        val ollamaRequest = OllamaRequestBodyFormat(
            model, prompt, ModelAnswerSchemas.divSearchFormat, false
        )

        val startTime = System.currentTimeMillis()

        val list = (1..20).map {
            async {
                testSemaphore.withPermit {
                    try {
                        val response = ollamaClient.request(ollamaRequest)
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
            Paths.get("C:\\Projeto de licenciatura\\trivagoBot\\src\\test\\kotlin\\parentDivTest\\gemma3-12b-with-semantic-1.txt")
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