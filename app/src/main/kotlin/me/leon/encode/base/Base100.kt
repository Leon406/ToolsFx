package me.leon.encode.base

const val BASE100_BYTE1 = 0xf0.toByte()
const val BASE100_BYTE2 = 0x9f.toByte()

fun ByteArray.base100() =
    String(
        foldIndexed(ByteArray(size * 4)) { index, acc, byte ->
            acc.apply {
                acc[index * 4] = BASE100_BYTE1
                acc[index * 4 + 1] = BASE100_BYTE2
                acc[index * 4 + 2] = (((byte.toInt() and 0xFFFF) + 55) / 64 + 143).toByte()
                acc[index * 4 + 3] = (((byte.toInt() and 0xFF) + 55) % 64 + 128).toByte()
            }
        }
    )

fun String.base100Decode() =
    with(toByteArray()) {
        asIterable()
            .chunked(4)
            .filter { it.first() == BASE100_BYTE1 && it[1] == BASE100_BYTE2 }
            .foldIndexed(ByteArray(this.size / 4)) { index, acc, list ->
                acc.apply { acc[index] = ((list[2] - 143) * 64 + list[3] - 128 - 55).toByte() }
            }
    }

fun String.base100Decode2String() = String(base100Decode())
