package me.leon.coroutine

import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlinx.coroutines.*
import org.junit.Ignore

/**
 * @author Leon
 * @since 2023-06-05 9:24
 * @email deadogone@gmail.com
 */
@Ignore
class CoroutineTest2 {
    @Test
    fun sequential() {
        runBlocking {
            val time = measureTimeMillis {
                val one = doSomethingUsefulOne()
                val two = doSomethingUsefulTwo()
                println("The answer is ${one + two}")
            }
            println("Completed in $time ms")
        }
    }

    @Test
    fun asynchronize() {
        runBlocking {
            val time = measureTimeMillis {
                val one = async { doSomethingUsefulOne() }
                val two = async { doSomethingUsefulTwo() }
                println("The answer is ${one.await() + two.await()}")
            }
            println("Completed in $time ms")
        }
    }

    @Test
    fun asynchronize_lazy() {
        // start或者await时执行
        runBlocking {
            val time = measureTimeMillis {
                val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
                val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
                // 如果不start await会阻塞
                two.start() // start the second one
                one.start() // start the first one
                println("The answer is ${one.await() + two.await()}")
            }
            println("Completed in $time ms")
        }
    }

    @Test
    fun asynchronize_function() {
        // 不建议采用, 全局异常后不会停止其他
        val time = measureTimeMillis {
            // we can initiate async actions outside of a coroutine
            val one = somethingUsefulOneAsync()
            val two = somethingUsefulTwoAsync()
            // but waiting for a result must involve either suspending or blocking.
            // here we use `runBlocking { ... }` to block the main thread while waiting for the
            // result
            runBlocking { println("The answer is ${one.await() + two.await()}") }
        }
        println("Completed in $time ms")
    }

    @Test
    fun async_structured_concurrent() {
        // 如果异常,所有coroutines 都会停止
        runBlocking {
            val time = measureTimeMillis { println("The answer is ${concurrentSum()}") }
            println("Completed in $time ms")
        }
    }

    @Test
    fun async_failed_structured_concurrent() {
        // 如果异常,所有scope内 coroutines 都会停止
        runBlocking {
            try {
                failedConcurrentSum()
            } catch (ignored: ArithmeticException) {
                println("Computation failed with ArithmeticException")
            }
        }
    }

    suspend fun failedConcurrentSum(): Int = coroutineScope {
        val one =
            async<Int> {
                try {
                    delay(Long.MAX_VALUE) // Emulates very long computation
                    42
                } finally {
                    println("First child was cancelled")
                }
            }
        val two =
            async<Int> {
                println("Second child throws an exception")
                throw ArithmeticException()
            }
        one.await() + two.await()
    }
}

suspend fun doSomethingUsefulOne(): Int {
    println("11111111")
    delay(1000L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    println("22222")
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}

@Suppress("All")
// The result type of somethingUsefulOneAsync is Deferred<Int>
@OptIn(DelicateCoroutinesApi::class)
fun somethingUsefulOneAsync() = GlobalScope.async { doSomethingUsefulOne() }

@Suppress("All")
// The result type of somethingUsefulTwoAsync is Deferred<Int>
@OptIn(DelicateCoroutinesApi::class)
fun somethingUsefulTwoAsync() = GlobalScope.async { doSomethingUsefulTwo() }

suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}
