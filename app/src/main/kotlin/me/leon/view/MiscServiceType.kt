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
}

val serviceTypeMap = MiscServiceType.values().associateBy { it.type }

fun String.locationServiceType() = serviceTypeMap[this] ?: MiscServiceType.UUID
