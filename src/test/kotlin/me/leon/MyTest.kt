package me.leon

import java.io.ByteArrayInputStream
import java.net.URLDecoder
import java.security.cert.CertificateFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.CRC32
import kotlin.system.measureNanoTime
import me.leon.encode.base.*
import me.leon.ext.hex2ByteArray
import me.leon.ext.stacktrace
import me.leon.ext.unicode2String
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
        measureNanoTime { println(Base58Check.encode("ABDdd东方丽景的猜测1#".toByteArray())) }.also {
            println(it)
        }
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

        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss")).also {
            println(it)
        }
    }

    @Test
    fun sig() {
        SignatureDemo.sigTest()
    }
}
