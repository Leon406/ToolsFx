package me.leon.classical

fun String.zero1248() =
    uppercase()
        .asIterable()
        .filter { it in 'A'..'Z' }
        .joinToString("0") { (it - 'A' + 1).zero1248() }

private fun Int.zero1248() = buildString {
    repeat(this@zero1248 / 8) { append("8") }
    repeat(this@zero1248 % 8 / 4) { append("4") }
    repeat(this@zero1248 % 4 / 2) { append("2") }
    repeat(this@zero1248 % 2 / 1) { append("1") }
}

fun String.zero1248Decode() =
    trim().split("0").map { (it.map { it - '0' }.sum() + 'A'.code - 1).toChar() }.joinToString("")
