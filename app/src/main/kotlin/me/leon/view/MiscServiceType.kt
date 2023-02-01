package me.leon.view

import java.text.SimpleDateFormat
import java.util.Date
import me.leon.ext.*

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
    )

val miscServiceTypeMap = MiscServiceType.values().associateBy { it.type }

fun String.miscServiceType() = miscServiceTypeMap[this] ?: MiscServiceType.UUID
