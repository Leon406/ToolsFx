package me.leon.asymmetric

import java.security.Security
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import me.leon.encode.base.base64
import me.leon.ext.crypto.*
import me.leon.ext.hex2ByteArray
import me.leon.ext.toHex
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test

class AsymmetricTest {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    val pri =
        "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAI4PFaNoiyF51e4v63d4okNnf1URlT+j8JwHR1wRka5LK9" +
            "Rx+hAT8AMvjwpYECS8SEFxz9QKqQHf91NcMklyDvX5s2wHiWAu+KCsfBw0eW5K7WhED6MuiSGkWNVP8kAUvXFHL" +
            "1hZwH0qjpkqwGs3kmUigkVAfmD3hR84ulFUp82BAgMBAAECgYAb0bRpFbX5TkSoqlWwRb1w+bmjzRevKMmbpIlC" +
            "7GXc/feNWOyhbWYZGZ0nZ2tx5jU4K7OJULUcGuHyPyHR5DYthlxo3OP2UpnQlCUXs8M3jorch8Z8BOv1EKETigR" +
            "HDsalbXtddF4l90c5V43XxVgFxntWZITxwTOQm0G4th0UKQJBAM2BWH9t1ETDUy2eI1VOiooK1ijJ27BaXIAhPe" +
            "50GOrTcEjlr2au7NPRFw/5vqcQP8TxSWNlXM53jnIdySP6L9sCQQCw9tlhUGv9CROcMhyZiLvndEg//qb9uXEnI" +
            "GjXs9Qz+SjYSvUxOz8WDF3JFt/iajoj5bOJ5FnFIcuu0/w+w9TTAkEAzSHRvtFY05LNknmJ93tA2u5aO7jS7EQm" +
            "lVeZRE7rGGwaZwmuficaC41pIe8/me+kV+gqQ2dIrme07sBAqQLxhQJBAKVb1J65Xl8QdzGSJeVVvne10blyxCn" +
            "8eX5dK3q7wANcxEzwJhN90CJTJeO8qzHPn0ph3pVwOm4ZeVGBJoijxx8CQAfYcZWIuAQvpeP890BaKehYWPZe/5" +
            "Ch83wJXjKzJnyKByaXA58wLmWJu0mkb8NmLeqEEZzujT0I8xnVRwXHT68="

    @Test
    fun rsaKey() {

        genBase64KeyArray("RSA", 2048).also {
            println(it.joinToString("\n"))
            checkKeyPair(it[0], it[1])
        }

        pkcs8ToPkcs1(pri).also {
            println(it)
            pkcs1ToPkcs8(it).also { assertEquals(pri, it) }
        }
    }

    @Test
    fun sm2() {
        var alg = "SM2"
        genBase64KeyArray(alg, emptyList()).also {
            println(it.joinToString("\n"))
            checkKeyPair(it[0], it[1], alg)
        }
        alg = "ElGamal"
        genBase64KeyArray(alg, listOf(1024)).also {
            println(it.joinToString("\n"))
            checkKeyPair(it[0], it[1], alg)
        }
    }

    @Test
    fun sm2e() {
        val pub =
            "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEZuj3DBdRs/FDfIwQuUtqAZrh8ARPfHSxinUuQiBFpGMeqWAzwRAa3pdBw9" +
                "7wR/xrNP/42/sUYvcj8Z6P9VMODQ=="
        val pri =
            "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg0e2GlRjoyVZ+i1O3ZTNHHvTUS2Y7yiOMej+egFst2Ce" +
                "gCgYIKoEcz1UBgi2hRANCAARm6PcMF1Gz8UN8jBC5S2oBmuHwBE98dLGKdS5CIEWkYx6pYDPBEBrel0HD" +
                "3vBH/Gs0//jb+xRi9yPxno/1Uw4N"

        val alg = "SM2"

        // 从ans1
        (pub.toPublicKey(alg) as BCECPublicKey).also { println(it.q.getEncoded(false).toHex()) }

        (pri.toPrivateKey(alg) as BCECPrivateKey).also { println(it.d.toString(16)) }
        //  转ans1

        val testData = byteArrayOf(67)
        testData
            .asymmetricEncrypt(pub.toPublicKey(alg), alg)
            .run { asymmetricDecrypt(pri.toPrivateKey(alg), alg).contentEquals(testData) }
            .also { println(it) }

        val pubKey = pub.toPublicKey(alg).also { println(it?.encoded?.base64()) }

        val priKey = pri.toPrivateKey(alg).also { println(it?.encoded?.base64()) }
        "hello".toByteArray().asymmetricEncrypt(pubKey, "SM2").also {
            println(it.base64())
            it.asymmetricDecrypt(priKey, "SM2").also { println(it.decodeToString()) }
        }

        //        "".toByteArray().rsaEncrypt()
    }

    @Test
    fun deriveAndMatch() {
        pri.privateKeyDerivedPublicKey().run { assertTrue { checkKeyPair(this, pri) } }
    }

    @Test
    fun cc() {
        val d =
            "048a1a666e653da9aa19e7112ab01da87af8ce3772de6f8585bbb103ec370ca8d8769814ad562c3ff8574501d6fa83af93a5d7" +
                "a584cccdb3d0bf97fe23eceb0d5a3fa354d1f6eb20c5a99a6f93f5f9edb43f5e11f87689a6c3fef25276759b751" +
                "9b2cff6c44d9c"
        println(d)
        val c1c3c2 = changeC1C2C3ToC1C3C2(d.hex2ByteArray()).toHex()
        println(c1c3c2)
        println(changeC1C3C2ToC1C2C3(c1c3c2.hex2ByteArray()).toHex())
    }

    /**
     * bc加解密使用旧标c1||c2||c3，此方法在加密后调用，将结果转化为c1||c3||c2
     * @param c1c2c3
     * @return
     */
    private fun changeC1C2C3ToC1C3C2(c1c2c3: ByteArray): ByteArray {
        val c1Len = 65
        val c3Len = 32
        val result = ByteArray(c1c2c3.size)
        System.arraycopy(c1c2c3, 0, result, 0, c1Len) // c1
        System.arraycopy(c1c2c3, c1c2c3.size - c3Len, result, c1Len, c3Len) // c3
        System.arraycopy(c1c2c3, c1Len, result, c1Len + c3Len, c1c2c3.size - c1Len - c3Len) // c2
        return result
    }

    /**
     * bc加解密使用旧标c1||c3||c2，此方法在解密前调用，将密文转化为c1||c2||c3再去解密
     * @param c1c3c2
     * @return
     */
    private fun changeC1C3C2ToC1C2C3(c1c3c2: ByteArray): ByteArray {
        val c1Len = 65
        val c3Len = 32
        val result = ByteArray(c1c3c2.size)
        System.arraycopy(c1c3c2, 0, result, 0, c1Len) // c1: 0->65
        System.arraycopy(c1c3c2, c1Len + c3Len, result, c1Len, c1c3c2.size - c1Len - c3Len) // c2
        System.arraycopy(c1c3c2, c1Len, result, c1c3c2.size - c3Len, c3Len) // c3
        return result
    }
}
