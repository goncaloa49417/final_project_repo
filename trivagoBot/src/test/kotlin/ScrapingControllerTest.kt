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
import navigation.ChromeDriverExtension
import navigation.SiteScraper
import navigation.scrapingController
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.openqa.selenium.WebElement


class ScrapingControllerTest {

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
            siteScraper.selectCookies(any(), eq(cssSelectors.cookies))
        } throws ElementNotFoundByCssSelector(cssSelectors.cookies.denyCookies)

        assertThrows<UnableToGenerateWorkingCssSelector> {
            scrapingController(
                siteScraper,
                projectFileManager,
                errorHandler
            )
        }

        verifyAll {
            projectFileManager.extractCssSelectors()
            projectFileManager.extractCssCase("#deny-button")
            siteScraper.selectCookies(any(), eq(cssSelectors.cookies))
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
            siteScraper.selectCookies(any(), eq(cssSelectors.cookies))
        } just Runs

        every { siteScraper.navigatePage1(any(), cssSelectors.pageOne) } just Runs

        every { siteScraper.navigatePage2(any(), cssSelectors.pageTwo) } just Runs

        every { projectFileManager.resetAllFailureCounters() } just Runs

        scrapingController(
            siteScraper,
            projectFileManager,
            errorHandler
        )

        verifyAll {
            projectFileManager.extractCssSelectors()
            siteScraper.selectCookies(any(), eq(cssSelectors.cookies))
            siteScraper.navigatePage1(any(), cssSelectors.pageOne)
            siteScraper.navigatePage2(any(), cssSelectors.pageTwo)
            projectFileManager.resetAllFailureCounters()
        }
    }
}