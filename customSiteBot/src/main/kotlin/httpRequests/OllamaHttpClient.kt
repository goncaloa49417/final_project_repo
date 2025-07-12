package org.example.httpRequests

import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.format.KotlinxSerialization.auto
import okhttp3.OkHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import java.time.Duration


class OllamaHttpClient(
    private val client: HttpHandler = OkHttp(
        OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(600))
            .readTimeout(Duration.ofSeconds(1200))
            .writeTimeout(Duration.ofSeconds(600))
            .build()
    )
) : HttpClient {

    override fun request(ollamaRequest: OllamaRequest): String =
        when (ollamaRequest) {
            is OllamaRequestBody -> {
                val response = getResponse("http://localhost:11434/api/generate", ollamaRequest)
                val jsonResponseLens = Body.auto<ApiGeneratedResponse>().toLens()

                jsonResponseLens(response).response
            }

            is OllamaRequestBodyFormat -> {
                val response = getResponse("http://localhost:11434/api/generate", ollamaRequest)
                val jsonResponseLens = Body.auto<ApiGeneratedResponse>().toLens()

                jsonResponseLens(response).response
            }

            is OllamaChatRequest -> {
                val response = getResponse("http://localhost:11434/api/chat", ollamaRequest)
                val jsonResponseLens = Body.auto<ApiChatResponse>().toLens()

                jsonResponseLens(response).message.content
            }
        }

    private inline fun <reified T : OllamaRequest> getResponse(path: String, body: T): Response {
        val jsonRequestLens = Body.auto<T>().toLens()
        val request = Request(Method.POST, path)
            .header("Content-Type", "application/json")
            .with(jsonRequestLens of body)

        return client(request)
    }

}