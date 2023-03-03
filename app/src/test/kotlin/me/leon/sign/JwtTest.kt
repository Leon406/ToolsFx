package me.leon.sign

import kotlin.test.assertTrue
import me.leon.ext.*
import org.junit.Test

class JwtTest {
    private val key = "jwtKeyjwtKeyjwtKeyjwtKeyjwtKey12"
    private val jwtToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0Ijox" +
            "NTE2MjM5MDIyfQ.wHnmcwzfkUwtLk95jRoVAhn5NYoxZZKe3WeBGOsDeC8"

    @Test
    fun jwtToken() {
        val data = """{"sub":"1234567890","name":"John Doe","iat":1516239022}"""
        val token = data.jwt(JWT_HMAC_ALGS.keys.last(), key)
        println(token)
        assertTrue(token.jwtVerify(key))
    }

    @Test
    fun parseToken() {
        println(jwtToken.jwtVerify(key))
    }
}
