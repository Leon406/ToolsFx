package me.leon.encode.base

import me.leon.hash

val base58HashFunc = { bytes: ByteArray -> bytes.hash("SHA-256").hash("SHA-256") }

fun ByteArray.baseCheck(
    maps: String = BASE58_DICT,
    hashFuc: (ByteArray) -> ByteArray = base58HashFunc,
    hashSize: Int = 4
): String {
    val b2 = ByteArray(size + hashSize)
    val hash = hashFuc.invoke(this)
    System.arraycopy(this, 0, b2, 0, size)
    System.arraycopy(hash, 0, b2, size, hashSize)
    return b2.radixNEncode(maps)
}

fun String.baseCheckDecode(
    maps: String = BASE58_DICT,
    hashFuc: (ByteArray) -> ByteArray = base58HashFunc,
    hashSize: Int = 4
): ByteArray =
    with(radixNDecode(maps)) {
        val value = copyOfRange(0, size - hashSize)
        val checksum = copyOfRange(size - hashSize, size)
        val expectedChecksum = hashFuc.invoke(value).copyOfRange(0, hashSize)
        if (checksum.contentEquals(expectedChecksum)) value else byteArrayOf()
    }

fun String.baseCheckDecode2String(
    maps: String = BASE58_DICT,
    hashFuc: (ByteArray) -> ByteArray = base58HashFunc,
    hashSize: Int = 4
) = String(baseCheckDecode(maps, hashFuc, hashSize))
