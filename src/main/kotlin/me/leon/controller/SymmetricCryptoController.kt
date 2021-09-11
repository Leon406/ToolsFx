package me.leon.controller

import java.io.File
import java.nio.charset.Charset
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import me.leon.encode.base.base64
import me.leon.ext.catch
import tornadofx.*

class SymmetricCryptoController : Controller() {
    fun encrypt(
        key: ByteArray,
        data: String,
        iv: ByteArray,
        alg: String,
        charset: String = "UTF-8"
    ): String =
        catch({ "encrypt error: $it" }) {
            println("encrypt  $alg")
            val cipher = makeCipher(alg, key, iv, Cipher.ENCRYPT_MODE)
            Base64.getEncoder()
                .encodeToString(cipher.doFinal(data.toByteArray(Charset.forName(charset))))
        }

    private fun makeCipher(alg: String, key: ByteArray, iv: ByteArray, cipherMode: Int) =
        Cipher.getInstance(alg).apply {
            val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))
            if (alg.contains("ECB|RC4".toRegex())) init(cipherMode, keySpec)
            else init(cipherMode, keySpec, IvParameterSpec(iv))
        }

    fun encryptByFile(key: ByteArray, path: String, iv: ByteArray, alg: String): String {
        val outFileName = path.substringBefore('.') + ".enc." + path.substringAfterLast('.')
        return catch({ "encrypt error: $it" }) {
            println("encrypt  $alg")
            val cipher = makeCipher(alg, key, iv, Cipher.ENCRYPT_MODE)
            doStreamCrypto(outFileName, cipher, path)
            "加密文件路径(同选择文件目录): ${File(outFileName).absolutePath} \n" +
                "alg: $alg\n" +
                "key(base64): ${key.base64()}\n" +
                "iv(base64): ${iv.base64()}\n"
        }
    }

    fun decryptByFile(key: ByteArray, path: String, iv: ByteArray, alg: String) =
        catch({ "decrypt error: $it" }) {
            println("decrypt  $alg")
            val cipher = makeCipher(alg, key, iv, Cipher.DECRYPT_MODE)
            val outFileName =
                if (path.contains(".enc")) path.replace(".enc", "_dec")
                else path.substringBeforeLast('.') + "_dec." + path.substringAfterLast('.')
            doStreamCrypto(outFileName, cipher, path)
            "解密文件路径(同选择文件目录): $outFileName"
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

    fun decrypt(
        key: ByteArray,
        data: String,
        iv: ByteArray,
        alg: String,
        charset: String = "UTF-8"
    ) =
        catch({ "decrypt error: $it" }) {
            println("decrypt  $alg")
            val cipher = makeCipher(alg, key, iv, Cipher.DECRYPT_MODE)
            String(cipher.doFinal(Base64.getDecoder().decode(data)), Charset.forName(charset))
        }
}
