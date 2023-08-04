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
class TranslateTest {
    val word = "annexing"
    val noMeaning = "$ONE_DRIVE_DIR/outOfDict.txt".toFile()
    val nodata =
        "$DESKTOP/nodata.txt".toFile().also {
            if (it.exists()) {
                it.createNewFile()
            }
        }
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
        readData().forEach {
            //            println(it)
            val mean = it.combineTranslate()
            if (mean.isNotEmpty()) {
                println(mean)
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
