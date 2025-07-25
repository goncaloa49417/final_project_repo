package navigation

import org.example.CookieSelectors
import org.example.PageOneSelectors
import org.example.PageTwoSelectors
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.WebDriverWait
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SiteScraper() : Scraper {

    private val fileName = "hotels.txt"

    override fun selectCookies(driver: ChromeDriverExtension, cookie: CookieSelectors) {
        val wait = WebDriverWait(driver, Duration.ofSeconds(5))
        val shadowDiv = driver.waitForElementByCssSelector(cookie.shadowRoot).shadowRoot

        val denyAllButton = wait.until {
            val button = shadowDiv.findElement(By.cssSelector(cookie.denyCookies))
            if (button.isDisplayed && button.isEnabled) button else null
        }

        denyAllButton!!.click()
    }

    override fun navigatePage1(driver: ChromeDriverExtension, pageOne: PageOneSelectors) {
        val input = driver.waitForElementByCssSelector(pageOne.writingBox)
        input.sendKeys("Porto")
        driver.waitToClickElementByCssSelector(pageOne.confirm)

        val nextDay = LocalDate.now().plusDays(1)
        val nextNextDay = LocalDate.now().plusDays(2)
        val formattedDate1 = nextDay.format(DateTimeFormatter.ISO_DATE)
        val formattedDate2 = nextNextDay.format(DateTimeFormatter.ISO_DATE)

        driver.waitToClickElementByCssSelector(pageOne.date + "${formattedDate1}']")
        driver.waitToClickElementByCssSelector(pageOne.date + "${formattedDate2}']")

        driver.waitForElementByCssSelector(pageOne.search).click()
    }

    override fun navigatePage2(driver: ChromeDriverExtension, pageTwo: PageTwoSelectors) {
        extractLoop(driver, pageTwo)
    }

    private fun extractLoop(driver: ChromeDriverExtension, pageTwo: PageTwoSelectors) {
        val path = Paths.get(fileName)
        if (Files.exists(path)) Files.delete(path)

        val numberOfPages = driver.waitForElementByCssSelector(pageTwo.numberOfPages).text.toInt()

        repeat (numberOfPages) { currentPage ->
            val hotels = driver.waitForElementsByCssSelector(pageTwo.hotelNames)
            val prices = driver.waitForElementsByCssSelector(pageTwo.hotelPrices)
            println("Hotel size: ${hotels.size} - Price size: ${prices.size}")

            hotels.forEachIndexed { i, hotel ->
                val title = hotel.text
                val price = prices[i].text
                Files.write(
                    path,
                    "$title - $price\n".toByteArray(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                )
            }

            if (currentPage != numberOfPages-1) {
                driver.waitToClickElementByCssSelector(pageTwo.nextPage)
                driver.waitUntilElementsStale(hotels)
            }
        }
    }
}