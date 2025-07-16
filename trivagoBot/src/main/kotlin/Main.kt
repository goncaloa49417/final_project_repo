package org.example

import navigation.SiteScraper
import navigation.scrapingController
import org.example.errorHandler.ErrorHandler
import org.example.httpRequests.OllamaHttpClient
import org.example.httpRequests.PromptBuilder


const val WEBSITE =
    "https://www.trivago.pt/pt?themeId=280&sem_keyword=trivago&sem_creativeid=713963771028&sem_matchtype=e&sem_network=g&sem_device=c&sem_placement=&sem_target=&sem_adposition=&sem_param1=&sem_param2=&sem_campaignid=14643174214&sem_adgroupid=126812097003&sem_targetid=kwd-5593367084&sem_location=9197552&cipc=br&cip=3511900005&gad_source=1&gad_campaignid=14643174214&gbraid=0AAAAAD6-gN_gsU_o75Bni1JFom47p_Nm7&gclid=Cj0KCQjwucDBBhDxARIsANqFdr3ysxx2J3IMnADgh6YNuUXMEReFhcXdjqttN_zg8JzOnhU0yLwkwMAaAgHjEALw_wcB"
const val CSS_FILE = "css selectors.json"

fun main() {
    val siteScraper = SiteScraper()
    val projectFileManager = ProjectFileManager(CSS_FILE)
    val promptBuilder = PromptBuilder()
    val ollamaClient = OllamaHttpClient()
    val errorHandler = ErrorHandler(projectFileManager, ollamaClient, promptBuilder)

    scrapingController(siteScraper, projectFileManager, errorHandler)
}