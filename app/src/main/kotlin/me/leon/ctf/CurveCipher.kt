package me.leon.ctf

fun String.curveCipher(row: Int, col: Int) =
    chunked(col).run {
        var reverseFlag = false
        val sb = StringBuilder()
        for (i in this@curveCipher.indices) {
            if (i % row == 0) {
                reverseFlag = !reverseFlag
            }
            val listIndex = if (reverseFlag) i % row else row - i % row - 1
            sb.append(this[listIndex][i / row])
        }
        sb.reverse().toString()
    }

fun String.curveCipherDecode(row: Int, col: Int): String {
    return with(this) {
        var reverseFlag = col % 2 == 0
        val c = CharArray(length)
        for (i in indices) {
            if (i % row == 0) {
                reverseFlag = !reverseFlag
            }
            val listIndex = if (reverseFlag) i % row else row - i % row - 1
            c[i / row + listIndex * col] = this[i]
        }
        c.joinToString("").reversed()
    }
}
