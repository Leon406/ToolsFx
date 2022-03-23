package me.leon

import java.util.Calendar
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder

@Warmup(iterations = 0)
@Measurement(iterations = 2)
@Fork(1)
open class SimpleBench {
    @Benchmark fun nop() {
        // do nothing
    }

    @Benchmark
    fun stringConcat() {
        var s = ""
        for (i in 0..999) {
            s += i
        }
    }

    @Benchmark
    fun stringConcat2() {
        val sb = StringBuffer()
        for (i in 0..999) {
            sb.append(i)
        }
    }

    @Benchmark
    fun stringConcat3() {
        val sb = StringBuilder()
        for (i in 0..999) {
            sb.append(i)
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    fun time01() {
        System.currentTimeMillis()
        System.currentTimeMillis()
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    fun time02() {
        Calendar.getInstance().timeInMillis
        Calendar.getInstance().timeInMillis
    }

    @Benchmark
    fun time03() {
        val sb = StringBuilder()
        for (i in 0..999) {
            sb.append(i)
        }
    }
}

fun main() {
    val opt =
        OptionsBuilder()
            .measurementIterations(5)
            .warmupIterations(5)
            .include(SimpleBench::class.java.simpleName)
            .forks(1)
            .build()

    Runner(opt).run()
}
