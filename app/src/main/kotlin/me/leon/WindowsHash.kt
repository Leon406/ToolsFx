package me.leon

import me.leon.ext.*
import me.leon.ext.crypto.encrypt

private const val LM_DATA = "KGS!@#$%"

fun String.lmHash(): String {
    require(length < 15) { "password length needs less than  15" }
    return uppercase().toHex().padEnd(28, '0').chunked(14).joinToString("") {
        val key =
            it.hex2ByteArray().toBinaryString().chunked(7).joinToString("") {
                (it + "0").binary2ByteArray().toHex()
            }

        LM_DATA.toByteArray()
            .encrypt(key.hex2ByteArray(), byteArrayOf(), "DES/ECB/PKCS5Padding")
            .toHex()
            .substring(0, 16)
            .uppercase()
    }
}

fun ByteArray.lmHash(): String {
    return decodeToString().lmHash()
}

fun String.ntlmHash() = toByteArray(Charsets.UTF_16LE).hash("MD4").toHex().uppercase()

fun ByteArray.ntlmHash(charset: String = UTF8) =
    toString(charset.toCharset()).toByteArray(Charsets.UTF_16LE).hash("MD4").toHex().uppercase()
