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
package hash.password

import hash.keygen.BytesKeyGenerator
import hash.keygen.KeyGenerators
import hash.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm
import java.security.*
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import me.leon.encode.base.base64
import me.leon.ext.hex2ByteArray
import me.leon.ext.toHex

/**
 * A [PasswordEncoder] implementation that uses PBKDF2 with :
 *
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

    override fun encode(rawPassword: CharSequence): String {
        val salt = saltGenerator.generateKey()
        val encoded = encode(rawPassword, salt)
        return encode(encoded)
    }

    fun encodeWithSalt(rawPassword: CharSequence, salt: ByteArray): String {
        val encoded = encode(rawPassword, salt)
        return encode(encoded)
    }

    private fun encode(bytes: ByteArray): String {
        return if (encodeHashAsBase64) {
            bytes.base64()
        } else bytes.toHex()
    }

    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        val digested = decode(encodedPassword)
        val salt = digested.sliceArray(0 until saltGenerator.keyLength)
        return MessageDigest.isEqual(digested, encode(rawPassword, salt))
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
        } else encodedBytes.hex2ByteArray()
    }

    fun encode(rawPassword: CharSequence, salt: ByteArray): ByteArray {
        return try {
            val spec = PBEKeySpec(rawPassword.toString().toCharArray(), salt, iterations, hashWidth)
            salt + SecretKeyFactory.getInstance(algorithm).generateSecret(spec).encoded
        } catch (ex: GeneralSecurityException) {
            throw IllegalStateException("Could not create hash", ex)
        }
    }

    enum class SecretKeyFactoryAlgorithm {
        PBKDF2WithHmacSHA1,
        PBKDF2WithHmacSHA256,
        PBKDF2WithHmacSHA512,
        PBEWithHMACTIGER,
        PBEWithHMACSHA256,
        PBKDF2WithHMACSHA256,
        PBKDF2WithHMACSHA224,
        PBEWithHMACRIPEMD160,
        `PBKDF2WithHMACSHA3-384`,
        PBKDF2WithHMACGOST3411,
        PBKDF2WithHMACSM3,
        PBKDF2WithHMACSHA384,
        `PBKDF2WithHMACSHA3-512`,
        PBKDF2WithHMACSHA512,
        PBEWithHMACSHA1,
        PBEWithHMACGOST3411,
        `PBKDF2WithHMACSHA3-256`,
        `PBKDF2WithHMACSHA3-224`,
    }

    companion object {
        private const val DEFAULT_SALT_LENGTH = 8
        private const val DEFAULT_HASH_WIDTH = 256
        private const val DEFAULT_ITERATIONS = 185000
    }
}
