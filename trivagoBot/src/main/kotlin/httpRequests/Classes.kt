package org.example.httpRequests

import kotlinx.serialization.Serializable

@Serializable
data class RequestBody(val model: String, val prompt: String, val stream: Boolean)

@Serializable
data class ApiResponse(
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