package me.leon.base

import me.leon.ext.toBinaryString

const val Base64Map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

fun String.base64() =
    toByteArray()
        .toBinaryString()
        .chunked(6)
        //        .also { println(it.joinToString("")) }
        .joinToString("") { Base64Map[it.padding("0", 6).toInt(2)].toString() }
        .padding("=", 4) // lcm (6, 8) /6 = 4

fun ByteArray.base64() =
    toBinaryString()
        .chunked(6)
        //        .also { println(it.joinToString("")) }
        .joinToString("") { Base64Map[it.padding("0", 6).toInt(2)].toString() }
        .padding("=", 4) // lcm (6, 8) /6 = 4

/** 标准的Base64并不适合直接放在URL里传输，因为URL编码器会把标准Base64中的“/”和“+”字符变为形如“%XX”的形式， */
fun String.safeBase64() =
    toByteArray()
        .toBinaryString()
        .chunked(6)
        //        .also { println(it.joinToString("")) }
        .joinToString("") { Base64Map[it.padding("0", 6).toInt(2)].toString() }
        .padding("=", 4)
        .replace("/", "_")
        .replace("+", "-")

fun String.base64DecodeString() =
    String(
        toCharArray()
            .filter { it != '=' }
            .joinToString("") { Base64Map.indexOf(it).toString(2).padding("0", 6, false) }
            .chunked(8)
            .filter { it.length == 8 }
            //            .also { println(it.joinToString("")) }
            .map { it.toInt(2).toByte() }
            .toByteArray()
    )

fun String.base64Decode() =

    //    Base64.getDecoder().decode(this)
    toCharArray()
        .filter { it != '=' }
        .joinToString("") { Base64Map.indexOf(it).toString(2).padding("0", 6, false) }
        .chunked(8)
        .filter { it.length == 8 }
        .map { (it.toInt(2) and 0xFF).toByte() }
        .toByteArray()

fun String.safeBase64Decode2() =
    String(
        this.replace("_", "/")
            .replace("-", "+")
            .toCharArray()
            .filter { it != '=' }
            .joinToString("") { Base64Map.indexOf(it).toString(2).padding("0", 6, false) }
            .chunked(8)
            .filter { it.length == 8 }
            .map { it.toInt(2).toByte() }
            .toByteArray()
    )

fun String.padding(char: String, block: Int, isAfter: Boolean = true) =
    chunked(block).joinToString("") {
        it.takeIf { it.length == block }
            ?: if (isAfter) it + char.repeat(block - it.length)
            else char.repeat(block - it.length) + it
    }
