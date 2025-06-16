package org.example.httpRequests

import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class PromptBuilder {

    private val pruningTemplate = """
        **Your task is to identify interactive elements, defined as:**
        **- Native interactive HTML tags: <button>, <a>, <input>, <select>, <textarea>, <form>**
        **- Non-semantic tags only if they contain an explicit** `onclick` **attribute or** `role="button"`**,** `role="link"` **attribute.**
        **- Elements with user-triggerable behavior**
        **- take special attention to interactives div example: <div role ="button"/>**  
        —-
        **### Example 1:**
        **<html>**
          **<a href="/home" title="Home">Home</a>**
          **<a href="/other" title="other">other</a>**
        **</html>**
        **Answer:**
        **2  interactive elements found: 2 <a>**  
        **### Example 2:**
        **<html>**
          **<form action="/search">**
            **<input type="text" name="q" />**
            **<button type="submit">Go</button>**
          **</form>**
        **</html>**
        **Answer:**
        **3 interactive elements found:1 <form>,1 <input>,1 <button>**  
        **### Example 3:**
        **<html>**
          **<div>No interactive elements here</div>**
        **</html>**
        **Answer:**
        **0 interactive elements found.**  
        **### Example 4:**
        **<html>**
        **<li class="_1dRr6B" role="option" aria-selected="false"><div data-testid="ssg-element" class="_7hpW3r"></li></html>**
        **Answer:**
        **1  interactive element found: 1 <li>**
        **Identify also interactive div that have roles**  
        —-
        **Now analyze this HTML:**
        %HTML_SNIPPET%
    """.trimIndent()

    private val cssTemplate = """
        ###Context###  
        The html element before web page update:  
        %ELEMENT%  
        The css selector "%SELECTOR%" stopped working.  
        %DESCRIPTION%  
        ###Updated Html Code###  
        %HTML_SNIPPET%
        ###Answer Format###  
        Old CSS selector: (content)  
        New CSS selector: (content)
    """.trimIndent()

    val cssFormat = buildJsonObject {
        put("type", "object")
        putJsonObject("properties") {
            putJsonObject("old_css_selector") {
                put("type", "string")
            }
            putJsonObject("new_css_selector") {
                put("type", "string")
            }
            putJsonObject("new_description_of_element") {
                put("type", "string")
            }
        }
        putJsonArray("required") {
            add("old_css_selector")
            add("new_css_selector")
            add("new_description_of_element")
        }
    }

    fun populatePruningTemplate(htmlSnippet: String): String =
        pruningTemplate.replace("%HTML_SNIPPET%", htmlSnippet)

    fun populateCssTemplate(
        element: String,
        cssSelector: String,
        description: String,
        htmlSnippet: String
    ): String =
        cssTemplate
            .replace("%ELEMENT%", element)
            .replace("%SELECTOR%", cssSelector)
            .replace("%DESCRIPTION%", description)
            .replace("%HTML_SNIPPET%", htmlSnippet)


}