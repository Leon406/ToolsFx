package me.leon.toolsfx.plugin.ext

import me.leon.encode.base.base64
import me.leon.ext.fx.base64Image
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
    IMAGE_TO_BASE64("ImgToBase64") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.properByteArray(isFile).base64()
    },
    BINARY_IMAGE("binary image") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.properString(isFile).binaryImage()
    },
    BINARY_QR_IMAGE("binaryQR image") {
        override fun process(raw: String, isFile: Boolean, params: Map<String, String>) =
            raw.properString(isFile).binaryImage(false)
    },
    RGB_IMAGE("rgb image") {
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
}

val serviceTypeMap = ImageServiceType.values().associateBy { it.type }

fun String.locationServiceType() = serviceTypeMap[this] ?: ImageServiceType.FIX_PNG
