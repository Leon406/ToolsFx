package me.leon.classical

fun String.zero1248() =
    uppercase()
        .toCharArray()
        .filter { it in 'A'..'Z' }
        .map { (it - 'A' + 1).zero1248() }
        .joinToString("0")

private fun Int.zero1248() =
    with(StringBuilder()) {
        repeat(this@zero1248 / 8) { append("8") }
        repeat(this@zero1248 % 8 / 4) { append("4") }
        repeat(this@zero1248 % 4 / 2) { append("2") }
        repeat(this@zero1248 % 2 / 1) { append("1") }
        this.toString()
    }

fun String.zero1248Decode() =
    trim()
        .split("0".toRegex())
        .map { (it.map { it.code - '0'.code }.sum() + 'A'.code - 1).toChar() }
        .joinToString("")
