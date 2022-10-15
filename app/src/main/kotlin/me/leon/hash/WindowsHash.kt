package me.leon.hash

import me.leon.UTF8
import me.leon.ext.*
import me.leon.ext.crypto.encrypt
import me.leon.hash

private const val LM_DATA = "KGS!@#$%"
private const val DES_ALG = "DES/ECB/PKCS5Padding"

fun String.lmHash(): String {
    if (length > 14) return ""
    return uppercase().toHex().padEnd(28, '0').chunked(14).joinToString("") {
        val key =
            it.hex2ByteArray().toBinaryString().chunked(7).joinToString("") {
                (it + "0").binary2ByteArray().toHex()
            }

        LM_DATA.toByteArray()
            .encrypt(key.hex2ByteArray(), byteArrayOf(), DES_ALG)
            .toHex()
            .substring(0, 16)
    }
}

fun ByteArray.lmHash(): String {
    return decodeToString().lmHash()
}

fun String.ntlmHash() = toByteArray(Charsets.UTF_16LE).hash("MD4").toHex()

fun ByteArray.ntlmHash(charset: String = UTF8) =
    toString(charset.toCharset()).toByteArray(Charsets.UTF_16LE).hash("MD4").toHex()
