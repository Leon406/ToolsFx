package me.leon.controller

import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import me.leon.base.base64
import me.leon.base.base64Decode
import me.leon.ext.stacktrace
import tornadofx.*

fun String.properKeyPairAlg() = takeUnless { it.equals("SM2", true) } ?: "EC"

class SignatureController : Controller() {

    fun sign(kpAlg: String, sigAlg: String, pri: String, msg: String) =
        try {
            Signature.getInstance(sigAlg.properKeyPairAlg())
                .apply {
                    initSign(getPrivateKey(pri, kpAlg))
                    update(msg.toByteArray())
                }
                .sign()
                .base64()
        } catch (e: Exception) {
            e.stacktrace()
        }

    fun verify(kpAlg: String, sigAlg: String, pub: String, msg: String, signed: ByteArray) =
        try {
            Signature.getInstance(sigAlg.properKeyPairAlg())
                .apply {
                    initVerify(getPublicKey(pub, kpAlg))
                    update(msg.toByteArray())
                }
                .verify(signed)
        } catch (e: Exception) {
            println(e.stacktrace())
            false
        }

    private fun getPrivateKey(privateKey: String, keyPairAlg: String): PrivateKey {
        val keyFactory = KeyFactory.getInstance(keyPairAlg.properKeyPairAlg())
        val decodedKey: ByteArray = privateKey.base64Decode()
        val keySpec = PKCS8EncodedKeySpec(decodedKey)
        return keyFactory.generatePrivate(keySpec)
    }

    /** 获取公钥 */
    private fun getPublicKey(publicKey: String, keyPairAlg: String): PublicKey {
        val keyFactory = KeyFactory.getInstance(keyPairAlg.properKeyPairAlg())
        val decodedKey: ByteArray = publicKey.base64Decode()
        val keySpec = X509EncodedKeySpec(decodedKey)
        return keyFactory.generatePublic(keySpec)
    }
}
