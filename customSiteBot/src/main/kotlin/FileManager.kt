package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

data class RequiredInformation(val names: String, val prices: String)

data class CssSelectors(
    val categories: String,
    val requiredInformation: RequiredInformation
){
    companion object {
        fun fromList(list: List<String>): CssSelectors {
            require(list.size == 3) { "Expected at least 3 CSS selectors, got ${list.size}" }
            return CssSelectors(
                categories = list[0],
                requiredInformation = RequiredInformation(list[1], list[2])
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

    private fun readJsonFile(): List<CssCase> {
        val fileContent = File(cssFile).readText()
        return Json.decodeFromString(ListSerializer(CssCase.serializer()), fileContent)
    }

    private fun writeJsonFile(cssCases: List<CssCase>) {
        val jsonContent = Json.encodeToString(cssCases)
        File(cssFile).writeText(jsonContent)
    }

    fun extractCssSelectors(): CssSelectors {
        val cssList = readJsonFile().map { it.cssSelector }
        return CssSelectors.fromList(cssList)
    }

    fun extractCssCase(cssSelector: String): CssCase {
        return readJsonFile().find { it.cssSelector == cssSelector }
            ?: throw Exception("$cssSelector not found in \"$cssFile\" file")
    }

    fun editCssFile(oldCssSelector: String, updatedCase: CssCase) {
        val cssCases = readJsonFile().toMutableList()
        val index = cssCases.indexOfFirst { it.cssSelector == oldCssSelector }

        if (index != -1) {
            cssCases[index] = updatedCase
        } else {
            cssCases.add(updatedCase)
        }
        writeJsonFile(cssCases)
    }

}