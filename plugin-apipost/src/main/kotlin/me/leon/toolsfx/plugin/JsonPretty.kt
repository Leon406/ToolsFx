package me.leon.toolsfx.plugin

fun String.prettyJson(): String {
    if (contains("\n") || !startsWith("{") && !startsWith("[")) return this
    var indentNumber = 0
    // two space
    val s = "  "
    var quoteCount = 0
    return foldIndexed(StringBuilder()) { index, acc, c ->
            if (quoteCount % 2 == 1 && c != '"') acc.append(c)
            else {
                acc.apply {
                    when (c) {
                        '{', '[' ->
                            if (this@prettyJson[index + 1] == '}' ||
                                    this@prettyJson[index + 1] == ']'
                            ) {
                                append(c)
                            } else append(c).appendLine().append(s.repeat(++indentNumber))
                        ',' ->
                            if (this@prettyJson.substring(index + 1).matches("^\\s*\\w.*".toRegex())
                            ) {
                                append(c)
                            } else append(c).appendLine().append(s.repeat(indentNumber))
                        ':' ->
                            if (this@prettyJson[index - 1] == '"' &&
                                    this@prettyJson[index + 1] != ' '
                            ) {
                                append(c).append(" ")
                            } else append(c)
                        '}', ']' ->
                            if (this@prettyJson[index - 1] == '{' ||
                                    this@prettyJson[index - 1] == '['
                            ) {
                                append(c)
                            } else appendLine().append(s.repeat(--indentNumber)).append(c)
                        '"' ->
                            append(c).also { if (this@prettyJson[index - 1] != '\\') quoteCount++ }
                        else -> append(c)
                    }
                }
            }
        }
        .toString()
}

private val regex =
    "\\s*\\n\\s*|(?<=[:\\[{,])\\s+(?=[\"\\[{]|null)|(?<=\")\\s+(?=[:\\]}])".toRegex()

fun String.uglyJson() = replace(regex, "")
