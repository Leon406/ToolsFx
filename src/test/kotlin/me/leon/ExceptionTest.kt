package me.leon

import me.leon.ext.catch
import org.junit.Test

class ExceptionTest {

    @Test
    fun catchTest() {
        val result = catch({ println("ddd $it") }) { 6 / 0 }
        println(result)
    }
}
