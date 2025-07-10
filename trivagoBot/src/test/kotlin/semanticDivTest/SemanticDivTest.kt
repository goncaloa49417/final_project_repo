package semanticDivTest

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.example.WEBSITE
import org.example.divSplitter
import org.example.httpRequests.OllamaChatRequest
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.OllamaRequestBody
import org.example.httpRequests.PreparedConversations
import org.example.httpRequests.PromptBuilder
import navigation.ChromeDriverExtension
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class Data(val response: String, val time: Double)


class SemanticDivTest {

    @Test
    fun test() = runBlocking {
        val ollamaClient = OllamaHttpClient()

        val startTime = System.currentTimeMillis()
        val responseList = (0..20).map { num ->
            async {
                val ollamaRequest = OllamaRequestBody(
                    "gemma3:12b",
                    "1 + $num = ?",
                    false
                )

                testSemaphore.withPermit {
                    val startTime = System.currentTimeMillis()
                    val response = ollamaClient.request(ollamaRequest)

                    Data(response, (System.currentTimeMillis() - startTime) / 1000.0)
                }
            }
        }.awaitAll()

        val totalTime = System.currentTimeMillis() - startTime / 1000.0

        responseList.forEach { data->
            println("R: ${data.response.removeSuffix("\n")}; T: ${data.time}\n")
        }

        val sumTime = responseList.sumOf { it.time }

        println("totalTime: $totalTime; sumTime: $sumTime")
    }

    private val testSemaphore = Semaphore(5)

    @Test
    fun `Semantic div of target HTML element 2`() = runBlocking {
        val driver = ChromeDriverExtension(null)
        val ollamaClient = OllamaHttpClient()
        val promptBuilder = PromptBuilder()
        val path =
            Paths.get("C:\\Projeto de licenciatura\\trivagoBot\\src\\test\\kotlin\\semanticDivTest\\llama3.2-3b-1.txt")

        driver.get(WEBSITE)
        val divList = divSplitter(driver)
        driver.quit()

        val chunkedDivList = divList.chunked(20)

        val startTime = System.currentTimeMillis()

        val responseList = chunkedDivList.map { divList ->
            async {
                testSemaphore.withPermit {
                    val prompt = promptBuilder.populateDivClassificationTemplate(divList)
                    val ollamaChatRequest =
                        OllamaChatRequest
                            .build("llama3.2:3b", PreparedConversations.semanticDivMessages, prompt)

                    val startTime = System.currentTimeMillis()
                    val response = ollamaClient.request(ollamaChatRequest)
                    val totalTime = (System.currentTimeMillis() - startTime) / 1000.0
                    "$response\n\ntime: $totalTime s"
                }
            }
        }.awaitAll()

        val endTime = System.currentTimeMillis()

        responseList.forEachIndexed { i, response ->
            Files.write(
                path,
                "Chunk ${i+1}:\n$response\n\n".toByteArray(),
                StandardOpenOption.APPEND
            )
        }.also {
            Files.write(
                path,
                "Total time: ${(endTime - startTime) / 1000.0}\n\n#########\n\n".toByteArray(),
                StandardOpenOption.APPEND
            )
        }
    }

    @Test
    fun `Semantic div of target HTML element`() = runBlocking {
        val driver = ChromeDriverExtension(null)
        val ollamaClient = OllamaHttpClient()
        val promptBuilder = PromptBuilder()
        val path =
            Paths.get("C:\\Projeto de licenciatura\\trivagoBot\\src\\test\\kotlin\\semanticDivTest\\llama3.2-3b.txt")

        driver.get(WEBSITE)
        val divList = divSplitter(driver)
        driver.quit()

        val chunkedDivList = divList.chunked(20)

        val startTime = System.currentTimeMillis()

        val responseList = chunkedDivList.map { divList ->
            async {
                testSemaphore.withPermit {
                    val prompt = promptBuilder.populateDivClassificationTemplate(divList)

                    val ollamaRequest = OllamaRequestBody(
                        "llama3.2:3b",
                        prompt,
                        false
                    )

                    val startTime = System.currentTimeMillis()
                    val response = ollamaClient.request(ollamaRequest)
                    val totalTime = (System.currentTimeMillis() - startTime) / 1000.0
                    "$response\n\ntime: $totalTime s"
                }
            }
        }.awaitAll()

        val endTime = System.currentTimeMillis()

        responseList.forEachIndexed { i, response ->
            Files.write(
                path,
                "Chunk ${i+1}:\n$response\n\n".toByteArray(),
                StandardOpenOption.APPEND
            )
        }.also {
            Files.write(
                path,
                "Total time: ${(endTime - startTime) / 1000.0}\n\n#########\n\n".toByteArray(),
                StandardOpenOption.APPEND
            )
        }
    }

}