package me.leon.misc

import java.text.SimpleDateFormat
import java.util.Date
import me.leon.C1
import me.leon.P1
import me.leon.ext.*
import me.leon.misc.net.*
import me.leon.misc.unit.unitConvert
import me.leon.misc.zhconvert.CONFIG
import me.leon.misc.zhconvert.convert

val SDF_TIME = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
val SDF_DATE = SimpleDateFormat("yyyy-MM-dd")
val SDF_DATE2 = SimpleDateFormat("yyyyMMdd")
private const val NOW = "NOW"

enum class MiscServiceType(val type: String) : MiscService {
    UUID("uuid") {
        override fun process(raw: String, params: Map<String, String>) =
            (0 until runCatching { raw.toInt() }.getOrDefault(1)).joinToString(
                System.lineSeparator()
            ) {
                val type = requireNotNull(params[C1])
                with(java.util.UUID.randomUUID().toString()) {
                    if (type == "normal") {
                        this
                    } else {
                        this.replace("-", "")
                    }
                }
            }
    },
    TIME_STAMP("stamp2date") {
        override fun process(raw: String, params: Map<String, String>): String {
            val factor =
                when (requireNotNull(params[C1])) {
                    "seconds" -> 1000L
                    "milliseconds" -> 1L
                    "minutes" -> 60_000L
                    "hours" -> 3_600_000L
                    "days" -> 86_400_000L
                    else -> 1L
                }
            return raw.lineAction { SDF_TIME.format(Date(it.toLong() * factor)) }
                .joinToString(System.lineSeparator())
        }
    },
    DATE2STAMP("date2stamp") {
        override fun process(raw: String, params: Map<String, String>): String {
            val format = requireNotNull(params[P1])
            val sdf =
                if (format.isNotEmpty()) {
                    SimpleDateFormat(format)
                } else {
                    null
                }
            return raw.lineAction {
                    if (NOW.equals(it, true)) {
                        System.currentTimeMillis()
                    } else {
                        sdf?.parse(it)?.time
                            ?: when (it.length) {
                                10 -> SDF_DATE.parse(it.replace("/", "-")).time
                                8 -> SDF_DATE2.parse(it).time
                                else -> SDF_TIME.parse(it.replace("/", "-")).time
                            }
                    }
                }
                .joinToString(System.lineSeparator())
        }
    },
    PORT_SCAN("port scan") {
        override fun process(raw: String, params: Map<String, String>) =
            with(requireNotNull(params[C1])) {
                val range = substringBefore("-").toInt()..substringAfter("-").toInt()
                raw.portScan(range.toList()).joinToString(System.lineSeparator()) {
                    "$it    ${CodeMapping.PORT_DICT[it.toString()].orEmpty()}"
                }
            }
    },
    IP_SCAN("ip scan") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.lanScan().joinToString(System.lineSeparator())
    },
    BATCH_PING("ping") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.batchPing(requireNotNull(params[C1]))
    },
    TCPING("tcping") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.batchTcPing(requireNotNull(params[C1]))
    },
    WHOIS("whois(online)") {
        override fun process(raw: String, params: Map<String, String>) =
            runCatching { Whois.parse(raw)?.showInfo ?: raw.whoisSocket() }
                .getOrElse { it.stacktrace() }
    },
    IP2INT("ip2Int") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.lineAction { runCatching { it.ip2Uint().toString() }.getOrElse { it.stacktrace() } }
                .joinToString(System.lineSeparator())
    },
    INT2IP("int2Ip") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.lineAction { runCatching { it.toUInt().toIp() }.getOrElse { it.stacktrace() } }
                .joinToString(System.lineSeparator())
    },
    CIDR("CIDR") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.lineAction { runCatching { it.cidr() }.getOrElse { it.stacktrace() } }
                .joinToString(System.lineSeparator())
    },
    IP_LOCATION("ip location(online)") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.lineAction { runCatching { it.ipLocation() }.getOrElse { it.stacktrace() } }
                .joinToString(System.lineSeparator())
    },
    DNS_SOLVE("DNS hosts") {
        override fun process(raw: String, params: Map<String, String>) =
            dnsSolve(raw.lines().filterNot { it.startsWith("#") || it.isEmpty() })
    },
    CRON_EXPLAIN("Cron Explain") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.lines()
                .filterNot { it.startsWith("#") || it.isEmpty() }
                .joinToString(System.lineSeparator()) { CronExpression(it).explain() }
    },
    LINK_CHECK("Link Check") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.linkCheck(3_000, type = requireNotNull(params[C1]))
    },
    GITHUB("Github") {
        override fun process(raw: String, params: Map<String, String>): String {
            val type = requireNotNull(params[C1])
            return raw.lineAction2String {
                runCatching { GithubAction.valueOf(type).convert(it) }.getOrElse { it.stacktrace() }
            }
        }
    },
    ENCODING_RECOVERY("recover encoding") {
        override fun process(raw: String, params: Map<String, String>) = raw.recoverEncoding()
    },
    FULL_WIDTH("half/full width") {
        override fun process(raw: String, params: Map<String, String>): String {
            val type = requireNotNull(params[C1])
            return if (type == "toFull") {
                raw.toFullWidth()
            } else {
                raw.toHalfWidth()
            }
        }
    },
    PUNCTUATION("中英文标点") {
        override fun process(raw: String, params: Map<String, String>): String {
            val type = requireNotNull(params[C1])
            return if (type == "EN") {
                raw.toEnPunctuation()
            } else {
                raw.toZhPunctuation()
            }
        }
    },
    ROMAN("roman number") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.lineAction2String { runCatching { it.roman() }.getOrElse { it.stacktrace() } }
    },
    ROMANJI("romanji(CJK)") {
        override fun process(raw: String, params: Map<String, String>) =
            raw.lineAction2String {
                runCatching { it.kawa(KawaType.valueOf(requireNotNull(params[C1]))).pretty() }
                    .getOrElse { it.stacktrace() }
            }
    },
    TRANSLATE("translate") {
        override fun process(raw: String, params: Map<String, String>): String {
            val type = requireNotNull(params[C1])
            return Translator.translate(raw, target = type)
        }
    },
    UNIT_CONVERT("unit conversion") {
        override fun process(raw: String, params: Map<String, String>): String {
            val type = requireNotNull(params[C1])
            return raw.lineAction2String { it.unitConvert(type) }
        }
    },
    TRADITION_CHINESE_CONVERT("繁简体转换") {
        override fun process(raw: String, params: Map<String, String>): String {
            val type = requireNotNull(params[C1])
            return CONFIG.first { it.name == type }.convert(raw)
        }
    },
    SHORT_URL("shorten url") {
        override fun process(raw: String, params: Map<String, String>): String {
            val type = requireNotNull(params[C1])
            return raw.lineAction2String {
                runCatching { it.shortUrl(type) }.getOrElse { it.stacktrace() }
            }
        }
    },
    CODE_EXPLAIN("code explain") {
        override fun process(raw: String, params: Map<String, String>): String {
            val type = requireNotNull(params[C1])
            return raw.lineAction2String {
                runCatching { CodeMapping.TYPE[type]!![it].orEmpty().ifEmpty { it } }
                    .getOrElse { it.stacktrace() }
            }
        }
    },
    ENDIA("endia") {
        override fun process(raw: String, params: Map<String, String>): String {
            val type = requireNotNull(params[C1])
            return raw.lineAction2String {
                runCatching { Endia.valueOf(type).convert(it) }.getOrElse { it.stacktrace() }
            }
        }
    },
    VARIABLE_NAMING("variable naming") {
        override fun process(raw: String, params: Map<String, String>): String {
            val type = requireNotNull(params[C1])
            return raw.lineAction2String {
                runCatching { VariableNaming.valueOf(type).convert(it) }
                    .getOrElse { it.stacktrace() }
            }
        }
    },
    ;

    override fun hint(): String {
        return MISC_CONFIG[this]!![HINT].orEmpty()
    }

    override fun options(): Array<out String> = MISC_OPTIONS_CONFIG[this]?.get(OPTIONS).orEmpty()

    override fun paramsHints(): Array<out String> =
        MISC_OPTIONS_CONFIG[this]?.get(PARAMS_HINT).orEmpty()
}

val miscServiceTypeMap = MiscServiceType.entries.associateBy { it.type }

fun String.miscServiceType() = miscServiceTypeMap[this] ?: MiscServiceType.UUID
