package me.leon.ext.crypto

import java.math.BigInteger
import me.leon.ext.toHex
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.math.ec.ECCurve

val allCurves =
    (CustomNamedCurves.getNames().toList().map { it.toString() } +
            SECNamedCurves.getNames().toList().map { it.toString() })
        .distinct()
        .sorted()

val String.curve: ECCurve
    get() = (CustomNamedCurves.getByName(this) ?: SECNamedCurves.getByName(this)).curve

fun String.curveMultiply(x: BigInteger, y: BigInteger, k: BigInteger): Pair<String, String> {
    with(curve.createPoint(x, y).multiply(k).getEncoded(false).toHex()) {
        return substring(2, 66) to substring(66)
    }
}

fun String.curveAdd(
    x: BigInteger,
    y: BigInteger,
    x2: BigInteger,
    y2: BigInteger
): Pair<String, String> {
    with(curve) {
        val hex = createPoint(x, y).add(createPoint(x2, y2)).getEncoded(false).toHex()
        return hex.substring(2, 66) to hex.substring(66)
    }
}

fun String.curveSubtract(
    x: BigInteger,
    y: BigInteger,
    x2: BigInteger,
    y2: BigInteger
): Pair<String, String> {
    with(curve) {
        val hex = createPoint(x, y).subtract(createPoint(x2, y2)).getEncoded(false).toHex()
        return hex.substring(2, 66) to hex.substring(66)
    }
}
