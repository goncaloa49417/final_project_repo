package org.example.navegation

import org.example.FileManager
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.ErrorHandler
import org.example.httpRequests.HttpClient
import org.example.httpRequests.PromptBuilder
import org.example.navigation.Scraper
import org.openqa.selenium.By

const val COUNT = 3

fun scrapingController(
    driver: ChromeDriverExtension,
    siteScraper: Scraper,
    projectFileManager: FileManager,
    errorHandler: ErrorHandler,
    ollamaClient: HttpClient,
    promptBuilder: PromptBuilder
) {
    while(true) {
        val cssSelectors = projectFileManager.extractCssSelectors()

        try {
            driver.get(projectFileManager.filePathToSite)
            val num = siteScraper.getCategoryNavs(driver, cssSelectors.categories)
            siteScraper.extractLoop(driver, num, cssSelectors)
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