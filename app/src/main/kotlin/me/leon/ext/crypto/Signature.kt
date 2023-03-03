package me.leon.ext.crypto

import java.security.*
import java.security.spec.ECGenParameterSpec
import me.leon.encode.base.base64
import me.leon.ext.catch

private fun String.properKeyPairAlg() = takeUnless { it.equals("SM2", true) } ?: "EC"

fun ByteArray.sign(kpAlg: String, sigAlg: String, pri: String): ByteArray =
    Signature.getInstance(sigAlg.properKeyPairAlg())
        .apply {
            initSign(pri.toPrivateKey(kpAlg))
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
                initVerify(pub.toPublicKey(kpAlg))
                update(this@verify)
            }
            .verify(signed)
    }

val ecdsaCurveMap =
    mapOf(
        "ES256" to "secp256r1",
        "ES384" to "secp384r1",
        "ES512" to "secp521r1",
    )

fun generateEcKeyPair(jwtAlg: String = "ES256"): KeyPair? {
    val kpg = KeyPairGenerator.getInstance("EC")
    val spec = ECGenParameterSpec(ecdsaCurveMap[jwtAlg])
    kpg.initialize(spec, SecureRandom())
    return kpg.generateKeyPair()
}

fun main() {
    generateEcKeyPair()?.let {
        println(it.public.encoded.base64())
        println(it.private.encoded.base64())
    }
}
