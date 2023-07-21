package me.leon.translate

import java.net.InetSocketAddress
import java.net.Proxy
import org.jsoup.Jsoup

/**
 * @author Leon
 * @since 2023-07-19 17:13
 * @email deadogone@gmail.com
 */
const val URL_HOW_MANY = "https://www.howmanysyllables.com/syllables/"
const val URL_WORD_HELP = "https://www.wordhelp.com/syllables/english/?q="
const val URL_SYLLABLE_WORD = "https://www.syllablewords.net/syllables-in/"
const val URL_LDOCE = "https://www.ldoceonline.com/dictionary/"
const val DEFAULT_TIME_OUT = 3000

fun String.syllableHowMany(timeout: Int = DEFAULT_TIME_OUT) =
    Jsoup.connect(URL_HOW_MANY + this)
        .proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 7890)))
        .timeout(timeout)
        .get()
        .selectFirst("#SyllableContentContainer span.Answer_Red")
        ?.text()
        .orEmpty()

fun String.syllableWord(timeout: Int = DEFAULT_TIME_OUT) =
    Jsoup.connect(URL_SYLLABLE_WORD + this)
        .timeout(timeout)
        .get()
        .select(".word-info h3")[1]
        .text()
        .orEmpty()

/** 支持第三人称,复数 */
fun String.syllableWordHelp(timeout: Int = DEFAULT_TIME_OUT) =
    Jsoup.connect(URL_WORD_HELP + this)
        .timeout(timeout)
        .get()
        .select("ul>li")[1]
        .select("div>div")[1]
        .text()
        .takeIf { it.replace("-", "") == this }
        .orEmpty()

/** 不支持第三人称,复数 */
fun String.syllableLdoce(timeout: Int = DEFAULT_TIME_OUT) =
    Jsoup.connect(URL_LDOCE + this)
        .timeout(timeout)
        .get()
        .select(".HYPHENATION")
        .firstOrNull { it.text().contains(".") }
        ?.text()
        ?.lowercase()
        ?.replace("‧", "-")
        ?.replace("·", "-")
        .orEmpty()
