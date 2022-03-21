package me.leon.ext.fx

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.*
import java.security.cert.CertificateFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import me.leon.encode.base.BYTE_BITS
import me.leon.encode.base.base64Decode

private fun getPropPublicKey(key: String): ByteArray =
    if (key.contains("-----BEGIN CERTIFICATE-----")) {
        val byteArrayInputStream = ByteArrayInputStream(key.toByteArray())
        CertificateFactory.getInstance("X.509")
            .generateCertificate(byteArrayInputStream)
            .publicKey
            .encoded
    } else {
        key.base64Decode()
    }

fun String.toPublicKey(alg: String): PublicKey? {
    val keySpec = X509EncodedKeySpec(getPropPublicKey(this))
    val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
    return KeyFactory.getInstance(keyFac).generatePublic(keySpec)
}

fun String.toPrivateKey(alg: String): PrivateKey? {
    val keySpec = PKCS8EncodedKeySpec(base64Decode())
    val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
    return KeyFactory.getInstance(keyFac).generatePrivate(keySpec)
}

fun ByteArray.pubDecrypt(key: String, alg: String) = pubDecrypt(key.toPublicKey(alg), alg)

fun ByteArray.pubDecrypt(publicKey: PublicKey?, alg: String): ByteArray =
    Cipher.getInstance(alg).run {
        init(Cipher.DECRYPT_MODE, publicKey)
        toList()
            .chunked(publicKey!!.bitLength() / BYTE_BITS) { this.doFinal(it.toByteArray()) }
            .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
            .toByteArray()
    }

fun ByteArray.pubEncrypt(publicKey: PublicKey?, alg: String, reserved: Int = 11): ByteArray {
    return Cipher.getInstance(alg).run {
        init(Cipher.ENCRYPT_MODE, publicKey)
        toList()
            .chunked(publicKey!!.bitLength() / BYTE_BITS - reserved) {
                this.doFinal(it.toByteArray())
            }
            .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
            .toByteArray()
    }
}

fun ByteArray.pubEncrypt(key: String, alg: String, reserved: Int = 11) =
    pubEncrypt(key.toPublicKey(alg), alg, reserved)

fun ByteArray.rsaDecrypt(
    key: Key?,
    alg: String,
): ByteArray =
    Cipher.getInstance(alg).run {
        init(Cipher.DECRYPT_MODE, key)
        val bitLen =
            when (key) {
                is PublicKey -> {
                    key.bitLength()
                }
                is PrivateKey -> {
                    key.bitLength()
                }
                else -> {
                    1024
                }
            }
        toList()
            .chunked(bitLen / BYTE_BITS) { this.doFinal(it.toByteArray()) }
            .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
            .toByteArray()
    }

fun ByteArray.privateDecrypt(
    key: String,
    alg: String,
): ByteArray = rsaDecrypt(key.toPrivateKey(alg), alg)

fun ByteArray.rsaEncrypt(key: Key?, alg: String, reserved: Int = 11): ByteArray =
    Cipher.getInstance(alg).run {
        init(Cipher.ENCRYPT_MODE, key)
        val bitLen =
            when (key) {
                is PublicKey -> {
                    key.bitLength()
                }
                is PrivateKey -> {
                    key.bitLength()
                }
                else -> {
                    1024
                }
            }
        toList()
            .chunked(bitLen / BYTE_BITS - reserved) { this.doFinal(it.toByteArray()) }
            .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
            .toByteArray()
    }

fun ByteArray.privateEncrypt(key: String, alg: String, reserved: Int = 11): ByteArray =
    rsaEncrypt(key.toPrivateKey(alg), alg, reserved)

fun PublicKey.bitLength() = (this as? RSAPublicKey)?.modulus?.bitLength() ?: 1024

fun PrivateKey.bitLength() = (this as? RSAPrivateKey)?.modulus?.bitLength() ?: 1024
