import navigation.ChromeDriverExtension
import org.junit.jupiter.api.Test
import org.openqa.selenium.chrome.ChromeOptions
import java.nio.file.Paths


class ChromeDriverExtensionTest {

    private val testPage = Paths.get("src", "test", "kotlin", "test-page.html").toAbsolutePath().toUri().toString()

    @Test
    fun `test waitForElementByCssSelector function`(){
        val options = ChromeOptions()
        options.addArguments("--headless")
        val driver = ChromeDriverExtension(options)
        driver.get(testPage)
        driver.waitForElementByCssSelector("#delayed-div")
        driver.quit()
    }

    @Test
    fun `test waitForElementsByCssSelector function`(){
        val options = ChromeOptions()
        options.addArguments("--headless")
        val driver = ChromeDriverExtension(options)
        driver.get(testPage)
        driver.waitForElementsByCssSelector("div.remove")
        driver.quit()
    }

    @Test
    fun `test waitToClickElementByCssSelector function`(){
        val options = ChromeOptions()
        options.addArguments("--headless")
        val driver = ChromeDriverExtension(options)
        driver.get(testPage)
        driver.waitToClickElementByCssSelector("#delayed-button")
        driver.quit()
    }

    @Test
    fun `test waitUntilElementsStale function`(){
        val options = ChromeOptions()
        options.addArguments("--headless")
        val driver = ChromeDriverExtension(options)
        driver.get(testPage)
        val elements = driver.waitForElementsByCssSelector("div.remove")
        driver.waitUntilElementsStale(elements)
        driver.quit()
    }
}