package me.leon.ctf

import me.leon.ext.binary2Ascii
import me.leon.ext.stripAllSpace

fun String.cetacean() =
    map {
        if (it == ' ') it
        else it.code.toString(2).padStart(16, '0').replace("1", "e").replace("0", "E")
    }
        .joinToString("")
        .also { println(it) }

fun String.cetaceanDecrypt() =
    stripAllSpace().chunked(16).joinToString("") {
        it.takeLast(8).replace("e", "1").replace("E", "0").binary2Ascii()
    }
