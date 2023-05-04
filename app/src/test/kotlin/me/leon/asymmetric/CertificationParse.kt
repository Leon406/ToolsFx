package me.leon.asymmetric

import java.io.File
import java.security.Security
import me.leon.TEST_DATA_DIR
import me.leon.encode.base.base64Decode
import me.leon.ext.crypto.parsePublicKeyFromCerFile
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.util.ASN1Dump
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

    @Test
    fun ans1() {
        ASN1InputStream(File(TEST_DATA_DIR, "signature/SM2.cer").inputStream()).use {
            println(ASN1Dump.dumpAsString(it.readObject(), true))
        }
        ASN1InputStream(File(TEST_DATA_DIR, "signature/p7.txt").readText().base64Decode()).use {
            println(ASN1Dump.dumpAsString(it.readObject(), true))
        }
    }
}
