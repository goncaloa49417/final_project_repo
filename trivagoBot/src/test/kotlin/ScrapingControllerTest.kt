import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifyAll
import org.example.CookieSelectors
import org.example.CssCase
import org.example.CssSelectors
import org.example.PageOneSelectors
import org.example.PageTwoSelectors
import org.example.ProjectFileManager
import org.example.WEBSITE
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.ErrorHandler
import org.example.errorHandler.UnableToGenerateWorkingCssSelector
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.PromptBuilder
import navigation.ChromeDriverExtension
import navigation.SiteScraper
import navigation.scrapingController
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.openqa.selenium.WebElement


class ScrapingControllerTest {

    val driver = mockk<ChromeDriverExtension>()
    val siteScraper = mockk<SiteScraper>()
    val projectFileManager = mockk<ProjectFileManager>()
    val element = mockk<WebElement>()
    val errorHandler = mockk<ErrorHandler>()

    @Test
    fun `checks loop in case of failure`() {
        val cssSelectors = CssSelectors(
            CookieSelectors("#root", "#deny-button"),
            PageOneSelectors("#location-box", "#confirm-selector", "#date-select", "#search-button"),
            PageTwoSelectors("#last-page-number", "#names-selector", "#prices-selector", "#next-page-selector")
        )

        every { projectFileManager.extractCssSelectors() } returns cssSelectors

        every { projectFileManager.extractCssCase("#deny-button") } returns
                CssCase("#deny-button", "element", "an element", 3)

        every {
            siteScraper.selectCookies(driver, eq(cssSelectors.cookies))
        } throws ElementNotFoundByCssSelector(cssSelectors.cookies.denyCookies)

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
            driver.get(WEBSITE)
            projectFileManager.extractCssSelectors()
            projectFileManager.extractCssCase("#deny-button")
            siteScraper.selectCookies(driver, eq(cssSelectors.cookies))
        }
    }

    @Test
    fun `checks loop in case of success`() {
        val cssSelectors = CssSelectors(
            CookieSelectors("#root", "#deny-button"),
            PageOneSelectors("#location-box", "#confirm-selector", "#date-select", "#search-button"),
            PageTwoSelectors("#last-page-number", "#names-selector", "#prices-selector", "#next-page-selector")
        )

        every { projectFileManager.extractCssSelectors() } returns cssSelectors

        every {
            siteScraper.selectCookies(driver, eq(cssSelectors.cookies))
        } just Runs

        every { siteScraper.navigatePage1(driver, cssSelectors.pageOne) } just Runs

        every { siteScraper.navigatePage2(driver, cssSelectors.pageTwo) } just Runs

        every { driver.get(any()) } just Runs

        every { projectFileManager.resetAllFailureCounters() } just Runs

        scrapingController(
            driver,
            siteScraper,
            projectFileManager,
            errorHandler
        )

        verifyAll {
            driver.get(WEBSITE)
            projectFileManager.extractCssSelectors()
            siteScraper.selectCookies(driver, eq(cssSelectors.cookies))
            siteScraper.navigatePage1(driver, cssSelectors.pageOne)
            siteScraper.navigatePage2(driver, cssSelectors.pageTwo)
            projectFileManager.resetAllFailureCounters()
        }
    }
}