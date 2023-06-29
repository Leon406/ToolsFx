package me.leon.misc

import IcibaVocabulary
import kotlin.test.Test
import me.leon.ext.*
import org.jsoup.Jsoup

/**
 * @author Leon
 * @since 2023-06-28 18:10
 * @email deadogone@gmail.com
 */
const val ICBA = "https://www.iciba.com/word?w=%s"
const val BING = "https://cn.bing.com/dict/search?q=%s"
const val YOUDAO = "https://dict.youdao.com/jsonapi?xmlVersion=5.1&jsonversion=2&q=%s"
const val CAMBRIDGE = "https://dictionary.cambridge.org/dictionary/english-chinese-simplified/%s"

val REG_ICBA = """"wordInfo":([^<]+?),"history"""".toRegex()

class TranslateTest {
    val word = "do"

    @Test
    fun icba() {
        ICBA.format(word).readFromNet().run {
            REG_ICBA.find(this)?.groupValues?.get(1)?.run {
                fromJson(IcibaVocabulary::class.java).also { println(it) }
            }
        }
    }

    @Test
    fun bing() {
        Jsoup.connect(BING.format(word)).get().run {
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

            buildString {
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
                .also { println(it) }
        }
    }

    @Test
    fun youdao() {
        YOUDAO.format("123").readFromNet().also { println(it.fromJson(YouDaoResponse::class.java)) }
    }

    @Test
    fun cambridge() {
        Jsoup.connect(CAMBRIDGE.format("super")).get().run {
            val headWord = selectFirst(".headword")?.text()
            val pronounceUs =
                select(".us>.pron").distinctBy { it.text() }.joinToString(" ") { it.text() }
            val pronounceUk =
                select(".uk>.pron").distinctBy { it.text() }.joinToString(" ") { it.text() }

            buildString {
                    append(headWord)
                    if (pronounceUs.isNotEmpty()) {
                        append("\t US:")
                        append(pronounceUs)
                    }
                    if (pronounceUk.isNotEmpty()) {
                        append(" UK: ")
                        append(pronounceUk)
                    }
                    appendLine()
                    selectFirst(".irreg-infls.dinfls")?.text()?.run {
                        append(selectFirst(".irreg-infls.dinfls")?.text())
                        appendLine()
                    }
                    append(
                        select(".def-body>.trans.dtrans.break-cj").joinToString("\t") { it.text() }
                    )
                }
                .also { println(it) }
        }
    }
}
