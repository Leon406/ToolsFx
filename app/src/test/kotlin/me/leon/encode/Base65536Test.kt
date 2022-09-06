package me.leon.encode

import kotlin.test.Test
import kotlin.test.assertEquals
import me.leon.encode.base.*

/**
 *
 * @author Leon
 * @since 2022-09-06 15:47
 * @email: deadogone@gmail.com
 */
class Base65536Test {

    @Test
    fun base65536() {
        val raw = "Hello World"
        val encode = "驈ꍬ啯\uD808\uDC57ꍲᕤ"
        val encode2 = "驈ꍬ啯𒁗ꍲᕤ"
        assertEquals(encode, raw.base65536())
        assertEquals(raw, encode.base65536Decode2String())
        assertEquals(raw, encode2.base65536Decode2String())
    }
}
