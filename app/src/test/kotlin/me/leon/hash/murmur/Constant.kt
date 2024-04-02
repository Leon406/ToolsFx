package me.leon.hash.murmur

interface Constant {

    companion object {
        /** Helps convert a byte into its unsigned value */
        const val UNSIGNED_MASK: Long = 0xff

        /** Helps convert integer to its unsigned value */
        const val UINT_MASK: Long = 0xFFFFFFFFL

        /** Helps convert long to its unsigned value */
        const val LONG_MASK: Long = -0x1L
    }
}
