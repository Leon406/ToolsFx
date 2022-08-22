package me.leon

import java.io.File
import me.leon.ext.parseRsaParams

val TEST_PRJ_DIR: File = File("").absoluteFile.parentFile
val TEST_DATA_DIR = File(TEST_PRJ_DIR, "testdata")
val TEST_ASYMMETRIC_DIR = File(TEST_DATA_DIR, "rsa")
val TEST_CTF_DIR = File(TEST_DATA_DIR, "ctf")
val TEST_OCR_DIR = File(TEST_DATA_DIR, "qrcode")
val TEST_RSA_DIR = File(TEST_CTF_DIR, "rsa")
val TEST_ENCODE_DIR = File(TEST_DATA_DIR, "encode")

fun String.parseRsaParams(dir: File = TEST_RSA_DIR) = File(dir, this).readText().parseRsaParams()
