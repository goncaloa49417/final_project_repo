package org.example.navigation

import org.example.CssSelectors
import org.example.navigation.Scraper
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class SiteScraper(): Scraper {

    private val fileName = "cars.txt"

    override fun getCategoryNavs(driver: ChromeDriverExtension, cssSelector: String) =
        driver.waitForElementsByCssSelector(cssSelector).size


    override fun extractLoop(driver: ChromeDriverExtension, num: Int, cssSelector: CssSelectors) {
        val path = Paths.get(fileName)
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