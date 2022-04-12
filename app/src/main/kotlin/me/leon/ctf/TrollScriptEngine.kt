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

    protected object Token {
        const val START = "tro"
        const val NEXT = "ooo"
        const val PREVIOUS = "ool"
        const val PLUS = "olo"
        const val MINUS = "oll"
        const val OUTPUT = "loo"
        const val INPUT = "lol"
        const val BRACKET_LEFT = "llo"
        const val BRACKET_RIGHT = "lll"
        const val END = "ll."
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
                if (charPointer + defaultTokenLength <= str.length)
                    str.substring(charPointer, charPointer + defaultTokenLength)
                else str.substring(charPointer, charPointer + (str.length - charPointer))
            if (isValidToken(token)) {
                if (token.equals(Token.START, ignoreCase = true)) started = true
                else if (token.equals(Token.END, ignoreCase = true)) break
                else if (started) tokens.add(token)
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
                Token.NEXT ->
                    dataPointer = if (dataPointer == data!!.size - 1) 0 else dataPointer + 1
                Token.PREVIOUS ->
                    dataPointer = if (dataPointer == 0) data!!.size - 1 else dataPointer - 1
                Token.PLUS -> data!![dataPointer]++
                Token.MINUS -> data!![dataPointer]--
                Token.OUTPUT -> outWriter.write(data!![dataPointer].toInt())
                Token.INPUT -> data!![dataPointer] = consoleReader.read().toByte()
                Token.BRACKET_LEFT ->
                    if (data!![dataPointer].toInt() == 0) {
                        var level = 1
                        while (level > 0) {
                            tokenPointer++
                            if (tokens[tokenPointer].equals(Token.BRACKET_LEFT, ignoreCase = true))
                                level++
                            else if (tokens[tokenPointer].equals(
                                    Token.BRACKET_RIGHT,
                                    ignoreCase = true
                                )
                            )
                                level--
                        }
                    }
                Token.BRACKET_RIGHT -> {
                    if (data!![dataPointer].toInt() != 0) {
                        var level = 1
                        while (level > 0) {
                            tokenPointer--
                            if (tokens[tokenPointer].equals(Token.BRACKET_LEFT, ignoreCase = true))
                                level--
                            else if (tokens[tokenPointer].equals(
                                    Token.BRACKET_RIGHT,
                                    ignoreCase = true
                                )
                            )
                                level++
                        }
                    }
                }
            }
            tokenPointer++
        }
    }

    /**
     * Is the given token a valid TrollScript token.
     *
     * @param token The token to check.
     * @return `true` if the given token is a valid `TrollScript` token, `false` otherwise.
     */
    private fun isValidToken(token: String): Boolean {
        return (token.equals(Token.START, ignoreCase = true) ||
            token.equals(Token.NEXT, ignoreCase = true) ||
            token.equals(Token.PREVIOUS, ignoreCase = true) ||
            token.equals(Token.PLUS, ignoreCase = true) ||
            token.equals(Token.MINUS, ignoreCase = true) ||
            token.equals(Token.OUTPUT, ignoreCase = true) ||
            token.equals(Token.INPUT, ignoreCase = true) ||
            token.equals(Token.BRACKET_LEFT, ignoreCase = true) ||
            token.equals(Token.BRACKET_RIGHT, ignoreCase = true) ||
            token.equals(Token.END, ignoreCase = true))
    }
}
