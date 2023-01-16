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

fun String.unescape() =
    replace("""(?!\\)\\n""".toRegex(), "\n")
        .replace("""(?!\\)\\r""".toRegex(), "\r")
        .replace("""(?!\\)\\t""".toRegex(), "\t")

fun String.lineActionIndex(action: (String, Int) -> String) =
    lines().mapIndexed { index, s -> action.invoke(s, index) }.joinToString("\n")

fun String.sliceList(split: List<Int>, delimiter: String = " ") =
    toList().sliceList(split).joinToString(delimiter) { it.joinToString("") }

fun String.random(count: Int): String {
    val sb = StringBuilder()
    repeat(count) { sb.append(random()) }
    return sb.toString()
}
