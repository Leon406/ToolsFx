package me.leon.translate

import java.net.InetSocketAddress
import java.net.Proxy
import kotlin.test.Test
import org.jsoup.Jsoup

/**
 * @author Leon
 * @since 2023-07-18 18:06
 * @email deadogone@gmail.com
 */
class SyllableTest {

    val URL_HOW_MANY = "https://www.howmanysyllables.com/syllables/"
    val URL_WORD_HELP = "https://www.wordhelp.com/syllables/english/?q="

    @Test
    fun howMany() {
        Jsoup.connect(URL_HOW_MANY + "ruler")
            .proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 7890)))
            .get()
            .run { println(this.select("#SyllableContentContainer.Answer_Red")) }
    }

    @Test
    fun wordHelp() {
        Jsoup.connect(URL_WORD_HELP + "ruler").get().run {
            val lis = this.select("ul>li")
            println(lis.size)
            if (lis.isNotEmpty()) {
                println(lis[0].select("div>div")[1].text())
                println(lis[1].select("div>div")[1].text())
            }
        }
    }
}
