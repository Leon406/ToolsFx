package me.leon.ext.crypto

import java.io.*
import java.math.BigInteger
import java.security.*
import java.security.cert.CertificateFactory
import java.security.interfaces.*
import java.security.spec.*
import javax.crypto.Cipher
import me.leon.encode.base.*
import me.leon.ext.*
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.asn1.x509.AlgorithmIdentifier
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters
import org.bouncycastle.pqc.crypto.lms.LMSigParameters
import org.bouncycastle.pqc.jcajce.spec.LMSKeyGenParameterSpec
import org.bouncycastle.pqc.jcajce.spec.SPHINCSPlusParameterSpec

val ASYMMETRIC_ALGOS =
    mapOf(
        "RSA" to listOf(512, 1024, 2048, 3072, 4096),
        "ElGamal" to listOf(512, 1024, 2048),
        "SM2" to listOf(256),
    )

val RSA_PADDINGS =
    listOf(
        "PKCS1Padding",
        "NoPadding",
        "OAEPWithMD5AndMGF1Padding",
        "OAEPWithSHA1AndMGF1Padding",
        "OAEPWithSHA224AndMGF1Padding",
        "OAEPWithSHA256AndMGF1Padding",
        "OAEPWithSHA384AndMGF1Padding",
        "OAEPWithSHA512AndMGF1Padding",
        "OAEPWithSHA3-224AndMGF1Padding",
        "OAEPWithSHA3-256AndMGF1Padding",
        "OAEPWithSHA3-384AndMGF1Padding",
        "OAEPWithSHA3-512AndMGF1Padding",
        "ISO9796-1Padding",
    )

fun String.removePemInfo() =
    replace("---+(?:END|BEGIN) (?:RSA )?\\w+ KEY---+|\n|\r|\r\n".toRegex(), "")

fun getPropPublicKey(key: String): ByteArray =
    if (key.contains("-----BEGIN CERTIFICATE-----")) {
        val byteArrayInputStream = ByteArrayInputStream(key.toByteArray())
        CertificateFactory.getInstance("X.509", "BC")
            .generateCertificate(byteArrayInputStream)
            .publicKey
            .encoded
    } else {
        key.removePemInfo().keyAutoDecode()
    }

fun parsePublicKeyFromCerFile(file: String): String {
    return file.toFile().parsePublicKeyFromCerFile()
}

fun File.parsePublicKeyFromCerFile(): String {
    return inputStream().use {
        CertificateFactory.getInstance("X.509", "BC")
            .generateCertificate(it)
            .publicKey
            .encoded
            .base64()
    }
}

fun String.toPublicKey(alg: String): PublicKey? {
    try {
        val keySpec = X509EncodedKeySpec(getPropPublicKey(this))
        return KeyFactory.getInstance(alg.properKeyPairAlg()).generatePublic(keySpec)
    } catch (ignore: Exception) {
        if (alg.contains("RSA")) {
            // rsa n e d p 参数解析
            return with(parseRsaParams()) {
                KeyFactory.getInstance(alg.properKeyPairAlg())
                    .generatePublic(
                        RSAPublicKeySpec(
                            this["n"] ?: BigInteger(this@toPublicKey, 16),
                            this["e"] ?: BigInteger("10001", 16)
                        )
                    )
            }
        }
        return null
    }
}

fun String.toPrivateKey(alg: String): PrivateKey? {
    try {
        val keySpec = PKCS8EncodedKeySpec(removePemInfo().keyAutoDecode())
        return KeyFactory.getInstance(alg.properKeyPairAlg()).generatePrivate(keySpec)
    } catch (ignore: Exception) {
        if (alg.contains("RSA")) {
            return with(parseRsaParams()) {
                KeyFactory.getInstance(alg.properKeyPairAlg())
                    .generatePrivate(RSAPrivateKeySpec(this["n"], this["d"]))
            }
        }
        return null
    }
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
            .chunked(
                publicKey!!.bitLength() / BYTE_BITS - if (alg.contains("RSA")) reserved else 0
            ) { this.doFinal(it.toByteArray()) }
            .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
            .toByteArray()
    }
}

fun ByteArray.pubEncrypt(key: String, alg: String, reserved: Int = 11) =
    if (alg == "SM2") sm2(true, key.removePemInfo().keyAutoDecode().toECPublicKeyParams())
    else pubEncrypt(key.toPublicKey(alg), alg, reserved)

val HEX_REGEX = "^[\\da-fA-F]+$".toRegex()

