import com.fasterxml.jackson.annotation.JsonFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.example.CSS_FILE
import org.example.CookieSelectors
import org.example.PageOneSelectors
import org.example.WEBSITE
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.httpRequests.DivResp
import org.example.httpRequests.RequestBodyFormat
import org.example.httpRequests.requestOllama
import org.example.httpRequests.semaphore
import org.example.navegation.ChromeDriverExtension
import org.example.navegation.Scraper
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.sql.Driver
import kotlin.system.measureTimeMillis
import kotlin.test.Test

class CssTest {

    private val prompt = """"
        ###Task###  
        You are a HTML code analyst expert. Given a description of the element and a list of div elements, decide what div would be the parent of the child element and create a css selector to access it using only it's attributes.
        ###Context###  
        A button with the purpose of starting the search after the a location has been selected.  
        ###Div List###  
        1. <div id="__next"> // low – framework-generated wrapper with no direct content meaning.
        2. <div class="_7Mr3YA"> // low – likely a styling wrapper with no semantic meaning.
        3. <div data-testid="page-header-wrapper" class=""> // medium – contains page header, but lacks additional semantics due to empty class attribute.
        4. <div class="FfmyqR e4D1FP jngrXy"> // low – ambiguous class names provide little semantic information.
        5. <div class="vTDE1M"> // low – no discernible semantic meaning in the class name.
        6. <div class="j4pLyK"> // low – similar to above, no clear semantic meaning.
        7. <div data-testid="desktop-dropdown-menu" class="_4DcEqf"> // high – contains a dropdown menu for desktop devices according to test ID.
        8. <div class="tbKdsQ"> // low – no discernible semantic meaning in the class name.
        9. <div class="FfmyqR T99TF6 e4D1FP A5QoPl"> // medium – contains multiple classes, but no clear semantic information without additional context.
        10. <div class="jkemPj"> // low – no discernible semantic meaning in the class name.
        11. <div class="FfmyqR e4D1FP jngrXy"> // low – same as #4, ambiguous class names provide little semantic information.
        12. <div class="vzC9TR FrYDhH REZdEJ" data-testid="search-form"> // high – contains a search form according to test ID, and multiple classes may indicate additional styling or state information.
        13. <div class="_3axGO1"> // low – no discernible semantic meaning in the class name.
        14. <div class=""> // low – empty class attribute provides no semantic information.
        15. <div role="combobox" aria-expanded="false" aria-controls="suggestion-list" class="If79lQ yXXD2G"> // high – serves as a combobox with associated ARIA attributes and potentially additional styling or state info in classes.
        16. <div role="button" class="HxkFDQ aaN4L7" tabindex="0"> // medium – clearly a button due to role attribute, but lacks specific semantic information regarding its purpose beyond being interactive.
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

        val ollamaRequest = RequestBodyFormat(
            "mistral-nemo:latest", prompt, format, false
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

                        if(expected != actual) throw ElementNotFoundByCssSelector(resp.div_css_selector)
                        driver.quit()
                        "success ${resp.div_css_selector}"
                    } catch (e: ElementNotFoundByCssSelector) {
                        "failure ${e.message}"
                    }
                }
            }
        }.awaitAll()

        val endTime = System.currentTimeMillis()


        val successCount = list.count { result -> "success" in result }
        val failureCount = 20 - successCount
        val percentage: Double = ((successCount - failureCount).toDouble() / 20) * 100

        list.forEach { println(it) }
        println("Success: $successCount, Failure: $failureCount")
        println("Percentage: $percentage%")
        println("Duration: ${(endTime - startTime) / 1000.0}s")
    }
}