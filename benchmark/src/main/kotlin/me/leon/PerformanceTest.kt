package me.leon

import java.util.Random
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.*

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@Fork(1)
open class PerformanceTest {

    // Could be an implicit State instead, but we are going to use it
    // as the dependency in one of the tests below
    @State(Scope.Benchmark)
    open class Data {
        @Param("1", "16", "256") var count = 0
        lateinit var arr: ByteArray

        @Setup
        fun setup() {
            arr = ByteArray(count)
            val random = Random(1234)
            random.nextBytes(arr)
        }
    }

    /*
     * The method above is subtly wrong: it sorts the random array on the first invocation
     * only. Every subsequent call will "sort" the already sorted array. With bubble sort,
     * that operation would be significantly faster!
     *
     * This is how we might *try* to measure it right by making a copy in Level.Invocation
     * setup. However, this is susceptible to the problems described in Level.Invocation
     * Javadocs, READ AND UNDERSTAND THOSE DOCS BEFORE USING THIS APPROACH.
     */
    @State(Scope.Thread)
    open class DataCopy {
        lateinit var copy: ByteArray

        @Setup(Level.Invocation)
        fun setup2(d: Data) {
            copy = d.arr.copyOf(d.arr.size)
        }
    }

    private fun bubbleSort(b: ByteArray) {
        var changed = true
        while (changed) {
            changed = false
            for (c in 0 until b.size - 1) {
                if (b[c] > b[c + 1]) {
                    val t: Byte = b[c]
                    b[c] = b[c + 1]
                    b[c + 1] = t
                    changed = true
                }
            }
        }
    }

    @Benchmark
    fun bubble(d: Data): ByteArray {
        val c = d.arr.copyOf(d.arr.size)
        bubbleSort(c)
        return c
    }

    @Benchmark
    fun copyArray(d: Data): ByteArray {
        return d.arr.copyOf(d.arr.size)
    }

    @Benchmark
    fun bubble2(d: Data): ByteArray {
        // 修改原始数据,导致第二次后的数据源为已排序
        bubbleSort(d.arr)
        return d.arr
    }

    @Benchmark
    fun bubble3(d: DataCopy): ByteArray {
        bubbleSort(d.copy)
        return d.copy
    }
}
