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
import org.example.divSplitter
import org.example.httpRequests.DivResp
import org.example.navegation.ChromeDriverExtension
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


class SemanticDivTest {

    private val prompt = """
        Rate the following <div> elements according to their **semantic level** and provide a brief **explanation of their purpose**, if discernible.
        Use this rating scale:
        - **Low**: No semantic information (e.g., random class names or IDs like __next, container, or alphanumeric strings).
        - **Medium**: Some semantic hints (e.g., data-testid or meaningful but ambiguous class names).
        - **High**: Clear intent (e.g., role, aria attributes, or descriptive test IDs or IDs).  
        **Output Format**:
        1. <div ...> // [rating] – [short explanation]
        Stick strictly to this format. Do not add additional text outside the list.
        ---
        Here are examples:
        1. <div class="_ioPl3_"> // low – likely a styling wrapper with no semantic meaning.
        2. <div id="__next"> // low – framework-generated wrapper with no direct content meaning.
        3. <div role="button"> // medium – probably an interactive element like a custom button.
        4. <div data-testid="login-form" class="_4DcEqf"> // high – likely contains a login form.
        ---
        Now rate the following:
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
    """.trimIndent()

    private val testSemaphore = Semaphore(5)

    @Test
    fun `Semantic div of target HTML element`() = runBlocking {
        val driver = ChromeDriverExtension(null)

        driver.get(WEBSITE)
        val divList = divSplitter(driver)
        driver.quit()
        divList.forEach {
            println(it)
        }
    }

}