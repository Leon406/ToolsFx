package me.leon

fun main() {
    println("11")
    System.getProperties()
        .stringPropertyNames()
        .filter { System.getProperty(it).isNotEmpty() }
        .map { "$it ${System.getProperty(it)}" }
        .forEach { println(it) }
    //    repeat(100) {
    //        ByteArray(5 * 1024 * 1024)
    //    }
}
