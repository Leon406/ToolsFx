@file:OptIn(ExperimentalCoroutinesApi::class)

package me.leon.coroutine

import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Ignore

/**
 * @author Leon
 * @since 2023-06-05 16:48
 * @email deadogone@gmail.com
 */
@Ignore
class CoroutineShareTest {

    var counter = 0
    var counter2 = AtomicInteger(0)
    @OptIn(DelicateCoroutinesApi::class)
    val counterContext = newSingleThreadContext("CounterContext")
    val mutex = Mutex()

    suspend fun massiveRun(action: suspend () -> Unit) {
        val n = 100 // number of coroutines to launch
        val k = 1000 // times an action is repeated by each coroutine
        val time = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) { launch { repeat(k) { action() } } }
            }
        }
        println("Completed ${n * k} actions in $time ms")
    }

    @Test
    fun problem() {

        runBlocking {
            withContext(Dispatchers.Default) { massiveRun { counter++ } }
            println("Counter = $counter")
        }
    }

    @Test
    fun resolution() {

        runBlocking {
            withContext(Dispatchers.Default) { massiveRun { counter2.getAndIncrement() } }
            println("Counter = $counter2")
        }
        println("~~~~~切换线程计数")
        runBlocking {
            withContext(Dispatchers.Default) {
                massiveRun { withContext(counterContext) { counter++ } }
            }
            println("Counter = $counter")
        }
        println("~~~~~切换线程计数")
        counter = 0
        runBlocking {
            // confine everything to a single-threaded context
            withContext(counterContext) { massiveRun { counter++ } }
            println("Counter = $counter")
        }

        println("~~~~~lock")
        counter = 0
        runBlocking {
            // confine everything to a single-threaded context
            withContext(Dispatchers.Default) { massiveRun { mutex.withLock { counter++ } } }
            println("Counter = $counter")
        }
        println("~~~~~actor")
        counter = 0
        runBlocking {
            val counter = counterActor() // create the actor
            // confine everything to a single-threaded context
            withContext(Dispatchers.Default) { massiveRun { counter.send(IncCounter) } }
            // send a message to get a counter value from an actor
            val response = CompletableDeferred<Int>()
            counter.send(GetCounter(response))
            println("Counter = ${response.await()}")
            counter.close() // shutdown the actor
        }
    }

    // Message types for counterActor
    sealed class CounterMsg

    object IncCounter : CounterMsg() // one-way message to increment counter

    class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // a request with reply

    // This function launches a new counter actor
    @OptIn(ObsoleteCoroutinesApi::class)
    fun CoroutineScope.counterActor() =
        actor<CounterMsg> {
            var counter = 0 // actor state
            for (msg in channel) { // iterate over incoming messages
                when (msg) {
                    is IncCounter -> counter++
                    is GetCounter -> msg.response.complete(counter)
                }
            }
        }
}
