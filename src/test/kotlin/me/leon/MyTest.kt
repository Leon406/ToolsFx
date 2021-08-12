package me.leon

import java.io.ByteArrayInputStream
import java.lang.StringBuilder
import java.math.BigInteger
import java.security.cert.CertificateFactory
import java.util.zip.CRC32
import me.leon.base.base64
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
    }

    @Test
    fun hex2Base64() {
        val data = "e4bda0e5a5bd4c656f6e21"
        data.hex2ByteArray().base64().also { println(it) }
    }

    fun String.radix(radix: Int, maps: String = map): String {
        var bigInteger = BigInteger(this.toByteArray())
        var remainder: Int = -1
        val sb = StringBuilder()
        while (bigInteger != BigInteger.ZERO) {
            bigInteger.divideAndRemainder(radix.toBigInteger()).run {
                bigInteger = this[0]
                remainder = this[1].toInt()
            }
            sb.append(maps[remainder])
        }
        return sb.reversed().toString()
    }

    fun ByteArray.radix(radix: Int, maps: String = map): String {
        var bigInteger = BigInteger(this)
        var remainder: Int = -1
        val sb = StringBuilder()
        while (bigInteger != BigInteger.ZERO) {
            bigInteger.divideAndRemainder(radix.toBigInteger()).run {
                bigInteger = this[0]
                remainder = this[1].toInt()
            }
            sb.append(maps[remainder])
        }
        return sb.reversed().toString()
    }

    private val map = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    fun String.radix2(radix: Int, maps: String = map) =
        String(
            toCharArray()
                .mapIndexed { index, c -> length - index - 1 to maps.indexOf(c) }
                .fold(0.toBigInteger()) { acc, pair ->
                    acc.add(
                        pair.second.toBigInteger().multiply(radix.toBigInteger().pow(pair.first))
                    )
                }
                .toByteArray()
        )

    @Test
    fun radix() {
        "ABDdd东方丽景的猜测1#".radix(58).also {
            println(it)
            println(it.radix2(58))
        }

        println(base58Check("ABD"))
        println(Base58Check.encode("ABD".toByteArray()))
    }

    private fun base58Check(plain: String): String {
        val hash = Digests.hash("SHA-256", Digests.hash("SHA-256", plain.toByteArray()))
        return (plain.toByteArray() + hash.copyOfRange(0, 4)).radix(58)
    }
}
