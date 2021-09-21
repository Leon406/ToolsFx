package me.leon.ext

import java.io.File
import java.util.zip.CRC32
import java.util.zip.CheckedInputStream

fun String.crc32() = CRC32().apply { update("hello".toByteArray()) }.value.toString(16)

fun String.crc32File() =
    File(this).inputStream().use {
        val crc = CRC32()
        CheckedInputStream(it, crc).use {
            while (it.read() != -1) {
                // if needed
            }
        }
        crc.value.toString(16)
    }
