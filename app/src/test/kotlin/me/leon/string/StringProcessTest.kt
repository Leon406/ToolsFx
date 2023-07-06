package me.leon.string

import kotlin.test.Test
import me.leon.ext.splitParagraph

/**
 * @author Leon
 * @since 2023-07-06 10:03
 * @email deadogone@gmail.com
 */
class StringProcessTest {
    @Test
    fun sentence() {
        val paragraph =
            "Another local firm, Guizhou Guisanhong Food Co., Ltd., " +
                "integrates chili cultivation, product research and development, " +
                "production processing," +
                " and sales. The company has built an extensive chili production network," +
                " helping nearly 200,000 chili farmers to boost their income through chili planting."

        println(paragraph.splitParagraph())
    }
}
