package me.leon.img

import kotlin.test.Test
import me.leon.ext.toFile
import me.leon.toolsfx.plugin.ext.splitGif
import org.junit.Ignore
import tornadofx.*

/**
 * @author Leon
 * @since 2022-09-14 8:58
 */
class ImageTest {

    @Test
    @Ignore
    fun base64() {
        launch<Main>()
    }

    @Test
    @Ignore
    fun gifSplit() {
        "E:\\gitrepo\\ToolsFx\\art\\ctf.gif".toFile().splitGif()
    }
}
