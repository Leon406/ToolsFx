package me.leon.plugin

import kotlin.test.Test
import me.leon.toolsfx.plugin.methodParse
import me.leon.toolsfx.plugin.replacePlaceHolders

class PlaceHolderTest {
    @Test
    fun holderTest() {
        val d1 = "Adbsd{{base64({{md5({{md5(23123-1231)}})}})}}"
        println(d1.methodParse())
        val d2 = "Adbsd{{base64({{digest(SHA3-256,6666dd)}})}}"
        println(d2.methodParse())
        val d3 = " Adbsd{{uuid}}  {{lowercase(dsaf123DSDf)}}"
        println(d3.replacePlaceHolders().methodParse())
        val d4 = " {{base64(21321)}}"
        println(d4.replacePlaceHolders().methodParse())

        val d5 = " {{datetime2Mills(2019-01-01 00:00:00)}}"
        println(d5.replacePlaceHolders().methodParse())
        val d6 = " {{date2Mills(2019-01-01)}}"
        println(d6.replacePlaceHolders().methodParse())
    }
}
