package me.leon

import kotlin.test.assertEquals
import me.leon.ext.JavascriptCipher
import me.leon.ext.Nashorn
import org.junit.Test

class Execute {
    @Test
    fun batch() {

        JavascriptCipher.aaEncode("aadfsdf").also {
            println(it)
            assertEquals("aadfsdf", JavascriptCipher.aaDecode(it))
        }
        JavascriptCipher.rabbitEncrypt("123", "123")?.also {
            println(it)
            assertEquals("123", Nashorn.invoke("rabbitDecrypt", it, "123"))
        }
        JavascriptCipher.jjEncode("123", "$$$", true)?.also {
            println(it)
            assertEquals("123", JavascriptCipher.jjDecode(it))
        }

        //        thread {
        //            Runtime.getRuntime().exec("notepad").apply {
        //                inputStream.bufferedReader().use { it.lines().forEach { println(it) } }
        //            }
        //        }
        Runtime.getRuntime()
            .exec("cmd /c chcp 65001 && ping www.baidu.com")
            //            .exec("python E:/gitrepo/pyutil/Args.py a b c")
            .apply { inputStream.bufferedReader().use { it.lines().forEach { println(it) } } }
    }

    @Test
    fun nashorn() {
        JavascriptCipher.aaEncode("aadfsdf").also {
            println(it)
            JavascriptCipher.aaDecode(it).also { println(it) }
        }
    }
}
