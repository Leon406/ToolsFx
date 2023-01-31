package me.leon.ext

import java.net.*
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.*

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
            .map { it to async(DISPATCHER) { connect(it, 500) } }
            .filter { it.second.await() >= 0 }
            .map { it.first }
    }
}

fun String.lanScan(): List<Int> {
    val ports = 1..255
    return runBlocking {
        ports
            .map { it to async(DISPATCHER) { "${this@lanScan}.$it".ping(2000) } }
            .filter { it.second.await() >= 0 }
            .map { it.first }
    }
}
