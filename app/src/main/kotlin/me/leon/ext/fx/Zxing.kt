package me.leon.ext.fx

import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.GenericMultipleBarcodeReader
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.Hashtable
import javax.imageio.ImageIO

/** 识别二维码 */
fun File.qrReader() = ImageIO.read(this).qrReader()

/** 识别二维码 */
@Throws(IOException::class, NotFoundException::class)
fun BufferedImage.qrReader(): String {
    val formatReader = MultiFormatReader()
    // 读取指定的二维码文件
    val binaryBitmap = BinaryBitmap(HybridBinarizer(BufferedImageLuminanceSource(this)))
    // 定义二维码参数
    val hints = Hashtable<DecodeHintType, Any>()
    hints[DecodeHintType.CHARACTER_SET] = "utf-8"
    val results =
        GenericMultipleBarcodeReader(formatReader)
            .decodeMultiple(binaryBitmap)
            .sortedBy { it.resultPoints.first().x }
            .fold(StringBuilder()) { acc, result ->
                println(result.resultPoints.map { "${it.x} ${it.y}" })
                println("解析结果：$result")
                println("二维码格式类型：" + result.barcodeFormat)
                println("二维码文本内容：" + result.text)
                if (acc.isNotEmpty()) acc.appendLine()
                acc.append(result.text)
            }
            .toString()

    this.flush()
    return results
}

fun String.createQR(
    width: Int = 400,
    height: Int = 400,
    charset: String = "utf-8",
    errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.L,
    margin: Int = 1
): BufferedImage {
    val hints = Hashtable<EncodeHintType, Any>()
    hints[EncodeHintType.CHARACTER_SET] = charset
    hints[EncodeHintType.ERROR_CORRECTION] = errorCorrectionLevel
    hints[EncodeHintType.MARGIN] = margin
    val bitMatrix = MultiFormatWriter().encode(this, BarcodeFormat.QR_CODE, width, height, hints)
    return MatrixToImageWriter.toBufferedImage(bitMatrix)
}
