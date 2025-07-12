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
import org.example.httpRequests.DivResp
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.OllamaChatRequest
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.OllamaRequestBodyFormat
import org.example.httpRequests.PreparedConversations
import org.example.httpRequests.PromptBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.String


class ErrorHandlerTest {

    val projectFileManager = mockk<ProjectFileManager>()
    val ollamaClient = mockk<OllamaHttpClient>()
    val promptBuilder = mockk<PromptBuilder>()
    val errorHandler = ErrorHandler(projectFileManager, ollamaClient, promptBuilder)

    @Test
    fun `success on generating CSS selector for div parent`() {
        val list = listOf<String>("")
        val chunkedList = list.chunked(20)

        val ollamaRequest1 = OllamaChatRequest
            .build("llama3.2:3b-instruct-fp16", PreparedConversations.semanticDivMessages, "")

        val ollamaRequest2 = OllamaRequestBodyFormat(
            "gemma3:12b", "", ModelAnswerSchemas.divSearchFormat, false
        )

        val ollamaRequest3 = OllamaRequestBodyFormat(
            "gemma3:12b", "", ModelAnswerSchemas.divSearchFormatFinal, false
        )

        val ollamaResponse2 = """
          {
            "div_element": "<element>" 
          }
        """.trimIndent()

        val ollamaResponse3 = """
          {
            "div_element": "<element>" 
            "div_css_selector": "div.abcd"
          }
        """.trimIndent()

        every { promptBuilder.populateDivClassificationTemplate(list) } returns ""
        every { promptBuilder.populateParentDivSearch("", "") } returns ""
        every { promptBuilder.populateParentDivSearchFinal(listOf("<element>"), "") } returns ""

        every { ollamaClient.request(ollamaRequest1) } returns ""
        every { ollamaClient.request(ollamaRequest2) } returns ollamaResponse2
        every { ollamaClient.request(ollamaRequest3) } returns ollamaResponse3

        errorHandler
            .getCssSelectorParentDiv(CssCase("", "", "", 0), list)

        verifyAll {
            promptBuilder.populateDivClassificationTemplate(list)
            promptBuilder.populateParentDivSearch("", "")
            promptBuilder.populateParentDivSearchFinal(listOf("<element>"), "")

            ollamaClient.request(ollamaRequest1)
            ollamaClient.request(ollamaRequest2)
            ollamaClient.request(ollamaRequest3)
        }
    }

    @Test
    fun `success on generating CSS selector for bot`() {
        val invalidCssSelector = "div.abcd"
        val newCssSelector = "div.efgh"


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

        every { promptBuilder.populatePruningTemplate("") } returns ""
        //every { ollamaClient.request() } returns ollamaResponse
        every {
            projectFileManager
                .editCssFile(
                    eq(invalidCssSelector),
                    eq(newCssCase)
                )
        } just runs

        //errorHandler.generateNewCssSelector(e, cssCase, "<body>")

        verifyAll {
            ollamaClient.request(any())
            projectFileManager
                .editCssFile(
                    eq(invalidCssSelector),
                    eq(newCssCase)
                )
        }
    }
}