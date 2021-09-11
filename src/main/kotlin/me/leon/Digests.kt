package me.leon

import me.leon.ext.toHex
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest

object Digests {
    fun hash(method: String, data: String) =
        MessageDigest.getInstance(method).apply { update(data.toByteArray()) }.digest().toHex()

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
