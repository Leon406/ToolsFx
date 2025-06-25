package me.leon.misc.net

import me.leon.ext.fromJson
import me.leon.ext.readFromNet

val DnsQueryDict =
    mapOf(
        "A" to "1",
        "CAA" to "257",
        "NS" to "2",
        "CNAME" to "5",
        "AAAA" to "28",
        "TXT" to "16",
        "MX" to "15",
    )

const val DNS_URL_FORMAT = "https://myssl.com/api/v1/tools/dns_query?qtype=%s&host=%s&qmode=-1"

data class MySSLResponse(val code: Int, val data: MutableMap<String, List<Info>>) {
    data class Info(val answer: Answer) {
        data class Answer(val records: List<RecordData>?) {
            data class RecordData(val ttl: Int, val value: String)
        }
    }
}

fun String.dnsQuery(type: String = "A") =
    DNS_URL_FORMAT.format(DnsQueryDict[type], this)
        .readFromNet()
        .fromJson<MySSLResponse>()
        .data
        .values
        .last()
        .last()
        .answer
        .records
        ?.joinToString(System.lineSeparator()) { it.value }
        .orEmpty()
