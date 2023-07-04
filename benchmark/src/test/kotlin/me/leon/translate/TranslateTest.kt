package me.leon.translate

import java.io.File
import kotlin.test.Test
import me.leon.ext.*
import me.leon.onedriveDir
import org.jsoup.Jsoup

/**
 * @author Leon
 * @since 2023-06-28 18:10
 * @email deadogone@gmail.com
 */
const val ICBA = "https://www.iciba.com/word?w=%s"
const val BING = "https://cn.bing.com/dict/search?q=%s"

/** refer https://github.com/Haleclipse/-Api/blob/master/YoudaoDic.md */
const val YOUDAO = "https://dict.youdao.com/jsonapi?xmlVersion=5.1&jsonversion=2&q=%s"
const val CAMBRIDGE = "https://dictionary.cambridge.org/dictionary/english-chinese-simplified/%s"

val REG_ICBA = """"wordInfo":([^<]+?),"history"""".toRegex()

class TranslateTest {
    val word = "do"

    @Test
    fun icba() {
        val file = "C:\\Users\\Leon\\Desktop\\rr2.txt".toFile()
        val nodata = "C:\\Users\\Leon\\Desktop\\nodata.txt".toFile()
        val exclusion = nodata.readText().lines().toSet()
        val trans = File(file.parentFile, "trans.txt")

        val translated =
            runCatching { trans.readText() }.getOrNull()?.lines()?.map { it.split("\t")[0] }
                ?: emptyList()
        val lines = file.readText().lines()
        val diff = lines - translated - exclusion
        println("${lines.size} / ${translated.size}/${exclusion.size}   diff: ${diff.size} ")
        diff.forEach { w ->
            println(w)
            ICBA.format(w).readFromNet().run {
                // 网站限制访问qps
                Thread.sleep(500)
                if (isEmpty()) {
                    println("\n~~~")
                    Thread.sleep(1000)
                } else {
                    REG_ICBA.find(this)?.groupValues?.get(1)?.run {
                        fromJson(IcibaVocabulary::class.java).also {
                            it.baesInfo?.run {
                                val mean = meanings(" ")
                                if (!mean.isNullOrEmpty()) {
                                    println(">>>>>>got<<<<<<")
                                    trans.appendText("$w\t$mean\n")
                                } else {
                                    nodata.appendText("$w\n")
                                }
                            }
                            //                            println(it.baesInfo?.meanings(" "))
                        }
                    }
                        ?: {
                            println(">>>>>>no result")
                            trans.appendText("$w\t\n")
                        }
                }
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
                        .append("\t")
                        .append(pos.zip(def).joinToString("; ") { it.first + " " + it.second })
                }
                .also { println(it) }

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
        YOUDAO.format("resumes").readFromNet().also {
            println(it.fromJson(YouDaoResponse::class.java).simple())
        }
    }

    @Test
    fun translate() {

        val toTranslated = "$onedriveDir/known.txt".toFile()
        val trans = File(toTranslated.parentFile, "trans.txt")
        val translated =
            runCatching { trans.readText() }.getOrNull()?.lines()?.map { it.split("\t")[0] }
                ?: emptyList()
        val lines = toTranslated.readText().lines()
        val diff = lines - translated
        println("${lines.size} / ${translated.size}  diff: ${diff.size} ")
        diff.forEach { trans.appendText(translateSimple(it) + System.lineSeparator()) }

        //        println(translateSimple("amazes"))
        //        println(translateSimple("abandon"))
        //        println(translateSimple("bothering"))
    }

    private fun translateSimple(text: String) =
        //  显示变形翻译
        YOUDAO.format(text)
            .readFromNet()
            .also {
                //            println(it)
            }
            .fromJson(YouDaoResponse::class.java)
            .simple()
    //  显示原形翻译
    //        Jsoup.connect(BING.format(text)).get().run {
    //            val headWord = selectFirst("#headword")?.text()
    //            // 意思
    //            val pos = select("li>span.pos").map { it.text() }
    //            val def = select("li>span.def").map { it.text() }
    //            buildString {
    //                append(headWord).append("\t").append(pos.zip(def).joinToString("; ") {
    // it.first + " " + it.second })
    //            }
    //        }.also {
    //            println(it)
    //        }
    //    }

    @Test
    fun cambridge() {
        Jsoup.connect(CAMBRIDGE.format(word)).get().run {
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

    @Test
    fun vocabulary() {
        val onedriveDir = "E:\\OneDrive\\我的文档\\学习"
        val text = "$onedriveDir/vocabulary.txt".toFile().readText()
        val trans = "$onedriveDir/trans.txt".toFile().readText()
        val output = "$onedriveDir/vocabulary2.txt".toFile()

        val sets = mutableSetOf<Pair<String, String>>()
        println(text.lines().size)
        val groups =
            (text + trans)
                .split(System.lineSeparator())
                .toSet()
                .filter { it.isNotEmpty() }
                .map { it.split("\t").run { this[0] to this[1] } }
                .groupBy { it.first }

        groups
            .filter { it.value.size == 1 }
            .also {
                println(it.size)
                sets.addAll(it.values.map { it.first() })
            }
        groups
            .filter { it.value.size > 1 }
            .map { it.value.maxBy { it.second.length } }
            .also {
                sets.addAll(it)
                println(it.size)
            }

        println(sets.size)
        output.writeText(
            sets
                .sortedBy { it.first.lowercase() }
                .joinToString(System.lineSeparator()) { "${it.first}\t${it.second}" }
        )
    }
}
