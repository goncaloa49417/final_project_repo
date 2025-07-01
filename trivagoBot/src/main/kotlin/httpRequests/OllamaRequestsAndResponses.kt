package org.example.httpRequests

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject


sealed class OllamaRequest

@Serializable
data class OllamaRequestBody(val model: String, val prompt: String, val stream: Boolean): OllamaRequest()

@Serializable
data class OllamaRequestBodyFormat(val model: String, val prompt: String, val format: JsonObject, val stream: Boolean): OllamaRequest()

@Serializable
data class Message(val role: String, val content: String)

@Serializable
data class Options(val temperature: Double)

@Serializable
data class OllamaChatRequest(
    val model: String,
    val messages: List<Message>,
    val options: Options,
    val stream: Boolean
): OllamaRequest() {

    companion object {
        fun build(model: String, messages: List<Message>, prompt: String): OllamaChatRequest =
            OllamaChatRequest(
                model,
                messages + Message(role = "user", content = prompt),
                Options(0.2),
                false
            )
    }

}

@Serializable
data class DivResp(val div_element: String, val div_css_selector: String)

@Serializable
data class CssResp(
    val old_element: String,
    val old_css_selector: String,
    val old_description: String,
    val new_element: String,
    val new_css_selector: String,
    val new_description: String
)

@Serializable
data class ApiGeneratedResponse(
    val model: String,
    val created_at: String,
    val response: String,
    val done: Boolean,
    val done_reason: String,
    val context: List<Int>,
    val total_duration: Long,
    val load_duration: Long,
    val prompt_eval_count: Int,
    val prompt_eval_duration: Long,
    val eval_count: Int,
    val eval_duration: Long
)

@Serializable
data class ApiChatResponse(
    val model: String,
    val created_at: String,
    val message: Message,
    val done: Boolean,
    val done_reason: String,
    val total_duration: Long,
    val load_duration: Long,
    val prompt_eval_count: Int,
    val prompt_eval_duration: Long,
    val eval_count: Int,
    val eval_duration: Long
)