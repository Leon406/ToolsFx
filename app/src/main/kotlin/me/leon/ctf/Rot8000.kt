package me.leon.ctf

private const val SIZE = 63_404
private const val ROTATE = SIZE / 2

/** @link https://github.com/rottytooth/rot8000 */
fun String.rot8000(shift: Int = ROTATE) =
    map { if (it == ' ') it else indexRe(((index(it) + shift) % SIZE)).toChar() }.joinToString("")

private fun index(char: Char) =
    when (char.code) {
        in 33 until 127 -> char.code - 33 // 94
        in 161 until 5760 -> char.code - 67 // 5599
        in 5761 until 8192 -> char.code - 68 // 2431
        in 8203 until 8232 -> char.code - 79 // 29
        in 8234 until 8239 -> char.code - 81 // 5
        in 8240 until 8287 -> char.code - 81 // 47
        in 8288 until 12_288 -> char.code - 83 // 4000
        in 12_289 until 55_296 -> char.code - 84 // 43007
        in 57_344 until 65_536 -> char.code - 2132
        else -> -char.code
    }

private fun indexRe(index: Int) =
    when (index) {
        in 0 until 94 -> index + 33 // 94
        in 94 until 5693 -> index + 67 // 5599
        in 5693 until 8124 -> index + 68 // 2431
        in 8124 until 8153 -> index + 79 // 29
        in 8153 until 8158 -> index + 81 // 5
        in 8158 until 8205 -> index + 81 // 47
        in 8205 until 12_205 -> index + 83 // 4000
        in 12_205 until 55_212 -> index + 84 // 43007
        in 55_212 until 65_536 -> index + 2132
        else -> index
    }
