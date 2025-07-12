package org.example.httpRequests

class PromptBuilder {

    private val divClassificationTemplate = "Rate these <div> elements:\n %DIV_LIST%"

    private val parentDivSearchFinal = """
        ###Task###  
        You are a HTML code analyst expert. Given a description of the element and a list of div elements each rated by level of semantic, decide what div would be the parent of the child element and create a css selector to access it using only it's attributes.
        
        ###Context###  
        %DESCRIPTION%
          
        ###Div List###
        %DIV_LIST%
        
        ###Answer Format###  
        Div element: (completed chosen div)  
        Css selector: div(complete the css selector with the most unique attribute to be select by a selenium bot)  
        Stick strictly to this json format. Do not add additional text outside the answer format.
    """.trimIndent()

    private val parentDivSearch = """
        ###Task###  
        You are a HTML code analyst expert. Given a description of the element and a list of div elements each rated by level of semantic, decide what div would be the parent of the child element and create a css selector to access it using only it's attributes.
        
        ###Context###  
        %DESCRIPTION%
          
        ###Div List###
        %DIV_LIST%
        
        ###Answer Format###  
        div_element: <div (rest of the chosen div)> //(the given classification and explanation)
        
        Stick strictly to this json format. Do not add additional text outside the answer format.
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
        Old Element: (complete element content)
        Old CSS selector: (content)
        Old Description: (content)
        New element: (complete element content)
        New CSS selector: (content)
        New Description: (content)
        
        To create a reliable CSS selector follow these 3 rules:

        1. Use stable attributes like `id`, `class`, `data-*`, `name`, `role` or `href`. Avoid auto-generated class attribute.
        2. Avoid using positional selectors like `:nth-child()` and `:(ordinal number)-child`.
        3. Avoid universal or overly broad selectors like `div span a`. Be precise and scoped.
    """.trimIndent()

    fun populateDivClassificationTemplate(list: List<String>) =
        divClassificationTemplate
            .replace("%DIV_LIST%", list.joinToString("\n"))

    fun populateParentDivSearch(htmlSnippet: String, description: String): String =
        parentDivSearch
            .replace("%DIV_LIST%", htmlSnippet)
            .replace("%DESCRIPTION%", description)

    fun populateParentDivSearchFinal(list: List<String>, description: String): String =
        parentDivSearchFinal
            .replace("%DIV_LIST%", list.joinToString("\n"))
            .replace("%DESCRIPTION%", description)

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