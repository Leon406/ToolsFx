package me.leon.ext.crypto

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

object PBE {
    val PBE_HMAC =
        arrayOf(
            "PBEWithHMACTIGER",
            "PBEWithHMACSHA256",
            "PBKDF2WithHMACSHA256",
            "PBKDF2WithHMACSHA224",
            "PBEWithHMACRIPEMD160",
            "PBKDF2WithHMACSHA3-384",
            "PBKDF2WithHMACGOST3411",
            "PBKDF2WithHMACSM3",
            "PBKDF2WithHMACSHA384",
            "PBKDF2WithHMACSHA3-512",
            "PBKDF2WithHMACSHA512",
            "PBEWithHMACSHA1",
            "PBEWithHMACGOST3411",
            "PBKDF2WithHMACSHA3-256",
            "PBKDF2WithHMACSHA3-224",
        )

    val PBE_CRYPTO =
        arrayOf(
            "MD5and256bitAES-CBC-OPENSSL",
            "MD5and192bitAES-CBC-OPENSSL",
            "MD5and128bitAES-CBC-OPENSSL",
            "MD5andRC2",
            "MD5andDES",
            "MD2andDES",
            "SHA1andDES",
            "SHA1andRC2",
            "SHA256and256bitAES-CBC-BC",
            "SHA256and192bitAES-CBC-BC",
            "SHA256and128bitAES-CBC-BC",
            "SHAand256bitAES-CBC-BC",
            "SHAand192bitAES-CBC-BC",
            "SHAand128bitAES-CBC-BC",
            "SHAand3-keyTRIPLEDES-CBC",
            "SHAand2-keyTRIPLEDES-CBC",
            "SHAand128bitRC4",
            "SHAand40bitRC4",
            "SHAand128bitRC2-CBC",
            "SHAand40bitRC2-CBC",
            "SHAandTWOFISH-CBC",
            "SHAandIDEA-CBC"
        )

    @Throws(NoSuchAlgorithmException::class)
    fun getSalt(len: Int = 16): ByteArray {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(len)
        sr.nextBytes(salt)
        return salt
    }
}
