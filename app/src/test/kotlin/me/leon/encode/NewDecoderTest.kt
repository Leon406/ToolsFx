package me.leon.encode

import kotlin.test.*
import me.leon.encode.base.*

/**
 * @author Leon
 * @since 2022-10-31 10:35
 */
class NewDecoderTest {

    @Test
    fun ecoji() {
        assertEquals("👖📸🎈☕", "abc".ecoji())
        assertEquals("6789", "🎥🤠📠🏍".ecojiDecode2String())
    }

    @Test
    fun base2048Test() {
        val data = byteArrayOf(1, 2, 4, 8, 16, 32, 64, -128)
        val encode = "GƸOʜeҩ"

        assertEquals(encode, data.base2048())
        assertContentEquals(data, encode.base2048Decode())
    }

    @Test
    fun base32678Test() {
        val data = byteArrayOf(104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100)
        val encode = "媒腻㐤┖ꈳ埳"

        assertEquals(encode, data.base32768())
        assertContentEquals(data, encode.base32768Decode())
    }

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
