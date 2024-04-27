package me.leon.toolsfx.plugin.compress

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream

enum class Compression(val alg: String) : ICompress {
    Bzip("bzip") {
        override fun compress(bytes: ByteArray) =
            compress(bytes) { input, output ->
                BZip2CompressorOutputStream(output).use { input.copyTo(it) }
            }

        override fun decompress(bytes: ByteArray) =
            decompress(bytes) { input, output ->
                BZip2CompressorInputStream(input).use { it.copyTo(output) }
            }
    },
    DEFLATE("deflate(zip)") {
        override fun compress(bytes: ByteArray) =
            compress(bytes) { input, output ->
                DeflateCompressorOutputStream(output).use { input.copyTo(it) }
            }

        override fun decompress(bytes: ByteArray) =
            decompress(bytes) { input, output ->
                DeflateCompressorInputStream(input).use { it.copyTo(output) }
            }
    },
    BR("br") {
        // 三方库不支持压缩
        override fun compress(bytes: ByteArray) = TODO()

        override fun decompress(bytes: ByteArray) =
            decompress(bytes) { input, output ->
                BrotliCompressorInputStream(input).use { it.copyTo(output) }
            }
    },
    GZIP("gzip") {
        override fun compress(bytes: ByteArray) =
            compress(bytes) { input, output ->
                GzipCompressorOutputStream(output).use { input.copyTo(it) }
            }

        override fun decompress(bytes: ByteArray) =
            decompress(bytes) { input, output ->
                GzipCompressorInputStream(input).use { it.copyTo(output) }
            }
    },
    XZ("xz") {
        override fun compress(bytes: ByteArray) =
            compress(bytes) { input, output ->
                XZCompressorOutputStream(output).use { input.copyTo(it) }
            }

        override fun decompress(bytes: ByteArray) =
            decompress(bytes) { input, output ->
                XZCompressorInputStream(input).use { it.copyTo(output) }
            }
    },
    ZStandard("zstd") {
        override fun compress(bytes: ByteArray) =
            compress(bytes) { input, output ->
                ZstdCompressorOutputStream(output).use { input.copyTo(it) }
            }

        override fun decompress(bytes: ByteArray) =
            decompress(bytes) { input, output ->
                ZstdCompressorInputStream(input).use { it.copyTo(output) }
            }
    },
    Snappy("snappy") {
        override fun compress(bytes: ByteArray) =
            compress(bytes) { input, output ->
                FramedSnappyCompressorOutputStream(output).use { input.copyTo(it) }
            }

        override fun decompress(bytes: ByteArray) =
            decompress(bytes) { input, output ->
                FramedSnappyCompressorInputStream(input).use { it.copyTo(output) }
            }
    },
    LZMA("lzma") {
        override fun compress(bytes: ByteArray) =
            compress(bytes) { input, output ->
                LZMACompressorOutputStream(output).use { input.copyTo(it) }
            }

        override fun decompress(bytes: ByteArray) =
            decompress(bytes) { input, output ->
                LZMACompressorInputStream(input).use { it.copyTo(output) }
            }
    },
    LZ4("lz4") {
        override fun compress(bytes: ByteArray) =
            compress(bytes) { input, output ->
                FramedLZ4CompressorOutputStream(output).use { input.copyTo(it) }
            }

        override fun decompress(bytes: ByteArray) =
            decompress(bytes) { input, output ->
                FramedLZ4CompressorInputStream(input).use { it.copyTo(output) }
            }
    },
    LZString("lzString") {
        override fun compress(bytes: ByteArray) =
            LzString.compressToBase64(bytes.decodeToString()).base64Decode()

        override fun decompress(bytes: ByteArray) =
            LzString.decompressFromBase64(
                    bytes.base64().let {
                        // 补充额外 'A===' padding
                        if (it.endsWith("=")) {
                            it
                        } else {
                            it + "A==="
                        }
                    }
                )
                ?.toByteArray()
                ?: byteArrayOf()
    }
}

private fun compress(
    data: ByteArray,
    action: (ByteArrayInputStream, ByteArrayOutputStream) -> Unit
): ByteArray =
    ByteArrayInputStream(data).use { input ->
        val outputStream = ByteArrayOutputStream()
        action(input, outputStream)
        outputStream.toByteArray()
    }

private fun decompress(
    compressed: ByteArray,
    action: (ByteArrayInputStream, ByteArrayOutputStream) -> Unit
): ByteArray =
    ByteArrayInputStream(compressed).use { input ->
        val outputStream = ByteArrayOutputStream()
        action(input, outputStream)
        outputStream.toByteArray()
    }

val compressTypeMap = Compression.values().sortedBy { it.alg.lowercase() }.associateBy { it.alg }

fun String.compressType() = compressTypeMap[this] ?: Compression.GZIP
