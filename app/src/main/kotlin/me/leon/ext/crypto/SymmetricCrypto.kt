package me.leon.ext.crypto

import java.io.File
import javax.crypto.*
import javax.crypto.spec.*

fun ByteArray.encrypt(key: ByteArray, iv: ByteArray, alg: String): ByteArray =
    makeCipher(alg, key, iv, Cipher.ENCRYPT_MODE).doFinal(this)

fun ByteArray.decrypt(key: ByteArray, iv: ByteArray, alg: String): ByteArray =
    makeCipher(alg, key, iv, Cipher.DECRYPT_MODE).doFinal(this)

fun makeCipher(alg: String, key: ByteArray, iv: ByteArray, cipherMode: Int): Cipher =
    Cipher.getInstance(alg).apply {
        val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))
        if (alg.contains("ECB|RC4".toRegex())) init(cipherMode, keySpec)
        // require jdk 11
        else if (alg.equals("ChaCha20", true))
            init(cipherMode, keySpec, ChaCha20ParameterSpec(iv, 7))
        else init(cipherMode, keySpec, IvParameterSpec(iv))
    }

fun String.encryptFile(key: ByteArray, iv: ByteArray, alg: String, outFileName: String) {
    val cipher = makeCipher(alg, key, iv, Cipher.ENCRYPT_MODE)
    doStreamCrypto(outFileName, cipher, this)
}

fun String.decryptFile(key: ByteArray, iv: ByteArray, alg: String, outFileName: String) {
    val cipher = makeCipher(alg, key, iv, Cipher.DECRYPT_MODE)
    doStreamCrypto(outFileName, cipher, this)
}

private fun doStreamCrypto(outFileName: String, cipher: Cipher, path: String) {
    CipherOutputStream(File(outFileName).outputStream(), cipher).use { cipherStream ->
        File(path).inputStream().buffered().use { bis ->
            val buf = ByteArray(DEFAULT_BUFFER_SIZE)
            var len: Int
            while (bis.read(buf).also { len = it } != -1) {
                cipherStream.write(buf, 0, len)
            }
        }
    }
}
