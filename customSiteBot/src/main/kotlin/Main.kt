package org.example

import org.example.httpRequests.PromptBuilder
import org.example.navegation.ChromeDriverExtension
import org.example.navegation.Scraper
import org.example.navegation.scrapingController
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


const val CSS_FILE = "css selectors.json"

fun main() {
    val scraper = Scraper()
    val fileManager = FileManager(CSS_FILE)
    val promptBuilder = PromptBuilder()

    scrapingController(scraper, fileManager, promptBuilder)
}