package me.leon.ext.crypto

import java.io.File
import java.util.zip.*

fun ByteArray.crc32() =
    CRC32()
        .apply { update(this@crc32, 0, this@crc32.size) }
        .value
        .toULong()
        .toString(16)
        .padStart(8, '0')

fun ByteArray.adler32() =
    with(
            Adler32()
                .apply { update(this@adler32, 0, this@adler32.size) }
                .value
                .toULong()
                .toString(16)
        ) {
            if (length % 2 == 0) this else "0$this"
        }
        .padStart(8, '0')

fun String.adler32File() =
    File(this).inputStream().use {
        val crc = Adler32()
        CheckedInputStream(it, crc).use {
            while (it.read() != -1) {
                // if needed
            }
        }
        with(crc.value.toULong().toString(16)) { if (length % 2 == 0) this else "0$this" }
    }
