package me.leon.translate

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

    init {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "256")
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
    }

    @Test
    fun ldoce() {
        assertEquals("rul-er", "ruler".syllableLdoce())
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
        val nodata =
            words -
                SYLLABLE_FILE.readText().lines().map { it.split("\t").first() }.toSet() -
                SYLLABLE_NO_DATA_FILE.readText().lines().toSet()
        println("${nodata.size} ${words.size}")

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

    @Test
    fun noData() {
        val newData = "$ONE_DRIVE_DIR/syllable2.txt".toFile()
        val nodataOri =
            SYLLABLE_FILE.readText().lines().emptyData().also {
                SYLLABLE_NO_DATA_FILE.writeText(it.joinToString(System.lineSeparator()))
            }
        println(nodataOri.size)
        val nodata = SYLLABLE_NO_DATA_FILE.readText().lines().toSet()
        println("${nodata.size}")
        nodata.parallelStream().forEach { word ->
            runCatching { word.syllable() }
                .getOrNull()
                ?.let {
                    if (it.isNotEmpty()) {
                        val data = "$word\t$it${System.lineSeparator()}"
                        print(data)
                        newData.appendText(data)
                    }
                }
        }
    }

    @Test
    fun replaceWords() {
        var text = SYLLABLE_FILE.readText()
        "$ONE_DRIVE_DIR/syllable2.txt"
            .toFile()
            .readText()
            .lines()
            .filter { it.split("\t").last().isNotEmpty() }
            .also { println(it.size) }
            .forEach {
                val line = it.substringBefore("\t")
                val regex = "\\b$line\t(?=\r?\n)".toRegex()
                println("$line $it ${text.contains(regex)}")
                text = text.replace(regex, it)
            }

        SYLLABLE_FILE.writeText(text)
    }

    private fun String.syllable(): String =
        runCatching { this.syllableWord() }.getOrElse { this.syllableHowMany() }
}

fun List<String>.emptyData(separator: String = "\t") =
    map { it.split(separator) }.filter { it.last().isEmpty() }.map { it.first() }.toSet()

fun List<String>.notEmptyData(separator: String = "\t") =
    map { it.split(separator) }.filter { it.last().isNotEmpty() }
