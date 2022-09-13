package me.leon.ext.crypto

import javax.crypto.Cipher
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.hash

const val SALT_SIZE = 8
const val SALT_PREFIX = "Salted__"

fun String.openSslEncrypt(
    alg: String,
    pass: ByteArray,
    salt: ByteArray,
    keySize: Int = 32,
    ivSize: Int = 16,
    hash: String = "MD5"
): String {
    val kdf = kdf(pass, salt, keySize + ivSize, hash)
    return makeCipher(
            alg,
            kdf.sliceArray(0 until keySize),
            kdf.sliceArray(keySize..kdf.lastIndex),
            Cipher.ENCRYPT_MODE
        )
        .run {
            (SALT_PREFIX.toByteArray() + salt + doFinal(this@openSslEncrypt.toByteArray())).base64()
        }
}

fun String.openSslDecrypt(
    alg: String,
    pass: ByteArray,
    keySize: Int = 32,
    ivSize: Int = 16,
    hash: String = "MD5"
): String {
    val kdf = parseKdf(this, pass, keySize + ivSize, hash)
    return makeCipher(
            alg,
            kdf.sliceArray(0 until keySize),
            kdf.sliceArray(keySize..kdf.lastIndex),
            Cipher.DECRYPT_MODE
        )
        .run {
            val base64Decode = base64Decode()
            doFinal(base64Decode.sliceArray((8 + SALT_SIZE)..base64Decode.lastIndex))
                .decodeToString()
        }
}

private fun kdf(
    pass: ByteArray,
    salt: ByteArray,
    outputSize: Int = 48,
    hash: String = "MD5"
): ByteArray {
    val tmpKey = pass + salt
    var key = tmpKey.hash(hash)

    var resultKey = key
    while (resultKey.size < outputSize) {
        key = (key + tmpKey).hash(hash)
        resultKey += key
    }
    println(resultKey.size)
    return resultKey.sliceArray(0 until outputSize)
}

private fun parseKdf(
    d: String,
    pass: ByteArray,
    outputSize: Int = 48,
    hash: String = "MD5"
): ByteArray {
    val salt = d.base64Decode().sliceArray(8 until (8 + SALT_SIZE))
    return kdf(pass, salt, outputSize, hash)
}
