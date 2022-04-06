package me.leon.ext.crypto

import java.io.*
import java.security.*
import java.security.cert.CertificateFactory
import java.security.interfaces.*
import java.security.spec.*
import javax.crypto.Cipher
import me.leon.encode.base.*
import me.leon.ext.toFile
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.asn1.x509.AlgorithmIdentifier

fun String.removePemInfo() =
    replace("---+(?:END|BEGIN) (?:RSA )?\\w+ KEY---+|\n|\r|\r\n".toRegex(), "")

fun getPropPublicKey(key: String): ByteArray =
    if (key.contains("-----BEGIN CERTIFICATE-----")) {
        val byteArrayInputStream = ByteArrayInputStream(key.toByteArray())
        CertificateFactory.getInstance("X.509")
            .generateCertificate(byteArrayInputStream)
            .publicKey
            .encoded
    } else {
        key.removePemInfo().base64Decode()
    }

fun parsePublicKeyFromCerFile(file: String): String {
    return file.toFile().parsePublicKeyFromCerFile()
}

fun File.parsePublicKeyFromCerFile(): String {
    return inputStream().use {
        CertificateFactory.getInstance("X.509").generateCertificate(it).publicKey.encoded.base64()
    }
}

fun String.toPublicKey(alg: String): PublicKey? {
    val keySpec = X509EncodedKeySpec(getPropPublicKey(this))
    val keyFac = if (alg.contains("/")) alg.substringBefore('/') else alg
    return KeyFactory.getInstance(keyFac).generatePublic(keySpec)
}

fun String.toPrivateKey(alg: String): PrivateKey? {
    val keySpec = PKCS8EncodedKeySpec(removePemInfo().base64Decode())
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

/** 生成密钥对 private key pkcs8 */
fun genKeys(alg: String, keySize: Int) =
    KeyPairGenerator.getInstance(alg).run {
        initialize(keySize)
        val keyPair = generateKeyPair()
        val publicKey = keyPair.public
        val privateKey = keyPair.private
        arrayOf(publicKey.encoded.base64(), privateKey.encoded.base64())
    }

fun checkKeyPair(pub: String, pri: String, alg: String = "RSA"): Boolean {
    val testData = byteArrayOf(67)
    return testData.rsaEncrypt(pub.toPublicKey(alg), alg).run {
        rsaDecrypt(pri.toPrivateKey(alg), alg).contentEquals(testData)
    }
}

fun pkcs8ToPkcs1(pkcs8: String) =
    PrivateKeyInfo.getInstance(pkcs8.base64Decode())
        .parsePrivateKey()
        .toASN1Primitive()
        .encoded
        .base64()

fun pkcs1ToPkcs8(pkcs1: String) =
    PrivateKeyInfo(
            AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption),
            ASN1Primitive.fromByteArray(pkcs1.base64Decode())
        )
        .encoded
        .run {
            PKCS8EncodedKeySpec(this).run {
                KeyFactory.getInstance("RSA").generatePrivate(this).encoded.base64()
            }
        }

fun String.privateKeyDerivedPublicKey(alg: String = "RSA"): String =
    PKCS8EncodedKeySpec(removePemInfo().base64Decode()).run {
        with(KeyFactory.getInstance(alg).generatePrivate(this) as RSAPrivateCrtKey) {
            KeyFactory.getInstance(alg)
                .generatePublic(RSAPublicKeySpec(modulus, publicExponent))
                .encoded
                .base64()
        }
    }
