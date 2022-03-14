package me.leon

import java.io.ByteArrayInputStream
import java.net.URLDecoder
import java.nio.charset.Charset
import java.security.cert.CertificateFactory
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.CRC32
import kotlin.system.measureNanoTime
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import me.leon.encode.base.*
import me.leon.ext.*
import org.junit.Test

class MyTest {

    @Test
    fun crc32Test() {
        CRC32().apply { update("hello".toByteArray()) }.value.also { println(it.toString(16)) }
    }

    @Test
    fun split() {
        val raw = "**a*a**a"
        raw.split("(?<!\\*)\\*a".toRegex()).also { println(it) }
    }

    @Test
    fun cerParse() {
        val cer =
            """
-----BEGIN CERTIFICATE-----
MIIDozCCAougAwIBAgIUcJ/dZjcbIUkiThqJPy4aX1o8McAwDQYJKoZIhvcNAQEL
BQAwYTELMAkGA1UEBhMCbGwxCzAJBgNVBAgMAmxsMQswCQYDVQQHDAJsbDELMAkG
A1UECgwCbGwxCzAJBgNVBAsMAmxsMQswCQYDVQQDDAJsbDERMA8GCSqGSIb3DQEJ
ARYCbGwwHhcNMjEwNzMwMDUxMDM3WhcNMjIwNzMwMDUxMDM3WjBhMQswCQYDVQQG
EwJsbDELMAkGA1UECAwCbGwxCzAJBgNVBAcMAmxsMQswCQYDVQQKDAJsbDELMAkG
A1UECwwCbGwxCzAJBgNVBAMMAmxsMREwDwYJKoZIhvcNAQkBFgJsbDCCASIwDQYJ
KoZIhvcNAQEBBQADggEPADCCAQoCggEBAOGio25Fonh9a7DUfGCMAsDTpZMPMVfD
XVTcf+2TMk27yRwWhZC5AN5vAGeqnv+2YB4ABqIDK93DtM5NjEMxAxer03YIjWPI
AZF30TJ1qqI5mb3AxhSpoMZYbp99OSq2WsV16X+Ah7AD9iKK4iipy535Z8oBTIxo
gpBjRO1YV9nT7BYAT1bcZ7agmEBRp2mzP/HZoFP57Q+7bYmKlT3evA5hle3DK/s5
UjVPwJemN9qFU5Twt6oD7/SS1v0AJflH7CSORW4tdsO+86QsPbcF2b5eRRiQEdpw
QKwB1XpPtQGIoW9feYgMyj/WwOf2onsgXunn1V3CmjlJMraHKoM3cocCAwEAAaNT
MFEwHQYDVR0OBBYEFPJXEaj8fuipXK2qjKvF94bW24ToMB8GA1UdIwQYMBaAFPJX
Eaj8fuipXK2qjKvF94bW24ToMA8GA1UdEwEB/wQFMAMBAf8wDQYJKoZIhvcNAQEL
BQADggEBAKq6tNSiy05IrOAAVsOpxL6ushrp2h/0vs1wPJ6bUBPI1ZPut4/S7U3j
mKiz5y7CqKDwK9zOwObgDeZGYpuAZG/406nVU33RCapxpF8J89J36y07bj7ocdRL
KN6fjOazrIW/uMDJUqlu/oEgW18x+I5WzafD6hczQ0JXiJDu3ulFIx8k2tZK45/5
UBrNu3OSCOshem+wyxqwcGmhEkXAMBqljzTju0vSqPYPAoGJCHeL8yY5QMXtZSEw
wHvjyxY5RSjtz74Pc/0YVE6KZTTzNzjO1qm+kH6sGNo0L1eBDOL7q67t0A6i9EEe
r9VfvQb3rJybNjUcimJT7PWSwABwHdE=
-----END CERTIFICATE-----
        """.trimIndent()

        println(cer)
        val byteArrayInputStream = ByteArrayInputStream(cer.toByteArray())

        val cert = CertificateFactory.getInstance("X.509").generateCertificate(byteArrayInputStream)
        println(cert.publicKey.encoded.base64())
    }

    @Test
    fun exceptionTest() {
        println(NullPointerException().stacktrace())
    }

    @Test
    fun decodeUnicode() {
        val u = "&#20320;&#22909;&#20013;&#22269;&#x4e2d;&#x56fd;&#X56FD;"
        println(u.unicode2String())
        assertEquals("🗻", "🗻".toUnicodeString().unicode2String())
        assertEquals("🗻", "🗻".toUnicodeString().unicode2String())

        assertContentEquals(
            arrayOf("🗾", "🗾"),
            arrayOf("&#128510;".unicode2String(), "128510".toInt().toUnicodeChar())
        )

        assertContentEquals(
            arrayOf(128510, 128507),
            arrayOf("\uD83D\uDDFE".unicodeCharToInt(), "🗻".unicodeCharToInt())
        )
        println("🗾".unicodeCharToInt())
    }

    @Test
    fun hex2Base64() {
        val data = "e4bda0e5a5bd4c656f6e21"
        data.hex2ByteArray().base64().also { println(it) }
    }

    @Test
    fun baseNEncode() {

        println("ABDdd东方丽景的猜测1#".base58())
        println("fvw2PFXr4yWZgdRoBp5UptcJeAW1c46YobuRaA".base58Decode2String())

        measureNanoTime {
            "ABDdd东方丽景的猜测1#".toByteArray().baseCheck().also {
                println("dddd " + String(it.baseCheckDecode()))
            }
        }
            .also { println("total $it") }

        measureNanoTime {
            "ABDdd东方丽景的猜测1#".base58Check().also {
                println("dddd2 " + it.base58CheckDecode2String())
            }
        }
            .also { println("total2 $it") }
    }

    @Test
    fun urlDecodeTest() {
        val raw =
            "https://subcon.dlj.tf/sub?target=clash&new_name=true&url=" +
                "ss://YWVzLTI1Ni1nY206NTRhYTk4NDYtN2YzMS00MzdmLTgxNjItOGNiMzc1" +
                "MjBiNTRlQGd6bS5taXNha2EucmVzdDoxMTQ1MQ==#%E9%A6%99%E6%B8%AF%E" +
                "F%BC%9ATG%E5%AE%98%E7%BD%91%40freeyule|ss://YWVzLTI1Ni1nY206NTRhY" +
                "Tk4NDYtN2YzMS00MzdmLTgxNjItOGNiMzc1MjBiNTRlQGd6bS5taXNha2EucmVzdDoxM" +
                "TQ1Mg==#%E6%97%A5%E6%9C%AC%EF%BC%9ATG%E5%AE%98%E7%BD%91%40freeyule&inse" +
                "rt=false&config=https://raw.githubusercontent.com/ACL4SSR/ACL4SSR/mas" +
                "er/Clash/config/ACL4SSR_Online.ini"

        URLDecoder.decode(raw).also { println(it) }
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
    fun sig() {
        SignatureDemo.sigTest()
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
}
