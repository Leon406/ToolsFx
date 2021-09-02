package me.leon.base

import java.math.BigInteger

const val BASE_58_DICT = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

fun String.baseNEncode(radix: Int = 58, maps: String = BASE_58_DICT): String {
    return toByteArray().baseNEncode(radix, maps)
}

fun ByteArray.baseNEncode(radix: Int = 58, maps: String = BASE_58_DICT): String {
    var bigInteger = BigInteger(1, this)
    var remainder: Int
    val sb = StringBuilder()
    while (bigInteger != BigInteger.ZERO) {
        bigInteger.divideAndRemainder(radix.toBigInteger()).run {
            bigInteger = this[0]
            remainder = this[1].toInt()
        }
        sb.append(maps[remainder])
    }
    return sb.reversed().toString()
}

fun String.baseNDecode(radix: Int = 58, maps: String = BASE_58_DICT): ByteArray {
    if (this.isEmpty()) {
        return ByteArray(0)
    }
    var intData = BigInteger.ZERO
    var leadingZeros = 0
    for (i in this.indices) {
        val current = this[i]
        val digit = maps.indexOf(current)
        require(digit != -1) {
            String.format("Invalid Base58 character `%c` at position %d", current, i)
        }
        intData = intData.multiply(radix.toBigInteger()).add(BigInteger.valueOf(digit.toLong()))
    }

    for (element in this) {
        if (element == '1') {
            leadingZeros++
        } else {
            break
        }
    }
    val bytesData: ByteArray =
        if (intData == BigInteger.ZERO) {
            ByteArray(0)
        } else {
            intData.toByteArray()
        }

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

fun String.baseNDecode2String(radix: Int = 58, maps: String = BASE_58_DICT) =
    String(baseNDecode(radix, maps))
