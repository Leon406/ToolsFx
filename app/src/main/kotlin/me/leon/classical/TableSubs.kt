package me.leon.classical

fun String.tableDecode(table: String, delimiter: String = " ", startCode: Char = 'A') =
    with(table.split("\\s+|$delimiter".toRegex())) {
        uppercase()
            .split("\\s+|$delimiter".toRegex())
            .map { startCode + this.indexOf(it) }
            .joinToString("")
    }

fun String.tableEncode(table: String, delimiter: String = " ", startCode: Char = 'A') =
    with(table.split("\\s+|$delimiter".toRegex())) {
        uppercase()
            .filter { it.isUpperCase() }
            .asIterable()
            .joinToString(delimiter) { this[it - startCode] }
    }
