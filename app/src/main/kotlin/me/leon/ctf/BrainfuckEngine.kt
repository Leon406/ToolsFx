package me.leon.ctf

import java.io.*
import kotlin.math.absoluteValue
import kotlin.math.sqrt

/**
 * The [BrainfuckEngine] class is an implementation of the original brainfuck language.
 *
 * @author Fabian M.
 */
open class BrainfuckEngine
@JvmOverloads
constructor(
    cells: Int = 64,
    out: OutputStream = ByteArrayOutputStream(),
    inputStream: InputStream? = System.`in`
) {

    /** The memory that's available for this brainfuck program. */
    @JvmField protected var data: ByteArray? = null

    /**
     * The data pointer that points to the current index in the [BrainfuckEngine.data] memory array.
     */
    @JvmField protected var dataPointer = 0

    /**
     * The character pointer that points to the current index of the character array of value of its
     * file or string.
     */
    @JvmField protected var charPointer = 0

    /**
     * The [BrainfuckEngine.consoleReader] allows us to read from the console for the ',' keyword.
     */
    @JvmField protected var consoleReader: InputStreamReader

    /** The [BrainfuckEngine.outWriter] allows us to write to the console. */
    @JvmField protected var outWriter: OutputStream

    init {
        initiate(cells)
        outWriter = out
        consoleReader = InputStreamReader(inputStream)
    }

    @Suppress("VarCouldBeVal") private var lineCount = 0
    private var columnCount = 0

    /**
     * The [Token] class contains tokens in brainfuck.
     *
     * @author Fabian M.
     */
    object Token {
        const val NEXT = '>'
        const val PREVIOUS = '<'
        const val PLUS = '+'
        const val MINUS = '-'
        const val OUTPUT = '.'
        const val INPUT = ','
        const val BRACKET_LEFT = '['
        const val BRACKET_RIGHT = ']'
    }

    /** Initiate this instance. */
    protected fun initiate(cells: Int) {
        data = ByteArray(cells)
        dataPointer = 0
        charPointer = 0
    }

    /** Interprets the given string. */
    @Throws(Exception::class)
    open fun interpret(str: String): String {
        (outWriter as ByteArrayOutputStream).reset()
        while (charPointer < str.length) {
            interpret(str[charPointer], str.toCharArray())
            charPointer++
        }
        initiate(data!!.size)
        return outWriter.toString()
    }

    /** Interprets the given char */
    @Throws(IndexOutOfBoundsException::class)
    protected fun interpret(c: Char, chars: CharArray) {
        data?.let {
            when (c) {
                Token.NEXT -> {
                    if (dataPointer + 1 > it.size) {
                        throw IndexOutOfBoundsException(
                            "Error on line $lineCount, column $columnCount:data pointer ($dataPointer ) " +
                                "on position $charPointer  out of range."
                        )
                    }
                    dataPointer++
                }
                Token.PREVIOUS -> {
                    // Decrement the data pointer (to point to the next cell to the left).
                    if (dataPointer - 1 < 0) {
                        throw IndexOutOfBoundsException(
                            "Error on line $lineCount, column $columnCount :data pointer ($dataPointer ) " +
                                "on position $charPointer   negative."
                        )
                    }
                    dataPointer--
                }
                Token.PLUS -> it[dataPointer]++
                Token.MINUS -> it[dataPointer]--
                Token.OUTPUT -> outWriter.write(it[dataPointer].toInt())
                Token.INPUT -> it[dataPointer] = consoleReader.read().toByte()
                Token.BRACKET_LEFT ->
                    if (it[dataPointer].toInt() == 0) {
                        var i = 1
                        while (i > 0) {
                            val c2 = chars[++charPointer]
                            if (c2 == Token.BRACKET_LEFT) {
                                i++
                            } else if (c2 == Token.BRACKET_RIGHT) {
                                i--
                            }
                        }
                    }
                Token.BRACKET_RIGHT -> {
                    var i = 1
                    while (i > 0) {
                        val c2 = chars[--charPointer]
                        if (c2 == Token.BRACKET_LEFT) i-- else if (c2 == Token.BRACKET_RIGHT) i++
                    }
                    charPointer--
                }
            }
            columnCount++
        }
    }
}

