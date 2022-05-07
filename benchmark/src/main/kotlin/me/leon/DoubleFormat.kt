package me.leon

import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.*

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@Fork(1)
open class DoubleFormat {

    @Benchmark
    fun stringFormat(): String {
        return String.format("%.2f", 1.0 / 3)
    }

    @Benchmark
    fun decimalFormat(): String {
        return DecimalFormat("0.00").format(1.0 / 3)
    }
}
