package me.leon.coroutine

import kotlin.system.measureTimeMillis
import kotlin.test.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * @author Leon
 * @since 2023-06-05 11:19
 * @email deadogone@gmail.com
 */
@Ignore
class CoroutineAsynchronousFlowTest {
    fun simple(): List<Int> = listOf(1, 2, 3)

    fun simple2(): Sequence<Int> = sequence { // sequence builder
        for (i in 1..3) {
            Thread.sleep(100) // pretend we are computing it
            yield(i) // yield next value
        }
    }

    suspend fun simple3(): List<Int> {
        delay(1000) // pretend we are doing something asynchronous here
        return listOf(1, 2, 3)
    }

    fun simple4(): Flow<Int> = flow { // flow builder
        for (i in 1..3) {
            delay(100) // pretend we are doing something useful here
            emit(i) // emit next value
        }
    }

    @Test
    fun `Representing multiple values`() {
        simple().forEach { value -> println(value) }
        simple2().forEach { value -> println(value) }

        runBlocking {
            simple3().forEach { value -> println(value) }

            // Launch a concurrent coroutine to check if the main thread is blocked
            launch {
                for (k in 1..3) {
                    println("I'm not blocked $k")
                    delay(100)
                }
            }
            // Collect the flow
            simple4().collect { value -> println(value) }
        }
    }

    @Test
    fun `Flows are cold`() {
        runBlocking {
            println("Calling simple function...")
            val flow = simple4()
            println("Calling collect...")
            flow.collect { value -> println(value) }
            println("Calling collect again...")
            flow.collect { value -> println(value) }
        }
    }

    @Test
    fun `Flow cancellation basics`() {
        runBlocking {
            withTimeoutOrNull(250) { // Timeout after 250ms
                simple4().collect { value -> println(value) }
            }
            println("Done")
        }
    }

    @Test
    fun `Intermediate flow operators`() {
        runBlocking {
            (1..3)
                .asFlow() // a flow of requests
                .map { request -> performRequest(request) }
                .collect { response -> println(response) }

            (1..3)
                .asFlow() // a flow of requests
                .transform { request ->
                    emit("Making request $request")
                    emit(performRequest(request))
                }
                .collect { response -> println(response) }
        }
    }

    @Test
    fun `Flows are sequential`() {
        runBlocking {
            (1..5)
                .asFlow()
                .flowOn(Dispatchers.IO)
                .filter {
                    println("Filter $it")
                    it % 2 == 0
                }
                .map {
                    println("Map $it")
                    "string $it"
                }
                .collect { println("Collect $it") }
        }
    }

    fun simple5(): Flow<Int> =
        flow {
                for (i in 1..3) {
                    Thread.sleep(100) // pretend we are computing it in CPU-consuming way
                    log("Emitting $i")
                    emit(i) // emit next value
                }
            }
            .flowOn(
                Dispatchers.Default
            ) // RIGHT way to change context for CPU-consuming code in flow builder

    @Test
    fun `flowOn operator`() {
        runBlocking { simple5().collect { value -> log("Collected $value") } }
    }

    @Test
    fun buffering() {
        runBlocking {
            val time = measureTimeMillis {
                simple4().collect { value ->
                    delay(300) // pretend we are processing it for 300 ms
                    println(value)
                }
            }
            println("Collected in $time ms")
        }
        runBlocking {
            val time = measureTimeMillis {
                simple4().buffer().collect { value ->
                    delay(300) // pretend we are processing it for 300 ms
                    println(value)
                }
            }
            println("Collected in $time ms")
        }
        // process only most recent ones
        runBlocking {
            val time = measureTimeMillis {
                simple4()
                    .conflate() // conflate emissions, don't process each one
                    .collect { value ->
                        delay(300) // pretend we are processing it for 300 ms
                        println(value)
                    }
            }
            println("Collected in $time ms")
        }
        // only latest
        runBlocking {
            val time = measureTimeMillis {
                simple4().collectLatest { value ->
                    println("Collecting $value")
                    delay(300) // pretend we are processing it for 300 ms
                    println("Done $value")
                }
            }
            println("Collected in $time ms")
        }
    }

