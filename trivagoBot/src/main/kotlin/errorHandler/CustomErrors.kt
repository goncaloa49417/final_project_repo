package org.example.errorHandler

class ElementNotFoundByCssSelector(val invalidCssSelector: String): Exception()

class CssCaseNotFound(val cssSelector: String, val file: String):
    Exception("$cssSelector not found in \"$file\" file")

class UnableToGenerateWorkingCssSelector(message: String): Exception()