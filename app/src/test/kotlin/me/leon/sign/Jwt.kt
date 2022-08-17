package me.leon.sign

import me.leon.encode.base.base64Url
import me.leon.encode.base.base64UrlDecode2String
import me.leon.ext.crypto.mac
import me.leon.ext.fromJson
import me.leon.ext.safeAs

val JWT_ALGS =
    mapOf(
        "HS256" to "HmacSHA256",
        "HS384" to "HmacSHA384",
        "HS512" to "HmacSHA512",
    )

const val HEADER_FORMAT = """{"alg":"%s","typ":"JWT"}"""

fun String.jwt(alg: String, key: String): String {
    val header = HEADER_FORMAT.format(alg).base64Url()
    val payload = base64Url()

    val toEncryptData = "$header.$payload"
    val base64Sig = toEncryptData.toByteArray().mac(key.toByteArray(), JWT_ALGS[alg]!!).base64Url()
    return "$header.$payload.$base64Sig"
}

fun String.jwtVerify(key: String): Pair<String, Boolean> {
    val sb = StringBuilder()
    val jwtParts = split(".")
    val header = jwtParts[0].base64UrlDecode2String()
    val alg = JWT_ALGS[header.fromJson(LinkedHashMap::class.java)["alg"].safeAs<String>()]
    val payload = jwtParts[1].base64UrlDecode2String()

    sb.append("HEADER:ALGORITHM & TOKEN TYPE:")
        .appendLine()
        .appendLine()
        .appendLine(header)
        .appendLine()
        .appendLine("PAYLOAD:DATA")
        .appendLine()
        .append(payload)

    val sig = jwtParts[2]
    val toEncryptData = substringBeforeLast('.')
    val base64Sig = toEncryptData.toByteArray().mac(key.toByteArray(), alg!!).base64Url()

    return sb.toString() to (sig == base64Sig)
}
