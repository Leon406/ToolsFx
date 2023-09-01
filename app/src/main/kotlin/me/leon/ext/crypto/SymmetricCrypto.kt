package me.leon.ext.crypto

import java.io.File
import javax.crypto.*
import javax.crypto.spec.*
import org.bouncycastle.jcajce.spec.AEADParameterSpec

val AEAD_MODE_REG = "GCM|EAX|CCM|OCB|ChaCha20-Poly1305".toRegex()

fun ByteArray.encrypt(
    key: ByteArray,
    iv: ByteArray,
    alg: String,
    associatedData: ByteArray = byteArrayOf()
): ByteArray = makeCipher(alg, key, iv, Cipher.ENCRYPT_MODE, associatedData).doFinal(this)

fun ByteArray.decrypt(
    key: ByteArray,
    iv: ByteArray,
    alg: String,
    associatedData: ByteArray = byteArrayOf()
): ByteArray = makeCipher(alg, key, iv, Cipher.DECRYPT_MODE, associatedData).doFinal(this)

fun makeCipher(
    alg: String,
    key: ByteArray,
    iv: ByteArray,
    cipherMode: Int,
    associatedData: ByteArray = byteArrayOf()
): Cipher =
    Cipher.getInstance(alg).apply {
        val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))
        if (alg.contains("ECB|RC4".toRegex())) {
            init(cipherMode, keySpec)
        }
        // require jdk 11
        else if (alg.equals("ChaCha20", true)) {
            init(
                cipherMode,
                keySpec,
                runCatching { ChaCha20ParameterSpec(iv, 0) }
                    .getOrElse { AEADParameterSpec(iv, 128) }
            )
        } else {
            init(cipherMode, keySpec, IvParameterSpec(iv)).also {
                if (alg.contains(AEAD_MODE_REG) && associatedData.isNotEmpty()) {
                    updateAAD(associatedData)
                }
            }
        }
    }

fun String.encryptFile(
    key: ByteArray,
    iv: ByteArray,
    alg: String,
    outFileName: String,
    associatedData: ByteArray = byteArrayOf()
) {
    val cipher = makeCipher(alg, key, iv, Cipher.ENCRYPT_MODE, associatedData)
    doStreamCrypto(outFileName, cipher, this)
}

fun String.decryptFile(
    key: ByteArray,
    iv: ByteArray,
    alg: String,
    outFileName: String,
    associatedData: ByteArray = byteArrayOf()
) {
    val cipher = makeCipher(alg, key, iv, Cipher.DECRYPT_MODE, associatedData)
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
