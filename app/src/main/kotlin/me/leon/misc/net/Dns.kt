package me.leon.misc.net

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import me.leon.ext.fromJson
import me.leon.ext.readFromNet

/**
 * @author Leon
 * @since 2023-04-10 11:03
 * @email deadogone@gmail.com
 */
fun dnsSolve(urls: List<String>): String = runBlocking {
    urls
        .sorted()
        .map { domain -> domain to async(DISPATCHER) { fastestIp(resolveDomains(domain)) } }
        .filter { it.second.await() != null }
        .map { "${it.second.await()!!.first} ${it.first}" }
        .joinToString(System.lineSeparator())
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

fun resolveDomains(name: String): List<String> =
    "https://dns.alidns.com/resolve?name=$name&type=1"
        .readFromNet()
        .fromJson(DnsResponse::class.java)
        .Answer
        .filter { it.type == 1 }
        .map { it.data }
