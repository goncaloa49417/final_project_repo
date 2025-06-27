package testClasses

import org.example.httpRequests.HttpClient
import org.example.httpRequests.OllamaRequest

class FakeHttpClient(): HttpClient {
    override fun request(ollamaRequest: OllamaRequest): String {
        return """
          "old_element": "<>",
          "old_css_selector": "div.abcd",
          "old_description": "An element",
          "new_element": "<>",
          "new_css_selector": "div.efgh",
          "new_description": "An element"
        """.trimIndent()
    }
}