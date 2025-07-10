package navigation

import org.example.FileManager
import org.example.WEBSITE
import org.example.divSplitter
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.ErrorHandler
import org.example.errorHandler.UnableToGenerateWorkingCssSelector
import org.example.formatHtml
import org.openqa.selenium.By


const val COUNT = 3

fun scrapingController(
    driver: ChromeDriverExtension,
    scraper: Scraper,
    projectFileManager: FileManager,
    errorHandler: ErrorHandler
) {
    while(true) {
        val cssSelectors = projectFileManager.extractCssSelectors()

        try {
            driver.get(WEBSITE)
            scraper.selectCookies(driver, cssSelectors.cookies)
            scraper.navigatePage1(driver, cssSelectors.pageOne)
            scraper.navigatePage2(driver, cssSelectors.pageTwo)
            projectFileManager.resetAllFailureCounters()
            return
        } catch (e: ElementNotFoundByCssSelector) {
            println(e.invalidCssSelector)

            val cssCase = projectFileManager.extractCssCase(e.invalidCssSelector)
            if (cssCase.failureCount >= COUNT)
                throw UnableToGenerateWorkingCssSelector("")

            val divList = divSplitter(driver)
            val divCssSelector = errorHandler.getCssSelectorParentDiv(cssCase, divList)
            val divHtml = formatHtml(
                driver.findElement(By.cssSelector(divCssSelector)).getDomProperty("outerHTML")
                    ?: throw Exception("Couldn't find page source")
            )

            errorHandler.generateNewCssSelector(e, cssCase, divHtml)
        }
    }
}