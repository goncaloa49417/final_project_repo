package org.example.httpRequests


class PromptBuilder {

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