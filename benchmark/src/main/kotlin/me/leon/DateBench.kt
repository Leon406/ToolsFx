package me.leon

import java.time.*
import java.util.Date
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
open class DateBench {
    @Benchmark
    fun baseline() {
        // do nothing
    }

    @Benchmark
    fun date(bl: Blackhole) {
        bl.consume(Date())
    }

    @Benchmark
    fun localDate(bl: Blackhole) {
        bl.consume(Date.from(Instant.from(LocalDateTime.now().atZone(ZoneId.systemDefault()))))
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
