package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths


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
){
    companion object {
        fun fromList(list: List<String>): CssSelectors {
            require(list.size >= 9) { "Expected at least 9 CSS selectors, got ${list.size}" }
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
    val description: String
)


class FileManager(private val cssFile: String) {

    //private val cssFile = "css selectors.json"

    private fun readJsonFile(): List<CssCase> {
        val fileContent = File(cssFile).readText()
        return Json.decodeFromString(ListSerializer(CssCase.serializer()), fileContent)
    }

    fun extractCssSelectors(): CssSelectors {
        val cssList = readJsonFile().map { it.cssSelector }
        return CssSelectors.fromList(cssList)
    }

    fun extractCssCase(cssSelector: String): CssCase {
        return readJsonFile().find { it.cssSelector == cssSelector }
            ?: throw Exception("$cssSelector not found in \"$cssFile\" file")
    }

}