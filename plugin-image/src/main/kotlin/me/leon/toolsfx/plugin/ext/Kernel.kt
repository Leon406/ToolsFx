package me.leon.toolsfx.plugin.ext

import java.awt.image.Kernel

/**
 * @author Leon
 * @since 2022-12-21 14:19
 * @email deadogone@gmail.com
 */
val DEFAULT_KERNEL_5x5 = Kernel(5, 5, FloatArray(25) { -1.0f }.also { it[(it.size / 2)] = 25.0f })

val DEFAULT_KERNEL_3x3 = Kernel(3, 3, floatArrayOf(0F, -1F, 0F, -1F, 9F, -1F, 0F, -1F, 0F))
val LAPLACE_KERNEL_D4 = Kernel(3, 3, floatArrayOf(0F, 1F, 0F, 1F, -4F, 1F, 0F, 1F, 0F))

val LAPLACE_KERNEL_D8 = Kernel(3, 3, floatArrayOf(1F, 1F, 1F, 1F, -8F, 1F, 1F, 1F, 1F))

/** Laplacian of Gaussian 高斯滤波器 + Laplace */
val LOG_KERNEL =
    Kernel(
        5,
        5,
        floatArrayOf(
            0F,
            0F,
            1F,
            0F,
            0F,
            0F,
            1F,
            2F,
            1F,
            1F,
            1F,
            2F,
            -16F,
            2F,
            1F,
            0F,
            1F,
            2F,
            1F,
            0F,
            0F,
            0F,
            1F,
            0F,
            0F,
        )
    )

/** 总和为 1, 中心与边缘差 */
val SHARPEN_KERNEL = Kernel(3, 3, floatArrayOf(-1F, -1F, -1F, -1F, 9F, -1F, -1F, -1F, -1F))

/** 总和为 0 */
val EDGE_KERNEL = Kernel(3, 3, floatArrayOf(-1F, -1F, -1F, -1F, 8F, -1F, -1F, -1F, -1F))
val EDGE_SOBEL_KERNEL = Kernel(3, 3, floatArrayOf(1F, 0F, -1F, 2F, 0F, -2F, 1F, 0F, -1F))
val EDGE_SOBEL_Y_KERNEL = Kernel(3, 3, floatArrayOf(1F, 2F, 1F, 0F, 0F, 0F, -1F, -2F, -1F))

/** 总和为1, 周围加权和 */
val DENOISE_KERNEL_AVG = Kernel(3, 3, FloatArray(9) { 1F / 9F })
val DENOISE_KERNEL_GAUSSIAN =
    Kernel(
        3,
        3,
        floatArrayOf(
            1F / 16F,
            2F / 16F,
            1F / 16F,
            2F / 16F,
            4F / 16F,
            2F / 16F,
            1F / 16F,
            2F / 16F,
            1F / 16F
        )
    )
