package me.leon

import kotlin.system.measureTimeMillis
import org.junit.Test

class ReplaceTest {
    @Test
    fun rTest() {
        val replaceMap =
            mutableMapOf(
                "一" to "1",
                "二" to "2",
                "三" to "3",
                "四" to "4",
                "五" to "5",
                "六" to "6",
                "七" to "7",
                "八" to "8",
                "九" to "9",
                "十" to "10"
            )

        var d2 = "一二三四五六七八九十"
        val sb = StringBuilder(d2)
        measureTimeMillis {
            sb.replace(replaceMap.keys.joinToString("|").toRegex()) { replaceMap[it.value] ?: "" }
            //                .also { println(it) }
        }
            .also { println("$it") }

        var d = "一二三四五六七八九十"

        measureTimeMillis {
            for ((k, v) in replaceMap) {
                d = d.replace(k.toRegex(), v)
            }
        }
            .also { println(" $it") }
    }
}
