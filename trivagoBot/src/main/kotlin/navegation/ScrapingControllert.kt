package org.example.navegation

import org.example.CssSelectors
import org.example.WEBSITE
import org.http4k.lens.httpBodyRoot
import org.openqa.selenium.TimeoutException

fun scrapingController(scraper: Scraper, cssSelectors: CssSelectors) {
    repeat(3) {
        val driver: ChromeDriverExtension = ChromeDriverExtension(null)
        try {
            driver.get(WEBSITE)
            scraper.selectCookies(driver, cssSelectors.cookies)
            scraper.navigatePage1(driver, cssSelectors.pageOne)
            scraper.navigatePage2(driver, cssSelectors.pageTwo)
            driver.quit()
            return
        } catch (e: Exception) {
            //println(e.message)
            //println("====================================================")
            //println(e.cause!!.message)
            println(e.message)
        }
        driver.quit()
    }
}