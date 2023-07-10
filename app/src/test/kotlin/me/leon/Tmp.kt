package me.leon

import java.io.File
import java.math.BigInteger
import java.security.Security
import javassist.ClassPool
import javassist.CtField
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import me.leon.ext.crypto.EncodeType
import me.leon.ext.math.eulerPhi
import me.leon.hash.argon2.Argon2PasswordEncoder
import me.leon.hash.bcrypt.BCryptPasswordEncoder
import me.leon.hash.password.*
import me.leon.hash.scrypt.SCryptPasswordEncoder
import org.bouncycastle.crypto.params.Argon2Parameters
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Ignore
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

        // 结果salt在前面, 编码salt在后
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
    @Ignore
    fun digest() {
        val salt = "12345678".toByteArray()
        // salt在前面
        for (algorithm in Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.values()) {
            Pbkdf2PasswordEncoder().apply {
                setAlgorithm(algorithm)
                val r = encode("123")
                println(algorithm.name + " " + r)
                assertTrue { matches("123", r) }
                assertTrue { matches("123", encodeWithSalt("123", salt).also { println(it) }) }
                assertTrue { matchesWithSalt("123", encodeWithSalt("123", salt), salt) }
            }
        }
    }

    @Test
    fun ldap() {
        val salt = "123456".toByteArray()
        // 结果计算salt在后面
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
        // 第二个 $为salt
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
    @Ignore
    fun bcrypt() {
        val salt = "123456123456123456123456".toByteArray()
        BCryptPasswordEncoder().apply {
            println(encode("Aa123456"))
            //            println(encode("123", salt))

            assertTrue { matches("123", encode("123")) }

            println(encode("123", salt))
            assertEquals(
                "\$2a\$10\$KRGxLBS0KRGxLBS0KRGxL.UgZ0Pz.x0KwrgWDPUNirAXO0CnTUpI2",
                encode("123", salt)
            )

            strength = 12
            println(encode("123"))
            measureTimeMillis { assertTrue { matches("123", encode("123")) } }.also { println(it) }
            strength = 14
            measureTimeMillis { assertTrue { matches("123", encode("123")) } }.also { println(it) }
            strength = 16
            measureTimeMillis { assertTrue { matches("123", encode("123")) } }.also { println(it) }
            version = BCryptPasswordEncoder.BCryptVersion.`$2Y`

            println(encode("123"))
            assertTrue { matches("123", encode("123")) }
        }
    }

    @Test
    fun argon2() {
        val salt = "1234".toByteArray()
        // 第四个$ 为salt
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
            .filter { it.name == "BC" }
            .flatMap { it.services }
            .filterNot { it.algorithm.contains("(\\.\\d+){4,}".toRegex()) } // 过滤掉OID算法
            //            .filter { it.type in listOf("Signature", "KeyPairGenerator") }
            .groupBy { it.type }
            .forEach { (k, v) ->
                println("$k Algorithm: \n\t${v.joinToString("\n\t") { it.algorithm }}")
            }
    }

    @Test
    fun big() {
        "12".toBigInteger().toString(16).also { println(it) }
        println(EncodeType.values().joinToString(" "))
    }

    @Test
    fun phi() {
        val list =
            mutableListOf(
                    "2",
                    "9857",
                    "80990192745708230644342256236014173435685606613945005625896333595456890957431",
                    "80990192745708230644342256236014173435685606613945005625896333595456890957431",
                    "80990192745708230644342256236014173435685606613945005625896333595456890957431",
                    "107843756547496736191228190917322558471918750590609940965721119253640998815543",
                    "107843756547496736191228190917322558471918750590609940965721119253640998815543"
                )
                .map { it.toBigInteger() }

        //        list.fold(list.product()) { acc, bigInteger ->
        //            println(acc )
        //            acc - acc / bigInteger
        //        }.also { println(it) }
        list
            .groupBy { it }
            .map { it.key to it.value.size }
            .fold(BigInteger.ONE) { acc, pair -> acc * pair.first.eulerPhi(pair.second) }
            .also { println(it) }
    }

    @Test
    fun assist() {
        val buildDir = "build"
        val file = File(buildDir, "classes/kotlin/main")
        println(file.absolutePath)
        val clazz = File(file.absolutePath, "me/leon/BuildConfig.class")
        clazz.parentFile.mkdirs()

        println("file $clazz  ${clazz.exists()}")
        val pool = ClassPool.getDefault()

        val ctClass = pool.makeClass("me.leon.BuildConfig")
        ctClass.addField(CtField.make("int HEIGHT = 6;", ctClass))
        ctClass.addField(CtField.make("public static int HEIGHT2 = 6;", ctClass))
        ctClass.writeFile(file.absolutePath)
    }

    @Test
    @Ignore
    fun assistModify() {
        val buildDir = "build"
        val file = File(buildDir, "classes/kotlin/main")
        val clazz = File(file.absolutePath, "me/leon/ConfigKt.class")

        println("file $clazz  ${clazz.exists()}")
        val pool = ClassPool.getDefault()
        val ctClass = pool.get("me.leon.ConfigKt")
        var versionField = ctClass.getDeclaredField("VERSION")
        println(versionField.constantValue)
        var dateField = ctClass.getDeclaredField("BUILD_DATE")
        println(dateField.constantValue)
        modifyField(
            dateField,
            "public static final java.lang.String BUILD_DATE =\"" + "2022" + "\";"
        )
        modifyField(
            versionField,
            "public static final java.lang.String VERSION =\"" + "1.15.6" + "\";"
        )
        dateField = ctClass.getDeclaredField("BUILD_DATE")
        versionField = ctClass.getDeclaredField("VERSION")
        println(dateField.constantValue)
        println(versionField.constantValue)
        ctClass.writeFile(file.absolutePath)
    }

    private fun modifyField(field: CtField, src: String) {
        println(src)
        val ctClass = field.declaringClass
        ctClass.removeField(field)
        ctClass.addField(CtField.make(src, ctClass))
    }
}
