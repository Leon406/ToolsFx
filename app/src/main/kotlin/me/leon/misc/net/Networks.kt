package me.leon.misc.net

import java.net.*
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.*
import me.leon.ext.headRequest

/**
 * @author Leon
 * @since 2023-01-31 17:00
 * @email deadogone@gmail.com
 */
@OptIn(ExperimentalCoroutinesApi::class) val DISPATCHER = Dispatchers.IO.limitedParallelism(128)

val LOCAL_IP_A = "^10\\.".toRegex()
val LOCAL_IP_B = "^172\\.(1[6-9]|2[0-9]|3[0-1])\\.".toRegex()
val LOCAL_IP_C = "^192\\.168\\.".toRegex()
const val LOOPBACK_IP = "127.0.0.1"

fun String.connect(
    port: Int = 80,
    timeout: Int = 1000,
    exceptionHandler: (info: String) -> Unit = {}
) =
    if (!contains(".") && !contains(":") || port < 0) {
        //        println("quick fail from cache")
        -1
    } else {
        runCatching {
                measureTimeMillis { Socket().connect(InetSocketAddress(this, port), timeout) }
            }
            .getOrElse {
                exceptionHandler.invoke("$this:$port")
                -1
            }
    }

/** ping 测试 */
fun String.ping(timeout: Int = 1000, exceptionHandler: (info: String) -> Unit = {}) =
    if (!contains(".") && !contains(":")) {
        println("fast failed")
        -1
    } else {
        runCatching {
                val start = System.currentTimeMillis()
                val reachable = InetAddress.getByName(this).isReachable(timeout)
                if (reachable) {
                    (System.currentTimeMillis() - start)
                } else {
                    exceptionHandler.invoke(this)
                    -1
                }
            }
            .getOrElse {
                exceptionHandler.invoke(this)
                -1
            }
    }

fun String.portScan(ports: List<Int> = (1..10_000).toList()): List<Int> {
    return runBlocking {
        ports
            .map { async(DISPATCHER) { it to connect(it, 500) } }
            .awaitAll()
            .filter { it.second >= 0 }
            .map { it.first }
    }
}

fun String.lanScan(): List<Int> {
    val ports = 1..255
    return runBlocking {
        ports
            .map { async(DISPATCHER) { it to "${this@lanScan}.$it".ping(2000) } }
            .awaitAll()
            .filter { it.second >= 0 }
            .map { it.first }
    }
}

fun String.batchPing(type: String) = batchPingResult().properResult(type)

fun String.batchPingResult() = lines().batchPingResult()

fun Collection<String>.batchPingResult() = runBlocking {
    filter { it.isNotEmpty() }
        .map { async(DISPATCHER) { it to it.ping() } }
        .awaitAll()
        .sortedByDescending { it.second }
}

fun String.batchTcPingResult() = runBlocking {
    lines()
        .filter { it.contains(":") }
        .map {
            async(DISPATCHER) {
                it to it.substringBeforeLast(":").connect(it.substringAfterLast(":").toInt())
            }
        }
        .awaitAll()
        .sortedByDescending { it.second }
}

fun String.batchTcPing(type: String) = batchTcPingResult().properResult(type)

private fun List<Pair<String, Long>>.properResult(type: String) =
    when (type) {
        "Ok" ->
            filter { it.second >= 0 }
                .sortedBy { it.second }
                .joinToString(System.lineSeparator()) { it.first }
        "Fail" -> filter { it.second < 0 }.joinToString(System.lineSeparator()) { it.first }
        "All" -> joinToString(System.lineSeparator()) { "${it.first}\t${it.second}" }
        else -> joinToString(System.lineSeparator()) { "${it.first}\t${it.second}" }
    }

fun Collection<String>.batchTcPing(port: Int) = runBlocking {
    map { async(DISPATCHER) { it to it.connect(port) } }.awaitAll().sortedByDescending { it.second }
}

fun String.linkCheck(timeout: Int = 2000, type: String = "All") =
    linkCheckResult(timeout).linkCheckProperResult(type)

fun List<Pair<String, Boolean>>.linkCheckProperResult(type: String) =
    when (type) {
        "Ok" -> filter { it.second }.joinToString(System.lineSeparator()) { it.first }
        "Fail" -> filterNot { it.second }.joinToString(System.lineSeparator()) { it.first }
        "All" -> joinToString(System.lineSeparator()) { "${it.first}\t${it.second}" }
        else -> joinToString(System.lineSeparator()) { "${it.first}\t${it.second}" }
    }

fun String.linkCheckResult(timeout: Int = 2000) =
    lines().filter { it.isNotEmpty() }.linkCheck(timeout)

fun Collection<String>.linkCheck(timeout: Int = 2000) = runBlocking {
    filter { it.isNotEmpty() }
        .map { it.substringBefore("\t").substringBefore(" ") }
        .map { async(DISPATCHER) { it to it.headRequest("GET", timeout) } }
        .awaitAll()
        .sortedByDescending { it.second }
}
