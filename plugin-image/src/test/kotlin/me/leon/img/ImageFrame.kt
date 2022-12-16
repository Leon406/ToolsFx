package me.leon.img

import java.awt.image.BufferedImage

data class ImageFrame(
    val image: BufferedImage?,
    val delay: Int,
    val disposal: String?,
    val width: Int,
    val height: Int
)
