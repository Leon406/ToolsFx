package me.leon

/**
 * @author Leon
 * @since 2023-04-27 9:28
 * @email deadogone@gmail.com
 */
// For JVM backends
@JvmInline value class Password(private val s: String)

// No actual instantiation of class 'Password' happens
// At runtime 'securePassword' contains just 'String'
val securePassword = Password("Don't try this in production")

fun main() {
    val s: String? = null
    println(s.toString() == null)
    println(s.toString() == "null")
    println(s == null)
}
