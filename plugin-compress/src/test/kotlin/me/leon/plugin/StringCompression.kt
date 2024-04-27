package me.leon.plugin

import kotlin.test.assertEquals
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.readBytesFromNet
import me.leon.toolsfx.plugin.compress.Compression
import me.leon.toolsfx.plugin.compress.LzString
import org.junit.Test

class StringCompression {

    private val data = "data to compress数据 123wdfd123d"

    @Test
    fun bzip2() {
        val compressed = Compression.Bzip.compress(data.toByteArray()).base64().also { println(it) }
        val decompressed =
            Compression.Bzip.decompress(compressed.base64Decode()).decodeToString().also {
                println(it)
            }
        assertEquals(data, decompressed)
    }

    @Test
    fun deflate() {
        val compressed =
            Compression.DEFLATE.compress(data.toByteArray()).base64().also { println(it) }
        val decompressed =
            Compression.DEFLATE.decompress(compressed.base64Decode()).decodeToString().also {
                println(it)
            }
        assertEquals(data, decompressed)
    }

    @Test
    fun brFromNet() {
        // implementation("org.brotli:dec:0.1.2")
        "https://dss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/js/components/tips-e2ceadd14d.js"
            .readBytesFromNet(headers = mapOf("Accept-Encoding" to "br"))
            .also { println(it.base64()) }
            .also { Compression.BR.decompress(it).decodeToString().also { println(it) } }
    }

    @Test
    fun gzip() {
        val compressed = Compression.GZIP.compress(data.toByteArray()).base64().also { println(it) }
        val decompressed =
            Compression.GZIP.decompress(compressed.base64Decode()).decodeToString().also {
                println(it)
            }
        assertEquals(data, decompressed)
    }

    @Test
    fun gzipFromNet() {
        "https://dss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/js/components/tips-e2ceadd14d.js"
            .readBytesFromNet(headers = mapOf("Accept-Encoding" to "gzip"))
            .also { Compression.GZIP.decompress(it).decodeToString().also { println(it) } }
    }

    @Test
    fun xz() {
        // implementation("org.tukaani:xz:1.9")
        val compressed = Compression.XZ.compress(data.toByteArray()).base64().also { println(it) }
        val decompressed =
            Compression.XZ.decompress(compressed.base64Decode()).decodeToString().also {
                println(it)
            }
        assertEquals(data, decompressed)
    }

    @Test
    fun zStandard() {
        // implementation("com.github.luben:zstd-jni:1.5.2-1")

        val compressed =
            Compression.ZStandard.compress(data.toByteArray()).base64().also { println(it) }
        val decompressed =
            Compression.ZStandard.decompress(compressed.base64Decode()).decodeToString().also {
                println(it)
            }
        assertEquals(data, decompressed)
    }

    @Test
    fun snappy() {
        val compressed =
            Compression.Snappy.compress(data.toByteArray()).base64().also { println(it) }
        val decompressed =
            Compression.Snappy.decompress(compressed.base64Decode()).decodeToString().also {
                println(it)
            }
        assertEquals(data, decompressed)
    }

    @Test
    fun lzma() {
        // implementation("org.tukaani:xz:1.9")
        val compressed = Compression.LZMA.compress(data.toByteArray()).base64().also { println(it) }
        val decompressed =
            Compression.LZMA.decompress(compressed.base64Decode()).decodeToString().also {
                println(it)
            }
        assertEquals(data, decompressed)
    }

    @Test
    fun lz4() {
        // implementation("org.tukaani:xz:1.9")
        val compressed = Compression.LZ4.compress(data.toByteArray()).base64().also { println(it) }
        val decompressed =
            Compression.LZ4.decompress(compressed.base64Decode()).decodeToString().also {
                println(it)
            }
        assertEquals(data, decompressed)
    }

    @Test
    fun lzString() {
        val compressed =
            Compression.LZString.compress(data.toByteArray()).base64().also { println(it) }
        val decompressed =
            Compression.LZString.decompress(compressed.base64Decode()).decodeToString().also {
                println(it)
            }
        assertEquals(data, decompressed)

        val bytes =
            "IIEwbghgdgxgpiABASyiOUAuiDmBXZdAZ0QBtU5EALZHK8uzVHRGaRAI0owg9IUQAzAPYAnIA==="
                .base64Decode()
        assertEquals(
            "Advanced indent guides line highlighting can be enabled for",
            Compression.LZString.decompress(bytes).decodeToString()
        )

        println(LzString.compressToBase64("ba"))
    }
}
