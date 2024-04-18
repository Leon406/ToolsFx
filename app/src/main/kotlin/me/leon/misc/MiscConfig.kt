package me.leon.misc

import me.leon.misc.net.GithubAction
import me.leon.misc.net.ShortUrlEnum
import me.leon.misc.unit.UNIT_TYPES

/**
 * @author Leon
 * @since 2023-06-02 14:04
 * @email deadogone@gmail.com
 */
const val OPTIONS = "options"
const val HINT = "hint"
const val PARAMS_HINT = "params_hint"
val MISC_CONFIG =
    mapOf(
        MiscServiceType.UUID to mapOf(HINT to "generate count"),
        MiscServiceType.TIME_STAMP to mapOf(HINT to "timestamp digit,separate by line"),
        MiscServiceType.DATE2STAMP to
            mapOf(
                HINT to
                    ("date, support format " +
                        "like 2023-02-01 12:00:00, 2023-02-01, 2023/02/01, 20230201,separate by line")
            ),
        MiscServiceType.PORT_SCAN to mapOf(HINT to "ip or domain (port from 1 to 10000)"),
        MiscServiceType.IP_SCAN to mapOf(HINT to "ip w/o last dot,like 192.168.0"),
        MiscServiceType.BATCH_PING to mapOf(HINT to "ping ip or domains,separate by line"),
        MiscServiceType.TCPING to
            mapOf(HINT to "tcp ping ip or domains,separate by line,format ip:port"),
        MiscServiceType.WHOIS to mapOf(HINT to "domain,separate by line"),
        MiscServiceType.IP2INT to
            mapOf(HINT to "ip, transform ip to integer, eg. 192.168.0.1,separate by line"),
        MiscServiceType.INT2IP to
            mapOf(HINT to "int, transform integer to ip,  eg. 3232235521,separate by line"),
        MiscServiceType.CIDR to mapOf(HINT to "ip, format 192.168.0.1/25,separate by line"),
        MiscServiceType.LINK_CHECK to mapOf(HINT to "url links,separate by line"),
        MiscServiceType.IP_LOCATION to mapOf(HINT to "ip/url"),
        MiscServiceType.DNS_SOLVE to mapOf(HINT to "domains,separate by line, comment by #"),
        MiscServiceType.CRON_EXPLAIN to
            mapOf(HINT to "cron expression, support crontab, quarts and normal format"),
        MiscServiceType.GITHUB to mapOf(HINT to "github repo or raw link"),
        MiscServiceType.ENCODING_RECOVERY to mapOf(HINT to "recover encoding"),
        MiscServiceType.FULL_WIDTH to mapOf(HINT to "transfer full/half width char"),
        MiscServiceType.PUNCTUATION to mapOf(HINT to "convert punctuation format"),
        MiscServiceType.TRANSLATE to mapOf(HINT to "translate sentence to specific language"),
        MiscServiceType.ROMAN to mapOf(HINT to "roman number, like VIII or 8,separate by line"),
        MiscServiceType.ROMANJI to mapOf(HINT to "romanji for Chinese,Japanese,Korean"),
        MiscServiceType.UNIT_CONVERT to mapOf(HINT to "unit convert, should specify type"),
        MiscServiceType.TRADITION_CHINESE_CONVERT to mapOf(HINT to "convert tradition chinese"),
        MiscServiceType.SHORT_URL to mapOf(HINT to "short long url,separate by line"),
        MiscServiceType.CODE_EXPLAIN to
            mapOf(HINT to "explain the meaning of code,separate by line"),
        MiscServiceType.ENDIA to
            mapOf(
                HINT to
                    "convert raw string or hex string to byte order hex(add '0x' prefix to hex string),separate by line"
            ),
        MiscServiceType.VARIABLE_NAMING to
            mapOf(HINT to "variable naming convert, separate by line"),
    )

val MISC_OPTIONS_CONFIG =
    mapOf(
        MiscServiceType.TIME_STAMP to
            mapOf(
                OPTIONS to
                    arrayOf(
                        "milliseconds",
                        "seconds",
                        "minutes",
                        "hours",
                        "days",
                    )
            ),
        MiscServiceType.UUID to mapOf(OPTIONS to arrayOf("normal", "w/o '-'")),
        MiscServiceType.ROMANJI to
            mapOf(OPTIONS to KawaType.entries.map { it.toString() }.toTypedArray()),
        MiscServiceType.PORT_SCAN to
            mapOf(
                OPTIONS to
                    arrayOf(
                        "1-1023",
                        "1-10000",
                        "1-20000",
                        "1-30000",
                        "1-40000",
                        "1-49151",
                        "1-65535"
                    )
            ),
        MiscServiceType.FULL_WIDTH to mapOf(OPTIONS to arrayOf("toFull", "toHalf")),
        MiscServiceType.PUNCTUATION to mapOf(OPTIONS to arrayOf("ZH", "EN")),
        MiscServiceType.TRANSLATE to mapOf(OPTIONS to Translator.SUPPORT_LANGUAGE),
        MiscServiceType.UNIT_CONVERT to mapOf(OPTIONS to UNIT_TYPES),
        MiscServiceType.BATCH_PING to mapOf(OPTIONS to arrayOf("All", "Ok", "Fail")),
        MiscServiceType.LINK_CHECK to mapOf(OPTIONS to arrayOf("All", "Ok", "Fail")),
        MiscServiceType.TCPING to mapOf(OPTIONS to arrayOf("All", "Ok", "Fail")),
        MiscServiceType.SHORT_URL to
            mapOf(OPTIONS to ShortUrlEnum.values().map { it.name }.toTypedArray()),
        MiscServiceType.TRADITION_CHINESE_CONVERT to
            mapOf(
                OPTIONS to
                    arrayOf(
                        "t2s",
                        "s2t",
                        "s2tw",
                        "s2twp",
                        "s2hk",
                        "tw2s",
                        "hk2s",
                        "hk2t",
                        "jp2t",
                        "t2jp"
                    )
            ),
        MiscServiceType.CODE_EXPLAIN to mapOf(OPTIONS to CodeMapping.TYPE.keys.toTypedArray()),
        MiscServiceType.ENDIA to mapOf(OPTIONS to Endia.values().map { it.name }.toTypedArray()),
        MiscServiceType.VARIABLE_NAMING to
            mapOf(OPTIONS to VariableNaming.values().map { it.name }.toTypedArray()),
        MiscServiceType.GITHUB to
            mapOf(OPTIONS to GithubAction.values().map { it.name }.toTypedArray()),
    )
