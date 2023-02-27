package me.leon

import kotlin.test.Test
import me.leon.ext.*
import me.leon.misc.net.*

/**
 * @author Leon
 * @since 2023-02-27 9:46
 * @email deadogone@gmail.com
 */
class IpTest {
    @Test
    fun ip() {
        println("A:")
        println("10.0.0.0".ip2Uint())
        println("10.255.255.255".ip2Uint())
        println("B:")
        println("172.16.0.0".ip2Uint())
        println("172.31.255.255".ip2Uint())

        println("C:")
        println("192.168.0.0".ip2Uint())
        println("192.168.255.255".ip2Uint())

        println("D:")
        println("224.0.0.0".ip2Uint())
        println("239.255.255.255".ip2Uint())

        println("E:")
        println("240.0.0.0".ip2Uint())
        println("255.255.255.255".ip2Uint())

        println("Loop back:")
        println("127.0.0.0".ip2Uint())
        println("127.255.255.255".ip2Uint())

        println("=====:")
        println("0.255.255.255".ip2Uint())
        println("A range:")
        println("127.255.255.255".ip2Uint())
        println("B range:")
        println("128.0.0.0".ip2Uint())
        println("191.255.255.255".ip2Uint())
        println("C range:")
        println("192.0.0.0".ip2Uint())
        println("223.255.255.255".ip2Uint())

        println("127.255.255.254".ip2Binary())
        println("127.255.255.254".ip2Binary().binary2Ip())

        println(4026531839U.toIp())
    }

    @Test
    fun mask() {

        println("43.97.0.1".cidr())
        println("43.97.0.1/25".cidr())
        arrayOf(
                "ip count",
                "mask",
                "net",
                "range",
                "broadcast",
            )
            .forEach { println(it.center(10) + ":") }
    }

    @Test
    fun ipLocation() {
        val url = "baidu.com"
        println(url.ipLocation())
    }
}
