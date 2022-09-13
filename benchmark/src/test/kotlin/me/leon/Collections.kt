package me.leon

import androidx.collection.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.Test
import org.openjdk.jol.info.ClassLayout
import org.openjdk.jol.info.GraphLayout

class Collections {

    @Test
    fun coll() {
        //
        arrayMapOf<String, String>()
        arrayMapOf("1" to 1, "2" to 2, "3" to 3).also {
            println(it)
            println(GraphLayout.parseInstance(it).toFootprint())
        }
        arraySetOf<Int>()
        arraySetOf(4, 1, 2, 3, 3, 41, 4, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0).also { println(it) }
        // sizeOf每个元素的大小 create 元素不存在时是否创建,默认不创建
        val cache =
            lruCache(
                    6,
                    { _: String, _: String -> 2 },
                    { k: String -> "$k+111" },
                    { b, k, old, new -> if (!b) println("$k change : $old --> $new") }
                )
                .also { println(it) }
        cache.put("1", "1")
        cache.put("2", "2")
        cache.put("3", "3")
        cache.put("2", "22")
        println(cache.toString() + " " + cache.snapshot())
        //        cache.put("2", "22")
        println(cache["1"])
        println(cache.toString() + " " + cache.snapshot())
        println(cache["2"])
        println(cache.toString() + " " + cache.snapshot())
        cache.put("4", "1")
        println(cache.toString() + " " + cache.snapshot())
        println(cache["5"])
        println(cache.toString() + " " + cache.snapshot())

        SparseArrayCompat<String>().put(1, "1")
        LongSparseArray<String>().put(1L, "1")
    }

    @Test
    fun memoryCompare() {
        arrayMapOf("1" to 1, "2" to 2, "3" to 3).also {
            println("arrayMapOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }
        mapOf("1" to 1, "2" to 2, "3" to 3).also {
            println("mapOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }

        mutableMapOf("1" to 1, "2" to 2, "3" to 3).also {
            println("mutableMapOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }
        arraySetOf(4, 1, 2, 3, 3, 41, 4, 12).also {
            println("arraySetOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }
        hashSetOf(4, 1, 2, 3, 3, 41, 4, 12).also {
            println("hashSetOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }
        mutableSetOf(4, 1, 2, 3, 3, 41, 4, 12).also {
            println("mutableSetOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }
        linkedSetOf(4, 1, 2, 3, 3, 41, 4, 12).also {
            println("linkedSetOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }
    }

    @Test
    fun listMemory() {
        arrayMapOf(1 to POJO(), 2 to POJO(), 3 to POJO(), 4 to POJO()).also {
            println("arrayMapOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }

        SparseArrayCompat<POJO>()
            .apply {
                put(1, POJO())
                put(2, POJO())
                put(3, POJO())
                put(4, POJO())
            }
            .also {
                println("SparseArrayCompat :\n ${GraphLayout.parseInstance(it).toFootprint()}")
            }

        listOf(POJO(), POJO(), POJO(), POJO()).also {
            println("listOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }
        mutableListOf(POJO(), POJO(), POJO(), POJO()).also {
            println("mutableListOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }
        // 1个空间节省 4 个字节
        ArrayList<POJO>()
            .apply {
                add(POJO("a"))
                add(POJO("b"))
                add(POJO("c"))
                add(POJO("d"))
            }
            .also {
                println("ArrayList :\n ${GraphLayout.parseInstance(it).toPrintable()}")
                println("ArrayList :\n ${GraphLayout.parseInstance(it).totalSize()}")
            }

        mutableListOf(POJO(), POJO(), POJO(), POJO()).also {
            println("mutableListOf :\n ${GraphLayout.parseInstance(it).toFootprint()}")
        }
    }

    @Test
    fun mapMemory() {
        // 1个空间节省 4 个字节
        HashMap<String, String>(4)
            .apply {
                put("111", "11")
                put("22", "22")
                put("33", "333")
                //            put("44", "333")
            }
            .also {
                println(GraphLayout.parseInstance(it).toPrintable())
                println(GraphLayout.parseInstance(it).totalSize())
            }

        // 1个空间节省 4个字节
        ConcurrentHashMap<String, String>()
            .apply {
                put("111", "11")
                put("22", "22")
                put("33", "333")
            }
            .also {
                println(GraphLayout.parseInstance(it).toPrintable())
                println(GraphLayout.parseInstance(it).totalSize())
            }
    }

    @Test
    fun typeMemory() {
        println(GraphLayout.parseInstance(emptyMap<String, Any>()).toFootprint())
        println(GraphLayout.parseInstance(mutableMapOf<String, Any>()).toFootprint())
        val stringList =
            mutableListOf("330108000001", "330108000002", "330108000003", "330108000004")
        val longList =
            mutableListOf(330_108_000_001L, 330_108_000_002L, 330_108_000_003L, 330_108_000_004L)
        println(
            GraphLayout.parseInstance(stringList).also { println(it.totalSize()) }.toPrintable()
        )
        println(GraphLayout.parseInstance(longList).also { println(it.totalSize()) }.toPrintable())
        println(GraphLayout.parseInstance("330108000001".toLong()).toFootprint())
        println(
            GraphLayout.parseInstance("330108000001").also { println(it.totalSize()) }.toFootprint()
        )
        println(ClassLayout.parseInstance("330108000001").toPrintable())
        println(
            GraphLayout.parseInstance(byteArrayOf(1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6))
                .also { println(it.totalSize()) }
                .toFootprint()
        )
        //        println(GraphLayout.parseInstance(POJO()).also { println(it.totalSize())
        // }.toFootprint())
    }
}
