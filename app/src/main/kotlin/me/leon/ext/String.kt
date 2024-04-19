package me.leon.ext

fun String.distinct() = asIterable().distinct().joinToString("")

// [ \f\n\r\t\v]
fun String.stripAllSpace() = replace("\\s+".toRegex(), "")

fun String.splitBySpace() = split("\\s+".toRegex())

fun String.splitByNonDigit() = split("\\D+".toRegex()).filter { it.isNotEmpty() }

fun String.sorted() = asIterable().sorted().joinToString("")

fun String.letters() = replace("[^a-zA-Z]".toRegex(), "")

fun String.lineAction2String(action: (String) -> String) =
    lines().joinToString("\n") { action.invoke(it) }

inline fun <T> String.lineAction(action: (String) -> T) = lines().map { action.invoke(it) }

fun String.lineCount() = lines().size

fun String.unescape() = replace("\\\\n", "\n").replace("\\\\r", "\r").replace("\\\\t", "\t")

fun String.lineActionIndex(action: (String, Int) -> String) =
    lines().mapIndexed { index, s -> action.invoke(s, index) }.joinToString("\n")

fun String.sliceList(split: List<Int>, delimiter: String = " ") =
    toList().sliceList(split).joinToString(delimiter) { it.joinToString("") }

fun String.random(count: Int): String {
    val sb = StringBuilder()
    repeat(count) { sb.append(random()) }
    return sb.toString()
}

fun String.toFullWidth() = map { it.fullWidth }.joinToString("")

fun String.toHalfWidth() = map { it.halfWidth }.joinToString("")

fun String.toZhPunctuation() =
    replace(":", "：")
        .replace(",", "，")
        .replace(".", "。")
        .replace("\"([^\"]+)\"".toRegex(), "“$1”")
        .replace("'([^']+)'".toRegex(), "‘$1’")
        .replace("'", "‘")
        .replace("\"", "“")
        .replace(";", "；")
        .replace("?", "？")
        .replace("!", "！")
        .replace("(", "（")
        .replace(")", "）")
        .replace("[", "【")
        .replace("]", "】")
        .replace("<", "《")
        .replace(">", "》")

fun String.toEnPunctuation() =
    replace("：", ":")
        .replace("，", ",")
        .replace("。", ".")
        .replace("“", "\"")
        .replace("”", "\"")
        .replace("‘", "'")
        .replace("’", "'")
        .replace("；", ";")
        .replace("？", "?")
        .replace("！", "!")
        .replace("（", "(")
        .replace("）", ")")
        .replace("《", "<")
        .replace("》", ">")
        .replace("、", ",")

val Char.fullWidth
    get() =
        when (code) {
            32 -> 12_288.toChar()
            in 33..126 -> (code + 65_248).toChar()
            else -> this
        }

val Char.halfWidth
    get() =
        when (code) {
            12_288 -> 32.toChar()
            in 65_281..65_374 -> (code - 65_248).toChar()
            else -> this
        }

fun String.splitParagraph(): MutableList<Pair<IntRange, String>> {
    val splits = mutableListOf<Pair<IntRange, String>>()
    var endIndex: Int
    var startIndex = 0
    while (indexOf("\n", startIndex, false).also { endIndex = it } != -1) {
        val range = startIndex..endIndex
        if (startIndex != endIndex) {
            splits.add(range to substring(range))
        }
        startIndex = endIndex + 1
    }
    if (endIndex == -1) {
        endIndex = lastIndex
        val range = startIndex..endIndex
        splits.add(range to substring(range))
    }
    println("start $startIndex end $endIndex")

    return splits
}

val ALPHABETA_DICT =
    mapOf(
        'a' to 'a',
        'à' to 'a',
        'á' to 'a',
        'ä' to 'a',
        'â' to 'a',
        'é' to 'e',
        'è' to 'e',
        'ê' to 'e',
        'ë' to 'e',
        'ì' to 'i',
        'í' to 'i',
        'ï' to 'i',
        'î' to 'i',
        'ò' to 'o',
        'ó' to 'o',
        'ô' to 'o',
        'ö' to 'o',
        'ù' to 'u',
        'ú' to 'u',
        'û' to 'u',
        'ü' to 'u',
        'ç' to 'c',
        'ñ' to 'n',
    )

fun String.normalCharacter() =
    map { ALPHABETA_DICT[it] ?: it }.joinToString("").replace("’", "'").replace("‘", "'")
