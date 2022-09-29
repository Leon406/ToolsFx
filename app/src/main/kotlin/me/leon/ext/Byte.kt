package me.leon.ext

infix fun Byte.xor(other: Byte): Byte = xor(other.toInt())

infix fun Byte.xor(other: Int): Byte = toInt().xor(other).toByte()

infix fun Byte.and(other: Byte): Byte = and(other.toInt())

infix fun Byte.and(other: Int): Byte = toInt().and(other).toByte()

infix fun Byte.or(other: Byte): Byte = or(other.toInt())

infix fun Byte.or(other: Int): Byte = toInt().or(other).toByte()
