package me.leon.plugin

import org.junit.Test

class Kt {
    @Test
    fun whenTests() {

        val map = mapOf("kk" to "dd")
        println(map["kk"])
        println(map["kk2"])
        listOf(5, 6).associateBy { "_$it" }.also { println(it) }
        listOf(5, 6).associateWith { "---$it" }.also { println(it) }
        val a = 1

        println(
            when (a) {
                1 -> "+++"
                else -> "---"
            }
        )

        repeat(10) {
            when (it) {
                1, 2, 3 -> println("a == 1")
                4 -> println("a == 2")
                in listOf(5, 6) -> println("a == list")
                else -> println("a is neither 1 nor 2")
            }
        }
    }
}
