package me.leon.ctf

const val BRAILLE_DICT =
    "⠐⠑⠒⠓⠔⠕⠖⠗⠘⠙⠚⠛⠜⠝⠞⠟⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⡰⡱⡲⡳⡴⡵⡶⡷⡸⡹⡺⡻⡼⡽⡾⡿⡠⡡⡢⡣⡤⡥⡦⡧⡨⡩⡪⡫⡬⡭⡮⡯⡐⡑⡒⡓⡔⡕⡖⡗⡘⡙⡚⡛⡜⡝⡞⡟⡀⡁⡂⡃⡄⡅⡆⡇⡈⡉⡊⡋⡌⡍⡎"

fun String.blindDecode() =
    asIterable()
        .filterNot { it == '=' }
        .map { (BRAILLE_DICT.indexOf(it) + 32).toChar() }
        .joinToString("")
        .replace("\u001F", "\r\n")

fun String.blindEncode() = asIterable().map { BRAILLE_DICT[it.code - 32] }.joinToString("") + "="
