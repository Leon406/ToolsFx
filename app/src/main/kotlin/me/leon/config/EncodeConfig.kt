package me.leon.config

import me.leon.ext.crypto.EncodeType

/**
 * @author Leon
 * @since 2022-12-15 11:26
 * @email deadogone@gmail.com
 */
val BASE_ENCODE_EXCLUDED_DICT_LIST =
    arrayOf(
        EncodeType.BASE100,
        EncodeType.BASE2048,
        EncodeType.BASE32768,
        EncodeType.BASE65536,
    )

val SHOW_DICT_LIST = arrayOf(EncodeType.RADIX_N, EncodeType.DECIMAL_RADIX_N)

val CRACK_EXCLUDE_ENCODE =
    arrayOf(
        EncodeType.RADIX8,
        EncodeType.DECIMAL_RADIX_N,
        EncodeType.BASE16,
        EncodeType.OCTAL,
        //            EncodeType.Decimal,
        EncodeType.RADIX9,
        EncodeType.RADIX_N,
        EncodeType.RADIX10,
        EncodeType.RADIX32,
        EncodeType.RADIX64,
        EncodeType.UTF7,
        EncodeType.UTF7_ALL,
        EncodeType.UTF7_EXT,
        EncodeType.BASE69,
        EncodeType.BASE65536,
        EncodeType.ECOJI,
        EncodeType.BASE2048,
        EncodeType.BASE32768,
    )
val ENCODE_TYPE_WITH_SPACE =
    arrayOf(
        EncodeType.UUENCODE,
        EncodeType.XXENCODE,
        EncodeType.QUOTE_PRINTABLE,
        EncodeType.PUNY_CODE,
        EncodeType.BASE69,
        EncodeType.BASE65536,
    )

fun EncodeType.showDict() =
    type.contains("base") && !BASE_ENCODE_EXCLUDED_DICT_LIST.contains(this) ||
        SHOW_DICT_LIST.contains(this)
