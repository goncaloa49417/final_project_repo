package org.example.errorHandler

import org.example.CssSelectors

class ElementNotFoundByCssSelector(val invalidCssSelector: String): Exception()

class UnableToGenerateWorkingCssSelector(message: String): Exception()