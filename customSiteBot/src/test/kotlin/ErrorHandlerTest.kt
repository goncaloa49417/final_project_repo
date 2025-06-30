import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verifyAll
import org.example.CssCase
import org.example.ProjectFileManager
import org.example.errorHandler.ElementNotFoundByCssSelector
import org.example.errorHandler.ErrorHandler
import org.example.errorHandler.UnableToGenerateWorkingCssSelector
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.PromptBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class ErrorHandlerTest {

    val errorHandler = ErrorHandler()
    val projectFileManager = mockk<ProjectFileManager>()
    val ollamaClient = mockk<OllamaHttpClient>()
    val promptBuilder = PromptBuilder()//mockk<PromptBuilder>()

    @Test
    fun `surpass retry limit`() {
        val invalidCssSelector = "div.abcd"
        val e = ElementNotFoundByCssSelector(invalidCssSelector)
        val cssCase = CssCase(
            "<element>",
            invalidCssSelector,
            "An element",
            3
        )

        every { projectFileManager.extractCssCase(invalidCssSelector) } returns cssCase

        assertThrows<UnableToGenerateWorkingCssSelector> {
            errorHandler.errorHandler(e, projectFileManager, ollamaClient, promptBuilder, "")
        }

        verifyAll {
            projectFileManager.extractCssCase(invalidCssSelector)
        }
    }

    @Test
    fun `success on creating a new cssCase`() {
        val invalidCssSelector = "div.abcd"
        val newCssSelector = "div.efgh"

        val e = ElementNotFoundByCssSelector(invalidCssSelector)

        val cssCase = CssCase(
            "<element>",
            invalidCssSelector,
            "An element",
            0
        )
        val newCssCase = CssCase(
            "<new element>",
            newCssSelector,
            "An new element",
            1
        )

        val ollamaResponse = """
          {
              "old_element": "<element>",
              "old_css_selector": "$invalidCssSelector",
              "old_description": "An element",
              "new_element": "<new element>",
              "new_css_selector": "$newCssSelector",
              "new_description": "An new element"
          }
        """.trimIndent()

        every { projectFileManager.extractCssCase(invalidCssSelector) } returns cssCase
        every { ollamaClient.request(any()) } returns ollamaResponse
        every {
            projectFileManager
                .editCssFile(
                    eq(invalidCssSelector),
                    eq(newCssCase)
                )
        } just runs

        errorHandler.errorHandler(e, projectFileManager, ollamaClient, promptBuilder, "<body>")

        verifyAll {
            projectFileManager.extractCssCase(invalidCssSelector)
            ollamaClient.request(any())
            projectFileManager
                .editCssFile(
                    eq(invalidCssSelector),
                    eq(newCssCase)
                )
        }
    }
}