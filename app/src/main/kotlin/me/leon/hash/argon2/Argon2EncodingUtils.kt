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

import java.util.Base64
import org.bouncycastle.crypto.params.Argon2Parameters

/**
 * Utility for encoding and decoding Argon2 hashes.
 *
 * Used by [Argon2PasswordEncoder].
 *
 * @author Simeon Macke
 * @since 5.3
 */
internal object Argon2EncodingUtils {
    private val b64encoder = Base64.getEncoder().withoutPadding()
    private val b64decoder = Base64.getDecoder()

    /**
     * Encodes a raw Argon2-hash and its parameters into the standard Argon2-hash-string as
     * specified in the reference implementation
     * (https://github.com/P-H-C/phc-winner-argon2/blob/master/src/encoding.c#L244):
     *
     * `$argon2<T>[$v=<num>]$m=<num>,t=<num>,p=<num>$<bin>$<bin>`
     *
     * where `<T>` is either 'd', 'id', or 'i', `<num>` is a decimal integer (positive, fits in an
     * 'unsigned long'), and `<bin>` is Base64-encoded data (no '=' padding characters, no newline
     * or whitespace).
     *
     * The last two binary chunks (encoded in Base64) are, in that order, the salt and the output.
     * If no salt has been used, the salt will be omitted.
     *
     * @param hash the raw Argon2 hash in binary format
     * @param parameters the Argon2 parameters that were used to create the hash
     * @return the encoded Argon2-hash-string as described above
     * @throws IllegalArgumentException if the Argon2Parameters are invalid
     */
    @Throws(IllegalArgumentException::class)
    fun encode(hash: ByteArray?, parameters: Argon2Parameters): String {
        val stringBuilder = StringBuilder()
        when (parameters.type) {
            Argon2Parameters.ARGON2_d -> stringBuilder.append("\$argon2d")
            Argon2Parameters.ARGON2_i -> stringBuilder.append("\$argon2i")
            Argon2Parameters.ARGON2_id -> stringBuilder.append("\$argon2id")
            else -> throw IllegalArgumentException("Invalid algorithm type: " + parameters.type)
        }
        stringBuilder
            .append("\$v=")
            .append(parameters.version)
            .append("\$m=")
            .append(parameters.memory)
            .append(",t=")
            .append(parameters.iterations)
            .append(",p=")
            .append(parameters.lanes)
        if (parameters.salt != null) {
            stringBuilder.append("$").append(b64encoder.encodeToString(parameters.salt))
        }
        stringBuilder.append("$").append(b64encoder.encodeToString(hash))
        return stringBuilder.toString()
    }

    /**
     * Decodes an Argon2 hash string as specified in the reference implementation
     * (https://github.com/P-H-C/phc-winner-argon2/blob/master/src/encoding.c#L244) into the raw
     * hash and the used parameters.
     *
     * The hash has to be formatted as follows:
     * `$argon2<T>[$v=<num>]$m=<num>,t=<num>,p=<num>$<bin>$<bin>`
     *
     * where `<T>` is either 'd', 'id', or 'i', `<num>` is a decimal integer (positive, fits in an
     * 'unsigned long'), and `<bin>` is Base64-encoded data (no '=' padding characters, no newline
     * or whitespace).
     *
     * The last two binary chunks (encoded in Base64) are, in that order, the salt and the output.
     * Both are required. The binary salt length and the output length must be in the allowed ranges
     * defined in argon2.h.
     *
     * @param encodedHash the Argon2 hash string as described above
     * @return an [Argon2Hash] object containing the raw hash and the [ ].
     * @throws IllegalArgumentException if the encoded hash is malformed
     */
    @Throws(IllegalArgumentException::class)
    fun decode(encodedHash: String): Argon2Hash {
        val paramsBuilder: Argon2Parameters.Builder
        val parts = encodedHash.split("\$").toTypedArray()

        require(parts.size >= 4) { "Invalid encoded Argon2-hash" }
        var currentPart = 1
        paramsBuilder =
            when (parts[currentPart++]) {
                "argon2d" -> Argon2Parameters.Builder(Argon2Parameters.ARGON2_d)
                "argon2i" -> Argon2Parameters.Builder(Argon2Parameters.ARGON2_i)
                "argon2id" -> Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                else -> throw IllegalArgumentException("Invalid algorithm type: " + parts[0])
            }
        if (parts[currentPart].startsWith("v=")) {
            paramsBuilder.withVersion(parts[currentPart].substring(2).toInt())
            currentPart++
        }
        val performanceParams = parts[currentPart++].split(",").toTypedArray()
        require(performanceParams.size == 3) { "Amount of performance parameters invalid" }
        require(performanceParams[0].startsWith("m=")) { "Invalid memory parameter" }
        paramsBuilder.withMemoryAsKB(performanceParams[0].substring(2).toInt())
        require(performanceParams[1].startsWith("t=")) { "Invalid iterations parameter" }
        paramsBuilder.withIterations(performanceParams[1].substring(2).toInt())
        require(performanceParams[2].startsWith("p=")) { "Invalid parallelity parameter" }
        paramsBuilder.withParallelism(performanceParams[2].substring(2).toInt())
        paramsBuilder.withSalt(b64decoder.decode(parts[currentPart++]))
        return Argon2Hash(b64decoder.decode(parts[currentPart]), paramsBuilder.build())
    }

    internal class Argon2Hash(var hash: ByteArray, val parameters: Argon2Parameters)
}
