package me.leon.pbe

import java.security.SecureRandom
import java.security.Security
import me.leon.ext.crypto.openSslDecrypt
import me.leon.ext.crypto.openSslEncrypt
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
    fun saltAes() {
        val alg = "AES/CBC/PKCS5Padding"
        val d = "-85297962_172051801"
        val e = "U2FsdGVkX192df0Gxgia8s93zZp85f9m2nU1VIGU+RZQDtViB1LPBnE0CBWgVDBj"
        val password = "583a01a9ba901a3adda7252ebca42c09".toByteArray()
        val keySize = 32
        val ivSize = 16
        var salt = ByteArray(saltSize)
        SecureRandom().nextBytes(salt)
        //        salt = "ef9b329a95241506".hex2ByteArray()
        // 随机生成
        salt = "7675fd06c6089af2".hex2ByteArray()
        // md5 openssl
        println(d.openSslEncrypt(alg, password, salt, keySize, ivSize))

        println(e.openSslDecrypt(alg, password, keySize, ivSize))
    }

    @Test
    fun saltDes() {
        val e = "U2FsdGVkX1/a0jOebm4TjoQUIxsRyRm88opg+LmNUFQ="
        val password = "modern".toByteArray()
        val keySize = 8
        val ivSize = 8
        val d = "hello"

        var salt = ByteArray(saltSize)
        // 随机生成
        SecureRandom().nextBytes(salt)
        salt = "7675fd06c6089af2".hex2ByteArray()
        // md5
        val alg = "DES/CBC/PKCS5Padding"
        println(d.openSslEncrypt(alg, password, salt, keySize, ivSize))
        // md2
        println(d.openSslEncrypt(alg, password, salt, keySize, ivSize, "MD2"))
        // sha1
        println(d.openSslEncrypt(alg, password, salt, keySize, ivSize, "SHA1"))
        println(e.openSslDecrypt(alg, password, keySize, ivSize))
    }

    @Test
    fun saltDes3() {
        val e = "U2FsdGVkX182QzHidtsqbtNnHgTgYNY8"
        val password = "modern".toByteArray()
        val keySize = 24
        val ivSize = 8
        val d = "hello"

        var salt = ByteArray(saltSize)
        // 随机生成
        SecureRandom().nextBytes(salt)
        salt = "7675fd06c6089af2".hex2ByteArray()
        // md5
        val alg = "DESede/CBC/PKCS5Padding"
        println(d.openSslEncrypt(alg, password, salt, keySize, ivSize))
        // md2
        println(d.openSslEncrypt(alg, password, salt, keySize, ivSize, "MD2"))
        // sha1
        println(d.openSslEncrypt(alg, password, salt, keySize, ivSize, "SHA1"))
        println(e.openSslDecrypt(alg, password, keySize, ivSize))
    }

    @Test
    fun saltRc4() {
        val e = "U2FsdGVkX192df0Gxgia8vdqVBpA"
        val password = "modern".toByteArray()
        val keySize = 32
        val ivSize = 0
        val d = "hello"

        var salt = ByteArray(saltSize)
        // 随机生成
        SecureRandom().nextBytes(salt)
        salt = "7675fd06c6089af2".hex2ByteArray()
        // md5
        val alg = "RC4"
        println(d.openSslEncrypt(alg, password, salt, keySize, ivSize))
        println(e.openSslDecrypt(alg, password, keySize, ivSize))
    }
}
