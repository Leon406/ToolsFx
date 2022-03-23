package me.leon

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.RunnerException
import org.openjdk.jmh.runner.options.OptionsBuilder

@BenchmarkMode(Mode.AverageTime)
open class BenchMarkTest {
    @Benchmark
    fun hello() {
        println("Hello, World!")
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You are expected to see the run with large number of iterations, and
     * very large throughput numbers. You can see that as the estimate of the
     * harness overheads per method call. In most of our measurements, it is
     * down to several cycles per call.
     *
     * a) Via command-line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_01
     *
     * JMH generates self-contained JARs, bundling JMH together with it.
     * The runtime options for the JMH are available with "-h":
     *    $ java -jar target/benchmarks.jar -h
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    companion object {
        @Throws(RunnerException::class)
        @JvmStatic
        open fun main(args: Array<String>) {
            val opt =
                OptionsBuilder()
                    .measurementIterations(5)
                    .warmupIterations(5)
                    .include(BenchMarkTest::class.java.simpleName)
                    .forks(1)
                    .build()
            Runner(opt).run()
        }
    }
}
