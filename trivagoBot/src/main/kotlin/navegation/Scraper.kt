package org.example.navegation

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

class Scraper(){

    private val fileName = "hotels.txt"

    fun selectCookies(driver: ChromeDriverExtension, cookie: CookieSelectors){
        val wait = WebDriverWait(driver, Duration.ofSeconds(5))
        val shadowDiv = driver.findElementWithWait(By.cssSelector(cookie.shadowRoot)).shadowRoot

        val denyAllButton = wait.until {
            val button = shadowDiv.findElement(By.cssSelector(cookie.denyCookies))
            if (button.isDisplayed && button.isEnabled) button else null
        }

        denyAllButton!!.click()
    }

    fun navigatePage1(driver: ChromeDriverExtension, pageOne: PageOneSelectors) {
        val input = driver.findElementWithWait(By.cssSelector(pageOne.writingBox))
        input.sendKeys("Lisboa")
        driver.clickElementWithWait(By.cssSelector(pageOne.confirm))

        val nextDay = LocalDate.now().plusDays(1)
        val nextNextDay = LocalDate.now().plusDays(2)
        val formattedDate1 = nextDay.format(DateTimeFormatter.ISO_DATE)
        val formattedDate2 = nextNextDay.format(DateTimeFormatter.ISO_DATE)

        //driver.clickElementWithWait(By.cssSelector("button[data-testid='search-form-calendar-checkin']"))
        driver.clickElementWithWait(By.cssSelector(pageOne.date + "${formattedDate1}']"))

        //driver.findElementWithWait(By.cssSelector("button[data-testid='search-form-calendar-checkout']")).click()
        driver.clickElementWithWait(By.cssSelector(pageOne.date + "${formattedDate2}']"))

        driver.findElementWithWait(By.cssSelector(pageOne.search)).click()
    }

    fun navigatePage2(driver: ChromeDriverExtension, pageTwo: PageTwoSelectors) {
        extractLoop(driver, pageTwo)
    }

    private fun extractLoop(driver: ChromeDriverExtension, pageTwo: PageTwoSelectors) {
        val path = Paths.get(fileName)
        if (Files.exists(path)) Files.delete(path)

        while (true) {
            val hotels = driver.findElementsWithWait(By.cssSelector(pageTwo.hotelNames))
            val prices = driver.findElementsWithWait(By.cssSelector(pageTwo.hotelPrices))
            println("Hotel size: ${hotels.size} - Price size: ${prices.size}")

            hotels.forEachIndexed { i, hotel ->
                val title = hotel.text
                val price = prices[i].text
                Files.write(path, "$title - $price\n".toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
            }

            try{
                driver.clickElementWithWait(By.cssSelector(pageTwo.nextPage))
                driver.waitUntilElementsStale(hotels)
                //driver.waitUntilElementsStale(prices)
            } catch (e: Exception) {
                break
            }
        }
    }
}