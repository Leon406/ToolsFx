package me.leon

import org.bouncycastle.pqc.crypto.lms.LMOtsParameters
import org.bouncycastle.pqc.crypto.lms.LMSigParameters

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
