package me.leon.toolsfx.plugin.ext

import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.metadata.IIOMetadataNode

/**
 * https://stackoverflow.com/questions/8933893/convert-each-animated-gif-frame-to-a-separate-bufferedimage
 */
fun File.splitGif(): String {
    val outputDir = File(parent, nameWithoutExtension)
    outputDir.mkdirs()
    val reader =
        ImageIO.getImageReadersByFormatName("gif").next().apply {
            input = ImageIO.createImageInputStream(inputStream())
        }

    var size = Rectangle()

    val metadata = reader.streamMetadata
    var backgroundColor: Color? = null
    if (metadata != null) {
        val globalRoot = metadata.getAsTree(metadata.nativeMetadataFormatName) as IIOMetadataNode
        size = globalRoot.parseSize()
        backgroundColor = globalRoot.parseBackground()
    }
    var masterImage: BufferedImage? = null
    var frameIndex = 0

    while (true) {
        val image: BufferedImage =
            try {
                reader.read(frameIndex)
            } catch (ignored: Exception) {
                break
            }

        if (size.isEmpty) {
            size.width = image.width
            size.height = image.height
        }
        val root =
            reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0")
                as IIOMetadataNode
        val children = root.childNodes
        if (masterImage == null) {
            masterImage =
                BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB).also {
                    it.createGraphics().apply {
                        color = backgroundColor
                        fillRect(0, 0, size.width, size.height)
                        drawImage(image, 0, 0, null)
                    }
                }
        } else {
            val currentPoint = Point()
            for (nodeIndex in 0 until children.length) {
                val nodeItem = children.item(nodeIndex)
                if (nodeItem.nodeName.equals("ImageDescriptor")) {
                    val map = nodeItem.attributes
                    currentPoint.x =
                        Integer.valueOf(map.getNamedItem("imageLeftPosition").nodeValue)
                    currentPoint.y = Integer.valueOf(map.getNamedItem("imageTopPosition").nodeValue)
                    break
                }
            }
            masterImage.createGraphics()?.drawImage(image, currentPoint.x, currentPoint.y, null)
            ImageIO.write(masterImage, "PNG", File(outputDir, "$frameIndex.png"))
        }
        masterImage.flush()
        frameIndex++
    }

    reader.dispose()
    return "saved to ${outputDir.absolutePath}"
}

private fun IIOMetadataNode.parseBackground(backgroundColor: Color? = null): Color? {
    var tmp = backgroundColor
    val globalColorTable = getElementsByTagName("GlobalColorTable")
    if (globalColorTable.length > 0) {
        val colorTable = globalColorTable.item(0) as IIOMetadataNode?
        if (colorTable != null) {
            val bgIndex = colorTable.getAttribute("backgroundColorIndex")
            var colorEntry = colorTable.firstChild as IIOMetadataNode?
            while (colorEntry != null) {
                if (colorEntry.getAttribute("index") == bgIndex) {
                    tmp = colorEntry.color()
                    break
                }
                colorEntry = colorEntry.nextSibling as IIOMetadataNode
            }
        }
    }
    return tmp
}

private fun IIOMetadataNode.parseSize(): Rectangle {
    var rect = Rectangle()
    val globalScreeDescriptor = getElementsByTagName("LogicalScreenDescriptor")

    if (globalScreeDescriptor.length > 0) {
        val screenDescriptor = globalScreeDescriptor.item(0) as IIOMetadataNode
        rect =
            Rectangle(
                screenDescriptor.getAttribute("logicalScreenWidth").toInt(),
                screenDescriptor.getAttribute("logicalScreenHeight").toInt()
            )
    }
    return rect
}

private fun IIOMetadataNode.color() =
    Color(getAttribute("red").toInt(), getAttribute("green").toInt(), getAttribute("blue").toInt())
