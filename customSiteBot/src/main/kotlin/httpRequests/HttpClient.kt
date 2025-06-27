package org.example.httpRequests

interface HttpClient {

    fun request(ollamaRequest: OllamaRequest): String

}