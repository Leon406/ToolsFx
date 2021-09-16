package me.leon.controller

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec
import me.leon.encode.base.base64
import me.leon.ext.PBE
import me.leon.ext.catch
import tornadofx.Controller

class PBEController : Controller() {
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
            cipher.doFinal(data.toByteArray()).base64()
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
        key: String,
        data: String,
        salt: ByteArray,
        alg: String,
        iteration: Int,
        keyLength: Int
    ) =
        catch({ "decrypt error: $it" }) {
            if (alg.contains("HMAC"))
                return@catch generatePBEKey(key, salt, alg, keyLength, iteration).encoded.base64()
            println("decrypt  $alg")
            val cipher = makeCipher(alg, key, salt, iteration, keyLength, Cipher.DECRYPT_MODE)
            cipher.doFinal(data.toByteArray()).base64()
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
}
