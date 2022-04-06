package me.leon.classical

fun String.affineEncrypt(factor: Int, bias: Int) =
    uppercase()
        .map { it.takeUnless { it in 'A'..'Z' } ?: ('A' + (factor * (it - 'A') + bias) % 26) }
        .joinToString("")

fun String.affineDecrypt(factor: Int, bias: Int) =
    uppercase()
        .map {
            it.takeUnless { it in 'A'..'Z' }
                ?: ('A' +
                    with(((26 - factor) * (it - 'A' - bias)) % 26) {
                        if (this >= 0) this else (26 + this)
                    })
        }
        .joinToString("")
