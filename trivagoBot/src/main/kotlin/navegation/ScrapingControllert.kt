package org.example.navegation

import org.example.CssSelectors
import org.example.FileManager
import org.example.WEBSITE
import org.example.divSplitter
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.errorHandler
import org.example.httpRequests.PromptBuilder
import org.example.httpRequests.askModel
import org.http4k.lens.httpBodyRoot
import org.openqa.selenium.TimeoutException

fun scrapingController(scraper: Scraper, fileManager: FileManager, promptBuilder: PromptBuilder) {
    repeat(3) {
        val driver: ChromeDriverExtension = ChromeDriverExtension(null)
        val cssSelectors = fileManager.extractCssSelectors()

        try {
            driver.get(WEBSITE)
            scraper.selectCookies(driver, cssSelectors.cookies)
            scraper.navigatePage1(driver, cssSelectors.pageOne)
            scraper.navigatePage2(driver, cssSelectors.pageTwo)
            driver.quit()
            return
        } catch (e: ElementNotFoundByCssSelector) {
            val divList = divSplitter(driver)
            errorHandler(e, fileManager, promptBuilder, divList)
        }
        driver.quit()
    }
}