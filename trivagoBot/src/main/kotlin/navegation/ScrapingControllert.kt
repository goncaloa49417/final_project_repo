package org.example.navegation

import org.example.Prompts
import org.example.WEBSITE
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException

fun scrapingController(scraper: Scraper) {
    val driver: ChromeDriverExtension = ChromeDriverExtension(null)
    driver.get(WEBSITE)

    try {
        scraper.selectCookies(driver)
        scraper.navegatePage1(driver)
        scraper.navegatePage2(driver)
        driver.quit()
    } catch (e: TimeoutException) {
        println(e.message)
        println("====================================================")
        println(e.cause!!.message)
        // função para extrair a informação pretinente para a LLM e perguntar à mesma
    } finally {
        driver.quit()
    }
}