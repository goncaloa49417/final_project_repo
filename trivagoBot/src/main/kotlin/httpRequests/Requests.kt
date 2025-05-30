package org.example.httpRequests

import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.format.KotlinxSerialization.auto
import okhttp3.OkHttpClient
import java.time.Duration
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit



val rawClient = OkHttpClient.Builder()
    .connectTimeout(Duration.ofSeconds(600))
    .readTimeout(Duration.ofSeconds(1200))
    .writeTimeout(Duration.ofSeconds(600))
    .build()

val client = OkHttp(rawClient)

val semaphore = Semaphore(2)


suspend fun requestOllama(prompt: String): String {
    val jsonRequestLens = Body.auto<RequestBody>().toLens()
    val jsonResponseLens = Body.auto<ApiResponse>().toLens()

    val body = RequestBody("mistral-nemo:latest", prompt, false)

    val request = Request(Method.POST, "http://localhost:11434/api/generate")
        .header("Content-Type", "application/json")
        .with(jsonRequestLens of body)

    val response = client(request)
    return jsonResponseLens(response).response
}

suspend fun askModel(prompt: String, idx: Int) =
    semaphore.withPermit {
        try {
            println("Processing... $idx")
            val res = requestOllama(prompt)
            println("Result($idx):\n$res")
        } catch (e: Exception) {
            println("Error: ${e.message}")
            "Error: ${e.message}"
        }
    }