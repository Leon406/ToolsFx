package me.leon.ext.crypto

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import me.leon.ext.catch

private fun String.properKeyPairAlg() = takeUnless { it.equals("SM2", true) } ?: "EC"

fun ByteArray.sign(kpAlg: String, sigAlg: String, pri: String): ByteArray =
    if (kpAlg == "SM2" && HEX_REGEX.matches(pri)) {
        sm2Sign(pri)
    } else {
        Signature.getInstance(sigAlg.properKeyPairAlg())
            .apply {
                initSign(pri.toPrivateKey(kpAlg))
                update(this@sign)
            }
            .sign()
    }

fun ByteArray.verify(kpAlg: String, sigAlg: String, pub: String, signed: ByteArray) =
    catch({
        println("verify err  $it")
        false
    }) {
        if (kpAlg == "SM2" && HEX_REGEX.matches(pub)) {
            sm2Verify(pub, signed)
        } else {
            Signature.getInstance(sigAlg.properKeyPairAlg())
                .apply {
                    initVerify(pub.toPublicKey(kpAlg))
                    update(this@verify)
                }
                .verify(signed)
        }
    }

val ecdsaCurveMap = mapOf("ES256" to "secp256r1", "ES384" to "secp384r1", "ES512" to "secp521r1")

fun generateEcKeyPair(jwtAlg: String = "ES256"): KeyPair? {
    val kpg = KeyPairGenerator.getInstance("EC")
    val spec = ECGenParameterSpec(ecdsaCurveMap[jwtAlg])
    kpg.initialize(spec, SecureRandom())
    return kpg.generateKeyPair()
}
