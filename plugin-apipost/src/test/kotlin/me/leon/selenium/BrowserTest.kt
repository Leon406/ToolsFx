package me.leon.selenium

import kotlin.test.Ignore
import org.junit.Test
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

/**
 * @author Leon
 * @since 2023-04-06 15:08
 * @email deadogone@gmail.com
 */
class BrowserTest {

    @Test
    @Ignore
    fun bogus() {
        println(System.getProperty("webdriver.chrome.driver"))
        println(Selenium.makeXBogus("aaa", "bbb"))
    }
}

fun main() {
    //            System.setProperty("webdriver.chrome.driver",
    // "E:\\driver\\chromedriver.exe")
    //            System.setProperty("webdriver.chrome.bin",
    // "E:/software/Lily5/soft/Chrome/App/chrome.exe")
    val options =
        ChromeOptions().apply {
            addArguments("--no-sandbox")
            addArguments("--disable-dev-shm-usage")
            addArguments("--headless")
        }
    val js = BrowserTest::class.java.getResourceAsStream("/X-Bogus.js").reader().readText()

    val driver = ChromeDriver(options)
    println(driver.executeScript("return navigator.userAgent;"))
    driver.executeScript(js)
    println("~~~~~")
    println(driver.executeScript("return sign('aaa','bbb')"))
    driver.close()
    // chromedriver服务地址WebDriver driver = new ChromeDriver(); // 新建一个WebDriver 的对象，但是new
    // 的是谷歌的驱动String url = "http://www.baidu.com";driver.get(url); //
    // 打开指定的网站driver.navigate().to(url); // 打开指定的网站
}
