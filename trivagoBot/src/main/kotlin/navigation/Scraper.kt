package navigation

import org.example.CookieSelectors
import org.example.PageOneSelectors
import org.example.PageTwoSelectors

interface Scraper {

    fun selectCookies(driver: ChromeDriverExtension, cookie: CookieSelectors)

    fun navigatePage1(driver: ChromeDriverExtension, pageOne: PageOneSelectors)

    fun navigatePage2(driver: ChromeDriverExtension, pageTwo: PageTwoSelectors)

}