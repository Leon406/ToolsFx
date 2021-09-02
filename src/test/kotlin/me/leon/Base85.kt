package me.leon

import me.leon.base.BYTE_BITS
import me.leon.base.baseNDecode
import me.leon.base.baseNEncode
import me.leon.ext.toBinaryString

const val BASE85_MAP =
    "!\"#\$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
const val BASE64_BLOCK_SIZE = 6
const val BASE64_PADDING_SIZE = 4
const val BYTE_MASK = 0xFF

fun String.base64() =
    toByteArray()
        .toBinaryString()
        .chunked(BASE64_BLOCK_SIZE)
        //        .also { println(it.joinToString("")) }
        .joinToString("") { BASE85_MAP[it.padding("0", BASE64_BLOCK_SIZE).toInt(2)].toString() }
        .padding("=", BASE64_PADDING_SIZE) // lcm (6, 8) /6 = 4

fun String.base85() =
    toByteArray()
        .toList()
        .chunked(4)
        .map {
            if (it.size != 4)
                ArrayList(it).apply {
                    println("___ ${this.size}")
                    for (i in 0..(4 - 1 - it.size)) add(0.toByte()).also { println("add $i") }

                    println("___after ${this.size}")
                }
            else it
        }
        .fold(StringBuilder()) { acc, list ->
            acc.apply { acc.append(list.toByteArray().baseNEncode(85, BASE85_MAP)) }
        }

fun String.base85Decode() =
    String(
        toList()
            .chunked(5)
            .map { it.joinToString("").baseNDecode(85, BASE85_MAP) }
            .fold(mutableListOf<Byte>()) { acc, bytes -> acc.apply { acc.addAll(bytes.toList()) } }
            .filterNot { it in 0..31 || it in 128..255 }
            .toByteArray()
    )

fun ByteArray.base64() =
    toBinaryString()
        .chunked(BASE64_BLOCK_SIZE)
        //        .also { println(it.joinToString("")) }
        .joinToString("") { BASE85_MAP[it.padding("0", BASE64_BLOCK_SIZE).toInt(2)].toString() }
        .padding("=", BASE64_PADDING_SIZE) // lcm (6, 8) /6 = 4

/** 标准的Base64并不适合直接放在URL里传输，因为URL编码器会把标准Base64中的“/”和“+”字符变为形如“%XX”的形式， */
fun String.safeBase64() =
    toByteArray()
        .toBinaryString()
        .chunked(BASE64_BLOCK_SIZE)
        //        .also { println(it.joinToString("")) }
        .joinToString("") { BASE85_MAP[it.padding("0", BASE64_BLOCK_SIZE).toInt(2)].toString() }
        .padding("=", BASE64_PADDING_SIZE)
        .replace("/", "_")
        .replace("+", "-")

fun String.base64DecodeString() =
    String(
        toCharArray()
            .filter { it != '=' }
            .joinToString("") {
                BASE85_MAP.indexOf(it).toString(2).padding("0", BASE64_BLOCK_SIZE, false)
            }
            .chunked(BYTE_BITS)
            .filter { it.length == BYTE_BITS }
            //            .also { println(it.joinToString("")) }
            .map { it.toInt(2).toByte() }
            .toByteArray()
    )

fun String.base64Decode() =

    //    Base64.getDecoder().decode(this)
    toCharArray()
        .filter { it != '=' }
        .joinToString("") {
            BASE85_MAP.indexOf(it).toString(2).padding("0", BASE64_BLOCK_SIZE, false)
        }
        .chunked(BYTE_BITS)
        .filter { it.length == BYTE_BITS }
        .map { (it.toInt(2) and BYTE_MASK).toByte() }
        .toByteArray()

fun String.safeBase64Decode2() =
    String(
        this.replace("_", "/")
            .replace("-", "+")
            .toCharArray()
            .filter { it != '=' }
            .joinToString("") {
                BASE85_MAP.indexOf(it).toString(2).padding("0", BASE64_BLOCK_SIZE, false)
            }
            .chunked(BYTE_BITS)
            .filter { it.length == BYTE_BITS }
            .map { it.toInt(2).toByte() }
            .toByteArray()
    )

fun String.padding(char: String, block: Int, isAfter: Boolean = true) =
    chunked(block).joinToString("") {
        it.takeIf { it.length == block }
            ?: if (isAfter) it + char.repeat(block - it.length)
            else char.repeat(block - it.length) + it
    }
