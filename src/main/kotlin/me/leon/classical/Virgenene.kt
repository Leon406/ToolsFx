package me.leon.classical

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
