package me.leon.encode.base

import me.leon.Digests

val base58HashFunc = { bytes: ByteArray -> Digests.hash("SHA-256", Digests.hash("SHA-256", bytes)) }

fun ByteArray.baseCheck(
    radix: Int = 58,
    maps: String = BASE58_DICT,
    hashFuc: (ByteArray) -> ByteArray = base58HashFunc,
    hashSize: Int = 4
): String {
    val b2 = ByteArray(size + hashSize)
    val hash = hashFuc.invoke(this)
    System.arraycopy(this, 0, b2, 0, size)
    System.arraycopy(hash, 0, b2, size, hashSize)
    return b2.baseNEncode(radix, maps)
}

fun String.baseCheckDecode(
    radix: Int = 58,
    maps: String = BASE58_DICT,
    hashFuc: (ByteArray) -> ByteArray = base58HashFunc,
    hashSize: Int = 4
): ByteArray =
    with(baseNDecode(radix, maps)) {
        val value = copyOfRange(0, size - hashSize)
        val checksum = copyOfRange(size - hashSize, size)
        val expectedChecksum = hashFuc.invoke(value).copyOfRange(0, hashSize)
        if (checksum.contentEquals(expectedChecksum)) value else byteArrayOf()
    }

fun String.baseCheckDecode2String(
    radix: Int = 58,
    maps: String = BASE58_DICT,
    hashFuc: (ByteArray) -> ByteArray = base58HashFunc,
    hashSize: Int = 4
) = String(baseCheckDecode(radix, maps, hashFuc, hashSize))
