package me.leon.toolsfx.plugin.ext

/**
 * @author Leon
 * @since 2022-12-23 16:42
 * @email deadogone@gmail.com
 */
enum class ImageOperation : SimpleService {
    GRAY {
        override fun process(file: String, params: Map<String, String>) =
            file.autoConvertToBufferImage()!!.gray()
    },
    BINARY {
        override fun process(file: String, params: Map<String, String>) =
            with(file.autoConvertToBufferImage()!!) { binary(ostu()) }
    },
    INVERSE {
        override fun process(file: String, params: Map<String, String>) =
            file.autoConvertToBufferImage()!!.inverse()
    },
    MIRROR_HEIGHT {
        override fun process(file: String, params: Map<String, String>) =
            file.autoConvertToBufferImage()!!.mirrorHeight()
    },
    MIRROR_WIDTH {
        override fun process(file: String, params: Map<String, String>) =
            file.autoConvertToBufferImage()!!.mirrorWidth()
    },
    MOSAIC {
        override fun process(file: String, params: Map<String, String>) =
            file.autoConvertToBufferImage()!!.mosaic()
    },
    OIL_PAINT {
        override fun process(file: String, params: Map<String, String>) =
            file.autoConvertToBufferImage()!!.oilPaint()
    },
}
