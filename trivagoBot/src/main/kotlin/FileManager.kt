package org.example

interface FileManager {

    fun extractCssSelectors(): CssSelectors

    fun extractCssCase(cssSelector: String): CssCase

    fun editCssFile(oldCssSelector: String, updatedCase: CssCase)

    fun resetAllFailureCounters()

}