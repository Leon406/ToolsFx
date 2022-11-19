package me.leon.ctf

import java.io.*

/**
 * The [TrollScriptEngine] is an implementation for the brainfuck derivative TrollScript.
 *
 * @author Fabian M.
 */
open class TrollScriptEngine
@JvmOverloads
constructor(
    cells: Int = 64,
    out: OutputStream? = ByteArrayOutputStream(),
    inputStream: InputStream? = System.`in`
) : BrainfuckEngine(cells, out!!, inputStream) {
    /** The default length of a token. */
    open var defaultTokenLength = 3

    object Token : BrainFuckToken {
        override val start = "tro"
        override val next = "ooo"
        override val pre = "ool"
        override val plus = "olo"
        override val minus = "oll"
        override val output = "loo"
        override val input = "lol"
        override val bracketLeft = "llo"
        override val bracketRight = "lll"
        override val end = "ll."
    }

    /**
     * Interprets the given string.
     *
     * @param str The string to interpret.
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun interpret(str: String): String {
        (outWriter as ByteArrayOutputStream).reset()
        var started = false
        val tokens: MutableList<String> = ArrayList()
        // It fine that all TrollScript tokens are 3 characters long :)
        // So we aren't going to loop through all characters.
        while (charPointer < str.length) {
            val token =
                if (charPointer + defaultTokenLength <= str.length) {
                    str.substring(charPointer, charPointer + defaultTokenLength)
                } else {
                    str.substring(charPointer, charPointer + (str.length - charPointer))
                }
            if (isValidToken(token, Token)) {
                if (token.equals(Token.start, ignoreCase = true)) {
                    started = true
                } else if (token.equals(Token.end, ignoreCase = true)) {
                    break
                } else if (started) {
                    tokens.add(token)
                }
                charPointer += defaultTokenLength
            } else if (charPointer + defaultTokenLength > str.length) {
                charPointer += str.length - charPointer
            } else {
                charPointer++
            }
        }
        parseTokens(tokens)
        // Clear all data.
        initiate(data!!.size)
        return outWriter.toString()
    }

    private fun parseTokens(tokens: MutableList<String>) {
        var tokenPointer = 0
        while (tokenPointer < tokens.size) {
            when (tokens[tokenPointer].lowercase()) {
                Token.next ->
                    dataPointer = if (dataPointer == data!!.size - 1) 0 else dataPointer + 1
                Token.pre ->
                    dataPointer = if (dataPointer == 0) data!!.size - 1 else dataPointer - 1
                Token.plus -> data!![dataPointer]++
                Token.minus -> data!![dataPointer]--
                Token.output -> outWriter.write(data!![dataPointer].toInt())
                Token.input -> data!![dataPointer] = consoleReader.read().toByte()
                Token.bracketLeft ->
                    if (data!![dataPointer].toInt() == 0) {
                        var level = 1
                        while (level > 0) {
                            tokenPointer++
                            if (tokens[tokenPointer].equals(Token.bracketLeft, ignoreCase = true)) {
                                level++
                            } else if (
                                tokens[tokenPointer].equals(Token.bracketRight, ignoreCase = true)
                            ) {
                                level--
                            }
                        }
                    }
                Token.bracketRight -> {
                    if (data!![dataPointer].toInt() != 0) {
                        var level = 1
                        while (level > 0) {
                            tokenPointer--
                            if (tokens[tokenPointer].equals(Token.bracketLeft, ignoreCase = true)) {
                                level--
                            } else if (
                                tokens[tokenPointer].equals(Token.bracketRight, ignoreCase = true)
                            ) {
                                level++
                            }
                        }
                    }
                }
            }
            tokenPointer++
        }
    }
}
