package org.example

import navigation.ChromeDriverExtension
import navigation.SiteScraper
import navigation.scrapingController
import org.example.errorHandler.ErrorHandler
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.PromptBuilder


const val WEBSITE = "https://www.trivago.pt/pt?themeId=280&sem_keyword=trivago&sem_creativeid=713963771028&sem_matchtype=e&sem_network=g&sem_device=c&sem_placement=&sem_target=&sem_adposition=&sem_param1=&sem_param2=&sem_campaignid=14643174214&sem_adgroupid=126812097003&sem_targetid=kwd-5593367084&sem_location=9197552&cipc=br&cip=3511900005&gad_source=1&gad_campaignid=14643174214&gbraid=0AAAAAD6-gN_gsU_o75Bni1JFom47p_Nm7&gclid=Cj0KCQjwucDBBhDxARIsANqFdr3ysxx2J3IMnADgh6YNuUXMEReFhcXdjqttN_zg8JzOnhU0yLwkwMAaAgHjEALw_wcB"
const val CSS_FILE = "css selectors.json"


fun main() {
    val siteScraper = SiteScraper()
    val projectFileManager = ProjectFileManager(CSS_FILE)
    val promptBuilder = PromptBuilder()
    val ollamaClient = OllamaHttpClient()
    val errorHandler = ErrorHandler(projectFileManager, ollamaClient, promptBuilder)
    val driver: ChromeDriverExtension = ChromeDriverExtension(null)

    try {
        scrapingController(driver, siteScraper, projectFileManager, errorHandler)
        driver.quit()
    } catch (e: Exception) {
        driver.quit()
        throw e
    }
}


fun promptBuilder(divList: List<String>, prompts: PromptBuilder): List<String> {
    val lists = divList.chunked(20)
    val promptList = lists.map { list ->
        buildString {
            appendLine(prompts)
            list.forEach { div ->
                appendLine(div)
            }
        }
    }
    return promptList
}

/*
fun main(){
    val options = ChromeOptions()
    options.setPageLoadStrategy(PageLoadStrategy.NORMAL)
    val driver: ChromeDriverExtension = ChromeDriverExtension(options)
    //driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_SECONDS)
    driver.get(WEBSITE)
    selectCookies(driver)
    navegatePage1(driver)
    navegatePage2(driver)
    driver.quit()
}*/


/*fun main() {
    val driver: ChromeDriverExtension = ChromeDriverExtension(null)

    try {
        driver.get(WEBSITE)
        selectCookies(driver)
        val rawHtml = driver.pageSource ?: throw Exception("Raw html could not be loaded")

        val doc: Document = Jsoup.parse(rawHtml)
        doc.outputSettings().indentAmount(2).prettyPrint(true)
        val formattedHtml = doc.outerHtml()

        println(formattedHtml)
    } finally {
        driver.quit()
    }
}*/

/*
suspend fun main() {
    val prompts = Prompts()
    val driver: ChromeDriverExtension = ChromeDriverExtension(null)
    driver.get(WEBSITE)
    selectCookies(driver)
    val divList = divSplitter(driver)
    driver.close()

    val promptList = promptBuilder(divList, prompts)

    val job = promptList.mapIndexed { i, prompt ->
        coroutine.async {
            askModel(prompt, i)
        }
    }

    job.awaitAll()
    client.close()
}*/

/*
fun main() {
    val doc = Jsoup.connect(WEBSITE).get()
    val element = doc.selectFirst("div.tbKdsQ")
    println(element)

    val rawClient = OkHttpClient.Builder()
        .connectTimeout(Duration.ofSeconds(60))
        .readTimeout(Duration.ofSeconds(120))
        .writeTimeout(Duration.ofSeconds(60))
        .build()

    val client = OkHttp(rawClient)

    val jsonLens = Body.auto<RequestBody>().toLens()
    val jsonResponseLens = Body.auto<ApiResponse>().toLens()

    val body = RequestBody("mistral-nemo-css:latest", prompt, false)

    val request = Request(Method.POST, "http://localhost:11434/api/generate")
        .header("Content-Type", "application/json")
        .with(jsonLens of body)

    val response = client(request)
    val parsed: ApiResponse = jsonResponseLens(response)

    println("Status: ${response.status}")
    println("Response Body: \n${parsed.response}")
}*/

