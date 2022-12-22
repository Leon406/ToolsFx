package me.leon.img

import java.io.File
import kotlin.test.Test
import me.leon.ext.toFile
import me.leon.toolsfx.plugin.ext.splitGif
import me.leon.toolsfx.plugin.ext.weChatXorKey
import org.junit.Ignore
import tornadofx.launch

/**
 * @author Leon
 * @since 2022-09-14 8:58
 */
class ImageTest {

    @Test
    fun base64() {
        launch<Main>()
    }

    @Test
    @Ignore
    fun gifSplit() {
        "E:\\gitrepo\\ToolsFx\\art\\ctf.gif".toFile().splitGif()
    }

    @Test
    @Ignore
    fun readWechat() {
        val dir = "wechat image dir"

        dir.toFile()
            .walk()
            .maxDepth(1)
            .filter { it.extension == "dat" }
            .forEach {
                println(it.absolutePath)
                decryptFile(it)
            }
    }

    private fun decryptFile(it: File) {
        weChatXorKey(it).run {
            it.inputStream().use { input ->
                File(it.parentFile.absolutePath, it.nameWithoutExtension + ".${this.second}")
                    .outputStream()
                    .use { output ->
                        output.write(
                            input
                                .readBytes()
                                .map { (it.toInt() xor this.first.toInt()).toByte() }
                                .toByteArray()
                        )
                    }
            }
        }
    }
}
