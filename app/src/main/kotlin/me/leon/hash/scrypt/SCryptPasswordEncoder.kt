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
package me.leon.hash.scrypt

import java.security.MessageDigest
import kotlin.math.ln
import kotlin.math.pow
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.hash.keygen.BytesKeyGenerator
import me.leon.hash.keygen.KeyGenerators.secureRandom
import me.leon.hash.password.PasswordEncoder
import org.bouncycastle.crypto.generators.SCrypt

/**
 * Implementation of PasswordEncoder that uses the SCrypt hashing function. Clients can optionally
 * supply a cpu cost parameter, a memory cost parameter and a parallelization parameter.
 *
 * A few
 * [ warnings](http://bouncy-castle.1462172.n4.nabble.com/Java-Bouncy-Castle-scrypt-implementation-td4656832.html)
 * :
 * * The current implementation uses Bouncy castle which does not exploit parallelism/optimizations
 *   that password crackers will, so there is an unnecessary asymmetry between attacker and
 *   defender.
 * * Scrypt is based on Salsa20 which performs poorly in Java (on par with AES) but performs awesome
 *   (~4-5x faster) on SIMD capable platforms
 * * While there are some that would disagree, consider
 *   reading - [Why I Don't Recommend Scrypt](https://blog.ircmaxell.com/2014/03/why-i-dont-recommend-scrypt.html)
 *   (for password storage)
 *
 * @author Shazin Sadakath
 * @author Rob Winch
 */
class SCryptPasswordEncoder
@JvmOverloads
constructor(
    var cpuCost: Int = 16_384,
    var memoryCost: Int = 8,
    var parallelization: Int = 1,
    var keyLength: Int = 32,
    var saltLength: Int = 64
) : PasswordEncoder {
    private val saltGenerator: BytesKeyGenerator

    init {
        require(cpuCost > 1) { "Cpu cost parameter must be > 1." }
        require(!(memoryCost == 1 && cpuCost > 65_536)) {
            "Cpu cost parameter must be > 1 and < 65536."
        }
        require(memoryCost >= 1) { "Memory cost must be >= 1." }
        val maxParallel = Int.MAX_VALUE / (128 * memoryCost * 8)
        require(!(parallelization < 1 || parallelization > maxParallel)) {
            ("Parallelization parameter p must be >= 1 and <= $maxParallel" +
                " (based on block size r of $memoryCost)")
        }
        require(keyLength >= 1) { "Key length must be >= " }
        require(saltLength >= 1) { "Salt length must be >= 1  " }
        saltGenerator = secureRandom(saltLength)
    }

    override fun encode(password: CharSequence): String {
        return digest(password, saltGenerator.generateKey())
    }

    override fun matches(password: CharSequence, encodedPassword: String): Boolean {
        if (encodedPassword.length < keyLength) {
            println("Empty encoded password")
            return false
        }
        return decodeAndCheckMatches(password, encodedPassword)
    }

    override fun upgradeEncoding(encodedPassword: String): Boolean {
        if (encodedPassword.isEmpty()) {
            return false
        }
        val parts = encodedPassword.split("\$").toTypedArray()
        require(parts.size == 4) { "Encoded password does not look like SCrypt: $encodedPassword" }
        val params = parts[1].toLong(16)
        val cpuCost = 2.0.pow((params shr 16 and 0xffff).toDouble()).toInt()
        val memoryCost = params.toInt() shr 8 and 0xff
        val parallelization = params.toInt() and 0xff
        return cpuCost < this.cpuCost ||
            memoryCost < this.memoryCost ||
            parallelization < this.parallelization
    }

    private fun decodeAndCheckMatches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        val parts = encodedPassword.split("\$").toTypedArray()
        if (parts.size != 4) {
            return false
        }
        val params = parts[1].toLong(16)
        val salt = parts[2].base64Decode()
        val derived = parts[3].base64Decode()
        val cpuCost = 2.0.pow((params shr 16 and 0xffff).toDouble()).toInt()
        val memoryCost = params.toInt() shr 8 and 0xff
        val parallelization = params.toInt() and 0xff
        val generated =
            SCrypt.generate(
                rawPassword.toString().toByteArray(),
                salt,
                cpuCost,
                memoryCost,
                parallelization,
                keyLength
            )
        return MessageDigest.isEqual(derived, generated)
    }

    fun digest(rawPassword: CharSequence, salt: ByteArray): String {
        val derived =
            SCrypt.generate(
                rawPassword.toString().toByteArray(),
                salt,
                cpuCost,
                memoryCost,
                parallelization,
                keyLength
            )
        val params =
            ((ln(cpuCost.toDouble()) / ln(2.0)).toInt() shl
                    16 or
                    (memoryCost shl 8) or
                    parallelization)
                .toLong()
                .toString(16)
        val sb =
            StringBuilder((salt.size + derived.size) * 2)
                .append("$")
                .append(params)
                .append('$')
                .append(salt.base64())
                .append('$')
                .append(derived.base64())
        return sb.toString()
    }
}
