package me.leon.selenium

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

/**
 * 安装浏览器及对应driver http://chromedriver.storage.googleapis.com/index.html
 *
 * @author Leon
 * @since 2023-04-06 16:07
 * @email deadogone@gmail.com
 */
object Selenium {
    var driver: ChromeDriver

    init {
        //        System.setProperty("webdriver.chrome.driver", "E:\\driver\\chromedriver.exe")
        //        System.setProperty("webdriver.chrome.bin",
        // "E:/software/Lily5/soft/Chrome/App/chrome.exe")
        println(System.getProperty("webdriver.chrome.driver"))
        val options =
            ChromeOptions().apply {
                addArguments("--no-sandbox")
                addArguments("--disable-dev-shm-usage")
                //            addArguments("--headless")
            }
        val js = Selenium.javaClass.getResourceAsStream("/X-Bogus.js").reader().readText()
        driver = ChromeDriver(options)
        driver.executeScript(js)
    }

    fun makeXBogus(query: String, userAgent: String): Any =
        driver.executeScript("return sign('$query','$userAgent')")
}
