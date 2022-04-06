package me.leon.ext.crypto

import me.leon.hash

// 可见字符
enum class PasswordHashingType {
    `md5(md5($pass)` {
        override fun hash(data: ByteArray): String {
            var hash = data.decodeToString()
            repeat(2) { hash = hash.lowercase().hash() }
            return hash
        }
    },
    `MD5(MD5($pass))` {
        override fun hash(data: ByteArray): String {
            var hash = data.decodeToString()
            repeat(2) { hash = hash.uppercase().hash() }
            return hash
        }
    },
    `md5(md5(md5($pass))` {
        override fun hash(data: ByteArray): String {
            var hash = data.decodeToString()
            repeat(3) { hash = hash.lowercase().hash() }
            return hash
        }
    },
    `MD5(MD5(MD5($pass)))` {
        override fun hash(data: ByteArray): String {
            var hash = data.decodeToString()
            repeat(3) { hash = hash.uppercase().hash() }
            return hash
        }
    },
    `md5(SHA1)` {
        override fun hash(data: ByteArray): String {
            return data.decodeToString().hash("SHA1").uppercase().hash()
        }
    },
    `md5(SHA256)` {
        override fun hash(data: ByteArray): String {
            return data.decodeToString().hash("SHA256").uppercase().hash()
        }
    },
    `md5(SHA384)` {
        override fun hash(data: ByteArray): String {
            return data.decodeToString().hash("SHA384").uppercase().hash()
        }
    },
    `md5(SHA512)` {
        override fun hash(data: ByteArray): String {
            return data.decodeToString().hash("SHA512").uppercase().hash()
        }
    };

    abstract fun hash(data: ByteArray): String
}
