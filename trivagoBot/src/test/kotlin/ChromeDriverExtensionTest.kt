import org.example.navegation.ChromeDriverExtension
import org.junit.jupiter.api.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions


class ChromeDriverExtensionTest {

    private val testPage = "C:\\Projeto de licenciatura\\customSiteBot\\src\\test\\kotlin\\test-page.html"

    @Test
    fun `test waitForElementByCssSelector function`(){
        val options = ChromeOptions()
        options.addArguments("--headless")
        val driver = ChromeDriverExtension(options)
        driver.get(testPage)
        driver.waitForElementByCssSelector("#delayed-div")
    }

    @Test
    fun `test waitToClickElementByCssSelector function`(){
        val options = ChromeOptions()
        options.addArguments("--headless")
        val driver = ChromeDriverExtension(options)
        driver.get(testPage)
        driver.waitToClickElementByCssSelector("#delayed-button")
    }

}