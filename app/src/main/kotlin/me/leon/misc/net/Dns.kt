package me.leon.misc.net

import kotlinx.coroutines.*
import me.leon.ext.fromJson
import me.leon.ext.readFromNet

/**
 * @author Leon
 * @since 2023-04-10 11:03
 * @email deadogone@gmail.com
 */
fun dnsSolve(urls: Iterable<String>): String =
    dnsSolveResult(urls)
        .groupBy { it.second.ipCdnType() }
        .toList()
        .joinToString(System.lineSeparator()) { (k, v) ->
            v.joinToString(System.lineSeparator(), "# $k ip ${System.lineSeparator()}") {
                it.second + "\t" + it.first
            }
        }

fun dnsSolveResult(domains: Iterable<String>): List<Pair<String, String>> = runBlocking {
    val aliOkDomains =
        domains
            .sorted()
            .distinct()
            .map { domain -> async(DISPATCHER) { domain to fastestIp(resolveDomainByAli(domain)) } }
            .awaitAll()
            .filter { it.second != null }
    // if alidns ip is not available, try cloudfare dns
    val cfOkDomains =
        (domains - aliOkDomains.map { it.first }.toSet())
            .also {
                println("ali dns failed ips: ${it.size} ${it.joinToString(System.lineSeparator())}")
            }
            .map { domain ->
                async(DISPATCHER) { domain to fastestIp(resolveDomainsByCloudfare(domain)) }
            }
            .awaitAll()
            .filter { it.second != null }
    val googleOkDomains =
        (domains - aliOkDomains.map { it.first }.toSet() - cfOkDomains.map { it.first }.toSet())
            .also {
                println("cf dns failed ips: ${it.size} ${it.joinToString(System.lineSeparator())}")
            }
            .map { domain ->
                async(DISPATCHER) { domain to fastestIp(resolveDomainsByGoogle(domain)) }
            }
            .awaitAll()
            .filter { it.second != null }
    val okDomains = aliOkDomains + cfOkDomains + googleOkDomains
    val errorDomain = domains - okDomains.map { it.first }.toSet()
    okDomains.map { it.first to it.second!!.first } + errorDomain.map { it to "127.0.0.1" }
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

val ALI_DNS = listOf("223.5.5.5", "223.6.6.6")

fun resolveDomainByAli(name: String): List<String> =
    "https://${ALI_DNS.random()}/resolve?name=$name&type=1".readFromNet().parseDnsIp()

fun resolveDomainsByCloudfare(name: String): List<String> =
    "https://cloudflare-dns.com/dns-query?name=$name"
        .readFromNet(headers = mapOf("accept" to "application/dns-json"))
        .parseDnsIp()

fun resolveDomainsByGoogle(name: String): List<String> =
    "https://dns.google/resolve?name=$name&type=A".readFromNet().parseDnsIp()

private fun String.parseDnsIp() =
    runCatching {
            fromJson(DnsResponse::class.java).Answer?.filter { it.type == 1 }
                ?.map { it.data }
                .orEmpty()
        }
        .getOrElse { emptyList() }
