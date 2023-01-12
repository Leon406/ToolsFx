package me.leon.img

import java.io.File
import kotlin.test.Test
import me.leon.encode.base.base64
import me.leon.ext.fx.*
import me.leon.ext.toFile
import me.leon.toolsfx.plugin.ext.*
import org.junit.Ignore
import tornadofx.*

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

    @Test
    fun imageScale() {
        ("iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAABKElEQVR4XnVRsU7DMBC9thJCLExt7IiqBDHxBUxM/EIEY5FQ7BQkGBjY+" +
                "g2M7IifYED8A8zdqGCAtnZiioTMnYWj68CTLonfvXd3OQMwzMreoVHizSrh8e0XSphKyVsP0OK6gLoU3yRyWt754fY6Ra" +
                "3TYzQT/+POskEjXgaxnDL/ClwpH6hYOBjVP2gOiHmZ7Fda+DqEvI88dppgPAHNTGPERMXMRiWfH8XOZjBcZEkoTA+aN4oi8Iev" +
                "MDfj3L8Gn0MHOznOEaJhUav0iCeex3trr0W6wTlabTBYLW9odSvJPO9YLV44Z4vkMnT1HlqW9oyr4wI/hnb8/hpt7VJ1o5NeIN" +
                "zpIPu72cn7sCsaE0B7rtNzyi1H8jryDXCMx1CJBY0xPRHdqPkFhf6jOYjRDuoAAAAASUVORK5CYII=")
            .base64Image()
            .toBufferImage()
            .scale(0.3)
            .toByteArray()
            .base64()
            .also { println(it) }
    }
}
