package me.leon.classical

/** refer http://www.practicalcryptography.com/ciphers/classical-era/bifid/ */
fun String.bifid(key: String, period: Int = 1) =
    filter { it.isLetter() }
        .polybius(key)
        .chunked(period * 2)
        .joinToString("") {
            it.filterIndexed { index, _ -> index % 2 == 0 } +
                it.filterIndexed { index, _ -> index % 2 == 1 }
        }
        .polybiusDecrypt(key)

fun String.bifidDecrypt(key: String, period: Int = 1) =
    filter { it.isLetter() }
        .polybius(key)
        .chunked(period * 2)
        .joinToString("") {
            it.foldIndexed(CharArray(it.length)) { index, acc, c ->
                    acc.apply {
                        if (index < this.size / 2) {
                            this[index * 2] = c
                        } else {
                            this[(index - this.size / 2) * 2 + 1] = c
                        }
                    }
                }
                .joinToString("")
        }
        .polybiusDecrypt(key)
