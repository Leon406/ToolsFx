package me.leon.toolsfx.plugin

fun String.prettyJson(): String {
    if (contains("\n")) return this
    var indentNumber = 0
    // two space
    val s = "  "
    return fold(StringBuilder()) { acc, c ->
            acc.apply {
                when (c) {
                    '{', '[' -> append(c).appendLine().append(s.repeat(++indentNumber))
                    ',' -> append(c).appendLine().append(s.repeat(indentNumber))
                    ':' -> append(c).append(" ")
                    '}', ']' -> appendLine().append(s.repeat(--indentNumber)).append(c)
                    else -> append(c)
                }
            }
        }
        .toString()
}
