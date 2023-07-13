package me.leon.classical

import me.leon.ext.*

fun String.autoKey(keyword: String): String {
    val key = keyword.uppercase()
    val stripText = this.stripAllSpace()
    val splits = splitBySpace().map { it.length }
    return stripText.virgeneneEncode(key + stripText).sliceList(splits)
}

fun String.autoKeyDecrypt(keyword: String): String {
    val key = keyword.uppercase()
    val splits = splitBySpace().map { it.length }
    val stripText = this.stripAllSpace()
    val keyBuilder = StringBuilder(stripText.length + key.length)
    keyBuilder.append(key)
    while (keyBuilder.length < stripText.length + key.length) {
        val substring =
            stripText
                .virgeneneDecode(keyBuilder.toString(), keyBuilder.length)
                .substring(
                    keyBuilder.length - key.length,
                    keyBuilder.length.takeIf { it < stripText.length } ?: stripText.length
                )
        keyBuilder.append(substring)
    }

    return keyBuilder.toString().replaceFirst(key, "").sliceList(splits)
}
