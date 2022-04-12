package me.leon

import java.io.File
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import me.leon.encode.base.base64
import me.leon.ext.*
import me.leon.ext.crypto.parsePublicKeyFromCerFile
import org.junit.Test

class MyTest {

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
    fun exceptionTest() {
        println(NullPointerException().stacktrace())
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
        val gbkBytes = r.toByteArray(Charset.forName("gb2312"))
        val big5Bytes = r.toByteArray(Charset.forName("BIG5"))
        val iso88591 = r.toByteArray(Charset.forName("ISO8859-1"))
        uft8Bytes.contentToString().also { println(it) }
        println(gbkBytes.charsetChange("gbk", "utf-8").contentToString())
        println(big5Bytes.charsetChange("BIG5", "utf-8").contentToString())
        println(iso88591.charsetChange("ISO8859-1", "utf-8").contentToString())
        gbkBytes.contentToString().also { println(it) }
        big5Bytes.contentToString().also { println(it) }
        iso88591.contentToString().also { println(it) }

        String(iso88591).also { println(it) }
        String(uft8Bytes).also { println(it) }
        uft8Bytes.toString(Charset.forName("gb2312")).also { println(it) }
        gbkBytes.toString(Charset.forName("gbk")).also { println(it) }
        big5Bytes.toString(Charset.forName("big5")).also { println(it) }
        iso88591.toString(Charset.forName("utf-8")).also { println(it) }
    }

    @Test
    fun collectionSpit() {
        val l = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        l.sliceList(mutableListOf(1, 2, 3, 4)).also { println(it) }
        val l2 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        l2.sliceList(mutableListOf(4, 2, 3, 1)).also { println(it) }
    }

    @Test
    fun sss() {
        val map =
            mapOf(
                0 to arrayOf('目', '口', '凹', '凸', '田'),
                1 to arrayOf('由'),
                2 to arrayOf('中'),
                3 to arrayOf('人', '入', '古'),
                4 to arrayOf('工', '互'),
                5 to arrayOf('果', '克', '尔', '土', '大'),
                6 to arrayOf('木', '王'),
                7 to arrayOf('夫', '主'),
                8 to arrayOf('井', '关', '丰', '并'),
                9 to arrayOf('圭', '羊'),
            )

        map.values.zip(map.keys).flatMap { (array, key) -> array.map { it to key } }.toMap().also {
            println(it)
        }
    }

    @Test
    fun updateJsonParse() {
        File("${TEST_PRJ_DIR.absolutePath}/update.json").readText().fromJson(Map::class.java).also {
            println(it["info"])
        }
    }
}
