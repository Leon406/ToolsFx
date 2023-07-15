package me.leon.misc.net

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import me.leon.ext.fromJson
import me.leon.ext.readFromNet

/**
 * @author Leon
 * @since 2023-04-10 11:03
 * @email deadogone@gmail.com
 */
fun dnsSolve(urls: List<String>): String = runBlocking {
    val aliOkUrls =
        urls
            .sorted()
            .distinct()
            .map { domain -> async(DISPATCHER) { domain to fastestIp(resolveDomainByAli(domain)) } }
            .awaitAll()
            .filter { it.second != null }
    // if alidns ip is not available, try cloudfare dns
    val cfOkUrls =
        (urls - aliOkUrls.map { it.second!!.first }.toSet())
            .also { println("ali dns failed ips: $it") }
            .map { domain ->
                async(DISPATCHER) { domain to fastestIp(resolveDomainsByCloudfare(domain)) }
            }
            .awaitAll()
            .filter { it.second != null }

    (aliOkUrls + cfOkUrls).joinToString(System.lineSeparator()) {
        "${it.second!!.first} ${it.first}"
    }
}

fun fastestIp(ips: List<String>, timeout: Int = 2000): Pair<String, Long>? {
    return runCatching {
            ips.map { ip ->
                    ip to
                        runCatching { ip.connect(443, timeout) }
                            .getOrElse { ip.connect(80, timeout) }
                }
                .filter { it.second > 0 }
                .minBy { it.second }
        }
        .getOrNull()
}

fun resolveDomainByAli(name: String): List<String> =
    "https://dns.alidns.com/resolve?name=$name&type=1".readFromNet().parseDnsIp()

fun resolveDomainsByCloudfare(name: String): List<String> =
    "https://1.1.1.1/dns-query?name=$name&type=A"
        .readFromNet(headers = mapOf("accept" to "application/dns-json"))
        .parseDnsIp()

private fun String.parseDnsIp() =
    fromJson(DnsResponse::class.java).Answer.filter { it.type == 1 }.map { it.data }
