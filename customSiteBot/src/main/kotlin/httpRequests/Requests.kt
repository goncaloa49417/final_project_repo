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
import kotlinx.serialization.json.Json
import org.http4k.client.JavaHttpClient


val rawClient = OkHttpClient.Builder()
    .connectTimeout(Duration.ofSeconds(600))
    .readTimeout(Duration.ofSeconds(1200))
    .writeTimeout(Duration.ofSeconds(600))
    .build()

val client = OkHttp(rawClient)

val semaphore = Semaphore(2)


fun requestOllama(ollamaRequest: OllamaRequest): String {
    val request = when(ollamaRequest) {
        is RequestBody -> {
            val jsonRequestLens = Body.auto<RequestBody>().toLens()
            Request(Method.POST, "http://localhost:11434/api/generate")
                .header("Content-Type", "application/json")
                .with(jsonRequestLens of ollamaRequest)
        }
        is RequestBodyFormat -> {
            val jsonRequestLens = Body.auto<RequestBodyFormat>().toLens()
            Request(Method.POST, "http://localhost:11434/api/generate")
                .header("Content-Type", "application/json")
                .with(jsonRequestLens of ollamaRequest)
        }
    }
    val jsonResponseLens = Body.auto<ApiResponse>().toLens()

    val response = client(request)
    return jsonResponseLens(response).response
}

suspend fun askModel(prompt: String, idx: Int) =
    semaphore.withPermit {
        try {
            println("Processing... $idx")
            val res = requestOllama(RequestBody("mistral-nemo:latest", "What is time?", false))
            println("Result($idx):\n$res")
        } catch (e: Exception) {
            println("Error: ${e.message}")
            "Error: ${e.message}"
        }
    }