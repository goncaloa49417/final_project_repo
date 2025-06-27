package org.example.httpRequests

import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject


object ModelAnswerSchemas {

    val cssFormat = buildJsonObject {
        put("type", "object")
        putJsonObject("properties") {
            putJsonObject("old_element") {
                put("type", "string")
            }
            putJsonObject("old_css_selector") {
                put("type", "string")
            }
            putJsonObject("old_description") {
                put("type", "string")
            }
            putJsonObject("new_element") {
                put("type", "string")
            }
            putJsonObject("new_css_selector") {
                put("type", "string")
            }
            putJsonObject("new_description") {
                put("type", "string")
            }
        }
        putJsonArray("required") {
            add("old_element")
            add("old_css_selector")
            add("old_description")
            add("new_element")
            add("new_css_selector")
            add("new_description")
        }
    }

    val divSearchFormat = buildJsonObject {
        put("type", "object")
        putJsonObject("properties") {
            putJsonObject("div_element") {
                put("type", "string")
            }
            putJsonObject("div_css_selector") {
                put("type", "string")
            }
        }
        putJsonArray("required") {
            add("div_element")
            add("div_css_selector")
        }
    }

}