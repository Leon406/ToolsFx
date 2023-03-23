package me.leon.ext

import me.leon.encode.base.*
import me.leon.ext.crypto.*

const val JWT = "JWT"

val JWT_HMAC_ALGS =
    mapOf(
        "HS256" to "HmacSHA256",
        "HS384" to "HmacSHA384",
        "HS512" to "HmacSHA512",
    )

val JWT_SIGNATURE_ALGS =
    mapOf(
        "RS256" to "RSA/SHA256withRSA",
        "RS384" to "RSA/SHA384withRSA",
        "RS512" to "RSA/SHA512withRSA",
        // @refer
        // https://stackoverflow.com/questions/59228957/es256-jwt-validation-signatureexception-invalid-encoding-for-signature-java
        "ES256" to "ECDSA/SHA256withPLAIN-ECDSA",
        "ES384" to "ECDSA/SHA384withPLAIN-ECDSA",
        "ES512" to "ECDSA/SHA512withPLAIN-ECDSA",
        "PS256" to "RSA/SHA256withRSAandMGF1",
        "PS384" to "RSA/SHA384withRSAandMGF1",
        "PS512" to "RSA/SHA512withRSAandMGF1",
    )

const val HEADER_FORMAT = """{"typ":"JWT","alg":"%s"}"""

fun String.jwt(alg: String, key: String): String {
    val header = HEADER_FORMAT.format(alg).base64Url()
    val payload = base64Url()
    val toEncryptData = "$header.$payload"
    val base64Sig = generateBase64Sig(toEncryptData, alg, key)

    return "$header.$payload.$base64Sig"
}

private fun generateBase64Sig(toEncryptData: String, alg: String, key: String): String {
    val base64Sig =
        JWT_HMAC_ALGS[alg]?.let {
            toEncryptData.toByteArray().mac(key.toByteArray(), JWT_HMAC_ALGS[alg]!!).base64Url()
        }
            ?: JWT_SIGNATURE_ALGS[alg]!!.run {
                val (kpAlg, sigAlg) = split("/")
                toEncryptData.toByteArray().sign(kpAlg, sigAlg, key).base64Url()
            }
    return base64Sig
}

fun String.jwtVerify(key: String): Boolean {

    val jwtParts = split(".")
    val header = jwtParts[0].base64UrlDecode2String()
    val alg = header.fromJson(LinkedHashMap::class.java)["alg"].safeAs<String>().orEmpty()

    val sig = jwtParts[2]
    return if (alg.startsWith("HS")) {
        val base64Sig = generateBase64Sig(substringBeforeLast("."), alg, key)
        sig == base64Sig
    } else {
        val (kpAlg, sigAlg) = JWT_SIGNATURE_ALGS[alg]!!.split("/")
        substringBeforeLast(".").toByteArray().verify(kpAlg, sigAlg, key, sig.base64UrlDecode())
    }
}

fun String.jwtParse(): Pair<String, String> {
    val jwtParts = split(".")
    val header = jwtParts[0].base64UrlDecode2String()
    val alg = header.fromJson(LinkedHashMap::class.java)["alg"].safeAs<String>() ?: "HS256"
    val payload = jwtParts[1].base64UrlDecode2String()
    return alg to payload
}
