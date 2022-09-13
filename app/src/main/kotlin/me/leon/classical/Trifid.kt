package me.leon.classical

/** refer http://www.practicalcryptography.com/ciphers/classical-era/trifid/ */
fun String.trifid(key: String, period: Int = 1) =
    if (key.length == 27) {
        val squares = key.chunked(9)
        this.uppercase()
            .map { squares.squareIndex(it).joinToString("") }
            .joinToString("")
            .chunked(period * 3)
            .joinToString("") {
                it.filterIndexed { index, _ -> index % 3 == 0 } +
                    it.filterIndexed { index, _ -> index % 3 == 1 } +
                    it.filterIndexed { index, _ -> index % 3 == 2 }
            }
            .chunked(3)
            .map {
                val p = it.map { it - '0' }
                squares.square(p[0], p[1], p[2])
            }
            .joinToString("")
    } else {
        "key length must be 27"
    }

private fun List<String>.square(square: Int, row: Int, col: Int) =
    this[square - 1][col - 1 + 3 * (row - 1)]

private fun List<String>.squareIndex(char: Char) =
    foldIndexed(IntArray(3)) { index, acc, c ->
        acc.apply {
            if (c.contains(char)) {
                this[0] = index + 1
                this[1] = c.indexOf(char) / 3 + 1
                this[2] = c.indexOf(char) % 3 + 1
            }
        }
    }

fun String.trifidDecrypt(key: String, period: Int = 1) =
    if (key.length == 27) {
        val squares = key.chunked(9)
        this.uppercase()
            .map { squares.squareIndex(it).joinToString("") }
            .joinToString("")
            .chunked(period * 3)
            .joinToString("") {
                it.foldIndexed(CharArray(it.length)) { index, acc, c ->
                        acc.apply {
                            val s = index / (this.size / 3)
                            val m = index % (this.size / 3)
                            this[m * 3 + s] = c
                        }
                    }
                    .joinToString("")
            }
            .chunked(3)
            .map {
                val p = it.map { it - '0' }
                squares.square(p[0], p[1], p[2])
            }
            .joinToString("")
    } else {
        "key length must be 27"
    }
