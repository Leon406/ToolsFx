package me.leon

import hash.argon2.Argon2PasswordEncoder
import hash.bcrypt.BCryptPasswordEncoder
import hash.password.*
import hash.scrypt.SCryptPasswordEncoder
import java.security.Security
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.bouncycastle.crypto.params.Argon2Parameters
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test

class Tmp {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    @Test
    fun simple() {
        val encoders: MutableMap<String, PasswordEncoder> = HashMap()
        encoders["argon2"] = Argon2PasswordEncoder()
        encoders["bcrypt"] = BCryptPasswordEncoder()
        encoders["scrypt"] = SCryptPasswordEncoder()
        encoders["ldap"] = LdapShaPasswordEncoder()
        encoders["pbkdf2"] = Pbkdf2PasswordEncoder()
        encoders["MD4"] = MessageDigestPasswordEncoder("MD4")
        encoders["MD5"] = MessageDigestPasswordEncoder("MD5")
        encoders["SHA-1"] = MessageDigestPasswordEncoder("SHA-1")
        encoders["SHA-256"] = MessageDigestPasswordEncoder("SHA-256")

        for ((k, v) in encoders) {
            println(k + " " + v.encode("123"))
            assertTrue { v.matches("123", v.encode("123")) }
        }
    }

    @Test
    fun digest() {
        val salt = "12345678".toByteArray()
        for (algorithm in Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.values()) {
            Pbkdf2PasswordEncoder().apply {
                setAlgorithm(algorithm)
                val r = encode("123")
                println(algorithm.name + " " + r)
                assertTrue { matches("123", r) }
                assertTrue { matches("123", encodeWithSalt("123", salt)) }
                assertTrue { matchesWithSalt("123", encodeWithSalt("123", salt), salt) }
            }
        }
    }

    @Test
    fun ldap() {
        val salt = "123456".toByteArray()
        LdapShaPasswordEncoder().apply {
            println(encode("123"))
            println(encode("123", null))
            println(encode("123", salt))
            assertTrue { matches("123", encode("123")) }
            assertTrue { matches("123", encode("123", null)) }
            assertEquals("{SSHA}/EMdfGQsjza+boN3JWsBkZIF+cwxMjM0NTY=", encode("123", salt))
            assertTrue { matches("123", encode("123", salt)) }
        }
    }

    @Test
    fun scrypt() {
        val salt = "123456".toByteArray()
        SCryptPasswordEncoder().apply {
            println(encode("123"))
            println(digest("123", salt))
            assertEquals(
                "\$e0801\$MTIzNDU2\$yToN3hhqWNrcFErZVOmOjF5mcVbrV9hj/SQl2x0ykQU=",
                digest("123", salt)
            )
            assertTrue { matches("123", encode("123")) }
            assertTrue { matches("123", digest("123", salt)) }
        }
    }

    @Test
    fun bcrypt() {
        val salt = "123456123456123456123456".toByteArray()
        BCryptPasswordEncoder().apply {
            println(encode("123"))
            //            println(encode("123", salt))

            assertTrue { matches("123", encode("123")) }

            println(encode("123", salt))
            assertEquals(
                "\$2a\$10\$KRGxLBS0KRGxLBS0KRGxL.UgZ0Pz.x0KwrgWDPUNirAXO0CnTUpI2",
                encode("123", salt)
            )

            strength = 12
            println(encode("123"))
            assertTrue { matches("123", encode("123")) }
            version = BCryptPasswordEncoder.BCryptVersion.`$2Y`

            println(encode("123"))
            assertTrue { matches("123", encode("123")) }
        }
    }

    @Test
    fun argon2() {
        val salt = "1234".toByteArray()
        Argon2PasswordEncoder().apply {
            println(encode("123"))
            assertTrue { matches("123", encode("123")) }
            println(encode("123", salt))
            assertTrue { matches("123", encode("123", salt)) }

            memory = 1 shl 14
            parallelism = 8

            iterations = 10
            println(encode("123", salt))
            assertTrue { matches("123", encode("123", salt)) }

            type = Argon2Parameters.ARGON2_i
            version = Argon2Parameters.ARGON2_VERSION_10

            println(encode("123", salt))
            assertTrue { matches("123", encode("123", salt)) }
        }
    }

    @Test
    fun ciphers() {

        Security.getProviders()
            //            .filter { it.name.contains("BC") }
            .map { provider ->
                provider.services
                    //                    .filter { it.type == "Mac" }
                    .groupBy { it.type }
                    .map {
                        "\n" + provider.name
                        " " + it.key + "\n\t" + it.value.joinToString("\n\t") { it.algorithm }
                    }
            }
            .also { println(it) }
    }

    @Test
    fun big() {
        "12".toBigInteger().toString(16).also { println(it) }
    }
}
