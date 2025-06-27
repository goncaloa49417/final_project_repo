package testClasses

import org.example.CssCase
import org.example.CssSelectors
import org.example.FileManager

class StubErrorFileManager(): FileManager {

    override val filePathToSite: String = ""

    override fun extractCssSelectors(): CssSelectors {
        TODO("Not yet implemented")
    }

    override fun extractCssCase(cssSelector: String): CssCase {
        return CssCase(
            "<>",
            cssSelector,
            "An element",
            3
        )
    }

    override fun editCssFile(oldCssSelector: String, updatedCase: CssCase) {
        TODO("Not yet implemented")
    }

    override fun resetAllFailureCounters() {
        TODO("Not yet implemented")
    }

    override fun getPathFromFile(): String {
        TODO("Not yet implemented")
    }

}



class StubFileManager(): FileManager {

    val a = mapOf<String, Pair<String, String>>()

    override val filePathToSite: String = ""

    override fun extractCssSelectors(): CssSelectors {
        TODO("Not yet implemented")
    }

    override fun extractCssCase(cssSelector: String): CssCase {
        return CssCase(
            "<>",
            cssSelector,
            "An element",
            0
        )
    }

    override fun editCssFile(oldCssSelector: String, updatedCase: CssCase) {

    }

    override fun resetAllFailureCounters() {
        TODO("Not yet implemented")
    }

    override fun getPathFromFile(): String {
        TODO("Not yet implemented")
    }

}