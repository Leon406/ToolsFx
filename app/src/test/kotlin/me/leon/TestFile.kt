package me.leon

import java.io.File
import java.math.BigInteger

val TEST_PRJ_DIR: File = File("").absoluteFile.parentFile
val TEST_DATA_DIR = File(TEST_PRJ_DIR, "testdata")
val TEST_CTF_DIR = File(TEST_DATA_DIR, "ctf")
val TEST_OCR_DIR = File(TEST_DATA_DIR, "qrcode")
val TEST_RSA_DIR = File(TEST_CTF_DIR, "rsa")

fun String.parseRsaParams(dir: File = TEST_RSA_DIR) =
    File(dir, this).readText().let {
        it.replace("\"|'", "").split("\n|\r\n".toRegex()).filterNot { it.isBlank() }.fold(
            mutableMapOf<String, BigInteger>()
        ) { acc, s ->
            acc.apply {
                with(s.split("\\s*=\\s*".toRegex())) {
                    acc[this[0]] =
                        this[1].takeUnless { it.startsWith("0x") }?.toBigInteger()
                            ?: this[1].substring(2).toBigInteger(16)
                }
            }
        }
    }
