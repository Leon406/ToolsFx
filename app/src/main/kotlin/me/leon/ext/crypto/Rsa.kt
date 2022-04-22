package me.leon.ext.crypto

import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

fun PublicKey.bitLength() = (this as? RSAPublicKey)?.modulus?.bitLength() ?: 1024

fun PrivateKey.bitLength() = (this as? RSAPrivateKey)?.modulus?.bitLength() ?: 1024
