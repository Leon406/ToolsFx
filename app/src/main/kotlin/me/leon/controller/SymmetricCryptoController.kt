package me.leon.controller

import java.io.File
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import me.leon.encode.base.base64
import me.leon.ext.*
import tornadofx.*

class SymmetricCryptoController : Controller() {
    fun encrypt(
        key: ByteArray,
        data: String,
        iv: ByteArray,
        alg: String,
        charset: String = "UTF-8",
        isSingleLine: Boolean = false,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
    ): String =
        if (isSingleLine)
            data.lineAction2String { encrypt(key, it, iv, alg, charset, inputEncode, outputEncode) }
        else encrypt(key, data, iv, alg, charset, inputEncode, outputEncode)

    private fun encrypt(
        key: ByteArray,
        data: String,
        iv: ByteArray,
        alg: String,
        charset: String = "UTF-8",
        inputEncode: String = "raw",
        outputEncode: String = "base64",
    ): String =
        catch({ "encrypt error: $it" }) {
            println("encrypt  $alg")
            val cipher = makeCipher(alg, key, iv, Cipher.ENCRYPT_MODE)

            val bytes = cipher.doFinal(data.decodeToByteArray(inputEncode, charset))
            if (outputEncode == "base64") {
                Base64.getEncoder().encodeToString(bytes)
            } else if (outputEncode == "hex") {
                bytes.toHex()
            } else {
                throw IllegalArgumentException("output encode error")
            }
        }

    private fun makeCipher(alg: String, key: ByteArray, iv: ByteArray, cipherMode: Int) =
        Cipher.getInstance(alg).apply {
            val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))
            if (alg.contains("ECB|RC4".toRegex())) init(cipherMode, keySpec)
            else init(cipherMode, keySpec, IvParameterSpec(iv))
        }

    fun encryptByFile(key: ByteArray, path: String, iv: ByteArray, alg: String): String {
        return catch({ "encrypt error: $it" }) {
            println("encrypt  $alg")
            val parentFile = path.toFile().parentFile.absolutePath
            val encryptDir =
                File(parentFile, "enc").also { if (!it.exists()) it.mkdirs() }.absolutePath
            val cipher = makeCipher(alg, key, iv, Cipher.ENCRYPT_MODE)
            val outFileName = path.replace(parentFile, encryptDir)
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
            val parentFile = path.toFile().parentFile.absolutePath
            val decryptDir =
                File(parentFile, "dec").also { if (!it.exists()) it.mkdirs() }.absolutePath
            val outFileName = path.replace(parentFile, decryptDir)
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
        charset: String = "UTF-8",
        isSingleLine: Boolean = false,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
    ): String =
        if (isSingleLine)
            data.lineAction2String { decrypt(key, it, iv, alg, charset, inputEncode, outputEncode) }
        else decrypt(key, data, iv, alg, charset, inputEncode, outputEncode)

    private fun decrypt(
        key: ByteArray,
        data: String,
        iv: ByteArray,
        alg: String,
        charset: String = "UTF-8",
        inputEncode: String = "base64",
        outputEncode: String = "raw",
    ) =
        catch({ "decrypt error: $it" }) {
            println("decrypt  $alg")
            val cipher = makeCipher(alg, key, iv, Cipher.DECRYPT_MODE)
            val bytes = cipher.doFinal(data.decodeToByteArray(inputEncode, charset))
            bytes.encodeTo(outputEncode, charset)
        }
}
