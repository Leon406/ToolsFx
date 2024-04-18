package me.leon.misc

import kotlin.test.Test

/**
 * @author Leon
 * @since 2024-04-17 11:09
 * @email deadogone@gmail.com
 */
class NamingTest {

    private val raw = "hello leon"

    @Test
    fun test() {

        var list =
            setOf(
                "helloLeon",
                "HelloLeon",
                "hello_leon",
                "HELLO_LEON",
                "hello-leon",
                "HELLO-LEON",
                "hello.leon",
                "leon.hello",
                "Hello leon",
                "Hello Leon",
                "HELLO Leon",
            )

        println(VariableNaming.values().map { it.name })
        list = list.map { it.normalSpaceFormat() }.toSet()

        //        list.forEach { println(it.camelNaming()) }
        //        list.forEach { println(it.pascalNaming()) }
        //        list.forEach { println(it.uppercaseNaming()) }
        //        list.forEach { println(it.snakeCaseNaming()) }
        //        list.forEach { println(it.dashNaming()) }
        //        list.forEach { println(it.uppercaseDashNaming()) }
        //        list.forEach { println(it.packageNaming()) }
        //        list.forEach { println(it.packageReverseNaming()) }
        //        list.forEach { println(it.titleNaming()) }
        list.forEach { println(it.firstWordUppercaseNaming()) }
    }
}
