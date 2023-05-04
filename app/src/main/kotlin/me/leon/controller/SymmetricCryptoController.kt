package me.leon.controller

import java.io.File
import me.leon.DEBUG
import me.leon.classical.xor
import me.leon.encode.base.base64
import me.leon.ext.*
import me.leon.ext.crypto.*
import tornadofx.*

class SymmetricCryptoController : Controller() {
    fun encrypt(
        key: ByteArray,
        data: String,
        iv: ByteArray,
        alg: String,
        charset: String = "UTF-8",
        singleLine: Boolean = false,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
        associatedData: ByteArray = byteArrayOf()
    ): String =
        catch({ "encrypt error: $it" }) {
            if (DEBUG) println("encrypt  $alg")
            if (singleLine) {
                data.lineAction2String {
                    encrypt(it, inputEncode, charset, key, iv, alg, outputEncode, associatedData)
                }
            } else {
                encrypt(data, inputEncode, charset, key, iv, alg, outputEncode, associatedData)
            }
        }

    private fun encrypt(
        data: String,
        inputEncode: String,
        charset: String,
        key: ByteArray,
        iv: ByteArray,
        alg: String,
        outputEncode: String,
        associatedData: ByteArray = byteArrayOf()
    ) =
        if (alg.startsWith("XXTEA")) {
            XXTEA.encrypt(data.decodeToByteArray(inputEncode, charset), key)
                .encodeTo(outputEncode, charset)
        } else if (alg == "XOR") {
            data.decodeToByteArray(inputEncode, charset).xor(key).encodeTo(outputEncode, charset)
        } else {
            data
                .decodeToByteArray(inputEncode, charset)
                .encrypt(key, iv, alg, associatedData)
                .encodeTo(outputEncode, charset)
        }

    fun encryptByFile(
        key: ByteArray,
        path: String,
        iv: ByteArray,
        alg: String,
        associatedData: ByteArray = byteArrayOf()
    ) =
        catch({ "encrypt error: $it" }) {
            if (DEBUG) println("encrypt  $alg")
            val parentFile = path.toFile().parentFile.absolutePath
            val encryptDir =
                File(parentFile, "enc").also { if (!it.exists()) it.mkdirs() }.absolutePath
            val outFileName = path.replace(parentFile, encryptDir)
            if (alg.startsWith("XXTEA")) {
                outFileName.toFile().outputStream().use { out ->
                    path.toFile().inputStream().use {
                        out.write(XXTEA.encrypt(it.readBytes(), key))
                    }
                }
            } else if (alg.startsWith("XOR")) {
                outFileName.toFile().outputStream().use { out ->
                    path.toFile().inputStream().use { out.write(it.readBytes().xor(key)) }
                }
            } else {
                path.encryptFile(key, iv, alg, outFileName, associatedData)
            }
            "加密文件路径(同选择文件目录): ${File(outFileName).absolutePath} \n" +
                "alg: $alg\n" +
                "key(base64): ${key.base64()}\n" +
                "iv(base64): ${iv.base64()}\n"
        }

    fun decryptByFile(
        key: ByteArray,
        path: String,
        iv: ByteArray,
        alg: String,
        associatedData: ByteArray = byteArrayOf()
    ) =
        catch({ "decrypt error: $it" }) {
            if (DEBUG) println("decrypt  $alg")
            val parentFile = path.toFile().parentFile.absolutePath
            val decryptDir =
                File(parentFile, "dec").also { if (!it.exists()) it.mkdirs() }.absolutePath
            val outFileName = path.replace(parentFile, decryptDir)
            if (alg.startsWith("XXTEA")) {
                outFileName.toFile().outputStream().use { out ->
                    path.toFile().inputStream().use {
                        out.write(XXTEA.decrypt(it.readBytes(), key))
                    }
                }
            } else if (alg.startsWith("XOR")) {
                outFileName.toFile().outputStream().use { out ->
                    path.toFile().inputStream().use { out.write(it.readBytes().xor(key)) }
                }
            } else {
                path.decryptFile(key, iv, alg, outFileName, associatedData)
            }
            "解密文件路径(同选择文件目录): $outFileName"
        }

    fun decrypt(
        key: ByteArray,
        data: String,
        iv: ByteArray,
        alg: String,
        charset: String = "UTF-8",
        singleLine: Boolean = false,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
        associatedData: ByteArray = byteArrayOf()
    ): String =
        catch({ "decrypt error: $it" }) {
            if (DEBUG) println("decrypt  $alg")
            if (singleLine) {
                data.lineAction2String {
                    decrypt(it, inputEncode, charset, key, iv, alg, outputEncode, associatedData)
                }
            } else {
                decrypt(data, inputEncode, charset, key, iv, alg, outputEncode, associatedData)
            }
        }

    private fun decrypt(
        data: String,
        inputEncode: String,
        charset: String,
        key: ByteArray,
        iv: ByteArray,
        alg: String,
        outputEncode: String,
        associatedData: ByteArray = byteArrayOf()
    ) =
        if (alg.startsWith("XXTEA")) {
            XXTEA.decrypt(data.decodeToByteArray(inputEncode, charset), key)
                .encodeTo(outputEncode, charset)
        } else if (alg == "XOR") {
            data.decodeToByteArray(inputEncode, charset).xor(key).encodeTo(outputEncode, charset)
        } else {
            data
                .decodeToByteArray(inputEncode, charset)
                .decrypt(key, iv, alg, associatedData)
                .encodeTo(outputEncode, charset)
        }
}
