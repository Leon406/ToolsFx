@file:OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package me.leon.coroutine

import kotlin.test.Ignore
import kotlin.test.Test
import kotlinx.coroutines.*

/**
 * @author Leon
 * @since 2023-06-05 10:18
 * @email deadogone@gmail.com
 */
@Ignore
class CoroutineContextAndDispatcherTest {
    val threadLocal = ThreadLocal<String?>()

    @Test
    fun dispatcher() {
        println(
            "main runBlocking      : thread ${Thread.currentThread().id}-${Thread.currentThread().name}"
        )
        runBlocking {
            println(
                "main runBlocking      : thread ${Thread.currentThread().id}-${Thread.currentThread().name}"
            )
            launch { // context of the parent, main runBlocking coroutine
                println(
                    "main runBlocking      : thread ${Thread.currentThread().id}-${Thread.currentThread().name}"
                )
            }
            launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
                println(
                    "Unconfined            : thread ${Thread.currentThread().id}-${Thread.currentThread().name}"
                )
            }
            launch(Dispatchers.Default) { // will get dispatched to DefaultDispatcher
                println(
                    "Default               : thread ${Thread.currentThread().id}-${Thread.currentThread().name}"
                )
            }
            launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
                println(
                    "newSingleThreadContext: thread ${Thread.currentThread().id}-${Thread.currentThread().name}"
                )
            }
        }
    }

    @Test
    fun repeatedly() {
        runBlocking {
            repeat(1000) {
                async {
                    delay(1000)
                    println("${Thread.currentThread().id}-" + Thread.currentThread().name)
                }
            }
        }
    }

    @Test
    fun confined() {
        runBlocking {
            launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
                println("Unconfined      : thread ${Thread.currentThread().name}")
                delay(500)
                println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
            }
            launch { // context of the parent, main runBlocking coroutine
                println("main runBlocking: thread ${Thread.currentThread().name}")
                delay(1000)
                println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
            }
        }
    }

    @Test
    fun thread_jump() {
        runBlocking {
            newSingleThreadContext("Ctx1").use { ctx1 ->
                newSingleThreadContext("Ctx2").use { ctx2 ->
                    println("My job is ${coroutineContext[Job]}")
                    runBlocking(ctx1) {
                        log("Started in ctx1")
                        withContext(ctx2) { log("Working in ctx2") }
                        log("Back to ctx1")
                    }
                }
            }
        }
    }

    @Test
    fun `Children of a coroutine`() {
        runBlocking {
            // launch a coroutine to process some kind of incoming request
            val request = launch {
                // it spawns two other jobs
                launch(Job()) {
                    println("job1: I run in my own Job and execute independently!")
                    delay(1000)
                    println("job1: I am not affected by cancellation of the request")
                }
                // and the other inherits the parent context
                launch {
                    delay(100)
                    println("job2: I am a child of the request coroutine")
                    delay(1000)
                    println("job2: I will not execute this line if my parent request is cancelled")
                }
            }
            delay(500)
            request.cancel() // cancel processing of the request
            println("main: Who has survived request cancellation?")
            delay(1000) // delay the main thread for a second to see what happens
        }
    }

    @Test
    fun `Parental responsibilities`() = runBlocking {
        // launch a coroutine to process some kind of incoming request
        val request = launch {
            repeat(3) { i -> // launch a few children jobs
                launch {
                    delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
                    println("Coroutine $i is done")
                }
            }
            println(
                "request: I'm done and I don't explicitly join my children that are still active"
            )
        }
        request.join() // wait for completion of the request, including all its children
        println("Now processing of the request is complete")
    }

    @Test
    fun `Naming coroutines for debugging`() =
        runBlocking(CoroutineName("main")) {
            log("Started main coroutine")
            // run two background value computations
            val v1 =
                async(CoroutineName("v1coroutine")) {
                    delay(500)
                    log("Computing v1")
                    252
                }
            val v2 =
                async(CoroutineName("v2coroutine")) {
                    delay(1000)
                    log("Computing v2")
                    6
                }
            log("The answer for v1 / v2 = ${v1.await() / v2.await()}")
        }

    @Test
    fun `Combining context elements`(): Unit = runBlocking {
        launch(Dispatchers.Default + CoroutineName("test")) {
            println("thread ${Thread.currentThread().name}")
        }
    }

    @Test
    fun `Thread-local data`(): Unit = runBlocking {
        threadLocal.set("main")
        println(
            "Pre-main,thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'"
        )
        val job =
            launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
                println(
                    "Launch start,thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'"
                )
                yield()
                println(
                    "After yield,thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'"
                )
            }
        job.join()
        println(
            "Post-main,thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'"
        )
    }
}

fun log(msg: String) = println("[${Thread.currentThread().id}-${Thread.currentThread().name}] $msg")
