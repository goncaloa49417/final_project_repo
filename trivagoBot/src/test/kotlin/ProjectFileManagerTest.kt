import org.example.CssCase
import org.example.ProjectFileManager
import org.example.errorHandler.CssCaseNotFound
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertEquals


class ProjectFileManagerTest {

    // lateinit: keyword usada para declarar variáveis com garantia de serem inicializadas no futuro
    private lateinit var tempFile: File
    private lateinit var manager: ProjectFileManager

    @BeforeEach
    fun setup() {
        tempFile = File.createTempFile("test-css", ".json")
        tempFile.writeText(
            """
            [
                {"element":"div", "cssSelector":"#shadow-root", "description":"Div container for shadow elements", "failureCount": 0},
                {"element":"button", "cssSelector":"#deny-button", "description":"Button likely used for denying or cancelling", "failureCount": 1},
                {"element":"input", "cssSelector":"#location-box", "description":"Box to write the location", "failureCount": 2},
                {"element":"div", "cssSelector":"#confirm-selector", "description":"Div to confirm location", "failureCount": 0},
                {"element":"button", "cssSelector":"#date-select", "description":"Button to select or confirm a date", "failureCount": 1},
                {"element":"button", "cssSelector":"#search-button", "description":"Button to initiate a search", "failureCount": 2},
                {"element":"div", "cssSelector":"#names-selector", "description":"Div component for selecting or displaying names", "failureCount": 0},
                {"element":"div", "cssSelector":"#prices-selector", "description":"Div section showing pricing details", "failureCount": 1},
                {"element":"button", "cssSelector":"#next-page-selector", "description":"Button to navigate to the next page", "failureCount": 2}
            ]
            """.trimIndent()
        )
        manager = ProjectFileManager(tempFile.absolutePath)
    }


    @Test
    fun `extractCssSelectors returns correct selectors`() {
        val selectors = manager.extractCssSelectors()
        assertEquals("#shadow-root", selectors.cookies.shadowRoot)
        assertEquals("#deny-button", selectors.cookies.denyCookies)
        assertEquals("#location-box", selectors.pageOne.writingBox)
        assertEquals("#confirm-selector", selectors.pageOne.confirm)
        assertEquals("#date-select", selectors.pageOne.date)
        assertEquals("#search-button", selectors.pageOne.search)
        assertEquals("#names-selector", selectors.pageTwo.hotelNames)
        assertEquals("#prices-selector", selectors.pageTwo.hotelPrices)
        assertEquals("#next-page-selector", selectors.pageTwo.nextPage)
    }

    @Test
    fun `extractCssCase returns correct CssCase`() {
        val expectedCssCase = CssCase(
            "div",
            "#shadow-root",
            "Div container for shadow elements",
            0
        )

        val result = manager.extractCssCase("#shadow-root")

        assertEquals(expectedCssCase, result)
    }

    @Test
    fun `extractCssCase throws exception`() {
        assertThrows<CssCaseNotFound> { manager.extractCssCase("#incorrect") }
    }

    @Test
    fun `editCssFile updates selector`() {
        val updated = CssCase(
            "input",
            "#location-input",
            "Input to write a location",
            0
        )
        manager.editCssFile("#location-box", updated)

        val result = manager.extractCssCase("#location-input")
        assertEquals("Input to write a location", result.description)
        assertEquals("input", result.element)
    }

    @Test
    fun `resetAllFailureCounters sets all to 0`() {
        manager.resetAllFailureCounters()
        val cases = listOf(
            "#shadow-root",
            "#deny-button",
            "#location-box",
            "#confirm-selector",
            "#date-select",
            "#search-button",
            "#names-selector",
            "#prices-selector",
            "#next-page-selector"
        ).map { manager.extractCssCase(it) }
        cases.forEach { assertEquals(0, it.failureCount) }
    }

    @AfterEach
    fun teardown() {
        tempFile.delete()
    }

}