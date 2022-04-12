package me.leon.ctf

import java.io.*

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

    /** The current line the engine is at. */
    private var lineCount = 0

    /** The current column the engine is at. */
    private var columnCount = 0

    /**
     * The [Token] class contains tokens in brainfuck.
     *
     * @author Fabian M.
     */
    protected object Token {
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
                            if (c2 == Token.BRACKET_LEFT) i++
                            else if (c2 == Token.BRACKET_RIGHT) i--
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
