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

/**
 * Factory for commonly used key generators. Public API for constructing a [BytesKeyGenerator] or
 * [StringKeyGenerator].
 *
 * @author Keith Donald
 */
object KeyGenerators {

    @JvmStatic
    fun secureRandom(): BytesKeyGenerator {
        return SecureRandomBytesKeyGenerator()
    }

    @JvmStatic
    fun secureRandom(keyLength: Int): BytesKeyGenerator {
        return SecureRandomBytesKeyGenerator(keyLength)
    }

    fun shared(keyLength: Int): BytesKeyGenerator {
        return SharedKeyGenerator(secureRandom(keyLength).generateKey())
    }

    fun string(): StringKeyGenerator {
        return HexEncodingStringKeyGenerator(secureRandom())
    }
}
