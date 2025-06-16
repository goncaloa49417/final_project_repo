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

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
const val WEBSITE = "C:\\Users\\Gonçalo\\Desktop\\projeto de licenciatura\\site\\mainPage.html"
const val CSS_FILE = "css selectors.json"

fun main() {
    val scraper = Scraper()
    val fileManager = FileManager(CSS_FILE)
    val promptBuilder = PromptBuilder()

    scrapingController(scraper, fileManager, promptBuilder)
}