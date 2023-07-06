package me.leon.ext.fx

import java.awt.Desktop
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.input.*
import javafx.stage.FileChooser
import javafx.stage.Window
import javax.imageio.ImageIO
import me.leon.encode.base.base64Decode

fun String.copy() =
    Clipboard.getSystemClipboard().setContent(ClipboardContent().apply { putString(this@copy) })

fun clipboardText(): String = Clipboard.getSystemClipboard().string.orEmpty()

fun clipboardImage(): Image? = Clipboard.getSystemClipboard().image

fun Image.copy() =
    Clipboard.getSystemClipboard().setContent(ClipboardContent().apply { putImage(this@copy) })

fun String.openInBrowser(): Unit = Desktop.getDesktop().browse(URL(this).toURI())

fun Image.toBufferImage(): BufferedImage = SwingFXUtils.fromFXImage(this, null)

fun BufferedImage.toFxImg(): Image = SwingFXUtils.toFXImage(this, null)

fun BufferedImage.writeFile(path: String, format: String = "png") {
    ImageIO.write(this, format, File(path))
}

fun BufferedImage.toByteArray(format: String = "png"): ByteArray =
    ByteArrayOutputStream().also { ImageIO.write(this, format, it) }.toByteArray()

fun Window.fileChooser(hint: String = "请选择文件"): File? =
    FileChooser().apply { title = hint }.showOpenDialog(this)

fun Window.multiFileChooser(hint: String = "请选择多个文件"): List<File>? =
    FileChooser().apply { title = hint }.showOpenMultipleDialog(this)

fun fileDraggedHandler(block: (List<File>) -> Unit) =
    EventHandler<DragEvent> {
        println("${it.dragboard.hasFiles()}______" + it.eventType)
        if (it.eventType.name == "DRAG_ENTERED" && it.dragboard.hasFiles()) {
            block.invoke(it.dragboard.files)
        }
    }

fun String.base64Image() = Image(base64Decode().inputStream())

fun ByteArray.toImage() = Image(inputStream())

fun File.toImage() = Image(inputStream())

fun runOnUi(action: () -> Unit) {
    Platform.runLater { action() }
}
