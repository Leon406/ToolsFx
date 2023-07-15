package me.leon.misc.net

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.*
import me.leon.ext.headRequest

/**
 * @author Leon
 * @since 2023-01-31 17:00
 * @email deadogone@gmail.com
 */
@OptIn(ExperimentalCoroutinesApi::class) val DISPATCHER = Dispatchers.IO.limitedParallelism(1024)

fun String.connect(
    port: Int = 80,
    timeout: Int = 1000,
    exceptionHandler: (info: String) -> Unit = {}
) =
    if (!contains(".") || port < 0) {
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
    if (!contains(".")) {
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

fun String.batchPing(type: String) =
    with(batchPingResult()) {
        when (type) {
            "Ok" ->
                filter { it.second > 0 }
                    .sortedBy { it.second }
                    .joinToString(System.lineSeparator()) { it.first }
            "Fail" -> filter { it.second < 0 }.joinToString(System.lineSeparator()) { it.first }
            "All" -> joinToString(System.lineSeparator()) { "${it.first}\t${it.second}" }
            else -> joinToString(System.lineSeparator()) { "${it.first}\t${it.second}" }
        }
    }

fun String.batchPingResult() = runBlocking {
    lines()
        .filter { it.isNotEmpty() }
        .map { async(DISPATCHER) { it to it.substringBeforeLast(":").ping() } }
        .awaitAll()
        .sortedByDescending { it.second }
}

fun String.batchTcPing() = runBlocking {
    lines()
        .filter { it.contains(":") }
        .map {
            async(DISPATCHER) {
                it to it.substringBeforeLast(":").connect(it.substringAfterLast(":").toInt())
            }
        }
        .awaitAll()
        .sortedByDescending { it.second }
        .joinToString(System.lineSeparator()) { "${it.first}\t${it.second}" }
}

fun String.linkCheck(timeout: Int = 2000) =
    lines()
        .filter { it.isNotEmpty() }
        .linkCheck(timeout)
        .joinToString(System.lineSeparator()) { "${it.first}\t${it.second}" }

fun List<String>.linkCheck(timeout: Int = 2000) = runBlocking {
    filter { it.isNotEmpty() }
        .map { async(DISPATCHER) { it to it.headRequest("GET", timeout) } }
        .awaitAll()
        .sortedByDescending { it.second }
}
