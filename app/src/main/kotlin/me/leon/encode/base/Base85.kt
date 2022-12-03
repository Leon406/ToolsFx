package me.leon.encode.base

const val BASE85_DICT =
    """!"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstu"""

const val Z85_DICT =
    """0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#"""

const val BASE85_IPV6_DICT =
    """0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&()*+-;<=>?@^_`{|}~"""

fun ByteArray.base85(dict: String = BASE85_DICT): String =
    asIterable()
        .chunked(4)
        .map {
            if (it.size < 4) ArrayList(it).apply { repeat(4 - it.size) { add(0.toByte()) } } else it
        }
        .fold(StringBuilder()) { acc, list ->
            acc.append(list.toByteArray().radixNEncode(dict.ifEmpty { BASE85_DICT }))
        }
        .toString()
        .run { substring(0, length - (4 - size % 4) % 4) }

fun String.base85Decode(dict: String = BASE85_DICT) =
    map { dict.ifEmpty { BASE85_DICT }.indexOf(it) }
        .chunked(5)
        .map {
            val count =
                when (it.size) {
                    2 -> 1
                    3 -> 2
                    4 -> 3
                    else -> 4
                }
            (it.getOrElse(0) { 0 } * 85 * 85 * 85 * 85 +
                    it.getOrElse(1) { 255 } * 85 * 85 * 85 +
                    it.getOrElse(2) { 255 } * 85 * 85 +
                    it.getOrElse(3) { 255 } * 85 +
                    it.getOrElse(4) { 255 })
                .toBigInteger()
                .toByteArray()
                .take(count)
        }
        .flatten()
        .toByteArray()
