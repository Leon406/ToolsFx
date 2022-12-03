package me.leon.encode.base

import me.leon.ext.toCharset

// @link https://datatracker.ietf.org/doc/draft-faltstrom-base45/
// 不像Base36 Base64需要padding =
const val BASE45_DICT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:"

fun String.base45(dict: String = BASE45_DICT, charset: String = "UTF-8") =
    toByteArray(charset.toCharset()).base45(dict)

fun ByteArray.base45(dict: String = BASE45_DICT) =
    asIterable().chunked(2).joinToString("") {
        with(it.toByteArray().radixNEncode(dict).reversed()) {
            if (length == 1) "${this[0]}0" else this
        }
    }

fun String.base45Decode(dict: String = BASE45_DICT) =
    chunked(3)
        .map {
            val prop = if (it.length == 2 && it[1] == '0') it[0].toString() else it
            prop.reversed().radixNDecode(dict)
        }
        .flatMap { it.asIterable() }
        .toByteArray()

fun String.base45Decode2String(dict: String = BASE45_DICT, charset: String = "UTF-8") =
    base45Decode(dict).toString(charset.toCharset())
