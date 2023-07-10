package me.leon.coroutine

import kotlin.test.Test
import kotlinx.coroutines.*
import org.junit.Ignore

/**
 * @author Leon
 * @since 2023-06-05 16:11
 * @email deadogone@gmail.com
 */
@OptIn(DelicateCoroutinesApi::class)
@Suppress("All")
@Ignore
class CoroutineExceptionTest {

    val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }

    @Test
    fun `Exception propagation`() {
        runBlocking {
            val job =
                GlobalScope.launch { // root coroutine with launch
                    println("Throwing exception from launch")
                    throw IndexOutOfBoundsException() // Will be printed to the console by
                    // Thread.defaultUncaughtExceptionHandler
                }
            job.join()
            println("Joined failed job")
            val deferred =
                GlobalScope.async { // root coroutine with async
                    println("Throwing exception from async")
                    throw ArithmeticException() // Nothing is printed, relying on user to call await
                }
            try {
                deferred.await()
                println("Unreached")
            } catch (e: ArithmeticException) {
                println("Caught ArithmeticException")
            }
        }
    }

    @Test
    fun exception() {
        runBlocking {
            val job =
                GlobalScope.launch(handler) { // root coroutine, running in GlobalScope
                    println("job")
                    throw AssertionError()
                }
            val deferred =
                GlobalScope.async(handler) { // also root, but async instead of launch
                    println("deferred")
                    throw ArithmeticException() // Nothing will be printed, relying on user to call
                    // deferred.await()
                }
            joinAll(job, deferred)
            println("Done!")
        }
    }

    @Test
    fun `Cancellation and exceptions`() {
        // it terminates, but it does not cancel its parent
        runBlocking {
            val job = launch {
                val child = launch {
                    try {
                        delay(Long.MAX_VALUE)
                    } finally {
                        println("Child is cancelled")
                    }
                }
                yield()
                println("Cancelling child")
                child.cancel()
                child.join()
                yield()
                println("Parent is not cancelled")
            }
            job.join()
        }
        // 如果child不是 CancellationException异常, 会上抛并cancel父类
        runBlocking {
            val job =
                GlobalScope.launch(handler) {
                    launch { // the first child
                        try {
                            delay(Long.MAX_VALUE)
                        } finally {
                            withContext(NonCancellable) {
                                println(
                                    "Children are cancelled, but exception is not handled until all children terminate"
                                )
                                delay(100)
                                println("The first child finished its non cancellable block")
                            }
                        }
                    }
                    launch { // the second child
                        delay(10)
                        println("Second child throws an exception")
                        throw ArithmeticException()
                    }
                }
            job.join()
        }
    }

    @Test
    fun supervision() {
        // it terminates, but it does not cancel its parent
        runBlocking {
            val supervisor = SupervisorJob()
            with(CoroutineScope(coroutineContext + supervisor)) {
                // launch the first child -- its exception is ignored for this example (don't do
                // this in practice!)
                val firstChild =
                    launch(CoroutineExceptionHandler { _, _ -> }) {
                        println("The first child is failing")
                        throw AssertionError("The first child is cancelled")
                    }
                // launch the second child
                val secondChild = launch {
                    firstChild.join()
                    // Cancellation of the first child is not propagated to the second child
                    println(
                        "The first child is cancelled: ${firstChild.isCancelled}, but the second one is still active"
                    )
                    try {
                        delay(Long.MAX_VALUE)
                    } finally {
                        // But cancellation of the supervisor is propagated
                        println(
                            "The second child is cancelled because the supervisor was cancelled"
                        )
                    }
                }
                // wait until the first child fails & completes
                firstChild.join()
                println("Cancelling the supervisor")
                supervisor.cancel()
                secondChild.join()
            }
        }

        println("~~~~~Supervision scope")
        runBlocking {
            try {
                supervisorScope {
                    launch {
                        try {
                            println("The child is sleeping")
                            delay(Long.MAX_VALUE)
                        } finally {
                            println("The child is cancelled")
                        }
                    }
                    // Give our child a chance to execute and print using yield
                    yield()
                    println("Throwing an exception from the scope")
                    throw AssertionError()
                }
            } catch (e: AssertionError) {
                println("Caught an assertion error")
            }
        }
    }
}
