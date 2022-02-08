package me.leon

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.readBytesFromNet
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
import org.junit.Test

class StringCompression {

    private val data = "data to compress数据"

    @Test
    fun bzip2() {
        compress(data) { input, output ->
            BZip2CompressorOutputStream(output).use { input.copyTo(it) }
        }
        val compressed =
            "QlpoOTFBWSZTWYkjk9AAAAKR+UAALgLcAAACAgAAAUAAAQAgADFMABNCgbUP1JporjZgTa4DuTSPSvi7kinChIRJHJ6A"
        unCompress(compressed.base64Decode()) { input, output ->
            BZip2CompressorInputStream(input).use { it.copyTo(output) }
        }
    }

    @Test
    fun deflate() {
        compress(data) { input, output ->
            DeflateCompressorOutputStream(output).use { input.copyTo(it) }
        }
        val compressed = "eJxLSSxJVCjJV0jOzy0oSi0ufjZ1w7PedQBm6Ap2"
        unCompress(compressed.base64Decode()) { input, output ->
            DeflateCompressorInputStream(input).use { it.copyTo(output) }
        }
    }

    @Test
    fun brFromNet() {
        // implementation("org.brotli:dec:0.1.2")
        "https://dss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/js/components/tips-e2ceadd14d.js"
            .readBytesFromNet(headers = mutableMapOf("Accept-Encoding" to "br"))
            .also {
                unCompress(it) { input, output ->
                    BrotliCompressorInputStream(input).use { it.copyTo(output) }
                }
            }
    }

    @Test
    fun gzip() {
        compress(data) { input, output ->
            GzipCompressorOutputStream(output).use { input.copyTo(it) }
        }
        val compressed = "H4sIAAAAAAAA/0tJLElUKMlXSM7PLShKLS5+NnXDs951AGDaLV4WAAAA"
        unCompress(compressed.base64Decode()) { input, output ->
            GzipCompressorInputStream(input).use { it.copyTo(output) }
        }
    }

    @Test
    fun gzipFromNet() {
        "https://dss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/js/components/tips-e2ceadd14d.js"
            .readBytesFromNet(headers = mutableMapOf("Accept-Encoding" to "gzip"))
            .also {
                unCompress(it) { input, output ->
                    GzipCompressorInputStream(input).use { it.copyTo(output) }
                }
            }
    }

    @Test
    fun xz() {
        // implementation("org.tukaani:xz:1.9")
        compress(data) { input, output ->
            XZCompressorOutputStream(output).use { input.copyTo(it) }
        }
        val compressed =
            "/Td6WFoAAATm1rRGAgAhARYAAAB0L+WjAQAVZGF0YSB0byBjb21wcmVzc+aVs" +
                "OaNrgAAAM0b7MKuQu2PAAEuFlYJVd8ftvN9AQAAAAAEWVo="
        unCompress(compressed.base64Decode()) { input, output ->
            XZCompressorInputStream(input).use { it.copyTo(output) }
        }
    }

    @Test
    fun zStandard() {
        // implementation("com.github.luben:zstd-jni:1.5.2-1")
        compress(data) { input, output ->
            ZstdCompressorOutputStream(output).use { input.copyTo(it) }
        }
        val compressed = "KLUv/QBYsQAAZGF0YSB0byBjb21wcmVzc+aVsOaNrg=="
        unCompress(compressed.base64Decode()) { input, output ->
            ZstdCompressorInputStream(input).use { it.copyTo(output) }
        }
    }

    @Test
    fun snappy() {
        compress(data) { input, output ->
            FramedSnappyCompressorOutputStream(output).use { input.copyTo(it) }
        }
        val compressed = "/wYAAHNOYVBwWQAcAACIf/9gFlRkYXRhIHRvIGNvbXByZXNz5pWw5o2u"
        unCompress(compressed.base64Decode()) { input, output ->
            FramedSnappyCompressorInputStream(input).use { it.copyTo(output) }
        }
    }

    @Test
    fun lzma() {
        // implementation("org.tukaani:xz:1.9")
        compress(data) { input, output ->
            LZMACompressorOutputStream(output).use { input.copyTo(it) }
        }
        val compressed = "XQAAgAD//////////wAyGEruliUvaye4s+pxuxCFrdbTgDy9SyWhsEH/896AAA=="
        unCompress(compressed.base64Decode()) { input, output ->
            LZMACompressorInputStream(input).use { it.copyTo(output) }
        }
    }

    @Test
    fun lz4() {
        // implementation("org.tukaani:xz:1.9")
        compress(data) { input, output ->
            FramedLZ4CompressorOutputStream(output).use { input.copyTo(it) }
        }
        val compressed = "BCJNGGRwuRYAAIBkYXRhIHRvIGNvbXByZXNz5pWw5o2uAAAAAJ2yK5w="

        unCompress(compressed.base64Decode()) { input, output ->
            FramedLZ4CompressorInputStream(input).use { it.copyTo(output) }
        }
    }

    @Test
    fun lzString() {
        LZ4K.compressToBase64(data).also { println(it) }
        val compressed = "CYQwLiAEYPaQxjAtgBwE4FMDOXAOpoO2Mg=="
        LZ4K.decompressFromBase64(compressed).also { println(it) }
    }

    private fun compress(
        data: String,
        action: (ByteArrayInputStream, ByteArrayOutputStream) -> Unit
    ) {
        ByteArrayInputStream(data.toByteArray())
            .use { input ->
                val outputStream = ByteArrayOutputStream()
                action(input, outputStream)
                outputStream.toByteArray().base64()
            }
            .also { println(it) }
    }

    private fun unCompress(
        compressed: ByteArray,
        action: (ByteArrayInputStream, ByteArrayOutputStream) -> Unit
    ) {
        ByteArrayInputStream(compressed)
            .use { input ->
                val outputStream = ByteArrayOutputStream()
                action(input, outputStream)
                outputStream.toString()
            }
            .also { println(it) }
    }
}
