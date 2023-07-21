package me.leon.translate

import java.io.File
import kotlin.test.Test
import me.leon.DESKTOP
import me.leon.ONE_DRIVE_DIR
import me.leon.ext.*
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
    val noMeaning = "$DESKTOP/nomeaning.txt".toFile()
    val nodata = "$DESKTOP/nodata.txt".toFile()
    val defaultTrans = File(noMeaning.parentFile, "trans.txt")

    @Test
    fun icba() {

        readData().forEach { w ->
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
                                    defaultTrans.appendText("$w\t$mean\n")
                                } else {
                                    nodata.appendText("$w\n")
                                }
                            }
                            //                            println(it.baesInfo?.meanings(" "))
                        }
                    }
                        ?: {
                            println(">>>>>>no result")
                            defaultTrans.appendText("$w\t\n")
                        }
                }
            }
        }
    }

    @Test
    fun bing() {
        println(word.bingDetail())
        readData().forEach { w ->
            with(w.bingTranslate()) {
                if (isNotEmpty()) {
                    println("$w\t$this")
                }
            }
        }
    }

    @Test
    fun youdao() {
        println(word.youdaoSimple())
        println("resumes".youdaoSimple())
        readData().forEach { w ->
            with(w.youdaoSimple()) {
                if (isNotEmpty()) {
                    println("$w\t$this")
                }
            }
        }
    }

    @Test
    fun translate() {
        val toTranslated = "$ONE_DRIVE_DIR/known.txt".toFile()
        readData(toTranslated, nodata).forEach {
            println(it)
            val mean = it.combineTranslate()
            if (mean.isNotEmpty()) {
                defaultTrans.appendText("$it\t$mean${System.lineSeparator()}")
            }
        }
    }

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
    fun combineVocabulary() {
        val text = "$ONE_DRIVE_DIR/vocabulary.txt".toFile().readText()
        val trans = "$ONE_DRIVE_DIR/trans.txt".toFile().readText()
        val output = "$ONE_DRIVE_DIR/vocabulary2.txt".toFile()

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

    private fun readData(
        toTranslate: File = noMeaning,
        noTranslation: File = nodata
    ): List<String> {

        val translatedFile = File(toTranslate.parentFile, "trans.txt")
        val translated =
            runCatching { translatedFile.readText() }
                .getOrNull()
                ?.lines()
                ?.map { it.split("\t")[0] }
                ?: emptyList()
        val lines = toTranslate.readText().lines()
        val exclusion = noTranslation.readText().lines().toSet()
        val diff = lines - translated.toSet() - exclusion
        println("${lines.size} / ${translated.size}/${exclusion.size}   diff: ${diff.size} ")
        return diff
    }
}
