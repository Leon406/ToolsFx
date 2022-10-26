package me.leon.music.qmc

import java.io.File

class QMCDecode {
    private var x = -1
    private var y = 8
    private var dx = 1
    private var index = -1
    private val seedMap =
        arrayOf(
            intArrayOf(0x4a, 0xd6, 0xca, 0x90, 0x67, 0xf7, 0x52),
            intArrayOf(0x5e, 0x95, 0x23, 0x9f, 0x13, 0x11, 0x7e),
            intArrayOf(0x47, 0x74, 0x3d, 0x90, 0xaa, 0x3f, 0x51),
            intArrayOf(0xc6, 0x09, 0xd5, 0x9f, 0xfa, 0x66, 0xf9),
            intArrayOf(0xf3, 0xd6, 0xa1, 0x90, 0xa0, 0xf7, 0xf0),
            intArrayOf(0x1d, 0x95, 0xde, 0x9f, 0x84, 0x11, 0xf4),
            intArrayOf(0x0e, 0x74, 0xbb, 0x90, 0xbc, 0x3f, 0x92),
            intArrayOf(0x00, 0x09, 0x5b, 0x9f, 0x62, 0x66, 0xa1)
        )

    fun nextMask(): Int {
        val ret: Int
        index++
        if (x < 0) {
            dx = 1
            y = (8 - y) % 8
            ret = 0xc3
        } else if (x > 6) {
            dx = -1
            y = 7 - y
            ret = 0xd8
        } else {
            ret = seedMap[y][x]
        }
        x += dx
        return if (index == 0x8000 || (index > 0x8000 && (index + 1) % 0x8000 == 0)) {
            nextMask()
        } else {
            ret
        }
    }
}

fun File.qmcDecode() {
    val qmcDecode = QMCDecode()
    val outputFile =
        File(
            parentFile,
            name
                .replace("qmc[03]".toRegex(), "mp3")
                .replace("qmc[2468]".toRegex(), "m4a")
                // qmcogg qmcflac
                .replace(".qmc", ".")
        )
    outputFile.outputStream().use { output ->
        output.write(
            inputStream()
                .readBytes()
                .map { (it.toInt() xor qmcDecode.nextMask()).toByte() }
                .toByteArray()
        )
    }
}
