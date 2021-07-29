package me.leon.controller

import me.leon.base.base64
import tornadofx.*
import java.io.File
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SymmetricCryptoController :Controller() {
    fun encrypt(key: ByteArray, data: String, iv: ByteArray, alg: String) =
        try {
            println("encrypt  $alg")
            val cipher = Cipher.getInstance(alg)
            val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))

            if (alg.contains("ECB|RC4".toRegex())) cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            else cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(iv))
            Base64.getEncoder().encodeToString(cipher.doFinal(data.toByteArray()))
        } catch (e: Exception) {
            e.printStackTrace()
            "encrypt error: ${e.message}"
        }

    fun encryptByFile(key: ByteArray, path: String, iv: ByteArray, alg: String) =
        try {
            println("encrypt  $alg")
            val cipher = Cipher.getInstance(alg)
            val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))
            if (alg.contains("ECB|RC4".toRegex())) cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            else cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(iv))

            CipherOutputStream(File("$path.enc").outputStream(), cipher).use { cipherStream ->
                File(path).inputStream().buffered().use {
                    var buf = ByteArray(DEFAULT_BUFFER_SIZE)
                    var len: Int
                    while (it.read(buf).also { len = it } != -1) {
                        cipherStream.write(buf, 0, len)
                    }
                }
            }

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
            val cipher = Cipher.getInstance(alg)
            val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))
            if (alg.contains("ECB|RC4".toRegex())) cipher.init(Cipher.DECRYPT_MODE, keySpec)
            else cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv))

            val outFileName = if (path.endsWith(".enc")) path.replace(".enc", "") else "$path.dec"
            CipherOutputStream(File(outFileName).outputStream(), cipher).use { cipherStream ->
                File(path).inputStream().buffered().use {
                    var buf = ByteArray(DEFAULT_BUFFER_SIZE)
                    var len: Int
                    while (it.read(buf).also { len = it } != -1) {
                        cipherStream.write(buf, 0, len)
                    }
                }
            }

            "解密文件路径(同选择文件目录): $outFileName"
        } catch (e: Exception) {
            e.printStackTrace()
            "decrypt error: ${e.message}"
        }

    fun decrypt(key: ByteArray, data: String, iv: ByteArray, alg: String) =
        try {
            println("decrypt  $alg")
            val cipher = Cipher.getInstance(alg)
            val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))
            if (alg.contains("ECB|RC4".toRegex())) cipher.init(Cipher.DECRYPT_MODE, keySpec)
            else cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv))
            String(cipher.doFinal(Base64.getDecoder().decode(data)))
        } catch (e: Exception) {
            e.printStackTrace()
            "decrypt error: ${e.message}"
        }
}