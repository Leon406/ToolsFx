package me.leon.encode.base

import java.math.BigInteger

const val BASE58_DICT = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

fun String.radixNEncode(radix: Int = 58, maps: String = BASE58_DICT): String {
    return toByteArray().radixNEncode(radix, maps)
}

fun ByteArray.radixNEncode(radix: Int = 58, maps: String = BASE58_DICT): String {
    var bigInteger = BigInteger(1, this)
    var remainder: Int
    val sb = StringBuilder()
    val leadingZero = maps.first()
    val base = radix.toBigInteger()
    while (bigInteger != BigInteger.ZERO) {
        bigInteger.divideAndRemainder(base).run {
            bigInteger = this[0]
            remainder = this[1].toInt()
        }
        sb.append(maps[remainder])
    }
    var result = sb.reversed().toString()
    var i = 0
    while (i < size && this[i].toInt() == 0) {
        result = "$leadingZero$result"
        i++
    }
    return result
}

fun String.radixNDecode(radix: Int = 58, maps: String = BASE58_DICT): ByteArray {
    if (this.isEmpty()) return ByteArray(0)
    val leadingZero = maps.first()
    var intData = BigInteger.ZERO
    var leadingZeros = 0
    val base = radix.toBigInteger()
    for (i in this.indices) {
        val current = this[i]
        val digit = maps.indexOf(current)
        require(digit != -1) { String.format("Invalid  character `%c` at position %d", current, i) }
        intData = intData.multiply(base).add(BigInteger.valueOf(digit.toLong()))
    }

    for (element in this) {
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

fun String.radixNDecode2String(radix: Int = 58, maps: String = BASE58_DICT) =
    String(radixNDecode(radix, maps))
