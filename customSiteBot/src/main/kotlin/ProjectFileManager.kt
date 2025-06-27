package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.util.Properties

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
    val description: String,
    val failureCount: Int
)


class ProjectFileManager(private val cssFile: String): FileManager {

    private val json = Json {
        prettyPrint = true
    }

    override val filePathToSite: String by lazy {
        val props = Properties()
        props.load(FileInputStream("config.properties"))
        props.getProperty("html.path")
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
            ?: throw Exception("$cssSelector not found in \"$cssFile\" file")
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

    override fun getPathFromFile(): String {
        val props = Properties()
        props.load(FileInputStream("config.properties"))

        return props.getProperty("html.path")
    }

}