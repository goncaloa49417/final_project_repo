package org.example.errorHandler

import org.example.CssSelectors

class ElementNotFoundByCssSelector(val invalidCssSelector: String): Exception()

class CssCaseNotFound(val cssSelector: String, val file: String):
    Exception("$cssSelector not found in \"$file\" file")

class UnableToGenerateWorkingCssSelector(message: String): Exception()