    @Test
    fun `Composing multiple flows`() {
        runBlocking {
            val nums = (1..3).asFlow() // numbers 1..3
            val strs = flowOf("one", "two", "three") // strings
            nums
                .zip(strs) { a, b -> "$a -> $b" } // compose a single string
                .collect { println(it) } // collect and print
        }

        runBlocking {
            val nums = (1..3).asFlow().onEach { delay(300) } // numbers 1..3 every 300 ms
            val strs = flowOf("one", "two", "three").onEach { delay(400) } // strings every 400 ms
            val startTime = System.currentTimeMillis() // remember the start time
            nums
                .zip(strs) { a, b -> "$a -> $b" } // compose a single string with "zip"
                .collect { value -> // collect and print
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }
        runBlocking {
            val nums = (1..3).asFlow().onEach { delay(300) } // numbers 1..3 every 300 ms
            val strs = flowOf("one", "two", "three").onEach { delay(400) } // strings every 400 ms
            val startTime = System.currentTimeMillis() // remember the start time
            nums
                .combine(strs) { a, b -> "$a -> $b" } // compose a single string with "zip"
                .collect { value -> // collect and print
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flattening flows`() {
        println("~~~~~~~~~~flatMapConcat")
        runBlocking {
            val startTime = System.currentTimeMillis() // remember the start time
            (1..3)
                .asFlow()
                .onEach { delay(100) } // emit a number every 100 ms
                .flatMapConcat { requestFlow(it) }
                .collect { value -> // collect and print
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }
        println("~~~~~~~~~~flatMapMerge")
        runBlocking {
            val startTime = System.currentTimeMillis() // remember the start time
            (1..3)
                .asFlow()
                .onEach { delay(100) } // emit a number every 100 ms
                .flatMapMerge { requestFlow(it) }
                .collect { value -> // collect and print
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }
        println("~~~~~~~~~~flatMapLatest")
        runBlocking {
            val startTime = System.currentTimeMillis() // remember the start time
            (1..3)
                .asFlow()
                .onEach { delay(100) } // a number every 100 ms
                .flatMapLatest { requestFlow(it) }
                .collect { value -> // collect and print
                    println("$value at ${System.currentTimeMillis() - startTime} ms from start")
                }
        }
    }

    fun simple6(): Flow<String> =
        flow {
                for (i in 1..3) {
                    println("Emitting $i")
                    emit(i) // emit next value
                }
            }
            .map { value ->
                check(value <= 1) { "Crashed on $value" }
                "string $value"
            }

    @Test
    fun `Exception transparency`() {
        runBlocking {
            simple6()
                .catch { e -> emit("Caught $e") } // emit on exception
                .collect { value -> println(value) }
        }
        // only catch upstream
        runBlocking {
            runCatching {
                    simple4()
                        .catch { e -> println("Caught $e") } // emit on exception
                        .collect { value ->
                            check(value <= 1) { "Collected $value" }
                            println(value)
                        }
                }
                .getOrElse { println("global exception $it") }
        }

        runBlocking {
            simple4()
                .onEach { value ->
                    check(value <= 1) { "Collected $value" }
                    println(value)
                }
                .catch { e -> println("Caught $e") }
                .collect()
        }
    }

    @Test
    fun `Flow completion`() {
        val flow = (1..3).asFlow()
        runBlocking {
            try {
                flow.collect { value -> println(value) }
            } finally {
                println("Done")
            }

            println("~~~~~onCompletion")
            flow.onCompletion { println("Done") }.collect { value -> println(value) }

            println("~~~~~onCompletion state")
            flow
                .onEach { check(it < 0) }
                .onCompletion { cause ->
                    if (cause != null) println("Flow completed exceptionally")
                }
                .catch { println("Caught exception") }
                .collect { value -> println(value) }
        }
    }

    // Imitate a flow of events
    fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

    @Test
    fun `Launching flow`() {
        runBlocking {
            events()
                .onEach { event -> println("Event: $event") }
                .collect() // <--- Collecting the flow waits
            println("Done")
        }

        println("~~~~~~~~ blocking2")

        runBlocking {
            events()
                .onEach { event -> println("Event: $event") }
                .launchIn(this) // <--- Launching the flow in a separate coroutine
            println("Done")
        }

        runBlocking {
            (1..5).asFlow().cancellable().collect { value ->
                if (value == 3) cancel()
                println(value)
            }
        }
        //        runBlocking {
        //            foo().collect { value ->
        //                if (value == 3) cancel()
        //                println(value)
        //            }
        //        }
    }

    fun foo(): Flow<Int> = flow {
        for (i in 1..5) {
            println("Emitting $i")
            emit(i)
        }
    }

    fun requestFlow(i: Int): Flow<String> = flow {
        emit("$i: First")
        delay(500) // wait 500 ms
        emit("$i: Second")
    }

    suspend fun performRequest(request: Int): String {
        delay(1000) // imitate long-running asynchronous work
        return "response $request"
    }
}
