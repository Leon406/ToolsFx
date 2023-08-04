package me.leon.translate

import me.leon.ext.*
import org.jsoup.Jsoup

/**
 * @author Leon
 * @since 2023-07-21 14:41
 * @email deadogone@gmail.com
 */
const val ICBA = "https://www.iciba.com/word?w=%s"
const val BING = "https://cn.bing.com/dict/search?q=%s"

/** refer https://github.com/Haleclipse/-Api/blob/master/YoudaoDic.md */
const val YOUDAO = "https://dict.youdao.com/jsonapi?xmlVersion=5.1&jsonversion=2&q=%s"
const val CAMBRIDGE = "https://dictionary.cambridge.org/dictionary/english-chinese-simplified/%s"

val REG_ICBA = """"wordInfo":([^<]+?),"history"""".toRegex()

fun String.combineTranslate(): String {
    return runCatching {
            val youdaoSimple = youdaoSimple()
            youdaoSimple.ifEmpty { bingTranslate() }
        }
        .getOrDefault("")
}

/** 只显示原型翻译,有网络翻译 */
fun String.bingTranslate(): String {
    Jsoup.connect(BING.format(this)).timeout(DEFAULT_TIME_OUT).get().run {
        // 意思
        val pos = select("li>span.pos").map { it.text() }
        val def = select("li>span.def").map { it.text() }
        return pos.zip(def).joinToString("; ") { it.first + " " + it.second }
    }
}

fun String.bingDetail(): String {
    Jsoup.connect(BING.format(this)).get().run {
        val headWord = selectFirst("#headword")?.text()
        val pronounceUs = selectFirst(".hd_prUS.b_primtxt")?.text().orEmpty()
        val pronounceUk = selectFirst(".hd_pr.b_primtxt")?.text().orEmpty()

        // 意思
        val pos = select("li>span.pos").map { it.text() }
        val def = select("li>span.def").map { it.text() }
        val meanings =
            pos.zip(def).joinToString(System.lineSeparator()) {
                "${it.first.center(6)} \t ${it.second}"
            }

        return buildString {
            append(headWord)
            append("\t")
            append(pos.zip(def).joinToString("; ") { it.first + " " + it.second })

            append(headWord)
            append("\t ")
            append(pronounceUs).append(" ")
            append(pronounceUk)
            append("\n\n")
            append(meanings)
            append("\n\n")
            // 变形
            append(select(".hd_if").joinToString("\t") { it.text() })
            // 搭配
            append("\n\n")
            append(select(".df_div2").joinToString("\n") { it.text() })
        }
    }
}

fun String.youdaoSimple() =
    YOUDAO.format(this)
        .readFromNet(timeout = DEFAULT_TIME_OUT)
        .fromJson(YouDaoResponse::class.java)
        .simple()
