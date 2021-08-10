package me.leon

import java.io.ByteArrayInputStream
import java.lang.NullPointerException
import java.security.cert.CertificateFactory
import java.util.zip.CRC32
import me.leon.base.base64
import me.leon.ext.stacktrace
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
        val u = "&#20320;&#22909;"
        "&#(\\d+);".toRegex().findAll(u)
            .map {it.groupValues[1]  }
            .fold(StringBuilder()) { acc, c -> acc.apply { append(c.toInt(10).toChar()) } }
            .toString()
            .also { println(it) }

    }
}
