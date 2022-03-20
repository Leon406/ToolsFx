package lianzhang

import java.io.IOException
import java.util.*
import kotlin.Throws
import kotlin.jvm.JvmStatic
import kotlin.math.sqrt
import me.leon.classical.alphabetIndexDecodeNum
import me.leon.classical.alphabetIndexNum

/** https://github.com/akourk/HillCipher/blob/master/Main.java */
class Main {
    lateinit var keymatrix: Array<IntArray>
    lateinit var linematrix: IntArray
    lateinit var resultmatrix: IntArray
    fun perform(line: String) {}

    fun check(key: String, len: Int): Boolean {
        keymatrix = key.hillIndexSplitArray(len, true)
        println("key matrix: ")
        keymatrix.showMatrix()
        var d = keymatrix.determinant(len) % 26
        return if (d == 0) {
            println("Invalid key!!! Key is not invertible because determinant=0...")
            false
        } else if (d % 2 == 0 || d % 13 == 0) {
            println(
                "Invalid key!!! Key is not invertible because determinant has common factor with 26..."
            )
            false
        } else {
            true
        }
    }

    companion object {
        // abcdefgh bcba cbidofuh  byab
        // att  pfo cefjcbdrh lzwmvhhuw
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val obj = Main()
            val line = "att"
            val key = "cefjcbdrh"
            println(line.alphabetIndexNum())

            line.hillEncrypt(key).also { println(it) }
            "pfo".hillDecrypt(key).also { println(it) }

            println("Enter the line:  $line")
            println("Enter the key:  $key")
            val sq = sqrt(key.length.toDouble())
            if (sq != sq.toLong().toDouble())
                println("Invalid key length!!! Does not form a square matrix...")
            else {
                val s = sq.toInt()
                val keymatrix = key.hillIndexSplitArray(s, true)
                keymatrix.showMatrix()
                if (obj.check(key, s)) {
                    println("Result:")

                    line
                        .hillSplit(s)
                        .joinToString("") {
                            keymatrix
                                .multMod(it.alphabetIndexNum().toIntArray(), 26)
                                .alphabetIndexDecodeNum()
                                .lowercase()
                        }
                        .also { println(it) }
                    keymatrix.invertModMatrix().also {
                        println("\nInverse key:")
                        println(it.toCharacter())
                        println("inverse matrix: ")
                        println(it.joinToString("\n") { it.joinToString("  ") })
                    }
                }
            }
        }
    }
}
