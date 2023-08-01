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
}
