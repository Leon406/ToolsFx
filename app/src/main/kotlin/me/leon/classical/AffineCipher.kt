package me.leon.classical

import me.leon.ext.crypto.TABLE_A_Z
import me.leon.ext.crypto.TABLE_A_Z_LOWER
import me.leon.ext.math.circleIndex

/** y = ax + b x = (y- b) / a */
fun String.affineEncrypt(factor: Int, bias: Int, table: String = TABLE_A_Z) =
    map {
            when (it) {
                in table -> table[(factor * table.indexOf(it) + bias) % table.length].propCase(it)
                in TABLE_A_Z_LOWER ->
                    TABLE_A_Z_LOWER[
                        (factor * TABLE_A_Z_LOWER.indexOf(it) + bias) % TABLE_A_Z_LOWER.length]
                else -> it
            }
        }
        .joinToString("")

fun String.affineDecrypt(factor: Int, bias: Int, table: String = TABLE_A_Z) =
    with(factor.toBigInteger().modInverse(table.length.toBigInteger()).toInt()) {
        map {
                when (it) {
                    in table ->
                        table[(this * (table.indexOf(it) - bias)).circleIndex(table.length)]
                            .propCase(it)
                    in TABLE_A_Z_LOWER ->
                        TABLE_A_Z_LOWER[
                            (this * (TABLE_A_Z_LOWER.indexOf(it) - bias)).circleIndex(
                                TABLE_A_Z_LOWER.length
                            )]
                    else -> it
                }
            }
            .joinToString("")
    }
