package me.leon

import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import me.leon.ext.crypto.passwordHashingTypes
import me.leon.ext.toFile
import me.leon.ext.toHex
import me.leon.hash.CRC_MAPPING

object Digests {

    fun hash(method: String, data: String): String = hashHexString(method, data.toByteArray())

    fun hashHexString(method: String, data: ByteArray): String =
        MessageDigest.getInstance(method).apply { update(data) }.digest().toHex()

    fun hash(method: String, data: ByteArray): ByteArray =
        MessageDigest.getInstance(method).apply { update(data) }.digest()

    fun hashByFile(method: String, path: String): String {
        val fis = FileInputStream(path)
        var md = MessageDigest.getInstance(method)
        val dis = DigestInputStream(fis, md)
        val buffer = ByteArray(1024 * 256)
        DigestInputStream(fis, md).use {
            while (dis.read(buffer) > 0) {
                // nop
            }
        }
        md = dis.messageDigest
        return md.digest().toHex()
    }
}

// https://www.bouncycastle.org/specifications.html
val ALGOS_HASH =
    linkedMapOf(
        "MD5" to listOf("128"),
        "MD5_MIDDLE" to listOf("128"),
        "MD4" to listOf("128"),
        "MD2" to listOf("128"),
        "SM3" to listOf("256"),
        "Tiger" to listOf("192"),
        "Whirlpool" to listOf("512"),
        "SHA1" to listOf("160"),
        "SHA2" to listOf("224", "256", "384", "512", "512/224", "512/256"),
        "SHA3" to listOf("224", "256", "384", "512"),
        "RIPEMD" to listOf("128", "160", "256", "320"),
        "Keccak" to listOf("224", "256", "288", "384", "512"),
        "Blake2b" to listOf("160", "256", "384", "512"),
        "Blake2s" to listOf("160", "224", "256"),
        "DSTU7564" to listOf("256", "384", "512"),
        "Skein" to
            listOf(
                "256-128",
                "256-160",
                "256-224",
                "256-256",
                "512-128",
                "512-160",
                "512-224",
                "512-256",
                "512-384",
                "512-512",
                "1024-384",
                "1024-512",
                "1024-1024"
            ),
        "GOST3411" to listOf("256"),
        "GOST3411-2012" to listOf("256", "512"),
        "Haraka" to listOf("256", "512"),
        "Blake3-256" to listOf("256"),
        "TupleHash" to listOf("128", "256"),
        "ParallelHash" to listOf("128", "256"),
        "CRC" to CRC_MAPPING.keys.toList(),
        "Adler32" to listOf("32"),
        "Windows" to listOf("LM", "NTLM"),
        "PasswordHashing" to passwordHashingTypes,
    )

fun String.hash(method: String = "MD5"): String = Digests.hash(method, this)

fun ByteArray.hash(method: String = "MD5"): ByteArray = Digests.hash(method, this)

fun ByteArray.hash2String(method: String = "MD5"): String = Digests.hash(method, this).toHex()

fun File.hash(method: String = "MD5"): String = Digests.hashByFile(method, this.absolutePath)

fun String.fileHash(method: String = "MD5"): String =
    Digests.hashByFile(method, this.toFile().absolutePath)
