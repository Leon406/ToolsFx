package me.leon.ext.crypto

val encodeTypeMap = EncodeType.entries.sortedBy { it.type.lowercase() }.associateBy { it.type }

fun String.encodeType() = encodeTypeMap[this] ?: EncodeType.BASE64

val classicalTypeMap =
    ClassicalCryptoType.entries.sortedBy { it.type.lowercase() }.associateBy { it.type }

fun String.classicalType() = classicalTypeMap[this] ?: ClassicalCryptoType.CAESAR

val passwordHashingTypeMap =
    PasswordHashingType.entries.sortedBy { it.alg.lowercase() }.associateBy { it.alg }

val passwordHashingTypes = PasswordHashingType.entries.map { it.alg }.sortedBy { it.lowercase() }

fun String.passwordHashingType() = passwordHashingTypeMap[this]

val calculatorType = Calculator.entries.associateBy { it.algo }

fun String.calculatorType() = calculatorType[this]
