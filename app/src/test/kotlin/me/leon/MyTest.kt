package me.leon

import java.io.ByteArrayInputStream
import java.io.File
import java.security.Security
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import me.leon.ext.crypto.parsePublicKeyFromCerFile
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test

class MyTest {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    @Test
    fun cerParse() {
        File(TEST_DATA_DIR, "rsa/public.cer").parsePublicKeyFromCerFile().also {
            println(it.base64())
        }
        File(TEST_DATA_DIR, "rsa/pub_cer_2048.pem").parsePublicKeyFromCerFile().also {
            println(it.base64())
        }
    }

    @Test
    fun localDate() {

        val now = LocalDateTime.now()
        println(now)
        LocalDateTime.parse(
                "2020-10-11 10:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            )
            .toInstant(ZoneOffset.of("+8"))
            .toEpochMilli()
            .also { println(it) }

        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss")).also {
            println(it)
        }
    }

    @Test
    fun charset() {
        val r = "中国China 666"
        val uft8Bytes = r.toByteArray()
        val gbkBytes = r.toByteArray(GBK)
        val big5Bytes = r.toByteArray(BIG5)
        val iso88591 = r.toByteArray(Charsets.ISO_8859_1)
        uft8Bytes.contentToString().also { println(it) }
        println(gbkBytes.charsetChange("gbk", "utf-8").contentToString())
        println(big5Bytes.charsetChange("BIG5", "utf-8").contentToString())
        println(iso88591.charsetChange("ISO8859-1", "utf-8").contentToString())
        gbkBytes.contentToString().also { println(it) }
        big5Bytes.contentToString().also { println(it) }
        iso88591.contentToString().also { println(it) }

        String(iso88591).also { println(it) }
        String(uft8Bytes).also { println(it) }
        uft8Bytes.toString(GBK).also { println(it) }
        gbkBytes.toString(GBK).also { println(it) }
        big5Bytes.toString(BIG5).also { println(it) }
        iso88591.toString(Charsets.UTF_8).also { println(it) }
    }

    @Test
    fun collectionSpit() {
        val l = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        l.sliceList(mutableListOf(1, 2, 3, 4)).also { println(it) }
        val l2 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        l2.sliceList(mutableListOf(4, 2, 3, 1)).also { println(it) }
    }

    @Test
    fun updateJsonParse() {
        println('你'.code)
        println('你'.code / 256)
        println('你'.code % 256)
        //  "79, 96"
        println("你".toByteArray(Charsets.UTF_16BE).contentToString())
        println("你".toByteArray(Charsets.UTF_16LE).contentToString())

        println(File.separatorChar)
        File("${TEST_PRJ_DIR.absolutePath}/update.json").readText().fromJson(Map::class.java).also {
            println(it["info"])
        }
    }

    @Test
    fun zip() {
        val data =
            "UEsDBBQAAAAIAAldCFXqOw7cKAAAACYAAAAIAAAAZmxhZy50eHRLy0lMrzZISk02SEwxTkk0MjQ0TjY3SDU1SEsxNTM0T7JI" +
                "NU+zrAUAUEsBAhQAFAAAAAgACV0IVeo7DtwoAAAAJgAAAAgAJAAAAAAAAAAgAAAAAAAAAGZsYWcudHh0CgAgAAAA" +
                "AAABABgAGxEfk9iq2AEbER+T2KrYAQJF+4rYqtgBUEsFBgAAAAABAAEAWgAAAE4AAAAAAA"

        ZipInputStream(ByteArrayInputStream(data.base64Decode())).run {
            var entry: ZipEntry? = null
            while (nextEntry?.also { entry = it } != null) {
                println(entry)
                println(this.readBytes().decodeToString())
            }
        }
    }
}
