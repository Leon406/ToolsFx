package me.leon.ext.crypto

/**
 * @author Leon
 * @since 2022-11-07 13:46
 */
const val D_DIGIT_DICT = "0123456789"
const val L_LOWER_LETTER_DICT = "abcdefghijklmnopqrstuvwxyz"
const val U_UPPER_LETTER_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val C_LETTER_DICT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val S_SPECIAL_DICT = "!\"#\$%&'()*+,-./:;<=>?@[]^_`{|}~"
const val A_ALL =
    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#\$%&'()*+,-./:;<=>?@[]^_`{|}~"

val MASK_MAP =
    mapOf(
        'd' to D_DIGIT_DICT,
        'l' to L_LOWER_LETTER_DICT,
        'u' to U_UPPER_LETTER_DICT,
        'c' to C_LETTER_DICT,
        's' to S_SPECIAL_DICT,
        'a' to A_ALL,
        'q' to "?"
    )

fun String.mask(customDict: String = D_DIGIT_DICT, condition: (String) -> Boolean): String? {
    var sq: Sequence<String>? = null
    val dictInfo = parseMask(customDict)
    if (dictInfo.first.isNotEmpty()) {
        sq =
            dictInfo.first
                .first()
                .toString()
                .map { it.toString() }
                .asSequence()
                .nextFix(dictInfo.first.substring(1))
    }
    for (dict in dictInfo.second) {
        sq = sq?.next(dict) ?: dict.map { it.toString() }.asSequence()
    }
    return sq?.find { condition.invoke(it) }
}

fun String.maskParallel(customDict: String, condition: (String) -> Boolean): String? {
    val dictInfo = parseMask(customDict)
    val dicts = dictInfo.second

    var sqs = mutableListOf<Sequence<String>>()
    for (dict in dicts) {
        if (sqs.isEmpty()) {
            // 第一个字典拆分
            sqs.addAll(
                dict.sliceCount(Runtime.getRuntime().availableProcessors()).map {
                    dictInfo.first.next(it)
                }
            )
        } else {
            sqs = sqs.map { it.next(dict) }.toMutableList()
        }
    }

    return sqs.parallelStream()
        .map { it.find { condition.invoke(it) } }
        .filter { it != null }
        .findFirst()
        .orElseGet { null }
}

fun String.sliceCount(count: Int) =
    with(length / count) {
        if (this == 0) {
            map { it.toString() }
        } else {
            chunked(this)
        }
    }

fun String.parseMask(customDict: String = D_DIGIT_DICT) =
    substringBefore("?") to
        substringAfter("?")
            .split('?')
            .filterNot { it.isEmpty() }
            .fold(mutableListOf<String>()) { acc, s ->
                acc.apply {
                    acc.add(MASK_MAP[s.first()] ?: customDict)
                    if (s.length > 1) {
                        acc.addAll(s.substring(1).map { it.toString() })
                    }
                }
            }

fun String.next(dict: String, count: Int = 1): Sequence<String> =
    with(dict.asSequence().map { this + it }) { if (count > 1) next(dict, count - 1) else this }

fun Sequence<String>.next(dict: String) = this.flatMap { it.next(dict) }

fun Sequence<String>.next(dict: String, count: Int) =
    (1..count).fold(this) { acc, _ -> acc.next(dict) }

fun Sequence<String>.nextFix(fix: String) = fix.fold(this) { acc, c -> acc.next(c.toString()) }
