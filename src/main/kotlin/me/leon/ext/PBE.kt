package me.leon.ext

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

object PBE {
    val PBE_HMAC =
        arrayOf(
            "PBEWITHHMACTIGER",
            "PBEWITHHMACSHA256",
            "PBKDF2WITHHMACSHA256",
            "PBKDF2WITHHMACSHA224",
            "PBEWITHHMACRIPEMD160",
            "PBKDF2WITHHMACSHA3-384",
            "PBKDF2WITHHMACGOST3411",
            "PBKDF2WITHHMACSM3",
            "PBKDF2WITHHMACSHA384",
            "PBKDF2WITHHMACSHA3-512",
            "PBKDF2WITHHMACSHA512",
            "PBEWITHHMACSHA1",
            "PBEWITHHMACGOST3411",
            "PBKDF2WITHHMACSHA3-256",
            "PBKDF2WITHHMACSHA3-224",
        )

    val PBE_CRYPTO =
        arrayOf(
            "PBEWITHSHAAND40BITRC2-CBC",
            "PBEWITHSHAAND128BITAES-CBC-BC",
            "PBEWITHSHA256AND256BITAES-CBC-BC",
            "PBEWITHMD5ANDRC2",
            "PBEWITHSHAAND2-KEYTRIPLEDES-CBC",
            "PBEWITHSHAAND3-KEYTRIPLEDES-CBC",
            "PBEWITHSHAAND128BITRC2-CBC",
            "PBEWITHSHAAND40BITRC4",
            "PBEWITHSHAAND128BITRC4",
            "PBEWITHSHA256AND128BITAES-CBC-BC",
            "PBEWITHSHAAND192BITAES-CBC-BC",
            "PBEWITHSHA1ANDDES",
            "PBEWITHSHAAND256BITAES-CBC-BC",
            "PBEWITHSHA256AND192BITAES-CBC-BC",
            "PBEWITHSHA1ANDRC2",
            "PBEWITHMD2ANDDES",
            "PBEWITHMD5ANDDES",
            "PBEWITHMD5AND192BITAES-CBC-OPENSSL",
            "PBEWITHMD5AND128BITAES-CBC-OPENSSL",
            "PBEWITHSHAANDTWOFISH-CBC",
            "PBEWITHSHAANDIDEA-CBC",
            "PBEWITHMD5AND256BITAES-CBC-OPENSSL"
        )

    @Throws(NoSuchAlgorithmException::class)
    fun getSalt(len: Int = 16): ByteArray {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(len)
        sr.nextBytes(salt)
        return salt
    }
}
