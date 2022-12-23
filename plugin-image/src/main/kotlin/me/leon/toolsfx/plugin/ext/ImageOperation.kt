package me.leon.toolsfx.plugin.ext

import me.leon.ext.toFile

/**
 * @author Leon
 * @since 2022-12-23 16:42
 * @email deadogone@gmail.com
 */
enum class ImageOperation : SimpleService {
    GRAY {
        override fun process(file: String, params: Map<String, String>) =
            file.toFile().toBufferImage().gray()
    },
    BINARY {
        override fun process(file: String, params: Map<String, String>) =
            with(file.toFile().toBufferImage()) { binary(ostu()) }
    },
    INVERSE {
        override fun process(file: String, params: Map<String, String>) =
            file.toFile().toBufferImage().inverse()
    },
    MIRROR_HEIGHT {
        override fun process(file: String, params: Map<String, String>) =
            file.toFile().toBufferImage().mirrorHeight()
    },
    MIRROR_WIDTH {
        override fun process(file: String, params: Map<String, String>) =
            file.toFile().toBufferImage().mirrorWidth()
    },
    MOSAIC {
        override fun process(file: String, params: Map<String, String>) =
            file.toFile().toBufferImage().mosaic()
    },
    OIL_PAINT {
        override fun process(file: String, params: Map<String, String>) =
            file.toFile().toBufferImage().oilPaint()
    }
}
