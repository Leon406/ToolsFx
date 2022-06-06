package me.leon.symmetric

import java.security.Security
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import me.leon.encode.base.base64
import me.leon.ext.crypto.decrypt
import me.leon.ext.crypto.encrypt
import me.leon.ext.toHex
import org.junit.Test

class SymmetricTest {
    //  Shacal2  VMPCKSA3 ARC4 GOST28147 GOST3412_2015 Noekeon

    init {
        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
    }

    @Test
    fun sys() {
        val plain = "Hello World!你好".toByteArray()
        val key = "1234567890123456".toByteArray()
        val iv = "1234567890123456".toByteArray()
        plain.encrypt(key, iv, "Shacal2").also {
            println(it.base64())
            it.decrypt(key, iv, "Shacal2").also { assertTrue { plain.contentEquals(it) } }
        }
        plain.encrypt(key, iv, "VMPC-KSA3").also {
            println(it.base64())
            it.decrypt(key, iv, "VMPC-KSA3").also { assertTrue { plain.contentEquals(it) } }
        }
        plain.encrypt(key, iv, "ARC4").also {
            println(it.base64())
            it.decrypt(key, iv, "ARC4").also { assertTrue { plain.contentEquals(it) } }
        }
        plain.encrypt(key + key, iv, "GOST28147").also {
            println(it.base64())
            it.decrypt(key + key, iv, "GOST28147").also { assertTrue { plain.contentEquals(it) } }
        }
        plain.encrypt(key + key, iv, "GOST3412-2015").also {
            println(it.base64())
            it.decrypt(key + key, iv, "GOST3412-2015").also {
                assertTrue { plain.contentEquals(it) }
            }
        }
        plain.encrypt(key, iv, "Noekeon").also {
            println(it.base64())
            it.decrypt(key, iv, "Noekeon").also { assertTrue { plain.contentEquals(it) } }
        }
    }

    @Test
    fun aead() {
        // OCB
        val key = "1234567890123456".toByteArray()
        var iv = "123456712345678".toByteArray()
        var alg = "AES/OCB/NOPadding"
        val raw = "Hello1234567890Hello1234567890"
        var data = raw.toByteArray()
        val ad = "1234567812345678".toByteArray()

        data.encrypt(key, iv, alg, ad).let {
            assertEquals(
                "1509a68ab16bba4831cf1b24eae69e90113b05f619d8107da9dcd58c3ee9d1bc906cdf1efc7844962ba0f5e436d2",
                it.toHex()
            )
            data = it
        }
        assertEquals(raw, data.decrypt(key, iv, alg, ad).decodeToString())

        // GCM
        iv = "1234567890123456".toByteArray()
        alg = "AES/GCM/NOPadding"
        raw.toByteArray().encrypt(key, iv, alg, ad).let {
            assertEquals(
                "cec189d0e85c89f4a0518f06b9834af95cfb2bfe89d0920c9386ebba5e4fc3d13a7bb5093004bc6470ba06fca0e6",
                it.toHex()
            )
            data = it
        }
        assertEquals(raw, data.decrypt(key, iv, alg, ad).decodeToString())

        // EAX
        alg = "AES/EAX/NOPadding"
        raw.toByteArray().encrypt(key, iv, alg, ad).let {
            assertEquals(
                "da1b29c03b1cd9c64296fef198928d25885ae9790b262635b3d3aa4d6fe8fe42aa74c6b13d53",
                it.toHex()
            )
            data = it
        }
        assertEquals(raw, data.decrypt(key, iv, alg, ad).decodeToString())

        // CCM
        iv = "1234567".toByteArray()
        alg = "AES/CCM/NOPadding"
        raw.toByteArray().encrypt(key, iv, alg, ad).let {
            assertEquals(
                "2e369d2d7fa37c905e7e6b9f232a8573cb6b6df1f981fcff924a12b75a71d38fb8d30d54a118",
                it.toHex()
            )
            data = it
        }
        assertEquals(raw, data.decrypt(key, iv, alg, ad).decodeToString())
    }
}
