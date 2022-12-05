package me.leon

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder

val s = "abecefeg".repeat(200)

@Warmup(iterations = 0)
@Measurement(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
open class IterableBench {

    @Benchmark
    fun baseline() {
        // do nothing
    }

    @Benchmark
    fun toList(bl: Blackhole) {
        bl.consume(s.toList().distinct())
    }

    @Benchmark
    fun iterable(bl: Blackhole) {
        bl.consume(s.asIterable().distinct())
    }

    @Benchmark
    fun charArray(bl: Blackhole) {
        bl.consume(s.toCharArray().distinct())
    }
}

fun main() {
    val opt =
        OptionsBuilder()
            .measurementIterations(1)
            .warmupIterations(0)
            .include(IterableBench::class.java.simpleName)
            .forks(1)
            .build()

    Runner(opt).run()
}
