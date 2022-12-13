package me.leon.encode

import kotlin.test.Test
import kotlin.test.assertEquals
import me.leon.encode.base.base69
import me.leon.encode.base.base69Decode2String

/**
 * @author Leon
 * @since 2022-09-06 15:47
 */
class Base69Test {

    @Test
    fun base69() {
        val raw = "你好123sadf收到否"
        val encode = "tBvA0AOAtAWA1BxAZAMApB2ALARAIBhBFBtAXBTBAB>AGBQAOBAAAAAAAAAAAA6="
        assertEquals(encode, raw.base69())
        assertEquals(raw, encode.base69Decode2String())
    }
}
