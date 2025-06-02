package org.example.navegation

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

    fun findElementWithWait(by: By, timeout: Long? = null): WebElement {
        val effectiveWait = timeout?.let { WebDriverWait(this, Duration.ofSeconds(timeout)) } ?: wait
        return try {
            effectiveWait.until(ExpectedConditions.presenceOfElementLocated(by))
        }catch (e: Exception){
            throw RuntimeException(by.toString())
        }
    }

    fun findElementsWithWait(by: By, timeout: Long? = null): List<WebElement> {
        val effectiveWait = timeout?.let { WebDriverWait(this, Duration.ofSeconds(timeout)) } ?: wait
        return effectiveWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by))
    }

    fun clickElementWithWait(by: By, timeout: Long? = null) {
        val effectiveWait = timeout?.let { WebDriverWait(this, Duration.ofSeconds(timeout)) } ?: wait
        effectiveWait.until {
            val element = this.findElementWithWait(by)
            //val element = ExpectedConditions.elementToBeClickable(by).apply(it)
            try {
                if (element.isEnabled && element.isDisplayed){
                    element.click()
                    true
                } else {
                    false
                }
            } catch (e: StaleElementReferenceException) {
                println("How?")
                false
            }
        }
    }

    fun waitUntilElementsStale(elements: List<WebElement>, timeout: Long? = null) {
        val effectiveWait = timeout?.let { WebDriverWait(this, Duration.ofSeconds(timeout)) } ?: wait
        elements.forEach { element ->
            effectiveWait.until(ExpectedConditions.stalenessOf(element))
        }
    }

}