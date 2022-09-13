package me.leon.ext.crypto

val HEX_REGEX = "^(?:0[xX])?[\\da-fA-F]+$".toRegex()
val HEX_WITH_LEAD_REGEX = "^0[xX][\\da-fA-F]+$".toRegex()
val HEX_LEAD_REGEX = "^0[xX]".toRegex()
val BINARY_LEAD_REGEX = "^0[bB]".toRegex()
val OCTAL_LEAD_REGEX = "^0[oO]".toRegex()

val OCTAL_REGEX = "^(?:0[oO])?[0-7]+$".toRegex()

val BINARY_REGEX = "^(?:0[bB])?[01]+$".toRegex()
