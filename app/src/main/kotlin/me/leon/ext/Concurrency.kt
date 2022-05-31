package me.leon.ext

fun <E> Collection<E>.findParallel(default: E, condition: (E) -> Boolean) =
    parallelStream().filter(condition).findFirst().orElseGet { default }
