package me.leon.controller

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import me.leon.encode.base.*
import me.leon.ext.catch
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.*

class AsymmetricCryptoController : Controller() {

    fun pubEncrypt(key: String, alg: String, data: String, length: Int = 1024, reserved: Int = 11) =
        catch({ "encrypt error: $it}" }) {
            println("encrypt $key  $alg $data")
            val keySpec = X509EncodedKeySpec(getPropPublicKey(key))
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val publicKey = KeyFactory.getInstance(keyFac).generatePublic(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.ENCRYPT_MODE, publicKey)
                data.toByteArray()
                    .toList()
                    .chunked(length / BYTE_BITS - reserved) {
                        println(it.size)
                        this.doFinal(it.toByteArray())
                    }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .base64()
            }
        }

    fun priDecrypt(key: String, alg: String, data: String, length: Int = 1024) =
        catch({ "decrypt error: $it" }) {
            println("decrypt $key  $alg $data")
            val keySpec = PKCS8EncodedKeySpec(key.base64Decode())
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val privateKey = KeyFactory.getInstance(keyFac).generatePrivate(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.DECRYPT_MODE, privateKey)
                data.base64Decode()
                    .toList()
                    .chunked(length / BYTE_BITS) {
                        println(it.size)
                        this.doFinal(it.toByteArray())
                    }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .toString(Charsets.UTF_8)
            }
        }

    fun priEncrypt(key: String, alg: String, data: String, length: Int = 1024, reserved: Int = 11) =
        catch({ "encrypt error: $it" }) {
            println("pri encrypt $key  $alg $data")
            val keySpec = PKCS8EncodedKeySpec(key.base64Decode())
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val privateKey = KeyFactory.getInstance(keyFac).generatePrivate(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.ENCRYPT_MODE, privateKey)
                data.toByteArray()
                    .toList()
                    .chunked(length / BYTE_BITS - reserved) {
                        println(it.size)
                        this.doFinal(it.toByteArray())
                    }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .base64()
            }
        }

    fun pubDecrypt(key: String, alg: String, data: String, length: Int = 1024) =
        catch({ "decrypt error: $it" }) {
            println("decrypt $key  $alg $data")
            val keySpec = X509EncodedKeySpec(getPropPublicKey(key))
            val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
            val publicKey = KeyFactory.getInstance(keyFac).generatePublic(keySpec)
            Cipher.getInstance(alg).run {
                init(Cipher.DECRYPT_MODE, publicKey)
                data.base64Decode()
                    .toList()
                    .chunked(length / BYTE_BITS) { this.doFinal(it.toByteArray()) }
                    .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
                    .toByteArray()
                    .toString(Charsets.UTF_8)
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
}
