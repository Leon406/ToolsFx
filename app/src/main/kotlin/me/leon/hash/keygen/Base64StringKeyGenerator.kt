/*
 * Copyright 2002-2018 the original author or authors.
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
package me.leon.hash.keygen

import java.util.Base64
import me.leon.hash.keygen.KeyGenerators.secureRandom

/**
 * A StringKeyGenerator that generates base64-encoded String keys. Delegates to a [ ] for the actual
 * key generation.
 *
 * @author Joe Grandja
 * @author Rob Winch
 * @since 5.0
 */
class Base64StringKeyGenerator
@JvmOverloads
constructor(
    private val encoder: Base64.Encoder = Base64.getEncoder(),
    keyLength: Int = DEFAULT_KEY_LENGTH
) : StringKeyGenerator {
    private val keyGenerator: BytesKeyGenerator

    init {
        require(keyLength >= DEFAULT_KEY_LENGTH) {
            "keyLength must be greater than or equal to$DEFAULT_KEY_LENGTH"
        }
        keyGenerator = secureRandom(keyLength)
    }

    override fun generateKey(): String {
        val key = keyGenerator.generateKey()
        val base64EncodedKey = encoder.encode(key)
        return String(base64EncodedKey)
    }

    companion object {
        private const val DEFAULT_KEY_LENGTH = 32
    }
}
