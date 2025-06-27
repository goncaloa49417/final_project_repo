package org.example.navegation

import org.example.FileManager
import org.example.WEBSITE
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.ErrorHandler
import org.example.httpRequests.HttpClient
import org.example.httpRequests.PromptBuilder
import org.openqa.selenium.By


const val COUNT = 3

fun scrapingController(
    driver: ChromeDriverExtension,
    scraper: Scraper,
    projectFileManager: FileManager,
    errorHandler: ErrorHandler,
    ollamaClient: HttpClient,
    promptBuilder: PromptBuilder
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

            val pageBody =
                driver.findElement(By.tagName("body")).getDomProperty("outerHTML")
                    ?: throw Exception("Couldn't find page source")

            errorHandler.errorHandler(e, projectFileManager, ollamaClient, promptBuilder, pageBody)
        }
    }
}