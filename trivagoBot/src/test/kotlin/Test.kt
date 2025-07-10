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
import org.example.errorHandler.ErrorHandler


class Test {

    @Test
    fun test() = runBlocking {
        val driver = ChromeDriverExtension(null)
        val ollamaClient = OllamaHttpClient()
        val promptBuilder = PromptBuilder()
        val projectFileManager = ProjectFileManager(CSS_FILE)
        val errorHandler = ErrorHandler(projectFileManager, ollamaClient, promptBuilder)

        driver.get(WEBSITE)
        val divList = divSplitter(driver)
        driver.quit()

        val cssCase = CssCase(
            element = "<button type=\"button\" class=\"_3tjlp_\" data-testid=\"search-button-with-loader\"><span class=\"x_o6V4\"><span class=\"c_Rwvc\">Pesquisar</span></span></button>",
            cssSelector =  "button[data-testid='search-button-with-loader']",
            description = "Main search button with loading indicator",
            failureCount = 0
        )

        val cssSelector = errorHandler.getCssSelectorParentDiv(cssCase, divList)
        println(cssSelector)
    }

}


