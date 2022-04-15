package me.leon.symmetric

import java.security.Security
import kotlin.test.assertTrue
import me.leon.encode.base.base64
import me.leon.ext.crypto.decrypt
import me.leon.ext.crypto.encrypt
import org.junit.Test

class Symmetric {
    //  Shacal2  VMPCKSA3 ARC4 GOST28147 GOST3412_2015 Noekeon

    init {
        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
    }

    @Test
    fun sys() {
        val plain = "Hello World!你好".toByteArray()
        val key = "1234567890123456".toByteArray()
        val iv = "1234567890123456".toByteArray()
        plain.encrypt(key, iv, "Shacal2").let {
            println(it.base64())
            it.decrypt(key, iv, "Shacal2").let { assertTrue { plain.contentEquals(it) } }
        }
        plain.encrypt(key, iv, "VMPC-KSA3").let {
            println(it.base64())
            it.decrypt(key, iv, "VMPC-KSA3").let { assertTrue { plain.contentEquals(it) } }
        }
        plain.encrypt(key, iv, "ARC4").let {
            println(it.base64())
            it.decrypt(key, iv, "ARC4").let { assertTrue { plain.contentEquals(it) } }
        }
        plain.encrypt(key + key, iv, "GOST28147").let {
            println(it.base64())
            it.decrypt(key + key, iv, "GOST28147").let { assertTrue { plain.contentEquals(it) } }
        }
        plain.encrypt(key + key, iv, "GOST3412-2015").let {
            println(it.base64())
            it.decrypt(key + key, iv, "GOST3412-2015").let {
                assertTrue { plain.contentEquals(it) }
            }
        }
        plain.encrypt(key, iv, "Noekeon").let {
            println(it.base64())
            it.decrypt(key, iv, "Noekeon").let { assertTrue { plain.contentEquals(it) } }
        }
    }
}
