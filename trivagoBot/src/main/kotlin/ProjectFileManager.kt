package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.errorHandler.CssCaseNotFound
import java.io.File
import java.io.FileInputStream
import java.util.Properties


data class CookieSelectors(
    val shadowRoot: String,
    val denyCookies: String
)

data class PageOneSelectors(
    val writingBox: String,
    val confirm: String,
    val date: String,
    val search: String
)

data class PageTwoSelectors(
    val hotelNames: String,
    val hotelPrices: String,
    val nextPage: String
)

data class CssSelectors(
    val cookies: CookieSelectors,
    val pageOne: PageOneSelectors,
    val pageTwo: PageTwoSelectors
) {
    companion object {
        fun fromList(list: List<String>): CssSelectors {
            require(list.size == 9) { "Expected at least 9 CSS selectors, got ${list.size}" }
            return CssSelectors(
                CookieSelectors(list[0], list[1]),
                PageOneSelectors(list[2], list[3], list[4], list[5]),
                PageTwoSelectors(list[6], list[7], list[8])
            )
        }
    }
}


@Serializable
data class CssCase(
    val element: String,
    val cssSelector: String,
    val description: String,
    val failureCount: Int
)


class ProjectFileManager(private val cssFile: String) : FileManager {

    private val json = Json {
        prettyPrint = true
    }

    private fun readJsonFile(): List<CssCase> {
        val fileContent = File(cssFile).readText()
        return Json.decodeFromString(ListSerializer(CssCase.serializer()), fileContent)
    }

    private fun writeJsonFile(cssCases: List<CssCase>) {
        val jsonContent = json.encodeToString(cssCases)
        File(cssFile).writeText(jsonContent)
    }

    override fun extractCssSelectors(): CssSelectors {
        val cssList = readJsonFile().map { it.cssSelector }
        return CssSelectors.fromList(cssList)
    }

    override fun extractCssCase(cssSelector: String): CssCase {
        return readJsonFile().find { it.cssSelector == cssSelector }
            ?: throw CssCaseNotFound(cssSelector, cssFile)
    }

    override fun editCssFile(oldCssSelector: String, updatedCase: CssCase) {
        val cssCases = readJsonFile()

        val updatedCssCases = cssCases.map { cssCase ->
            if (cssCase.cssSelector == oldCssSelector) updatedCase else cssCase
        }

        writeJsonFile(updatedCssCases)
    }

    override fun resetAllFailureCounters() {
        val cssCases = readJsonFile().map { case ->
            case.copy(failureCount = 0)
        }

        writeJsonFile(cssCases)
    }

}