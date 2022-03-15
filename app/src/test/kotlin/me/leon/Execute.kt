package me.leon

import javax.script.Invocable
import javax.script.ScriptEngineManager
import org.junit.Test

class Execute {
    @Test
    fun batch() {

        ScriptEngineManager().getEngineByName("nashorn").also {
            it.eval(javaClass.getResourceAsStream("/aaencode.js").reader())
            with(it as Invocable) {
                invokeFunction("aaencode", "aadfsdf").also {
                    println(it)
                    println(invokeFunction("aadecode", it))
                }
            }
            it.eval("print(\"Hello, JavaScript\")")
            it.eval(javaClass.getResourceAsStream("/jjencode.js").reader())
            with(it as Invocable) {
                invokeFunction("encode_jj", "encode", "$$$", true).also {
                    println(it)
                    println(invokeFunction("jjdecode", it))
                }
            }
            it.eval(javaClass.getResourceAsStream("/rc4.js").reader())
            with(it as Invocable) {
                invokeFunction("rabbitEncrypt", "123", "123").also {
                    println(it)
                    println(invokeFunction("rabbitDecrypt", it, "123"))
                }
            }
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
