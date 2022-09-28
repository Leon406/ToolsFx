package me.leon.music

object ByteUtil {
    /** 返回一个从0到255填充的byte[] */
    @JvmStatic
    fun fillByteArr(): ByteArray {
        return ByteArray(256) { it.toByte() }
    }

    /** 将长度为4的byte数组转为int类型 */
    fun bytes2Int(byteNum: ByteArray): Int {
        var num = 0
        for (b in byteNum) {
            num = num shl 8 or (b.toInt() and 0xff)
        }
        return num
    }

    /** 作用和python的stack.unpack('<1',bytes([1,2,3,4]))方法一致 */
    @JvmStatic
    fun unpack(bytes: ByteArray): Int {
        bytes.reverse()
        return bytes2Int(bytes)
    }
}
