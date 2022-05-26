package me.leon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * size = 100000
 *
 * <p>Benchmark Mode Cnt Score Error Units ForBench.baseline thrpt 2 2306221.300 ops/ms
 * ForBench.forEach thrpt 2 16.756 ops/ms ForBench.forI thrpt 2 16.960 ops/ms ForBench.forI2 thrpt 2
 * 16.854 ops/ms ForBench.forIn thrpt 2 16.688 ops/ms ForBench.streamFor thrpt 2 15.063 ops/ms
 * ForBench.streamParallelFor thrpt 2 29.568 ops/ms
 */
public class ForTest {

    public static final int SIZE = 10000;
    public static List<String> list = new ArrayList<>(SIZE);

    static {
        for (int i = 0; i < SIZE; i++) {
            list.add("" + i);
            Optional.of(1).map(j -> j + 1);
        }
    }

    public static void forI() {
        for (int i = 0; i < list.size(); i++) {
            ForTest.action(list.get(i));
        }
    }

    public static void forI2() {
        for (int i = 0, size = list.size(); i < size; i++) {
            ForTest.action(list.get(i));
        }
    }

    public static void forIn() {
        for (String s : list) {
            ForTest.action(s);
        }
    }

    public static void forEach() {
        list.forEach(ForTest::action);
    }

    public static void streamForEach() {
        list.stream().forEach(ForTest::action);
    }

    public static void streamParallel() {
        list.parallelStream().forEach(ForTest::action);
    }

    public static void action(String obj) {}
}
