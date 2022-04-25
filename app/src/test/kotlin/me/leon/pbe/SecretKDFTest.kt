package me.leon.pbe

import java.security.Security
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.crypto.PBE
import me.leon.ext.toHex
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test

class SecretKDFTest {
    init {
        Security.addProvider(BouncyCastleProvider())
    }

    // https://8gwifi.org/pbkdf.jsp
    @Test
    fun kdf() {

        PBE.PBE_HMAC
        val iteration = 1000
        val keyLen = 128
        val salt = "tsrBc8LGmGN/LRYH/Q+FDg==".base64Decode()
        val pbeKeySpec = PBEKeySpec("password".toCharArray(), salt, iteration, keyLen)

        for (alg in PBE.PBE_HMAC) {
            SecretKeyFactory.getInstance(alg).generateSecret(pbeKeySpec).encoded.also {
                println("$alg " + it.base64())
                println("$iteration:${salt.toHex()}:${it.toHex()}")
            }
        }
    }

    fun salt(): ByteArray {
        val salt = ByteArray(16)
        val random = java.security.SecureRandom()
        random.nextBytes(salt)
        return salt
    }
}
