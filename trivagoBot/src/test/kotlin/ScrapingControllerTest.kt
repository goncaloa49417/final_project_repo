import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifyAll
import org.example.CssCase
import org.example.CssSelectors
import org.example.ProjectFileManager
import org.example.RequiredInformation
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.ErrorHandler
import org.example.errorHandler.UnableToGenerateWorkingCssSelector
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.PromptBuilder
import org.example.navegation.ChromeDriverExtension
import org.example.navegation.SiteScraper
import org.example.navegation.scrapingController
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class ScrapingControllerTest {

    val driver = mockk<ChromeDriverExtension>()
    val siteScraper = mockk<SiteScraper>()
    val projectFileManager = mockk<ProjectFileManager>()
    val ollamaClient = mockk<OllamaHttpClient>()
    val element = mockk<WebElement>()
    val errorHandler = mockk<ErrorHandler>()
    val promptBuilder = PromptBuilder()//mockk<PromptBuilder>()

    @Test
    fun `checks loop in case of failure`() {
        val cssSelectors = CssSelectors(
            "div.categories",
            RequiredInformation(
                "div.name",
                "div.price"
            )
        )

        every { projectFileManager.extractCssSelectors() } returns cssSelectors

        every {
            siteScraper.getCategoryNavs(driver, eq(cssSelectors.categories))
        } throws ElementNotFoundByCssSelector(cssSelectors.categories)

        every {
            errorHandler.errorHandler(
                any(),
                any(),
                any(),
                any() ,
                any()
            )
        } throws UnableToGenerateWorkingCssSelector("")

        every { projectFileManager.filePathToSite } returns "path"

        every { driver.get(any()) } just Runs

        every { driver.findElement(By.tagName("body")) } returns element

        every { element.getDomProperty("outerHTML") } returns "<body>"

        assertThrows<UnableToGenerateWorkingCssSelector> {
            scrapingController(
                driver,
                siteScraper,
                projectFileManager,
                errorHandler,
                ollamaClient,
                promptBuilder
            )
        }

        verifyAll {
            projectFileManager.filePathToSite
            driver.get("path")
            projectFileManager.extractCssSelectors()
            driver.findElement(By.tagName("body"))
            siteScraper.getCategoryNavs(driver, eq(cssSelectors.categories))
        }
    }

    @Test
    fun `checks loop in case of success`() {
        val cssSelectors = CssSelectors(
            "div.categories",
            RequiredInformation(
                "div.name",
                "div.price"
            )
        )

        every { projectFileManager.extractCssSelectors() } returns cssSelectors

        every {
            siteScraper.getCategoryNavs(driver, eq(cssSelectors.categories))
        } returns 4

        every { siteScraper.extractLoop(driver, 4, cssSelectors) } just Runs

        every {
            errorHandler.errorHandler(
                any(),
                any(),
                any(),
                any() ,
                any()
            )
        } just Runs

        every { projectFileManager.filePathToSite } returns "path"

        every { driver.get(any()) } just Runs

        every { projectFileManager.resetAllFailureCounters() } just Runs

        scrapingController(
            driver,
            siteScraper,
            projectFileManager,
            errorHandler,
            ollamaClient,
            promptBuilder
        )

        verifyAll {
            projectFileManager.filePathToSite
            driver.get("path")
            projectFileManager.extractCssSelectors()
            siteScraper.getCategoryNavs(driver, eq(cssSelectors.categories))
            siteScraper.extractLoop(driver, 4, cssSelectors)
            projectFileManager.resetAllFailureCounters()
        }
    }
}