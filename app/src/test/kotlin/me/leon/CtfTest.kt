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
    fun asciiShift() {
        val d = "FDCB[8LDQ?ZLOO?FHUWDLQOB?VXFFHHG?LQ?ILJKWLQJ?WKH?HSLGHPLF]"
        d.map { (it.code + 29).toChar() }.also { println(it.joinToString("")) }

        val emoji = "🙃💵🌿🎤🚪🌏🐎🥋🚫😆😍🐍✅🐎👑😡😁😁😍🕹🦓😂🕹🚪🎤💵📂✉🥋🚪😂🚪👑🐘🍎🍎🐍👑🙃😁✅😀🥋🗒"
        val emojiShift1 ="🐎🤣🌏🚰👁🌪🌊✉❓💵✅💧🔪🌊👉🎃😆😆✅📂🏹🥋📂👁🚰🤣🛩🚹✉👁🥋👁👉🌿🍌🍌💧👉🐎😆🔪🖐✉🍎"
        val base64 = "U2FsdGVkX19v+Vq7009NzjNds2OlkdjdqEaavqU0+gk="

        assertEquals(base64, emoji.emojiReplaceDecode())
        assertEquals(base64, emojiShift1.emojiReplaceDecode(1))
        assertEquals(emoji, base64.emojiReplace().also { println(it) })
        assertEquals(emojiShift1, base64.emojiReplace(1).also { println(it) })
    }
}
