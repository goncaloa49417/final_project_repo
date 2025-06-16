package org.example.navegation

import org.example.CssCase
import org.example.CssSelectors
import org.example.RequiredInformation
import org.openqa.selenium.WebElement
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class Scraper(){

    private val fileName = "cars.txt"

    fun getCategoryNavs(driver: ChromeDriverExtension, cssSelector: String) =
        driver.waitForElementsByCssSelector(cssSelector).size


    fun extractLoop(driver: ChromeDriverExtension, num: Int, cssSelector: CssSelectors) {
        val path = Paths.get("cars.txt")
        if (Files.exists(path)) Files.delete(path)

        repeat(num) { i ->
            driver.waitForElementsByCssSelector(cssSelector.categories)[i].click()
            val carList = driver.waitForElementsByCssSelector(cssSelector.requiredInformation.names)
            val priceList = driver.waitForElementsByCssSelector(cssSelector.requiredInformation.prices)

            carList.forEachIndexed { i, car ->
                Files.write(
                    path,
                    "${car.text} - ${priceList[i].text}\n".toByteArray(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                )
            }
        }

    }

}