package org.example.httpRequests

import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class PromptBuilder {

    private val divClassificationTemplate = """
        Rate the following <div> elements according to their **semantic level** and provide a brief **explanation of their purpose**, if discernible.
        Use this rating scale:
        - **Low**: No semantic information (e.g., random class names or IDs like __next, container, or alphanumeric strings).
        - **Medium**: Some semantic hints (e.g., data-testid or meaningful but ambiguous class names).
        - **High**: Clear intent (e.g., role, aria attributes, or descriptive test IDs or IDs).  
        ---
        **Output Format**:
        1. <div ...> // [rating] – [likely purpose or content]
        Stick strictly to this format. Do not add additional text outside the list.
        ---
        Here are examples:
        1. <div class="_ioPl3_"> // low – likely a styling wrapper with no semantic meaning.  
        2. <div class="_yum1E H7jktv_ xv7Ua"> // low – likely a styling wrapper with no semantic meaning.
        3. <div id="__next"> // low – framework-generated wrapper with no direct content meaning.
        4. <div role="button"> // medium – probably an interactive element like a custom button.
        5. <div data-testid="login-form" class="_4DcEqf"> // high – likely contains a login form.
        ---
        Now rate the following:
        %DIV_LIST%
    """.trimIndent()

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

    fun populateDivClassificationTemplate(list: List<String>) =
        divClassificationTemplate
            .replace("%DIV_LIST%", list.joinToString("\n"))

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