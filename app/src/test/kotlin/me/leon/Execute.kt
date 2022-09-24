package me.leon

import java.lang.System.getProperty
import kotlin.test.assertEquals
import me.leon.ext.*
import me.leon.ext.crypto.JavascriptCipher
import org.junit.Test

class Execute {
    @Test
    fun batch() {

        JavascriptCipher.aaEncode("aadfsdf").also {
            println(it)
            assertEquals("aadfsdf", JavascriptCipher.aaDecode(it))
        }
        JavascriptCipher.rabbitEncrypt("123", "123").also {
            println(it)
            assertEquals("123", Nashorn.invoke("rabbitDecrypt", it, "123"))
        }
        JavascriptCipher.jjEncode("123", "$$$", true).also {
            println(it)
            assertEquals("123", JavascriptCipher.jjDecode(it))
        }

        //        thread {
        //            Runtime.getRuntime().exec("notepad").apply {
        //                inputStream.bufferedReader().use { it.lines().forEach { println(it) } }
        //            }
        //        }

        if (getProperty("os.name").contains("Windows")) {
            Runtime.getRuntime()
                .exec("cmd /c chcp 65001 && ping www.baidu.com")
                .inputStream
                .bufferedReader()
                .use { it.lines().forEach { println(it) } }
        }
    }

    @Test
    fun nashorn() {
        val js =
            """
            function getKey(a) {
                for (var c = [], d = "", b = 48; 58 > b; b++)
                    c.push(String.fromCharCode(b));
                for (b = 64; 91 > b; b++)
                    c.push(String.fromCharCode(b));
                for (b = 0; b < a.length; b++)
                    d += c[a[b]];
                return d
            }
        """
                .trimIndent()
        Nashorn.loadString(js)
        Nashorn.invoke("getKey", intArrayOf(2, 0, 3, 12, 6, 1, 14, 3, 5, 0, 6, 8)).also {
            println(it)
        }
        JavascriptCipher.aaEncode("aadfsdf").also {
            println(it)
            JavascriptCipher.aaDecode(it).also { println(it) }
        }
    }

    @Test
    fun parseMoe() {
        with("http://hi.pcmoe.net/js/main.min.js".readFromNet()) {
            println(this)
            "function +getKey\\([^)]+\\) *\\{[^}]+}".toRegex().find(this)?.let {
                Nashorn.loadString(it.value)
            }
            """getKey\(\[(\d+(?:,(?:\r\n|\n|\r)?\d+)+)\]\)""".toRegex().find(this)?.let {
                println(Nashorn.invoke("getKey", it.groupValues[1].splitByNonDigit()))
            }
        }
    }
}
