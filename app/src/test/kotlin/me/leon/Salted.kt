package me.leon

import java.security.SecureRandom
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.hex2ByteArray
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test

/** for PBE process comprehend */
class Salted {
    init {
        Security.addProvider(BouncyCastleProvider())
    }
    private val saltSize = 8
    @Test
    fun salt() {
        val d = "-85297962_172051801"
        val e = "U2FsdGVkX192df0Gxgia8s93zZp85f9m2nU1VIGU+RZQDtViB1LPBnE0CBWgVDBj"
        val key = "583a01a9ba901a3adda7252ebca42c09"

        var salt = ByteArray(saltSize)
        SecureRandom().nextBytes(salt)
        //        salt = "ef9b329a95241506".hex2ByteArray()
        salt = "7675fd06c6089af2".hex2ByteArray()
        kdf(key.toByteArray(), salt).also {
            makeCipher(
                "AES/CBC/PKCS5Padding",
                it.sliceArray(0 until 32),
                it.sliceArray(32..it.lastIndex),
                Cipher.ENCRYPT_MODE
            )
                .also {
                    println(
                        ("Salted__".toByteArray() + salt + it.doFinal(d.toByteArray())).base64()
                    )
                }
        }

        decryptKeyIv(e, key.toByteArray()).also {
            makeCipher(
                "AES/CBC/PKCS5Padding",
                it.sliceArray(0 until 32),
                it.sliceArray(32..it.lastIndex),
                Cipher.DECRYPT_MODE
            )
                .also {
                    val base64Decode = e.base64Decode()
                    println(
                        it.doFinal(base64Decode.sliceArray((8 + saltSize)..base64Decode.lastIndex))
                            .decodeToString()
                    )
                }
        }
    }

    private fun makeCipher(alg: String, key: ByteArray, iv: ByteArray, cipherMode: Int) =
        Cipher.getInstance(alg).apply {
            val keySpec: SecretKey = SecretKeySpec(key, alg.substringBefore("/"))
            if (alg.contains("ECB|RC4".toRegex())) init(cipherMode, keySpec)
            else init(cipherMode, keySpec, IvParameterSpec(iv))
        }

    private fun kdf(pass: ByteArray, salt: ByteArray, outputSize: Int = 48): ByteArray {
        val tmpKey = pass + salt
        var key = Digests.hash("MD5", tmpKey)

        var resultKey = key
        while (resultKey.size < outputSize) {
            key = Digests.hash("MD5", key + tmpKey)
            resultKey += key
        }
        println(resultKey.size)
        return resultKey.sliceArray(0 until outputSize)
    }

    private fun decryptKeyIv(d: String, pass: ByteArray): ByteArray {
        // Salted__
        val salt = d.base64Decode().sliceArray(8 until (8 + saltSize))
        return kdf(pass, salt)
    }
}
