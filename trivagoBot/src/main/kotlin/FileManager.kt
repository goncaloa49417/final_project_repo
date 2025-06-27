package org.example

interface FileManager {

    val filePathToSite: String

    fun extractCssSelectors(): CssSelectors

    fun extractCssCase(cssSelector: String): CssCase

    fun editCssFile(oldCssSelector: String, updatedCase: CssCase)

    fun resetAllFailureCounters()

    fun getPathFromFile(): String

}