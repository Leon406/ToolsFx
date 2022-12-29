package me.leon.toolsfx.plugin.ext

/**
 * @author Leon
 * @since 2022-12-23 15:27
 * @email deadogone@gmail.com
 */
const val OPTIONS = "options"
const val HINT = "hint"
val IMAGE_CONFIG =
    mapOf(
        ImageServiceType.ZERO_ONE_QR_IMAGE to
            mapOf(OPTIONS to arrayOf(ColorMode.BLACK1.toString(), ColorMode.WHITE1.toString())),
        ImageServiceType.ZERO_ONE_IMAGE to
            mapOf(OPTIONS to arrayOf(ColorMode.BLACK1.toString(), ColorMode.WHITE1.toString())),
        ImageServiceType.IMAGE_TO_01 to
            mapOf(OPTIONS to arrayOf(ColorMode.WHITE1.toString(), ColorMode.BLACK1.toString())),
        ImageServiceType.IMAGE_PROCESS to
            mapOf(OPTIONS to ImageOperation.values().map { it.toString() }.toTypedArray()),
        ImageServiceType.MORPHOLOGY to
            mapOf(
                HINT to arrayOf("kernel size default is 3"),
                OPTIONS to ImageMorphology.values().map { it.toString() }.toTypedArray()
            ),
        ImageServiceType.SCALE to mapOf(HINT to arrayOf("scale ratio"))
    )
