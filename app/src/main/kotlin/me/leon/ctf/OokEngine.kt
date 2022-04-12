package me.leon.ctf

import java.io.*

/**
 * The [OokEngine] is an implementation for the brainfuck derivative Ook!.
 *
 * @author Fabian M.
 */
class OokEngine
@JvmOverloads
constructor(
    cells: Int = 64,
    out: OutputStream? = ByteArrayOutputStream(),
    inputStream: InputStream? = System.`in`
) : TrollScriptEngine(cells, out, inputStream) {
    /** The default length of a token. */
    override var defaultTokenLength = 9

    enum class Token(var value: String) {
        NEXT("Ook. Ook?"),
        PREVIOUS("Ook? Ook."),
        PLUS("Ook. Ook."),
        MINUS("Ook! Ook!"),
        OUTPUT("Ook! Ook."),
        INPUT("Ook. Ook!"),
        BRACKET_LEFT("Ook! Ook?"),
        BRACKET_RIGHT("Ook? Ook!")
    }

    /** Interprets the given string. */
    @Throws(Exception::class)
    override fun interpret(str: String): String {
        (outWriter as ByteArrayOutputStream).reset()

        // List with tokens.defaultTokenLenght
        val tokens: MutableList<Token> = ArrayList()
        // It fine that all Ook! tokens are 9 characters long :)
        // So we aren't going to loop through all characters..
        while (charPointer < str.length) {
            if (str[charPointer].toString().isBlank()) {
                charPointer++
                continue
            }
            val token =
                if (charPointer + defaultTokenLength <= str.length) // The string we found.
                 str.substring(charPointer, charPointer + defaultTokenLength)
                else str.substring(charPointer, charPointer + (str.length - charPointer))
            var b = false
            for (tokenCheck in Token.values()) {
                if (tokenCheck.value == token) {
                    tokens.add(tokenCheck)
                    charPointer += defaultTokenLength
                    b = true
                    break
                }
            }
            // If the token was invalid, b is false.
            if (!b)
                if (charPointer + defaultTokenLength > str.length)
                    charPointer += str.length - charPointer
                else charPointer++
        }

        loopAllTokens(tokens)
        // Clear all data.
        initiate(data!!.size)
        return outWriter.toString()
    }

    private fun loopAllTokens(tokens: MutableList<Token>) {
        // Loop through all tokens.
        var tokenPointer = 0
        while (tokenPointer < tokens.size) {
            when (tokens[tokenPointer]) {
                Token.NEXT ->
                    dataPointer = (if (dataPointer == data!!.size - 1) 0 else dataPointer + 1)
                Token.PREVIOUS ->
                    dataPointer = (if (dataPointer == 0) data!!.size - 1 else dataPointer - 1)
                Token.PLUS -> data!![dataPointer]++
                Token.MINUS -> data!![dataPointer]--
                Token.OUTPUT -> outWriter.write(data!![dataPointer].toInt())
                Token.INPUT -> data!![dataPointer] = consoleReader.read().toByte()
                Token.BRACKET_LEFT ->
                    if (data!![dataPointer].toInt() == 0) {
                        var level = 1
                        while (level > 0) {
                            tokenPointer++
                            if (tokens[tokenPointer] == Token.BRACKET_LEFT) level++
                            else if (tokens[tokenPointer] == Token.BRACKET_RIGHT) level--
                        }
                    }
                Token.BRACKET_RIGHT ->
                    if (data!![dataPointer].toInt() != 0) {
                        var level = 1
                        while (level > 0) {
                            tokenPointer--
                            if (tokens[tokenPointer] == Token.BRACKET_LEFT) level--
                            else if (tokens[tokenPointer] == Token.BRACKET_RIGHT) level++
                        }
                    }
            }
            tokenPointer++
        }
    }
}
