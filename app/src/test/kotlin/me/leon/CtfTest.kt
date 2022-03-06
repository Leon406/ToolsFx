package me.leon

import kotlin.test.assertEquals
import me.leon.ctf.*
import me.leon.ext.hex2ByteArray
import me.leon.ext.toHex
import org.junit.Test

class CtfTest {
    @Test
    fun pawnshopTest() {
        println("flag{pawn_sh0p_ciph3r}".pawnshop().also { println(it.pawnshopDecode()) })
        println("王夫 井工 夫口 由中人 井中 夫夫 由中大".pawnshopDecode())
    }

    /**
     * 偶数列 奇数列 HelloWorldab gesfcinphodtmwuqouryzejrehbxvalookT lrbaoleWdloH The quick brown fox
     * jumps over the lazy dog
     */
    @Test
    fun curveCipher() {
        val d = "HelloWorldab"
        println(d.curveCipher(3, 4))
        println("The quick brown fox jumps over the lazy dog".replace(" ", "").curveCipher(5, 7))
        println("gesfcinphodtmwuqouryzejrehbxvalookT".curveCipherDecode(5, 7))
        println("lrbaoleWdloH".curveCipherDecode(3, 4))
    }

    @Test
    fun circleIndex() {
        assertEquals(2, 0.circleIndex(5, 3))
        assertEquals(0, 1.circleIndex(5, 3))
        assertEquals(1, 2.circleIndex(5, 3))
    }

    @Test
    fun asciiShitf() {
        val d = "FDCB[8LDQ?ZLOO?FHUWDLQOB?VXFFHHG?LQ?ILJKWLQJ?WKH?HSLGHPLF]"
        d.map { (it.code + 29).toChar() }.also { println(it.joinToString("")) }

        "U2FsdGVkX19v+Vq7009NzjNds2OlkdjdqEaavqU0+gk=".emojiReplace().also { println(it) }
        "🙃💵🌿🎤🚪🌏🐎🥋🚫😆😍🐍✅🐎👑😡😁😁😍🕹🦓😂🕹🚪🎤💵📂✉🥋🚪😂🚪👑🐘🍎🍎🐍👑🙃😁✅😀🥋🗒"
            .emojiReplaceDecode()
            .also { println(it) }
        "U2FsdGVkX19v+Vq7009NzjNds2OlkdjdqEaavqU0+gk=".emojiReplace(1).also { println(it) }
        "🐎🤣🌏🚰👁🌪🌊✉❓💵🔪😂👉👁🐅☀🐍🐎👣📂☂💵⏩🕹🕹🔬📂🚪😍🗒✉🌿🌊🥋🏹🎤❓✉😆🤣😍🤣✉🍎"
            .also { println(it) }
            .emojiReplaceDecode(1)
            .also { println(it) }

        emojiMap.joinToString("").toByteArray().forEach {
            when (it.toInt().and(0xFF).shr(4)) {
                in 0..7 -> 1
                12, 13 -> 2
                14 -> 3
                15 -> 4
                else -> -1
            }
            //                    .also { println(it) }
        }
        "😀".toByteArray(Charsets.UTF_32BE).also { println(it.toHex()) }
        "😀".toByteArray(Charsets.UTF_8).also { println(it.toHex()) }
        println("f09f9880".hex2ByteArray().decodeToString())

        "0001F387".hex2ByteArray().toString(Charsets.UTF_32BE).also { println(it) }
    }
}
