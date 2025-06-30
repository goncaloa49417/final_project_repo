package org.example.httpRequests

import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.format.KotlinxSerialization.auto
import okhttp3.OkHttpClient
import org.http4k.client.DualSyncAsyncHttpHandler
import org.http4k.core.HttpHandler
import java.time.Duration


class OllamaHttpClient(
    private val client: HttpHandler = OkHttp(
        OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(600))
            .readTimeout(Duration.ofSeconds(1200))
            .writeTimeout(Duration.ofSeconds(600))
            .build()
    )
): HttpClient {

    override fun request(ollamaRequest: OllamaRequest): String {
        val request = when (ollamaRequest) {
            is OllamaRequestBody -> {
                val jsonRequestLens = Body.auto<OllamaRequestBody>().toLens()
                Request(Method.POST, "http://localhost:11434/api/generate")
                    .header("Content-Type", "application/json")
                    .with(jsonRequestLens of ollamaRequest)
            }

            is OllamaRequestBodyFormat -> {
                val jsonRequestLens = Body.auto<OllamaRequestBodyFormat>().toLens()
                Request(Method.POST, "http://localhost:11434/api/generate")
                    .header("Content-Type", "application/json")
                    .with(jsonRequestLens of ollamaRequest)
            }
        }
        val jsonResponseLens = Body.auto<ApiResponse>().toLens()

        val response = client(request)

        return jsonResponseLens(response).response
    }

}