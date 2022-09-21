package me.leon.ext

fun <E> Collection<E>.findParallel(default: E, condition: (E) -> Boolean): E =
    parallelStream().filter(condition).findFirst().orElseGet { default }
