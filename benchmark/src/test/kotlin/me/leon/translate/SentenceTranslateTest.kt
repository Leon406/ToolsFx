package me.leon.translate

import kotlin.test.Test
import me.leon.misc.Translator

class SentenceTranslateTest {

    private val text = "what can i do for you"
    private val textCN = "我能为你做什么"

    @Test
    fun google() {
        println(Translator.google(text))
        println(Translator.google(textCN, target = "en"))
    }

    @Test
    fun lingva() {
        println(Translator.lingva(text))
        println(Translator.lingva(textCN, target = "en"))
    }
}
