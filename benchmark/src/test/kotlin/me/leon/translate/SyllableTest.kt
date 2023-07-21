package me.leon.translate

import java.util.concurrent.ForkJoinPool
import kotlin.test.Test
import kotlin.test.assertEquals
import me.leon.*
import me.leon.ext.toFile

/**
 * @author Leon
 * @since 2023-07-18 18:06
 * @email deadogone@gmail.com
 */
class SyllableTest {

    private val forkJoinPool = ForkJoinPool(128)
    private val SYLLABLE2 =
        "$ONE_DRIVE_DIR/syllable2.txt".toFile().also {
            if (!it.exists()) {
                it.createNewFile()
            }
        }

    @Test
    fun howMany() {
        assertEquals("rul-er", "ruler".syllableHowMany())
    }

    @Test
    fun syllable() {
        assertEquals("rul-er", "ruler".syllableWord())
    }

    @Test
    fun wordHelp() {
        assertEquals("rul-er", "ruler".syllableWordHelp())
        println("languages".syllableWordHelp())
    }

    @Test
    fun ldoce() {
        assertEquals("rul-er", "ruler".syllableLdoce())
    }

    @Test
    fun ss() {
        val words =
            VOCABULARY_FILE2.readText()
                .lines()
                .filter { it.isNotEmpty() && !it.contains("-") && !it.contains("'") }
                .map { it.split("\t").first() }
                .toSet()
                .also { println(it.size) }
        val nodata =
            words -
                SYLLABLE_FILE.readText().lines().map { it.split("\t").first() }.toSet() -
                SYLLABLE_NO_DATA_FILE.readText().lines().toSet()
        println("${nodata.size}/${words.size}")
        forkJoinPool
            .submit {
                nodata.parallelStream().forEach { word ->
                    runCatching { word.syllable() }
                        .getOrNull()
                        ?.let {
                            if (it.isEmpty()) {
                                SYLLABLE_NO_DATA_FILE.appendText("$word{System.lineSeparator()}")
                            }
                            val data = "$word\t$it${System.lineSeparator()}"
                            print(data)
                            SYLLABLE_FILE.appendText(data)
                        }
                }
            }
            .get()
    }

    @Test
    fun noData() {
        val nodataOri =
            SYLLABLE_FILE.readText().lines().emptyData().also {
                SYLLABLE_NO_DATA_FILE.writeText(it.joinToString(System.lineSeparator()))
            }
        println(nodataOri.size)
        var nodata = SYLLABLE_NO_DATA_FILE.readText().lines().toSet()
        repeat(10) {
            forkJoinPool
                .submit {
                    nodata =
                        (nodata -
                            SYLLABLE2.readText().lines().notEmptyData().map { it.first() }.toSet())
                    println("nodata ${nodata.size}")
                    nodata.parallelStream().forEach { word ->
                        runCatching { word.syllable() }
                            .getOrNull()
                            ?.let {
                                //                            println(word)
                                if (it.isNotEmpty()) {
                                    val data = "$word\t$it${System.lineSeparator()}"
                                    print(data)
                                    SYLLABLE2.appendText(data)
                                }
                            }
                    }
                }
                .get()
        }
    }

    @Test
    fun duplicate() {
        val s2 = "$ONE_DRIVE_DIR/syllable.txt".toFile()
        val data = s2.readText().lines()
        data.notEmptyData().groupBy { it.first() }.filter { it.value.size > 1 }.also { println(it) }
        println(data.size)
        data
            .map { it.split("\t") }
            .filter { it.isNotEmpty() && it.first() == it.last().replace("-", "").lowercase() }
            .also {
                println(it.size)
                s2.writeText(
                    it.joinToString(System.lineSeparator()) { it.first() + "\t" + it.last() }
                )
            }
    }

    @Test
    fun replaceWords() {
        var text = SYLLABLE_FILE.readText()
        SYLLABLE2.readText()
            .lines()
            .filter { it.split("\t").last().isNotEmpty() }
            .also { println(it.size) }
            .forEach {
                val line = it.substringBefore("\t")
                val regex = "\\b$line\t(?=\r?\n)".toRegex()
                //                println("$line $it ${text.contains(regex)}")
                text = text.replace(regex, it)
            }

        SYLLABLE_FILE.writeText(text)
    }

    private fun String.syllable(): String? = runCatching { syllableHowMany() }.getOrNull()
    //            .getOrElse { this.syllableHowMany() }
}

fun List<String>.emptyData(separator: String = "\t") =
    map { it.split(separator) }.filter { it.last().isEmpty() }.map { it.first() }.toSet()

fun List<String>.notEmptyData(separator: String = "\t") =
    map { it.split(separator) }.filter { it.last().isNotEmpty() }
