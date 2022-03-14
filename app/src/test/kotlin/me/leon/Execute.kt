package me.leon

import kotlin.concurrent.thread
import org.junit.Test

class Execute {
    @Test
    fun batch() {
        thread {
            Runtime.getRuntime().exec("notepad").apply {
                inputStream.bufferedReader().use { it.lines().forEach { println(it) } }
            }
        }
        Runtime.getRuntime()
            .exec("cmd /c chcp 65001 && ping www.baidu.com")
            //            .exec("python E:/gitrepo/pyutil/Args.py a b c")
            .apply { inputStream.bufferedReader().use { it.lines().forEach { println(it) } } }
    }
}
