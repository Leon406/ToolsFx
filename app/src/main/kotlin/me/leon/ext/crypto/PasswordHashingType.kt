package me.leon.ext.crypto

import me.leon.hash
import me.leon.hash.argon2.Argon2PasswordEncoder
import me.leon.hash.bcrypt.BCryptPasswordEncoder
import me.leon.hash.password.*
import me.leon.hash.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.*
import me.leon.hash.scrypt.SCryptPasswordEncoder

// 可见字符
enum class PasswordHashingType(val alg: String) {
    DoubleMd5("md5(md5)") {
        override fun hash(data: ByteArray): String {
            var hash = data.decodeToString()
            repeat(2) { hash = hash.lowercase().hash() }
            return hash
        }
    },
    DoubleMd5Uppercase("MD5(MD5)") {
        override fun hash(data: ByteArray): String {
            var hash = data.decodeToString()
            repeat(2) { hash = hash.uppercase().hash() }
            return hash
        }
    },
    TripleMd5("md5(md5(md5))") {
        override fun hash(data: ByteArray): String {
            var hash = data.decodeToString()
            repeat(3) { hash = hash.lowercase().hash() }
            return hash
        }
    },
    TripleMd5Uppercase("MD5(MD5(MD5))") {
        override fun hash(data: ByteArray): String {
            var hash = data.decodeToString()
            repeat(3) { hash = hash.uppercase().hash() }
            return hash
        }
    },
    Md5Sha1("md5(SHA1)") {
        override fun hash(data: ByteArray): String {
            return data.decodeToString().hash("SHA1").uppercase().hash()
        }
    },
    Md5Sha256("md5(SHA256)") {
        override fun hash(data: ByteArray): String {
            return data.decodeToString().hash("SHA256").uppercase().hash()
        }
    },
    Md5Sha384("md5(SHA384)") {
        override fun hash(data: ByteArray): String {
            return data.decodeToString().hash("SHA384").uppercase().hash()
        }
    },
    Md5Sha512("md5(SHA512)") {
        override fun hash(data: ByteArray): String {
            return data.decodeToString().hash("SHA512").uppercase().hash()
        }
    },
    MySql("mysql") {
        override fun hash(data: ByteArray): String {
            return data.mysqlOld()
        }
    },
    MySql5("mysql5") {
        override fun hash(data: ByteArray): String {
            return data.mysql()
        }
    },
    SpringSecurityMD4("SpringSecurity-MD4") {
        override fun hash(data: ByteArray): String {
            return PH_MD4.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PH_MD4.matches(pass, hash)
        }
    },
    SpringSecurityMD5("SpringSecurity-MD5") {
        override fun hash(data: ByteArray): String {
            return PH_MD5.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PH_MD5.matches(pass, hash)
        }
    },
    SpringSecuritySHA1("SpringSecurity-SHA1") {
        override fun hash(data: ByteArray): String {
            return PH_SHA1.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PH_SHA1.matches(pass, hash)
        }
    },
    SpringSecuritySHA256("SpringSecurity-SHA256") {
        override fun hash(data: ByteArray): String {
            return PH_SHA256.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PH_SHA256.matches(pass, hash)
        }
    },
    SpringSecurityLDAP("SpringSecurity-LDAP") {
        override fun hash(data: ByteArray): String {
            return PH_LDAP.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PH_LDAP.matches(pass, hash)
        }
    },
    SpringSecuritySCRYPT("SpringSecurity-SCRYPT") {
        override fun hash(data: ByteArray): String {
            return PH_SCRYPT.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PH_SCRYPT.matches(pass, hash)
        }
    },
    SpringSecurityBRYPT("SpringSecurity-BCRYPT") {
        override fun hash(data: ByteArray): String {
            return PH_BCRYPT.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PH_BCRYPT.matches(pass, hash)
        }
    },
    SpringSecurityARGON2("SpringSecurity-ARGON2") {
        override fun hash(data: ByteArray): String {
            return PH_ARGON2.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PH_ARGON2.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacSHA1("SpringSecurity-PBKDF2WithHmacSHA1") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBKDF2WithHmacSHA1]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBKDF2WithHmacSHA1]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacSHA256("SpringSecurity-PBKDF2WithHmacSHA256") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBKDF2WithHmacSHA256]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBKDF2WithHmacSHA256]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacSHA512("SpringSecurity-PBKDF2WithHmacSHA512") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBKDF2WithHmacSHA512]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBKDF2WithHmacSHA512]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBEWithHmacTIGER("SpringSecurity-PBEWithHmacTIGER") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBEWithHmacTIGER]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBEWithHmacTIGER]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBEWithHmacSHA256("SpringSecurity-PBEWithHmacSHA256") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBEWithHmacSHA256]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBEWithHmacSHA256]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacSHA224("SpringSecurity-PBKDF2WithHmacSHA224") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBKDF2WithHmacSHA224]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBKDF2WithHmacSHA224]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBEWithHmacRIPEMD160("SpringSecurity-PBEWithHmacRIPEMD160") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBEWithHmacRIPEMD160]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBEWithHmacRIPEMD160]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacSHA3_384("SpringSecurity-PBKDF2WithHmacSHA3-384") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[`PBKDF2WithHmacSHA3-384`]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[`PBKDF2WithHmacSHA3-384`]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacGOST3411("SpringSecurity-PBKDF2WithHmacGOST3411") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBKDF2WithHmacGOST3411]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBKDF2WithHmacGOST3411]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacSM3("SpringSecurity-PBKDF2WithHmacSM3") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBKDF2WithHmacSM3]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBKDF2WithHmacSM3]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacSHA384("SpringSecurity-PBKDF2WithHmacSHA384") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBKDF2WithHmacSHA384]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBKDF2WithHmacSHA384]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacSHA3_512("SpringSecurity-PBKDF2WithHmacSHA3-512") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[`PBKDF2WithHmacSHA3-512`]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[`PBKDF2WithHmacSHA3-512`]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBEWithHmacSHA1("SpringSecurity-PBEWithHmacSHA1") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBEWithHmacSHA1]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBEWithHmacSHA1]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBEWithHmacGOST3411("SpringSecurity-PBEWithHmacGOST3411") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[PBEWithHmacGOST3411]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[PBEWithHmacGOST3411]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacSHA3_256("SpringSecurity-PBKDF2WithHmacSHA3-256") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[`PBKDF2WithHmacSHA3-256`]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[`PBKDF2WithHmacSHA3-256`]!!.matches(pass, hash)
        }
    },
    SpringSecurityPBKDF2WithHmacSHA3_224("SpringSecurity-PBKDF2WithHmacSHA3-224") {
        override fun hash(data: ByteArray): String {
            return PBE_ENCODERS[`PBKDF2WithHmacSHA3-224`]!!.encode(data.decodeToString())
        }

        override fun check(pass: String, hash: String): Boolean {
            return PBE_ENCODERS[`PBKDF2WithHmacSHA3-224`]!!.matches(pass, hash)
        }
    };

    abstract fun hash(data: ByteArray): String

    open fun check(pass: String, hash: String): Boolean {
        return hash(pass.toByteArray()) == hash
    }
}

val PH_MD4 = MessageDigestPasswordEncoder("MD4")
val PH_MD5 = MessageDigestPasswordEncoder("MD5")
val PH_SHA1 = MessageDigestPasswordEncoder("SHA-1")
val PH_SHA256 = MessageDigestPasswordEncoder("SHA-256")
val PH_LDAP = LdapShaPasswordEncoder()
val PH_SCRYPT = SCryptPasswordEncoder()
val PH_BCRYPT = BCryptPasswordEncoder()
val PH_ARGON2 = Argon2PasswordEncoder()
