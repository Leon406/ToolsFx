package me.leon.translate

import kotlin.test.Test
import kotlin.test.assertEquals
import me.leon.misc.Translator

class SentenceTranslateTest {

    private val text = "what can I do for you"
    private val textCN = "我能为你做什么"

    init {
        Translator.init()
    }

    @Test
    fun google() {

        println(Translator.simpleGoogle(text))
        println(Translator.simpleGoogle(textCN, target = "en"))
        println(Translator.google(text))
        println(Translator.google(textCN, target = "en"))

        assertEquals(textCN, Translator.translate(text))
        assertEquals(text, Translator.translate(textCN, target = "en"))
    }

    @Test
    fun lingva() {
        println(Translator.lingva(text))
        println(Translator.lingva(textCN, target = "en"))
    }

    @Test
    fun googleLongSentence() {
        val shortSentence =
            "The result is a two-tier economy " +
                "Firms that embrace tech are pulling away from the competition "
        val longSentence =
            shortSentence +
                "In 2010 the average worker at Britain’s most productive firms produced goods " +
                "and services worth £98,000 (in today’s money), which had risen to £108,500 by 2019. " +
                "Those at the worst firms saw no rise. " +
                "In Canada in the 1990s frontier firms’ productivity growth was " +
                "about 40% higher than non-frontier firms. From 2000 to 2015 it was three " +
                "times as high. A book by Tim Koller of McKinsey, a consultancy, " +
                "and colleagues finds that, after ranking firms according to their " +
                "return on invested capital, the 75th percentile had a return 20 percentage " +
                "points higher than the median in 2017—double the gap in 2000. Some companies " +
                "see huge gains from buying new tech;" +
                " many see none at all."
        //        Translator.translate(longSentence)
        //            .also { println(it) }

        println(Translator.google(longSentence))
        println(Translator.simpleGoogle(longSentence))
        println(Translator.lingva(longSentence))
    }
}
