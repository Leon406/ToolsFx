/*
 * Copyright 2011-2016 the original author or authors.
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

import java.security.SecureRandom

/**
 * A KeyGenerator that uses [SecureRandom] to generate byte array-based keys.
 *
 * No specific provider is used for the `SecureRandom`, so the platform default will be used.
 *
 * @author Keith Donald
 */
internal class SecureRandomBytesKeyGenerator
@JvmOverloads
constructor(override val keyLength: Int = DEFAULT_KEY_LENGTH) : BytesKeyGenerator {
    private val random = SecureRandom()

    override fun generateKey(): ByteArray {
        val bytes = ByteArray(keyLength)
        random.nextBytes(bytes)
        return bytes
    }

    companion object {
        private const val DEFAULT_KEY_LENGTH = 8
    }
}
