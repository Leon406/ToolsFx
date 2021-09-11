package me.leon

object Caesar {

    private fun String.shift10(bias: Int) =
        this.map { it.takeUnless { it in '0'..'9' } ?: ('0' + (it + bias - '0') % 10) }
            .joinToString("")

    private fun String.rot18() =
        this.lowercase()
            .map {
                it.takeUnless { it in '0'..'9' || it in 'a'..'z' }
                    ?: with(it) {
                        when (this) {
                            in '0'..'9' -> '0' + (this + 5 - '0') % 10
                            else -> 'a' + (this + 13 - 'a') % 26
                        }
                    }
            }
            .joinToString("")

    private fun String.shift26(bias: Int) =
        this.uppercase()
            .map { it.takeUnless { it in 'A'..'Z' } ?: ('A' + (it + bias - 'A') % 26) }
            .joinToString("")

    private fun String.shift94(bias: Int) =
        this.map { it.takeUnless { it in '!'..'~' } ?: ('!' + (it + bias - '!') % 94) }
            .joinToString("")

    private fun String.affine(factor: Int, bias: Int) =
        this.uppercase()
            .map { it.takeUnless { it in 'A'..'Z' } ?: ('A' + (factor * (it - 'A') + bias) % 26) }
            .joinToString("")

    @JvmStatic
    fun main(args: Array<String>) {
        val plain = "hello! yoshiko"
        println(plain.shift26(26))
        val encrypt = "PELCGBTENCUL"
        for (i in 1..25) {
            println(encrypt.shift26(i))
        }

        val rot13 =
            "How can you tell an extrovert from an\n" +
                "introvert at NSA? Va gur ryringbef,\n" +
                "gur rkgebireg ybbxf ng gur BGURE thl'f fubrf. "

        println(rot13)

        println(rot13.shift26(13).also { println(it) }.shift26(13))

        val rot47 = "The Quick Brown Fox Jumps Over The Lazy Dog."
        println(rot47.shift94(47).shift94(47))

        println()
        val dd = "ROT5/13/18/47 is the easiest and yet powerful cipher!"
        println(dd.shift10(5))
        println(dd.rot18())
        println(dd.shift26(13))
        println(dd.shift94(47))

        "123sb".shift10(5).also { println(it) }
        "123sb".rot18().also { println(it) }

        "AFFINECIPHER".affine(5, 8).also { println(it) }
        vig()
    }

    fun String.virgeneneEncode(key: String) =
        uppercase()
            .mapIndexed { index, c ->
                c.takeUnless { it in 'A'..'Z' }
                    ?: ('A' + (c + key[index % key.length].code - 130).code % 26)
            }
            .joinToString("")

    fun String.virgeneneDecode(key: String) =
        uppercase()
            .mapIndexed { index, c ->
                c.takeUnless { it in 'A'..'Z' }
                    ?: ('A' + (c - key[index % key.length].code + 130).code % 26)
            }
            .joinToString("")

    fun vig() {
        "ATTACKATDAWN".virgeneneEncode("LEMONLEMONLE").also { println(it) }
        "CRYPTO IS SHORT FOR CRYPTOGRAPHY".virgeneneEncode("ABCDEF AB CDEFA BCD EFABCDEFABCD")
            .also { println(it) }
        "LXFOPVEFRNHR".virgeneneDecode("LEMONLEMONLE").also { println(it) }
    }
}
