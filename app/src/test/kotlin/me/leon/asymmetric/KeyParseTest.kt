package me.leon.asymmetric

import java.io.File
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import me.leon.TEST_DATA_DIR
import me.leon.ext.crypto.toPrivateKey
import me.leon.ext.crypto.toPublicKey
import org.junit.Test

class KeyParseTest {

    @Test
    fun rsaParse() {

        File(TEST_DATA_DIR, "rsa/pri_2048_pkcs8.pem").readText().toPrivateKey("RSA").also {
            println(it.toString())
            println((it as RSAPrivateKey).privateExponent.toString(16))
        }

        File(TEST_DATA_DIR, "rsa/pub_2048_pcks1.pem").readText().toPublicKey("RSA").also {
            println(it)
            println((it as RSAPublicKey).publicExponent.toString(16))
            println(it.modulus.toString(16))
        }
    }
}
