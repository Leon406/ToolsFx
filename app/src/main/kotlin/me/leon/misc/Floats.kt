package me.leon.misc

import java.lang.Long

// (-1)^符号位 × (1.尾数位) × 2^(指数位-127)
// 1 sign  2-9  exp  10-32 mantissa
fun Float.ieee754() =
    Integer.toBinaryString(toRawBits()).padStart(32, '0').chunked(8).joinToString(" ")

fun Float.ieee754Hex() = Integer.toHexString(toRawBits()).padStart(8, '0').uppercase()

fun Float.ieee754LE() = ieee754().split(" ").reversed().joinToString(" ")

fun Float.ieee754HexLE() = ieee754Hex().chunked(2).reversed().joinToString("")

fun Float.ieee754Readable() =
    with(ieee754().replace(" ", "")) { "${first()} ${substring(1..8)} ${substring(9)}" }

// (-1)^符号位 × (1.尾数位) × 2^(指数位-1023)
// 1 sign  2-12  exp  13-64 mantissa
fun Double.ieee754() =
    Long.toBinaryString(toRawBits()).padStart(64, '0').chunked(8).joinToString(" ")

fun Double.ieee754LE() = ieee754().split(" ").reversed().joinToString(" ")

fun Double.ieee754Hex() = Long.toHexString(toRawBits()).padStart(16, '0').uppercase()

fun Double.ieee754HexLE() = ieee754Hex().chunked(2).reversed().joinToString("")

fun Double.ieee754Readable() =
    with(ieee754().replace(" ", "")) { "${first()} ${substring(1..11)} ${substring(12)}" }

enum class Floats : Transfer {
    FLOAT_32 {
        override fun convert(s: String) = s.toFloatOrNull()?.ieee754().orEmpty()
    },
    FLOAT_32_READABLE {
        override fun convert(s: String) = s.toFloatOrNull()?.ieee754Readable().orEmpty()
    },
    FLOAT_32_LE {
        override fun convert(s: String) = s.toFloatOrNull()?.ieee754LE().orEmpty()
    },
    FLOAT_32_HEX {
        override fun convert(s: String) = s.toFloatOrNull()?.ieee754Hex().orEmpty()
    },
    FLOAT_32_HEX_LE {
        override fun convert(s: String) = s.toFloatOrNull()?.ieee754HexLE().orEmpty()
    },
    DOUBLE_64 {
        override fun convert(s: String) = s.toDoubleOrNull()?.ieee754().orEmpty()
    },
    DOUBLE_64_READABLE {
        override fun convert(s: String) = s.toDoubleOrNull()?.ieee754Readable().orEmpty()
    },
    DOUBLE_64_LE {
        override fun convert(s: String) = s.toDoubleOrNull()?.ieee754LE().orEmpty()
    },
    DOUBLE_64_HEX {
        override fun convert(s: String) = s.toDoubleOrNull()?.ieee754Hex().orEmpty()
    },
    DOUBLE_64_HEX_LE {
        override fun convert(s: String) = s.toDoubleOrNull()?.ieee754HexLE().orEmpty()
    },
}
