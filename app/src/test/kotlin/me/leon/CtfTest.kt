package me.leon

import fourSquare
import fourSquareDecrypt
import kotlin.test.assertEquals
import me.leon.classical.*
import me.leon.ctf.*
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
        d.porta("hello").also {
            println(it)
            it.porta("hello").also { println(it) }
        }
    }

    @Test
    fun beaufort() {
        val d = "where is beaufort Cipher flag"
        d.beaufort("hello").also {
            println(it)
            it.beaufort("hello").also { println(it) }
        }
    }
    @Test
    fun gronsfeld() {
        val d = "where is gronsfeld Cipher flag"
        d.gronsfeld().also {
            println(it)
            it.gronsfeldDecrypt().also { println(it) }
        }
    }

    @Test
    fun fourSquare() {
        val key1 = "OPQABCDEFGHRSTUVWXYZIKLMN"
        val key2 = "GHIKOPQRSYZABTULMNVWXCDEF"
        val d = "Foursquare Cipher"
        d.fourSquare(key1, key2).also {
            println(it)
            it.fourSquareDecrypt(key1, key2).also { println(it) }
        }
    }
}
