package me.leon.encode.base

import java.math.BigInteger
import me.leon.ctf.dictValueParse

const val BASE58_DICT = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

fun String.radixNEncode(dict: String = BASE58_DICT): String {
    return toByteArray().radixNEncode(dict)
}

fun String.radixNEncode(dict: List<String>): String {
    return toByteArray().radixNEncode(dict)
}

fun ByteArray.radixNEncode(dict: String = BASE58_DICT): String {
    return radixNEncode(dict.asIterable().map { it.toString() }.toList())
}

fun ByteArray.radixNEncode(dict: List<String>): String {
    val radix = dict.size
    var bigInteger = BigInteger(1, this)
    var remainder: Int
    val sb = mutableListOf<String>()
    val leadingZero = dict.first()
    val base = radix.toBigInteger()
    while (bigInteger != BigInteger.ZERO) {
        bigInteger.divideAndRemainder(base).run {
            bigInteger = this[0]
            remainder = this[1].toInt()
        }
        sb.add(dict[remainder])
    }
    var result = sb.reversed().joinToString("")
    var i = 0
    while (i < size && this[i].toInt() == 0) {
        result = "$leadingZero$result"
        i++
    }
    return result
}

fun BigInteger.radixNEncode(dict: String) = toByteArray().radixNEncode(dict)

fun String.radixNDecode(dict: String = BASE58_DICT): ByteArray {
    return if (matches("^[$dict]+$".toRegex())) {
        radixNDecode(dict.asIterable().map { it.toString() }.toList())
    } else {
        error("Wrong data format!")
    }
}

fun String.radixNDecode(dict: List<String>): ByteArray {
    if (isEmpty()) return ByteArray(0)
    val radix = dict.size
    val leadingZero = dict.first()
    var intData = BigInteger.ZERO
    var leadingZeros = 0
    val base = radix.toBigInteger()
    val values = dictValueParse(dict)
    for (s in values) {
        val digit = dict.indexOf(s)
        require(digit != -1) { String.format("Invalid  character `%s` at position", s) }
        intData = intData.multiply(base).add(BigInteger.valueOf(digit.toLong()))
    }

    for (element in values) {
        if (element == leadingZero) leadingZeros++ else break
    }
    val bytesData: ByteArray =
        if (intData == BigInteger.ZERO) ByteArray(0) else intData.toByteArray()

    val stripSignByte = bytesData.size > 1 && bytesData[0].toInt() == 0 && bytesData[1] < 0
    val decoded = ByteArray(bytesData.size - (if (stripSignByte) 1 else 0) + leadingZeros)
    System.arraycopy(
        bytesData,
        if (stripSignByte) 1 else 0,
        decoded,
        leadingZeros,
        decoded.size - leadingZeros
    )
    return decoded
}

fun String.radixNDecode2Decimal(dict: List<String>): ByteArray {
    return radixNDecode2DecimalString(dict).toByteArray()
}

fun String.radixNDecode2DecimalString(dict: List<String>): String {
    if (isEmpty()) return ""
    val radix = dict.size
    var intData = BigInteger.ZERO
    val base = radix.toBigInteger()
    val values = dictValueParse(dict)
    for (s in values) {
        val digit = dict.indexOf(s)
        require(digit != -1) { String.format("Invalid  character `%s` at position", s) }
        intData = intData.multiply(base).add(BigInteger.valueOf(digit.toLong()))
    }
    return intData.toString()
}

fun String.radixNDecode2String(dict: String = BASE58_DICT) = String(radixNDecode(dict))

fun String.radixNDecode2String(dict: List<String>) = String(radixNDecode(dict))
