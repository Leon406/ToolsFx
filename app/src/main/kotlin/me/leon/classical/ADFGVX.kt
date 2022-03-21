package me.leon.classical

const val ADFGVX_ENCODEMAP = "ADFGVX"

fun String.adfgvx(table: String, keyword: String) =
    adfgx(table, keyword, ADFGVX_ENCODEMAP, "I" to "I")

fun String.adfgvxDecrypt(table: String, keyword: String) =
    adfgxDecrypt(table, keyword, ADFGVX_ENCODEMAP)
