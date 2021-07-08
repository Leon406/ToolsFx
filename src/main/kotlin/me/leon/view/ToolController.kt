package me.leon.view

import me.leon.Digests
import me.leon.base.*
import me.leon.ext.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.Security
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ToolController : Controller() {

    fun encode(raw: String, type: EncodeType = EncodeType.Base64): String =
        try {
            if (raw.isEmpty()) ""
            else when (type) {
                EncodeType.Base64 -> Base64.getEncoder().encodeToString(raw.toByteArray())
                EncodeType.Base64Safe -> Base64.getUrlEncoder().encodeToString(raw.toByteArray())
                EncodeType.Hex -> raw.toByteArray(Charsets.UTF_8).toHex()
                EncodeType.UrlEncode -> URLEncoder.encode(raw).replace("+", "%20")
                EncodeType.Base32 -> raw.base32()
                EncodeType.Base16 -> raw.base16()
                EncodeType.Unicode -> raw.toUnicodeString()
                EncodeType.Binary -> raw.toBinaryString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "编码错误: ${e.message}"
        }

    fun decode(encoded: String, type: EncodeType = EncodeType.Base64): String =
        try {
            if (encoded.isEmpty()) ""
            else when (type) {
                EncodeType.Base64 -> Base64.getDecoder().decode(encoded).toString(Charset.defaultCharset())
                EncodeType.Base64Safe -> Base64.getUrlDecoder().decode(encoded).toString(Charset.defaultCharset())
                EncodeType.Hex -> encoded.hex2Ascii()
                EncodeType.UrlEncode -> URLDecoder.decode(encoded)
                EncodeType.Base32 -> encoded.base32Decode()
                EncodeType.Base16 -> encoded.base16Decode()
                EncodeType.Unicode -> encoded.unicode2String()
                EncodeType.Binary -> encoded.binary2Ascii()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "解码错误: ${e.message}"
        }

    fun digest(method: String, data: String) = if (data.isEmpty()) "" else Digests.hash(method, data)
    fun digestFile(method: String, path: String) = if (path.isEmpty()) "" else Digests.hashByFile(method, path)

    fun encrypt(key: ByteArray, data: String, iv: ByteArray, alg: String) =
        try {
            println("encrypt  $alg")
            val cipher = Cipher.getInstance(alg)
            val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))

            if (alg.contains("ECB".toRegex()))
                cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            else
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(iv))
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
            if (alg.contains("ECB".toRegex()))
                cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            else
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(iv))

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
            if (alg.contains("ECB".toRegex()))
                cipher.init(Cipher.DECRYPT_MODE, keySpec)
            else
                cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv))

            val outFileName =
                if (path.endsWith(".enc")) path.replace(".enc", "")
                else "$path.dec"
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
            if (alg.contains("ECB".toRegex()))
                cipher.init(Cipher.DECRYPT_MODE, keySpec)
            else
                cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv))
            String(cipher.doFinal(Base64.getDecoder().decode(data)))
        } catch (e: Exception) {
            e.printStackTrace()
            "decrypt error: ${e.message}"
        }

    fun pubEncrypt(key: String, alg: String, data: String, length: Int = 1024, reserved: Int = 11) =
        try {
            println("encrypt $key  $alg $data")
            val keySpec = X509EncodedKeySpec(key.base64Decode())
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val publicKey = KeyFactory.getInstance(keyFac).generatePublic(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.ENCRYPT_MODE, publicKey)
                data.toByteArray().toList().also {
                    if (it.size > length / 8) println("长度建议大于 ${length / 8}")
                }.chunked(length / 8 - reserved) {
                    println(it.size)
                    this.doFinal(it.toByteArray())
                }.fold(ByteArrayOutputStream()) { acc, bytes ->
                    acc.also { acc.write(bytes) }
                }.toByteArray().base64()
            }
//            RsaUtils.encryptDataStr(data.toByteArray(),RsaUtils.loadPublicKey(key)!!)
        } catch (e: Exception) {
            e.printStackTrace()
            "encrypt error: ${e.message}"
        }


    fun priDecrypt(key: String, alg: String, data: String, length: Int = 1024) =
        try {
            println("decrypt $key  $alg $data")
            val keySpec = PKCS8EncodedKeySpec(key.base64Decode())
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val privateKey = KeyFactory.getInstance(keyFac).generatePrivate(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.DECRYPT_MODE, privateKey)
                data.base64Decode().toList().chunked(length / 8) {
                    println(it.size)
                    this.doFinal(it.toByteArray())
                }.fold(ByteArrayOutputStream()) { acc, bytes ->
                    acc.also { acc.write(bytes) }
                }.toByteArray().toString(Charsets.UTF_8)
            }

//            RsaUtils.decryptDataStr(data.base64Decode(),RsaUtils.loadPrivateKey(key)!!)
        } catch (e: Exception) {
            e.printStackTrace()
            "decrypt error: ${e.message}"
        }

    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
