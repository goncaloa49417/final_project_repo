package org.example.navigation

import org.example.CssSelectors


interface Scraper {

    fun getCategoryNavs(driver: ChromeDriverExtension, cssSelector: String): Int

    fun extractLoop(driver: ChromeDriverExtension, num: Int, cssSelector: CssSelectors)

}