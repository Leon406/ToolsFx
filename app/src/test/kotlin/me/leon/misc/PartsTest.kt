package me.leon.misc

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class PartsTest {
    @Test
    fun test() {
        val s =
            "△All. 3.2.92(90)：“The fellow has a deal of that too much，”i. e. " +
                "the fellow has a great deal too much of the power to inducecorruption. "
        Spliter.initDict(
            File("${File("").absoluteFile.parentFile}\\vocabulary\\wordninja_words.txt")
        )
        val message = Spliter.splitContiguousWords(s).joinToString("")
        assertEquals(136, message.length)
    }
}
