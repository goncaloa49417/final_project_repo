import io.mockk.mockk
import org.example.WEBSITE
import org.example.divSplitter
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.PromptBuilder
import navigation.ChromeDriverExtension
import org.junit.jupiter.api.Test
import kotlinx.coroutines.runBlocking
import org.example.CSS_FILE
import org.example.CssCase
import org.example.ProjectFileManager
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.ErrorHandler
import org.example.formatHtml
import org.example.httpRequests.OllamaRequestBodyFormat
import org.openqa.selenium.By
import java.nio.file.Paths
import kotlin.text.get


class Test {

    private val driver = ChromeDriverExtension(null)
    private val ollamaClient = OllamaHttpClient()
    private val promptBuilder = PromptBuilder()
    private val projectFileManager = ProjectFileManager(CSS_FILE)
    private val errorHandler = ErrorHandler(projectFileManager, ollamaClient, promptBuilder)

    private val cssCase = CssCase(
        element = "<button type=\"button\" class=\"_3tjlp_\" data-testid=\"search-button-with-loader\"><span class=\"x_o6V4\"><span class=\"c_Rwvc\">Pesquisar</span></span></button>",
        cssSelector =  "button[data-testid='search-button-with-loader']",
        description = "Main search button with loading indicator",
        failureCount = 0
    )

    @Test
    fun test() {
        driver.get(WEBSITE)
        val divList = divSplitter(driver)
        driver.quit()

        val cssSelector = errorHandler.getCssSelectorParentDiv(cssCase, divList)
        println(cssSelector)
    }

}


