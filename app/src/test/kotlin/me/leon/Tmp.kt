package me.leon

import argon2.Argon2PasswordEncoder
import bcrypt.BCryptPasswordEncoder
import kotlin.test.assertTrue
import org.junit.Test
import password.*
import scrypt.SCryptPasswordEncoder

class Tmp {

    @Test
    fun scrypt() {
        val encoders: MutableMap<String, PasswordEncoder> = HashMap()
        encoders["bcrypt"] = BCryptPasswordEncoder()
        encoders["ldap"] = LdapShaPasswordEncoder()
        encoders["MD4"] = Md4PasswordEncoder()
        encoders["MD5"] = MessageDigestPasswordEncoder("MD5")
        encoders["noop"] = NoOpPasswordEncoder.getInstance()
        encoders["pbkdf2"] = Pbkdf2PasswordEncoder()
        encoders["scrypt"] = SCryptPasswordEncoder()
        encoders["SHA-1"] = MessageDigestPasswordEncoder("SHA-1")
        encoders["SHA-256"] = MessageDigestPasswordEncoder("SHA-256")
        encoders["argon2"] = Argon2PasswordEncoder()

        for ((k, v) in encoders) {
            println(k + " " + v.encode("123"))
            assertTrue { v.matches("123", v.encode("123")) }
        }
    }
}
