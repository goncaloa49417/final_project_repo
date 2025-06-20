package org.example.navegation

import org.example.FileManager
import org.example.WEBSITE
import org.example.divSplitter
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.errorHandler
import org.example.httpRequests.PromptBuilder
import org.openqa.selenium.By
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
            val pageBody =
                driver.findElement(By.tagName("body")).getDomProperty("outerHTML")
                    ?: throw Exception("Couldn't find page source")
            //val pageSource = driver.pageSource ?: throw Exception("Couldn't find page source")
            errorHandler(e, fileManager, promptBuilder, pageBody)
        } finally {
            driver.quit()
        }
    }
}