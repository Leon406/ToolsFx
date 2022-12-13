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

import me.leon.encode.base.base64
import me.leon.ext.toHex
import me.leon.hash.keygen.Base64StringKeyGenerator
import me.leon.hash.keygen.StringKeyGenerator

/**
 * This [PasswordEncoder] is provided for legacy purposes only and is not considered secure.
 *
 * Encodes passwords using the passed in MessageDigest.
 *
 * The general format of the password is:
 * <pre> s = salt == null ? "" : "{" + salt + "}" s + digest(password + s) </pre> *
 *
 * Such that "salt" is the salt, digest is the digest method, and password is the actual password.
 * For example when using MD5, a password of "password", and a salt of "thisissalt":
 * <pre> String s = salt == null ? "" : "{" + salt + "}"; s + md5(password + s) "{thisissalt}" +
 * md5(password + "{thisissalt}") "{thisissalt}2a4e7104c2780098f50ed5a84bb2323d" </pre> *
 *
 * If the salt does not exist, then omit "{salt}" like this:
 * <pre> digest(password) </pre> *
 *
 * If the salt is an empty String, then only use "{}" like this:
 * <pre> "{}" + digest(password + "{}") </pre> *
 *
 * The format is intended to work with the DigestPasswordEncoder that was found in the Spring
 * Security core module. However, the passwords will need to be migrated to include any salt with
 * the password since this API provides Salt internally vs making it the responsibility of the user.
 * To migrate passwords from the SaltSource use the following:
 * <pre> String salt = saltSource.getSalt(user); String s = salt == null ? null : "{" + salt + "}";
 * String migratedPassword = s + user.getPassword(); </pre> *
 *
 * @author Ray Krueger
 * @author Luke Taylor
 * @author Rob Winch
 * @since 5.0
 */
class MessageDigestPasswordEncoder(algorithm: String) : PasswordEncoder {
    private val saltGenerator: StringKeyGenerator = Base64StringKeyGenerator()
    var encodeHashAsBase64 = false

    private val digester: Digester = Digester(algorithm, 1)

    /**
     * Encodes the rawPass using a MessageDigest. If a salt is specified it will be merged with the
     * password before encoding.
     *
     * @param password The plain text password
     * @return Hex string of password digest or base64 encoded string if encodeHashAsBase64 is
     *   enabled.
     */
    override fun encode(password: CharSequence): String {
        val salt = PREFIX + saltGenerator.generateKey() + SUFFIX
        return digest(salt, password)
    }

    fun digest(salt: String, rawPassword: CharSequence): String {
        val saltedPassword = rawPassword.toString() + salt
        val digest = digester.digest(saltedPassword.toByteArray())
        val encoded = encode(digest)
        return salt + encoded
    }

    fun digest(salt: ByteArray, rawPassword: ByteArray): ByteArray {
        return salt + digester.digest(salt + rawPassword)
    }

    private fun encode(digest: ByteArray): String {
        return if (encodeHashAsBase64) {
            digest.base64()
        } else {
            digest.toHex()
        }
    }

    /**
     * Takes a previously encoded password and compares it with a rawpassword after mixing in the
     * salt and encoding that value
     *
     * @param password plain text password
     * @param encodedPassword previously encoded password
     * @return true or false
     */
    override fun matches(password: CharSequence, encodedPassword: String): Boolean {
        val salt = extractSalt(encodedPassword)
        return encodedPassword == digest(salt, password)
    }

    /**
     * Sets the number of iterations for which the calculated hash value should be "stretched". If
     * this is greater than one, the initial digest is calculated, the digest function will be
     * called repeatedly on the result for the additional number of iterations.
     *
     * @param iterations the number of iterations which will be executed on the hashed password/salt
     *   value. Defaults to 1.
     */
    fun setIterations(iterations: Int) {
        digester.setIterations(iterations)
    }

    private fun extractSalt(prefixEncodedPassword: String): String {
        val start = prefixEncodedPassword.indexOf(PREFIX)
        if (start != 0) {
            return ""
        }
        val end = prefixEncodedPassword.indexOf(SUFFIX, start)
        return if (end < 0) {
            ""
        } else {
            prefixEncodedPassword.substring(start, end + 1)
        }
    }

    companion object {
        private const val PREFIX = "{"
        private const val SUFFIX = "}"
    }
}
