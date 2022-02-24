package me.leon.controller

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec
import me.leon.encode.base.*
import me.leon.ext.*
import tornadofx.Controller

class PBEController : Controller() {
    fun encrypt(
        password: String,
        data: String,
        salt: ByteArray,
        alg: String,
        iteration: Int,
        keyLength: Int,
        isSingleLine: Boolean
    ) =
        if (isSingleLine)
            data.lineAction2String { encrypt(password, it, salt, alg, iteration, keyLength) }
        else encrypt(password, data, salt, alg, iteration, keyLength)

    fun encrypt(
        password: String,
        data: String,
        salt: ByteArray,
        alg: String,
        iteration: Int,
        keyLength: Int
    ): String =
        catch({ "encrypt error: $it" }) {
            if (alg.contains("HMAC"))
                return@catch generatePBEKey(password, salt, alg, keyLength, iteration)
                    .encoded
                    .base64()
            val cipher = makeCipher(alg, password, salt, iteration, keyLength, Cipher.ENCRYPT_MODE)
            // openssl
            ("Salted__".toByteArray() + salt + cipher.doFinal(data.toByteArray())).base64()
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

    fun decrypt(
        password: String,
        data: String,
        saltLength: Int,
        alg: String,
        iteration: Int,
        keyLength: Int,
        isSingleLine: Boolean
    ) =
        if (isSingleLine)
            data.lineAction2String { decrypt(password, it, saltLength, alg, iteration, keyLength) }
        else decrypt(password, data, saltLength, alg, iteration, keyLength)

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
        return catch({ "decrypt error: $it" }) {
            if (alg.contains("HMAC"))
                return@catch generatePBEKey(key, salt, alg, keyLength, iteration).encoded.base64()
            println("decrypt  $alg $data")
            val cipher = makeCipher(alg, key, salt, iteration, keyLength, Cipher.DECRYPT_MODE)

            cipher
                .doFinal(base64Decode.sliceArray((8 + saltLength)..base64Decode.lastIndex))
                .decodeToString()
        }
    }

    private fun generatePBEKey(
        password: String,
        salt: ByteArray = byteArrayOf(),
        alg: String,
        keyLen: Int = 128,
        saltLen: Int = 16,
        iterations: Int = 1000
    ): SecretKey {
        val chars = password.toCharArray()
        val saltBytes = salt.takeUnless { it.isEmpty() } ?: PBE.getSalt(saltLen)
        val spec = PBEKeySpec(chars, saltBytes, iterations, keyLen)
        val skf = SecretKeyFactory.getInstance(alg)
        return skf.generateSecret(spec)
    }

    fun getSalt(length: Int) = PBE.getSalt(length)
}
