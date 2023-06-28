package me.leon.misc

import IcibaVocabulary
import kotlin.test.Test
import me.leon.ext.fromJson
import me.leon.ext.readFromNet

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
        BING.format(word).readFromNet().also { println(it) }
    }

    @Test
    fun youdao() {
        YOUDAO.format(word).readFromNet().also { println(it) }
    }

    @Test
    fun cambridge() {
        CAMBRIDGE.format(word).readFromNet().also { println(it) }
    }
}
