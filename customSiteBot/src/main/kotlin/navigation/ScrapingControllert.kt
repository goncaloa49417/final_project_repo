package org.example.navigation

import org.example.FileManager
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.ErrorHandler
import org.example.errorHandler.UnableToGenerateWorkingCssSelector
import org.example.formatHtml
import org.example.navigation.ChromeDriverExtension
import org.example.navigation.Scraper
import org.openqa.selenium.By

const val COUNT = 3

fun scrapingController(
    driver: ChromeDriverExtension,
    siteScraper: Scraper,
    projectFileManager: FileManager,
    errorHandler: ErrorHandler
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

            val cssCase = projectFileManager.extractCssCase(e.invalidCssSelector)
            if (cssCase.failureCount >= COUNT)
                throw UnableToGenerateWorkingCssSelector("")

            val pageBody = formatHtml(
                driver.findElement(By.tagName("body")).getDomProperty("outerHTML")
                    ?: throw Exception("Couldn't find page source")
            )

            errorHandler.errorHandler(e, cssCase, pageBody)
        }
    }
}