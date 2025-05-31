package org.example.navegation

import org.example.CssSelectors
import org.example.WEBSITE
import org.openqa.selenium.TimeoutException

fun scrapingController(scraper: Scraper, cssSelectors: CssSelectors) {
    val driver: ChromeDriverExtension = ChromeDriverExtension(null)
    driver.get(WEBSITE)

    repeat(3) {
        try {
            scraper.selectCookies(driver, cssSelectors.cookies)
            scraper.navigatePage1(driver, cssSelectors.pageOne)
            scraper.navigatePage2(driver, cssSelectors.pageTwo)
        } catch (e: TimeoutException) {
            println(e.message)
            println("====================================================")
            println(e.cause!!.message)
            e.message!!.split("By.cssSelector: ")
            // função para extrair a informação pretinente para a LLM e perguntar à mesma
        } finally {
            driver.quit()
        }
    }
}