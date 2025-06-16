package org.example.navegation

import org.example.FileManager
import org.example.WEBSITE
import org.example.divSplitter
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.errorHandler
import org.example.httpRequests.PromptBuilder
import java.nio.file.Files
import java.nio.file.Paths

fun scrapingController(scraper: Scraper, fileManager: FileManager, promptBuilder: PromptBuilder) {
    repeat(3) {
        val driver: ChromeDriverExtension = ChromeDriverExtension(null)
        val cssSelectors = fileManager.extractCssSelectors()

        try {
            driver.get(WEBSITE)
            val num = scraper.getCategoryNavs(driver, cssSelectors.categories)
            scraper.extractLoop(driver, num, cssSelectors)
            driver.quit()
            return
        } catch (e: ElementNotFoundByCssSelector) {
            println(e.message)
            val divList = divSplitter(driver)
            val pageSource = driver.pageSource ?: throw Exception("Couldn't find page source")
            errorHandler(e, fileManager, promptBuilder, divList, pageSource)
        }
        driver.quit()
    }
}