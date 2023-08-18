package me.leon.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CoroutineTest {
    @Test
    fun flow2() {
        runTest {
            flow {
                    for (i in 1..5) {
                        delay(100)
                        emit(i)
                    }
                }
                .map { it * it }
                .flowOn(Dispatchers.IO)
                .collect { println("${Thread.currentThread().name}: $it") }

            withTimeoutOrNull(2500) {
                flow {
                        for (i in 1..5) {
                            delay(1000)
                            emit(i)
                        }
                    }
                    .collect { println(it) }
            }

            println("Done")
        }
    }

    @Test
    fun asyncTest() {
        runBlocking {
            val deferred: Deferred<Int> = async { loadData() }
            println("waiting...")
            println(deferred.await())

            (1..3)
                .map {
                    async {
                        delay(1000L)
                        println("Loading $it")
                        it
                    }
                }
                .awaitAll()
                .sum()
                .also { println(it) }
        }
    }

    suspend fun loadData(): Int {
        println("loading...")
        delay(1000L)
        println("loaded!")
        return 42
    }

    @Test
    fun channelTest() {
        runBlocking {
            val channel = Channel<String>()
            log("blocking")

            launch {
                channel.send("A1")
                channel.send("A2")
                log("A done")
            }
            launch {
                channel.send("B1")
                log("B done")
            }
            launch {
                repeat(3) {
                    val x = channel.receive()
                    log(x)
                }
            }
        }
    }

    fun log(message: Any?) {
        println("[${Thread.currentThread().name}] $message")
    }

    @Test
    fun cancelTest() {
        // 超时会有异常, 如果不捕获不会抛出
        runTest {
            val job = launch {
                repeat(1000) { i ->
                    if (isActive) {
                        try {
                            println("job: I'm sleeping $i ...")
                            delay(500L)
                        } catch (e: Exception) {
                            println(e)
                        }
                    }
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            //            job.cancel() // cancels the job
            //            job.join() // waits for job's completion
            job.cancelAndJoin()
            println("main: Now I can quit.")
        }
    }

    @Test
    fun timeoutTest() {
        runTest {
            try {
                withTimeout(1300L) {
                    repeat(1000) { i ->
                        println("I'm sleeping $i ...")
                        delay(500L)
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    @Test
    fun batch() {
        val list = 1..100
        runBlocking {
            list.chunked(10).map { async(Dispatchers.IO) { it.forEach { Thread.sleep(200) } } }
        }
    }
}
