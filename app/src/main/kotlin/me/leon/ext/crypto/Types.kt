package me.leon.ext.crypto

val encodeTypeMap = EncodeType.values().sortedBy { it.type.lowercase() }.associateBy { it.type }

fun String.encodeType() = encodeTypeMap[this] ?: EncodeType.BASE64

val classicalTypeMap =
    ClassicalCryptoType.values().sortedBy { it.type.lowercase() }.associateBy { it.type }

fun String.classicalType() = classicalTypeMap[this] ?: ClassicalCryptoType.CAESAR

val passwordHashingTypeMap =
    PasswordHashingType.values().sortedBy { it.alg.lowercase() }.associateBy { it.alg }

val passwordHashingTypes = PasswordHashingType.values().map { it.alg }.sortedBy { it.lowercase() }

fun String.passwordHashingType() = passwordHashingTypeMap[this]

val calculatorType = Calculator.values().associateBy { it.algo }

fun String.calculatorType() = calculatorType[this]
