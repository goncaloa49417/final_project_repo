package org.example.navegation

import org.example.errorHandler.ElementNotFoundByCssSelector
import org.openqa.selenium.By
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

val EXPLICIT_WAIT_SECONDS: Duration = Duration.ofSeconds(10)

class ChromeDriverExtension(options: ChromeOptions?): ChromeDriver(options ?: ChromeOptions()) {

    private val wait: WebDriverWait = WebDriverWait(this, EXPLICIT_WAIT_SECONDS)

    fun waitForElementByCssSelector(cssSelector: String, timeout: Long? = null): WebElement {
        val effectiveWait = timeout?.let { WebDriverWait(this, Duration.ofSeconds(timeout)) } ?: wait
        return try {
            effectiveWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)))
        }catch (e: Exception){
            throw ElementNotFoundByCssSelector(cssSelector)
        }
    }

    fun waitForElementsByCssSelector(cssSelector: String, timeout: Long? = null): List<WebElement> {
        val effectiveWait = timeout?.let { WebDriverWait(this, Duration.ofSeconds(timeout)) } ?: wait
        return try {
            effectiveWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(cssSelector)))
        }catch (e: Exception){
            throw ElementNotFoundByCssSelector(cssSelector)
        }
    }

    fun waitToClickElementByCssSelector(cssSelector: String, timeout: Long? = null) {
        val effectiveWait = timeout?.let { WebDriverWait(this, Duration.ofSeconds(timeout)) } ?: wait

        try {
            effectiveWait.until {
                val element = this.waitForElementByCssSelector(cssSelector)

                try {
                    if (element.isEnabled && element.isDisplayed) {
                        element.click()
                        true
                    } else {
                        false
                    }
                } catch (e: StaleElementReferenceException) {
                    false
                }
            }
        }catch (e: Exception){
            throw ElementNotFoundByCssSelector(cssSelector)
        }
    }

    fun waitUntilElementsStale(elements: List<WebElement>, timeout: Long? = null) {
        val effectiveWait = timeout?.let { WebDriverWait(this, Duration.ofSeconds(timeout)) } ?: wait
        elements.forEach { element ->
            effectiveWait.until(ExpectedConditions.stalenessOf(element))
        }
    }

}