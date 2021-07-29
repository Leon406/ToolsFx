package me.leon.controller

import java.io.File
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import me.leon.base.base64
import tornadofx.*

class SymmetricCryptoController : Controller() {
    fun encrypt(key: ByteArray, data: String, iv: ByteArray, alg: String): String =
        try {
            println("encrypt  $alg")
            val cipher = makeCipher(alg, key, iv, Cipher.ENCRYPT_MODE)
            Base64.getEncoder().encodeToString(cipher.doFinal(data.toByteArray()))
        } catch (e: Exception) {
            e.printStackTrace()
            "encrypt error: ${e.message}"
        }

    private fun makeCipher(alg: String, key: ByteArray, iv: ByteArray, cipherMode: Int) =
        Cipher.getInstance(alg).apply {
            val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))
            if (alg.contains("ECB|RC4".toRegex())) init(cipherMode, keySpec)
            else init(cipherMode, keySpec, IvParameterSpec(iv))
        }

    fun encryptByFile(key: ByteArray, path: String, iv: ByteArray, alg: String) =
        try {
            println("encrypt  $alg")
            val cipher = makeCipher(alg, key, iv, Cipher.ENCRYPT_MODE)
            doStreamCrypto("$path.enc", cipher, path)
            "加密文件路径(同选择文件目录): ${File("$path.enc").absolutePath} \n" +
                "alg: $alg\n" +
                "key(base64): ${key.base64()}\n" +
                "iv(base64): ${iv.base64()}\n"
        } catch (e: Exception) {
            e.printStackTrace()
            "encrypt error: ${e.message}"
        }

    fun decryptByFile(key: ByteArray, path: String, iv: ByteArray, alg: String) =
        try {
            println("decrypt  $alg")
            val cipher = makeCipher(alg, key, iv, Cipher.DECRYPT_MODE)
            val outFileName = if (path.endsWith(".enc")) path.replace(".enc", "") else "$path.dec"
            doStreamCrypto(outFileName, cipher, path)

            "解密文件路径(同选择文件目录): $outFileName"
        } catch (e: Exception) {
            e.printStackTrace()
            "decrypt error: ${e.message}"
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

    fun decrypt(key: ByteArray, data: String, iv: ByteArray, alg: String) =
        try {
            println("decrypt  $alg")
            val cipher = makeCipher(alg, key, iv, Cipher.DECRYPT_MODE)
            String(cipher.doFinal(Base64.getDecoder().decode(data)))
        } catch (e: Exception) {
            e.printStackTrace()
            "decrypt error: ${e.message}"
        }
}
