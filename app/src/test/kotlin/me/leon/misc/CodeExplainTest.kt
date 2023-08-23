package me.leon.misc

import kotlin.test.Test

/**
 * @author Leon
 * @since 2023-08-01 10:52
 * @email deadogone@gmail.com
 */
class CodeExplainTest {
    @Test
    fun httpCode() {
        println(CodeMapping.HTTP_CODE_DICT["200"])
        println(CodeMapping.HTTP_CODE_DICT["499"])
        println(CodeMapping.HTTP_CODE_DICT["405"])
    }

    @Test
    fun port() {
        println(CodeMapping.PORT_DICT["22"])
        println(CodeMapping.PORT_DICT["23"])
        println(CodeMapping.PORT_DICT["53"])
    }

    @Test
    fun mime() {
        println(CodeMapping.MIME_DICT["jpeg"])
        println(CodeMapping.MIME_DICT["mp4"])
        println(CodeMapping.MIME_DICT["mp3"])
        println(CodeMapping.MIME_DICT["mpeg"])
        println(CodeMapping.MIME_DICT["css"])
        println(CodeMapping.MIME_DICT["aac"])
        val search = "mp4"
        (CodeMapping.MIME_DICT[search]
                ?: CodeMapping.MIME_DICT.filter { it.key.contains(search) }
                    .map { (k, v) -> "$k\t$v" }
                    .joinToString(System.lineSeparator()))
            .also { println(it) }
    }
}
