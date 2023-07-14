package me.leon.misc

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Leon
 * @since 2023-07-14 14:21
 * @email deadogone@gmail.com
 */
class SimpleChineseTraditionTest {
    @Test
    fun tradition2Simple() {
        val text = "生命不息，奮鬥不止"
        val target = "生命不息，奋斗不止"
        assertEquals(target, ZhConvert.convert(text, ZhConvert.ZhType.TW2S))
        println(ZhConvert.convert("為什麼嘴唇裂了圜滑鼠裡面的矽二極體壞了，導致游標解析度降低。", ZhConvert.ZhType.TW2S))
        println(ZhConvert.convert("出租车", ZhConvert.ZhType.S2TW))
        println(ZhConvert.convert("里面", ZhConvert.ZhType.S2TW))
        println(ZhConvert.convert("出租车", ZhConvert.ZhType.S2HK))
        println(ZhConvert.convert("為什麼嘴唇裂了"))
    }

    @Test
    fun simple2Tw() {
        var simple =
            "猪八戒吃人参果这斜月三星洞…… 长寿面，孙悟空，猪八戒，唐僧，沙和尚，白龙马，李靖，托塔天王, " + "戏说西游，许多人都这样说，收拾一下，拾金不昧；纔=才"
        println(ZhConvert.convert(simple, ZhConvert.ZhType.S2T))
        println(ZhConvert.convert(simple, ZhConvert.ZhType.S2TW))
        println(ZhConvert.convert(simple, ZhConvert.ZhType.S2HK))

        simple = "服务器程序"
        println(ZhConvert.convert(simple, ZhConvert.ZhType.S2T))
        println(ZhConvert.convert(simple, ZhConvert.ZhType.S2TW))
        println(ZhConvert.convert(simple, ZhConvert.ZhType.S2HK))
    }
}
