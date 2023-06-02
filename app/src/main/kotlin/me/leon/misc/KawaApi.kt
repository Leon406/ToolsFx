package me.leon.misc

import me.leon.ext.*

/**
 * @author Leon
 * @since 2023-06-02 11:15
 * @email deadogone@gmail.com
 */
enum class KawaType(val mode: String) {
    JAPANESE("japanese"),
    HANGUL("hangul"),
    CHINESE("chinese"),
    CANTONESE("cantonese"),
    KANA("kana"),
}

const val KAWA_API = "http://www.kawa.net/works/ajax/romanize/romanize.cgi"

val REG_NO = "<span>([^<]+)</span>".toRegex()
val REG_ROMANJI = "<span title=\"([^\"]+)\">([^<]+)</span>".toRegex()

fun String.kawa(type: KawaType = KawaType.JAPANESE) =
    KAWA_API.readFromNet(
            "POST",
            data = mapOf("mode" to type.mode, "q" to this).toParams(),
            headers = mapOf("Content-type" to "application/x-www-form-urlencoded")
        )
        .lines()
        .mapNotNull {
            (REG_NO.find(it)?.groupValues ?: REG_ROMANJI.find(it)?.groupValues)?.run {
                if (size > 2) {
                    this[2] to this[1]
                } else {
                    this[1] to ""
                }
            }
        }

fun List<Pair<String, String>>.pretty(separator: String = "\t") =
    fold(StringBuilder() to StringBuilder()) { acc, pair ->
            val maxLen = maxOf(pair.first.properLength(), pair.second.properLength())
            acc.first.append(pair.first.center(maxLen)).append(separator)
            acc.second.append(pair.second.center(maxLen)).append(separator)
            acc
        }
        .run { "$second\n$first" }