fun Int.pointerLoopCalculate(): IntArray {
    val int = sqrt(toDouble()).toInt()
    val t = (this / 32 + 1)
    val loops = intArrayOf(0, 0, 0)
    if (this < 10) {
        loops[0] = this
        //                println("<10 $it = $it ")
    } else if (((int - 1) * (int + 3) - this).absoluteValue <= t) {
        loops[0] = int - 1
        loops[1] = int + 3
        loops[2] = this - loops[0] * loops[1]
    } else if (((int + 1) * (int - 1) - this).absoluteValue <= t) {
        loops[0] = int - 1
        loops[1] = int + 1
    } else if (((int + 1) * (int - 2) - this).absoluteValue <= t) {
        loops[0] = int - 2
        loops[1] = int + 1
    } else if (((int - 1) * (int + 2) - this).absoluteValue <= t) {
        loops[0] = int - 1
        loops[1] = int + 2
    } else if ((int * (int + 2) - this).absoluteValue <= t) {
        loops[0] = int
        loops[1] = int + 2
    } else if (((int + 1) * (int + 1) - this).absoluteValue <= t) {
        loops[0] = int + 1
        loops[1] = int + 1
    } else if ((int * (int + 1) - this).absoluteValue <= t) {
        loops[0] = int
        loops[1] = int + 1
    } else // if ((int * int - it).absoluteValue <= t)
    {
        loops[0] = int
        loops[1] = int
    }
    if (loops[1] != 0) {
        loops[2] = this - loops[0] * loops[1]
    }
    return loops
}

fun IntArray.translate(): String {
    require(size == 3) { "size must be 3" }
    return if (this[1] == 0) {
        BrainfuckEngine.Token.PLUS.toString().repeat(this[0])
    } else {
        StringBuilder()
            .append(BrainfuckEngine.Token.PLUS.toString().repeat(this[0]))
            .append(BrainfuckEngine.Token.BRACKET_LEFT)
            .append(BrainfuckEngine.Token.NEXT)
            .append(BrainfuckEngine.Token.PLUS.toString().repeat(this[1]))
            .append(BrainfuckEngine.Token.PREVIOUS)
            .append(BrainfuckEngine.Token.MINUS)
            .append(BrainfuckEngine.Token.BRACKET_RIGHT)
            .append(BrainfuckEngine.Token.NEXT)
            .also {
                if (this[2] < 0) {
                    it.append((BrainfuckEngine.Token.MINUS.toString().repeat(-this[2])))
                } else if (this[2] > 0) {
                    it.append((BrainfuckEngine.Token.PLUS.toString().repeat(this[2])))
                }
            }
            .append(BrainfuckEngine.Token.OUTPUT)
            .toString()
    }
}

fun String.encode(next: String = BrainfuckEngine.Token.NEXT.toString()) =
    map { it.code.pointerLoopCalculate().translate() }.joinToString(next)

fun String.brainFuckShortEncode(next: String = BrainfuckEngine.Token.NEXT.toString()) =
    mapIndexed { index, c ->
            when (index) {
                0 -> c.code.pointerLoopCalculate().translate()
                else ->
                    with(c.code - this[index - 1].code) {
                        if (this.absoluteValue < 10) {
                            this.repeatMinusPlusAndOutput()
                        } else {
                            next + c.code.pointerLoopCalculate().translate()
                        }
                    }
            }
        }
        .joinToString("")

fun Int.repeatMinusPlusAndOutput(
    minus: String = BrainfuckEngine.Token.MINUS.toString(),
    plus: String = BrainfuckEngine.Token.PLUS.toString(),
    output: String = BrainfuckEngine.Token.OUTPUT.toString()
) =
    if (this > 0) {
        plus.repeat(this)
    } else {
        minus.repeat(-this)
    } + output
