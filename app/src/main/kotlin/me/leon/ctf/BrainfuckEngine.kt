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
    object Token : BrainFuckToken {
        override val next = ">"
        override val pre = "<"
        override val plus = "+"
        override val minus = "-"
        override val output = "."
        override val input = ","
        override val bracketLeft = "["
        override val bracketRight = "]"
        override val start = ""
        override val end = ""
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
            when (c.toString()) {
                Token.next -> {
                    if (dataPointer + 1 > it.size) {
                        throw IndexOutOfBoundsException(
                            "Error on line $lineCount, column $columnCount:data pointer ($dataPointer ) " +
                                "on position $charPointer  out of range."
                        )
                    }
                    dataPointer++
                }
                Token.pre -> {
                    // Decrement the data pointer (to point to the next cell to the left).
                    if (dataPointer - 1 < 0) {
                        throw IndexOutOfBoundsException(
                            "Error on line $lineCount, column $columnCount :data pointer ($dataPointer ) " +
                                "on position $charPointer   negative."
                        )
                    }
                    dataPointer--
                }
                Token.plus -> it[dataPointer]++
                Token.minus -> it[dataPointer]--
                Token.output -> outWriter.write(it[dataPointer].toInt())
                Token.input -> it[dataPointer] = consoleReader.read().toByte()
                Token.bracketLeft ->
                    if (it[dataPointer].toInt() == 0) {
                        var i = 1
                        while (i > 0) {
                            val c2 = chars[++charPointer]
                            if (c2.toString() == Token.bracketLeft) {
                                i++
                            } else if (c2.toString() == Token.bracketRight) {
                                i--
                            }
                        }
                    }
                Token.bracketRight -> {
                    var i = 1
                    while (i > 0) {
                        val c2 = chars[--charPointer]
                        if (c2.toString() == Token.bracketLeft) {
                            i--
                        } else if (c2.toString() == Token.bracketRight) {
                            i++
                        }
                    }
                    charPointer--
                }
            }
            columnCount++
        }
    }

    fun isValidToken(token: String, tokens: BrainFuckToken = Token): Boolean {
        return (token.equals(tokens.start, ignoreCase = true) ||
            token.equals(tokens.next, ignoreCase = true) ||
            token.equals(tokens.pre, ignoreCase = true) ||
            token.equals(tokens.plus, ignoreCase = true) ||
            token.equals(tokens.minus, ignoreCase = true) ||
            token.equals(tokens.output, ignoreCase = true) ||
            token.equals(tokens.input, ignoreCase = true) ||
            token.equals(tokens.bracketLeft, ignoreCase = true) ||
            token.equals(tokens.bracketRight, ignoreCase = true) ||
            token.equals(tokens.end, ignoreCase = true))
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

fun IntArray.translate(token: BrainFuckToken = BrainfuckEngine.Token): String {
    require(size == 3) { "size must be 3" }
    return if (this[1] == 0) {
        token.plus.repeat(this[0])
    } else {
        StringBuilder()
            .append(token.plus.repeat(this[0]))
            .append(token.bracketLeft)
            .append(token.next)
            .append(token.plus.repeat(this[1]))
            .append(token.pre)
            .append(token.minus)
            .append(token.bracketRight)
            .append(token.next)
            .also {
                if (this[2] < 0) {
                    it.append((token.minus.repeat(-this[2])))
                } else if (this[2] > 0) {
                    it.append((token.plus.repeat(this[2])))
                }
            }
            .append(token.output)
            .toString()
    }
}

fun String.encode(token: BrainFuckToken = BrainfuckEngine.Token) =
    map { it.code.pointerLoopCalculate().translate() }.joinToString(token.next)

fun String.brainFuckShortEncode(token: BrainFuckToken = BrainfuckEngine.Token) =
    mapIndexed { index, c ->
            when (index) {
                0 -> c.code.pointerLoopCalculate().translate(token)
                else ->
                    with(c.code - this[index - 1].code) {
                        if (this.absoluteValue < 10) {
                            this.repeatMinusPlusAndOutput(token)
                        } else {
                            token.next + c.code.pointerLoopCalculate().translate(token)
                        }
                    }
            }
        }
        .joinToString("")
        .run {
            if (token.start.isEmpty()) {
                this
            } else {
                "${token.start}$this${token.end}"
            }
        }

fun Int.repeatMinusPlusAndOutput(token: BrainFuckToken = BrainfuckEngine.Token) =
    if (this > 0) {
        token.plus.repeat(this)
    } else {
        token.minus.repeat(-this)
    } + token.output
