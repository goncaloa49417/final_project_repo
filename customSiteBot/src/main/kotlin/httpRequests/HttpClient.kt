package org.example.httpRequests

interface HttpClient {

    fun request(ollamaRequest: OllamaRequestBodyFormat): String

}