package me.leon.ctf

private val map =
    mapOf(
        0 to arrayOf('目', '口', '凹', '凸', '田'),
        1 to arrayOf('由'),
        2 to arrayOf('中'),
        3 to arrayOf('人', '入', '古'),
        4 to arrayOf('工', '互'),
        5 to arrayOf('果', '克', '尔', '土', '大'),
        6 to arrayOf('木', '王'),
        7 to arrayOf('夫', '主'),
        8 to arrayOf('井', '关', '丰', '并'),
        9 to arrayOf('圭', '羊'),
    )
private val reverseMap =
    mapOf(
        '目' to 0,
        '田' to 0,
        '口' to 0,
        '凹' to 0,
        '凸' to 0,
        '由' to 1,
        '中' to 2,
        '人' to 3,
        '入' to 3,
        '古' to 3,
        '工' to 4,
        '互' to 4,
        '果' to 5,
        '克' to 5,
        '尔' to 5,
        '土' to 5,
        '大' to 5,
        '木' to 6,
        '王' to 6,
        '夫' to 7,
        '主' to 7,
        '井' to 8,
        '关' to 8,
        '丰' to 8,
        '并' to 8,
        '圭' to 9,
        '羊' to 9,
    )

// ASCII
fun String.pawnshop() =
    toCharArray()
        .filter { it.code in 0..127 }
        .map { it.code.split().joinToString("") { map[it]!!.random().toString() } }
        .joinToString(" ")

private fun Int.split() = this.toString().toCharArray().map { it - '0' }

fun String.pawnshopDecode() =
    split("\\s+".toRegex())
        .map { it.toCharArray().map { reverseMap[it] }.joinToString("").toInt().toChar() }
        .joinToString("")
