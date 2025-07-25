import io.mockk.every
import io.mockk.mockk
import org.example.httpRequests.ApiGeneratedResponse
import org.example.httpRequests.ModelAnswerSchemas
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.OllamaRequestBodyFormat
import org.http4k.connect.ollama.OllamaMoshi.auto
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OllamaHttpClientTest {

    private val mockClient = mockk<HttpHandler>()

    @Test
    fun `run the request function successfully`() {

        val apiResponse = ApiGeneratedResponse(
            model = "llama3.2:3b",
            created_at = "2025-06-29T16:42:10.123Z",
            response = "Dont know, check your watch",
            done = true,
            done_reason = "stop_sequence",
            context = listOf(218, 317, 446, 129, 908, 334),
            total_duration = 892_000_000,
            load_duration = 112_000_000,
            prompt_eval_count = 17,
            prompt_eval_duration = 74_000_000,
            eval_count = 78,
            eval_duration = 590_000_000
        )

        val responseLens = Body.auto<ApiGeneratedResponse>().toLens()
        val mockResponse = Response(Status.OK).with(responseLens of apiResponse)

        val ollamaRequestBody = OllamaRequestBodyFormat(
            "model",
            "What is time?",
            ModelAnswerSchemas.cssFormat,
            false
        )

        every { mockClient.invoke(any()) } returns mockResponse

        val ollamaHttpClient = OllamaHttpClient(mockClient)

        val response = ollamaHttpClient.request(ollamaRequestBody)

        assertEquals("Dont know, check your watch", response)
    }

}