package me.leon.controller

import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.Security
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import me.leon.base.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.*
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory

class AsymmetricCryptoController : Controller() {

    fun pubEncrypt(key: String, alg: String, data: String, length: Int = 1024, reserved: Int = 11) =
        try {
            println("encrypt $key  $alg $data")
            val keySpec = X509EncodedKeySpec(getPropPublicKey(key))
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val publicKey = KeyFactory.getInstance(keyFac).generatePublic(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.ENCRYPT_MODE, publicKey)
                data.toByteArray()
                    .toList()
                    .also { if (it.size > length / 8) println("长度建议大于 ${length / 8}") }
                    .chunked(length / 8 - reserved) {
                        println(it.size)
                        this.doFinal(it.toByteArray())
                    }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .base64()
            }
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
                data.base64Decode()
                    .toList()
                    .chunked(length / 8) {
                        println(it.size)
                        this.doFinal(it.toByteArray())
                    }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .toString(Charsets.UTF_8)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "decrypt error: ${e.message}"
        }

    fun priEncrypt(key: String, alg: String, data: String, length: Int = 1024, reserved: Int = 11) =
        try {
            println("pri encrypt $key  $alg $data")
            val keySpec = PKCS8EncodedKeySpec(key.base64Decode())
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val privateKey = KeyFactory.getInstance(keyFac).generatePrivate(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.ENCRYPT_MODE, privateKey)
                data.toByteArray()
                    .toList()
                    .also { if (it.size > length / 8) println("长度建议大于 ${length / 8}") }
                    .chunked(length / 8 - reserved) {
                        println(it.size)
                        this.doFinal(it.toByteArray())
                    }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .base64()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "encrypt error: ${e.message}"
        }

    fun pubDecrypt(key: String, alg: String, data: String, length: Int = 1024) =
        try {
            println("decrypt $key  $alg $data")
            val keySpec = X509EncodedKeySpec(getPropPublicKey(key))
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val publicKey = KeyFactory.getInstance(keyFac).generatePublic(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.DECRYPT_MODE, publicKey)
                data.base64Decode()
                    .toList()
                    .chunked(length / 8) {
                        this.doFinal(it.toByteArray())
                    }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .toString(Charsets.UTF_8)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "decrypt error: ${e.message}"
        }

    private fun getPropPublicKey(key: String) =
        if (key.contains("-----BEGIN CERTIFICATE-----")) {
            val byteArrayInputStream = ByteArrayInputStream(key.toByteArray())
            CertificateFactory.getInstance("X.509")
                .generateCertificate(byteArrayInputStream)
                .publicKey.encoded
        } else {
            key.base64Decode()
        }


    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
