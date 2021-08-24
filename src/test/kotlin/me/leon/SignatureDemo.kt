package me.leon

import java.math.BigInteger
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import me.leon.base.base64
import me.leon.base.base64Decode
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
import org.bouncycastle.jce.provider.BouncyCastleProvider

/** @link https://www.liaoxuefeng.com/wiki/1252599548343744/1304227943022626 */
object SignatureDemo {
    init {
        Security.addProvider(BouncyCastleProvider())
        Security.getProviders()
            .filter {
                println(it.name)
                it.name == "BC"
            }
            .flatMap { it.services }
            .filter { it.type in listOf("Signature", "KeyPairGenerator") }
            .groupBy { it.type }
            .forEach { (k, v) ->
                v.last().provider.name
                println(
                    "$k Algorithm: \n\t${v.joinToString("\n\t") { it.algorithm + "  " + it.provider.name }}"
                )
            }
    }

    // 公私钥  keysize   MAC算法
    // RSA   1024     SHA512withRSA
    // DSA  512 (最小)     SHA512withDSA
    // ECDSA  224     SHA512withECDSA
    private const val keyPairAlg = "EC"
    private const val keySize = 521
    private const val sigAlg = "SHA384withECNR"

    private fun String.properKeyPairAlg() = takeUnless { it.equals("SM2", true) } ?: "EC"

    fun sigTest() {
        // 生成公钥/私钥:
        val kp = generateKeyPair(keyPairAlg, keySize)
        val sk = kp.private
        val pk = kp.public

        val privateKey = sk.encoded.base64()
        val pubKey = pk.encoded.base64()
        println("公钥: $pubKey \n私钥: $privateKey")
        // 待签名的消息:
        val message = "Hello, I am Bob!".toByteArray()
        // 用私钥签名:
        val signed: ByteArray = sign(sigAlg, getPrivateKey(privateKey), message)
        println(String.format("signature: %x", BigInteger(1, signed)))
        println(signed.base64())

        // 用公钥验证:
        val valid = verify(sigAlg, getPublicKey(pubKey), message, signed)
        println("valid? $valid")
    }

    private fun generateKeyPair(alg: String, size: Int) =
        KeyPairGenerator.getInstance(alg.properKeyPairAlg(), "BC")
            .apply {
                when {
                    alg == "SM2" ->
                        initialize(ECGenParameterSpec(ecGenParameterSpec[alg.uppercase()]))
                    alg == "GOST3410" ->
                        initialize(
                            org.bouncycastle.jce.spec.GOST3410ParameterSpec(
                                CryptoProObjectIdentifiers.gostR3410_94_CryptoPro_A.id
                            )
                        )
                    alg.contains("ECGOST3410") ->
                        initialize(
                            ECGenParameterSpec(ecGenParameterSpec[alg.uppercase() + "-$size"]),
                            SecureRandom()
                        )
                    alg in arrayOf("ED448", "ED25519") -> {
                        // nop
                    }
                    else -> initialize(size)
                }
            }
            .generateKeyPair()

    private fun sign(alg: String, pri: PrivateKey, msg: ByteArray) =
        Signature.getInstance(alg)
            .apply {
                initSign(pri)
                update(msg)
            }
            .sign()

    private fun verify(alg: String, pub: PublicKey, msg: ByteArray, signed: ByteArray) =
        Signature.getInstance(alg)
            .apply {
                initVerify(pub)
                update(msg)
            }
            .verify(signed)

    private fun getPrivateKey(privateKey: String): PrivateKey {
        val keyFactory = KeyFactory.getInstance(keyPairAlg.properKeyPairAlg())
        val decodedKey: ByteArray = privateKey.base64Decode()
        val keySpec = PKCS8EncodedKeySpec(decodedKey)
        return keyFactory.generatePrivate(keySpec)
    }

    /** 获取公钥 */
    private fun getPublicKey(publicKey: String): PublicKey {
        val keyFactory = KeyFactory.getInstance(keyPairAlg.properKeyPairAlg())
        val decodedKey: ByteArray = publicKey.base64Decode()
        val keySpec = X509EncodedKeySpec(decodedKey)
        return keyFactory.generatePublic(keySpec)
    }
}

val ecGenParameterSpec =
    mapOf(
        "ECGOST3410-2012" to "Tc26-Gost-3410-12-512-paramSetA",
        "ECGOST3410-2012-512" to "Tc26-Gost-3410-12-512-paramSetA",
        "ECGOST3410-2012-256" to "Tc26-Gost-3410-12-256-paramSetA",
        "SM2" to "sm2p256v1"
    )
