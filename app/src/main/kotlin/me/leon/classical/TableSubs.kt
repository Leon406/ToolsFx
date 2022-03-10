package me.leon.classical

fun String.tableDecode(table: String, separator: String = " ", startCode: Char = 'A') =
    with(table.split("\\s+|$separator".toRegex())) {
        uppercase()
            .split("\\s+|$separator".toRegex())
            .map { startCode + this.indexOf(it) }
            .joinToString("")
    }

fun String.tableEncode(table: String, separator: String = " ", startCode: Char = 'A') =
    with(table.split("\\s+|$separator".toRegex())) {
        uppercase().filter { it.isUpperCase() }.toCharArray().joinToString(separator) {
            this[it - startCode]
        }
    }
