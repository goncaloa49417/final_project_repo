package org.example.navegation

import org.example.CookieSelectors
import org.example.PageOneSelectors
import org.example.PageTwoSelectors
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.WebDriverWait
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface Scraper {

    fun selectCookies(driver: ChromeDriverExtension, cookie: CookieSelectors)

    fun navigatePage1(driver: ChromeDriverExtension, pageOne: PageOneSelectors)

    fun navigatePage2(driver: ChromeDriverExtension, pageTwo: PageTwoSelectors)

}