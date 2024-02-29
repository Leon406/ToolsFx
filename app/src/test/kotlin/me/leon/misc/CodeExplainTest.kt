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
    fun carNo() {
        println(CodeMapping.CN_CAR_NO["甘M"])
        println(CodeMapping.CN_CAR_NO["青O"])
        println(CodeMapping.CN_CAR_NO["青A"])
        println(CodeMapping.CN_CAR_NO["新B"])
        println(CodeMapping.CN_CAR_NO["沪M"])
        println(CodeMapping.CN_CAR_NO["琼C3"])
    }

    @Test
    fun language() {
        println(CodeMapping.LANGUAGE["鞑靼语"])
        println(CodeMapping.LANGUAGE["vi"])
        println(CodeMapping.LANGUAGE["中文"])
        println(CodeMapping.LANGUAGE["uz"])
        println(CodeMapping.LANGUAGE["ay_BO"])
        println(CodeMapping.LANGUAGE["sq_AL"])
        println(CodeMapping.LANGUAGE["sr_SP"])
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
