package me.leon.base

import java.math.BigInteger

const val BASE_58_DICT = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

fun String.baseNEncode(radix: Int = 58, maps: String = BASE_58_DICT): String {
    return toByteArray().baseNEncode(radix, maps)
}

fun ByteArray.baseNEncode(radix: Int = 58, maps: String = BASE_58_DICT): String {
    var bigInteger = BigInteger(this)
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

fun String.baseNDecode(radix: Int = 58, maps: String = BASE_58_DICT) =
    toCharArray()
        .mapIndexed { index, c -> length - index - 1 to maps.indexOf(c) }
        .fold(0.toBigInteger()) { acc, pair ->
            acc.add(pair.second.toBigInteger().multiply(radix.toBigInteger().pow(pair.first)))
        }
        .toByteArray()

fun String.baseNDecode2String(radix: Int = 58, maps: String = BASE_58_DICT) =
    String(baseNDecode(radix, maps))
