package me.leon.ext.crypto

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import org.bouncycastle.asn1.gm.GMNamedCurves
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.asn1.x9.ECNamedCurveTable
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.engines.SM2Engine
import org.bouncycastle.crypto.params.*
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil
import org.bouncycastle.jcajce.spec.OpenSSHPrivateKeySpec
import org.bouncycastle.jcajce.spec.OpenSSHPublicKeySpec
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import org.bouncycastle.util.BigIntegers

private val x9ECParameters = GMNamedCurves.getByName("sm2p256v1")
private val ecDomainParameters =
    ECDomainParameters(x9ECParameters.curve, x9ECParameters.g, x9ECParameters.n, x9ECParameters.h)

fun ByteArray.toECPrivateKeyParams(): AsymmetricKeyParameter {
    try {
        // 尝试d值
        return ECPrivateKeyParameters(BigIntegers.fromUnsignedByteArray(this), ecDomainParameters)
    } catch (ignore: Exception) {
        // nop
    }
    // 尝试X.509
    val private =
        try {
            KeyFactory.getInstance("EC").generatePrivate(PKCS8EncodedKeySpec(this))
        } catch (ignore: Exception) {
            KeyFactory.getInstance("EC").generatePrivate(OpenSSHPrivateKeySpec(this))
        }
    return generatePrivateKeyParameter(private)
}

fun ByteArray.toECPublicKeyParams(): AsymmetricKeyParameter {
    try {
        // 尝试Q值
        return ECPublicKeyParameters(ecDomainParameters.curve.decodePoint(this), ecDomainParameters)
    } catch (ignore: Exception) {
        // nop
    }
    // 尝试X.509
    val public =
        try {
            KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(this))
        } catch (ignore: Exception) {
            KeyFactory.getInstance("EC").generatePublic(OpenSSHPublicKeySpec(this))
        }
    return generatePublicKeyParameter(public)
}

private fun generatePrivateKeyParameter(key: PrivateKey): AsymmetricKeyParameter =
    when (key) {
        is ECPrivateKey -> {
            var s = key.parameters
            if (s == null) {
                s = BouncyCastleProvider.CONFIGURATION.ecImplicitlyCa
            }
            if (key.parameters is ECNamedCurveParameterSpec) {
                val name = (key.parameters as ECNamedCurveParameterSpec).name
                ECPrivateKeyParameters(
                    key.d,
                    ECNamedDomainParameters(
                        ECNamedCurveTable.getOID(name),
                        s!!.curve,
                        s.g,
                        s.n,
                        s.h,
                        s.seed
                    )
                )
            } else {
                ECPrivateKeyParameters(key.d, ECDomainParameters(s!!.curve, s.g, s.n, s.h, s.seed))
            }
        }
        is java.security.interfaces.ECPrivateKey -> {
            val s = EC5Util.convertSpec(key.params)
            ECPrivateKeyParameters(key.s, ECDomainParameters(s.curve, s.g, s.n, s.h, s.seed))
        }
        else -> {
            // see if we can build a key from key.getEncoded()
            val bytes = key.encoded
            val privateKey = BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(bytes))
            ECUtil.generatePrivateKeyParameter(privateKey)
        }
    }

private fun generatePublicKeyParameter(key: PublicKey): AsymmetricKeyParameter =
    when (key) {
        is ECPublicKey -> {
            val s = key.parameters
            ECPublicKeyParameters(key.q, ECDomainParameters(s.curve, s.g, s.n, s.h, s.seed))
        }
        is java.security.interfaces.ECPublicKey -> {
            val s = EC5Util.convertSpec(key.params)
            ECPublicKeyParameters(
                EC5Util.convertPoint(key.params, key.w),
                ECDomainParameters(s.curve, s.g, s.n, s.h, s.seed)
            )
        }
        else -> {
            // see if we can build a key from key.getEncoded()
            val bytes = key.encoded
            val publicKey =
                BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(bytes))
            ECUtil.generatePublicKeyParameter(publicKey)
        }
    }

fun ByteArray.sm2(
    isEncrypt: Boolean,
    params: CipherParameters,
    mode: SM2Engine.Mode = SM2Engine.Mode.C1C3C2
): ByteArray =
    with(SM2Engine(mode)) {
        init(isEncrypt, if (isEncrypt) ParametersWithRandom(params, SecureRandom()) else params)
        processBlock(this@sm2, 0, size)
    }
