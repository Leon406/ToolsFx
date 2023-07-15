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
}
