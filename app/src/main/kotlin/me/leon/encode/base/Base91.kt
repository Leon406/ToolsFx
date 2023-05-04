package me.leon.encode.base

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import kotlin.math.ceil
import kotlin.math.roundToInt

object Base91 {

    private val ENCODING_TABLE =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\""
            .toByteArray()
    private val BASE = ENCODING_TABLE.size
    private const val AVERAGE_ENCODING_RATIO = 1.2297f

    fun encode(data: ByteArray, dict: ByteArray = ENCODING_TABLE): ByteArray {
        val estimatedSize = ceil((data.size * AVERAGE_ENCODING_RATIO).toDouble()).toInt()
        val output = ByteArrayOutputStream(estimatedSize)
        var ebq = 0
        var en = 0
        for (i in data.indices) {
            ebq = ebq or (data[i].toInt() and 255 shl en)
            en += 8
            // 仅在位数大于13时进行处理, 2^13 8192 91*91=8281, 需要13~14位才能编码
            if (en > 13) {
                // 取13位
                var ev = ebq and 8191
                if (ev > 88) {
                    // 右移 13位
                    ebq = ebq shr 13
                    en -= 13
                } else {
                    // 取14位
                    ev = ebq and 16_383
                    // 右移 14位
                    ebq = ebq shr 14
                    en -= 14
                }
                output.write(dict[ev % BASE].toInt())
                output.write(dict[ev / BASE].toInt())
            }
        }
        if (en > 0) {
            output.write(dict[ebq % BASE].toInt())
            if (en > 7 || ebq > 90) {
                output.write(dict[ebq / BASE].toInt())
            }
        }
        return output.toByteArray()
    }

    fun decode(data: ByteArray, dict: ByteArray = ENCODING_TABLE): ByteArray {
        var dbq = 0
        var dn = 0
        var dv = -1
        val estimatedSize = (data.size / AVERAGE_ENCODING_RATIO).roundToInt()
        val output = ByteArrayOutputStream(estimatedSize)
        for (i in data.indices) {
            if (dict.indexOf(data[i]) == -1) {
                continue
            }
            if (dv == -1) {
                dv = dict.indexOf(data[i])
            } else {
                dv += dict.indexOf(data[i]) * BASE
                dbq = dbq or (dv shl dn)
                dn += if (dv and 8191 > 88) 13 else 14
                do {
                    output.write(dbq)
                    dbq = dbq shr 8
                    dn -= 8
                } while (dn > 7)
                dv = -1
            }
        }
        if (dv != -1) {
            output.write(((dbq or (dv shl dn)).toByte()).toInt())
        }
        return output.toByteArray()
    }
}

const val BASE91_DICT =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\""

fun String.base91(dict: String = BASE91_DICT, charset: String = "UTF-8") =
    toByteArray().base91(dict, charset)

fun ByteArray.base91(dict: String = BASE91_DICT, charset: String = "UTF-8") =
    String(
        Base91.encode(this, dict.ifEmpty { BASE91_DICT }.toByteArray()),
        Charset.forName(charset)
    )

fun String.base91Decode(dict: String = BASE91_DICT) =
    if (length > 1 && asIterable().all { it in dict.ifEmpty { BASE91_DICT } }) {
        Base91.decode(toByteArray(), dict.ifEmpty { BASE91_DICT }.toByteArray())
    } else {
        error("非法字符")
    }

fun String.base91Decode2String(dict: String = BASE91_DICT) = String(base91Decode(dict))
