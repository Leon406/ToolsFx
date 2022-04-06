package me.leon

import java.util.Calendar
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder

@Warmup(iterations = 0)
@Measurement(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
open class SimpleBench {
    @Benchmark
    fun baseline() {
        // do nothing
    }

    @Benchmark
    fun stringConcat(bl: Blackhole) {
        var s = ""
        for (i in 0..999) {
            s += i
        }
        bl.consume(s)
    }

    @Benchmark
    fun stringBuffer(bl: Blackhole) {
        val sb = StringBuffer()
        for (i in 0..999) {
            sb.append(i)
        }
        bl.consume(sb)
    }

    @Benchmark
    fun stringBuilder(bl: Blackhole) {
        val sb = StringBuilder()
        for (i in 0..999) {
            sb.append(i)
        }
        bl.consume(sb)
    }

    @Benchmark
    fun time01(): Long {
        val start = System.currentTimeMillis()
        val end = System.currentTimeMillis()
        return end - start
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    fun time02(): Long {
        val start = Calendar.getInstance().timeInMillis
        val end = Calendar.getInstance().timeInMillis
        return end - start
    }
}

fun main() {
    val opt =
        OptionsBuilder()
            .measurementIterations(1)
            .warmupIterations(0)
            .include(SimpleBench::class.java.simpleName)
            .forks(1)
            .build()

    Runner(opt).run()
}
