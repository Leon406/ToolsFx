package me.leon.ctf

import me.leon.ext.stripAllSpace

fun String.cetacean() =
    map {
            it.takeIf { it == ' ' }
                ?: it.code.toString(2).padStart(16, '0').replace("1", "e").replace("0", "E")
        }
        .joinToString("")

fun String.cetaceanDecrypt() =
    stripAllSpace().chunked(16).joinToString("") {
        it.replace("e", "1").replace("E", "0").toBigInteger(2).toInt().toChar().toString()
    }
