/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.leon.hash.password

import java.security.*
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import me.leon.encode.base.base64
import me.leon.ext.hex2ByteArray
import me.leon.ext.toHex
import me.leon.hash.keygen.BytesKeyGenerator
import me.leon.hash.keygen.KeyGenerators
import me.leon.hash.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm

/**
 * A [PasswordEncoder] implementation that uses PBKDF2 with :
 * * a configurable random salt value length (default is {@value #DEFAULT_SALT_LENGTH} bytes)
 * * a configurable number of iterations (default is {@value #DEFAULT_ITERATIONS})
 * * a configurable output hash width (default is {@value #DEFAULT_HASH_WIDTH} bits)
 * * a configurable key derivation function (see [SecretKeyFactoryAlgorithm])
 * * a configurable secret appended to the random salt (default is empty)
 *
 * The algorithm is invoked on the concatenated bytes of the salt, secret and password.
 *
 * @author Rob Worsnop
 * @author Rob Winch
 * @author Loïc Guibert
 * @since 4.1
 */
class Pbkdf2PasswordEncoder
@JvmOverloads
constructor(
    private val secret: String = "",
    saltLength: Int = DEFAULT_SALT_LENGTH,
    private var iterations: Int = DEFAULT_ITERATIONS,
    private var hashWidth: Int = DEFAULT_HASH_WIDTH
) : PasswordEncoder {
    private val saltGenerator: BytesKeyGenerator
    private var algorithm = SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1.name
    var encodeHashAsBase64 = false

    init {
        saltGenerator = KeyGenerators.secureRandom(saltLength)
    }

    fun setAlgorithm(secretKeyFactoryAlgorithm: SecretKeyFactoryAlgorithm) {
        val algorithmName = secretKeyFactoryAlgorithm.name
        try {
            SecretKeyFactory.getInstance(algorithmName)
            algorithm = algorithmName
        } catch (ex: NoSuchAlgorithmException) {
            throw IllegalArgumentException("Invalid algorithm '$algorithmName'.", ex)
        }
    }

    override fun encode(password: CharSequence): String {
        val salt = saltGenerator.generateKey()
        val encoded = encode(password, salt)
        return encode(encoded)
    }

    fun encodeWithSalt(rawPassword: CharSequence, salt: ByteArray): String {
        val encoded = encode(rawPassword, salt)
        return encode(encoded)
    }

    private fun encode(bytes: ByteArray): String {
        return if (encodeHashAsBase64) {
            bytes.base64()
        } else {
            bytes.toHex()
        }
    }

    override fun matches(password: CharSequence, encodedPassword: String): Boolean {
        val digested = decode(encodedPassword)
        val salt = digested.sliceArray(0 until saltGenerator.keyLength)
        return MessageDigest.isEqual(digested, encode(password, salt))
    }

    fun matchesWithSalt(
        rawPassword: CharSequence,
        encodedPassword: String,
        salt: ByteArray
    ): Boolean {
        val digested = decode(encodedPassword)
        return MessageDigest.isEqual(digested, encode(rawPassword, salt))
    }

    private fun decode(encodedBytes: String): ByteArray {
        return if (encodeHashAsBase64) {
            Base64.getDecoder().decode(encodedBytes)
        } else {
            encodedBytes.hex2ByteArray()
        }
    }

    fun encode(rawPassword: CharSequence, salt: ByteArray): ByteArray {
        return try {
            val spec =
                PBEKeySpec(
                    rawPassword.toString().toCharArray(),
                    salt + secret.toByteArray(),
                    iterations,
                    hashWidth
                )
            salt + SecretKeyFactory.getInstance(algorithm).generateSecret(spec).encoded
        } catch (ex: GeneralSecurityException) {
            throw IllegalStateException("Could not create hash", ex)
        }
    }

    enum class SecretKeyFactoryAlgorithm {
        PBKDF2WithHmacSHA1,
        PBKDF2WithHmacSHA256,
        PBKDF2WithHmacSHA512,
        PBEWithHmacTIGER,
        PBEWithHmacSHA256,
        PBKDF2WithHmacSHA224,
        PBEWithHmacRIPEMD160,
        `PBKDF2WithHmacSHA3-384`,
        PBKDF2WithHmacGOST3411,
        PBKDF2WithHmacSM3,
        PBKDF2WithHmacSHA384,
        `PBKDF2WithHmacSHA3-512`,
        PBEWithHmacSHA1,
        PBEWithHmacGOST3411,
        `PBKDF2WithHmacSHA3-256`,
        `PBKDF2WithHmacSHA3-224`,
    }

    companion object {
        private const val DEFAULT_SALT_LENGTH = 8
        private const val DEFAULT_HASH_WIDTH = 256
        private const val DEFAULT_ITERATIONS = 185_000
    }
}

val PBE_ENCODERS =
    Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.values().fold(
        mutableMapOf<SecretKeyFactoryAlgorithm, Pbkdf2PasswordEncoder>()
    ) { acc, secretKeyFactoryAlgorithm ->
        acc.apply {
            put(
                secretKeyFactoryAlgorithm,
                Pbkdf2PasswordEncoder().apply { setAlgorithm(secretKeyFactoryAlgorithm) }
            )
        }
    }
