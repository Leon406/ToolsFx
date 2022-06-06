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
package me.leon.hash.password

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.hash.keygen.BytesKeyGenerator
import me.leon.hash.keygen.KeyGenerators.secureRandom

/**
 * This [PasswordEncoder] is provided for legacy purposes only and is not considered secure.
 *
 * A version of [PasswordEncoder] which supports Ldap SHA and SSHA (salted-SHA) encodings. The
 * values are base-64 encoded and have the label "{SHA}" (or "{SSHA}") prepended to the encoded
 * hash. These can be made lower-case in the encoded password, if required, by setting the
 * <tt>forceLowerCasePrefix</tt> property to true.
 *
 * Also supports plain text passwords, so can safely be used in cases when both encoded and
 * non-encoded passwords are in use or when a null implementation is required.
 *
 * @author Luke Taylor
 */
class LdapShaPasswordEncoder
@JvmOverloads
constructor(private val saltGenerator: BytesKeyGenerator = secureRandom()) : PasswordEncoder {

    private var forceLowerCasePrefix = false

    private fun combineHashAndSalt(hash: ByteArray, salt: ByteArray?): ByteArray {
        if (salt == null) {
            return hash
        }
        return hash + salt
    }

    /**
     * Calculates the hash of password (and salt bytes, if supplied) and returns a base64 encoded
     * concatenation of the hash and salt, prefixed with {SHA} (or {SSHA} if salt was used).
     *
     * @param password the password to be encoded.
     * @return the encoded password in the specified format
     */
    override fun encode(password: CharSequence): String {
        return encode(password, saltGenerator.generateKey())
    }

    fun encode(password: CharSequence, salt: ByteArray?): String {
        val sha = getSha(password)
        if (salt != null && salt.isNotEmpty()) {
            sha.update(salt)
        }
        val hash = combineHashAndSalt(sha.digest(), salt)
        val prefix = getPrefix(salt)
        return prefix + hash.base64()
    }

    private fun getSha(password: CharSequence): MessageDigest {
        return try {
            MessageDigest.getInstance("SHA").apply { update(password.toString().toByteArray()) }
        } catch (ignored: NoSuchAlgorithmException) {
            error("No SHA implementation available!")
        }
    }

    private fun getPrefix(salt: ByteArray?): String {
        if (salt == null || salt.isEmpty()) {
            return if (forceLowerCasePrefix) SHA_PREFIX_LC else SHA_PREFIX
        }
        return if (forceLowerCasePrefix) SSHA_PREFIX_LC else SSHA_PREFIX
    }

    private fun extractSalt(encPass: String): ByteArray {
        val encPassNoLabel = encPass.substring(6)
        val hashAndSalt = encPassNoLabel.base64Decode()
        val saltLength = hashAndSalt.size - SHA_LENGTH
        val salt = ByteArray(saltLength)
        System.arraycopy(hashAndSalt, SHA_LENGTH, salt, 0, saltLength)
        return salt
    }

    /**
     * Checks the validity of an unencoded password against an encoded one in the form
     * "{SSHA}sQuQF8vj8Eg2Y1hPdh3bkQhCKQBgjhQI".
     *
     * @param password unencoded password to be verified.
     * @param encodedPassword the actual SSHA or SHA encoded password
     * @return true if they match (independent of the case of the prefix).
     */
    override fun matches(password: CharSequence, encodedPassword: String): Boolean {
        return matches(password.toString(), encodedPassword)
    }

    private fun matches(password: String, encodedPassword: String): Boolean {
        val prefix = extractPrefix(encodedPassword) ?: return encodedPassword == password
        val salt = getSalt(encodedPassword, prefix)
        val startOfHash = prefix.length
        val encodedRawPass = encode(password, salt).substring(startOfHash)
        return encodedRawPass == encodedPassword.substring(startOfHash)
    }

    private fun getSalt(encodedPassword: String, prefix: String): ByteArray? {
        if (prefix == SSHA_PREFIX || prefix == SSHA_PREFIX_LC) {
            return extractSalt(encodedPassword)
        }
        require(!(prefix != SHA_PREFIX && prefix != SHA_PREFIX_LC)) {
            "Unsupported password prefix '$prefix'"
        }
        // Standard SHA
        return null
    }

    /** Returns the hash prefix or null if there isn't one. */
    private fun extractPrefix(encPass: String): String? {
        if (!encPass.startsWith("{")) {
            return null
        }
        val secondBrace = encPass.lastIndexOf('}')
        require(secondBrace >= 0) { "Couldn't find closing brace for SHA prefix" }
        return encPass.substring(0, secondBrace + 1)
    }

    fun setForceLowerCasePrefix(forceLowerCasePrefix: Boolean) {
        this.forceLowerCasePrefix = forceLowerCasePrefix
    }

    companion object {
        private const val SHA_LENGTH = 20
        private const val SSHA_PREFIX = "{SSHA}"
        private val SSHA_PREFIX_LC = SSHA_PREFIX.lowercase()
        private const val SHA_PREFIX = "{SHA}"
        private val SHA_PREFIX_LC = SHA_PREFIX.lowercase()
    }
}
