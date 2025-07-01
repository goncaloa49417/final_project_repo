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
                {"element":"div", "cssSelector":"#car-navigation", "description":"div for car navigation", "failureCount": 0},
                {"element":"div", "cssSelector":"#name", "description":"div with car name", "failureCount": 1},
                {"element":"div", "cssSelector":"#price", "description":"div with car price", "failureCount": 2}
            ]
            """.trimIndent()
        )
        manager = ProjectFileManager(tempFile.absolutePath)
    }


    @Test
    fun `extractCssSelectors returns correct selectors`() {
        val selectors = manager.extractCssSelectors()
        assertEquals("#car-navigation", selectors.categories)
        assertEquals("#name", selectors.requiredInformation.names)
        assertEquals("#price", selectors.requiredInformation.prices)
    }

    @Test
    fun `extractCssCase returns correct CssCase`() {
        val expectedCssCase = CssCase(
            "div",
            "#car-navigation",
            "div for car navigation",
            0
        )

        val result = manager.extractCssCase("#car-navigation")

        assertEquals(expectedCssCase, result)
    }

    @Test
    fun `extractCssCase throws exception`() {
        assertThrows<CssCaseNotFound> { manager.extractCssCase("#incorrect") }
    }

    @Test
    fun `editCssFile updates selector`() {
        val updated = CssCase(
            "nav",
            "#car-navigation",
            "nav for car navigation",
            0
        )
        manager.editCssFile("#car-navigation", updated)

        val result = manager.extractCssCase("#car-navigation")
        assertEquals("nav for car navigation", result.description)
        assertEquals("nav", result.element)
    }

    @Test
    fun `resetAllFailureCounters sets all to 0`() {
        manager.resetAllFailureCounters()
        val cases = listOf("#car-navigation", "#name", "#price").map { manager.extractCssCase(it) }
        cases.forEach { assertEquals(0, it.failureCount) }
    }

    @AfterEach
    fun teardown() {
        tempFile.delete()
    }
}