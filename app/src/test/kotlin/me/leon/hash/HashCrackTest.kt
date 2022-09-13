package me.leon.hash

import kotlin.system.measureTimeMillis
import me.leon.encode.base.BASE58_DICT
import me.leon.encode.base.BASE92_DICT
import me.leon.hash
import org.junit.Test

/**
 *
 * @author Leon
 * @since 2022-08-26 9:33
 * @email: deadogone@gmail.com
 */
class HashCrackTest {
    val hash = "c22a563acc2a587afbfaaaa6d67bc6e628872b00bd7e998873881f7c6fdc62fc"

    @Test
    fun hash() {

        for (p in 8_617_090_000_000L..8_617_099_999_999L) {
            if (p.toString().hash("SHA-256") == hash) {
                println(p)
                break
            }
        }
    }

    @Test
    fun dict() {
        val dict = "0123456789"
        val mask = "86170????????"
        println(BASE58_DICT.sliceCount(Runtime.getRuntime().availableProcessors()))
        measureTimeMillis {
                mask.maskCrack(dict) { it.hash("SHA-256") == hash }.also { println(it) }
            }
            .also { println(it) }

        measureTimeMillis {
                mask.maskCrackParallel(dict) { it.hash("SHA-256") == hash }.also { println(it) }
            }
            .also { println(it) }
    }

    @Test
    fun passMatch() {
        val dict = BASE92_DICT
        val pass = dict.random(5)

        val mask = "?????"
        measureTimeMillis { mask.maskCrackParallel(dict) { it == pass }.also { println(it) } }
            .also { println(it) }

        measureTimeMillis { mask.maskCrack(dict) { it == pass }.also { println(it) } }
            .also { println(it) }
    }

    @Test
    fun randomPassword() {
        repeat(10) { println(BASE92_DICT.random(5)) }
    }

    fun String.maskCrack(dict: String, condition: (String) -> Boolean): String? {
        val sb = StringBuilder()
        var sq: Sequence<String>? = null
        for (c in this) {
            if (c == '?') {
                sq =
                    sq?.run {
                        if (sb.isEmpty()) {
                            sq!!.next(dict)
                        } else {
                            sq!!.nextFix(sb.toString()).next(dict)
                        }
                    }
                        ?: sb.toString().next(dict)
                sb.clear()
            } else {
                sb.append(c)
            }
        }

        if (sb.isNotEmpty()) {
            sq?.nextFix(sb.toString())
        }

        return sq?.find { condition.invoke(it) }
    }

    fun String.sliceCount(count: Int) =
        with(length / count) {
            if (this == 0) {
                toList().map { it.toString() }
            } else {
                chunked(this)
            }
        }

    fun String.random(count: Int): String {
        val sb = StringBuilder()
        repeat(count) { sb.append(random()) }
        return sb.toString()
    }

    fun String.maskCrackParallel(dict: String, condition: (String) -> Boolean): String? {

        val sb = StringBuilder()
        val d = dict.sliceCount(Runtime.getRuntime().availableProcessors())
        var sqs = mutableListOf<Sequence<String>>()
        for (c in this) {
            if (c == '?') {
                if (sqs.isEmpty()) {
                    sqs.addAll(d.map { sb.toString().next(it) })
                } else {
                    sqs =
                        sqs.map {
                                it.run {
                                    if (sb.isEmpty()) {
                                        it.next(dict)
                                    } else {
                                        it.nextFix(sb.toString()).next(dict)
                                    }
                                }
                            }
                            .toMutableList()
                }
                sb.clear()
            } else {
                sb.append(c)
            }
        }

        if (sb.isNotEmpty()) {
            sqs.map { it.nextFix(sb.toString()) }
        }
        return sqs.parallelStream()
            .map { it.find { condition.invoke(it) } }
            .filter { it != null }
            .findFirst()
            .orElseGet { null }
    }

    fun String.next(dict: String, count: Int = 1): Sequence<String> =
        with(dict.asSequence().map { this + it }) { if (count > 1) next(dict, count - 1) else this }

    fun Sequence<String>.next(dict: String) = this.flatMap { it.next(dict) }
    fun Sequence<String>.next(dict: String, count: Int) =
        (1..count).fold(this) { acc, _ -> acc.next(dict) }

    fun Sequence<String>.nextFix(fix: String) = fix.fold(this) { acc, c -> acc.next(c.toString()) }
}
