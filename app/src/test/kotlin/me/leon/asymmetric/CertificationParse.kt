package me.leon.asymmetric

import java.io.File
import java.security.Security
import me.leon.TEST_DATA_DIR
import me.leon.ext.crypto.parsePublicKeyFromCerFile
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test

class CertificationParse {
    init {
        Security.addProvider(BouncyCastleProvider())
    }
    @Test
    fun parseSm2() {
        File(TEST_DATA_DIR, "signature/SM2.cer").parsePublicKeyFromCerFile().also { println(it) }
    }
}
