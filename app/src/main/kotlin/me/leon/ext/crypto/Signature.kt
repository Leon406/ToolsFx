package me.leon.ext.crypto

import java.security.Signature
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
