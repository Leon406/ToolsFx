package me.leon;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;

@Warmup(iterations = 1)
@Measurement(iterations = 2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@Fork(1)
public class ForBench {
    @Benchmark
    public void baseline() {
        // do nothing
    }

    @Benchmark
    public void forI() {
        ForTest.forI();
    }

    @Benchmark
    public void forI2() {
        ForTest.forI2();
    }

    @Benchmark
    public void forIn() {
        ForTest.forIn();
    }

    @Benchmark
    public void forEach() {
        ForTest.forEach();
    }

    @Benchmark
    public void streamFor() {
        ForTest.streamForEach();
    }

    @Benchmark
    public void streamParallelFor() {
        ForTest.streamParallel();
    }
}
