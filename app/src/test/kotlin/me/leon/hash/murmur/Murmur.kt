package me.leon.hash.murmur

/** from https://github.com/c0x12c/kmurmur */
enum class Murmur {
    V1 {
        override fun hash(data: ByteArray, length: Int, seed: Long): Long {
            return One.hash(data, length, seed)
        }
    },
    V2 {
        override fun hash(data: ByteArray, length: Int, seed: Long): Long {
            return Two.hash(data, length, seed)
        }
    },
    V3 {
        override fun hash(data: ByteArray, length: Int, seed: Long): Long {
            return Three.hash86b32(data, length, seed)
        }
    };

    abstract fun hash(data: ByteArray, length: Int, seed: Long): Long
}
