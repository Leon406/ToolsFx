/*
 * Copyright 2011-2018 the original author or authors.
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

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Helper for working with the MessageDigest API.
 *
 * Performs the configured number of iterations of the hashing algorithm per digest to aid in
 * protecting against brute force attacks.
 *
 * @author Keith Donald
 * @author Luke Taylor
 */
internal class Digester(private val algorithm: String, private var iterations: Int = 0) {

    fun digest(value: ByteArray): ByteArray {
        var tmp = value
        val messageDigest = createDigest(algorithm)
        repeat(iterations) { tmp = messageDigest.digest(tmp) }
        return tmp
    }

    fun setIterations(iterations: Int) {
        require(iterations > 0) { "Iterations value must be greater than zero" }
        this.iterations = iterations
    }

    companion object {
        private fun createDigest(algorithm: String): MessageDigest {
            return try {
                MessageDigest.getInstance(algorithm)
            } catch (ex: NoSuchAlgorithmException) {
                throw IllegalStateException("No such hashing algorithm", ex)
            }
        }
    }
}
