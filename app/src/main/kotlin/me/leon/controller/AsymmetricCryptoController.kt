package me.leon.controller

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import me.leon.encode.base.*
import me.leon.ext.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.*

class AsymmetricCryptoController : Controller() {

    fun pubEncrypt(
        key: String,
        alg: String,
        data: String,
        length: Int = 1024,
        isSingleLine: Boolean = false,
        reserved: Int = 11,
        inputEncode: String = "raw",
        outputEncode: String = "base64"
    ) =
        if (isSingleLine)
            data.lineAction2String {
                pubEncrypt(key, alg, it, length, reserved, inputEncode, outputEncode)
            }
        else pubEncrypt(key, alg, data, length, reserved, inputEncode, outputEncode)

    fun lengthFromPub(key: String): Int {
        val keySpec = X509EncodedKeySpec(getPropPublicKey(key))
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec)
        return (publicKey as RSAPublicKey).modulus.bitLength()
    }

    fun lengthFromPri(key: String): Int {
        val keySpec = PKCS8EncodedKeySpec(key.base64Decode())
        val privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec)
        return (privateKey as RSAPrivateKey).modulus.bitLength()
    }

    private fun pubEncrypt(
        key: String,
        alg: String,
        data: String,
        length: Int = 1024,
        reserved: Int = 11,
        inputEncode: String = "raw",
        outputEncode: String = "base64"
    ) =
        catch({ "encrypt error: $it}" }) {
            println("encrypt $key  $alg $data")
            val keySpec = X509EncodedKeySpec(getPropPublicKey(key))

            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val publicKey = KeyFactory.getInstance(keyFac).generatePublic(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.ENCRYPT_MODE, publicKey)
                data.decodeToByteArray(inputEncode)
                    .toList()
                    .chunked(length / BYTE_BITS - reserved) {
                        println(it.size)
                        this.doFinal(it.toByteArray())
                    }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .encodeTo(outputEncode)
            }
        }

    fun priDecrypt(
        key: String,
        alg: String,
        data: String,
        length: Int = 1024,
        isSingleLine: Boolean = false,
        inputEncode: String = "base64",
        outputEncode: String = "raw"
    ) =
        if (isSingleLine)
            data.lineAction2String { priDecrypt(key, alg, it, length, inputEncode, outputEncode) }
        else priDecrypt(key, alg, data, length, inputEncode, outputEncode)

    fun priDecrypt(
        key: String,
        alg: String,
        data: String,
        length: Int = 1024,
        inputEncode: String = "base64",
        outputEncode: String = "raw"
    ) =
        catch({ "decrypt error: $it" }) {
            println("decrypt $key  $alg $data")
            val keySpec = PKCS8EncodedKeySpec(key.base64Decode())
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val privateKey = KeyFactory.getInstance(keyFac).generatePrivate(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.DECRYPT_MODE, privateKey)
                data.decodeToByteArray(inputEncode)
                    .toList()
                    .chunked(length / BYTE_BITS) {
                        println(it.size)
                        this.doFinal(it.toByteArray())
                    }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .encodeTo(outputEncode)
            }
        }

    fun priEncrypt(
        key: String,
        alg: String,
        data: String,
        length: Int = 1024,
        isSingleLine: Boolean = false,
        reserved: Int = 11,
        inputEncode: String = "raw",
        outputEncode: String = "base64"
    ) =
        if (isSingleLine)
            data.lineAction2String {
                priEncrypt(key, alg, it, length, reserved, inputEncode, outputEncode)
            }
        else priEncrypt(key, alg, data, length, reserved, inputEncode, outputEncode)

    fun priEncrypt(
        key: String,
        alg: String,
        data: String,
        length: Int = 1024,
        reserved: Int = 11,
        inputEncode: String = "base64",
        outputEncode: String = "raw"
    ) =
        catch({ "encrypt error: $it" }) {
            println("pri encrypt $key  $alg $data")
            val keySpec = PKCS8EncodedKeySpec(key.base64Decode())
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val privateKey = KeyFactory.getInstance(keyFac).generatePrivate(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.ENCRYPT_MODE, privateKey)
                data.decodeToByteArray(inputEncode)
                    .toList()
                    .chunked(length / BYTE_BITS - reserved) {
                        println(it.size)
                        this.doFinal(it.toByteArray())
                    }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .encodeTo(outputEncode)
            }
        }

    fun pubDecrypt(
        key: String,
        alg: String,
        data: String,
        length: Int = 1024,
        isSingleLine: Boolean = false,
        inputEncode: String = "base64",
        outputEncode: String = "raw"
    ) =
        if (isSingleLine)
            data.lineAction2String { pubDecrypt(key, alg, it, length, inputEncode, outputEncode) }
        else pubDecrypt(key, alg, data, length, inputEncode, outputEncode)

    private fun pubDecrypt(
        key: String,
        alg: String,
        data: String,
        length: Int = 1024,
        inputEncode: String = "base64",
        outputEncode: String = "raw"
    ) =
        catch({ "decrypt error: $it" }) {
            println("decrypt $key  $alg $data")

            val keySpec = X509EncodedKeySpec(getPropPublicKey(key))
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val publicKey = KeyFactory.getInstance(keyFac).generatePublic(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.DECRYPT_MODE, publicKey)
                data.decodeToByteArray(inputEncode)
                    .toList()
                    .chunked(length / BYTE_BITS) { this.doFinal(it.toByteArray()) }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .encodeTo(outputEncode)
            }
        }

    private fun getPropPublicKey(key: String) =
        if (key.contains("-----BEGIN CERTIFICATE-----")) {
            val byteArrayInputStream = ByteArrayInputStream(key.toByteArray())
            CertificateFactory.getInstance("X.509")
                .generateCertificate(byteArrayInputStream)
                .publicKey
                .encoded
        } else {
            key.base64Decode()
        }

    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    fun String.decodeToByteArray(encode: String) =
        when (encode) {
            "raw" -> toByteArray()
            "base64" -> base64Decode()
            "hex" -> hex2ByteArray()
            else -> throw IllegalArgumentException("input encode error")
        }

    fun ByteArray.encodeTo(encode: String) =
        when (encode) {
            "raw" -> decodeToString()
            "base64" -> base64()
            "hex" -> toHex()
            else -> throw IllegalArgumentException("input encode error")
        }
}
