package me.leon.ctf

import java.nio.charset.Charset
import kotlin.math.ceil

/**
 * @link
 *   http://wiki.yak.net/589/Bubble_Babble_Encoding.txthttp://wiki.yak.net/589/Bubble_Babble_Encoding.txt
 *   仅支持 ascii
 */
object BubbleBabble {
    private const val vowels = "aeiouy" // 元音表
    private const val consonants = "bcdfghklmnprstvzx" // 常量表

    /**
     * 对原文进行bubble bubble编码
     *
     * @param str 原文字符串
     * @return 编码字符串
     */
    fun encrypt(str: String): String {
        val k = str.length // 明文长度
        val D = str.toByteArray() // 字节数据
        val C = ByteArray(k / 2 + 1) // 校验和
        val T = Array(k / 2) { IntArray(5) } // 5元组
        val P = IntArray(3) // 三元组
        for (i in 0 until k / 2 + 1) {
            C[i] =
                if (i == 0) {
                    1
                } else {
                    ((C[i - 1] * 5 + D[((i + 1) * 2) - 3 - 1] * 7 + D[((i + 1) * 2) - 2 - 1]) % 36)
                        .toByte()
                }
        }
        for (i in 0 until k / 2) { // 每两个数据构建一个五元组
            val a = ((D[i * 2].toInt() shr 6 and 3) + C[i]) % 6
            val b = D[i * 2].toInt() shr 2 and 15
            val c = (D[i * 2].toInt() and 3) + C[i] / 6 % 6
            if (2 * i + 1 >= k) break
            val d = D[i * 2 + 1].toInt() shr 4 and 15
            val e = D[i * 2 + 1].toInt() and 15
            T[i][0] = a
            T[i][1] = b
            T[i][2] = c
            T[i][3] = d
            T[i][4] = e
        }
        // 三元组
        P[0] = if (k % 2 == 0) C[k / 2] % 6 else ((D[k - 1].toInt() shr 6 and 3) + C[k / 2]) % 6
        P[1] = if (k % 2 == 0) 16 else D[k - 1].toInt() shr 2 and 15
        P[2] = if (k % 2 == 0) C[k / 2] / 6 else ((D[k - 1].toInt() and 3) + C[k / 2] / 6) % 6
        val out = StringBuilder("x")
        for (i in T.indices) {
            out.append(vowels[T[i][0]])
                .append(consonants[T[i][1]])
                .append(vowels[T[i][2] % 6])
                .append(consonants[T[i][3]])
                .append("-")
                .append(consonants[T[i][4]])
        }
        out.append(vowels[P[0]])
        out.append(consonants[P[1]])
        out.append(vowels[P[2]])
        out.append('x')
        return out.toString()
    }

    /**
     * 对bubble bubble 编码的字符串进行解码
     *
     * @param str 编码字符串
     * @return 原始数据
     */
    fun decrypt(str: String): String {
        var tmpStr = str
        var c = 1
        tmpStr = tmpStr.substring(1, tmpStr.length - 1) // 取出x
        val lastTuple = ceil(str.length / 6.0).toInt() - 1
        return tmpStr
            .chunked(6)
            .foldIndexed(StringBuilder()) { index, acc, value ->
                acc.apply {
                    val tup = mutableListOf<String>()
                    tup.add(vowels.indexOf(value[0]).toString())
                    tup.add(consonants.indexOf(value[1]).toString())
                    tup.add(vowels.indexOf(value[2]).toString())
                    runCatching {
                        tup.add(consonants.indexOf(value[3]).toString())
                        tup.add("-")
                        tup.add(consonants.indexOf(value[5]).toString())
                    }
                    val high = (tup[0].toInt() - c % 6 + 6) % 6
                    val mid = tup[1].toInt()
                    val low = (tup[2].toInt() - c / 6 % 6 + 6) % 6
                    val b = (high shl 6 or (mid shl 2) or low).toByte()
                    if (lastTuple == index) {
                        if (tup[1] != "16") {
                            acc.append(b.toInt().toChar())
                        }
                    } else {
                        acc.append(b.toInt().toChar())
                        val b1 = (tup[3].toInt() shl 4 or tup[5].toInt()).toByte()
                        acc.append(b1.toInt().toChar())
                        c = (c * 5 + b * 7 + b1) % 36
                    }
                }
            }
            .toString()
    }
}

fun String.bubbleBabble() = BubbleBabble.encrypt(this)

fun ByteArray.bubbleBabble(charset: String) =
    BubbleBabble.encrypt(toString(Charset.forName(charset)))

fun String.bubbleBabbleDecode() = BubbleBabble.decrypt(this)

fun String.bubbleBabbleDecode2String() = BubbleBabble.decrypt(this)
