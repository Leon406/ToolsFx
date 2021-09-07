package me.leon.encode.base

const val BASE85_DICT =
    "!\"#\$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"

fun String.base85(dict: String = BASE85_DICT) = toByteArray().base85(dict)

fun ByteArray.base85(dict: String = BASE85_DICT) =
    toList()
        .chunked(4)
        .map {
            if (it.size != 4) ArrayList(it).apply { repeat(4 - it.size) { add(0.toByte()) } }
            else it
        }
        .fold(StringBuilder()) { acc, list ->
            acc.apply {
                acc.append(list.toByteArray().baseNEncode(85, dict.ifEmpty { BASE85_DICT }))
            }
        }
        .toString()

fun String.base85Decode(dict: String = BASE85_DICT) =
    toList()
        .chunked(5)
        .map { it.joinToString("").baseNDecode(85, dict.ifEmpty { BASE85_DICT }) }
        .fold(mutableListOf<Byte>()) { acc, bytes -> acc.apply { acc.addAll(bytes.toList()) } }
        .filterNot { it in 0..31 || it in 128..255 }
        .toByteArray()

fun String.base85Decode2String(dict: String = BASE85_DICT) = String(base85Decode(dict))
