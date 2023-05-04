/*
 * Copyright 2002-2019 the original author or authors.
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
package me.leon.hash.argon2

import me.leon.hash.argon2.Argon2EncodingUtils.Argon2Hash
import me.leon.hash.keygen.BytesKeyGenerator
import me.leon.hash.keygen.KeyGenerators.secureRandom
import me.leon.hash.password.PasswordEncoder
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters

/**
 * Implementation of PasswordEncoder that uses the Argon2 hashing function. Clients can optionally
 * supply the length of the salt to use, the length of the generated hash, a cpu cost parameter, a
 * memory cost parameter and a parallelization parameter.
 *
 * Note:
 *
 * The current implementation uses Bouncy castle which does not exploit parallelism/optimizations
 * that password crackers will, so there is an unnecessary asymmetry between attacker and defender.
 *
 * @author Simeon Macke
 * @since 5.3
 */
class Argon2PasswordEncoder
@JvmOverloads
constructor(
    saltLength: Int = DEFAULT_SALT_LENGTH,
    var hashLength: Int = DEFAULT_HASH_LENGTH,
    var parallelism: Int = DEFAULT_PARALLELISM,
    var memory: Int = DEFAULT_MEMORY,
    var iterations: Int = DEFAULT_ITERATIONS
) : PasswordEncoder {
    private val saltGenerator: BytesKeyGenerator = secureRandom(saltLength)

    var type = Argon2Parameters.ARGON2_id
    var version = Argon2Parameters.ARGON2_VERSION_13

    override fun encode(password: CharSequence): String {
        return encode(password, saltGenerator.generateKey())
    }

    fun encode(rawPassword: CharSequence, salt: ByteArray): String {
        val hash = ByteArray(hashLength)
        val params =
            Argon2Parameters.Builder(type)
                .withVersion(version)
                .withSalt(salt)
                .withParallelism(parallelism)
                .withMemoryAsKB(memory)
                .withIterations(iterations)
                .build()
        Argon2BytesGenerator()
            .apply { init(params) }
            .generateBytes(rawPassword.toString().toCharArray(), hash)
        return Argon2EncodingUtils.encode(hash, params)
    }

    override fun matches(password: CharSequence, encodedPassword: String): Boolean {
        val decoded: Argon2Hash =
            try {
                Argon2EncodingUtils.decode(encodedPassword)
            } catch (ignore: IllegalArgumentException) {
                return false
            }
        val hashBytes = ByteArray(decoded.hash.size)
        val generator = Argon2BytesGenerator()
        generator.init(decoded.parameters)
        generator.generateBytes(password.toString().toCharArray(), hashBytes)
        return constantTimeArrayEquals(decoded.hash, hashBytes)
    }

    override fun upgradeEncoding(encodedPassword: String): Boolean {
        if (encodedPassword.isEmpty()) {
            println("password hash is null")
            return false
        }
        val parameters = Argon2EncodingUtils.decode(encodedPassword).parameters
        return parameters.memory < memory || parameters.iterations < iterations
    }

    companion object {
        private const val DEFAULT_SALT_LENGTH = 16
        private const val DEFAULT_HASH_LENGTH = 32
        private const val DEFAULT_PARALLELISM = 1
        private const val DEFAULT_MEMORY = 1 shl 12
        private const val DEFAULT_ITERATIONS = 3

        private fun constantTimeArrayEquals(expected: ByteArray, actual: ByteArray): Boolean {
            if (expected.size != actual.size) {
                return false
            }
            var result = 0
            for (i in expected.indices) {
                result = result or (expected[i].toInt() xor actual[i].toInt())
            }
            return result == 0
        }
    }
}
