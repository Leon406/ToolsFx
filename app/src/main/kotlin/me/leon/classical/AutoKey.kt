package me.leon.classical

import me.leon.ext.sliceList

fun String.autoKey(keyword: String): String {
    val stripText = this.replace("\\s".toRegex(), "")
    val splits = split("\\s+".toRegex()).map { it.length }.also { println(it) }
    return stripText
        .virgeneneEncode(keyword + stripText.also { println(it) })
        .also { println(it) }
        .sliceList(splits)
}

fun String.autoKeyDecrypt(keyword: String): String {
    val splits = split("\\s".toRegex()).map { it.length }.also { println(it) }
    val stripText = this.replace("\\s".toRegex(), "")
    val keyBuilder = StringBuilder(stripText.length + keyword.length)
    keyBuilder.append(keyword)
    while (keyBuilder.length < stripText.length + keyword.length) {
        val substring =
            stripText
                .virgeneneDecode(keyBuilder.toString(), keyBuilder.length)
                .substring(
                    keyBuilder.length - keyword.length,
                    keyBuilder.length.takeIf { it < stripText.length } ?: stripText.length
                )
        keyBuilder.append(substring)
    }

    return keyBuilder.toString().replaceFirst(keyword, "").sliceList(splits)
}
