package me.leon.misc

import java.text.SimpleDateFormat
import java.util.Date
import me.leon.ext.*
import me.leon.misc.net.*

val SDF_TIME = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
val SDF_DATE = SimpleDateFormat("yyyy-MM-dd")
val SDF_DATE2 = SimpleDateFormat("yyyyMMdd")
private const val NOW = "NOW"
val ALL_PORTS = (1..65_535).toList()

enum class MiscServiceType(val type: String) : MiscService {
    UUID("uuid") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            (0 until runCatching { raw.toInt() }.getOrDefault(1)).joinToString(
                System.lineSeparator()
            ) {
                java.util.UUID.randomUUID().toString()
            }
    },
    TIME_STAMP("timestamp(ms)") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.lineAction { SDF_TIME.format(Date(it.toLong())) }
                .joinToString(System.lineSeparator())
    },
    TIME_STAMP_SECOND("timestamp(s)") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.lineAction { SDF_TIME.format(Date(it.toLong() * 1000)) }
                .joinToString(System.lineSeparator())
    },
    DATE2STAMP("date2stamp") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.lineAction {
                    if (NOW.equals(it, true)) {
                        System.currentTimeMillis()
                    } else {
                        when (it.length) {
                            10 -> {
                                SDF_DATE.parse(it.replace("/", "-")).time
                            }
                            8 -> {
                                SDF_DATE2.parse(it).time
                            }
                            else -> {
                                SDF_TIME.parse(it.replace("/", "-")).time
                            }
                        }
                    }
                }
                .joinToString(System.lineSeparator())
    },
    PORT_SCAN("port scan") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.portScan().joinToString(System.lineSeparator())
    },
    PORT_SCAN_FULL("full port scan") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.portScan(ALL_PORTS).joinToString(System.lineSeparator())
    },
    IP_SCAN("ip scan") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.lanScan().joinToString(System.lineSeparator())
    },
    BATCH_PING("ping") {
        override fun process(raw: String, params: MutableMap<String, String>) = raw.batchPing()
    },
    BATCH_TCPING("tcping") {
        override fun process(raw: String, params: MutableMap<String, String>) = raw.batchTcPing()
    },
    WHOIS("whois(online)") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            runCatching { Whois.parse(raw)?.showInfo ?: raw.whoisSocket() }
                .getOrElse { it.stacktrace() }
    },
    ICP("ICP备案(online)") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.lineAction2String {
                "$it:\n\n" +
                    runCatching { MiitInfo.domainInfo(it).showInfo }.getOrElse { it.stacktrace() } +
                    "\n"
            }
    },
    IP2INT("ip2Int") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.lineAction { runCatching { it.ip2Uint().toString() }.getOrElse { it.stacktrace() } }
                .joinToString(System.lineSeparator())
    },
    INT2IP("int2Ip") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.lineAction { runCatching { it.toUInt().toIp() }.getOrElse { it.stacktrace() } }
                .joinToString(System.lineSeparator())
    },
    CIDR("CIDR") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.lineAction { runCatching { it.cidr() }.getOrElse { it.stacktrace() } }
                .joinToString(System.lineSeparator())
    },
    IP_LOCATION("ip location(online)") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            raw.lineAction { runCatching { it.ipLocation() }.getOrElse { it.stacktrace() } }
                .joinToString(System.lineSeparator())
    };

    override fun hint(): String {
        return HINTS[this].orEmpty()
    }
}

val HINTS =
    mapOf(
        MiscServiceType.UUID to "generate count",
        MiscServiceType.TIME_STAMP to "timestamp in milliseconds,separate by line",
        MiscServiceType.TIME_STAMP_SECOND to "timestamp in seconds,separate by line",
        MiscServiceType.DATE2STAMP to
            "date, support format " +
                "like 2023-02-01 12:00:00, 2023-02-01, 2023/02/01, 20230201,separate by line",
        MiscServiceType.PORT_SCAN to "ip or domain (port from 1 to 10000)",
        MiscServiceType.PORT_SCAN_FULL to "ip or domain ( port from 1 to 65535)",
        MiscServiceType.IP_SCAN to "ip w/o last dot,like 192.168.0",
        MiscServiceType.BATCH_PING to "ping ip or domains,separate by line",
        MiscServiceType.BATCH_TCPING to "tcp ping ip or domains,separate by line",
        MiscServiceType.WHOIS to "domain,separate by line",
        MiscServiceType.ICP to "domain, 工信部备案信息,separate by line",
        MiscServiceType.IP2INT to "ip, transform ip to integer, eg. 192.168.0.1,separate by line",
        MiscServiceType.INT2IP to "int, transform integer to ip,  eg. 3232235521,separate by line",
        MiscServiceType.CIDR to "ip, format 192.168.0.1/25,separate by line",
        MiscServiceType.IP_LOCATION to "ip/url",
    )

val miscServiceTypeMap = MiscServiceType.values().associateBy { it.type }

fun String.miscServiceType() = miscServiceTypeMap[this] ?: MiscServiceType.UUID
