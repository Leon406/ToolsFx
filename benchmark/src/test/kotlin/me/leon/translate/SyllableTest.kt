package me.leon.translate

import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.ForkJoinPool
import kotlin.test.Test
import me.leon.SYLLABLE_FILE
import me.leon.VOCABULARY_FILE
import org.jsoup.Jsoup

/**
 * @author Leon
 * @since 2023-07-18 18:06
 * @email deadogone@gmail.com
 */
class SyllableTest {

    val URL_HOW_MANY = "https://www.howmanysyllables.com/syllables/"
    val URL_WORD_HELP = "https://www.wordhelp.com/syllables/english/?q="
    val URL_SYLLABLE_WORD = "https://www.syllablewords.net/syllables-in/"

    @Test
    fun howMany() {
        Jsoup.connect(URL_HOW_MANY + "ruler")
            .proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 7890)))
            .timeout(3000)
            .get()
            .run { println(this.select("#SyllableContentContainer span.Answer_Red")) }
    }

    @Test
    fun syllable() {
        Jsoup.connect(URL_SYLLABLE_WORD + "ruler").timeout(3000).get().run {
            val lis = this.select(".word-info h3")
            println("${lis[0].text().substringBefore(" ")} ${lis[1].text()}")
        }
    }

    @Test
    fun wordHelp() {
        Jsoup.connect(URL_WORD_HELP + "ruler").timeout(5000).get().run {
            val lis = this.select("ul>li")
            if (lis.isNotEmpty()) {
                println(lis[0].select("div>div")[1].text().substringBefore(" "))
                println(lis[1].select("div>div")[1].text())
            }
        }
    }

    @Test
    fun ss() {

        val words =
            VOCABULARY_FILE.readText()
                .lines()
                .filter { it.isNotEmpty() && !it.contains("-") && !it.contains("'") }
                .map { it.split("\t").first() }
                .toSet()
                .also { println(it.size) }
        val nodata = words - SYLLABLE_FILE.readText().lines().map { it.split("\t").first() }.toSet()
        println("${nodata.size} ${words.size}")
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "256")
        println(ForkJoinPool.getCommonPoolParallelism())
        nodata.parallelStream().forEach { word ->
            runCatching { word.syllable() }
                .getOrNull()
                ?.let {
                    val data = "$word\t$it${System.lineSeparator()}"
                    print(data)
                    SYLLABLE_FILE.appendText(data)
                }
        }
    }

    fun String.syllable(): String =
        runCatching {
                Jsoup.connect(URL_SYLLABLE_WORD + this).timeout(3000).get().run {
                    val lis = this.select(".word-info h3")
                    lis[1].text()
                }
            }
            .getOrElse {
                Jsoup.connect(URL_WORD_HELP + this).timeout(3000).get().run {
                    val lis = this.select("ul>li")
                    lis[1].select("div>div")[1].text()
                }
            }
}
