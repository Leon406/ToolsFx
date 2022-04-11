package me.leon.ext

fun String.distinct() = toCharArray().distinct().joinToString("")

// [ \f\n\r\t\v]
fun String.stripAllSpace() = replace("\\s+".toRegex(), "")

fun String.splitBySpace() = split("\\s+".toRegex())

fun String.splitByNonDigit() = split("\\D+".toRegex()).filter { it.isNotEmpty() }

fun String.sorted() = toCharArray().sorted().joinToString("")

fun String.letters() = replace("[^a-zA-Z]".toRegex(), "")

fun String.lineAction2String(action: (String) -> String) =
    lineSplit().joinToString("\n") { action.invoke(it) }

inline fun <T> String.lineAction(action: (String) -> T) =
    split("\n|\r\n".toRegex()).map { action.invoke(it) }

fun String.lineSplit() = split("\n|\r\n".toRegex())

fun String.lineCount() = lineSplit().size

fun String.lineActionIndex(action: (String, Int) -> String) =
    lineSplit().mapIndexed { index, s -> action.invoke(s, index) }.joinToString("\n")

fun String.sliceList(split: List<Int>, delimiter: String = " ") =
    toList().sliceList(split).joinToString(delimiter) { it.joinToString("") }
