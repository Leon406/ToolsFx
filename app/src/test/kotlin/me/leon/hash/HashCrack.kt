package me.leon.hash

/**
 *
 * @author Leon
 * @since 2022-11-07 13:46
 * @email: deadogone@gmail.com
 */
const val D_DIGIT_DICT = "0123456789"
const val L_LOWER_LETTER_DICT = "abcdefghijklmnopqrstuvwxyz"
const val U_UPPER_LETTER_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val S_SPECIAL_DICT = "!\"#\$%&'()*+,-./:;<=>?@[]^_`{|}~"

/**
 * hashcat 掩码
 *
 * l abcdefghijklmnopqrstuvwxyz u ABCDEFGHIJKLMNOPQRSTUVWXYZ d 0123456789 h 0123456789abcdef H
 * 0123456789ABCDEF s !"#$%&'()*+,-./:;<=>?@[]^_`{|}~ a 键盘上所有可见的字符
 */
fun String.maskCrack(dict: String, condition: (String) -> Boolean): String? {
    val sb = StringBuilder()
    var sq: Sequence<String>? = null
    for (c in this) {
        if (c == '?') {
            sq =
                sq?.run {
                    if (sb.isEmpty()) {
                        sq!!.next(dict)
                    } else {
                        sq!!.nextFix(sb.toString()).next(dict)
                    }
                }
                    ?: sb.toString().next(dict)
            sb.clear()
        } else {
            sb.append(c)
        }
    }

    if (sb.isNotEmpty()) {
        sq = sq?.nextFix(sb.toString())
    }

    return sq?.find { condition.invoke(it) }
}

fun String.sliceCount(count: Int) =
    with(length / count) {
        if (this == 0) {
            toList().map { it.toString() }
        } else {
            chunked(this)
        }
    }

fun String.random(count: Int): String {
    val sb = StringBuilder()
    repeat(count) { sb.append(random()) }
    return sb.toString()
}

fun String.maskCrackParallel(dict: String, condition: (String) -> Boolean): String? {

    val sb = StringBuilder()
    val d = dict.sliceCount(Runtime.getRuntime().availableProcessors())
    var sqs = mutableListOf<Sequence<String>>()
    for (c in this) {
        if (c == '?') {
            if (sqs.isEmpty()) {
                sqs.addAll(d.map { sb.toString().next(it) })
            } else {
                sqs =
                    sqs.map {
                            it.run {
                                if (sb.isEmpty()) {
                                    it.next(dict)
                                } else {
                                    it.nextFix(sb.toString()).next(dict)
                                }
                            }
                        }
                        .toMutableList()
            }
            sb.clear()
        } else {
            sb.append(c)
        }
    }

    if (sb.isNotEmpty()) {
        sqs = sqs.map { it.nextFix(sb.toString()) }.toMutableList()
    }
    return sqs.parallelStream()
        .map { it.find { condition.invoke(it) } }
        .filter { it != null }
        .findFirst()
        .orElseGet { null }
}

fun String.next(dict: String, count: Int = 1): Sequence<String> =
    with(dict.asSequence().map { this + it }) { if (count > 1) next(dict, count - 1) else this }

fun Sequence<String>.next(dict: String) = this.flatMap { it.next(dict) }

fun Sequence<String>.next(dict: String, count: Int) =
    (1..count).fold(this) { acc, _ -> acc.next(dict) }

fun Sequence<String>.nextFix(fix: String) = fix.fold(this) { acc, c -> acc.next(c.toString()) }
