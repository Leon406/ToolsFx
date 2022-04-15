package me.leon.asymmetric

import java.security.Security
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import me.leon.encode.base.base64
import me.leon.ext.crypto.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test

class Asymmetric {

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

        genKeys("RSA", 2048).also {
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
        genKeys(alg, emptyList()).also {
            println(it.joinToString("\n"))
            checkKeyPair(it[0], it[1], alg)
        }
        alg = "ElGamal"
        genKeys(alg, listOf(1024)).also {
            println(it.joinToString("\n"))
            checkKeyPair(it[0], it[1], alg)
        }
    }

    @Test
    fun sm2e() {
        val pub =
            "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEZuj3DBdRs/FDfIwQuUtqAZrh8ARPfHSxinUuQiBFpGMeqWAzwRAa3" +
                "pdBw97wR/xrNP/42/sUYvcj8Z6P9VMODQ=="
        val pri =
            "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg0e2GlRjoyVZ+i1O3ZTNHHvTUS2Y7yiOMej+egFst2CegCg" +
                "YIKoEcz1UBgi2hRANCAARm6PcMF1Gz8UN8jBC5S2oBmuHwBE98dLGKdS5CIEWkYx6pYDPBEBrel0HD3vBH/Gs0//jb+xR" +
                "i9yPxno/1Uw4N"

        val alg = "SM2"

        val testData = byteArrayOf(67)
        testData
            .asymmtricEncrypt(pub.toPublicKey(alg), alg)
            .run { asymmetricDecrypt(pri.toPrivateKey(alg), alg).contentEquals(testData) }
            .also { println(it) }

        val pubKey = pub.toPublicKey(alg).also { println(it?.encoded?.base64()) }

        val priKey = pri.toPrivateKey(alg).also { println(it?.encoded?.base64()) }
        "hello".toByteArray().asymmtricEncrypt(pubKey, "SM2").also {
            println(it.base64())
            it.asymmetricDecrypt(priKey, "SM2").also { println(it.decodeToString()) }
        }

        //        "".toByteArray().rsaEncrypt()
    }

    @Test
    fun deriveAndMatch() {
        pri.privateKeyDerivedPublicKey().run { assertTrue { checkKeyPair(this, pri) } }
    }
}
