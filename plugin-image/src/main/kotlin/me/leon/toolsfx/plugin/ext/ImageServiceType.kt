package me.leon.toolsfx.plugin.ext

import javafx.scene.image.Image
import me.leon.C1
import me.leon.P1
import me.leon.encode.base.base64
import me.leon.ext.fx.base64Image
import me.leon.ext.hex2ByteArray
import me.leon.ext.toFile

enum class ImageServiceType(val type: String) : ImageService {
    FIX_PNG("fix png") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.properByteArray(isFile).fixPng()
    },
    BASE64_IMG("base64ToImg") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.properString(isFile).base64Image()
    },
    HEX_IMG("hexToImg") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            Image(raw.properString(isFile).hex2ByteArray().inputStream())
    },
    IMAGE_TO_BASE64("ImgToBase64") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.properByteArray(isFile).base64()
    },
    ZERO_ONE_QR_IMAGE("01 to QR image") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.properString(isFile)
                .zeroOneImage(false, requireNotNull(params[C1]) == ColorMode.BLACK1.toString())
    },
    ZERO_ONE_IMAGE("01 to image") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.properString(isFile)
                .zeroOneImage(
                    isBlackOne = requireNotNull(params[C1]) == ColorMode.BLACK1.toString()
                )
    },
    IMAGE_TO_01("image to 01") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.toFile()
                .toBufferImage()
                .toBinaryString(
                    isBlackOne = requireNotNull(params[C1]) == ColorMode.BLACK1.toString()
                )
    },
    RGB_IMAGE("rgb to image") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.properString(isFile).rgb()
    },
    GIF_SPLIT("gif split") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.toFile().splitGif()
    },
    WECHAT("wechat image") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.toFile().weChatDecrypt()
    },
    IMAGE_PROCESS("image process") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            ImageOperation.valueOf(requireNotNull(params[C1])).process(raw, params)
    },
    MORPHOLOGY("morphology") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            ImageMorphology.valueOf(requireNotNull(params[C1])).process(raw, params)
    },
    SCALE("scale") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.autoConvertToBufferImage()?.scale(requireNotNull(params[P1]).toDouble())
                ?: error("wrong parameter!!")
    };

    override fun options(): Array<out String> = IMAGE_CONFIG[this]?.get(OPTIONS).orEmpty()

    override fun paramsHints(): Array<out String> = IMAGE_CONFIG[this]?.get(HINT).orEmpty()
}

val serviceTypeMap = ImageServiceType.values().associateBy { it.type }

fun String.locationServiceType() = serviceTypeMap[this] ?: ImageServiceType.FIX_PNG
