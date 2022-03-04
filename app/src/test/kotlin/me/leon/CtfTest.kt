package me.leon

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
}
