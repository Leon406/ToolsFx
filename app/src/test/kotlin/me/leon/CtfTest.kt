package me.leon

import kotlin.test.assertEquals
import me.leon.classical.*
import me.leon.ctf.*
import org.junit.Test

class CtfTest {
    @Test
    fun pawnshopTest() {
        val d = "flag{pawn_sh0p_ciph3r}"
        d.pawnshop().also { assertEquals(d, it.pawnshopDecode()) }
        assertEquals("CTF{RM}", "王夫 井工 夫口 由中人 井中 夫夫 由中大".pawnshopDecode())
    }

    /**
     * 偶数列 奇数列 HelloWorldab gesfcinphodtmwuqouryzejrehbxvalookT lrbaoleWdloH The quick brown fox
     * jumps over the lazy dog
     */
    @Test
    fun curveCipher() {
        val d = "HelloWorldab"
        val d2 = "gesfcinphodtmwuqouryzejrehbxvalookT"
        val d3 = "The quick brown fox jumps over the lazy dog".replace(" ", "")
        assertEquals("lrbaoleWdloH", d.curveCipher(3, 4))
        assertEquals(d, "lrbaoleWdloH".curveCipherDecode(3, 4))
        assertEquals("Thequickbrownfoxjumpsoverthelazydog", d2.curveCipherDecode(5, 7))
        assertEquals(d2, d3.curveCipher(5, 7))
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

        val emoji =
            "🙃💵🌿🎤🚪🌏🐎🥋🚫😆😍🐍✅🐎👑😡😁😁😍🕹🦓😂🕹🚪🎤💵📂✉🥋🚪😂🚪👑🐘🍎🍎🐍👑🙃😁✅😀🥋🗒"
        val emojiShift1 =
            "🐎🤣🌏🚰👁🌪🌊✉❓💵✅💧🔪🌊👉🎃😆😆✅📂🏹🥋📂👁🚰🤣🛩🚹✉👁🥋👁👉🌿🍌🍌💧👉🐎😆🔪🖐✉🍎"
        val base64 = "U2FsdGVkX19v+Vq7009NzjNds2OlkdjdqEaavqU0+gk="

        assertEquals(base64, emoji.emojiReplaceDecode())
        assertEquals(base64, emojiShift1.emojiReplaceDecode(1))
        assertEquals(emoji, base64.emojiReplace().also { println(it) })
        assertEquals(emojiShift1, base64.emojiReplace(1).also { println(it) })
    }

    @Test
    fun tableSub() {
        val table =
            "2 22 222 3 33 333 4 44 444 5 55 555 6 66 666 7 77 777 7777 8 88 888 9 99 999 9999"
        val d = "abcdefghijklmnopqrstuvwxyz"
        println(d.tableEncode(table, " "))
        println("333 555 2 4 444 7777 44 2 66 3 999 222 666 3 33".tableDecode(table))
    }

    @Test
    fun porta() {
        val d = "where is Porta Cipher flag"
        d.porta("hello").also { assertEquals(d.replace(" ", "").uppercase(), it.porta("hello")) }
    }

    @Test
    fun beaufort() {
        val d = "where is beaufort Cipher flag"
        d.beaufort("hello").also {
            assertEquals(d.replace(" ", "").uppercase(), it.beaufort("hello"))
        }
    }

    @Test
    fun gronsfeld() {
        val d = "where is gronsfeld Cipher flag"
        d.gronsfeld().also { assertEquals(d.replace(" ", "").uppercase(), it.gronsfeldDecrypt()) }
    }

    @Test
    fun fourSquare() {
        val key1 = "OPQABCDEFGHRSTUVWXYZIKLMN"
        val key2 = "GHIKOPQRSYZABTULMNVWXCDEF"
        val d = "Foursquare Cipher"
        d.fourSquare(key1, key2).also {
            assertEquals(d.replace(" ", "").uppercase(), it.fourSquareDecrypt(key1, key2))
        }
    }

    @Test
    fun bifid() {
        val key = "mabkynvhgsorpczxedfqwtuil"
        val d = "defend the east wall of the castle"
        d.bifid(key, 3).also {
            assertEquals(d.replace(" ", "").uppercase(), it.bifidDecrypt(key, 3))
        }
    }

    @Test
    fun trifid() {
        val key = "EPSDUCVWYM.ZLKXNBTFGORIJHAQ"
        val d = "D E F E N D T H E E A S T W A L L O F T H E C A S T L E .".replace(" ", "")
        d.trifid(key, 5).also { assertEquals(d, it.trifidDecrypt(key, 5)) }
    }
}
