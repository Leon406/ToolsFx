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
            "MD5andRC4",
            "MD5andDES",
            "MD5andTripleDES",
            "MD5and192bitAES-CBC-OPENSSL",
            "MD5and128bitAES-CBC-OPENSSL",
            "MD5andRC2",
            "MD2andDES",
            "SHA1andDES",
            "SHA1andRC2",
            "SHA256and256bitAES-CBC-BC",
            "SHA256and192bitAES-CBC-BC",
            "SHA256and128bitAES-CBC-BC",
            "SHAand256bitAES-CBC-BC",
            "SHAand192bitAES-CBC-BC",
            "SHAand128bitAES-CBC-BC",
            "SHAand3-keyTripleDES-CBC",
            "SHAand2-keyTripleDES-CBC",
            "SHAand128bitRC4",
            "SHAand40bitRC4",
            "SHAand128bitRC2-CBC",
            "SHAand40bitRC2-CBC",
            "SHAandTwoFISH-CBC",
            "SHAandIDEA-CBC"
        )

    fun decrypt(
        password: String,
        data: String,
        saltLength: Int,
        alg: String,
        iteration: Int = 1,
        keyLength: Int = 128
    ): String {
        val base64Decode = data.base64Decode()
        val salt = base64Decode.sliceArray(8 until (8 + saltLength))
        return when {
            alg.contains("HMAC", true) ->
                generatePBEKey(password, salt, alg, keyLength, iteration).encoded.base64()
            alg == "PBEWithMD5andRC4" -> data.openSslDecrypt("RC4", password.toByteArray(), 32, 0)
            alg == "PBEWithMD5andTripleDES" ->
                data.openSslDecrypt("DESede/CBC/PKCS5Padding", password.toByteArray(), 24, 8)
            else ->
                with(makeCipher(alg, password, salt, iteration, keyLength, Cipher.DECRYPT_MODE)) {
                    doFinal(base64Decode.sliceArray((8 + saltLength)..base64Decode.lastIndex))
                        .decodeToString()
                }
        }
    }

    fun encrypt(
        password: String,
        data: String,
        salt: ByteArray,
        alg: String,
        iteration: Int = 1,
        keyLength: Int = 128
    ) =
        when {
            alg.contains("HMAC", true) ->
                generatePBEKey(password, salt, alg, keyLength, iteration).encoded.base64()
            alg == "PBEWithMD5andRC4" ->
                data.openSslEncrypt("RC4", password.toByteArray(), salt, 32, 0)
            alg == "PBEWithMD5andTripleDES" ->
                data.openSslEncrypt("DESede/CBC/PKCS5Padding", password.toByteArray(), salt, 24, 8)
            else ->
                with(makeCipher(alg, password, salt, iteration, keyLength, Cipher.ENCRYPT_MODE)) {
                    ("Salted__".toByteArray() + salt + doFinal(data.toByteArray())).base64()
                }
        }

    @Throws(NoSuchAlgorithmException::class)
    fun getSalt(len: Int = 8): ByteArray {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(len)
        sr.nextBytes(salt)
        return salt
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
}
