/*
 * Copyright 2002-2011 the original author or authors.
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
package me.leon.hash.bcrypt

import java.security.SecureRandom
import me.leon.hash.bcrypt.BCrypt.Companion.checkPw
import me.leon.hash.bcrypt.BCrypt.Companion.genSalt
import me.leon.hash.bcrypt.BCrypt.Companion.hashpw
import me.leon.hash.password.PasswordEncoder

/**
 * Implementation of PasswordEncoder that uses the BCrypt strong hashing function. Clients can
 * optionally supply a "version" ($2a, $2b, $2y) and a "strength" (a.k.a. log rounds in BCrypt) and
 * a SecureRandom instance. The larger the strength parameter the more work will have to be done
 * (exponentially) to hash the passwords. The default value is 10.
 *
 * @author Dave Syer
 */
class BCryptPasswordEncoder
@JvmOverloads
constructor(
    var version: BCryptVersion = BCryptVersion.`$2A`,
    var strength: Int = -1,
    private var random: SecureRandom? = null
) : PasswordEncoder {

    init {
        require(
            !(strength != -1 &&
                (strength < BCrypt.MIN_LOG_ROUNDS || strength > BCrypt.MAX_LOG_ROUNDS))
        ) {
            "Bad strength"
        }
        this.strength = if (strength == -1) 10 else strength
    }

    private val salt: String
        get() = genSalt(version.version, strength, random ?: SecureRandom())

    override fun encode(password: CharSequence): String {
        return hashpw(password.toString(), salt)
    }

    fun encode(rawPassword: CharSequence, salt: ByteArray): String {
        return hashpw(rawPassword.toString(), genSalt(salt, version.version, strength))
    }

    override fun matches(password: CharSequence, encodedPassword: String): Boolean {
        if (encodedPassword.isEmpty()) {
            println("Empty encoded password")
            return false
        }

        if (!encodedPassword.matches(REG_BCRYPT)) {
            println("Encoded password does not look like BCrypt")
            return false
        }
        return checkPw(password.toString(), encodedPassword)
    }

    override fun upgradeEncoding(encodedPassword: String): Boolean {
        if (encodedPassword.isEmpty()) {
            println("Empty encoded password")
            return false
        }
        val matcher = REG_BCRYPT.find(encodedPassword)
        requireNotNull(matcher) { "Encoded password does not look like BCrypt: $encodedPassword" }
        val strength = matcher.groupValues[2].toInt()
        return strength < this.strength
    }

    /**
     * Stores the default bcrypt version for use in configuration.
     *
     * @author Lin Feng
     */
    enum class BCryptVersion(val version: String) {
        `$2A`("$2a"),
        `$2Y`("$2y"),
        `$2B`("$2b")
    }

    companion object {
        private val REG_BCRYPT = "\\$2([ayb])?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}".toRegex()
    }
}
