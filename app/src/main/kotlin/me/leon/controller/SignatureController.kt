package me.leon.controller

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import tornadofx.*

fun String.properKeyPairAlg() = takeUnless { it.equals("SM2", true) } ?: "EC"

class SignatureController : Controller() {

    fun sign(kpAlg: String, sigAlg: String, pri: String, msg: String, isSingleLine: Boolean) =
        if (isSingleLine) msg.lineAction2String { sign(kpAlg, sigAlg, pri, it) }
        else sign(kpAlg, sigAlg, pri, msg)

    fun sign(kpAlg: String, sigAlg: String, pri: String, msg: String) =
        catch({ it }) {
            Signature.getInstance(sigAlg.properKeyPairAlg())
                .apply {
                    initSign(getPrivateKey(pri, kpAlg))
                    update(msg.toByteArray())
                }
                .sign()
                .base64()
        }

    fun verify(
        kpAlg: String,
        sigAlg: String,
        pub: String,
        msg: String,
        signed: String,
        isSingleLine: Boolean
    ) =
        if (isSingleLine)
            msg.lineActionIndex { s, i ->
                verify(kpAlg, sigAlg, pub, s, signed.lineSplit()[i].base64Decode()).toString()
            }
        else verify(kpAlg, sigAlg, pub, msg, signed.base64Decode())

    fun verify(kpAlg: String, sigAlg: String, pub: String, msg: String, signed: ByteArray) =
        catch({ false }) {
            Signature.getInstance(sigAlg.properKeyPairAlg())
                .apply {
                    initVerify(getPublicKey(pub, kpAlg))
                    update(msg.toByteArray())
                }
                .verify(signed)
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
