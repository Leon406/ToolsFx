package me.leon.classical

/**
 * ported from
 * https://github.com/gchq/CyberChef/blob/master/src/core/operations/CitrixCTX1Encode.mjs
 * https://github.com/gchq/CyberChef/blob/master/src/core/operations/CitrixCTX1Decode.mjs
 *
 * @author Leon
 * @since 2023-02-14 10:12
 * @email deadogone@gmail.com
 */
fun String.citrixCtx1(): String {
    var tmp = 0
    return toByteArray(Charsets.UTF_16LE)
        .fold(mutableListOf<Byte>()) { acc, byte ->
            acc.apply {
                tmp = byte.toInt() xor 0xa5 xor tmp
                val high = (tmp ushr 4 and 0xf) + 0x41
                val low = (tmp and 0xf) + 0x41
                add(high.toByte())
                add(low.toByte())
            }
        }
        .toByteArray()
        .decodeToString()
}

fun String.citrixCtx1Decode(): String {
    require(length % 4 == 0)

    var tmp: Int

    val byteRev = toByteArray().reversed()
    val result = mutableListOf<Byte>()

    for (i in byteRev.indices step 2) {
        tmp =
            if (i + 2 >= byteRev.size) {
                0
            } else {
                ((byteRev[i + 2] - 0x41) and 0xf) xor (((byteRev[i + 3] - 0x41) shl 4) and 0xf0)
            }
        tmp =
            (((byteRev[i] - 0x41) and 0xf) xor (((byteRev[i + 1] - 0x41) shl 4) and 0xf0)) xor
                0xa5 xor
                tmp
        result.add(tmp.toByte())
    }
    return result.reversed().toByteArray().toString(Charsets.UTF_16LE)
}
