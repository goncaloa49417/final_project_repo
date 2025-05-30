package org.example.navegation

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

    fun selectCookies(driver: ChromeDriverExtension){
        val wait = WebDriverWait(driver, Duration.ofSeconds(5))
        val shadowDiv = driver.findElementWithWait(By.cssSelector("#usercentrics-root")).shadowRoot

        val denyAllButton = wait.until {
            val button = shadowDiv.findElement(By.cssSelector("button[data-testid='uc-deny-all-button']"))
            if (button.isDisplayed && button.isEnabled) button else null
        }

        denyAllButton!!.click()
    }

    fun navegatePage1(driver: ChromeDriverExtension) {
        val input = driver.findElementWithWait(By.cssSelector("#input-auto-complete"))
        input.sendKeys("Lisboa")
        driver.clickElementWithWait(By.cssSelector("ul.znDBLW li:first-child"))

        val nextDay = LocalDate.now().plusDays(1)
        val nextNextDay = LocalDate.now().plusDays(2)
        val formattedDate1 = nextDay.format(DateTimeFormatter.ISO_DATE)
        val formattedDate2 = nextNextDay.format(DateTimeFormatter.ISO_DATE)

        //driver.clickElementWithWait(By.cssSelector("button[data-testid='search-form-calendar-checkin']"))
        driver.clickElementWithWait(By.cssSelector("button[data-testid='valid-calendar-day-${formattedDate1}']"))

        //driver.findElementWithWait(By.cssSelector("button[data-testid='search-form-calendar-checkout']")).click()
        driver.clickElementWithWait(By.cssSelector("button[data-testid='valid-calendar-day-${formattedDate2}']"))

        driver.findElementWithWait(By.cssSelector("button[data-testid='search-button-with-loader']")).click()
    }

    fun navegatePage2(driver: ChromeDriverExtension) {
        extractLoop(driver)
    }

    private fun extractLoop(driver: ChromeDriverExtension) {
        val path = Paths.get(fileName)
        if (Files.exists(path)) Files.delete(path)

        while (true) {
            val hotels = driver.findElementsWithWait(By.cssSelector("button.uWs6sG.SwVR4I span"))
            val prices = driver.findElementsWithWait(By.cssSelector("div.QWtI5m div.HjOk6Q.oVtsoQ.ZTIfHR"))
            println("Hotel size: ${hotels.size} - Price size: ${prices.size}")

            hotels.forEachIndexed { i, hotel ->
                val title = hotel.text
                val price = prices[i].text
                Files.write(path, "$title - $price\n".toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
            }

            try{
                driver.clickElementWithWait(By.cssSelector("button[data-testid='next-result-page']"))
                driver.waitUntilElementsStale(hotels)
                //driver.waitUntilElementsStale(prices)
            } catch (e: Exception) {
                break
            }
        }
    }
}