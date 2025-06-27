package org.example

import org.example.errorHandler.ErrorHandler
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.PromptBuilder
import org.example.navegation.ChromeDriverExtension
import org.example.navegation.SiteScraper
import org.example.navegation.scrapingController

const val CSS_FILE = "css selectors.json"

fun main() {
    val siteScraper = SiteScraper()
    val projectFileManager = ProjectFileManager(CSS_FILE)
    val promptBuilder = PromptBuilder()
    val ollamaClient = OllamaHttpClient()
    val errorHandler = ErrorHandler()
    val driver: ChromeDriverExtension = ChromeDriverExtension(null)

    try {
        scrapingController(driver, siteScraper, projectFileManager, errorHandler, ollamaClient, promptBuilder)
        driver.quit()
    } catch (e: Exception) {
        driver.quit()
        throw e
    }
}