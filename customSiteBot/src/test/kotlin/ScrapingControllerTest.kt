import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
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
import org.example.navigation.ChromeDriverExtension
import org.example.navigation.SiteScraper
import org.example.navigation.scrapingController
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class ScrapingControllerTest {

    val driver = mockk<ChromeDriverExtension>()
    val siteScraper = mockk<SiteScraper>()
    val projectFileManager = mockk<ProjectFileManager>()
    val errorHandler = mockk<ErrorHandler>()

    @Test
    fun `checks loop in case of failure`() {
        val cssSelectors = CssSelectors(
            "div.categories",
            RequiredInformation(
                "div.name",
                "div.price"
            )
        )

        val cssCase = CssCase(
            "div.categories",
            "element",
            "an element",
            3
        )

        every { projectFileManager.extractCssSelectors() } returns cssSelectors

        every {
            siteScraper.getCategoryNavs(driver, eq(cssSelectors.categories))
        } throws ElementNotFoundByCssSelector(cssSelectors.categories)

        every {
            errorHandler.errorHandler(
                any(),
                any(),
                any()
            )
        } throws UnableToGenerateWorkingCssSelector("")

        every { projectFileManager.filePathToSite } returns "path"

        every { projectFileManager.extractCssCase(any()) } returns cssCase

        every { driver.get(any()) } just Runs

        assertThrows<UnableToGenerateWorkingCssSelector> {
            scrapingController(
                driver,
                siteScraper,
                projectFileManager,
                errorHandler
            )
        }

        verifyAll {
            projectFileManager.filePathToSite
            projectFileManager.extractCssCase(any())
            projectFileManager.extractCssSelectors()
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
            errorHandler
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