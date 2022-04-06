package me.leon

import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import me.leon.ext.toFile
import me.leon.ext.toHex

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

fun String.hash(method: String = "MD5"): String = Digests.hash(method, this)

fun ByteArray.hash(method: String = "MD5"): ByteArray = Digests.hash(method, this)

fun ByteArray.hash2String(method: String = "MD5"): String = Digests.hash(method, this).toHex()

fun File.hash(method: String = "MD5"): String = Digests.hashByFile(method, this.absolutePath)

fun String.fileHash(method: String = "MD5"): String =
    Digests.hashByFile(method, this.toFile().absolutePath)
