package org.example.errorHandler

class ElementNotFoundByCssSelector(val invalidCssSelector: String): Exception()

class UnableToGenerateWorkingCssSelector(message: String): Exception()