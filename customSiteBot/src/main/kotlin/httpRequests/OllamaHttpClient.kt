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
): HttpClient {

    override fun request(ollamaRequest: OllamaRequestBodyFormat): String {
        val response = getResponse(ollamaRequest)
        val jsonResponseLens = Body.auto<ApiGeneratedResponse>().toLens()

        return jsonResponseLens(response).response
    }

    private fun getResponse(body: OllamaRequestBodyFormat): Response {
        val jsonRequestLens = Body.auto<OllamaRequestBodyFormat>().toLens()
        val request = Request(Method.POST, "http://localhost:11434/api/generate")
            .header("Content-Type", "application/json")
            .with(jsonRequestLens of body)

        return client(request)
    }

}