fun String.keyAutoDecode(): ByteArray =
    if (HEX_REGEX.matches(this)) hex2ByteArray() else base64Decode()

fun ByteArray.asymmetricDecrypt(
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
): ByteArray =
    if (alg == "SM2") sm2(false, key.keyAutoDecode().toECPrivateKeyParams())
    else asymmetricDecrypt(key.toPrivateKey(alg), alg)

fun ByteArray.asymmetricEncrypt(key: Key?, alg: String, reserved: Int = 11): ByteArray =
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
            .chunked(bitLen / BYTE_BITS - if (alg.contains("RSA")) reserved else 0) {
                this.doFinal(it.toByteArray())
            }
            .fold(ByteArrayOutputStream()) { acc, bytes -> acc.also { acc.write(bytes) } }
            .toByteArray()
    }

fun ByteArray.privateEncrypt(key: String, alg: String, reserved: Int = 11): ByteArray =
    asymmetricEncrypt(key.toPrivateKey(alg), alg, reserved)

/** 生成密钥对 private key pkcs8 */
fun genBase64KeyArray(alg: String, keySize: Int) =
    KeyPairGenerator.getInstance(alg.properKeyPairAlg()).run {
        initialize(keySize)
        val keyPair = generateKeyPair()
        val publicKey = keyPair.public
        val privateKey = keyPair.private
        arrayOf(publicKey.encoded.base64(), privateKey.encoded.base64())
    }

private fun String.properKeyPairAlg() =
    when {
        this == "SM2" -> "EC"
        this.startsWith("RSA") -> "RSA"
        this.contains("/") -> substringBefore('/')
        else -> this
    }

private val ecGenParameterSpec =
    mapOf(
        "ECGOST3410-2012" to "Tc26-Gost-3410-12-512-paramSetA",
        "ECGOST3410-2012-512" to "Tc26-Gost-3410-12-512-paramSetA",
        "ECGOST3410-2012-256" to "Tc26-Gost-3410-12-256-paramSetA",
        "SM2" to "sm2p256v1"
    )

fun genBase64KeyArray(alg: String, params: List<Any> = emptyList()) =
    with(genKeyPair(alg, params)) { arrayOf(public.encoded.base64(), private.encoded.base64()) }

fun genKeyPair(alg: String, params: List<Any> = emptyList()): KeyPair =
    KeyPairGenerator.getInstance(alg.properKeyPairAlg(), BouncyCastleProvider.PROVIDER_NAME).run {
        when {
            alg == "SM2" -> initialize(ECGenParameterSpec(ecGenParameterSpec[alg.uppercase()]))
            alg.startsWith("EC") ->
                initialize(ECGenParameterSpec(ecGenParameterSpec[alg.uppercase()]))
            alg == "SPHINCSPLUS" ->
                initialize(SPHINCSPlusParameterSpec.fromName(params.first().toString()))
            alg == "LMS" ->
                initialize(
                    LMSKeyGenParameterSpec(
                        params[0] as LMSigParameters,
                        params[1] as LMOtsParameters
                    )
                )
            alg == "GOST3410" ->
                initialize(
                    org.bouncycastle.jce.spec.GOST3410ParameterSpec(
                        CryptoProObjectIdentifiers.gostR3410_94_CryptoPro_A.id
                    )
                )
            alg.contains("ECGOST3410") ->
                initialize(
                    ECGenParameterSpec(
                        ecGenParameterSpec[alg.uppercase() + "-${params[0] as Int}"]
                    ),
                    SecureRandom()
                )
            alg in arrayOf("ED448", "ED25519") -> {
                // nop
            }
            else -> initialize(params[0] as Int)
        }

        generateKeyPair()
    }

fun checkKeyPair(pub: String, pri: String, alg: String = "RSA"): Boolean {
    val testData = byteArrayOf(67)
    return testData.asymmetricEncrypt(pub.toPublicKey(alg), alg).run {
        asymmetricDecrypt(pri.toPrivateKey(alg), alg).contentEquals(testData)
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
    PKCS8EncodedKeySpec(removePemInfo().keyAutoDecode()).run {
        with(
            KeyFactory.getInstance(alg.properKeyPairAlg()).generatePrivate(this) as RSAPrivateCrtKey
        ) {
            KeyFactory.getInstance(alg.properKeyPairAlg())
                .generatePublic(RSAPublicKeySpec(modulus, publicExponent))
                .encoded
                .base64()
        }
    }
