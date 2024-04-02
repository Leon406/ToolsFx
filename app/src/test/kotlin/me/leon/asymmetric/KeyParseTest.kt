package me.leon.asymmetric

import java.io.File
import me.leon.TEST_DATA_DIR
import me.leon.ext.crypto.*
import org.junit.Test

class KeyParseTest {

    @Test
    fun rsaParse() {

        File(TEST_DATA_DIR, "rsa/pri_2048_pkcs8.pem").readText().toPrivateKey("RSA").also {
            println(it!!.parseRsaInfo())
        }

        File(TEST_DATA_DIR, "rsa/pub_2048_pkcs1.pem").readText().toPublicKey("RSA").also {
            println(it!!.parseRsaInfo())
        }
    }
}
