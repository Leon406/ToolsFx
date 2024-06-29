package me.leon.misc

import java.io.File
import kotlin.test.Test

class PartsTest {
    @Test
    fun test() {
        val s =
            "△All. 3.2.92(90)：“The fellow has a deal of that too much，”i. e. " +
                "the fellow has a great deal too much of the power to inducecorruption. "
        Spliter.initDict(
            File("${File("").absoluteFile.parentFile}\\vocabulary\\wordninja_words.txt")
        )
        println(Spliter.splitContiguousWords(s).joinToString(""))
    }
}
