package me.leon.ext.crypto

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.*
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode

object PBE {
    val PBE_HMAC =
        arrayOf(
            "PBKDF2WithHmacSHA1",
            "PBKDF2WithHmacSHA224",
            "PBKDF2WithHmacSHA256",
            "PBKDF2WithHmacSHA384",
            "PBKDF2WithHmacSHA512",
            "PBKDF2WithHmacGOST3411",
            "PBKDF2WithHmacSHA3-224",
            "PBKDF2WithHmacSHA3-256",
            "PBKDF2WithHmacSHA3-384",
            "PBKDF2WithHmacSHA3-512",
            "PBEWithHmacTIGER",
            "PBEWithHmacSHA256",
            "PBEWithHmacRIPEMD160",
            "PBEWithHmacGOST3411",
            "PBKDF2WithHmacSM3",
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
    fun getSalt(len: Int = 8): ByteArray {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(len)
        sr.nextBytes(salt)
        return salt
    }

    private fun generatePBEKey(
        password: String,
        salt: ByteArray = byteArrayOf(),
        alg: String,
        keyLen: Int = 128,
        saltLen: Int = 8,
        iterations: Int = 1
    ): SecretKey {
        val chars = password.toCharArray()
        val saltBytes = salt.takeUnless { it.isEmpty() } ?: getSalt(saltLen)
        val spec = PBEKeySpec(chars, saltBytes, iterations, keyLen)
        val skf = SecretKeyFactory.getInstance(alg)
        return skf.generateSecret(spec)
    }

    fun decrypt(
        key: String,
        data: String,
        saltLength: Int,
        alg: String,
        iteration: Int,
        keyLength: Int
    ): String {
        val base64Decode = data.base64Decode()
        val salt = base64Decode.sliceArray(8 until (8 + saltLength))
        if (alg.contains("HMAC", true))
            return generatePBEKey(key, salt, alg, keyLength, iteration).encoded.base64()
        val cipher = makeCipher(alg, key, salt, iteration, keyLength, Cipher.DECRYPT_MODE)
        return cipher
            .doFinal(base64Decode.sliceArray((8 + saltLength)..base64Decode.lastIndex))
            .decodeToString()
    }

    private fun makeCipher(
        alg: String,
        pwd: String,
        salt: ByteArray,
        iteration: Int,
        keyLength: Int,
        cipherMode: Int
    ) =
        Cipher.getInstance(alg).apply {
            val key = generatePBEKey(pwd, salt, alg, keyLength, iteration)
            val pbeParameterSpec = PBEParameterSpec(salt, iteration)
            this.init(cipherMode, key, pbeParameterSpec)
            println("key ${key.encoded.base64()}  iv ${iv?.base64()}")
        }

    fun encrypt(
        password: String,
        data: String,
        salt: ByteArray,
        alg: String,
        iteration: Int,
        keyLength: Int
    ): String {
        if (alg.contains("HMAC", true))
            return generatePBEKey(password, salt, alg, keyLength, iteration).encoded.base64()
        val cipher = makeCipher(alg, password, salt, iteration, keyLength, Cipher.ENCRYPT_MODE)
        // openssl
        return ("Salted__".toByteArray() + salt + cipher.doFinal(data.toByteArray())).base64()
    }
}
