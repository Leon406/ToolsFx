package me.leon

import kotlin.test.assertEquals
import org.junit.Test

class Execute {
    @Test
    fun batch() {

        Nashorn.loadResource("/aaencode.js")
            .loadResource("/jjencode.js")
            .loadResource("/rabbit.js")
            .invoke("aaencode", "aadfsdf")
            ?.also {
                println(it)
                assertEquals("aadfsdf", Nashorn.invoke("aadecode", it))
            }

        Nashorn.invoke("rabbitEncrypt", "123", "123")?.also {
            println(it)
            assertEquals("123", Nashorn.invoke("rabbitDecrypt", it, "123"))
        }
        Nashorn.invoke("encode_jj", "encode", "$$$", true)?.also {
            println(Nashorn.invoke("jjdecode", it))
            assertEquals("encode", Nashorn.invoke("jjdecode", it))
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
}
