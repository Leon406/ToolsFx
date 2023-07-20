package me.leon.misc

import java.io.File
import kotlin.system.measureNanoTime
import kotlin.test.Test
import kotlin.test.assertEquals
import me.leon.misc.zhconvert.CONFIG
import me.leon.misc.zhconvert.convert

/**
 * @author Leon
 * @since 2023-07-14 14:21
 * @email deadogone@gmail.com
 */
val TEST_PRJ_DIR: File = File("").absoluteFile.parentFile
val TEST_DATA_DIR = File(TEST_PRJ_DIR, "testdata/misc/zhconvert")

class SimpleChineseTraditionTest {
    private val origin = File(TEST_DATA_DIR, "origin.txt").readText()
    private val tw = File(TEST_DATA_DIR, "traditionTw.txt").readText()
    private val hk = File(TEST_DATA_DIR, "traditionHk.txt").readText()
    private val tradition = File(TEST_DATA_DIR, "tradition.txt").readText()

    @Test
    fun simple2Tradition() {

        var config = CONFIG.first { it.name == "s2t" }
        assertEquals(tradition, config.convert(origin))

        config = CONFIG.first { it.name == "s2tw" }
        assertEquals(tw, config.convert(origin))

        config = CONFIG.first { it.name == "s2hk" }
        assertEquals(hk, config.convert(origin))
    }

    @Test
    fun tradition2Simple() {
        val beforePart = "具体有三类情况："
        var config = CONFIG.first { it.name == "t2s" }
        val origin = origin.substringBefore(beforePart)
        assertEquals(origin, config.convert(tradition).substringBefore(beforePart))

        config = CONFIG.first { it.name == "tw2s" }
        assertEquals(origin, config.convert(tw).substringBefore(beforePart))

        config = CONFIG.first { it.name == "hk2s" }
        assertEquals(origin, config.convert(hk).substringBefore(beforePart))
    }

    @Test
    fun convertNormal() {
        val tradition =
            "雖然之前來過日本旅遊兩次，但是理解都浮於表面，" +
                "只是驚歎於日本街道的乾淨和優良的秩序。" +
                "哪怕現在我也很難說我對日本有多麼深刻的認識，只是浸淫在這樣的文化中，體驗更加真實。" +
                "日本是我生活的第五個國家（前四個是中國、英國、瑞士、美國），也是第一個中國以外的東方國家。"
        val simple =
            "虽然之前来过日本旅游两次，但是理解都浮于表面，只是惊叹于日本街道的干净和优良的秩序。" +
                "哪怕现在我也很难说我对日本有多么深刻的认识，只是浸淫在这样的文化中，体验更加真实。" +
                "日本是我生活的第五个国家（前四个是中国、英国、瑞士、美国），也是第一个中国以外的东方国家。"

        var config = CONFIG.first { it.name == "t2s" }
        assertEquals(simple, config.convert(tradition))
        println(config.convert("乾坤"))
        println(config.convert("「日曆」作「曆」，與歷史有關用「歷」"))
        config = CONFIG.first { it.name == "s2t" }
        assertEquals(tradition, config.convert(simple))
    }

    @Test
    fun convertTw() {
        val tradition =
            "雖然之前來過日本旅遊兩次，但是理解都浮於表面，" +
                "只是驚歎於日本街道的乾淨和優良的秩序。" +
                "哪怕現在我也很難說我對日本有多麼深刻的認識，只是浸淫在這樣的文化中，體驗更加真實。" +
                "日本是我生活的第五個國家（前四個是中國、英國、瑞士、美國），也是第一個中國以外的東方國家。"
        val simple =
            "虽然之前来过日本旅游两次，但是理解都浮于表面，只是惊叹于日本街道的干净和优良的秩序。" +
                "哪怕现在我也很难说我对日本有多么深刻的认识，只是浸淫在这样的文化中，体验更加真实。" +
                "日本是我生活的第五个国家（前四个是中国、英国、瑞士、美国），也是第一个中国以外的东方国家。"

        var config = CONFIG.first { it.name == "tw2s" }
        assertEquals(simple, config.convert(tradition))

        val phrase = "程式伺服器"
        val phraseSimple = "程序服务器"
        config = CONFIG.first { it.name == "s2tw" }
        assertEquals(tradition, config.convert(simple))
        println(config.convert(phraseSimple))

        config = CONFIG.first { it.name == "s2twp" }
        assertEquals(phrase, config.convert(phraseSimple))
    }

    @Test
    fun convertHk() {
        val simple = "群峰"
        val hk = "羣峯"

        var config = CONFIG.first { it.name == "s2hk" }
        assertEquals(hk, config.convert(simple))

        println(config.convert("皇后和后宫"))
        config = CONFIG.first { it.name == "hk2s" }
        assertEquals(simple, config.convert(hk))

        config = CONFIG.first { it.name == "hk2t" }
        println(config.convert("唇裏"))
    }

    @Test
    fun convertJp() {
        val old = "壓邊國號變步缺罐屍篰"
        val newHanzi = "圧辺国号変歩欠缶死部"

        var config = CONFIG.first { it.name == "jp2t" }
        assertEquals(old, config.convert(newHanzi))

        config = CONFIG.first { it.name == "t2jp" }
        assertEquals(newHanzi, config.convert(old))
    }

    @Test
    fun convert() {
        val simple =
            "虽然之前来过日本旅游两次，但是理解都浮于表面，只是惊叹于日本街道的干净和优良的秩序。" +
                "哪怕现在我也很难说我对日本有多么深刻的认识，只是浸淫在这样的文化中，体验更加真实。" +
                "日本是我生活的第五个国家（前四个是中国、英国、瑞士、美国），也是第一个中国以外的东方国家。"
        val config = CONFIG.first { it.name == "s2t" }

        val count = 1000
        var sum = 0L
        // warm up
        repeat(count) { config.convert(simple) }
        repeat(count) { measureNanoTime { config.convert(simple) }.also { sum += it } }
        println(sum / count)
    }
}
