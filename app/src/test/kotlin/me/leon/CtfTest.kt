package me.leon

import org.junit.Test

class CtfTest {
    @Test
    fun pawnshopTest() {

        println("flag{pawn_sh0p_ciph3r}".pawnshop().also {
            println(it.pawnshopDecode())
        })
        println("王夫 井工 夫口 由中人 井中 夫夫 由中大".pawnshopDecode())
    }

    fun String.curveCipher(row: Int, col: Int) =
        chunked(col).let {
//            println(it.joinToString(" "))
            var reverseFlag = false
            val sb = StringBuilder()
            for (i in this.indices) {
                if (i % row == 0) {
                    reverseFlag = !reverseFlag
                }
                val listIndex = if (reverseFlag) i % row else row - i % row - 1
                sb.append(it[listIndex][i / row])
            }
            sb.reverse().toString()
        }

    fun String.curveCipherDecode(row: Int, col: Int): String {
        return with(this) {
            var reverseFlag = false
            val c = CharArray(length)
            for (i in indices) {
                if (i % row == 0) {
                    reverseFlag = !reverseFlag
                }
//                println(" ${i / row} ${i % row}")
                val listIndex = if (reverseFlag) i % row else row - i % row - 1
//                println(this[i / row + listIndex * col])
                c[i / row + listIndex * col] = this[i]
            }
            c.joinToString("").reversed()
        }
    }


    /**
     * 偶数列           奇数列
     * HelloWorldab   gesfcinphodtmwuqouryzejrehbxvalookT
     * lrbaoleWdloH   The quick brown fox jumps over the lazy dog
     */
    @Test
    fun curveCipher() {

        val d = "HelloWorldab"
        println(d.curveCipher(3, 4))
        println("The quick brown fox jumps over the lazy dog".replace(" ","").curveCipher(5, 7))
        println("gesfcinphodtmwuqouryzejrehbxvalookT".curveCipherDecode(5, 7))
        println("lrbaoleWdloH".curveCipherDecode(3, 4))
    }

    val map = mapOf(
        0 to arrayOf('目', '口', '凹', '凸', '田'),
        1 to arrayOf('由'),
        2 to arrayOf('中'),
        3 to arrayOf('人', '入', '古'),
        4 to arrayOf('工', '互'),
        5 to arrayOf('果', '克', '尔', '土', '大'),
        6 to arrayOf('木', '王'),
        7 to arrayOf('夫', '主'),
        8 to arrayOf('井', '关', '丰', '并'),
        9 to arrayOf('圭', '羊'),
    )
    val reverseMap = mapOf(
        '目' to 0,
        '田' to 0,
        '口' to 0,
        '凹' to 0,
        '凸' to 0,
        '由' to 1,
        '中' to 2,
        '人' to 3,
        '入' to 3,
        '古' to 3,
        '工' to 4,
        '互' to 4,
        '果' to 5,
        '克' to 5,
        '尔' to 5,
        '土' to 5,
        '大' to 5,
        '木' to 6,
        '王' to 6,
        '夫' to 7,
        '主' to 7,
        '井' to 8,
        '关' to 8,
        '丰' to 8,
        '并' to 8,
        '圭' to 9,
        '羊' to 9,
    )


    // ASCII
    fun String.pawnshop() = toCharArray().filter { it.code in 0..127 }
        .map {
            it.code.split().joinToString("") { map[it]!!.random().toString() }
        }.joinToString(" ")

    fun Int.split() = this.toString().toCharArray().map { it - '0' }

    fun String.pawnshopDecode() = split("\\s+".toRegex()).map {
        it.toCharArray().map {
            reverseMap[it]
        }.joinToString("").toInt().toChar()
    }.joinToString("")

}
