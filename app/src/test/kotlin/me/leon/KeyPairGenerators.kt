package me.leon

import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters
import org.bouncycastle.pqc.crypto.lms.LMSigParameters
import org.bouncycastle.pqc.jcajce.spec.LMSKeyGenParameterSpec
import org.bouncycastle.pqc.jcajce.spec.SPHINCSPlusParameterSpec

val sphincsPlusList =
    arrayOf(
        "sha256-128f-robust",
        "sha256-128s-robust",
        "sha256-192f-robust",
        "sha256-192s-robust",
        "sha256-256f-robust",
        "sha256-256s-robust",
        "sha256-128s-simple",
        "sha256-128f-simple",
        "sha256-192f-simple",
        "sha256-192s-simple",
        "sha256-256f-simple",
        "sha256-256s-simple",
        "shake256-128f-robust",
        "shake256-128s-robust",
        "shake256-192f-robust",
        "shake256-192s-robust",
        "shake256-256s-robust",
        "shake256-128f-simple",
        "shake256-128s-simple",
        "shake256-192f-simple",
        "shake256-192s-simple",
        "shake256-256f-simple",
        "shake256-256s-simple",
    )

val lmsSigList =
    arrayOf(
        LMSigParameters.lms_sha256_n32_h10,
        LMSigParameters.lms_sha256_n32_h10,
        LMSigParameters.lms_sha256_n32_h15,
        LMSigParameters.lms_sha256_n32_h20,
        LMSigParameters.lms_sha256_n32_h25,
    )

val lmsParamsList =
    arrayOf(
        LMOtsParameters.sha256_n32_w1,
        LMOtsParameters.sha256_n32_w2,
        LMOtsParameters.sha256_n32_w4,
        LMOtsParameters.sha256_n32_w8
    )

val ecGenParameterSpec =
    mapOf(
        "ECGOST3410-2012" to "Tc26-Gost-3410-12-512-paramSetA",
        "ECGOST3410-2012-512" to "Tc26-Gost-3410-12-512-paramSetA",
        "ECGOST3410-2012-256" to "Tc26-Gost-3410-12-256-paramSetA",
        "SM2" to "sm2p256v1"
    )

fun String.properKeyPairAlg() =
    takeUnless { it.equals("SM2", true) || it.startsWith("ECGOST") } ?: "EC"

fun generateKeyPair(
    alg: String,
    size: Int = 1024,
    params: List<Any> = listOf("sha256-128f-robust")
) =
    KeyPairGenerator.getInstance(alg.properKeyPairAlg(), BouncyCastleProvider.PROVIDER_NAME)
        .apply {
            when {
                alg == "SM2" -> initialize(ECGenParameterSpec(ecGenParameterSpec[alg.uppercase()]))
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
