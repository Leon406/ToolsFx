package me.leon.misc

import java.nio.ByteOrder
import kotlin.test.Test

/**
 * @author Leon
 * @since 2023-08-23 9:40
 * @email deadogone@gmail.com
 */
class EndiaTest {
    @Test
    fun endia() {
        println(ByteOrder.nativeOrder())
        println("abcd".toByteArray(Charsets.UTF_16).contentToString())
        println("abcd".toByteArray(Charsets.UTF_16LE).contentToString())
        println("abcd".toByteArray(Charsets.UTF_16BE).contentToString())
    }
}
