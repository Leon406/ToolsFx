package me.leon

import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import me.leon.ext.toHex

object Digests {
    fun hash(method: String, data: String) =
        MessageDigest.getInstance(method).apply { update(data.toByteArray()) }.digest().toHex()

    fun hashByFile(method: String, path: String): String {
        val fis = FileInputStream(path)
        var md = MessageDigest.getInstance(method)
        val dis = DigestInputStream(fis, md)
        val buffer = ByteArray(1024 * 256)
        DigestInputStream(fis, md).use { while (dis.read(buffer) > 0) {} }
        md = dis.messageDigest
        return md.digest().toHex()
    }

    fun hashFile2(method: String, path: String): String {
        var md = MessageDigest.getInstance(method)
        File(path).inputStream().buffered().use {
            var buf = ByteArray(DEFAULT_BUFFER_SIZE)
            var len: Int
            while (it.read(buf).also { len = it } != -1) {
                md.update(buf, 0, len)
            }
        }

        return md.digest().toHex()
    }
}
