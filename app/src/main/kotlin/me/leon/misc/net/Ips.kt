package me.leon.misc.net

import kotlin.math.pow
import me.leon.ext.*

/**
 * @author Leon
 * @since 2023-02-27 14:05
 * @email deadogone@gmail.com
 */
fun String.ip2Uint() = split(".").fold(0U) { acc, s -> acc * 256U + s.toUInt() }

fun UInt.toIp() = "${shr(24)}.${shr(16) and 0xFFU}.${shr(8) and 0xFFU}.${this and 0xFFU}"

fun String.ip2Binary() =
    split(".")
        .fold(StringBuilder()) { acc, s -> acc.append(s.toUInt().toString(2).padStart(8, '0')) }
        .toString()

fun String.binary2Ip() = stripAllSpace().chunked(8).joinToString(".") { it.toUInt(2).toString() }

val IP_LOCATION =
    mapOf(
        // 0.0.0.0 - 0.255.255.255
        0U..16777215U to "Local Identification",
        // 10.0.0.0 - 10.255.255.255
        167772160U..184549375U to "local A",
        // 127.0.0.1 - 127.255.255.255
        2130706432U..2147483647U to "loopback",
        // 172.16.0.0 - 172.31.255.255
        2886729728U..2887778303U to "local B",
        // 192.168.0.0 - 192.168.255.255
        3232235520U..3232301055U to "local C",
        // 0.0.0.0 - 127.255.255.255
        0U..2147483647U to "Type A",
        // 128.0.0.0 - 191.255.255.255
        2147483648U..3221225471U to "Type B",
        // 192.0.0.0 - 223.255.255.255
        3221225472U..3758096383U to "Type C",
        // 224.0.0.0 - 239.255.255.255
        3758096384U..4026531839U to "MultiCast(D)",
        // 240.0.0.0 - 255.255.255.255
        4026531840U..4294967295U to "Reserved(E)",
    )

fun String.ipType(): String {
    val ipInt = ip2Uint()
    return IP_LOCATION.filterKeys { it.contains(ipInt) }.firstNotNullOfOrNull { it.value }
        ?: "Not Found"
}

fun Int.ipMaskBinary() = "1".repeat(this) + "0".repeat(32 - this)

fun Int.ipMask() = ipMaskBinary().binary2Ip()

fun String.cidr(): String {
    val (ipStr, cidrStr) = split("/").takeIf { it.size > 1 } ?: listOf(this, "24")
    val cidr = cidrStr.takeIf { it.isNotEmpty() }?.toInt() ?: 24
    val ip = ipStr.ip2Uint()
    val sub = 32 - cidr
    val count = 2.0.pow(sub).toInt()
    val mask = cidr.ipMask().ip2Uint()
    val net = mask and ip

    return StringBuilder()
        .append("${"ip count".center(12)}:\t${count - 2}")
        .appendLine()
        .append("${"mask".center(12)}:\t" + cidr.ipMask())
        .appendLine()
        .append("${"net".center(12)}:\t" + net.toIp())
        .appendLine()
        .append(
            "${"range".center(12)}:\t${(net + 1U).toIp()} - ${(net + (count - 2).toUInt()).toIp()}"
        )
        .appendLine()
        .append("${"broadcast".center(12)}:\t" + (net + (count - 1).toUInt()).toIp())
        .appendLine()
        .toString()
}

private const val IP_API = "http://ip-api.com/json/%s?lang=zh-CN"
private const val PCONLINE_API = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true"

fun String.ipLocation() =
    runCatching {
            PCONLINE_API.format(fastestIp(resolveDomainByAli(this))?.first)
                .readBytesFromNet()
                .toString(GBK)
                .fromJson(PcOnlineIp::class.java)
                .addr
        }
        .getOrElse { IP_API.format(this).readFromNet().fromJson(IpApi::class.java).info }
