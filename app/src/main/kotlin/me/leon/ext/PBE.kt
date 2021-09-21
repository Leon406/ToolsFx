package me.leon.ext

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
            "PBEWithSHAand40bitRC2-CBC",
            "PBEWithSHAand128bitAES-CBC-BC",
            "PBEWithSHA256and256bitAES-CBC-BC",
            "PBEWithMD5andRC2",
            "PBEWithSHAand2-keyTRIPLEDES-CBC",
            "PBEWithSHAand3-keyTRIPLEDES-CBC",
            "PBEWithSHAand128bitRC2-CBC",
            "PBEWithSHAand40bitRC4",
            "PBEWithSHAand128bitRC4",
            "PBEWithSHA256and128bitAES-CBC-BC",
            "PBEWithSHAand192bitAES-CBC-BC",
            "PBEWithSHA1andDES",
            "PBEWithSHAand256bitAES-CBC-BC",
            "PBEWithSHA256and192bitAES-CBC-BC",
            "PBEWithSHA1andRC2",
            "PBEWithMD2andDES",
            "PBEWithMD5andDES",
            "PBEWithMD5and192bitAES-CBC-OPENSSL",
            "PBEWithMD5and128bitAES-CBC-OPENSSL",
            "PBEWithSHAandTWOFISH-CBC",
            "PBEWithSHAandIDEA-CBC",
            "PBEWithMD5and256bitAES-CBC-OPENSSL"
        )

    @Throws(NoSuchAlgorithmException::class)
    fun getSalt(len: Int = 16): ByteArray {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(len)
        sr.nextBytes(salt)
        return salt
    }
}
