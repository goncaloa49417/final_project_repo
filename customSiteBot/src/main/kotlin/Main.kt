package org.example

import org.example.errorHandler.ErrorHandler
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.PromptBuilder
import org.example.navigation.ChromeDriverExtension
import org.example.navigation.SiteScraper
import org.example.navigation.scrapingController

const val CSS_FILE = "css selectors.json"

fun main() {
    val siteScraper = SiteScraper()
    val projectFileManager = ProjectFileManager(CSS_FILE)
    val promptBuilder = PromptBuilder()
    val ollamaClient = OllamaHttpClient()
    val errorHandler = ErrorHandler(projectFileManager, ollamaClient, promptBuilder)
    val driver: ChromeDriverExtension = ChromeDriverExtension(null)

    try {
        scrapingController(driver, siteScraper, projectFileManager, errorHandler)
        driver.quit()
    } catch (e: Exception) {
        driver.quit()
        throw e
    }
}