package org.example.errorHandler

import org.example.FileManager
import org.example.httpRequests.Prompts

class ElementNotFoundByCssSelector(message: String): Exception(message)

fun errorHandler(e: ElementNotFoundByCssSelector, fileManager: FileManager) {
    val p = Prompts()
    val c = fileManager.extractCssCase(e.message!!)
    val pr = p.populateCssTemplate(c.element, c.cssSelector, c.description, "aaaaa")
}