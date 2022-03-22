package me.leon.ext.crypto

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import me.leon.encode.base.base64Decode
import me.leon.ext.catch

private fun String.properKeyPairAlg() = takeUnless { it.equals("SM2", true) } ?: "EC"

fun ByteArray.sign(kpAlg: String, sigAlg: String, pri: String): ByteArray =
    Signature.getInstance(sigAlg.properKeyPairAlg())
        .apply {
            initSign(getPrivateKey(pri, kpAlg))
            update(this@sign)
        }
        .sign()

fun ByteArray.verify(kpAlg: String, sigAlg: String, pub: String, signed: ByteArray) =
    catch({
        println("verify err  $it")
        false
    }) {
        Signature.getInstance(sigAlg.properKeyPairAlg())
            .apply {
                initVerify(getPublicKey(pub, kpAlg))
                update(this@verify)
            }
            .verify(signed)
    }

private fun getPrivateKey(privateKey: String, keyPairAlg: String): PrivateKey {
    val keyFactory = KeyFactory.getInstance(keyPairAlg.properKeyPairAlg())
    val decodedKey: ByteArray = privateKey.removePemInfo().base64Decode()
    val keySpec = PKCS8EncodedKeySpec(decodedKey)
    return keyFactory.generatePrivate(keySpec)
}

/** 获取公钥 */
private fun getPublicKey(publicKey: String, keyPairAlg: String): PublicKey {
    val keyFactory = KeyFactory.getInstance(keyPairAlg.properKeyPairAlg())
    val keySpec = X509EncodedKeySpec(getPropPublicKey(publicKey))
    return keyFactory.generatePublic(keySpec)
}
