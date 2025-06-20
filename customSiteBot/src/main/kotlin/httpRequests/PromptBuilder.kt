package org.example.httpRequests

import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class PromptBuilder {

    private val pruningTemplate = """
        **Your task is to prune the following HTML snippet by identifying  and extracting all interactive elements so that the next model can use your answer to create css selectors in order to access them.**
        Here are some guide lines to help you find the interactive elements:
          - Native interactive HTML tags: <button>, <a>, <input>, <select>, <textarea>, <form>**
          - Non-semantic tags only if they contain an explicit `onclick` **attribute or** `role="button"`, `role="link"` attribute.
          - Elements with user-triggerable behavior
          - take special attention to interactives div example: <div role ="button"/>**  
        * * *
        
        **Now analyze the HTML snippet:**
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
        Old Element: (content)
        Old CSS selector: (content)
        Old Description: (content)
        New element: (content)
        New CSS selector: (content)
        New Description: (content)
    """.trimIndent()

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